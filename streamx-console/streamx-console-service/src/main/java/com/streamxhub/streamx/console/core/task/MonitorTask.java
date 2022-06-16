package com.streamxhub.streamx.console.core.task;

import static com.streamxhub.streamx.console.core.task.K8sFlinkTrkMonitorWrapper.Bridge.toTrkId;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.streamxhub.streamx.common.enums.ExecutionMode;
import com.streamxhub.streamx.common.util.YarnUtils;
import com.streamxhub.streamx.console.core.entity.Application;
import com.streamxhub.streamx.console.core.entity.MonitorDefine;
import com.streamxhub.streamx.console.core.entity.MonitorInstance;
import com.streamxhub.streamx.console.core.entity.MonitorKafkaDefine;
import com.streamxhub.streamx.console.core.entity.MonitorKafkaInstance;
import com.streamxhub.streamx.console.core.service.ApplicationService;
import com.streamxhub.streamx.console.core.service.MonitorDefineService;
import com.streamxhub.streamx.console.core.service.MonitorInstanceService;
import com.streamxhub.streamx.console.core.service.MonitorKafkaDefineService;
import com.streamxhub.streamx.console.core.service.MonitorKafkaInstanceService;
import com.streamxhub.streamx.console.core.service.SettingService;
import com.streamxhub.streamx.console.core.utils.CommandUtil;
import com.streamxhub.streamx.console.core.utils.MessageManager;
import com.streamxhub.streamx.console.core.utils.PlatformMessage;
import com.streamxhub.streamx.console.system.entity.User;
import com.streamxhub.streamx.console.system.service.UserService;
import com.streamxhub.streamx.flink.kubernetes.K8sFlinkTrkMonitor;
import com.streamxhub.streamx.flink.kubernetes.model.TrkId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Jim Chen
 */
@Slf4j
@Component
public class MonitorTask {

    @Autowired
    private K8sFlinkTrkMonitor k8sFlinkTrkMonitor;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private MonitorDefineService monitorDefineService;

    @Autowired
    private MonitorInstanceService monitorInstanceService;

    @Autowired
    private MonitorKafkaDefineService monitorKafkaDefineService;

    @Autowired
    private MonitorKafkaInstanceService monitorKafkaInstanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private SettingService settingService;

    @Scheduled(cron = "*/10 * * * * ?")
    public void jobStatus() {
        List<MonitorDefine> onlines = monitorDefineService.getOnlines();
        if (onlines != null && onlines.size() > 0) {
            for (MonitorDefine define : onlines) {
                String appName = define.getAppName();
                Integer executionMode = define.getExecutionMode();

                User user = userService.findByName(define.getMaintainName());

                List list = doMonitorJobStatus(appName, executionMode);
                Integer appNum = (Integer) list.get(0);
                String msg = (String) list.get(1);

                processJobStatus(appNum, msg, define, user.getMobile());
            }
        }
    }

    //    @Scheduled(cron = "*/10 * * * * ?")
    public void kafkaLag() {
        List<MonitorKafkaDefine> onlines = monitorKafkaDefineService.getOnlines();
        if (onlines != null && onlines.size() > 0) {
            for (MonitorKafkaDefine define : onlines) {
                String groupId = define.getGroup();
                String topic = define.getTopic();
                Long delaySecond = define.getDelaySecond();
                String brokerServer = define.getBrokerServer();

                String resultMsg = doMonitorKafkaLag(brokerServer, topic, groupId, delaySecond);
                if (StringUtils.isNotBlank(resultMsg)) {
                    processKafkaLag(resultMsg, define);
                } else {
                    log.info(String.format("消费者组： %s 消费topic: %s 正常", groupId, topic));
                }
            }
        }


    }

