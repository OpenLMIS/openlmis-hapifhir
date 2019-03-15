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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.LocationPhysicalType;
import org.openlmis.hapifhir.i18n.Message;
import org.openlmis.hapifhir.i18n.MessageKeys;
import org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptor;
import org.openlmis.hapifhir.service.ResourceCommunicationService;
import org.openlmis.hapifhir.service.ValidationMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FacilityCreatorInterceptor extends OpenLmisResourceCreatorInterceptor<FacilityDto> {

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  @Autowired
  private FacilityTypeReferenceDataService facilityTypeReferenceDataService;

  @Autowired
  private GeographicZoneReferenceDataService geographicZoneReferenceDataService;

  @Value("${facilityTypeId}")
  private UUID facilityTypeId;

  private GeometryFactory geometryFactory = new GeometryFactory();
  private FacilityTypeDto facilityType;

  @Override
  protected boolean supports(LocationPhysicalType type) {
    return LocationPhysicalType.SI == type;
  }

  @Override
  protected FacilityDto buildResource(Location location) {
    FacilityDto facility = findFacility(location.getIdElement().getIdPart());

    if (null == facility) {
      facility = new FacilityDto();
      facility.setId(UUID.fromString(location.getIdElement().getIdPart()));
    }

    // optional
    Optional
        .ofNullable(location.getName())
        .ifPresent(facility::setName);
    Optional
        .ofNullable(location.getDescription())
        .ifPresent(facility::setDescription);
    Optional
        .ofNullable(location.getPosition())
        .filter(position -> Objects.nonNull(position.getLongitude())
            && Objects.nonNull(position.getLatitude()))
        .map(position -> {
          double longitude = position.getLongitude().doubleValue();
          double latitude = position.getLatitude().doubleValue();

          Coordinate coordinate = new Coordinate(longitude, latitude);
          return geometryFactory.createPoint(coordinate);
        })
        .ifPresent(facility::setLocation);

    // mandatory
    facility.setCode(
        Optional
            .ofNullable(location.getAlias())
            .map(list -> list.isEmpty() ? null : list.get(0))
            .map(PrimitiveType::getValue)
            .orElseThrow(() -> new ValidationMessageException(
                new Message(MessageKeys.ERROR_FACILITY_CODE_REQUIRED))));
    facility.setGeographicZone(findGeographicZone(location.getPartOf()));
    facility.setType(getFacilityType());
    facility.setActive(location.getStatus() == LocationStatus.ACTIVE);
    facility.setEnabled(facility.getActive());

    return facility;
  }

  @Override
  protected ResourceCommunicationService<FacilityDto> getCommunicationService() {
    return facilityReferenceDataService;
  }

  private FacilityTypeDto getFacilityType() {
    if (null == facilityType) {
      facilityType = Optional
          .ofNullable(facilityTypeReferenceDataService.findOne(facilityTypeId))
          .orElseThrow(() -> new ValidationMessageException(
              new Message(MessageKeys.ERROR_NOT_FOUND_FACILITY_TYPE, facilityTypeId)));
    }

    return facilityType;
  }

  private FacilityDto findFacility(String id) {
    UUID idAsUuid = UUID.fromString(id);
    return facilityReferenceDataService.findOne(idAsUuid);
  }

  private GeographicZoneDto findGeographicZone(Reference reference) {
    UUID idAsUuid = UUID.fromString(reference.getReferenceElement().getIdPart());
    GeographicZoneDto found = geographicZoneReferenceDataService.findOne(idAsUuid);

    if (null == found) {
      throw new ValidationMessageException(
          new Message(MessageKeys.ERROR_NOT_FOUND_GEO_ZONE, idAsUuid));
    }

    return found;
  }
}
