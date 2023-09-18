package com.qiushangcheng.fastsqlbuilder.core;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author qiushangcheng
 * @description
 * @date 2023/1/3 15:02
 */

@Slf4j
public class SqlExpressionCreator {
    private final MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    private String sql = "";
    // 参数索引
    private int index = 0;
    // 0 SELECT 1 INSERT 2 UPDATE 3 DELETE
    private int sqlType = 0;
    private static final String PARAM_PREFIX = "expression_";

    /*
     * 查询
     */

    public SqlExpressionCreator select(String tableName, String... fields) {
        sql = "SELECT ";
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            sb.append(field).append(", ");
        }
        sql += sb.toString();
        sqlRevise();
        sql += " FROM " + tableName + " ";
        return this;
    }

    public SqlExpressionCreator select(String tableName, List<SqlBuildResult> sqlBuildResults) {
        sql = "SELECT ";
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(sqlBuildResults)) {
            paramsTransfer(sb, sqlBuildResults);
        }
        sql += sb.toString();
        sqlRevise();
        sql = sql + " FROM " + tableName + " ";
        return this;
    }

    public SqlExpressionCreator select(String tableName, List<SqlBuildResult> sqlBuildResults, List<String> fields) {
        sql = "SELECT ";
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(fields)) {
            for (String field : fields) {
                sb.append(field).append(", ");
            }
        }
        if (!CollectionUtils.isEmpty(sqlBuildResults)) {
            paramsTransfer(sb, sqlBuildResults);
        }
        sql += sb.toString();
        sqlRevise();
        sql = sql + " FROM " + tableName + " ";
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

    /*
     * 通用
     */

    public SqlExpressionCreator or() {
        sqlRevise();
        sql = sql + " OR ";
        return this;
    }

    public SqlExpressionCreator and() {
        sqlRevise();
        sql = sql + " AND ";
        return this;
    }


    public SqlExpressionCreator where() {
        sqlRevise();
        sql = sql + " WHERE ";
        return this;
    }

    public <T> SqlExpressionCreator equal(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " = " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlExpressionCreator fieldEqual(String field1, String field2) {
        sql += " " + field1 + " = " + field2 + " ";
        return this;
    }

    public SqlExpressionCreator equal(String field, SqlBuildResult sqlBuildResult) {
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

    public SqlExpressionCreator isNull(String field) {
        sql = sql + field + " IS NULL ";
        return this;
    }

    public SqlExpressionCreator distinct() {
        sql += " DISTINCT ";
        return this;
    }

    public SqlExpressionCreator isNotNull(String field) {
        sql = sql + field + " IS NOT NULL ";
        return this;
    }

    public SqlExpressionCreator in(String field, List params) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " IN ( " + ":" + p + " ) ";
        mapSqlParameterSource.addValue(p, params);
        return this;
    }

    public SqlExpressionCreator in(String field, SqlBuildResult sqlBuildResult) {
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

    public SqlExpressionCreator like(String field, String param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " LIKE " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlExpressionCreator like(String field, SqlBuildResult sqlBuildResult) {
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

    public SqlExpressionCreator concat(String... params) {
        StringBuilder sb = new StringBuilder();
        sql += " CONCAT( ";
        for (String param : params) {
            String p = PARAM_PREFIX + index++;
            sb.append(":").append(p).append(", ");
            mapSqlParameterSource.addValue(p, param);
        }
        sql += sb.toString();
        sqlRevise();
        sql += " ) ";
        return this;
    }

    public SqlExpressionCreator concat_ws(String separator, String... params) {
        StringBuilder sb = new StringBuilder();
        String s = PARAM_PREFIX + index++;
        sql += " CONCAT_WS( :" + s + ", ";
        mapSqlParameterSource.addValue(s, separator);
        for (String param : params) {
            String p = PARAM_PREFIX + index++;
            sb.append(":").append(p).append(", ");
            mapSqlParameterSource.addValue(p, param);
        }
        sql += sb.toString();
        sqlRevise();
        sql += " ) ";
        return this;
    }

    public SqlExpressionCreator left(String str, int len) {
        sql += " LEFT( " + str + ", " + len + " ) ";
        return this;
    }

    public SqlExpressionCreator orderBy(String field, String sortDirection) {
        sqlRevise();
        sql = sql + " ORDER BY " + field + " " + sortDirection + " ";
        return this;
    }

    public SqlExpressionCreator groupBy(String field) {
        sqlRevise();
        sql = sql + " GROUP BY " + field + " ";
        return this;
    }

    public SqlExpressionCreator having(SqlBuildResult expression) {
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

    public <T> SqlExpressionCreator betweenAnd(String field, T between, T and) {
        String p1 = PARAM_PREFIX + index++;
        String p2 = PARAM_PREFIX + index++;
        sql = sql + field + " BETWEEN " + ":" + p1 + " AND " + ":" + p2 + " ";
        mapSqlParameterSource.addValue(p1, between);
        mapSqlParameterSource.addValue(p2, and);
        return this;
    }

    public SqlExpressionCreator EXISTS(SqlBuildResult sqlBuildResult) {
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

    public SqlExpressionCreator any(SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql += " ANY(" + sqlBuildResult.getSql() + ") ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public SqlExpressionCreator some(SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql += " SOME(" + sqlBuildResult.getSql() + ") ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public SqlExpressionCreator all(SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql += " ALL(" + sqlBuildResult.getSql() + ") ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public <T> SqlExpressionCreator lt(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " < " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlExpressionCreator lt(String field, SqlBuildResult sqlBuildResult) {
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

    public <T> SqlExpressionCreator lte(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " <= " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlExpressionCreator lte(String field, SqlBuildResult sqlBuildResult) {
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

    public <T> SqlExpressionCreator gt(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " > " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlExpressionCreator gt(String field, SqlBuildResult sqlBuildResult) {
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

    public <T> SqlExpressionCreator gte(String field, T param) {
        String p = PARAM_PREFIX + index++;
        sql = sql + field + " >= " + ":" + p + " ";
        mapSqlParameterSource.addValue(p, param);
        return this;
    }

    public SqlExpressionCreator gte(String field, SqlBuildResult sqlBuildResult) {
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

    public SqlExpressionCreator sum(String field) {
        sql = sql + " SUM( " + field + " ), ";
        return this;
    }

    public SqlExpressionCreator limit(Integer num) {
        sql += " LIMIT " + num + " ";
        return this;
    }

    public SqlExpressionCreator sum(SqlBuildResult expression) {
        if (Objects.isNull(expression)) {
            return this;
        }
        if (StringUtils.isNotEmpty(expression.getSql())) {
            sql = sql + " SUM( " + expression.getSql() + " ), ";
        }
        if (Objects.nonNull(expression.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(expression.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public SqlExpressionCreator count(String field) {
        sql = sql + " COUNT( " + field + " ), ";
        return this;
    }

    public SqlExpressionCreator count(SqlBuildResult expression) {
        if (Objects.isNull(expression)) {
            return this;
        }
        if (StringUtils.isNotEmpty(expression.getSql())) {
            sql = sql + " COUNT( " + expression.getSql() + " ), ";
        }
        if (Objects.nonNull(expression.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(expression.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public SqlExpressionCreator as(String name) {
        sqlRevise();
        sql = sql + " AS " + name + ", ";
        return this;
    }

    public SqlExpressionCreator customize(String customize) {
        sql += " " + customize + " ";
        return this;
    }

    public SqlExpressionCreator customize(SqlBuildResult sqlBuildResult) {
        if (Objects.isNull(sqlBuildResult)) {
            return this;
        }
        if (StringUtils.isNotEmpty(sqlBuildResult.getSql())) {
            sql += " " + sqlBuildResult.getSql() + " ";
        }
        if (Objects.nonNull(sqlBuildResult.getMapSqlParameterSource())) {
            mapSqlParameterSource.addValues(sqlBuildResult.getMapSqlParameterSource().getValues());
        }
        return this;
    }

    public SqlExpressionCreator max(String field) {
        sql += " MAX( " + field + " ), ";
        return this;
    }

    public SqlExpressionCreator min(String field) {
        sql += " MIN( " + field + " ), ";
        return this;
    }

    public SqlExpressionCreator group_concat(String field) {
        sql += " GROUP_CONCAT( " + field + " ), ";
        return this;
    }


    public SqlExpressionCreator IF(SqlBuildResult expression, Object trueValue, Object falseValue) {
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
    public SqlExpressionCreator leftJoin(String tableName) {
        sql += " LEFT JOIN " + tableName + " ";
        return this;
    }

    public SqlExpressionCreator rightJoin(String tableName) {
        sql += " RIGHT JOIN " + tableName + " ";
        return this;
    }

    public SqlExpressionCreator innerJoin(String tableName) {
        sql += " INNER JOIN " + tableName + " ";
        return this;
    }

    public SqlExpressionCreator on() {
        sql += " ON ";
        return this;
    }

    public SqlExpressionCreator priority() {
        sql = " ( " + sql + " ) ";
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
        sqlRevise();
        SqlBuildResult sqlBuildResult = new SqlBuildResult(mapSqlParameterSource, sql.trim().replaceAll("\\s+", " "));
        sqlType = 0;
        sql = "";
        return sqlBuildResult;
    }
}
