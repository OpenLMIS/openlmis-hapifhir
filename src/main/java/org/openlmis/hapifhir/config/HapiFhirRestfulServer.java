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
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Meta;
import org.openlmis.util.Version;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.WebApplicationContext;

@Component
public class HapiFhirRestfulServer extends RestfulServer {

  private final XLogger logger = XLoggerFactory.getXLogger(getClass());

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

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request = new MultiReadHttpServletRequestWrapper(request);
    String uri = request.getRequestURI();

    boolean isTransactionRequest = false;
    if ("POST".equalsIgnoreCase(request.getMethod())) {
      try {
        String reqBody = ((MultiReadHttpServletRequestWrapper) request).getBody();
        logger.debug("Request body = {}", reqBody);
        Bundle bundle = FhirContext.forDstu3().newXmlParser().parseResource(Bundle.class, reqBody);
        BundleType bundleType = bundle.getType();
        isTransactionRequest = bundleType == BundleType.TRANSACTION;
      } catch (DataFormatException dfe) {
        logger.debug("POST request body was not a Bundle, ignoring, exception = {}", dfe);
      }
    }

    if ("/hapifhir".equalsIgnoreCase(uri)) {
      // workaround for the problem with retrieving information about the service version
      response.setStatus(HttpStatus.SC_OK);
      response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

      ObjectMapper mapper = myAppCtx.getBean(ObjectMapper.class);
      mapper.writeValue(response.getWriter(), new Version());
    } else if (isTransactionRequest) {
      // if request is already a transaction, we should not run it in a transaction, as HAPI FHIR
      // does not allow running a transaction request in a transaction
      super.service(request, response);
    } else {
      // workaround for the problem which has been described in the following link:
      // https://groups.google.com/forum/#!topic/hapi-fhir/Hm2I3UPACCw
      TransactionDefinition definition = new DefaultTransactionDefinition();
      PlatformTransactionManager manager = myAppCtx.getBean(PlatformTransactionManager.class);
      TransactionStatus transaction = manager.getTransaction(definition);

      try {
        super.service(request, response);

        if (!transaction.isCompleted() && !transaction.isRollbackOnly()) {
          manager.commit(transaction);
        }
      } catch (Exception exp) {
        if (!transaction.isCompleted()) {
          manager.rollback(transaction);
        }

        throw exp;
      }
    }
  }
  
  private class MultiReadHttpServletRequestWrapper extends HttpServletRequestWrapper {
    
    private final String body;
    
    MultiReadHttpServletRequestWrapper(HttpServletRequest request) {
      super(request);

      StringBuilder stringBuilder = new StringBuilder();
      BufferedReader bufferedReader = null;

      try (InputStream inputStream = request.getInputStream()) {
        if (inputStream != null) {
          bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

          char[] charBuffer = new char[128];
          int bytesRead;

          while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
            stringBuilder.append(charBuffer, 0, bytesRead);
          }
        }
      } catch (IOException ex) {
        logger.error("Error reading the request body, exception = {}", ex);
      } finally {
        if (bufferedReader != null) {
          try {
            bufferedReader.close();
          } catch (IOException ex) {
            logger.error("Error closing bufferedReader, exception = {}", ex);
          }
        }
      }

      body = stringBuilder.toString();  
    }
    
    @Override
    public ServletInputStream getInputStream() {
      return new ServletInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes());

        @Override
        public boolean isFinished() {
          return bais.available() == 0;
        }

        @Override
        public boolean isReady() {
          return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
          throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public int read() {
          return bais.read();
        }
      };
    }
    
    @Override
    public BufferedReader getReader() {
      return new BufferedReader(new StringReader(body));
    }
    
    public String getBody() {
      return body;
    }
  }
}
