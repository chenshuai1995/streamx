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

package com.streamxhub.streamx.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.streamxhub.streamx.console.base.domain.Constant;
import com.streamxhub.streamx.console.base.domain.RestRequest;
import com.streamxhub.streamx.console.base.util.SortUtils;
import com.streamxhub.streamx.console.core.dao.MonitorDefineMapper;
import com.streamxhub.streamx.console.core.entity.MonitorDefine;
import com.streamxhub.streamx.console.core.service.CommonService;
import com.streamxhub.streamx.console.core.service.MonitorDefineService;
import com.streamxhub.streamx.console.system.entity.User;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jim
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MonitorDefineServiceImpl extends ServiceImpl<MonitorDefineMapper, MonitorDefine>
    implements MonitorDefineService {

    @Autowired
    private CommonService commonService;

    @Override
    public boolean create(MonitorDefine monitorDefine) {
        Map<String, Object> map = new HashMap<>();
        map.put("execution_mode", monitorDefine.getExecutionMode());
        map.put("app_name", monitorDefine.getAppName());
        List<MonitorDefine> monitorDefines = this.baseMapper.selectByMap(map);
        if (monitorDefines != null && monitorDefines.size() > 0) {
            log.error("该任务已存在, " + monitorDefine);
            return false;
        }

        User currentUser = commonService.getCurrentUser();

        monitorDefine.setRelease(0); // '0：下线，1上线'
        monitorDefine.setCreateBy(currentUser.getUserId());
        monitorDefine.setUpdateBy(currentUser.getUserId());
        monitorDefine.setMaintainBy(currentUser.getUserId());
        monitorDefine.setCreateName(currentUser.getUsername());
        monitorDefine.setUpdateName(currentUser.getUsername());
        monitorDefine.setMaintainName(currentUser.getUsername());
        monitorDefine.setCreateTime(new Date());
        monitorDefine.setUpdateTime(new Date());
        return save(monitorDefine);
    }

    @Override
    public boolean update(MonitorDefine monitorDefine) {
        try {
            if (monitorDefine.getId() == null || "".equals(monitorDefine.getId().toString())) {
                log.error("id 不存在， " + monitorDefine);
                return false;
            }

            User currentUser = commonService.getCurrentUser();

            monitorDefine.setUpdateBy(currentUser.getUserId());
            monitorDefine.setMaintainBy(currentUser.getUserId());
            monitorDefine.setUpdateName(currentUser.getUsername());
            monitorDefine.setMaintainName(currentUser.getUsername());
            monitorDefine.setUpdateTime(new Date());
            return updateById(monitorDefine);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public IPage<MonitorDefine> page(MonitorDefine monitorDefine, RestRequest request) {
        Page<MonitorDefine> page = new Page<>();
        SortUtils.handlePageSort(request, page, "update_time", Constant.ORDER_DESC, false);
        this.baseMapper.page(page, monitorDefine);
        List<MonitorDefine> records = page.getRecords();
        // TODO 查询用户名
        page.setRecords(records);
        return page;
    }

    @Override
    public boolean online(MonitorDefine monitorDefine) {
        try {
            if (monitorDefine.getId() == null || "".equals(monitorDefine.getId().toString())) {
                log.error("id 不存在， " + monitorDefine);
                return false;
            }

            User currentUser = commonService.getCurrentUser();

            monitorDefine.setRelease(1);
            monitorDefine.setUpdateBy(currentUser.getUserId());
            monitorDefine.setMaintainBy(currentUser.getUserId());
            monitorDefine.setUpdateName(currentUser.getUsername());
            monitorDefine.setMaintainName(currentUser.getUsername());
            monitorDefine.setUpdateTime(new Date());
            return updateById(monitorDefine);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public boolean offline(MonitorDefine monitorDefine) {
        try {
            if (monitorDefine.getId() == null || "".equals(monitorDefine.getId().toString())) {
                log.error("id 不存在， " + monitorDefine);
                return false;
            }

            User currentUser = commonService.getCurrentUser();

            monitorDefine.setRelease(0);
            monitorDefine.setUpdateBy(currentUser.getUserId());
            monitorDefine.setMaintainBy(currentUser.getUserId());
            monitorDefine.setUpdateName(currentUser.getUsername());
            monitorDefine.setMaintainName(currentUser.getUsername());
            monitorDefine.setUpdateTime(new Date());
            return updateById(monitorDefine);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public boolean delete(MonitorDefine monitorDefine) {
        try {
            if (monitorDefine.getId() == null || "".equals(monitorDefine.getId().toString())) {
                log.error("id 不存在， " + monitorDefine);
                return false;
            }

            User currentUser = commonService.getCurrentUser();

            monitorDefine.setDeleted(1);
            monitorDefine.setUpdateBy(currentUser.getUserId());
            monitorDefine.setMaintainBy(currentUser.getUserId());
            monitorDefine.setUpdateName(currentUser.getUsername());
            monitorDefine.setMaintainName(currentUser.getUsername());
            monitorDefine.setUpdateTime(new Date());
            return updateById(monitorDefine);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public List<MonitorDefine> getOnlines() {
        QueryWrapper<MonitorDefine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("`release`", 1);
        queryWrapper.eq("deleted", 0);
        List<MonitorDefine> defines = this.baseMapper.selectList(queryWrapper);

        return defines;
    }
}
