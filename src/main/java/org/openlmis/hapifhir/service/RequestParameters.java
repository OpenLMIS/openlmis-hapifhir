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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.EqualsAndHashCode;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@EqualsAndHashCode
public final class RequestParameters {

  private final MultiValueMap<String, String> params;

  private RequestParameters() {
    params = new LinkedMultiValueMap<>();
  }

  public static RequestParameters init() {
    return new RequestParameters();
  }

  /**
   * Set parameter (key argument) with the value only if the value is not null.
   */
  public RequestParameters set(String key, Collection<?> valueCollection) {
    Optional
        .ofNullable(valueCollection)
        .orElse(Collections.emptyList())
        .forEach(elem -> set(key, elem));

    return this;
  }

  /**
   * Set parameter (key argument) with the value only if the value is not null.
   */
  public RequestParameters set(String key, Object value) {
    if (null != value) {
      params.add(key, String.valueOf(value));
    }

    return this;
  }

  /**
   * Copy parameters from the existing {@link RequestParameters}. If null value has been passed,
   * the method will return non changed instance.
   */
  public RequestParameters setAll(RequestParameters parameters) {
    if (null != parameters) {
      parameters.forEach(entry -> set(entry.getKey(), entry.getValue()));
    }

    return this;
  }

  public void forEach(Consumer<Map.Entry<String, List<String>>> action) {
    params.entrySet().forEach(action);
  }

}
