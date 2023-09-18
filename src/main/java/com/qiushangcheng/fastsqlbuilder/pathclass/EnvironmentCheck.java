package com.qiushangcheng.fastsqlbuilder.pathclass;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * @auther QiuShangcheng
 * @create 2023/8/12
 */

@Slf4j
@Component
public class EnvironmentCheck implements ApplicationContextAware {

    @Value("${fastSqlBuilder.path.enable-auto-update:false}")
    private Boolean autoUpdate;
    private static final String PROFILE_DEV = "dev";
    private static final String PROFILE_PROD = "prod";
    private static final String PROFILE_UNKNOWN = "unknown";

    /**
     * spring的应用上下文
     */
    @Getter
    private static ApplicationContext applicationContext;

    /**
     * 初始化时将应用上下文设置进applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EnvironmentCheck.applicationContext = applicationContext;
    }

    /**
     * 获取spring.profiles.active
     */
    public static String getProfile() {
        String[] activeProfiles = Optional.ofNullable(getApplicationContext())
                .map(EnvironmentCapable::getEnvironment)
                .map(Environment::getActiveProfiles)
                .orElse(null);
        return Objects.isNull(activeProfiles) || activeProfiles.length == 0 ? PROFILE_UNKNOWN : activeProfiles[0];
    }

    public boolean check() {
        try {
            String profile = getProfile();
            log.info("SqlBuildUtil: environment={}", profile);
            if (PROFILE_PROD.equals(profile)) {
                return false;
            }
            if (PROFILE_DEV.equals(profile) || PROFILE_UNKNOWN.equals(profile)) {
                return autoUpdate;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
