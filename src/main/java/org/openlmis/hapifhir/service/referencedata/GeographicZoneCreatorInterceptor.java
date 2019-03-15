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

import static org.apache.commons.lang.StringUtils.isBlank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Location.LocationPositionComponent;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.LocationPhysicalType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.openlmis.hapifhir.i18n.Message;
import org.openlmis.hapifhir.i18n.MessageKeys;
import org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptor;
import org.openlmis.hapifhir.service.ResourceCommunicationService;
import org.openlmis.hapifhir.service.ValidationMessageException;
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
  protected GeographicZoneDto buildResource(Location location) {
    GeographicZoneDto geographicZone = findGeographicZone(
        location.getIdElement().getIdPart(), false);

    if (null == geographicZone) {
      geographicZone = new GeographicZoneDto();
      geographicZone.setId(UUID.fromString(location.getIdElement().getIdPart()));
    }

    // optional
    Optional
        .ofNullable(location.getName())
        .ifPresent(geographicZone::setName);
    Optional
        .ofNullable(location.getPosition())
        .map(LocationPositionComponent::getLatitude)
        .filter(Objects::nonNull)
        .map(BigDecimal::doubleValue)
        .ifPresent(geographicZone::setLatitude);
    Optional
        .ofNullable(location.getPosition())
        .map(LocationPositionComponent::getLongitude)
        .filter(Objects::nonNull)
        .map(BigDecimal::doubleValue)
        .ifPresent(geographicZone::setLongitude);
    Optional
        .ofNullable(location.getPartOf())
        .map(Reference::getReferenceElement)
        .map(IIdType::getIdPart)
        .filter(Objects::nonNull)
        .map(id -> findGeographicZone(id, true))
        .ifPresent(geographicZone::setParent);

    // mandatory
    geographicZone.setCode(
        Optional
            .ofNullable(location.getAlias())
            .map(list -> list.isEmpty() ? null : list.get(0))
            .map(PrimitiveType::getValue)
            .orElseThrow(() -> new ValidationMessageException(
                new Message(MessageKeys.ERROR_GEO_ZONE_CODE_REQUIRED))));
    geographicZone.setLevel(findGeographicLevel(location, geographicZone.getParent()));

    return geographicZone;
  }

  @Override
  protected ResourceCommunicationService<GeographicZoneDto> getCommunicationService() {
    return geographicZoneReferenceDataService;
  }

  private GeographicLevelDto findGeographicLevel(Location location, GeographicZoneDto parent) {
    List<GeographicLevelDto> levels = geographicLevelReferenceDataService.findAll();
    int currentLevel;

    if (isBlank(location.getPartOf().getReference())) {
      currentLevel = largestLevelNumber;
    } else {
      int parentLevel = parent.getLevel().getLevelNumber();
      currentLevel = largestLevelNumber == 1 ? parentLevel + 1 : parentLevel - 1;
    }

    return levels
        .stream()
        .filter(level -> level.getLevelNumber() == currentLevel)
        .findFirst()
        .orElseThrow(() -> new ValidationMessageException(
            new Message(MessageKeys.ERROR_NOT_FOUND_GEO_LEVEL, currentLevel)));
  }

  private GeographicZoneDto findGeographicZone(String id, boolean required) {
    UUID idAsUuid = UUID.fromString(id);
    GeographicZoneDto found = geographicZoneReferenceDataService.findOne(idAsUuid);

    if (required && null == found) {
      throw new ValidationMessageException(
          new Message(MessageKeys.ERROR_NOT_FOUND_GEO_ZONE, idAsUuid));
    }

    return found;
  }

}
