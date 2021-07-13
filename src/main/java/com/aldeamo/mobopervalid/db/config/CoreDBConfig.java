package com.aldeamo.mobopervalid.db.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "coreEntityManagerFactory", transactionManagerRef = "coreTransactionManager", basePackages = {
		"com.aldeamo.mobopervalid.repository.core" })
@EntityScan("com.aldeamo.mobopervalid.entity.core.*")
public class CoreDBConfig {

	@Primary
	@Bean(name = "coreDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "coreEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("coreDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.aldeamo.mobopervalid.entity.core").persistenceUnit("corePU").build();
	}

	@Primary
	@Bean(name = "coreTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("coreEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

}
