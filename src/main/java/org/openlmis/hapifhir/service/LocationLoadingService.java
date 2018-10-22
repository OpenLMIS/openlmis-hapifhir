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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Point;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Location.LocationPositionComponent;
import org.hl7.fhir.dstu3.model.Location.LocationStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.codesystems.LocationPhysicalType;
import org.openlmis.hapifhir.service.referencedata.FacilityDto;
import org.openlmis.hapifhir.service.referencedata.FacilityReferenceDataService;
import org.openlmis.hapifhir.service.referencedata.GeographicZoneDto;
import org.openlmis.hapifhir.service.referencedata.GeographicZoneReferenceDataService;
import org.openlmis.hapifhir.service.referencedata.ReferenceDataVersionService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocationLoadingService {

  private final XLogger logger = XLoggerFactory.getXLogger(getClass());
  private static final int CHUNK_SIZE = 500;
  static final int CLIENT_SOCKET_TIMEOUT = 30000;

  @Value("${service.url}")
  private String serviceUrl;

  @Autowired
  private ReferenceDataVersionService referenceDataVersionService;
  
  @Autowired
  private AuthService authService;
  
  @Autowired
  private GeographicZoneReferenceDataService geographicZoneService;

  @Autowired
  private FacilityReferenceDataService facilityService;

  /**
   * Initialize FHIR client.
   */
  public IGenericClient initialize() throws InterruptedException {
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

    logger.info("Initialize FHIR context");
    FhirContext ctx = FhirContext.forDstu3();
    //default socket timeout of 10s gets HTTP code 499
    ctx.getRestfulClientFactory().setSocketTimeout(CLIENT_SOCKET_TIMEOUT);

    logger.info("Create generic client");
    IGenericClient client = ctx.newRestfulGenericClient(serviceUrl + "/hapifhir/");

    // Need to generate service token for the REST call
    logger.info("Create bearer token interceptor and register it");
    BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(
        authService.obtainAccessToken());
    client.registerInterceptor(authInterceptor);
    
    return client;
  }

  /**
   * Load geographic zones into FHIR datastore.
   */
  public void loadGeographicZones(IGenericClient client) {

    logger.info("Get geographic zones");
    List<GeographicZoneDto> geographicZones = geographicZoneService.getPage("", 
        RequestParameters.init()).getContent();
    //Need to sort by level, so that parents are added first for the partOf reference for later adds
    logger.info("Sort geographic zones");
    geographicZones.sort(Comparator.comparing(obj -> obj.getLevel().getLevelNumber()));

    logger.info("Loop through geographic zone chunks");
    for (List<GeographicZoneDto> geoZoneChunk : Lists.partition(geographicZones, CHUNK_SIZE)) {

      logger.info("Convert geographic zone chunk to location chunk");
      List<Location> geoZoneLocations = geoZoneChunk.stream()
          .map(this::getLocationFor)
          .collect(Collectors.toList());

      logger.info("Save geographic zone location chunk in transaction");
      client.transaction()
          .withResources(geoZoneLocations)
          .execute();
    }
  }

  /**
   * Load facilities into FHIR datastore.
   */
  public void loadFacilities(IGenericClient client) {
    logger.info("Get facilities");
    List<FacilityDto> facilities = facilityService.findAll();

    logger.info("Loop through facility chunks");
    for (List<FacilityDto> facilityChunk : Lists.partition(facilities, CHUNK_SIZE)) {

      logger.info("Convert facility chunk to location chunk");
      List<Location> facilityLocations = facilityChunk.stream()
          .map(this::getLocationFor)
          .collect(Collectors.toList());

      logger.info("Save facility location chunk in transaction");
      client.transaction()
          .withResources(facilityLocations)
          .execute();
    }
  }
  
  private Location getLocationFor(GeographicZoneDto geographicZone) {
    Location location = new Location();
    location.setId(geographicZone.getId().toString());
    location.addAlias(geographicZone.getCode());
    location.setName(geographicZone.getName());
    location.addIdentifier()
        .setSystem("urn:ietf:rfc:3986")
        .setValue(serviceUrl + "/api/geographicLevels/" + geographicZone.getLevel().getId());
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
  
  private Location getLocationFor(FacilityDto facility) {
    Location location = new Location();
    location.setId(facility.getId().toString());
    location.addAlias(facility.getCode());
    location.setName(facility.getName());
    location.setDescription(facility.getDescription());
    location.addIdentifier()
        .setSystem("urn:ietf:rfc:3986")
        .setValue(serviceUrl + "/api/facilityTypes/" + facility.getType().getId());
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
