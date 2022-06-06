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

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.streamxhub.streamx.console.base.domain.RestRequest;
import com.streamxhub.streamx.console.base.util.CalciteUtils;
import com.streamxhub.streamx.console.base.util.JacksonUtils;
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
public class MetaTableServiceImpl extends ServiceImpl<MetaTableMapper, MetaTable>
    implements MetaTableService {

    @Autowired
    private FlinkSqlService flinkSqlService;

    @Autowired
    private LineageAppService lineageAppService;

    @Override
    public IPage<MetaTable> page(MetaTable metaTable, RestRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean create(Application app) throws IOException {
        FlinkSql flinkSql = flinkSqlService.getEffective(app.getId(), true);
        List<SqlCommandCall> sqlCommandCallList = SqlCommandParser.parseSQL(flinkSql.getSql());
        Iterator<SqlCommandCall> iterator = sqlCommandCallList.iterator();
        while (iterator.hasNext()) {
            SqlCommandCall sqlCommandCall = iterator.next();
            SqlCommand command = sqlCommandCall.command();
            if ("CREATE_TABLE".equals(command.entryName()) ||
                "CREATE_VIEW".equals(command.entryName()) ||
                "INSERT_INTO".equals(command.entryName())) {
                String parsedResult = CalciteUtils.parseSql(sqlCommandCall.originSql());
                if (!parsedResult.isEmpty()) {
                    HashMap sqlMeta = JacksonUtils.read(parsedResult, HashMap.class);

                    if ("CREATE_TABLE".equals(sqlMeta.get("sqlType"))) {
                        processCreateTable(app, sqlMeta);
                    } else if ("CREATE_VIEW".equals(sqlMeta.get("sqlType"))) {
                        processCreateView(app, sqlMeta);
                    } else if ("INSERT_INTO".equals(sqlMeta.get("sqlType"))) {
                        processInsertInto(app, sqlMeta);
                    }
                }
            }
        }


        return true;
    }

    private boolean processInsertInto(Application application, HashMap sqlMeta) throws IOException {
        return lineageAppService.createByApplication(application, sqlMeta);
    }

    private boolean processCreateView(Application application, HashMap sqlMeta) throws IOException {
        return lineageAppService.createViewByApplication(application, sqlMeta);
    }

    private boolean processCreateTable(Application app, HashMap sqlMeta)
        throws com.fasterxml.jackson.core.JsonProcessingException {
        String tableName = (String)sqlMeta.get("tableName");
        String withOptionsJson = (String) sqlMeta.get("withOptions");
        Map<String, String> withOptions = JacksonUtils.read(withOptionsJson, Map.class);
        String connector = withOptions.get("connector");

        MetaTable metaTable = this.baseMapper.getByAppId(app.getId());
        if (metaTable != null) {
            metaTable.setFlinkTableName(tableName);
            metaTable.setFlinkTableOptions(withOptionsJson);
            metaTable.setFlinkTableOptionsConnector(connector);
            metaTable.setUpdateTime(new Date());
            return this.updateById(metaTable);
        } else {
            metaTable = new MetaTable();
            metaTable.setAppId(app.getId());
            metaTable.setFlinkTableName(tableName);
            metaTable.setFlinkTableOptions(withOptionsJson);
            metaTable.setFlinkTableOptionsConnector(connector);
            metaTable.setCreateTime(new Date());
            metaTable.setUpdateTime(new Date());
            return this.save(metaTable);
        }
    }

    @Override
    public Boolean delete(MetaTable metaTable) {
        return null;
    }
}
