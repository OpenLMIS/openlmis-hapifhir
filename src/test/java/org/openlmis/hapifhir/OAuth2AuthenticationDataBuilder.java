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

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

public class OAuth2AuthenticationDataBuilder {
  private static final String API_KEY_PREFIX = "api-key-client-";

  private static final String CLIENT_CLIENT_ID = "user-client";
  private static final String SERVICE_CLIENT_ID = "trusted-client";
  private static final String API_KEY_CLIENT_ID = API_KEY_PREFIX + "20171214111354128";

  /**
   * Builds user authentication.
   */
  public OAuth2Authentication buildUserAuthentication() {
    return new DummyOAuth2Authentication(CLIENT_CLIENT_ID, "admin");
  }

  public OAuth2Authentication buildServiceAuthentication() {
    return new DummyOAuth2Authentication(SERVICE_CLIENT_ID);
  }

  public OAuth2Authentication buildApiKeyAuthentication() {
    return new DummyOAuth2Authentication(API_KEY_CLIENT_ID);
  }

  @EqualsAndHashCode(callSuper = false)
  private static final class DummyOAuth2Authentication extends OAuth2Authentication {
    private String user;

    DummyOAuth2Authentication(String clientId) {
      super(new OAuth2Request(null, clientId, null, true, null, null, null, null, null), null);
    }

    DummyOAuth2Authentication(String clientId, String user) {
      super(new OAuth2Request(null, clientId, null, true, null, null, null, null, null),
          new UsernamePasswordAuthenticationToken(user, null, null));
      this.user = user;
    }

    @Override
    public boolean isClientOnly() {
      return null == user;
    }

    @Override
    public Object getPrincipal() {
      return user;
    }
  }

}
