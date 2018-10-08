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

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.codesystems.LocationPhysicalType;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class OpenLmisResourceCreatorInterceptor<T extends BaseDto & ExtraDataContainer>
    extends InterceptorAdapter {

  static final String IS_MANAGED_EXTERNALLY = "isManagedExternally";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  @Qualifier("myLocationDaoDstu3")
  private IFhirResourceDao<Location> locationRepository;

  @Override
  public void processingCompletedNormally(ServletRequestDetails details) {
    IIdType locationId = details.getId();
    logger.debug("Load location with id: {}", details.getId());
    Location location = locationRepository.read(locationId);

    logger.debug("Build OpenLMIS's resources");
    List<BuildResult> resources = location
        .getPhysicalType()
        .getCoding()
        .stream()
        .map(this::convertCodingToEnum)
        .filter(this::supports)
        .map(type -> buildResource(location))
        .collect(Collectors.toList());

    ResourceCommunicationService<T> communicationService = getCommunicationService();

    for (BuildResult result : resources) {
      T resource = result.getResource();
      resource.addExtraDataEntry(IS_MANAGED_EXTERNALLY, true);

      if (result.isCreated()) {
        logger.trace("Create new resource");
        communicationService.create(resource);
      } else {
        logger.trace("Update the existing resource");
        communicationService.update(resource);
      }
    }
  }

  protected abstract boolean supports(LocationPhysicalType type);

  protected abstract BuildResult buildResource(Location location);

  protected abstract ResourceCommunicationService<T> getCommunicationService();

  private LocationPhysicalType convertCodingToEnum(Coding coding) {
    try {
      return LocationPhysicalType.fromCode(coding.getCode().toLowerCase(Locale.ENGLISH));
    } catch (FHIRException exp) {
      throw new IllegalStateException(exp);
    }
  }

  @Getter
  @AllArgsConstructor
  public final class BuildResult {
    private final T resource;
    private final boolean created;
  }

}
