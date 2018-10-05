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

package org.openlmis.hapifhir.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

public class DataRetrievalExceptionTest {

  @Test
  public void shouldCreateInstanceBasedOnHttpStatusCodeException() {
    String resource = "Facility";
    HttpStatus status = HttpStatus.NOT_FOUND;
    String responseBody = "Not found";

    HttpStatusCodeException ex = new HttpClientErrorException(
        status, status.name(), responseBody.getBytes(), StandardCharsets.UTF_8);

    DataRetrievalException exception = DataRetrievalException.build(resource, ex);

    assertThat(exception.getResource()).isEqualTo(resource);
    assertThat(exception.getStatus()).isEqualTo(status);
    assertThat(exception.getResponse()).isEqualTo(responseBody);
  }
}
