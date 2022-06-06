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

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.streamxhub.streamx.console.base.domain.RestRequest;
import com.streamxhub.streamx.console.base.util.CalciteUtils;
import com.streamxhub.streamx.console.base.util.JacksonUtils;
import com.streamxhub.streamx.console.core.dao.LineageAppMapper;
import com.streamxhub.streamx.console.core.dao.MetaTableMapper;
import com.streamxhub.streamx.console.core.entity.Application;
import com.streamxhub.streamx.console.core.entity.FlinkSql;
import com.streamxhub.streamx.console.core.entity.LineageApp;
import com.streamxhub.streamx.console.core.entity.MetaTable;
import com.streamxhub.streamx.console.core.service.FlinkSqlService;
import com.streamxhub.streamx.console.core.service.LineageAppService;
import com.streamxhub.streamx.console.core.service.MetaTableService;
import com.streamxhub.streamx.flink.core.SqlCommand;
import com.streamxhub.streamx.flink.core.SqlCommandCall;
import com.streamxhub.streamx.flink.core.SqlCommandParser;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import scala.collection.Iterator;
import scala.collection.immutable.List;

/**
 * @author jim
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LineageAppServiceImpl extends ServiceImpl<LineageAppMapper, LineageApp>
    implements LineageAppService {


    @Override
    public IPage<LineageApp> page(LineageApp lineageApp, RestRequest request) {
        return null;
    }

    @Override
    public boolean create(LineageApp lineageApp) throws IOException {
        return this.save(lineageApp);
    }

    @Override
    public Boolean delete(LineageApp lineageApp) {
        return null;
    }

    @Override
    public boolean createByApplication(Application application, HashMap sqlMeta) {
        String sourceTable = (String) sqlMeta.get("sourceTable");
        String optionKVPairsJson = (String) sqlMeta.get("optionKVPairs");
        String targetTable = (String) sqlMeta.get("targetTable");

        LineageApp lineageApp = this.baseMapper.getByAppIdAndTargetTable(application.getId(), targetTable);
        if (lineageApp != null) {
            lineageApp.setFlinkSourceTableName(sourceTable);
            lineageApp.setFlinkSourceTableOptions(optionKVPairsJson);
            lineageApp.setFlinkTargetTableName(targetTable);
            lineageApp.setUpdateTime(new Date());

            UpdateWrapper<LineageApp> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("app_id", lineageApp.getAppId());
            updateWrapper.eq("flink_target_table_name", lineageApp.getFlinkTargetTableName());

            return update(lineageApp, updateWrapper);
        } else {
            lineageApp = new LineageApp();
            lineageApp.setAppId(application.getId());
            lineageApp.setFlinkSourceTableName(sourceTable);
            lineageApp.setFlinkSourceTableOptions(optionKVPairsJson);
            lineageApp.setFlinkTargetTableName(targetTable);
            lineageApp.setCreateTime(new Date());
            lineageApp.setUpdateTime(new Date());
            return this.save(lineageApp);
        }
    }

    @Override
    public boolean createViewByApplication(Application application, HashMap sqlMeta) {
        String viewName = (String) sqlMeta.get("viewName");
        String temporary = (String) sqlMeta.get("temporary");
        String sourceTable = (String) sqlMeta.get("sourceTable");
        String optionKVPairsJson = (String) sqlMeta.get("optionKVPairs");

        LineageApp lineageApp = this.baseMapper.getByAppIdAndTargetView(application.getId(), viewName);
        if (lineageApp != null) {
            lineageApp.setFlinkSourceTableName(sourceTable);
            lineageApp.setFlinkSourceTableOptions(optionKVPairsJson);
            lineageApp.setUpdateTime(new Date());
            this.baseMapper.updateByAppIdAndTargetView(lineageApp);
        } else {
            lineageApp = new LineageApp();
            lineageApp.setAppId(application.getId());
            lineageApp.setFlinkSourceTableName(sourceTable);
            lineageApp.setFlinkSourceTableOptions(optionKVPairsJson);
            lineageApp.setFlinkTargetTableName(viewName);
            lineageApp.setView(1);
            lineageApp.setTemporary(Boolean.parseBoolean(temporary) ? 1 : 0);
            lineageApp.setCreateTime(new Date());
            lineageApp.setUpdateTime(new Date());
            return save(lineageApp);
        }
        return true;
    }
}
