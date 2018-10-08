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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptor.IS_MANAGED_EXTERNALLY;

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import com.google.common.collect.Lists;
import java.util.UUID;
import lombok.Getter;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.codesystems.LocationPhysicalType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class OpenLmisResourceCreatorInterceptorTest
    <T extends BaseDto & ExtraDataContainer> {

  protected static final UUID LOCATION_ID = UUID.randomUUID();

  @Mock(name = "myLocationDaoDstu3")
  protected IFhirResourceDao<Location> locationRepository;

  @Mock
  @Getter
  private Location locationMock;

  @Mock
  private CodeableConcept physicalType;

  @Mock
  private Coding coding;

  @Mock
  private ServletRequestDetails details;

  @Captor
  private ArgumentCaptor<T> resourceCaptor;

  private IdType locationId = new IdType(LOCATION_ID.toString());

  @Before
  public void setUp() {
    when(details.getId()).thenReturn(locationId);

    when(locationRepository.read(locationId)).thenReturn(locationMock);

    when(locationMock.getPhysicalType()).thenReturn(physicalType);
    when(locationMock.getIdElement()).thenReturn(locationId);

    when(physicalType.getCoding()).thenReturn(Lists.newArrayList(coding));

    when(coding.getCode()).thenReturn(getSupportedType().toCode());
  }

  @Test
  public void shouldCreateOpenLmisResource() {
    // given
    OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();
    ResourceCommunicationService<T> communicationService = interceptor.getCommunicationService();

    // when
    prepareInterceptorForCreate(locationMock);
    interceptor.processingCompletedNormally(details);

    // then
    verify(communicationService).create(resourceCaptor.capture());
    verify(communicationService, never()).update(any());

    T resource = resourceCaptor.getValue();
    assertThat(resource.getExtraData()).containsEntry(IS_MANAGED_EXTERNALLY, "true");
    assertResourceAfterCreate(resource);
  }

  @Test
  public void shouldUpdateOpenLmisResource() {
    // given
    OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();
    ResourceCommunicationService<T> communicationService = interceptor.getCommunicationService();

    // when
    prepareInterceptorForUpdate(locationMock);
    interceptor.processingCompletedNormally(details);

    // then
    verify(communicationService, never()).create(any());
    verify(communicationService).update(resourceCaptor.capture());

    T resource = resourceCaptor.getValue();
    assertThat(resource.getExtraData()).containsEntry(IS_MANAGED_EXTERNALLY, "true");
    assertResourceAfterUpdate(resource);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfCodingCannotBeConvertedToEnum() {
    when(coding.getCode()).thenReturn("test-code");
    getInterceptor().processingCompletedNormally(details);
  }

  @Test
  public void shouldSupportSiPhysicalType() {
    assertThat(getInterceptor().supports(getSupportedType())).isTrue();
  }

  @Test
  public void shouldNotSupportOtherPhysicalTypes() {
    OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();

    for (LocationPhysicalType type : LocationPhysicalType.values()) {
      if (type == getSupportedType()) {
        continue;
      }

      boolean result = interceptor.supports(type);
      assertThat(result)
          .as("Should not support: %s", type)
          .isFalse();
    }
  }

  protected abstract OpenLmisResourceCreatorInterceptor<T> getInterceptor();

  protected abstract LocationPhysicalType getSupportedType();

  /**
   * Instructions in this method should prepare the interceptor to a create event. Instructions
   * should be related only with the minimal happy part for the event. Other paths should be
   * tested separately in subclass.
   */
  protected abstract void prepareInterceptorForCreate(Location locationMock);

  /**
   * Instructions in this method should prepare the interceptor to a update event. Instructions
   * should be related only with the minimal happy part for the event. Other paths should be
   * tested separately in subclass.
   */
  protected abstract void prepareInterceptorForUpdate(Location locationMock);

  /**
   * Asserts if resource that have been created is correct. The resource should contain only data
   * related with minimal happy path. Other paths should be tested separately in subclass.
   */
  protected abstract void assertResourceAfterCreate(T resource);

  /**
   * Asserts if resource that have been updated is correct. The resource should contain only data
   * related with minimal happy path. Other paths should be tested separately in subclass.
   */
  protected abstract void assertResourceAfterUpdate(T resource);

}
