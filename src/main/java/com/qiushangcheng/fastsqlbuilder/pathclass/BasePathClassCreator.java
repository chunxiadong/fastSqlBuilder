package com.qiushangcheng.fastsqlbuilder.pathclass;

import com.qiushangcheng.fastsqlbuilder.util.HumpAndUnderlineConvertUtil;
import com.qiushangcheng.fastsqlbuilder.util.ReflectionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: fastSqlBuilder
 * @description: Path类生成器，获取被@Entity、@SqlBuilderPath注解的类，服务启动时自动创建或更新path类。
 * @author: QiuShangcheng
 * @create: 2023-08-12 13:02
 **/
@Slf4j
@Component
public class BasePathClassCreator {
    /**
     * 手动触发path类更新, 方法如下：
     * public static void main(String[] args) {
     * BasePathClassCreator basePathClassCreator = new BasePathClassCreator();
     * BasePathClassCreator.PathClassConfiguration pathClassConfiguration = BasePathClassCreator.PathClassConfiguration.getInstance(FastSqlBuilderConfig.entityPackage);
     * basePathClassCreator.setProperties(pathClassConfiguration, false);
     * }
     */

    @Resource
    private EnvironmentCheck environmentCheck;
    private PathClassConfiguration configuration;

    @Data
    public static class PathClassConfiguration {
        private String pathPackage; // path类的存放路径
        private String entityPackage; // 扫描数据库对应的Entity类的路径
        private String entityManagerPathPackage; // 扫描@SqlBuilderPath的路径
        private String moduleName = ""; // path类的存放路径所在子模块的模块名称

        public static PathClassConfiguration getInstance(String entityPackage) {
            return new PathClassConfiguration(entityPackage);
        }

        public PathClassConfiguration(String entityPackage) {
            this.entityPackage = entityPackage;
            this.pathPackage = entityPackage + ".path";
            this.entityManagerPathPackage = entityPackage;
        }
    }

    public void setProperties(PathClassConfiguration configuration, boolean manuallyTriggered) {
        this.configuration = configuration;
        String folder = System.getProperty("user.dir") + configuration.getModuleName() + "/src/main/java/" + configuration.getPathPackage().replace(".", "/") + "/";
        log.info("SqlBuildUtil: path class folder={}", folder);
        if (manuallyTriggered || environmentCheck.check()) {
            initPathClass();
        }
    }

    /**
     * 获取@Entity、@SqlBuilderPath注解的类，启动时创建对应的path类
     */
    private void initPathClass() {
        // 获取指定包下面存在@Entity注解的类
        Reflections reflections1 = new Reflections(configuration.getEntityPackage());
        Reflections reflections2 = new Reflections(configuration.getEntityManagerPathPackage());
        Set<Class<?>> typesAnnotatedWith = reflections1.getTypesAnnotatedWith(Entity.class);
        Set<Class<?>> typesAnnotatedWith2 = reflections2.getTypesAnnotatedWith(SqlBuilderPath.class);
        typesAnnotatedWith.addAll(typesAnnotatedWith2);

        // 创建每个entity类对应的path类
        typesAnnotatedWith.forEach(e -> {
            Field[] fields = e.getDeclaredFields();
            HashMap<String, String> map = new HashMap<>();
            HashSet<String> set = new HashSet<>();
            String tableName = "";
            try {
                tableName = e.getAnnotation(SqlBuilderPath.class).tableName();
            } catch (Exception ex) {
                try {
                    tableName = e.getAnnotation(Table.class).name();
                } catch (Exception exc) {
                    log.warn("SqlBuildUtil: {} don't exist @Table", e);
                }
            }
            for (Field field : fields) {
                String columnName;
                try {
                    columnName = field.getAnnotation(Column.class).name();
                } catch (Exception ex) {
                    columnName = field.getName();
                    columnName = HumpAndUnderlineConvertUtil.humpToUnderline(columnName);
                }
                map.put(field.getName(), StringUtils.equals(tableName, "") ? columnName : tableName + "." + columnName);
                set.add(field.getName());
                set.add(StringUtils.equals(tableName, "") ? columnName : tableName + "." + columnName);
            }
            map.put("tableName", tableName);
            set.add("tableName");
            set.add(tableName);

            createClass(map, e.getSimpleName(), set);
        });
    }

    /**
     * 创建path类
     *
     * @param map
     * @param className
     */
    private void createClass(HashMap<String, String> map, String className, HashSet<String> entitySet) {
        // 创建path类
        String folder = System.getProperty("user.dir") + configuration.getModuleName() + "/src/main/java/" + configuration.getPathPackage().replace(".", "/") + "/";
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = folder + className + "Path.java";
        File classFile = new File(fileName);
        // 判断是创建还是更新path类
        HashSet<String> pathSet = null;
        if (classFile.exists()) {
            pathSet = ReflectionUtil.getPathFields(className, configuration.getPathPackage());
        }
        try {
            FileWriter fileWriter = null;
            if (!classFile.exists()) {
                if (classFile.createNewFile()) {
                    try {
                        fileWriter = new FileWriter(classFile);
                        fileWriter.write(writeContent(map, className));
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (Exception e) {
                        log.warn("SqlBuildUtil: write path {} fail!!!", fileName);
                        if (fileWriter != null) {
                            fileWriter.close();
                        }
                    }
                } else {
                    log.warn("SqlBuildUtil: create file {} fail!!!", fileName);
                }
            } else if (!setEqual(entitySet, pathSet)) {
                try {
                    fileWriter = new FileWriter(classFile);
                    fileWriter.write(writeContent(map, className));
                    fileWriter.flush();
                    fileWriter.close();
                } catch (Exception e) {
                    log.warn("SqlBuildUtil: write path {} fail!!!", fileName);
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                }
            }
        } catch (IOException e) {
            log.warn("SqlBuildUtil: create file {} fail!!!", fileName);
        }
    }

    /**
     * 填充path类的数据库字段信息
     *
     * @param map
     * @param className
     * @return
     */
    private String writeContent(HashMap<String, String> map, String className) {
        StringBuffer classBuffer = new StringBuffer();
        classBuffer.append("package " + configuration.getPathPackage() + ";\n\n");
        classBuffer.append("/*" + " \n");
        classBuffer.append("* 说明：每个实体类拥有一个path类，对应实体类为：" + className + " \n");
        classBuffer.append("* 建议：未使用的path类不要提交到代码仓库" + " \n");
        classBuffer.append("* path类在服务启动时自动更新，基本不需手动维护。 若需手动更新，请参考BasePathClassCreator类的注释" + " \n");
        classBuffer.append("*/" + " \n\n");
        classBuffer.append("public class " + className + "Path" + " {\n");
        if (!CollectionUtils.isEmpty(map)) {
            map.forEach((k, v) -> {
                classBuffer.append("\tpublic static final String " + k + " = " + "\"" + v + "\";\n");
            });
        }
        classBuffer.append("}\n");
        return classBuffer.toString();
    }

    /**
     * 比较实体类的set与path类的set，确定实体类是否被修改，需要进行更新
     *
     * @param set1
     * @param set2
     * @return
     */
    private boolean setEqual(HashSet<String> set1, HashSet<String> set2) {
        if (set1 == null || set2 == null) {
            return false;
        }

        if (set1.size() != set2.size()) {
            return false;
        }

        return set1.containsAll(set2);
    }

}
