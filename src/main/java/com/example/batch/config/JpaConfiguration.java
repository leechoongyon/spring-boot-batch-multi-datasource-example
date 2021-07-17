package com.example.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
public class JpaConfiguration {
    private static final String MAIN_ENTITY_MANAGER_FACTORY = "mainEntityManagerFactory";
    private static final String SUB_ENTITY_MANAGER_FACTORY = "subEntityManagerFactory";
    private static final String MAIN_TRANSACTION_MANAGER = "mainTransactionManager";
    private static final String SUB_TRANSACTION_MANAGER = "subTransactionManager";
    public static final String CHAINED_TRANSACTION_MANAGER = "chainedTransactionManager";

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;
    private final ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders;
    private final EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    private final String MAIN_DOMAIN_PACKAGE = "com.example.batch.domain.main";
    private final String SUB_DOMAIN_PACKAGE = "com.example.batch.domain.sub";

    /**
     * spring data jpa 의존성이있으면 EntityManagerFactory 를 자동으로 빈으로 설정할 수 있음.
     * 단, @ConditionalOnMissingBean 이 걸려있기에 entityManagerFactory 로 등록된 bean 이 없을 때만
     * 빈으로 설정 됨.
     * @param dataSource
     * @return
     */
    @Primary
    @Bean(name = MAIN_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(
            @Qualifier(DataSourceConfiguration.MAIN_DATASOURCE) DataSource dataSource)  {
        return EntityManagerFactoryCreator.builder()
                .properties(jpaProperties)
                .hibernateProperties(hibernateProperties)
                .metadataProviders(metadataProviders)
                .entityManagerFactoryBuilder(entityManagerFactoryBuilder)
                .dataSource(dataSource)
                .packages(MAIN_DOMAIN_PACKAGE)
                .persistenceUnit("mainUnit")
                .build()
                .create();
    }

    @Bean(name = SUB_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean subEntityManagerFactory(
            @Qualifier(DataSourceConfiguration.SUB_DATASOURCE) DataSource dataSource)  {
        return EntityManagerFactoryCreator.builder()
                .properties(jpaProperties)
                .hibernateProperties(hibernateProperties)
                .metadataProviders(metadataProviders)
                .entityManagerFactoryBuilder(entityManagerFactoryBuilder)
                .dataSource(dataSource)
                .packages(SUB_DOMAIN_PACKAGE)
                .persistenceUnit("subUnit")
                .build()
                .create();
    }

    @Primary
    @Bean(name = MAIN_TRANSACTION_MANAGER)
    public PlatformTransactionManager mainTransactionManager(
            @Qualifier(MAIN_ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean(name = SUB_TRANSACTION_MANAGER)
    public PlatformTransactionManager subTransactionManager(
            @Qualifier(SUB_ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean(name = CHAINED_TRANSACTION_MANAGER)
    public PlatformTransactionManager chainedTransactionManager(
             @Qualifier(MAIN_TRANSACTION_MANAGER) PlatformTransactionManager mainTransactionManager
            ,@Qualifier(SUB_TRANSACTION_MANAGER) PlatformTransactionManager subTransactionManager) {
        return new ChainedTransactionManager(mainTransactionManager, subTransactionManager);
    }


    public static final String MAIN_REPOSITORY_PACKAGE = "com.example.batch.repository.main";
    public static final String SUB_REPOSITORY_PACKAGE = "com.example.batch.repository.sub";

    @Configuration
    @EnableJpaRepositories(
             basePackages = MAIN_REPOSITORY_PACKAGE
            ,entityManagerFactoryRef = JpaConfiguration.MAIN_ENTITY_MANAGER_FACTORY
            ,transactionManagerRef = JpaConfiguration.MAIN_TRANSACTION_MANAGER
    )
    public static class MainJpaRepositoriesConfig{}

    @Configuration
    @EnableJpaRepositories(
            basePackages = SUB_REPOSITORY_PACKAGE
            ,entityManagerFactoryRef = JpaConfiguration.SUB_ENTITY_MANAGER_FACTORY
            ,transactionManagerRef = JpaConfiguration.SUB_TRANSACTION_MANAGER
    )
    public static class SubJpaRepositoriesConfig{}

}