package com.qiushangcheng.fastsqlbuilder.core;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */
public class SqlExecutor {
    public int update(SqlBuildResult sqlBuildResult, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return namedParameterJdbcTemplate.update(sqlBuildResult.getSql(), sqlBuildResult.getMapSqlParameterSource());
    }

    public <T> List<T> query(SqlBuildResult sqlBuildResult, NamedParameterJdbcTemplate namedParameterJdbcTemplate, Class<T> target) {
        return namedParameterJdbcTemplate.query(sqlBuildResult.getSql(), sqlBuildResult.getMapSqlParameterSource(), new BeanPropertyRowMapper<>(target));
    }

    public <T> T queryForObject(SqlBuildResult sqlBuildResult, NamedParameterJdbcTemplate namedParameterJdbcTemplate, Class<T> target) {
        return DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(sqlBuildResult.getSql(), sqlBuildResult.getMapSqlParameterSource(), new BeanPropertyRowMapper<>(target)));
    }
}
