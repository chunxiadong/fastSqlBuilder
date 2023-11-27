package com.qiushangcheng.fastsqlbuilder.core;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */

import com.qiushangcheng.fastsqlbuilder.util.ReflectionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
public class SqlBuilder {
    private final MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    private final List<String> updateList = new ArrayList<>();
    private final List<String> insertList = new ArrayList<>();
    private String sql = "";
    private final TableInfo tableInfo;
    // 参数索引
    private int index = 0;
    // 0 SELECT 1 INSERT 2 UPDATE 3 DELETE
    private int sqlType = 0;
    private int insertNumCount = 1;
    public static int maxInsertNum = 1000;
    private static final String PARAM_PREFIX = "param_";

    public SqlBuilder(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    @Data
    @AllArgsConstructor
    public static class TableInfo {
        private String tableName;
        private List<String> fieldList;
    }

    /*
     * 查询
     *
     */
    public SqlBuilder select(String... fields) {
        sql = "SELECT ";
        StringBuilder sb = new StringBuilder();
        if (fields.length == 0) {
            for (String field : tableInfo.getFieldList()) {
                sb.append(field).append(", ");
            }
        } else {
            for (String field : fields) {
                sb.append(field).append(", ");
            }
        }
        sql += sb.toString();
        sqlRevise();
        sql += " FROM " + tableInfo.getTableName() + " ";
        return this;
    }

    public SqlBuilder select(List<SqlBuildResult> sqlBuildResults) {
        sql = "SELECT ";
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(sqlBuildResults)) {
            paramsTransfer(sb, sqlBuildResults);
        }
        sql += sb.toString();
        sqlRevise();
        sql = sql + " FROM " + tableInfo.getTableName() + " ";
        return this;
    }

    public SqlBuilder select(List<SqlBuildResult> sqlBuildResults, String... fields) {
        sql = "SELECT ";
        StringBuilder sb = new StringBuilder();
        if (fields.length != 0) {
            for (String field : fields) {
                sb.append(field).append(", ");
            }
        }
        if (!CollectionUtils.isEmpty(sqlBuildResults)) {
            paramsTransfer(sb, sqlBuildResults);
        }
        sql += sb.toString();
        sqlRevise();
        sql = sql + " FROM " + tableInfo.getTableName() + " ";
        return this;
    }

