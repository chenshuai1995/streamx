package com.streamxhub.streamx.console.base.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.sql.SqlHint;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlTableRef;
import org.apache.flink.sql.parser.ddl.SqlCreateCatalog;
import org.apache.flink.sql.parser.ddl.SqlCreateTable;
import org.apache.flink.sql.parser.ddl.SqlCreateView;
import org.apache.flink.sql.parser.ddl.SqlDropTable;
import org.apache.flink.sql.parser.ddl.SqlSet;
import org.apache.flink.sql.parser.ddl.SqlTableOption;
import org.apache.flink.sql.parser.ddl.SqlUseCatalog;
import org.apache.flink.sql.parser.dml.RichSqlInsert;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.planner.delegation.ParserImpl;

/**
 * @author Jim Chen
 */
@Slf4j
public class CalciteUtils {

    @SneakyThrows
    public static String parseSql(String sql) {
        ParserImpl parser = getParser();
        SqlNode sqlNode = parser.parseSqlNode(sql);

        String result = "";
        try {
            if (sqlNode instanceof RichSqlInsert) {
                result = parseRichSqlInsert((RichSqlInsert) sqlNode);
            } else if (sqlNode instanceof SqlCreateTable) {
                result = parseSqlCreateTable((SqlCreateTable) sqlNode);
            } else if (sqlNode instanceof SqlCreateView) {
                result = parseSqlCreateView((SqlCreateView) sqlNode);
            } else {
                log.warn("暂不支持解析该类型:" + sql);
            }
        } catch (Exception e) {
            log.error("解析SQL异常：" + sql, e);
        }

        return result;
    }

    /**
     * 解析 create view类型的SQL
     * @param createView
     * @return
     */
    @SneakyThrows
    private static String parseSqlCreateView(SqlCreateView createView) {
        Map<String, Object> result = new HashMap<>();
        result.put("sqlType", "CREATE_VIEW");

        String viewName = createView.getViewName().toString();
        boolean temporary = createView.isTemporary();
        result.put("viewName", viewName);
        result.put("temporary", temporary);

        SqlNode from = ((SqlSelect) createView.getQuery()).getFrom();

        String sourceTable = null;
        Map<String, String> optionKVPairs = new HashMap<>();

        if ("TABLE_REF".equals(from.getKind().toString())) {
            // 有option
            List<SqlNode> operandList = ((SqlTableRef) from).getOperandList();
            sourceTable = operandList.get(0).toString();

            SqlNode sourceTableOptions = operandList.get(1);
            List<SqlNode> sourceTableOptionsList = ((SqlNodeList) sourceTableOptions).getList();
            if (sourceTableOptionsList != null && sourceTableOptionsList.size() > 0) {
                for (SqlNode sqlNode : sourceTableOptionsList) {
                    optionKVPairs.putAll(((SqlHint)sqlNode).getOptionKVPairs());
                }
            }
        } else if ("IDENTIFIER".equals(from.getKind().toString())) {
            sourceTable = from.toString();
        }
        result.put("sourceTable", sourceTable);
        result.put("optionKVPairs", JacksonUtils.write(optionKVPairs));

        return JacksonUtils.write(result);
    }


    /**
     * 解析 create table类型的SQL
     * @param createTable
     */
    @SneakyThrows
    private static String parseSqlCreateTable(SqlCreateTable createTable) {
        Map<String, Object> result = new HashMap<>();
        result.put("sqlType", "CREATE_TABLE");

        String tableName = createTable.getTableName().toString();
        result.put("tableName", tableName);

        Map<String, String> withOptions = new HashMap<>();
        SqlNodeList propertyList = createTable.getPropertyList();
        List<SqlNode> list = propertyList.getList();
        for (SqlNode sqlNode : list) {
            withOptions.put(
                ((SqlTableOption) sqlNode).getKeyString(),
                ((SqlTableOption) sqlNode).getValueString());
        }
        result.put("withOptions", JacksonUtils.write(withOptions));
        return JacksonUtils.write(result);
    }

    /**
     * 解析 insert 类型的SQL
     * @param insert
     * @return
     */
    @SneakyThrows
    private static String parseRichSqlInsert(RichSqlInsert insert) {
        Map<String, Object> result = new HashMap<>();
        result.put("sqlType", "INSERT_INTO");

        // source
        SqlNode from = ((SqlSelect) insert.getSource()).getFrom();

        String sourceTable = null;
        Map<String, String> optionKVPairs = new HashMap<>();

        List<SqlNode> operandList;
        if ("TABLE_REF".equals(from.getKind().toString())) {
            operandList = ((SqlTableRef) from).getOperandList();
            sourceTable = operandList.get(0).toString();
            if (operandList.size() > 0) {
                SqlNode sourceTableOptions = operandList.get(1);
                List<SqlNode> sourceTableOptionsList = ((SqlNodeList) sourceTableOptions).getList();
                if (sourceTableOptionsList != null && sourceTableOptionsList.size() > 0) {
                    for (SqlNode sqlNode : sourceTableOptionsList) {
                        optionKVPairs.putAll(((SqlHint) sqlNode).getOptionKVPairs());
                    }
                }
            }
        } else if ("IDENTIFIER".equals(from.getKind().toString())) {
            sourceTable = from.toString();
        }
        result.put("sourceTable", sourceTable);
        result.put("optionKVPairs", JacksonUtils.write(optionKVPairs));

        // target
        String targetTable = insert.getTargetTable().toString();
        result.put("targetTable", targetTable);
        return JacksonUtils.write(result);
    }

    private static ParserImpl getParser() {
        EnvironmentSettings settings = EnvironmentSettings
            .newInstance()
            .useBlinkPlanner()
            .inStreamingMode()
            .build();

        TableEnvironmentImpl tableEnv = TableEnvironmentImpl.create(settings);
        ParserImpl parser = (ParserImpl)tableEnv.getParser();
        return parser;
    }

}
