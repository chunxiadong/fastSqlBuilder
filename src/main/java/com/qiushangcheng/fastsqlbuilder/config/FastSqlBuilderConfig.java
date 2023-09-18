package com.qiushangcheng.fastsqlbuilder.config;

import com.qiushangcheng.fastsqlbuilder.pathclass.BasePathClassCreator;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */

@Configuration
public class FastSqlBuilderConfig {
    @Resource
    private BasePathClassCreator basePathClassCreator;

    // 扫描数据库对应的Entity类的路径
    public static final String entityPackage = "com.qiushangcheng.fastsqlbuilder.demo.repository.entity";

    // 用于存放path类的module的名称，没有module则不配置
//    public static final String moduleName = "/module名称";
    // path类的存放路径，不配置默认为 pathPackage = entityPackage + ".path"
//    public static final String pathPackage = "com.qiushangcheng.fastsqlbuilder.path";
    // 扫描@SqlBuilderPath的路径, 不配置默认为 entityManagerPathPackage = entityPackage
//    public static final String entityManagerPathPackage = "com.qiushangcheng.fastsqlbuilder.demo.repository.entity";

    @PostConstruct
    public void init() {
        BasePathClassCreator.PathClassConfiguration pathClassConfiguration = new BasePathClassCreator.PathClassConfiguration(entityPackage);
        basePathClassCreator.setProperties(pathClassConfiguration);
        basePathClassCreator.refresh(false);

        // 若使用mycat且区分租户进行配置，其他情况下不配置该参数
//        SqlBuilder.tenantId = "system_id";
        // 一条插入语句插入记录数量的最大限制，不配置则使用默认值（1000）
//        SqlBuilder.maxInsertNum = 2000;
    }
}
