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
    basePackages = "com.dentist.repository.local",
    entityManagerFactoryRef = "localEntityManagerFactory",
    transactionManagerRef = "localTransactionManager"
)
public class LocalDatabaseConfig {

    @Bean(name = "localEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean localEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("localDataSource") DataSource localDataSource) {
        return builder
                .dataSource(localDataSource)
                .packages("com.dentist.beans")
                .persistenceUnit("local")
                .build();
    }

    @Bean(name = "localTransactionManager")
    PlatformTransactionManager localTransactionManager(
            @Qualifier("localEntityManagerFactory") EntityManagerFactory localEntityManagerFactory) {
        return new JpaTransactionManager(localEntityManagerFactory);
    }

    @Bean(name = "localEntityManager")
    EntityManager localEntityManager(
        @Qualifier("localEntityManagerFactory") EntityManagerFactory localEntityManagerFactory) {
    return localEntityManagerFactory.createEntityManager();
}
}