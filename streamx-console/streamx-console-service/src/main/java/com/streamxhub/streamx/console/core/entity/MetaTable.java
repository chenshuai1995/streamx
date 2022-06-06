package com.streamxhub.streamx.console.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jim Chen
 */
@Data
@TableName("t_meta_table")
@Slf4j
public class MetaTable implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * flink table name
     */
    private String flinkTableName;

    /**
     * flink table options
     */
    private String flinkTableOptions;

    /**
     * flink table options connector
     */
    private String flinkTableOptionsConnector;

    /**
     * create_time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * update_time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
