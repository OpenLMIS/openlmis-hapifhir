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

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@SuppressWarnings("PMD.TooManyMethods")
public class RequestHelperTest {
  private static final String URL = "http://localhost";
  private static final String BEARER = "Bearer ";

  @Test
  public void shouldCreateUriWithoutParameters() {
    URI uri = RequestHelper.createUri(URL, RequestParameters.init());
    assertThat(uri.getQuery(), is(nullValue()));
  }

  @Test
  public void shouldCreateUriWithNullParameters() {
    URI uri = RequestHelper.createUri(URL);
    assertThat(uri.getQuery(), is(nullValue()));
  }

  @Test
  public void shouldCreateUriWithParameters() {
    URI uri = RequestHelper.createUri(URL, RequestParameters.init().set("a", "b"));
    assertThat(uri.getQuery(), is("a=b"));
  }

  @Test
  public void shouldCreateUriWithEncodedParameters() {
    URI uri = RequestHelper.createUri(URL, RequestParameters.init().set("a", "b c"));
    assertThat(uri.getQuery(), is("a=b c"));
    assertThat(uri.getRawQuery(), is("a=b%20c"));
  }

  @Test
  public void shouldCreateEntityWithAnAuthHeader() {
    String body = "test";
    String token = "token";

    HttpEntity<String> entity = RequestHelper.createEntity(body,
        RequestHeaders.init().setAuth(token));

    assertThat(entity.getHeaders().get(HttpHeaders.AUTHORIZATION),
        is(singletonList(BEARER + token)));
    assertThat(entity.getBody(), is(body));
  }

  @Test
  public void shouldCreateEntityWithNoBody() {
    String token = "token";

    HttpEntity<String> entity = RequestHelper.createEntity(null,
        RequestHeaders.init().setAuth(token));

    assertThat(entity.getHeaders().get(HttpHeaders.AUTHORIZATION),
        is(singletonList(BEARER + token)));
  }

}
