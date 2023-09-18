package com.qiushangcheng.fastsqlbuilder.core;

import lombok.Data;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */

@Data
public class SqlBuildResult {
    private String sql;
    private MapSqlParameterSource mapSqlParameterSource;

    public SqlBuildResult(MapSqlParameterSource mapSqlParameterSource, String sql) {
        this.mapSqlParameterSource = mapSqlParameterSource;
        this.sql = sql;
    }
}
