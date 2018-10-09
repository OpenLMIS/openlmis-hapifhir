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

import com.google.common.net.HttpHeaders;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.http.HttpEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public final class RequestHelper {

  private RequestHelper() {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a {@link URI} from the given string representation without any parameters.
   */
  public static URI createUri(String url) {
    return createUri(url, null);
  }

  /**
   * Creates a {@link URI} from the given string representation and with the given parameters.
   */
  public static URI createUri(String url, RequestParameters parameters) {
    UriComponentsBuilder builder = UriComponentsBuilder.newInstance().uri(URI.create(url));

    RequestParameters
        .init()
        .setAll(parameters)
        .forEach(entry -> addQueryParam(entry, builder));

    return builder.build(true).toUri();
  }

  private static void addQueryParam(Entry<String, List<String>> entry,
      UriComponentsBuilder builder) {
    entry
        .getValue()
        .stream()
        .map(value -> UriUtils.encodeQueryParam(String.valueOf(value), StandardCharsets.UTF_8))
        .map(value -> new ImmutablePair<>(entry.getKey(), value))
        .forEach(pair -> builder.queryParam(pair.left, pair.right));
  }

  /**
   * Creates an {@link HttpEntity} with the given payload as a body and headers.
   */
  public static <E> HttpEntity<E> createEntity(E payload, RequestHeaders headers) {
    return new HttpEntity<>(payload, headers.toHeaders());
  }

  /**
   * Gets Client's IP address from request.
   */
  public static String getClientIpAddress(HttpServletRequest request) {
    String clientIpAddress = request.getHeader(HttpHeaders.X_FORWARDED_FOR);

    if (StringUtils.isBlank(clientIpAddress)) {
      clientIpAddress = request.getRemoteAddr();
    }

    return clientIpAddress;
  }

}
