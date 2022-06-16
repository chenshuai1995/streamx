package com.streamxhub.streamx.console.core.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jim Chen
 * @date 2022-06-13
 */
@Getter
@Slf4j
public enum  MessageTypeEnum {
  unknown_type(-1, "未知类型"),
  text(1, "文本类型"),
  image(2, "图片类型");

  private final int value;
  private final String desc;


  MessageTypeEnum(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  /**
   * 根据值返回枚举
   *
   * @param value
   * @return
   */
  public static MessageTypeEnum getEnumByValue(int value) {
    for (MessageTypeEnum v : MessageTypeEnum.values()) {
      if (v.getValue() == value) {
        return v;
      }
    }
    return unknown_type;
  }

  /**
   * 根据编码获取描述信息
   *
   * @param value
   * @return
   */
  public static String getDescByValue(String value) {
    if (value == null || "".equals(value)) {
      return null;
    }
    try {
      int discount = Integer.parseInt(value.trim());
      for (MessageTypeEnum v : MessageTypeEnum.values()) {
        if (v.getValue() == discount) {
          return v.getDesc();
        }
      }
      return "未知类型: " + value;
    } catch (Exception e) {
      log.info("字符串格式转数字异常，str: {}", value);
      return null;
    }
  }
}
