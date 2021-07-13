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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "portabilityCoreEntityManagerFactory", transactionManagerRef = "portabilityCoreTransactionManager", basePackages = {
		"com.aldeamo.mobopervalid.repository.portability" })
@EntityScan("com.aldeamo.mobopervalid.entity.portability.*")
public class PortabilityCoreDBConfig {

	@Bean(name = "portabilityCoreDataSource")
	@ConfigurationProperties(prefix = "spring.portability-core-datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "portabilityCoreEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("portabilityCoreDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.aldeamo.mobopervalid.entity.portability").persistenceUnit("portabilityPU").build();
	}

	@Bean(name = "portabilityCoreTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("portabilityCoreEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

}
