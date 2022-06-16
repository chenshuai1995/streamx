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

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Jim Chen
 */
@Data
@TableName("t_monitor_kafka_instance")
@Slf4j
public class MonitorKafkaInstance implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * kafka监控定义id
     */
    private Long monitorKafkaId;

    /**
     * 触发时间
     */
    private Date triggerTime;

    /**
     * 触发原因
     */
    private String reason;

    /**
     * 是否解决：'0未解决，1已解决'
     */
    private Integer slovedState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
