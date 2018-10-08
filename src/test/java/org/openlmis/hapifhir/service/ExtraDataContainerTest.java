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

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

public class ExtraDataContainerTest {

  private static final String KEY = "key";
  private static final String VALUE = "value";

  private TestContainer container = new TestContainer();

  @Test
  public void shouldAddExtraDataEntry() {
    container.setExtraData(Maps.newHashMap());
    container.addExtraDataEntry(KEY, VALUE);

    assertThat(container.getExtraData())
        .hasSize(1)
        .containsEntry(KEY, VALUE);
  }

  @Test
  public void shouldAddExtraDataEntryEvenIfContainerDoesNotHaveExtraDataSet() {
    container.setExtraData(null);
    container.addExtraDataEntry(KEY, VALUE);

    assertThat(container.getExtraData())
        .hasSize(1)
        .containsEntry(KEY, VALUE);
  }

  @Setter
  @Getter
  private static final class TestContainer implements ExtraDataContainer {
    private Map<String, String> extraData;
  }

}
