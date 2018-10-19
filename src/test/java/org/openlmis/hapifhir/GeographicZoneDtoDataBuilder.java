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

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Polygon;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomUtils;
import org.openlmis.hapifhir.service.referencedata.GeographicLevelDto;
import org.openlmis.hapifhir.service.referencedata.GeographicZoneDto;

public class GeographicZoneDtoDataBuilder {

  private static int instanceNumber = 0;

  private UUID id;
  private String code;
  private String name;
  private GeographicLevelDto level;
  private GeographicZoneDto parent;
  private Integer catchmentPopulation;
  private Double latitude;
  private Double longitude;
  private Polygon boundary;
  private Map<String, String> extraData;

  /**
   * Returns instance of {@link GeographicZoneDtoDataBuilder} with sample data.
   */
  public GeographicZoneDtoDataBuilder() {
    instanceNumber++;

    id = UUID.randomUUID();
    code = "GZ" + instanceNumber;
    name = "Geographic Zone #" + instanceNumber;
    level = new GeographicLevelDtoDataBuilder().build();
    catchmentPopulation = RandomUtils.nextInt(0, 1000);
    latitude = RandomUtils.nextDouble(0, 200) - 100;
    longitude = RandomUtils.nextDouble(0, 200) - 100;
    extraData = Maps.newHashMap();
  }

  /**
   * Builds instance of {@link GeographicZoneDto} without id.
   */
  public GeographicZoneDto buildAsNew() {
    return new GeographicZoneDto(code, name, level, catchmentPopulation, latitude, longitude,
        parent, boundary, extraData);
  }

  /**
   * Builds instance of {@link GeographicZoneDto}.
   */
  public GeographicZoneDto build() {
    GeographicZoneDto zone = buildAsNew();
    zone.setId(id);

    return zone;
  }

  public GeographicZoneDtoDataBuilder withLevel(GeographicLevelDto level) {
    this.level = level;
    return this;
  }

  public GeographicZoneDtoDataBuilder withParent(GeographicZoneDto parent) {
    this.parent = parent;
    return this;
  }
  
  public GeographicZoneDtoDataBuilder withLongitude(Double longitude) {
    this.longitude = longitude;
    return this;
  }

  public GeographicZoneDtoDataBuilder withLatitude(Double latitude) {
    this.latitude = latitude;
    return this;
  }

  public GeographicZoneDtoDataBuilder withId(UUID id) {
    this.id = id;
    return this;
  }
}
