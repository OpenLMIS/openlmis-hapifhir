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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletException;
import org.hl7.fhir.dstu3.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
public class HapiFhirRestfulServer extends RestfulServer {
  private static final long serialVersionUID = 1L;

  private final String serviceUrl;
  private final WebApplicationContext myAppCtx;

  @Autowired
  public HapiFhirRestfulServer(@Value("${service.url}") String serviceUrl,
      WebApplicationContext myAppCtx) {
    this.serviceUrl = serviceUrl;
    this.myAppCtx = myAppCtx;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void initialize() throws ServletException {
    super.initialize();

    setFhirContext(FhirContext.forDstu3());

    List<IResourceProvider> beans = myAppCtx.getBean("myResourceProvidersDstu3", List.class);
    setResourceProviders(beans);

    setPlainProviders(myAppCtx.getBean("mySystemProviderDstu3", JpaSystemProviderDstu3.class));

    IFhirSystemDao<org.hl7.fhir.dstu3.model.Bundle, Meta> systemDao = myAppCtx.getBean(
        "mySystemDaoDstu3", IFhirSystemDao.class);
    JpaConformanceProviderDstu3 confProvider = new JpaConformanceProviderDstu3(this,
        systemDao, myAppCtx.getBean(DaoConfig.class));
    confProvider.setImplementationDescription("Example Server");
    setServerConformanceProvider(confProvider);

    setETagSupport(ETagSupportEnum.ENABLED);
    setDefaultPrettyPrint(true);
    setDefaultResponseEncoding(EncodingEnum.JSON);

    setPagingProvider(myAppCtx.getBean(DatabaseBackedPagingProvider.class));

    Collection<IServerInterceptor> interceptorBeans = myAppCtx.getBeansOfType(IServerInterceptor
        .class).values();
    for (IServerInterceptor interceptor : interceptorBeans) {
      this.registerInterceptor(interceptor);
    }

    setServerAddressStrategy(new HardcodedServerAddressStrategy(serviceUrl + "/hapifhir/"));

  }

}
