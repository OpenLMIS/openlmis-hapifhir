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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.openlmis.hapifhir.service.OpenLmisResourceCreatorInterceptor.IS_MANAGED_EXTERNALLY;

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.apache.http.HttpHeaders;
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
import org.springframework.http.HttpMethod;
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

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private OAuth2Authentication authentication;

  @Captor
  private ArgumentCaptor<T> resourceCaptor;

  private IdType locationId = new IdType(LOCATION_ID.toString());

  @Before
  public void setUp() {
    when(locationRepository.read(locationId)).thenReturn(locationMock);

    when(locationMock.getPhysicalType()).thenReturn(physicalType);
    when(locationMock.getIdElement()).thenReturn(locationId);

    when(physicalType.getCoding()).thenReturn(Lists.newArrayList(coding));

    when(coding.getCode()).thenReturn(getSupportedType().toCode());

    when(response.getHeader(HttpHeaders.CONTENT_LOCATION)).thenReturn(LOCATION_ID.toString());

    when(details.getServletRequest()).thenReturn(request);
    when(details.getServletResponse()).thenReturn(response);
    when(details.getResourceName()).thenReturn("Location");

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
    when(request.getMethod()).thenReturn("POST");
    interceptor.processingCompletedNormally(details);

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
    when(request.getMethod()).thenReturn("PUT");
    interceptor.processingCompletedNormally(details);

    // then
    verify(communicationService).update(resourceCaptor.capture());

    T resource = resourceCaptor.getValue();
    assertThat(resource.getExtraData()).containsEntry(IS_MANAGED_EXTERNALLY, "true");
    assertResourceAfterUpdate(resource);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfCodingCannotBeConvertedToEnum() {
    when(coding.getCode()).thenReturn("test-code");
    when(request.getMethod()).thenReturn("POST");
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

  @Test
  public void shouldNotHandleRequestIfMethodIsNotPostOrPut() {
    Set<HttpMethod> methods = Arrays
        .stream(HttpMethod.values())
        .filter(method -> method != HttpMethod.POST)
        .filter(method -> method != HttpMethod.PUT)
        .collect(Collectors.toSet());

    OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();

    for (HttpMethod method : methods) {
      when(request.getMethod()).thenReturn(method.name());
      interceptor.processingCompletedNormally(details);
    }

    verifyZeroInteractions(locationRepository, interceptor.getCommunicationService());
  }

  @Test
  public void shouldNotHandleRequestIfResourceIsNotLocation() {
    OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();

    when(details.getResourceName()).thenReturn("TestResource");
    interceptor.processingCompletedNormally(details);

    verifyZeroInteractions(locationRepository, interceptor.getCommunicationService());
  }

  @Test
  public void shouldNotHandleRequestIfItIsFromReferenceDataService() {
    OpenLmisResourceCreatorInterceptor<T> interceptor = getInterceptor();

    when(authentication.getOAuth2Request()).thenReturn(createAuthRequest("service-token"));
    interceptor.processingCompletedNormally(details);

    verifyZeroInteractions(locationRepository, interceptor.getCommunicationService());
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

  private OAuth2Request createAuthRequest(String clientId) {
    return new OAuth2Request(null, clientId, null, true, null, null, null, null, null);
  }

}
