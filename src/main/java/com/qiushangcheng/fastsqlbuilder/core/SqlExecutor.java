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
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int update(SqlBuildResult sqlBuildResult) {
        return namedParameterJdbcTemplate.update(sqlBuildResult.getSql(), sqlBuildResult.getMapSqlParameterSource());
    }

    public <T> List<T> query(SqlBuildResult sqlBuildResult, Class<T> target) {
        return namedParameterJdbcTemplate.query(sqlBuildResult.getSql(), sqlBuildResult.getMapSqlParameterSource(), new BeanPropertyRowMapper<>(target));
    }

    public <T> T queryForObject(SqlBuildResult sqlBuildResult, Class<T> target) {
        return DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(sqlBuildResult.getSql(), sqlBuildResult.getMapSqlParameterSource(), new BeanPropertyRowMapper<>(target)));
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return this.namedParameterJdbcTemplate;
    }
}
