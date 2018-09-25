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

package org.openlmis.hapifhir.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.restassured.RestAssuredClient;
import javax.annotation.PostConstruct;
import org.hl7.fhir.dstu3.model.Location;
import org.junit.runner.RunWith;
import org.openlmis.hapifhir.OAuth2AuthenticationDataBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseResourceIntegrationTest {

  static final String USER_ACCESS_TOKEN = "418c89c5-7f21-4cd1-a63a-38c47892b0fe";
  private static final String USER_ACCESS_TOKEN_HEADER = BEARER_TYPE + "" + USER_ACCESS_TOKEN;

  static final String CLIENT_ACCESS_TOKEN = "6d6896a5-e94c-4183-839d-911bc63174ff";
  private static final String CLIENT_ACCESS_TOKEN_HEADER = BEARER_TYPE + "" + CLIENT_ACCESS_TOKEN;

  static final String API_KEY_ACCESS_TOKEN = "6d6896a5-e94c-4183-839d-911bc63174ff";
  private static final String API_KEY_ACCESS_TOKEN_HEADER = BEARER_TYPE + "" + API_KEY_ACCESS_TOKEN;

  static final OAuth2Authentication USER_AUTHENTICATION = new OAuth2AuthenticationDataBuilder()
      .buildUserAuthentication();
  static final OAuth2Authentication CLIENT_AUTHENTICATION = new OAuth2AuthenticationDataBuilder()
      .buildServiceAuthentication();
  static final OAuth2Authentication API_KEY_AUTHENTICATION = new OAuth2AuthenticationDataBuilder()
      .buildApiKeyAuthentication();

  @LocalServerPort
  private int randomPort;

  @Value("${auth.server.baseUrl}")
  private String baseUri;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RemoteTokenServices tokenServices;

  @SpyBean(name = "myLocationDaoDstu3")
  IFhirResourceDao<Location> locationActions;

  RestAssuredClient restAssured;

  /**
   * Initialize the REST Assured client. Done here and not in the constructor, so that randomPort is
   * available.
   */
  @PostConstruct
  public void init() {
    RestAssured.baseURI = baseUri;
    RestAssured.port = randomPort;
    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
        new ObjectMapperConfig().jackson2ObjectMapperFactory((clazz, charset) -> objectMapper)
    );
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    RamlDefinition ramlDefinition = RamlLoaders.fromClasspath()
        .load("api-definition-raml.yaml").ignoringXheaders();
    restAssured = ramlDefinition.createRestAssured();
  }

  /**
   * Get a user access token. An arbitrary UUID string is returned and the tests assume it is a
   * valid one for an admin user.
   *
   * @return an access token
   */
  String getUserTokenHeader() {
    return USER_ACCESS_TOKEN_HEADER;
  }

  /**
   * Get a trusted client access token. An arbitrary UUID string is returned and the tests assume it
   * is a valid one for a trusted client. This is for service-to-service communication.
   *
   * @return an access token
   */
  String getClientTokenHeader() {
    return CLIENT_ACCESS_TOKEN_HEADER;
  }

  /**
   * Get an API key access token. An arbitrary UUID string is returned and the tests assume it is a
   * valid one for an API key.
   *
   * @return an access token
   */
  String getApiKeyTokenHeader() {
    return API_KEY_ACCESS_TOKEN_HEADER;
  }

  void mockAccess(OAuth2Authentication authorization, String token) {
    SecurityContextHolder.getContext().setAuthentication(authorization);
    given(tokenServices.loadAuthentication(token)).willReturn(authorization);
  }

}
