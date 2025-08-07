	package com.dentist.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.dentist.beans.Tooth;
import com.dentist.repository.local.LocalToothRepositoryJPA;

@Configuration
public class DataSourceConfig {

	@Bean
	EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaProperties jpaProperties) {
	    return new EntityManagerFactoryBuilder(
	        new HibernateJpaVendorAdapter(),
	        new HashMap<>(jpaProperties.getProperties()),
	        null
	    );
	}
	
    @Bean(name = "cloudDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.cloud")
    DataSource cloudDataSource() {
        return new DriverManagerDataSource();
    }

    @Bean(name = "localDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.local")
    DataSource localDataSource() {
        return new DriverManagerDataSource();
    }
    
}
