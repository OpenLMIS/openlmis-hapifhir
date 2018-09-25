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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class CustomAuthorizationInterceptorTest {

  private static final String SERVICE_TOKEN_CLIENT_ID = "service-token";
  private static final String API_KEY_PREFIX = "api-key-";

  @InjectMocks
  private CustomAuthorizationInterceptor interceptor;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private OAuth2Authentication authentication;

  @Before
  public void setUp() {
    ReflectionTestUtils.setField(interceptor, "serviceTokenClientId", SERVICE_TOKEN_CLIENT_ID);
    ReflectionTestUtils.setField(interceptor, "apiKeyPrefix", API_KEY_PREFIX);

    SecurityContextHolder.setContext(securityContext);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isClientOnly()).thenReturn(true);
  }

  @Test
  public void shouldThrowExceptionIfAuthorizationIsNotSet() {
    when(securityContext.getAuthentication()).thenReturn(null);

    assertThatThrownBy(() -> interceptor.incomingRequestPreProcessed(null, null))
        .isEqualTo(CustomAuthorizationInterceptor.MISSING_AUTH);
  }

  @Test
  public void shouldThrowExceptionIfAuthorizationIsNotOAuth2() {
    when(securityContext.getAuthentication())
        .thenReturn(new UsernamePasswordAuthenticationToken(null, null));

    assertThatThrownBy(() -> interceptor.incomingRequestPreProcessed(null, null))
        .isEqualTo(CustomAuthorizationInterceptor.INCORRECT_AUTH);
  }

  @Test
  public void shouldThrowExceptionIfUserTokenWasUsed() {
    when(authentication.isClientOnly()).thenReturn(false);

    assertThatThrownBy(() -> interceptor.incomingRequestPreProcessed(null, null))
        .isEqualTo(CustomAuthorizationInterceptor.INCORRECT_AUTH);
  }

  @Test
  public void shouldThrowExceptionIfIncorrectClientIdWasUsed() {
    when(authentication.getOAuth2Request())
        .thenReturn(createAuthRequest("invalid-client-id"));

    assertThatThrownBy(() -> interceptor.incomingRequestPreProcessed(null, null))
        .isEqualTo(CustomAuthorizationInterceptor.INCORRECT_AUTH);
  }

  @Test
  public void shouldThrowExceptionIfTokenHasIncorrectPrefixWasUsed() {
    when(authentication.getOAuth2Request())
        .thenReturn(createAuthRequest("invalid-"));

    assertThatThrownBy(() -> interceptor.incomingRequestPreProcessed(null, null))
        .isEqualTo(CustomAuthorizationInterceptor.INCORRECT_AUTH);
  }

  @Test
  public void shouldReturnTrueIfServiceLevelTokenWasUsed() {
    when(authentication.getOAuth2Request())
        .thenReturn(createAuthRequest(SERVICE_TOKEN_CLIENT_ID));

    assertThat(interceptor.incomingRequestPreProcessed(null, null))
        .isTrue();
  }

  @Test
  public void shouldCreateAllowAllRulesIfApiKeyWasUsed() {
    when(authentication.getOAuth2Request())
        .thenReturn(createAuthRequest(API_KEY_PREFIX + "123456789"));

    assertThat(interceptor.incomingRequestPreProcessed(null, null))
        .isTrue();
  }

  private OAuth2Request createAuthRequest(String clientId) {
    return new OAuth2Request(null, clientId, null, true, null, null, null, null, null);
  }

}
