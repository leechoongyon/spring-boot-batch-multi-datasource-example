package com.example.batch.config;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EntityManagerFactoryCreator {
    private static final String PROVIDER_DISABLES_AUTOCOMMIT = "hibernate.connection.provider_disables_autocommit";

    private final JpaProperties properties;
    private final HibernateProperties hibernateProperties;
    private final ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders;
    private final EntityManagerFactoryBuilder entityManagerFactoryBuilder;
    private final DataSource dataSource;
    private final String packages;
    private final String persistenceUnit;

    @Builder
    public EntityManagerFactoryCreator(JpaProperties properties, HibernateProperties hibernateProperties, ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders, EntityManagerFactoryBuilder entityManagerFactoryBuilder, DataSource dataSource, String packages, String persistenceUnit) {
        this.properties = properties;
        this.hibernateProperties = hibernateProperties;
        this.metadataProviders = metadataProviders;
        this.entityManagerFactoryBuilder = entityManagerFactoryBuilder;
        this.dataSource = dataSource;
        this.packages = packages;
        this.persistenceUnit = persistenceUnit;
    }

    public LocalContainerEntityManagerFactoryBean create () {
        Map<String, Object> vendorProperties = getVendorProperties();
        customizeVendorProperties(vendorProperties);
        return entityManagerFactoryBuilder
                .dataSource(this.dataSource)
                .packages(packages)
                .properties(vendorProperties)
                .persistenceUnit(persistenceUnit)
                .mappingResources(getMappingResources())
                .build();
    }

    private String[] getMappingResources() {
        List<String> mappingResources = this.properties.getMappingResources();
        return (!ObjectUtils.isEmpty(mappingResources) ? StringUtils.toStringArray(mappingResources) : null);
    }

    private Map<String, Object> getVendorProperties() {
        return new LinkedHashMap<>(this.hibernateProperties.determineHibernateProperties(
                this.properties.getProperties(),
                new HibernateSettings()));
    }

    private void customizeVendorProperties(Map<String, Object> vendorProperties) {
        if (!vendorProperties.containsKey(PROVIDER_DISABLES_AUTOCOMMIT)) {
            configureProviderDisablesAutocommit(vendorProperties);
        }
    }

    /**
     * dataSource 의 AutoCommit 이 false 여야 hibernate.connection.provider_disables_autocommit 옵션을 킬 수 있다.
     * hibernate.connection.provider_disables_autocommit 옵션을 키면 setAutoCommit, getAutoCommit 을 수행안하기에
     * 성능 향상을 기대할 수 있다. setAutoCommit 의 경우 트랜잭션 begin 과 동일하며, 매번 수행할 때마다 connection 을 가져오게 돼있음.
     *
     * 만약, hibernate.connection.provider_disables_autocommit 옵션을 true 했는데 hikari 의 autoCommit true 이면
     * autoCommit 으로 처리돼기에 조심해야 함.
     *
     * application.yml 에서 hikari 의 autoCommit 을 false 로 하면 아래 isDataSource AutoCommitDisabled 에서
     * true 를 리턴해 hibernate.connection.provider_disables_autocommit 옵션이 적용됨.
     *
     * @param vendorProperties
     */
    private void configureProviderDisablesAutocommit(Map<String, Object> vendorProperties) {
        if (isDataSourceAutoCommitDisabled()) {
            log.info("Hikari auto-commit: false");
            vendorProperties.put(PROVIDER_DISABLES_AUTOCOMMIT, "true");
        }
    }

    private boolean isDataSourceAutoCommitDisabled() {
        DataSourcePoolMetadataProvider poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(metadataProviders.getIfAvailable());
        DataSourcePoolMetadata poolMetadata = poolMetadataProvider.getDataSourcePoolMetadata(this.dataSource);
        return poolMetadata != null && Boolean.FALSE.equals(poolMetadata.getDefaultAutoCommit());
    }
}
