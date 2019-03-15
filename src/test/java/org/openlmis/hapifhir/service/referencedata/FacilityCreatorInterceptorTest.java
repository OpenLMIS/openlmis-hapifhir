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
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.UUID;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Location.LocationPositionComponent;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.codesystems.LocationPhysicalType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openlmis.hapifhir.FacilityDtoDataBuilder;
import org.openlmis.hapifhir.FacilityTypeDtoDataBuilder;
import org.openlmis.hapifhir.GeographicZoneDtoDataBuilder;
import org.openlmis.hapifhir.i18n.MessageKeys;
import org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptor;
import org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptorTest;
import org.openlmis.hapifhir.service.ValidationMessageException;
import org.springframework.test.util.ReflectionTestUtils;

public class FacilityCreatorInterceptorTest
    extends OpenLmisResourceCreatorInterceptorTest<FacilityDto> {

  private static final UUID GEO_ZONE_ID = UUID.randomUUID();

  private static final Double LATITUDE = 5.5;
  private static final Double LONGITUDE = -5.0;

  private static final String NAME = "name";
  private static final String ALIAS = "alias";
  private static final String DESCRIPTION = "description";
  private static final Reference PART_OF = new Reference(new IdType(GEO_ZONE_ID.toString()));
  private static final LocationStatus LOCATION_STATUS = LocationStatus.ACTIVE;
  private static final LocationPositionComponent POSITION = new LocationPositionComponent(
      new DecimalType(LONGITUDE), new DecimalType(LATITUDE)
  );

  @Mock
  private FacilityReferenceDataService facilityReferenceDataService;

  @Mock
  private FacilityTypeReferenceDataService facilityTypeReferenceDataService;

  @Mock
  private GeographicZoneReferenceDataService geographicZoneReferenceDataService;

  @Spy
  private GeometryFactory geometryFactory;

  @InjectMocks
  private FacilityCreatorInterceptor interceptor;

  private FacilityTypeDto facilityType = new FacilityTypeDtoDataBuilder()
      .build();

  private GeographicZoneDto geographicZone = new GeographicZoneDtoDataBuilder()
      .withId(GEO_ZONE_ID)
      .build();

  private FacilityDto facility = new FacilityDtoDataBuilder()
      .withId(LOCATION_ID)
      .build();

  @Before
  public void setUp() {
    super.setUp();

    ReflectionTestUtils.setField(interceptor, "geometryFactory", geometryFactory);
    ReflectionTestUtils.setField(interceptor, "facilityTypeId", facilityType.getId());
    ReflectionTestUtils.setField(interceptor, "facilityType", null);

    when(facilityTypeReferenceDataService.findOne(facilityType.getId())).thenReturn(facilityType);
    when(geographicZoneReferenceDataService.findOne(GEO_ZONE_ID)).thenReturn(geographicZone);
  }

  @Test
  public void shouldSetOptionalFieldsIfLocationHasRelatedData() {
    Location locationMock = getLocationMock();
    prepareInterceptorForCreate(locationMock);

    when(locationMock.getName()).thenReturn(NAME);
    when(locationMock.getDescription()).thenReturn(DESCRIPTION);
    when(locationMock.getPosition()).thenReturn(POSITION);

    FacilityDto resource = interceptor.buildResource(locationMock);

    assertThat(resource.getName()).isEqualTo(NAME);
    assertThat(resource.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(resource.getLocation().getX()).isEqualTo(LONGITUDE);
    assertThat(resource.getLocation().getY()).isEqualTo(LATITUDE);
  }

  @Test
  public void shouldRetrieveFacilityTypeOnlyOnce() {
    Location locationMock = getLocationMock();
    prepareInterceptorForCreate(locationMock);

    interceptor.buildResource(locationMock);
    interceptor.buildResource(locationMock);
    interceptor.buildResource(locationMock);
    interceptor.buildResource(locationMock);

    verify(facilityTypeReferenceDataService).findOne(facilityType.getId());
  }

  @Test
  public void shouldThrowExceptionIfFacilityCodeIsNullAndAliasListWasNotSet() {
    Location locationMock = getLocationMock();
    prepareInterceptorForCreate(locationMock);
    when(locationMock.getAlias()).thenReturn(Lists.newArrayList());

    exception.expect(ValidationMessageException.class);
    exception.expectMessage(containsString(MessageKeys.ERROR_FACILITY_CODE_REQUIRED));

    interceptor.buildResource(locationMock);
  }

  @Test
  public void shouldThrowExceptionIfFacilityCodeIsNullAndAliasListWasSet() {
    Location locationMock = getLocationMock();
    prepareInterceptorForCreate(locationMock);
    when(locationMock.getAlias()).thenReturn(Lists.newArrayList(new StringType()));

    exception.expect(ValidationMessageException.class);
    exception.expectMessage(containsString(MessageKeys.ERROR_FACILITY_CODE_REQUIRED));

    interceptor.buildResource(locationMock);
  }

  @Test
  public void shouldThrowExceptionIfFacilityTypeCantBeFound() {
    Location locationMock = getLocationMock();
    prepareInterceptorForCreate(locationMock);
    when(facilityTypeReferenceDataService.findOne(facilityType.getId())).thenReturn(null);

    exception.expect(ValidationMessageException.class);
    exception.expectMessage(containsString(MessageKeys.ERROR_NOT_FOUND_FACILITY_TYPE));

    interceptor.buildResource(locationMock);
  }

  @Test
  public void shouldThrowExceptionIfGeographicZoneCantBeFound() {
    Location locationMock = getLocationMock();
    prepareInterceptorForCreate(locationMock);
    when(geographicZoneReferenceDataService.findOne(GEO_ZONE_ID)).thenReturn(null);

    exception.expect(ValidationMessageException.class);
    exception.expectMessage(containsString(MessageKeys.ERROR_NOT_FOUND_GEO_ZONE));

    interceptor.buildResource(locationMock);
  }

  @Override
  protected OpenLmisResourceCreatorInterceptor<FacilityDto> getInterceptor() {
    return interceptor;
  }

  @Override
  protected LocationPhysicalType getSupportedType() {
    return LocationPhysicalType.SI;
  }

  @Override
  protected void prepareInterceptorForCreate(Location locationMock) {
    when(facilityReferenceDataService.findOne(any(UUID.class))).thenReturn(null);

    when(locationMock.getAlias()).thenReturn(Lists.newArrayList(new StringType(ALIAS)));
    when(locationMock.getPartOf()).thenReturn(PART_OF);
    when(locationMock.getStatus()).thenReturn(LOCATION_STATUS);
  }

  @Override
  protected void prepareInterceptorForUpdate(Location locationMock) {
    when(facilityReferenceDataService.findOne(any(UUID.class))).thenReturn(facility);

    when(locationMock.getAlias()).thenReturn(Lists.newArrayList(new StringType(ALIAS)));
    when(locationMock.getPartOf()).thenReturn(PART_OF);
    when(locationMock.getStatus()).thenReturn(LOCATION_STATUS);
  }

  @Override
  protected void assertResourceAfterCreate(FacilityDto resource) {
    assertThat(resource.getId()).isEqualTo(LOCATION_ID);
    assertThat(resource.getCode()).isEqualTo(ALIAS);
    assertThat(resource.getGeographicZone()).isEqualTo(geographicZone);
    assertThat(resource.getType()).isEqualTo(facilityType);
    assertThat(resource.getActive()).isTrue();
    assertThat(resource.getEnabled()).isTrue();
  }

  @Override
  protected void assertResourceAfterUpdate(FacilityDto resource) {
    assertThat(resource.getId()).isEqualTo(LOCATION_ID);
    assertThat(resource.getCode()).isEqualTo(ALIAS);
    assertThat(resource.getGeographicZone()).isEqualTo(geographicZone);
    assertThat(resource.getType()).isEqualTo(facilityType);
    assertThat(resource.getActive()).isTrue();
    assertThat(resource.getEnabled()).isTrue();
  }
}
