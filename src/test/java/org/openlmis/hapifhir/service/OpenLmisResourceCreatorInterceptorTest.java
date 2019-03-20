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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptor.IS_MANAGED_EXTERNALLY;

import com.google.common.collect.Lists;
import java.util.UUID;
import lombok.Getter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.codesystems.LocationPhysicalType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidUsingHardCodedIP"})
public abstract class OpenLmisResourceCreatorInterceptorTest
    <T extends BaseDto & ExtraDataContainer> {

  private static final String API_KEY_PREFIX = "prefix";
  protected static final UUID LOCATION_ID = UUID.randomUUID();

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Mock
  @Getter
  private Location locationMock;

  @Mock
  private CodeableConcept physicalType;

  @Mock
  private Coding coding;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private OAuth2Authentication authentication;

  @Captor
  private ArgumentCaptor<T> resourceCaptor;

  private IdType locationId = new IdType(LOCATION_ID.toString());

  @Before
  public void setUp() {
    when(locationMock.getPhysicalType()).thenReturn(physicalType);
    when(locationMock.getIdElement()).thenReturn(locationId);

    when(physicalType.getCoding()).thenReturn(Lists.newArrayList(coding));

    when(coding.getCode()).thenReturn(getSupportedType().toCode());

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isClientOnly()).thenReturn(true);
    when(authentication.getOAuth2Request()).thenReturn(createAuthRequest(API_KEY_PREFIX));

    ReflectionTestUtils.setField(getInterceptor(), "apiKeyPrefix", API_KEY_PREFIX);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void shouldCreateOpenLmisResource() {
    // given
    final OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();
    final ResourceCommunicationService<T> communicationService = interceptor
        .getCommunicationService();

    // when
    prepareInterceptorForCreate(locationMock);
    interceptor.resourceCreated(null, locationMock);

    // then
    verify(communicationService).update(resourceCaptor.capture());

    T resource = resourceCaptor.getValue();
    assertThat(resource.getExtraData()).containsEntry(IS_MANAGED_EXTERNALLY, "true");
    assertResourceAfterCreate(resource);
  }

  @Test
  public void shouldUpdateOpenLmisResource() {
    // given
    final OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();
    final ResourceCommunicationService<T> communicationService = interceptor
        .getCommunicationService();

    // when
    prepareInterceptorForUpdate(locationMock);
    interceptor.resourceUpdated(null, null, locationMock);

    // then
    verify(communicationService).update(resourceCaptor.capture());

    T resource = resourceCaptor.getValue();
    assertThat(resource.getExtraData()).containsEntry(IS_MANAGED_EXTERNALLY, "true");
    assertResourceAfterUpdate(resource);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfCodingCannotBeConvertedToEnumForCreateEvent() {
    when(coding.getCode()).thenReturn("test-code");
    getInterceptor().resourceCreated(null, locationMock);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfCodingCannotBeConvertedToEnumForUpdateEvent() {
    when(coding.getCode()).thenReturn("test-code");
    getInterceptor().resourceUpdated(null, null, locationMock);
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

  @Test
  public void shouldIgnoreEventIfResourceIsNotLocation() {
    OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();

    IBaseResource resource = mock(Patient.class);

    interceptor.resourceCreated(null, resource);
    interceptor.resourceUpdated(null, null, resource);

    verifyZeroInteractions(securityContext, interceptor.getCommunicationService());
  }

  @Test
  public void shouldIgnoreEventIfItIsFromReferenceDataService() {
    OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();

    when(authentication.getOAuth2Request()).thenReturn(createAuthRequest("service-token"));
    interceptor.resourceCreated(null, locationMock);
    interceptor.resourceUpdated(null, null, locationMock);

    verifyZeroInteractions(interceptor.getCommunicationService());
  }

  protected abstract OpenLmisResourceCreatorInterceptor<T> getInterceptor();

  protected abstract LocationPhysicalType getSupportedType();

  /**
   * Instructions in this method should prepare the interceptor to a create event. Instructions
   * should be related only with the minimal happy part for the event. Other paths should be tested
   * separately in subclass.
   */
  protected abstract void prepareInterceptorForCreate(Location locationMock);

  /**
   * Instructions in this method should prepare the interceptor to a update event. Instructions
   * should be related only with the minimal happy part for the event. Other paths should be tested
   * separately in subclass.
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

  private OAuth2Request createAuthRequest(String clientId) {
    return new OAuth2Request(null, clientId, null, true, null, null, null, null, null);
  }

}
