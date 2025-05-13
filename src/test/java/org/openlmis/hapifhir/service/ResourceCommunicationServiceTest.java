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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.hapifhir.i18n.MessageKeys;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

@SuppressWarnings("PMD.TooManyMethods")
public abstract class ResourceCommunicationServiceTest<T extends BaseDto>
    extends BaseCommunicationServiceTest<T> {

  private static final String EXCEPTION_METHOD_FORMAT
      = "Unable to retrieve %s. Error code: %d, response message: ";

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Mock
  private ObjectMapper objectMapper;

  private ResourceCommunicationService<T> service;

  @Before
  @Override
  public void setUp() {
    super.setUp();

    mockAuth();

    service = getService();
    ReflectionTestUtils.setField(service, "objectMapper", objectMapper);
  }

  @Override
  protected void initService(RestOperations restClient, AuthService authService) {
    ReflectionTestUtils.setField(getService(), "restTemplate", restClient);
    ReflectionTestUtils.setField(getService(), "authService", authService);
  }

  @Override
  protected Class<T> getResultClass() {
    return service.getResultClass();
  }

  @Override
  protected Class<T[]> getArrayResultClass() {
    return service.getArrayResultClass();
  }

  protected abstract ResourceCommunicationService<T> getService();

  @Test
  public void shouldCreateResource() {
    // given
    ResourceCommunicationService<T> service = getService();

    // when
    T instance = mockResponseEntityAndGetDto();
    T created = service.create(instance);

    // then
    assertThat(created, is(instance));

    verifyRequest()
        .isPostRequest()
        .hasAuthHeader()
        .hasJsonAsContentType()
        .hasBody(instance)
        .isUriStartsWith(service.getServiceUrl() + service.getUrl());
  }

  @Test
  public void shouldThrowExceptionIfResourceCantBeCreated() throws IOException {
    // given
    LocalizedMessageDto message = new LocalizedMessageDto("A", "B");

    // when
    mockRequestFail(HttpStatus.BAD_REQUEST);
    when(objectMapper.readValue(anyString(), eq(LocalizedMessageDto.class)))
        .thenReturn(message);

    exception.expect(ExternalApiException.class);
    exception.expectMessage(message.getMessage());

    T instance = generateInstance();
    service.create(instance);
  }

  @Test
  public void shouldThrowExceptionIfResponseCantBeConvertedToLocalizedMessageForCreate()
      throws IOException {
    // when
    mockRequestFail(HttpStatus.BAD_REQUEST);
    when(objectMapper.readValue(anyString(), eq(LocalizedMessageDto.class)))
        .thenThrow(new JsonParseException(null, "test-exception"));

    exception.expect(ServerException.class);
    exception.expectMessage(containsString("test-exception"));

    T instance = generateInstance();
    service.create(instance);
  }

  @Test
  public void shouldThrowExceptionIfThereAreOtherProblemsWithCreateRequest() {
    // given
    Class<T> definition = getResultClass();
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String exceptionMessage = String
        .format(EXCEPTION_METHOD_FORMAT, definition.getSimpleName(), status.value());

    // when
    exception.expect(DataRetrievalException.class);
    exception.expectMessage(exceptionMessage);
    mockRequestFail(status);

    T instance = generateInstance();
    service.create(instance);
  }

  @Test
  public void shouldUpdateResource() {
    // given
    ResourceCommunicationService<T> service = getService();

    // when
    T instance = mockResponseEntityAndGetDto();
    T created = service.update(instance);

    // then
    assertThat(created, is(instance));

    verifyRequest()
        .isPutRequest()
        .hasAuthHeader()
        .hasJsonAsContentType()
        .hasBody(instance)
        .isUriStartsWith(service.getServiceUrl() + service.getUrl() + instance.getId());
  }

  @Test
  public void shouldThrowExceptionIfResourceCantBeUpdated() throws IOException {
    // given
    LocalizedMessageDto message = new LocalizedMessageDto("C", "D");

    // when
    mockRequestFail(HttpStatus.BAD_REQUEST);
    when(objectMapper.readValue(anyString(), eq(LocalizedMessageDto.class)))
        .thenReturn(message);

    exception.expect(ExternalApiException.class);
    exception.expectMessage(message.getMessage());

    T instance = generateInstance();
    service.update(instance);
  }

  @Test
  public void shouldThrowExceptionIfResponseCantBeConvertedToLocalizedMessageForUpdate()
      throws IOException {
    // when
    mockRequestFail(HttpStatus.BAD_REQUEST);
    when(objectMapper.readValue(anyString(), eq(LocalizedMessageDto.class)))
        .thenThrow(new JsonParseException(null, "test-exception"));

    exception.expect(ServerException.class);
    exception.expectMessage(containsString("test-exception"));

    T instance = generateInstance();
    service.update(instance);
  }

  @Test
  public void shouldThrowExceptionIfThereAreOtherProblemsWithUpdateRequest() {
    // given
    Class<T> definition = getResultClass();
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String exceptionMessage = String
        .format(EXCEPTION_METHOD_FORMAT, definition.getSimpleName(), status.value());

    // when
    exception.expect(DataRetrievalException.class);
    exception.expectMessage(exceptionMessage);
    mockRequestFail(status);

    T instance = generateInstance();
    service.update(instance);
  }

  @Test
  public void shouldFindResource() {
    // given
    ResourceCommunicationService<T> service = getService();
    UUID id = UUID.randomUUID();

    // when
    T instance = mockResponseEntityAndGetDto();
    T found = service.findOne(id);

    // then
    assertThat(found, is(instance));

    verifyRequest()
        .isGetRequest()
        .hasAuthHeader()
        .hasEmptyBody()
        .isUriStartsWith(service.getServiceUrl() + service.getUrl() + id);
  }

  @Test
  public void shouldReturnNullIfEntityNotFound() {
    // given
    ResourceCommunicationService<T> service = getService();
    UUID id = UUID.randomUUID();

    // when
    mockRequestFail(HttpStatus.NOT_FOUND);

    T found = service.findOne(id);

    // then
    assertThat(found, is(nullValue()));

    verifyRequest()
        .isGetRequest()
        .hasAuthHeader()
        .hasEmptyBody()
        .isUriStartsWith(service.getServiceUrl() + service.getUrl() + id);
  }

  @Test
  public void shouldThrowExceptionIfThereIsOtherProblemWithFindingById() {
    // given
    Class<T> definition = getResultClass();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String exceptionMessage = String
        .format(EXCEPTION_METHOD_FORMAT, definition.getSimpleName(), status.value());

    // when
    exception.expect(DataRetrievalException.class);
    exception.expectMessage(exceptionMessage);
    mockRequestFail(status);

    service.findOne(UUID.randomUUID());
  }

  @Test
  public void shouldFindAllResources() {
    // when
    T dto = mockArrayResponseEntityAndGetDto();
    List<T> found = service.findAll();

    // then
    assertThat(found, hasItem(dto));

    verifyArrayRequest()
        .isGetRequest()
        .hasAuthHeader()
        .hasEmptyBody()
        .isUriStartsWith(service.getServiceUrl() + service.getUrl());
  }

  @Test
  public void shouldThrowExceptionIfThereIsProblemWithFindingAllResources() {
    // given
    Class<T> definition = getResultClass();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String exceptionMessage = String
        .format(EXCEPTION_METHOD_FORMAT, definition.getSimpleName(), status.value());

    // when
    exception.expect(DataRetrievalException.class);
    exception.expectMessage(exceptionMessage);
    mockRequestFail(status);

    service.findAll();
  }

  @Test
  public void shouldGetPage() {
    // given
    ResourceCommunicationService<T> service = getService();

    // when
    T instance = mockPageResponseEntityAndGetDto();
    Page<T> found = service.getPage("", RequestParameters.init());

    // then
    assertThat(found, hasItem(instance));

    verifyPageRequest()
        .isGetRequest()
        .hasAuthHeader()
        .hasEmptyBody()
        .isUriStartsWith(service.getServiceUrl() + service.getUrl());
  }

  @Test
  public void shouldGetPagePost() {
    // given
    ResourceCommunicationService<T> service = getService();
    String payload = "param=value";

    // when
    T instance = mockPageResponseEntityAndGetDto();
    Page<T> found = service.getPage("", RequestParameters.init(), payload);

    // then
    assertThat(found, hasItem(instance));

    verifyPageRequest()
        .isPostRequest()
        .hasAuthHeader()
        .hasBody(payload)
        .isUriStartsWith(service.getServiceUrl() + service.getUrl());
  }

  @Test
  public void shouldThrowExceptionIfThereIsProblemWithGettingPage() {
    // given
    Class<T> definition = getResultClass();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String exceptionMessage = String
        .format(EXCEPTION_METHOD_FORMAT, definition.getSimpleName(), status.value());

    // when
    exception.expect(DataRetrievalException.class);
    exception.expectMessage(exceptionMessage);
    mockPageRequestFail(status);

    service.getPage("", RequestParameters.init());
  }

  @Test
  public void shouldThrowExceptionIfThereIsProblemWithGettingPagePost() {
    // given
    Class<T> definition = getResultClass();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String exceptionMessage = String
        .format(EXCEPTION_METHOD_FORMAT, definition.getSimpleName(), status.value());

    // when
    exception.expect(DataRetrievalException.class);
    exception.expectMessage(exceptionMessage);
    mockPageRequestFail(status);

    service.getPage("", RequestParameters.init(), "param=value");
  }

}
