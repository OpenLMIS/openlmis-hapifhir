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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.openlmis.hapifhir.service.referencedata.FacilityDto;
import org.openlmis.hapifhir.service.referencedata.FacilityOperatorDto;
import org.openlmis.hapifhir.service.referencedata.FacilityTypeDto;
import org.openlmis.hapifhir.service.referencedata.GeographicZoneDto;

@SuppressWarnings("PMD.TooManyMethods")
public class FacilityDtoDataBuilder {

  private static int instanceNumber = 0;

  private UUID id;
  private String code;
  private String name;
  private String description;
  private GeographicZoneDto geographicZone;
  private FacilityTypeDto type;
  private FacilityOperatorDto operator;
  private Boolean active;
  private Boolean enabled;
  private Point location;
  private Map<String, String> extraData;

  /**
   * Returns instance of {@link FacilityDtoDataBuilder} with sample data.
   */
  public FacilityDtoDataBuilder() {
    instanceNumber++;

    id = UUID.randomUUID();
    code = "F" + instanceNumber;
    name = "Facility #" + instanceNumber;
    description = "Test facility";
    geographicZone = new GeographicZoneDtoDataBuilder().build();
    type = new FacilityTypeDtoDataBuilder().build();
    operator = null;
    active = true;
    enabled = true;
    location = new GeometryFactory().createPoint(new Coordinate(54.5, 18.5));
    extraData = new HashMap<>();
  }

  /**
   * Builds instance of {@link FacilityDto} without id.
   */
  public FacilityDto buildAsNew() {
    return new FacilityDto(
        code, name, description, active, enabled, geographicZone, operator, type, location,
        extraData);
  }

  /**
   * Builds instance of {@link FacilityDto}.
   */
  public FacilityDto build() {
    FacilityDto facility = buildAsNew();
    facility.setId(id);

    return facility;
  }

  public FacilityDtoDataBuilder withLocation(Point location) {
    this.location = location;
    return this;
  }

  public FacilityDtoDataBuilder withActive(Boolean active) {
    this.active = active;
    return this;
  }

  public FacilityDtoDataBuilder withEnabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  public FacilityDtoDataBuilder withId(UUID id) {
    this.id = id;
    return this;
  }
}
