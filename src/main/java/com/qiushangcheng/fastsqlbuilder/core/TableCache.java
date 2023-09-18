package com.qiushangcheng.fastsqlbuilder.core;

import com.qiushangcheng.fastsqlbuilder.core.SqlBuilder;
import com.qiushangcheng.fastsqlbuilder.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */
@Slf4j
public class TableCache {
    private static final Map<Class, SqlBuilder.TableInfo> tableCache = new HashMap<>();
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final Lock readLock = readWriteLock.readLock();
    private static final Lock writeLock = readWriteLock.writeLock();

    public <T> SqlBuilder.TableInfo get(Class<T> key) {
        SqlBuilder.TableInfo tableInfo = null;
        readLock.lock();
        try {
            tableInfo = tableCache.get(key);
        } finally {
            readLock.unlock();
        }
        if (tableInfo != null) {
            return tableInfo;
        }
        writeLock.lock();
        try {
            tableInfo = tableCache.get(key);
            if (tableInfo == null) {
                String tableName = ReflectionUtil.getTableName(key);
                List<String> fieldList = ReflectionUtil.getFields(tableName, key);
                tableInfo = new SqlBuilder.TableInfo(tableName, fieldList);
                tableCache.put(key, tableInfo);
                log.info("SqlBuildUtil: Init Db TableInfo, TableInfo={}", tableInfo);
            }
        } finally {
            writeLock.unlock();
        }
        return tableInfo;
    }
}
