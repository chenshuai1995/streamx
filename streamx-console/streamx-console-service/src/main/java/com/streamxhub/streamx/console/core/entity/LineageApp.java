package com.streamxhub.streamx.console.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("t_lineage_app")
@Slf4j
public class LineageApp implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * flink source table name
     */
    private String flinkSourceTableName;

    /**
     * flink source table options
     */
    private String flinkSourceTableOptions;

    /**
     * flink target table name
     */
    private String flinkTargetTableName;

    @TableField("is_view")
    private Integer view;

    @TableField("is_temporary")
    private Integer temporary;

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
