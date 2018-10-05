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

import java.lang.reflect.Type;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;

/**
 * Extension of {@link ParameterizedTypeReference} from Spring that allows dynamically changing
 * the type it represents at runtime. Since generic hacks are generally ugly, so is this class.
 * It eases the usage of the rest template however, allowing easily retrieving
 * {@link Map} objects with the provided generic type at runtime.
 */
public final class DynamicMapTypeReference<K, V> extends BaseParameterizedTypeReference<Map<K, V>> {

  /**
   * Constructs an instance that will represents {@link Map} wrappers for the
   * given type.
   *
   * @param valueType the value type (generic type) of the {@link Map} type that
   *                  this will represent
   */
  public DynamicMapTypeReference(Class<?> keyType, Class<?> valueType) {
    super(keyType, valueType);
  }

  @Override
  protected Type getBaseType() {
    return Map.class;
  }

}
