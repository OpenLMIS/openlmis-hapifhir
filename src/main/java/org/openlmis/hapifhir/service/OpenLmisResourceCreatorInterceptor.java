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

import static org.apache.commons.lang3.StringUtils.startsWith;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.ServerOperationInterceptorAdapter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.codesystems.LocationPhysicalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public abstract class OpenLmisResourceCreatorInterceptor<T extends BaseDto & ExtraDataContainer>
    extends ServerOperationInterceptorAdapter {

  static final String IS_MANAGED_EXTERNALLY = "isManagedExternally";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${auth.server.clientId.apiKey.prefix}")
  private String apiKeyPrefix;

  @Override
  public void resourceCreated(RequestDetails details, IBaseResource resource) {
    handleEvent(resource);
  }

  @Override
  public void resourceUpdated(RequestDetails details, IBaseResource oldResource,
      IBaseResource newResource) {
    handleEvent(newResource);
  }

  private void handleEvent(IBaseResource fhirResource) {
    if (shouldIgnore(fhirResource)) {
      return;
    }

    Location location = (Location) fhirResource;

    List<T> resources = location
        .getPhysicalType()
        .getCoding()
        .stream()
        .map(this::convertCodingToEnum)
        .filter(this::supports)
        .map(type -> {
          logger.debug("Build OpenLMIS's resources for type: {}", type);
          return buildResource(location);
        })
        .collect(Collectors.toList());

    ResourceCommunicationService<T> communicationService = getCommunicationService();

    for (T resource : resources) {
      resource.addExtraDataEntry(IS_MANAGED_EXTERNALLY, true);
      logger.trace("Update the existing resource");
      communicationService.update(resource);
    }
  }

  protected abstract boolean supports(LocationPhysicalType type);

  protected abstract T buildResource(Location location);

  protected abstract ResourceCommunicationService<T> getCommunicationService();

  private boolean shouldIgnore(IBaseResource resource) {
    if (!(resource instanceof Location)) {
      logger.info("Resource is not an instance of Location. Skipping synchronization process.");
      return true;
    }

    Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (authentication instanceof OAuth2Authentication) {
      OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
      String clientId = auth2Authentication.getOAuth2Request().getClientId();

      boolean result = !auth2Authentication.isClientOnly()
          || !startsWith(clientId, apiKeyPrefix);

      if (result) {
        logger.info(
            "Request came from the reference data service. Skipping synchronization process."
        );
      }

      return result;
    } else {
      logger.info("Unknown authentication. Skipping synchronization process.");

      return true;
    }
  }

  private LocationPhysicalType convertCodingToEnum(Coding coding) {
    try {
      return LocationPhysicalType.fromCode(coding.getCode().toLowerCase(Locale.ENGLISH));
    } catch (FHIRException exp) {
      throw new IllegalStateException(exp);
    }
  }

}
