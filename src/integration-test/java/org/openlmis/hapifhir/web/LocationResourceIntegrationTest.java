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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.server.SimpleBundleProvider;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.hl7.fhir.r4.model.Location;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.hapifhir.LocationDataBuilder;

public class LocationResourceIntegrationTest extends BaseResourceIntegrationTest {
  private static final String RESOURCE_URL = "/hapifhir/Location";

  private static final Location LOCATION = new LocationDataBuilder().build();
  private static final IBundleProvider BUNDLE_PROVIDER = new SimpleBundleProvider(LOCATION);

  @Before
  public void setUp() {
    willReturn(BUNDLE_PROVIDER).given(locationActions).search(any(), any(), any());
    willReturn(BUNDLE_PROVIDER).given(locationActions).search(any(), any());
    willReturn(BUNDLE_PROVIDER).given(locationActions).search(any());
  }

  @Test
  public void shouldDenyAccessForUserRequests() {
    mockAccess(USER_AUTHENTICATION, USER_ACCESS_TOKEN);

    restAssured.given().header(HttpHeaders.AUTHORIZATION, getUserTokenHeader()).when()
        .get(RESOURCE_URL).then().statusCode(HttpStatus.SC_UNAUTHORIZED)
        .body("issue.diagnostics", hasItem("Incorrect authorization"));
  }

  @Test
  public void shouldAllowAccessForServiceRequests() {
    mockAccess(CLIENT_AUTHENTICATION, CLIENT_ACCESS_TOKEN);

    ValidatableResponse response =
        restAssured.given().header(HttpHeaders.AUTHORIZATION, getClientTokenHeader()).when()
            .get(RESOURCE_URL).then().statusCode(HttpStatus.SC_OK);

    assertEntry(response);
  }

  @Test
  public void shouldAllowAccessForApiKeyRequests() {
    mockAccess(API_KEY_AUTHENTICATION, API_KEY_ACCESS_TOKEN);

    ValidatableResponse response =
        restAssured.given().header(HttpHeaders.AUTHORIZATION, getApiKeyTokenHeader()).when()
            .get(RESOURCE_URL).then().statusCode(HttpStatus.SC_OK);

    assertEntry(response);
  }

  private void assertEntry(ValidatableResponse response) {
    response.body("entry", hasSize(1))
        .body("entry[0].resource.resourceType", is(LOCATION.getResourceType().name()))
        .body("entry[0].resource.id", is(LOCATION.getId()));
  }

}
