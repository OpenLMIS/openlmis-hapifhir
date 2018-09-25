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

package org.openlmis.hapifhir.config;

import static org.apache.commons.lang3.StringUtils.startsWith;

import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openlmis.hapifhir.i18n.Message;
import org.openlmis.hapifhir.i18n.MessageKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthorizationInterceptor extends InterceptorAdapter {

  @Value("${auth.server.clientId}")
  private String serviceTokenClientId;

  @Value("${auth.server.clientId.apiKey.prefix}")
  private String apiKeyPrefix;

  @Override
  public boolean incomingRequestPreProcessed(HttpServletRequest request,
      HttpServletResponse response) {
    Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (null == authentication) {
      throw new AuthenticationMessageException(new Message(MessageKeys.MISSING_AUTHORIZATION));
    }

    if (authentication instanceof OAuth2Authentication) {
      OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;

      if (auth2Authentication.isClientOnly() && checkServiceToken(auth2Authentication)) {
        return true;
      }
    }

    throw new AuthenticationMessageException(new Message(MessageKeys.INCORRECT_AUTHORIZATION));
  }

  private boolean checkServiceToken(OAuth2Authentication authentication) {
    String clientId = authentication.getOAuth2Request().getClientId();
    return serviceTokenClientId.equals(clientId) || startsWith(clientId, apiKeyPrefix);
  }

}
