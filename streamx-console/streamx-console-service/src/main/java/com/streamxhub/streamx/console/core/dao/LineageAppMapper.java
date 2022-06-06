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

package com.streamxhub.streamx.console.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.streamxhub.streamx.console.core.entity.LineageApp;
import javax.sound.sampled.Line;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author jim
 */
public interface LineageAppMapper extends BaseMapper<LineageApp> {
    IPage<LineageApp> page(Page<LineageApp> page, @Param("lineageApp") LineageApp lineageApp);

    LineageApp getLineageApp(@Param("lineageApp") LineageApp lineageApp);

    @Select("select * from t_lineage_app where app_id = #{appId}")
    LineageApp getByAppId(@Param("appId") Long appId);

    @Select("select * from t_lineage_app where app_id = #{appId} and flink_target_table_name = #{targetTable}")
    LineageApp getByAppIdAndTargetTable(@Param("appId") Long appId, @Param("targetTable") String targetTable);

    @Select("select * from t_lineage_app where app_id = #{appId} and flink_target_table_name = #{targetTable} and is_vew = 1")
    LineageApp getByAppIdAndTargetView(@Param("appId") Long appId, @Param("targetTable") String targetTable);

    @Update("update t_lineage_app set flink_source_table_name = #{lineageApp.flinkSourceTableName},"
        + "flink_source_table_options = #{lineageApp.flinkSourceTableOptions},"
        + "update_time = #{lineageApp.updateTime} "
        + "where app_id = #{lineageApp.appId} and flink_target_table_name = #{lineageApp.targetTable} and is_vew = 1 ")
    void updateByAppIdAndTargetView(@Param("lineageApp") LineageApp lineageApp);
}
