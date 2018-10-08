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

package org.openlmis.hapifhir;

import java.util.UUID;
import org.openlmis.hapifhir.service.referencedata.GeographicLevelDto;

public class GeographicLevelDtoDataBuilder {

  private static int instanceNumber = 0;

  private UUID id;
  private String code;
  private String name;
  private Integer levelNumber;

  /**
   * Returns instance of {@link GeographicLevelDtoDataBuilder} with sample data.
   */
  public GeographicLevelDtoDataBuilder() {
    instanceNumber++;

    id = UUID.randomUUID();
    code = "GL" + instanceNumber;
    name = "Geographic Level" + 1;
    levelNumber = 1;
  }

  /**
   * Builds instance of {@link GeographicLevelDto} without id.
   */
  public GeographicLevelDto buildAsNew() {
    return new GeographicLevelDto(code, name, levelNumber);
  }

  /**
   * Builds instance of {@link GeographicLevelDto}.
   */
  public GeographicLevelDto build() {
    GeographicLevelDto level = buildAsNew();
    level.setId(id);

    return level;
  }

  public GeographicLevelDtoDataBuilder withLevelNumber(Integer levelNumber) {
    this.levelNumber = levelNumber;
    return this;
  }
}
