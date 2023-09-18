package com.qiushangcheng.fastsqlbuilder.util;

import com.qiushangcheng.fastsqlbuilder.pathclass.SqlBuilderPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */
@Slf4j
public class ReflectionUtil {
    /**
     * 通过反射获取数据库相关字段
     *
     * @param target
     * @return
     */
    public static <T> List<String> getFields(String tableName, Class<T> target) {
        if (Objects.isNull(target)) {
            return new ArrayList<>();
        }

        Field[] fields = target.getDeclaredFields();
        ArrayList<String> fieldList = new ArrayList<>();
        for (Field field : fields) {
            String columnName;
            try {
                columnName = field.getAnnotation(Column.class).name();
            } catch (Exception e) {
                columnName = field.getName();
                columnName = HumpAndUnderlineConvertUtil.humpToUnderline(columnName);
            }
            fieldList.add(StringUtils.equals(tableName, "") ? columnName : tableName + "." + columnName);
        }

        return fieldList;
    }

    /**
     * 通过反射获取数据库表名
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> String getTableName(Class<T> target) {
        if (Objects.isNull(target)) {
            return "";
        }

        String tableName = "";
        try {
            tableName = target.getAnnotation(Table.class).name();
        } catch (Exception e) {
            try {
                tableName = target.getAnnotation(SqlBuilderPath.class).tableName();
            } catch (Exception ex) {
                return tableName;
            }
        }

        return tableName;
    }

    /**
     * 通过反射获取数据库表主键
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> List<String> getPriKey(String tableName, Class<T> target) {
        if (Objects.isNull(target)) {
            return new ArrayList<>();
        }

        Field[] fields = target.getDeclaredFields();
        ArrayList<String> fieldList = new ArrayList<>();
        for (Field field : fields) {
            String columnName;
            try {
                if (field.isAnnotationPresent(Id.class)) {
                    columnName = field.getName();
                    columnName = HumpAndUnderlineConvertUtil.humpToUnderline(columnName);
                    fieldList.add(StringUtils.equals(tableName, "") ? columnName : tableName + "." + columnName);
                }
            } catch (Exception e) {
                log.error("SqlBuildUtil: get priKey failed, e={}", e.getMessage());
            }
        }

        return fieldList;
    }

    /**
     * 获取传入类的字段及值
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> getParamMap(String tableName, T target) {
        if (Objects.isNull(target)) {
            return new HashMap<>();
        }

        HashMap<String, Object> map = new HashMap<>();
        try {
            Class<?> clazz = target.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String columnName;
                try {
                    columnName = field.getAnnotation(Column.class).name();
                } catch (Exception e) {
                    columnName = field.getName();
                    columnName = HumpAndUnderlineConvertUtil.humpToUnderline(columnName);
                }
                map.put(StringUtils.equals(tableName, "") ? columnName : tableName + "." + columnName, field.get(target));
            }
            return map;
        } catch (Exception e) {
            log.warn("SqlBuildUtil: get {} fields and params error!!!", target);
            return null;
        }
    }

    /**
     * 获取path类的字段信息
     *
     * @param className
     * @return
     */
    public static HashSet<String> getPathFields(String className, String packageName) {
        HashSet<String> set = new HashSet<>();
        try {
            Class<?> clazz = Class.forName(packageName + "." + className + "Path");
            Object o = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                set.add(field.getName());
                set.add((String) field.get(o));
            }
            return set;
        } catch (Exception e) {
            log.warn("SqlBuildUtil: get {} fields error!!!", packageName + className + "Path");
            return null;
        }
    }
}
