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

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.XmlParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.context.WebApplicationContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FhirContext.class)
public class HapiFhirRestfulServerTest {

  private static final String SERVICE_URL = "http://localhost";

  @Mock
  private WebApplicationContext context;

  @Mock
  private PlatformTransactionManager transactionManager;

  @Mock
  private TransactionStatus transactionStatus;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  private HapiFhirRestfulServer server;

  @Before
  public void setUp() {
    server = spy(new HapiFhirRestfulServer(SERVICE_URL, context));

    when(context.getBean(PlatformTransactionManager.class)).thenReturn(transactionManager);
    when(transactionManager.getTransaction(any(TransactionDefinition.class)))
        .thenReturn(transactionStatus);
    when(request.getRequestURI()).thenReturn("/Location");
    when(request.getMethod()).thenReturn("");
  }

  @Test
  public void shouldCommitTransaction() throws ServletException, IOException {
    when(transactionStatus.isCompleted()).thenReturn(false);

    server.service(request, response);

    verify(transactionManager).commit(transactionStatus);
    verify(transactionManager, never()).rollback(transactionStatus);
  }

  @Test
  public void shouldNotCommitTransactionIfItIsCompleted() throws ServletException, IOException {
    when(transactionStatus.isCompleted()).thenReturn(true);

    server.service(request, response);

    verify(transactionManager, never()).commit(transactionStatus);
    verify(transactionManager, never()).rollback(transactionStatus);
  }

  @Test
  public void shouldNotCommitTransactionIfItItRollbackOnly() throws ServletException, IOException {
    when(transactionStatus.isRollbackOnly()).thenReturn(true);

    server.service(request, response);

    verify(transactionManager, never()).commit(transactionStatus);
    verify(transactionManager, never()).rollback(transactionStatus);
  }

  @Test
  public void shouldRollbackTransactionOnException() throws IOException {
    doThrow(new IOException()).when(response).sendError(anyInt(), anyString());
    when(transactionStatus.isCompleted()).thenReturn(false);

    try {
      server.service(request, response);
      fail("HAPI FHIR server should throw exception");
    } catch (ServletException | IOException e) {
      verify(transactionManager).rollback(transactionStatus);
    }

    verify(transactionManager, never()).commit(transactionStatus);
  }

  @Test
  public void shouldNotRollbackTransactionIfItIsCompleted() throws IOException {
    doThrow(new IOException()).when(response).sendError(anyInt(), anyString());
    when(transactionStatus.isCompleted()).thenReturn(true);

    try {
      server.service(request, response);
      fail("HAPI FHIR server should throw exception");
    } catch (ServletException | IOException e) {
      verify(transactionManager, never()).rollback(transactionStatus);
    }

    verify(transactionManager, never()).commit(transactionStatus);
  }
  
  @Test
  @Ignore
  public void shouldNotUseTransactionIfTransactionRequest() throws IOException, ServletException {
    //given
    String body = "<Bundle><type value='transaction'/></Bundle>";
    when(request.getMethod()).thenReturn("POST");
    when(request.getInputStream()).thenReturn(new ServletInputStream() {
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
    });
    mockStatic(FhirContext.class);
    FhirContext fhirCtx = mock(FhirContext.class);
    when(FhirContext.forDstu3()).thenReturn(fhirCtx);
    XmlParser xmlParser = mock(XmlParser.class);
    when(fhirCtx.newXmlParser()).thenReturn(xmlParser);
    Bundle bundle = mock(Bundle.class);
    when(xmlParser.parseResource(Bundle.class, body)).thenReturn(bundle);
    when(bundle.getType()).thenReturn(BundleType.TRANSACTION);
    
    //when
    server.service(request, response);
    
    //then
    verify(context, never()).getBean(PlatformTransactionManager.class);
    verify(transactionManager, never()).getTransaction(any(TransactionDefinition.class));
  }
}
