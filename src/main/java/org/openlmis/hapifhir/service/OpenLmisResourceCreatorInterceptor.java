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

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpHeaders;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.dstu3.model.codesystems.LocationPhysicalType;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public abstract class OpenLmisResourceCreatorInterceptor<T extends BaseDto & ExtraDataContainer>
    extends InterceptorAdapter {

  static final String IS_MANAGED_EXTERNALLY = "isManagedExternally";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  @Qualifier("myLocationDaoDstu3")
  private IFhirResourceDao<Location> locationRepository;

  @Value("${auth.server.clientId.apiKey.prefix}")
  private String apiKeyPrefix;

  @Override
  public void processingCompletedNormally(ServletRequestDetails details) {
    if (shouldIgnore(details)) {
      logger.info(
          "Request came from the reference data service. Skipping synchronization process."
      );
      return;
    }

    String contentLocation = details
        .getServletResponse()
        .getHeader(HttpHeaders.CONTENT_LOCATION);
    IIdType locationId = new IdType(new UriType(contentLocation));
    logger.debug("Load location with id: {}", locationId);
    Location location = locationRepository.read(locationId);

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

  private boolean shouldIgnore(ServletRequestDetails details) {
    HttpServletRequest request = details.getServletRequest();

    if (incorrectRequestMethod(request) || incorrectResource(details)) {
      return true;
    }

    Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (authentication instanceof OAuth2Authentication) {
      OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
      String clientId = auth2Authentication.getOAuth2Request().getClientId();

      return !auth2Authentication.isClientOnly() || !startsWith(clientId, apiKeyPrefix);
    }

    return true;
  }

  private boolean incorrectRequestMethod(HttpServletRequest request) {
    logger.trace("Request method: {}", request.getMethod());

    boolean isNotPost = !"POST".equalsIgnoreCase(request.getMethod());
    boolean isNotPut = !"PUT".equalsIgnoreCase(request.getMethod());

    if (isNotPost && isNotPut) {
      logger.debug("The request's method is not POST or PUT");
      return true;
    }

    return false;
  }

  private boolean incorrectResource(ServletRequestDetails details) {
    logger.trace("Resource name: {}", details.getResourceName());

    boolean isNotLocation = !"Location".equalsIgnoreCase(details.getResourceName());

    if (isNotLocation) {
      logger.debug("The resource name is not equal to Location");
      return true;
    }

    return false;
  }

  private LocationPhysicalType convertCodingToEnum(Coding coding) {
    try {
      return LocationPhysicalType.fromCode(coding.getCode().toLowerCase(Locale.ENGLISH));
    } catch (FHIRException exp) {
      throw new IllegalStateException(exp);
    }
  }

}
