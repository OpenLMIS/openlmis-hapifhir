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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public final class RequestHeaders {
  private Map<String, String> headers = Maps.newHashMap();

  private RequestHeaders() {
  }

  public static RequestHeaders init() {
    return new RequestHeaders();
  }

  public RequestHeaders setAuth(String token) {
    return isNotBlank(token) ? set(HttpHeaders.AUTHORIZATION, "Bearer " + token) : this;
  }

  public RequestHeaders setJsonAsContentType() {
    return set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  }

  /**
   * Set parameter (key argument) with the value only if the value is not null.
   */
  public RequestHeaders set(String key, String value) {
    if (isNotBlank(value)) {
      headers.put(key, value);
    }

    return this;
  }

  /**
   * Copy parameters from the existing {@link RequestHeaders}. If null value has been passed,
   * the method will return non changed instance.
   */
  public RequestHeaders setAll(RequestHeaders headers) {
    if (null != headers) {
      headers.forEach(entry -> set(entry.getKey(), entry.getValue()));
    }

    return this;
  }

  /**
   * Converts this instance to {@link HttpHeaders}.
   */
  public HttpHeaders toHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    forEach((entry -> httpHeaders.set(entry.getKey(), entry.getValue())));
    return httpHeaders;
  }

  void forEach(Consumer<Map.Entry<String, String>> action) {
    headers.entrySet().forEach(action);
  }

}