    /**
     * 处理监控job后的业务逻辑
     * @param appNum
     * @param msg
     * @param monitorDefine
     * @param mobile
     */
    private void processJobStatus(Integer appNum, String msg, MonitorDefine monitorDefine,
        String mobile) {
        if (appNum == 0) {
            // 任务不存在
            log.error(msg);

            // 根据预警级别发送预警
            boolean alarmResult = alarmByLevel(msg, monitorDefine.getAlarmLevel(), mobile);
            if (!alarmResult) {
                log.error("预警失败");
            } else {
                MonitorInstance instance = new MonitorInstance();
                instance.setMonitorId(monitorDefine.getId());
                instance.setTriggerTime(new Date());
                instance.setReason("任务失败");
                instance.setSlovedState(0);
                instance.setCreateTime(new Date());
                instance.setUpdateTime(new Date());
                monitorInstanceService.save(instance);
            }

            // 自动拉起
            Integer autoRecover = monitorDefine.getAutoRecover();
            if (1 == autoRecover) {
                String recoverStartUser = monitorDefine.getRecoverStartUser();
                String recoverStartNode = monitorDefine.getRecoverStartNode();
                String recoverStartCommand = monitorDefine.getRecoverStartCommand();
                String command = String
                    .format("ssh %s@%s \"%s\"", recoverStartUser, recoverStartNode,
                        recoverStartCommand);
                try {
                    int execute = CommandUtil.execute(command);
                    if (execute != 0) {
                        String cmdMsg = String.format("%s 执行脚本失败： %s", monitorDefine.getAppName(), command);
                        log.error(cmdMsg);
                        alarmByLevel(cmdMsg, 3, mobile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage(), e);
                }
            }
        } else if (appNum > 1) {
            // 任务重复
            Integer expectInstance = monitorDefine.getExpectInstance();
            if (appNum != expectInstance) {
                // 根据预警级别发送预警
                boolean alarmResult = alarmByLevel(msg, monitorDefine.getAlarmLevel(), mobile);
                if (!alarmResult) {
                    log.error("预警失败");
                } else {
                    MonitorInstance instance = new MonitorInstance();
                    instance.setMonitorId(monitorDefine.getId());
                    instance.setTriggerTime(new Date());
                    instance.setReason("任务重复");
                    instance.setSlovedState(0);
                    instance.setCreateTime(new Date());
                    instance.setUpdateTime(new Date());
                    monitorInstanceService.save(instance);
                }
            }
        } else {
            // 任务正常
            log.info(msg);
        }
    }

    /**
     * 监控job status
     */
    private List doMonitorJobStatus(String appName, Integer executionMode) {
        Integer appNum = getAppsInRemoteCluster(appName, executionMode);

        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .format(LocalDateTime.now());
        String msg = String
            .format("%s : in the %s, the job %s status is %s", date, executionMode == 6 ? "K8S"
                    : (executionMode == 4 ? "Yarn Application" : "unknown cluster"),
                appName, appNum == 1 ? "存活" : (appNum == 0 ? "死亡" : "重复"));

        return Arrays.asList(appNum, msg);
    }



    /**
     * 处理kafka lag预警后的业务逻辑
     */
    private void processKafkaLag(String msg, MonitorKafkaDefine monitorKafkaDefine) {
        User user = userService.findByName(monitorKafkaDefine.getMaintainName());

        // 根据预警级别发送预警
        boolean alarmResult = alarmByLevel(msg, monitorKafkaDefine.getAlarmLevel(), user.getMobile());
        if (!alarmResult) {
            log.error("预警失败");
        } else {
            MonitorKafkaInstance instance = new MonitorKafkaInstance();
            instance.setMonitorKafkaId(monitorKafkaDefine.getId());
            instance.setTriggerTime(new Date());
            instance.setReason("消费延迟");
            instance.setSlovedState(0);
            instance.setCreateTime(new Date());
            instance.setUpdateTime(new Date());
            monitorKafkaInstanceService.save(instance);
        }
    }

    /**
     * 监控kafka lag
     */
    private String doMonitorKafkaLag(String brokerServer, String topic, String groupId,
        long delaySecond) {
        String msg = "";

        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "PlatformMonitor" + groupId);

        AdminClient adminClient = AdminClient.create(props);
        ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient
            .listConsumerGroupOffsets(groupId);

        KafkaFuture<java.util.Map<TopicPartition, OffsetAndMetadata>> mapKafkaFuture = listConsumerGroupOffsetsResult
            .partitionsToOffsetAndMetadata();
        try {
            java.util.Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = mapKafkaFuture
                .get();

            Set<TopicPartition> topicPartitions = topicPartitionOffsetAndMetadataMap.keySet();
            Set<TopicPartition> topicPartitionSet = topicPartitions.stream()
                .filter(t -> t.topic().equals(topic)).collect(Collectors.toSet());

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
            consumer.assign(topicPartitionSet);

            for (Entry<TopicPartition, OffsetAndMetadata> entry : topicPartitionOffsetAndMetadataMap
                .entrySet()) {
                if (entry.getKey().topic().equals(topic)) {
                    consumer.seek(entry.getKey(), entry.getValue().offset());
                }
            }

            ConsumerRecords<String, String> records = consumer.poll(100);
            while (records.isEmpty()) {
                Thread.sleep(1000);
                records = consumer.poll(100);
                log.info("poll some record......" + records.count());
            }

            ConsumerRecord consumerRecord = null;
            long delay = -1;
            Boolean isWarn = false;
            for (ConsumerRecord<String, String> record : records) {
                delay = (System.currentTimeMillis() - record.timestamp()) / 1000;
                log.info("延迟 ：" + String.valueOf(delay) + "秒");
                if (delay > delaySecond) {
                    String kafkaTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()),
                            ZoneOffset.of("+8")));
                    msg = String.format(
                        "消费者组: %s 消费topic: %s 延迟了 %s秒，最近写入kafka信息: partition: %s, offset: %s, 写入时间: %s",
                        groupId, record.topic(), delay, record.partition(), record.offset(),
                        kafkaTime);
                    log.error(msg);
                    isWarn = true;
                    consumerRecord = record;
                    break;
                }
            }
            return msg;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 根据预警级别发送预警
     */
    private boolean alarmByLevel(String msg, Integer alarmLevel, String mobile) {
        String alertWebHookUrl = settingService.getAlertWebhookUrl();
        if (1 == alarmLevel) {
            // info（企业微信）
            // 发送企业微信
            return MessageManager.sendTextMessage(msg, mobile, alertWebHookUrl);
        } else if (2 == alarmLevel) {
            // warn（企业微信+短信）
            // 发送企业微信
            boolean robot = MessageManager.sendTextMessage(msg, mobile, alertWebHookUrl);
            // 发送短信息
            boolean shortMessage = PlatformMessage.send2345WarningPlatform(3, msg, mobile);
            return robot && shortMessage;
        } else if (3 == alarmLevel) {
            // error（企业微信+短信+电话）
            // 发送企业微信
            boolean robot = MessageManager.sendTextMessage(msg, mobile, alertWebHookUrl);
            // 发送短信息
            boolean shortMessage = PlatformMessage.send2345WarningPlatform(3, msg, mobile);
            // 拨打电话
            boolean phoneMessage = PlatformMessage.send2345WarningPlatform(9, msg, mobile);
            return robot && shortMessage && phoneMessage;
        } else {
            log.error("未定义预警级别...");
        }
        return false;
    }

    private Integer getAppsInRemoteCluster(String jobName, Integer executionMode) {
        if (ExecutionMode.isYarnMode(executionMode)) {
            List<ApplicationId> applicationIdList = YarnUtils.getAppId(jobName);
            return applicationIdList.size();
        } else if (ExecutionMode.isKubernetesMode(executionMode)) {
            return checkIsInRemoteK8sCluster(jobName, executionMode) ? 1 : 0;
        }
        return 0;
    }

    private boolean checkIsInRemoteK8sCluster(String jobName, Integer executionMode) {
        try {
            QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("job_name", jobName);
            queryWrapper.eq("execution_mode", executionMode);
            Application application = applicationService.getOne(queryWrapper);

            TrkId trkId = toTrkId(application);

            return k8sFlinkTrkMonitor.checkIsInRemoteCluster(trkId);
        } catch (IllegalArgumentException e1) {
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

}
