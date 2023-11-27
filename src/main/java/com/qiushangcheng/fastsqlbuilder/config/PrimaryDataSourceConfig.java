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
@EnableJpaRepositories(basePackages = {"com.qiushangcheng.fastsqlbuilder.demo.repository.primary"})
public class PrimaryDataSourceConfig {
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private static final String PRIMARY_UNIT_NAME = "persistenceUnit";
    private static final String ENTITY_PACKAGE_PATH = "com.qiushangcheng.fastsqlbuilder.demo.repository.entity";
    private static final String PHYSICAL_NAMING_STRATEGY = "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy";

    @Primary
    @Bean("dataSource")
    DataSource createPrimaryDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        log.info("create primary dataSource finish");
        return hikariDataSource;
    }

    @Primary
    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean createPrimaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean em = builder
                .dataSource(createPrimaryDataSource())
                .packages(ENTITY_PACKAGE_PATH)
                .build();
        em.setPersistenceUnitName(PRIMARY_UNIT_NAME);
        Properties properties = new Properties();
        properties.setProperty("hibernate.physical_naming_strategy", PHYSICAL_NAMING_STRATEGY);
        em.setJpaProperties(properties);
        log.info("create primary entityManagerFactory finish");
        return em;
    }

    @Primary
    @Bean("transactionManager")
    public PlatformTransactionManager createPrimaryTransactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(createPrimaryEntityManagerFactory(builder).getObject());
        log.info("create primary transactionManager finish");
        return jpaTransactionManager;
    }

    @Primary
    @Bean("entityManager")
    public EntityManager createPrimaryEntityManager(EntityManagerFactoryBuilder builder) {
        EntityManager entityManager = createPrimaryEntityManagerFactory(builder).getObject().createEntityManager();
        log.info("create primary entityManager finish");
        return entityManager;
    }

    @Primary
    @Bean("namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate createPrimaryNamedParameterJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        log.info("create primary namedParameterJdbcTemplate finish");
        return namedParameterJdbcTemplate;
    }
}
