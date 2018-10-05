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

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

@SuppressWarnings("PMD.TooManyMethods")
public class RequestParametersTest {
  private static final String KEY = "key";
  private static final String VALUE = "value";

  @Test
  public void equalsContract() {
    EqualsVerifier
        .forClass(RequestParameters.class)
        .verify();
  }

  @Test
  public void shouldSetParameter() {
    RequestParameters params = RequestParameters.init().set(KEY, VALUE);
    assertThat(toMap(params), hasEntry(KEY, Collections.singletonList(VALUE)));
  }

  @Test
  public void shouldNotSetParametersValueCollectionIsNull() {
    RequestParameters params = RequestParameters.init().set(KEY, null);
    assertThat(toMap(params), not(hasKey(KEY)));
  }

  @Test
  public void shouldNotSetParametersValueIsNull() {
    RequestParameters params = RequestParameters.init().set(KEY, (Object) null);
    assertThat(toMap(params), not(hasKey(KEY)));
  }

  @Test
  public void shouldSetAllParametersFromOtherInstance() {
    RequestParameters parent = RequestParameters.init().set(KEY, VALUE);
    RequestParameters params = RequestParameters.init().setAll(parent);

    assertThat(toMap(params), hasEntry(KEY, Collections.singletonList(VALUE)));
  }

  @Test
  public void shouldHandleNullValueWhenTryToSetAllParametersFromOtherInstance() {
    RequestParameters params = RequestParameters.init().setAll(null);

    assertThat(toMap(params).entrySet(), hasSize(0));
  }

  private Map<String, List<String>> toMap(RequestParameters parameters) {
    Map<String, List<String>> map = Maps.newHashMap();
    parameters.forEach(e -> map.put(e.getKey(), e.getValue()));

    return map;
  }
}
