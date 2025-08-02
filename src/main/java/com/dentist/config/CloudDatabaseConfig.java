package com.dentist.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.dentist.repository.cloud",
    entityManagerFactoryRef = "cloudEntityManagerFactory",
    transactionManagerRef = "cloudTransactionManager"
)
public class CloudDatabaseConfig {

    @Bean(name = "cloudEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean cloudEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("cloudDataSource") DataSource cloudDataSource) {
        return builder
                .dataSource(cloudDataSource)
                .packages("com.dentist.beans")
                .persistenceUnit("cloud")
                .build();
    }

    @Bean(name = "cloudTransactionManager")
    PlatformTransactionManager cloudTransactionManager(
            @Qualifier("cloudEntityManagerFactory") EntityManagerFactory cloudEntityManagerFactory) {
        return new JpaTransactionManager(cloudEntityManagerFactory);
    }

    @Bean(name = "cloudEntityManager")
    EntityManager cloudEntityManager(
        @Qualifier("cloudEntityManagerFactory") EntityManagerFactory cloudEntityManagerFactory) {
    return cloudEntityManagerFactory.createEntityManager();
}
}