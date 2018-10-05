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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class DynamicMapTypeReferenceTest extends ToStringContractTest<DynamicMapTypeReference> {

  private static final DynamicMapTypeReference<String, String> REFERENCE
      = new DynamicMapTypeReference<>(String.class, String.class);

  @Test
  public void shouldGetType() {
    Type result = REFERENCE.getType();

    assertThat(result instanceof ParameterizedType).isTrue();

    ParameterizedType parameterizedType = (ParameterizedType) result;

    assertThat(parameterizedType.getActualTypeArguments())
        .containsExactly(String.class, String.class);
    assertThat(parameterizedType.getRawType()).isEqualTo(Map.class);
    assertThat(parameterizedType.getOwnerType()).isNull();
  }

  @Test
  public void shouldGetBaseType() {
    assertThat(REFERENCE.getBaseType()).isEqualTo(Map.class);
  }

  @Override
  protected Class<DynamicMapTypeReference> getTestClass() {
    return DynamicMapTypeReference.class;
  }

  @Override
  protected Optional<DynamicMapTypeReference> getInstance() {
    return Optional.of(new DynamicMapTypeReference<>(String.class, String.class));
  }

  @Override
  protected void prepare(EqualsVerifier<DynamicMapTypeReference> verifier) {
    verifier.withIgnoredFields("type");
  }

}
