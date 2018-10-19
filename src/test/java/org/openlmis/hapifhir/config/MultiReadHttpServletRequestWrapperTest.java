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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;

public class MultiReadHttpServletRequestWrapperTest {

  private static final String DEFAULT_BODY = "<test></test>";
  
  private HttpServletRequest request;

  private MultiReadHttpServletRequestWrapper wrapper;
  
  @Before
  public void setUp() throws IOException {
    request = mock(HttpServletRequest.class);
    when(request.getMethod()).thenReturn("POST");
    when(request.getInputStream()).thenReturn(new StringServletInputStream(DEFAULT_BODY));
    wrapper = new MultiReadHttpServletRequestWrapper(request);
  }
  
  @Test
  public void constructorShouldBuildBodyString() {
    assertEquals(DEFAULT_BODY, wrapper.getBody());
  }
}
