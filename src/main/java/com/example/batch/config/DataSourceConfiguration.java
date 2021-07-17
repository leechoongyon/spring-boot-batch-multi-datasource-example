package com.example.batch.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfiguration {
    private static final String MAIN_PROPERTIES = "spring.datasource.main.hikari";
    private static final String SUB_PROPERTIES = "spring.datasource.sub.hikari";

    public static final String MAIN_DATASOURCE = "mainDataSource";
    public static final String SUB_DATASOURCE = "subDataSource";

    @Bean
    @ConfigurationProperties(prefix = MAIN_PROPERTIES)
    public HikariConfig mainHikariConfig() {
        return new HikariConfig();
    }


    /**
     * LazyConnectionDataSourceProxy 로 wrapping 한 이유는 스프링의 경우 트랜잭션 시작 시,
     * datasource 의 connection 를 사용하건 안하건 커넥션을 확보합니다.
     * 그로 인해 불필요한 리소스가 발생하게되고, 이를 줄이기 위해 LazyConnectionDataSourceProxy 로
     * 감쌀 경우 실제 커넥션이 필요한 경우에만 datasource 에서 connection 을 반환합니다.
     * 즉, Multi DataSource 에서 해당 설정을 넣어주면 좋습니다. single datasource 에서는
     * 미리 가져오면 거의 왠만하면 쓰겠지만 2개 이상의 datasource 의 경우는 안쓰는 datasource 가
     * 있을 수 있기에 이 설정을 넣어주면 좋습니다.
     * @return
     */
    @Primary // batch job repository datasource 는 primary 설정 datasource 로 설정됨.
    @Bean(MAIN_DATASOURCE)
    public DataSource mainDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(mainHikariConfig()));
    }

    @Bean
    @ConfigurationProperties(prefix = SUB_PROPERTIES)
    public HikariConfig subHikariConfig() {
        return new HikariConfig();
    }

    @Bean(SUB_DATASOURCE)
    public DataSource subDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(subHikariConfig()));
    }
}