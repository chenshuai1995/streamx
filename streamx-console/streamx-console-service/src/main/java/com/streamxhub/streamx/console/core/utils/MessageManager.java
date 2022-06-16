package com.streamxhub.streamx.console.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.streamxhub.streamx.console.core.entity.MessageTypeEnum;
import com.streamxhub.streamx.console.core.entity.RobotMessage;
import com.streamxhub.streamx.console.core.entity.TextMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Jim Chen
 * @date 2022-06-13
 */
@Slf4j
public class MessageManager {

    public static boolean sendTextMessage(String msg, String mobile, String webHookAddress) {
        RobotMessage message = new RobotMessage();
        message.setMsgtype(MessageTypeEnum.text);
        TextMessage textMessage = new TextMessage();
        textMessage.setContent(msg);
        textMessage.setMentioned_mobile_list(Arrays.asList(mobile));
        JSONObject text = JSONObject.parseObject(JSONObject.toJSONString(textMessage));
        message.setText(text);
        return sendMessage(message, webHookAddress);
    }

    public static boolean sendMessage(RobotMessage message, String webHookAddress) {
        OkHttpClient client = new OkHttpClient.Builder()
            // 设置连接超时时间
            .connectTimeout(10, TimeUnit.SECONDS)
            // 设置读取超时时间
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
        MediaType contentType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(contentType, JSONObject.toJSONString(message));
        Request request = new Request.Builder().url(webHookAddress).post(body)
            .addHeader("cache-control", "no-cache").build();
        try {
            Response response = client.newCall(request).execute();
            byte[] datas = response.body().bytes();
            String respMsg = new String(datas);
            JSONObject resultJSON = JSONObject.parseObject(respMsg);
            if (resultJSON.getIntValue("errcode") == 0) {
                log.info("企业微信消息发送成功!");
                return true;
            }
            log.error("企业微信消息发送失败, 错误信息如下: {}", resultJSON.getString("errmsg"));
            return false;
        } catch (IOException e) {
            log.error("企业微信消息发送失败, 异常信息如下: {}", e.getMessage());
            return false;
        }
    }

}

