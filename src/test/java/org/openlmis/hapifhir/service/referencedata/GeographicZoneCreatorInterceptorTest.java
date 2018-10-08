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

package org.openlmis.hapifhir.service.referencedata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import java.util.UUID;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Location.LocationPositionComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.codesystems.LocationPhysicalType;
import org.hl7.fhir.instance.model.IdType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.hapifhir.GeographicLevelDtoDataBuilder;
import org.openlmis.hapifhir.GeographicZoneDtoDataBuilder;
import org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptor;
import org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptorTest;
import org.springframework.test.util.ReflectionTestUtils;

public class GeographicZoneCreatorInterceptorTest
    extends OpenLmisResourceCreatorInterceptorTest<GeographicZoneDto> {

  private static final UUID PARENT_GEO_ZONE_ID = UUID.randomUUID();

  private static final Double LATITUDE = 10.0;
  private static final Double LONGITUDE = -10.5;

  private static final String NAME = "name";
  private static final LocationPositionComponent POSITION = new LocationPositionComponent(
      new DecimalType(LONGITUDE), new DecimalType(LATITUDE)
  );
  private static final Reference PART_OF = new Reference(new IdType(PARENT_GEO_ZONE_ID.toString()));
  private static final String ALIAS = "alias";

  @Mock
  private GeographicZoneReferenceDataService geographicZoneReferenceDataService;

  @Mock
  private GeographicLevelReferenceDataService geographicLevelReferenceDataService;

  @InjectMocks
  private GeographicZoneCreatorInterceptor interceptor;

  private int largestLevelNumber = 1;

  private GeographicLevelDto largestGeographicLevel = new GeographicLevelDtoDataBuilder()
      .withLevelNumber(largestLevelNumber)
      .build();
  private GeographicLevelDto parentGeographicLevel = new GeographicLevelDtoDataBuilder()
      .withLevelNumber(largestLevelNumber + 1)
      .build();
  private GeographicLevelDto currentGeographicLevel = new GeographicLevelDtoDataBuilder()
      .withLevelNumber(largestLevelNumber + 2)
      .build();

  private GeographicZoneDto parentGeographicZone = new GeographicZoneDtoDataBuilder()
      .withLevel(parentGeographicLevel)
      .withId(PARENT_GEO_ZONE_ID)
      .build();

  private GeographicZoneDto geographicZone = new GeographicZoneDtoDataBuilder()
      .withLevel(currentGeographicLevel)
      .withId(LOCATION_ID)
      .build();

  @Before
  public void setUp() {
    super.setUp();

    ReflectionTestUtils.setField(interceptor, "largestLevelNumber", largestLevelNumber);

    when(geographicLevelReferenceDataService.findAll()).thenReturn(
        Lists.newArrayList(largestGeographicLevel, parentGeographicLevel, currentGeographicLevel));

    when(geographicZoneReferenceDataService.findOne(parentGeographicZone.getId()))
        .thenReturn(parentGeographicZone);
  }

  @Test
  public void shouldSetOptionalFieldsIfLocationHasRelatedData() {
    Location locationMock = getLocationMock();
    prepareInterceptorForCreate(locationMock);

    when(locationMock.getName()).thenReturn(NAME);
    when(locationMock.getPosition()).thenReturn(POSITION);
    when(locationMock.getPartOf()).thenReturn(PART_OF);

    GeographicZoneDto resource = interceptor.buildResource(locationMock).getResource();

    assertThat(resource.getName()).isEqualTo(NAME);
    assertThat(resource.getLatitude()).isEqualTo(LATITUDE);
    assertThat(resource.getLongitude()).isEqualTo(LONGITUDE);
    assertThat(resource.getParent()).isEqualTo(parentGeographicZone);
  }

  @Test
  public void shouldSetGeoLevelWithNumberGreaterByOneThanParentZoneLevel() {
    Location locationMock = getLocationMock();
    prepareInterceptorForCreate(locationMock);

    when(locationMock.getPartOf()).thenReturn(PART_OF);

    GeographicZoneDto resource = interceptor.buildResource(locationMock).getResource();

    assertThat(resource.getParent().getLevel()).isEqualTo(parentGeographicLevel);
    assertThat(resource.getLevel()).isEqualTo(currentGeographicLevel);
  }

  @Override
  protected OpenLmisResourceCreatorInterceptor<GeographicZoneDto> getInterceptor() {
    return interceptor;
  }

  @Override
  protected LocationPhysicalType getSupportedType() {
    return LocationPhysicalType.AREA;
  }

  @Override
  protected void prepareInterceptorForCreate(Location locationMock) {
    when(geographicZoneReferenceDataService.findOne(LOCATION_ID)).thenReturn(null);
    when(locationMock.getAlias()).thenReturn(Lists.newArrayList(new StringType(ALIAS)));
    when(locationMock.getPartOf()).thenReturn(null);
  }

  @Override
  protected void prepareInterceptorForUpdate(Location locationMock) {
    when(geographicZoneReferenceDataService.findOne(LOCATION_ID)).thenReturn(geographicZone);
    when(locationMock.getAlias()).thenReturn(Lists.newArrayList(new StringType(ALIAS)));
    when(locationMock.getPartOf()).thenReturn(null);
  }

  @Override
  protected void assertResourceAfterCreate(GeographicZoneDto resource) {
    assertThat(resource.getId()).isEqualTo(LOCATION_ID);
    assertThat(resource.getCode()).isEqualTo(ALIAS);
    assertThat(resource.getLevel()).isEqualTo(largestGeographicLevel);
  }

  @Override
  protected void assertResourceAfterUpdate(GeographicZoneDto resource) {
    assertThat(resource.getId()).isEqualTo(LOCATION_ID);
    assertThat(resource.getCode()).isEqualTo(ALIAS);
    assertThat(resource.getLevel()).isEqualTo(largestGeographicLevel);
  }
}
