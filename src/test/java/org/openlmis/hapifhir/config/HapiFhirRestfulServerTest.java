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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.context.WebApplicationContext;

@RunWith(MockitoJUnitRunner.class)
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
}