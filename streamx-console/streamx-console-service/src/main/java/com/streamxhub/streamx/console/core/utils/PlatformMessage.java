package com.streamxhub.streamx.console.core.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Jim Chen
 * @date 2022-06-15
 */
@Slf4j
public class PlatformMessage {

    public static boolean send2345WarningPlatform(Integer warningType, String message, String phoneNumber) {
        log.warn("发送平台预警： warningType=" + warningType + ", phoneNumber=" + phoneNumber + ", message=" + message);
        JSONArray params = new JSONArray();

        JSONObject projectName = new JSONObject();
        projectName.put("key", "projectName");
        projectName.put("value", "实时计算平台");

        JSONObject ruleName = new JSONObject();
        ruleName.put("key", "ruleName");
        ruleName.put("value", "任务监控预警");

        JSONObject notifier = new JSONObject();
        notifier.put("key", "notifier");
        notifier.put("value", "");

        JSONObject pullTime = new JSONObject();
        pullTime.put("key", "pullTime");
        pullTime.put("value", "");

        JSONObject error_message = new JSONObject();
        error_message.put("key", "error_message");
        error_message.put("value", message);

        JSONObject msg = new JSONObject();
        msg.put("msg", "test.....");
        JSONObject receiptUrl = new JSONObject();
        receiptUrl.put("receiptUrl", "");

        params.add(projectName);
        params.add(ruleName);
        params.add(notifier);
        params.add(pullTime);
        params.add(error_message);
        params.add(msg);
        params.add(receiptUrl);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("params", params);
        jsonObject.put("channelId", warningType);
//        jsonObject.put("channelId", 9);// 电话
//        jsonObject.put("channelId", 1);// 邮件
//        jsonObject.put("channelId", 3);// 短信
        jsonObject.put("project", "实时计算平台");
        jsonObject.put("ruleName", "任务监控预警" + message);
        jsonObject.put("receiver", phoneNumber);
//        jsonObject.put("receiver", "chens@2345.com");
        jsonObject.put("templateId", "481658662873985024");
        jsonObject.put("url", "");


        return doPost("http://bumblebee.2345.cn/notification/notification/alert",
            jsonObject.toJSONString());

    }

    private static boolean doPost(String url, String jsonStr) {

        OkHttpClient client = new OkHttpClient.Builder()
            // 设置连接超时时间
            .connectTimeout(10, TimeUnit.SECONDS)
            // 设置读取超时时间
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
        MediaType contentType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(contentType, jsonStr);
        Request request = new Request.Builder().url(url).post(body)
            .addHeader("cache-control", "no-cache").build();
        try {
            Response response = client.newCall(request).execute();
            byte[] datas = response.body().bytes();
            String respMsg = new String(datas);
            JSONObject resultJSON = JSONObject.parseObject(respMsg);
            if (resultJSON.getIntValue("errcode") == 0) {
                log.info("平台消息发送成功!");
                return true;
            }
            log.error("平台消息发送失败, 错误信息如下: {}", resultJSON.getString("errmsg"));
            return false;
        } catch (IOException e) {
            log.error("平台消息发送失败, 异常信息如下: {}", e.getMessage());
            return false;
        }


    }

}
