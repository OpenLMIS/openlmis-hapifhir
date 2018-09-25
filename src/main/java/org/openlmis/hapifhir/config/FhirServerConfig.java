/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.hapifhir.config;

import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.util.SubscriptionsRequireManualActivationInterceptorDstu3;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:application.properties")
public class FhirServerConfig extends BaseJavaConfigDstu3 {

  @Value("${spring.datasource.url}")
  private String databaseUrl;

  @Value("${spring.datasource.username}")
  private String databaseUserName;

  @Value("${spring.datasource.password}")
  private String databasePassword;


  /**
   * A bean that configures Dao.
   * @return
   */
  @Bean()
  public DaoConfig daoConfig() {
    DaoConfig retVal = new DaoConfig();
    retVal.setAllowExternalReferences(true);
    retVal.setAllowMultipleDelete(true);
    return retVal;
  }

  /**
   * Data Source for HAPI FHIR.
   * This is required because with default auto configuration, HAPI FHIR assumes a derby database.
   *
   * @return DataSource
   */
  @Bean(destroyMethod = "close")
  public DataSource dataSource() {
    BasicDataSource retVal = new BasicDataSource();
    retVal.setDriver(new org.postgresql.Driver());
    retVal.setUrl(databaseUrl);
    retVal.setUsername(databaseUserName);
    retVal.setPassword(databasePassword);
    return retVal;
  }

  /**
   * Create a bean for a custom entity manager factory.
   *
   * @return LocalContainerEntityManagerFactoryBean
   */
  @Bean()
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
    retVal.setPersistenceUnitName("default");
    retVal.setDataSource(dataSource());
    retVal.setPackagesToScan("ca.uhn.fhir");
    retVal.setPersistenceProvider(new HibernatePersistenceProvider());
    retVal.setJpaPropertyMap(jpaProperties().getProperties());
    return retVal;
  }


  @Bean
  @ConfigurationProperties("spring.jpa.properties")
  public JpaProperties jpaProperties() {
    return new JpaProperties();
  }


  /**
   * Demo Logging interceptor.
   *
   * @return LoggingInterceptor
   */
  @Bean
  public IServerInterceptor loggingInterceptor() {
    LoggingInterceptor retVal = new LoggingInterceptor();
    retVal.setLoggerName("hapi.fhir.access");
    retVal.setMessageFormat(
        "Path[${servletPath}] Source[${requestHeader.x-forwarded-for}] "
            + "Operation[${operationType} ${operationName} ${idOrResourceName}] "
            + "UA[${requestHeader.user-agent}] Params[${requestParameters}] "
            + "ResponseEncoding[${responseEncodingNoDefault}]");
    retVal.setLogExceptions(true);
    retVal.setErrorMessageFormat("ERROR - ${requestVerb} ${requestUrl}");
    return retVal;
  }

  /**
   * Demo subscription security interceptor.
   * @return
   */
  @Bean(autowire = Autowire.BY_TYPE)
  public IServerInterceptor subscriptionSecurityInterceptor() {
    return new SubscriptionsRequireManualActivationInterceptorDstu3();
  }

  /**
   * Configure transaction manager.
   *
   * @param entityManagerFactory entityManagerFactory
   * @return JpaTransactionManager
   */
  @Bean()
  public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager retVal = new JpaTransactionManager();
    retVal.setEntityManagerFactory(entityManagerFactory);
    return retVal;
  }

  /**
   * Configures FHIR servlet.
   */
  @Bean
  public ServletRegistrationBean servletRegistrationBean(HapiFhirRestfulServer server) {
    ServletRegistrationBean bean = new ServletRegistrationBean();
    bean.setServlet(server);
    bean.addUrlMappings("/hapifhir/*");
    bean.setLoadOnStartup(1);

    return bean;
  }

}
