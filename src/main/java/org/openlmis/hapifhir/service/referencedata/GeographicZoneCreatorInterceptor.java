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

package org.openlmis.hapifhir.service.referencedata;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Location.LocationPositionComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.codesystems.LocationPhysicalType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptor;
import org.openlmis.hapifhir.service.ResourceCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeographicZoneCreatorInterceptor extends
    OpenLmisResourceCreatorInterceptor<GeographicZoneDto> {

  @Autowired
  private GeographicZoneReferenceDataService geographicZoneReferenceDataService;

  @Autowired
  private GeographicLevelReferenceDataService geographicLevelReferenceDataService;

  @Value("${largestLevelNumber}")
  private int largestLevelNumber;

  @Override
  protected boolean supports(LocationPhysicalType type) {
    return LocationPhysicalType.AREA == type;
  }

  @Override
  protected BuildResult buildResource(Location location) {
    GeographicZoneDto geographicZone = findGeographicZone(location.getIdElement().getIdPart());
    boolean created = false;

    if (null == geographicZone) {
      geographicZone = new GeographicZoneDto();
      geographicZone.setId(UUID.fromString(location.getIdElement().getIdPart()));

      created = true;
    }

    // optional
    Optional
        .ofNullable(location.getName())
        .ifPresent(geographicZone::setName);
    Optional
        .ofNullable(location.getPosition())
        .map(LocationPositionComponent::getLatitude)
        .map(BigDecimal::doubleValue)
        .ifPresent(geographicZone::setLatitude);
    Optional
        .ofNullable(location.getPosition())
        .map(LocationPositionComponent::getLongitude)
        .map(BigDecimal::doubleValue)
        .ifPresent(geographicZone::setLongitude);
    Optional
        .ofNullable(location.getPartOf())
        .map(Reference::getReferenceElement)
        .map(IIdType::getIdPart)
        .map(this::findGeographicZone)
        .ifPresent(geographicZone::setParent);

    // mandatory
    geographicZone.setCode(location.getAlias().get(0).getValueNotNull());
    geographicZone.setLevel(findGeographicLevel(location, geographicZone.getParent()));

    return new BuildResult(geographicZone, created);
  }

  @Override
  protected ResourceCommunicationService<GeographicZoneDto> getCommunicationService() {
    return geographicZoneReferenceDataService;
  }

  private GeographicLevelDto findGeographicLevel(Location location, GeographicZoneDto parent) {
    List<GeographicLevelDto> levels = geographicLevelReferenceDataService.findAll();
    int currentLevel;

    if (null == location.getPartOf()) {
      currentLevel = largestLevelNumber;
    } else {
      int parentLevel = null == location.getPartOf() ? -1 : parent.getLevel().getLevelNumber();
      currentLevel = largestLevelNumber == 1 ? parentLevel + 1 : parentLevel - 1;
    }

    return levels
        .stream()
        .filter(level -> level.getLevelNumber() == currentLevel)
        .findFirst()
        .orElse(null);
  }

  private GeographicZoneDto findGeographicZone(String id) {
    UUID idAsUuid = UUID.fromString(id);
    return geographicZoneReferenceDataService.findOne(idAsUuid);
  }

}
