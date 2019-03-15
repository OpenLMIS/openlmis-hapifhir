/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2018 VillageReach
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

import static org.apache.commons.lang3.BooleanUtils.isTrue;

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import com.vividsolutions.jts.geom.Point;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Location.LocationPositionComponent;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.LocationPhysicalType;
import org.openlmis.hapifhir.service.referencedata.FacilityDto;
import org.openlmis.hapifhir.service.referencedata.FacilityReferenceDataService;
import org.openlmis.hapifhir.service.referencedata.GeographicZoneDto;
import org.openlmis.hapifhir.service.referencedata.GeographicZoneReferenceDataService;
import org.openlmis.hapifhir.service.referencedata.ReferenceDataVersionService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocationLoadingService {

  private final XLogger logger = XLoggerFactory.getXLogger(getClass());
  private static final String KEY_URI_IDENTIFIER = "urn:ietf:rfc:3986";

  @Value("${service.url}")
  private String serviceUrl;

  @Autowired
  private ReferenceDataVersionService referenceDataVersionService;
  
  @Autowired
  private GeographicZoneReferenceDataService geographicZoneService;

  @Autowired
  private FacilityReferenceDataService facilityService;

  @Autowired
  @Qualifier("myLocationDaoR4")
  private IFhirResourceDao<Location> locationDao;

  /**
   * Wait for Reference Data service to be available.
   */
  public void waitForReferenceData() throws InterruptedException {
    //wait until reference data returns its version info
    while (true) {
      VersionDto version = null;
      try {
        version = referenceDataVersionService.getInfo();
      } catch (Exception exc) {
        logger.info("Reference data version not found, trying again, exception = " + exc);
      }
      if (null != version) {
        logger.info("Reference data version found, load FHIR demo data");
        break;
      }
      Thread.sleep(5000);
    }
  }

  /**
   * Load geographic zones into FHIR datastore.
   */
  @Transactional
  public void loadGeographicZones() {

    logger.info("Get geographic zones");
    List<GeographicZoneDto> geographicZones = geographicZoneService
        .getPage("", RequestParameters.init())
        .getContent();

    //Need to sort by level, so that parents are added first for the partOf reference for later adds
    logger.info("Sort geographic zones");
    geographicZones.sort(Comparator.comparing(obj -> obj.getLevel().getLevelNumber()));

    logger.info("Save geographic zones");
    geographicZones.forEach(
        geographicZone -> locationDao.update(buildLocationFrom(geographicZone)));
  }

  /**
   * Load facilities into FHIR datastore.
   */
  @Transactional
  public void loadFacilities() {
    logger.info("Get facilities");
    List<FacilityDto> facilities = facilityService
        .getPage("", RequestParameters.init())
        .getContent();

    logger.info("Save facilities");
    facilities.forEach(facility -> locationDao.update(buildLocationFrom(facility)));
  }
  
  private Location buildLocationFrom(GeographicZoneDto geographicZone) {
    Location location = new Location();
    location.setId(geographicZone.getId().toString());
    location.addAlias(geographicZone.getCode());
    location.setName(geographicZone.getName());
    location.addIdentifier()
        .setSystem(KEY_URI_IDENTIFIER)
        .setValue(serviceUrl + "/api/geographicLevels/" + geographicZone.getLevel().getId());
    location.addIdentifier()
        .setSystem(serviceUrl)
        .setValue(geographicZone.getId().toString());
    if (null != geographicZone.getLongitude() && null != geographicZone.getLatitude()) {
      LocationPositionComponent position = new LocationPositionComponent(
          new DecimalType(geographicZone.getLongitude()),
          new DecimalType(geographicZone.getLatitude()));
      location.setPosition(position);
    }
    CodeableConcept area = new CodeableConcept();
    area.addCoding(new Coding(LocationPhysicalType.AREA.getSystem(),
        LocationPhysicalType.AREA.toCode(), LocationPhysicalType.AREA.getDisplay()));
    location.setPhysicalType(area);

    Optional
        .ofNullable(geographicZone.getParent())
        .ifPresent(parent ->
            location.setPartOf(new Reference("Location/" + parent.getId())));
    
    return location;
  }
  
  private Location buildLocationFrom(FacilityDto facility) {
    Location location = new Location();
    location.setId(facility.getId().toString());
    location.addAlias(facility.getCode());
    location.setName(facility.getName());
    location.setDescription(facility.getDescription());
    location.addIdentifier()
        .setSystem(KEY_URI_IDENTIFIER)
        .setValue(serviceUrl + "/api/facilityTypes/" + facility.getType().getId());
    location.addIdentifier()
        .setSystem(serviceUrl)
        .setValue(facility.getId().toString());
    Point facilityLocation = facility.getLocation();
    if (null != facilityLocation) {
      LocationPositionComponent position = new LocationPositionComponent(
          new DecimalType(facilityLocation.getX()),
          new DecimalType(facilityLocation.getY()));
      location.setPosition(position);
    }
    CodeableConcept site = new CodeableConcept();
    site.addCoding(new Coding(LocationPhysicalType.SI.getSystem(),
        LocationPhysicalType.SI.toCode(), LocationPhysicalType.SI.getDisplay()));
    location.setPhysicalType(site);

    Optional
        .ofNullable(facility.getGeographicZone())
        .ifPresent(geographicZone ->
            location.setPartOf(new Reference("Location/" + geographicZone.getId())));

    location.setStatus(isTrue(facility.getActive()) && isTrue(facility.getEnabled())
        ? LocationStatus.ACTIVE : LocationStatus.INACTIVE);

    return location;
  }
}
