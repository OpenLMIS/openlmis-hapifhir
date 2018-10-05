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

import static org.openlmis.hapifhir.service.RequestHelper.createEntity;
import static org.openlmis.hapifhir.service.RequestHelper.createUri;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public abstract class BaseCommunicationService {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private AuthService authService;

  private RestOperations restTemplate = new RestTemplate();

  protected abstract String getServiceUrl();

  protected abstract String getUrl();

  protected <P> ResponseEntity<P> execute(String resourceUrl, RequestParameters parameters,
                                          RequestHeaders headers, Object payload,
                                          HttpMethod method, Class<P> type) {
    String url = getServiceUrl() + getUrl() + resourceUrl;
    URI uri = createUri(url, parameters);
    HttpEntity<Object> entity = createEntity(payload, addAuthHeader(headers));
    return restTemplate.exchange(uri, method, entity, type);
  }

  protected <P> ResponseEntity<P> execute(String resourceUrl, RequestParameters parameters,
                                          RequestHeaders headers, Object payload,
                                          HttpMethod method, ParameterizedTypeReference<P> type) {
    String url = getServiceUrl() + getUrl() + resourceUrl;
    URI uri = createUri(url, parameters);
    HttpEntity<Object> entity = createEntity(payload, addAuthHeader(headers));
    return restTemplate.exchange(uri, method, entity, type);
  }

  private RequestHeaders addAuthHeader(RequestHeaders headers) {
    return RequestHeaders
        .init()
        .setAll(headers)
        .setAuth(authService.obtainAccessToken());
  }
}