    private void paramsTransfer(StringBuilder sb, List<SqlBuildResult> sqlBuildResults) {
        for (SqlBuildResult sqlBuildResult : sqlBuildResults) {
            if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
                sb.append(sqlBuildResult.getSql()).append(", ");
            }
            if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
                mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
            }
        }
    }

    /*插入*/

    public SqlBuilder insertField(String... fields) {
        return insertField(new ArrayList<>(Arrays.asList(fields)), false);
    }

    public SqlBuilder insertField(boolean isIgnoreField, String... fields) {
        return insertField(new ArrayList<>(Arrays.asList(fields)), isIgnoreField);
    }

    private SqlBuilder insertField(List<String> fields, boolean isIgnoreField) {
        sqlType = 1;
        sql = "INSERT INTO " + tableInfo.getTableName() + " ( ";
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(fields)) {
            if (isIgnoreField) {
                insertList.addAll(tableInfo.getFieldList());
                insertList.removeAll(fields);
                for (String s : insertList) {
                    sb.append(s).append(", ");
                }
            } else {
                for (String s : fields) {
                    sb.append(s).append(", ");
                }
                insertList.addAll(fields);
            }
        } else {
            for (String field : tableInfo.getFieldList()) {
                sb.append(field).append(", ");
            }
            insertList.addAll(tableInfo.getFieldList());
        }
        sql += sb.toString();
        sqlRevise();
        sql += " VALUES ";
        return this;
    }

    public <T> SqlBuilder batchInsert(List<T> list) {
        for (T t : list) {
            if (insertNumCount > maxInsertNum) {
                log.warn("SqlBuildUtil: reach max insert number limit={}, discard after all!!!", maxInsertNum);
                break;
            }
            StringBuilder sb = new StringBuilder(sql);
            if (insertNumCount > 1) {
                sb.append(", ");
            }
            insertNumCount++;
            sb.append(" ( ");
            Map<String, Object> map = ReflectionUtil.getParamMap(tableInfo.getTableName(), t);
            if (!CollectionUtils.isEmpty(map)) {
                for (String field : insertList) {
                    String p = PARAM_PREFIX + index++;
                    sb.append(":").append(p).append(", ");
                    mapSqlParameterSource.addValue(p, map.get(field));
                }
            }
            sql = sb.toString();
            sqlRevise();
        }
        return this;
    }

    public <T> SqlBuilder insertObject(T target) {
        if (Objects.isNull(target)) {
            return this;
        }
        sql += " ( ";
        StringBuilder sb = new StringBuilder();
        Map<String, Object> map = ReflectionUtil.getParamMap(tableInfo.getTableName(), target);
        if (!CollectionUtils.isEmpty(map)) {
            for (String field : insertList) {
                String p = PARAM_PREFIX + index++;
                sb.append(":").append(p).append(", ");
                mapSqlParameterSource.addValue(p, map.get(field));
            }
        }
        sql += sb.toString();
        sqlRevise();
        return this;
    }

    public <T> SqlBuilder insertParam(T... param) {
        if (insertNumCount > maxInsertNum) {
            log.warn("SqlBuildUtil: reach max insert number limit={}, discard after all!!!", maxInsertNum);
            return this;
        }
        if (insertNumCount > 1) {
            sql += ", ";
        }
        insertNumCount++;
        sql += " ( ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < param.length; i++) {
            String p = PARAM_PREFIX + index++;
            sb.append(":").append(p).append(", ");
            mapSqlParameterSource.addValue(p, param[i]);
        }
        sql += sb.toString();
        sqlRevise();
        return this;
    }

    public SqlBuilder onDuplicateKeyUpdate() {
        sql += " ON DUPLICATE KEY UPDATE ";
        return this;
    }


    public <T> SqlBuilder updateIfPresent(String field, T newValue, boolean isExpression) {
        sqlType = 2;
        if (isExpression) {
            sql += " " + field + " = " + newValue + ", ";
        } else {
            String p = PARAM_PREFIX + index++;
            sql += " " + field + " = " + ":" + p + ", ";
            mapSqlParameterSource.addValue(p, newValue);
        }
        return this;
    }

    /*
     * 删除
     */

    public SqlBuilder delete() {
        sql = "DELETE FROM " + tableInfo.getTableName() + " ";
        sqlType = 3;
        return this;
    }

    /*
     * 更新
     */

    public SqlBuilder updateField(String... fields) {
        sqlType = 2;
        sql = "UPDATE " + tableInfo.getTableName() + " SET ";
        if (fields.length == 0) {
            updateList.addAll(tableInfo.getFieldList());
        } else {
            updateList.addAll(Arrays.asList(fields));
        }
        return this;
    }

    public <T> SqlBuilder updateParam(T... params) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (String field : updateList) {
            String p = PARAM_PREFIX + index++;
            sb.append(field).append(" = ").append(":").append(p).append(", ");
            mapSqlParameterSource.addValue(p, params[i]);
            i++;
        }
        sql += sb.toString();
        return this;
    }

    public <T> SqlBuilder updateObject(T target) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> map = ReflectionUtil.getParamMap(tableInfo.getTableName(), target);
        if (!CollectionUtils.isEmpty(map)) {
            for (String field : updateList) {
                String p = PARAM_PREFIX + index++;
                sb.append(field).append(" = ").append(":").append(p).append(", ");
                mapSqlParameterSource.addValue(p, map.get(field));
            }
        }
        sql += sb.toString();
        sqlRevise();
        return this;
    }


    /*
     * 通用
     */

    public SqlBuilder or() {
        sqlRevise();
        sql = sql + " OR ";
        return this;
    }

    public SqlBuilder and() {
        sqlRevise();
        sql = sql + " AND ";
        return this;
    }


    public SqlBuilder where() {
        sqlRevise();
        sql = sql + " WHERE ";
        return this;
    }

    public <T> SqlBuilder equal(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " = " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlBuilder fieldEqual(String field1, String field2) {
        sql += " " + field1 + " = " + field2 + " ";
        return this;
    }

    public SqlBuilder equal(String field, SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql = sql + field + " = " + sqlBuildResult.getSql() + " ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public SqlBuilder isNull(String field) {
        sql = sql + field + " IS NULL ";
        return this;
    }

    public SqlBuilder isNotNull(String field) {
        sql = sql + field + " IS NOT NULL ";
        return this;
    }

    public <T> SqlBuilder in(String field, List<T> params) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " IN ( " + ":" + p + " ) ";
        mapSqlParameterSource.addValue(p, params);
        return this;
    }

    public SqlBuilder in(String field, SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql = sql + field + " IN ( " + sqlBuildResult.getSql() + " ) ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public SqlBuilder like(String field, String param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " LIKE " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlBuilder like(String field, SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql = sql + field + " LIKE " + sqlBuildResult.getSql() + " ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public SqlBuilder orderBy(String field, String sortDirection) {
        sqlRevise();
        sql = sql + " ORDER BY " + field + " " + sortDirection + " ";
        return this;
    }

    public SqlBuilder groupBy(String field) {
        sqlRevise();
        sql = sql + " GROUP BY " + field + " ";
        return this;
    }

    public SqlBuilder having(SqlBuildResult expression) {
        if (Objects.isNull(expression)) {
            return this;
        }
        if (StringUtils.isNotEmpty(expression.getSql())) {
            sql = sql + " HAVING " + expression.getSql() + " ";
        }
        if (Objects.nonNull(expression.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(expression.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public <T> SqlBuilder betweenAnd(String field, T between, T and) {
        String p1 = PARAM_PREFIX + index++;
        String p2 = PARAM_PREFIX + index++;
        sql = sql + field + " BETWEEN " + ":" + p1 + " AND " + ":" + p2 + " ";
        mapSqlParameterSource.addValue(p1, between);
        mapSqlParameterSource.addValue(p2, and);
        return this;
    }

    public SqlBuilder EXISTS(SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql = sql + " EXISTS ( " + sqlBuildResult.getSql() + " ) ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public <T> SqlBuilder lt(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " < " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlBuilder lt(String field, SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql = sql + field + " < " + sqlBuildResult.getSql() + " ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public <T> SqlBuilder lte(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " <= " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlBuilder lte(String field, SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql = sql + field + " <= " + sqlBuildResult.getSql() + " ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public <T> SqlBuilder gt(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " > " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlBuilder gt(String field, SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql = sql + field + " > " + sqlBuildResult.getSql() + " ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public <T> SqlBuilder gte(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " >= " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlBuilder gte(String field, SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql = sql + field + " >= " + sqlBuildResult.getSql() + " ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }


    public SqlBuilder limit(Integer num) {
        sql += " LIMIT " + num + " ";
        return this;
    }

    public SqlBuilder IF(SqlBuildResult expression, Object trueValue, Object falseValue) {
        if (Objects.isNull(expression)) {
            return this;
        }
        if (StringUtils.isNotEmpty(expression.getSql())) {
            sql += " IF( " + expression.getSql() + ", " + trueValue + ", " + falseValue + " ) ";
        }
        if (Objects.nonNull(expression.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(expression.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    // 连表查询
    public SqlBuilder leftJoin(String tableName) {
        sql += " LEFT JOIN " + tableName + " ";
        return this;
    }

    public SqlBuilder rightJoin(String tableName) {
        sql += " RIGHT JOIN " + tableName + " ";
        return this;
    }

    public SqlBuilder innerJoin(String tableName) {
        sql += " INNER JOIN " + tableName + " ";
        return this;
    }

    public SqlBuilder on() {
        sql += " ON ";
        return this;
    }

    public SqlBuilder customize(String customize) {
        sql += " " + customize + " ";
        return this;
    }

    /**
     * sql修正
     */
    private void sqlRevise() {
        if (sql.endsWith("AND ")) {
            sql = sql.substring(0, sql.length() - 4);
        }
        if (sql.endsWith("OR ")) {
            sql = sql.substring(0, sql.length() - 3);
        }
        if (sql.endsWith(", ")) {
            if (sqlType == 1) {
                sql = sql.substring(0, sql.length() - 2) + " )";
            }
            if (sqlType == 2 || sqlType == 0) {
                sql = sql.substring(0, sql.length() - 2);
            }
        }
    }

    public SqlBuildResult build() {
        return buildSql(true);
    }

    public SqlBuildResult build(boolean printSqlInfo) {
        return buildSql(printSqlInfo);
    }

    private SqlBuildResult buildSql(boolean printSqlInfo) {
        sqlRevise();
        index = 0;
        sqlType = 0;
        insertNumCount = 1;
        updateList.clear();
        insertList.clear();
        sql = sql.trim().replaceAll("\\s+", " ");
        if (printSqlInfo) {
            log.info("SqlBuildUtil: {}, {}", sql, mapSqlParameterSource);
        }
        SqlBuildResult sqlBuildResult = new SqlBuildResult(mapSqlParameterSource, sql);
        sql = "";
        return sqlBuildResult;
    }
}
