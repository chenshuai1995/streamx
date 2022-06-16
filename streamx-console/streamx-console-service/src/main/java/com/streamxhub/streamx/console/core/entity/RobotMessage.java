package com.streamxhub.streamx.console.core.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author Jim Chen
 * @date 2022-06-13
 */
@Data
public class RobotMessage {
  /**
   * 消息类型
   */
  private MessageTypeEnum msgtype;
  /**
   * 文本消息
   */
  private JSONObject text;

  /**
   * 图片消息
   */
  private JSONObject image;

  /**
   * 表示是否是保密消息，0表示否，1表示是，默认0
   */
  private Integer safe;

}
