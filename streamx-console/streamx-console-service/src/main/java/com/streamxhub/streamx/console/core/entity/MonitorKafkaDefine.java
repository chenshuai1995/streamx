/*
 * Copyright (c) 2019 The StreamX Project
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamxhub.streamx.console.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Jim Chen
 */
@Data
@TableName("t_monitor_kafka_define")
@Slf4j
public class MonitorKafkaDefine implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务名称
     */
    private String appName;

    /**
     * broker_server
     */
    private String brokerServer;

    /**
     * topic
     */
    private String topic;

    /**
     * group
     */
    @TableField(value = "`group`")
    private String group;

    /**
     * 最大超时秒数
     */
    private Long delaySecond;

    /**
     * 预警级别：1：info，2：warn，3：error"
     * info: 企业微信
     * warn: 企业微信 + 短信
     * error: 企业微信 + 短信 + 电话
     */
    private Integer alarmLevel;

    /**
     * 是否上线：'0：下线，1上线'
     */
    @TableField(value = "`release`")
    private Integer release;

    /**
     * 是否删除：'0否，1是'
     */
    private Integer deleted;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 修改人id
     */
    private Long updateBy;

    /**
     * 维护人id
     */
    private Long maintainBy;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 修改人名称
     */
    private String updateName;

    /**
     * 维护人名称
     */
    private String maintainName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
