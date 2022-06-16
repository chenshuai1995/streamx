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
import com.streamxhub.streamx.console.core.dao.MonitorKafkaDefineMapper;
import com.streamxhub.streamx.console.core.entity.MonitorKafkaDefine;
import com.streamxhub.streamx.console.core.service.CommonService;
import com.streamxhub.streamx.console.core.service.MonitorKafkaDefineService;
import com.streamxhub.streamx.console.system.entity.User;
import java.util.Date;
import java.util.List;
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
public class MonitorKafkaDefineServiceImpl extends ServiceImpl<MonitorKafkaDefineMapper, MonitorKafkaDefine>
    implements MonitorKafkaDefineService {

    @Autowired
    private CommonService commonService;

    @Override
    public boolean create(MonitorKafkaDefine monitorKafkaDefine) {

        User currentUser = commonService.getCurrentUser();

        monitorKafkaDefine.setRelease(0); // '0：下线，1上线'
        monitorKafkaDefine.setCreateBy(currentUser.getUserId());
        monitorKafkaDefine.setUpdateBy(currentUser.getUserId());
        monitorKafkaDefine.setMaintainBy(currentUser.getUserId());
        monitorKafkaDefine.setCreateName(currentUser.getUsername());
        monitorKafkaDefine.setUpdateName(currentUser.getUsername());
        monitorKafkaDefine.setMaintainName(currentUser.getUsername());
        monitorKafkaDefine.setCreateTime(new Date());
        monitorKafkaDefine.setUpdateTime(new Date());
        return save(monitorKafkaDefine);
    }

    @Override
    public boolean update(MonitorKafkaDefine monitorKafkaDefine) {
        try {
            if (monitorKafkaDefine.getId() == null || "".equals(monitorKafkaDefine.getId().toString())) {
                log.error("id 不存在， " + monitorKafkaDefine);
                return false;
            }

            User currentUser = commonService.getCurrentUser();

            monitorKafkaDefine.setUpdateBy(currentUser.getUserId());
            monitorKafkaDefine.setMaintainBy(currentUser.getUserId());
            monitorKafkaDefine.setUpdateName(currentUser.getUsername());
            monitorKafkaDefine.setMaintainName(currentUser.getUsername());
            monitorKafkaDefine.setUpdateTime(new Date());
            return updateById(monitorKafkaDefine);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public boolean delete(MonitorKafkaDefine monitorKafkaDefine) {
        try {
            if (monitorKafkaDefine.getId() == null || "".equals(monitorKafkaDefine.getId().toString())) {
                log.error("id 不存在， " + monitorKafkaDefine);
                return false;
            }

            User currentUser = commonService.getCurrentUser();

            monitorKafkaDefine.setDeleted(1);
            monitorKafkaDefine.setUpdateBy(currentUser.getUserId());
            monitorKafkaDefine.setMaintainBy(currentUser.getUserId());
            monitorKafkaDefine.setUpdateName(currentUser.getUsername());
            monitorKafkaDefine.setMaintainName(currentUser.getUsername());
            monitorKafkaDefine.setUpdateTime(new Date());
            return updateById(monitorKafkaDefine);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public boolean online(MonitorKafkaDefine monitorKafkaDefine) {
        try {
            if (monitorKafkaDefine.getId() == null || "".equals(monitorKafkaDefine.getId().toString())) {
                log.error("id 不存在， " + monitorKafkaDefine);
                return false;
            }

            User currentUser = commonService.getCurrentUser();

            monitorKafkaDefine.setRelease(1);
            monitorKafkaDefine.setUpdateBy(currentUser.getUserId());
            monitorKafkaDefine.setMaintainBy(currentUser.getUserId());
            monitorKafkaDefine.setUpdateName(currentUser.getUsername());
            monitorKafkaDefine.setMaintainName(currentUser.getUsername());
            monitorKafkaDefine.setUpdateTime(new Date());
            return updateById(monitorKafkaDefine);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public boolean offline(MonitorKafkaDefine monitorKafkaDefine) {
        try {
            if (monitorKafkaDefine.getId() == null || "".equals(monitorKafkaDefine.getId().toString())) {
                log.error("id 不存在， " + monitorKafkaDefine);
                return false;
            }

            User currentUser = commonService.getCurrentUser();

            monitorKafkaDefine.setRelease(0);
            monitorKafkaDefine.setUpdateBy(currentUser.getUserId());
            monitorKafkaDefine.setMaintainBy(currentUser.getUserId());
            monitorKafkaDefine.setUpdateName(currentUser.getUsername());
            monitorKafkaDefine.setMaintainName(currentUser.getUsername());
            monitorKafkaDefine.setUpdateTime(new Date());
            return updateById(monitorKafkaDefine);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public List<MonitorKafkaDefine> getOnlines() {
        QueryWrapper<MonitorKafkaDefine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("`release`", 1);
        queryWrapper.eq("deleted", 0);
        List<MonitorKafkaDefine> defines = this.baseMapper.selectList(queryWrapper);

        return defines;
    }

    @Override
    public IPage<MonitorKafkaDefine> page(MonitorKafkaDefine monitorKafkaDefine, RestRequest request) {
        Page<MonitorKafkaDefine> page = new Page<>();
        SortUtils.handlePageSort(request, page, "update_time", Constant.ORDER_DESC, false);
        this.baseMapper.page(page, monitorKafkaDefine);
        List<MonitorKafkaDefine> records = page.getRecords();
        // TODO 查询用户名
        page.setRecords(records);
        return page;
    }
}
