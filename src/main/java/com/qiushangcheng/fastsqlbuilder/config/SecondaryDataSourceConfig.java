package com.qiushangcheng.fastsqlbuilder.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @auther QiuShangcheng
 * @create 2023/11/27
 */

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.qiushangcheng.fastsqlbuilder.demo.repository.secondary"})
public class SecondaryDataSourceConfig {
    @Value("${spring.secondary-datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.secondary-datasource.url}")
    private String url;

    @Value("${spring.secondary-datasource.username}")
    private String username;

    @Value("${spring.secondary-datasource.password}")
    private String password;

    private static final String PRIMARY_UNIT_NAME = "secondaryPersistenceUnit";
    private static final String ENTITY_PACKAGE_PATH = "com.qiushangcheng.fastsqlbuilder.demo.repository.entity";
    private static final String PHYSICAL_NAMING_STRATEGY = "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy";

    @Bean("secondaryDataSource")
    DataSource createSecondaryDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        log.info("create secondary dataSource finish");
        return hikariDataSource;
    }

    @Bean("secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean createSecondaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean em = builder
                .dataSource(createSecondaryDataSource())
                .packages(ENTITY_PACKAGE_PATH)
                .build();
        em.setPersistenceUnitName(PRIMARY_UNIT_NAME);
        Properties properties = new Properties();
        properties.setProperty("hibernate.physical_naming_strategy", PHYSICAL_NAMING_STRATEGY);
        em.setJpaProperties(properties);
        log.info("create secondary entityManagerFactory finish");
        return em;
    }

    @Bean("secondaryTransactionManager")
    public PlatformTransactionManager createSecondaryTransactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(createSecondaryEntityManagerFactory(builder).getObject());
        log.info("create secondary transactionManager finish");
        return jpaTransactionManager;
    }

    @Bean("secondaryEntityManager")
    public EntityManager createecondaryEntityManager(EntityManagerFactoryBuilder builder) {
        EntityManager entityManager = createSecondaryEntityManagerFactory(builder).getObject().createEntityManager();
        log.info("create secondary entityManager finish");
        return entityManager;
    }

    @Bean("secondaryNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate createSecondaryNamedParameterJdbcTemplate(@Qualifier("secondaryDataSource") DataSource dataSource) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        log.info("create secondary namedParameterJdbcTemplate finish");
        return namedParameterJdbcTemplate;
    }
}
