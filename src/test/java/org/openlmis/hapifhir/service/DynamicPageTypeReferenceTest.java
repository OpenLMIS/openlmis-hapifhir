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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DynamicPageTypeReferenceTest extends ToStringContractTest<DynamicPageTypeReference> {

  private static final DynamicPageTypeReference<String> REFERENCE
      = new DynamicPageTypeReference<>(String.class);

  @Test
  public void shouldGetType() {
    Type result = REFERENCE.getType();

    assertThat(result instanceof ParameterizedType, is(true));

    ParameterizedType parameterizedType = (ParameterizedType) result;

    Assertions.assertThat(parameterizedType.getActualTypeArguments())
        .containsExactly(String.class);
    assertEquals(parameterizedType.getRawType(), PageDto.class);
    assertNull(parameterizedType.getOwnerType());
  }

  @Test
  public void shouldGetBaseType() {
    Assertions.assertThat(REFERENCE.getBaseType()).isEqualTo(PageDto.class);
  }

  @Override
  protected Class<DynamicPageTypeReference> getTestClass() {
    return DynamicPageTypeReference.class;
  }

  @Override
  protected Optional<DynamicPageTypeReference> getInstance() {
    return Optional.of(new DynamicPageTypeReference<>(String.class));
  }

  @Override
  protected void prepare(EqualsVerifier<DynamicPageTypeReference> verifier) {
    verifier.withIgnoredFields("type");
  }

}
