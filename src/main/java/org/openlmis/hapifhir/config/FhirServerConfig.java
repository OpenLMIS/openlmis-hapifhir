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

import ca.uhn.fhir.jpa.config.BaseJavaConfigR4;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.DaoConfig.IdStrategyEnum;
import ca.uhn.fhir.jpa.model.entity.ModelConfig;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import org.hl7.fhir.dstu2.model.Subscription;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EntityScan({"ca.uhn.fhir.jpa.entity", "ca.uhn.fhir.jpa.model.entity"})
@PropertySource("classpath:application.properties")
public class FhirServerConfig extends BaseJavaConfigR4 {

  /**
   * A bean that configures Dao.
   */
  @Bean
  public DaoConfig daoConfig() {
    DaoConfig retVal = new DaoConfig();
    retVal.setAllowExternalReferences(true);
    retVal.setAllowMultipleDelete(true);
    retVal.setResourceServerIdStrategy(IdStrategyEnum.UUID);
    retVal.setEnableInMemorySubscriptionMatching(true);
    retVal.addSupportedSubscriptionType(Subscription.SubscriptionChannelType.RESTHOOK);

    return retVal;
  }

  @Bean
  public ModelConfig modelConfig() {
    return daoConfig().getModelConfig();
  }

  /**
   * Demo Logging interceptor.
   *
   * @return LoggingInterceptor
   */
  @Bean
  public LoggingInterceptor loggingInterceptor() {
    LoggingInterceptor retVal = new LoggingInterceptor();
    retVal.setLoggerName("hapi.fhir.access");
    retVal.setMessageFormat("Path[${servletPath}] Source[${requestHeader.x-forwarded-for}] "
        + "Operation[${operationType} ${operationName} ${idOrResourceName}] "
        + "UA[${requestHeader.user-agent}] Params[${requestParameters}] "
        + "ResponseEncoding[${responseEncodingNoDefault}]");
    retVal.setLogExceptions(true);
    retVal.setErrorMessageFormat("ERROR - ${requestVerb} ${requestUrl}");
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
