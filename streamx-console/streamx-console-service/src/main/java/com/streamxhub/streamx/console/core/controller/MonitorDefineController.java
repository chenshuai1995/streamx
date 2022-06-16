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
import com.streamxhub.streamx.console.core.entity.MonitorDefine;
import com.streamxhub.streamx.console.core.service.MonitorDefineService;
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
@RequestMapping("monitor/alarm")
public class MonitorDefineController {


    @Autowired
    private MonitorDefineService monitorDefineService;

    @PostMapping("create")
//    @RequiresPermissions("alarm:create")
    public RestResponse create(MonitorDefine monitorDefine) throws IOException {
        boolean saved = monitorDefineService.create(monitorDefine);
        return RestResponse.create().data(saved);
    }

    @PostMapping("update")
//    @RequiresPermissions("alarm:update")
    public RestResponse update(MonitorDefine monitorDefine) {
        boolean updated = monitorDefineService.update(monitorDefine);
        return RestResponse.create().data(updated);
    }

    @PostMapping("online")
//    @RequiresPermissions("alarm:update")
    public RestResponse online(MonitorDefine monitorDefine) {
        boolean updated = monitorDefineService.online(monitorDefine);
        return RestResponse.create().data(updated);
    }

    @PostMapping("offline")
//    @RequiresPermissions("alarm:update")
    public RestResponse offline(MonitorDefine monitorDefine) {
        boolean updated = monitorDefineService.offline(monitorDefine);
        return RestResponse.create().data(updated);
    }

    @PostMapping("delete")
//    @RequiresPermissions("alarm:update")
    public RestResponse delete(MonitorDefine monitorDefine) {
        boolean updated = monitorDefineService.delete(monitorDefine);
        return RestResponse.create().data(updated);
    }

    @PostMapping("list")
//    @RequiresPermissions("alarm:list")
    public RestResponse list(MonitorDefine monitorDefine, RestRequest request) {
        IPage<MonitorDefine> monitorDefineList = monitorDefineService.page(monitorDefine, request);
        List<MonitorDefine> monitorDefineRecords = monitorDefineList.getRecords();

        monitorDefineList.setRecords(monitorDefineRecords);
        return RestResponse.create().data(monitorDefineList);
    }

}
