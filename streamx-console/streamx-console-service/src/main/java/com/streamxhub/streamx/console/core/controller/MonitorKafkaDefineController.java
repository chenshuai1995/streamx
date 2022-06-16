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

package com.streamxhub.streamx.console.core.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.streamxhub.streamx.console.base.domain.RestRequest;
import com.streamxhub.streamx.console.base.domain.RestResponse;
import com.streamxhub.streamx.console.core.entity.MonitorKafkaDefine;
import com.streamxhub.streamx.console.core.service.MonitorKafkaDefineService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jim Chen
 */
@Slf4j
@Validated
@RestController
@RequestMapping("monitor/alarm/kafka")
public class MonitorKafkaDefineController {


    @Autowired
    private MonitorKafkaDefineService monitorKafkaDefineService;

    @PostMapping("create")
//    @RequiresPermissions("alarm:create")
    public RestResponse create(MonitorKafkaDefine monitorKafkaDefine) throws IOException {
        boolean saved = monitorKafkaDefineService.create(monitorKafkaDefine);
        return RestResponse.create().data(saved);
    }

    @PostMapping("update")
//    @RequiresPermissions("alarm:update")
    public RestResponse update(MonitorKafkaDefine monitorKafkaDefine) {
        boolean updated = monitorKafkaDefineService.update(monitorKafkaDefine);
        return RestResponse.create().data(updated);
    }

    @PostMapping("online")
//    @RequiresPermissions("alarm:update")
    public RestResponse online(MonitorKafkaDefine monitorKafkaDefine) {
        boolean updated = monitorKafkaDefineService.online(monitorKafkaDefine);
        return RestResponse.create().data(updated);
    }

    @PostMapping("offline")
//    @RequiresPermissions("alarm:update")
    public RestResponse offline(MonitorKafkaDefine monitorKafkaDefine) {
        boolean updated = monitorKafkaDefineService.offline(monitorKafkaDefine);
        return RestResponse.create().data(updated);
    }

    @PostMapping("list")
//    @RequiresPermissions("alarm:list")
    public RestResponse list(MonitorKafkaDefine monitorKafkaDefine, RestRequest request) {
        IPage<MonitorKafkaDefine> monitorKafkaDefineList = monitorKafkaDefineService.page(monitorKafkaDefine, request);
        List<MonitorKafkaDefine> monitorKafkaDefineRecords = monitorKafkaDefineList.getRecords();

        monitorKafkaDefineList.setRecords(monitorKafkaDefineRecords);
        return RestResponse.create().data(monitorKafkaDefineList);
    }

    @PostMapping("delete")
//    @RequiresPermissions("alarm:update")
    public RestResponse delete(MonitorKafkaDefine monitorKafkaDefine) {
        boolean updated = monitorKafkaDefineService.delete(monitorKafkaDefine);
        return RestResponse.create().data(updated);
    }

}
