package com.qiushangcheng.fastsqlbuilder.core;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */
public class FastSqlBuilder {
    public static final String DISTINCT = " DISTINCT ";
    public static final String ASC = " ASC ";
    public static final String DESC = " DESC ";
    public static final TableCache tableCache = new TableCache();

    public static <T> SqlBuilder getSqlBuilder(Class<T> target) {
        return new SqlBuilder(tableCache.get(target));
    }

    public static SqlExpressionCreator getSqlExpressionCreator() {
        return new SqlExpressionCreator();
    }
}
