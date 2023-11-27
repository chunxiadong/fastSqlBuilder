package com.qiushangcheng.fastsqlbuilder.config;

import com.qiushangcheng.fastsqlbuilder.pathclass.BasePathClassCreator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */

@Slf4j
@Aspect
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
        BasePathClassCreator.PathClassConfiguration pathClassConfiguration = BasePathClassCreator.PathClassConfiguration.getInstance(entityPackage);
//        pathClassConfiguration.setPathPackage();
//        pathClassConfiguration.setEntityManagerPathPackage();
//        pathClassConfiguration.setModuleName();
        basePathClassCreator.setProperties(pathClassConfiguration, false);
        // 一条插入语句插入记录数量的最大限制，不配置则使用默认值（1000）
//        SqlBuilder.maxInsertNum = 2000;
    }

    @Around("execution(* com.qiushangcheng.fastsqlbuilder.demo.repository..*.*(..)))")
    private Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String sql = joinPoint.getSignature().toShortString();
        String type = "normalSql";
        String result = "success";
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            result = "failed";
            throw e;
        } finally {
            stopWatch.stop();
            long costTime = stopWatch.getTime();
            if (costTime > 500L) {
                type = "slowSql";
            }
            log.info("SqlMonitor report: sql={}, result={}, type={}, costTime={}ms", sql, result, type, costTime);
        }
    }
}
