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

package org.openlmis.hapifhir.i18n;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.interceptor.ExceptionHandlingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.openlmis.hapifhir.i18n.Message.LocalizedMessage;
import org.openlmis.hapifhir.service.ExternalApiException;
import org.openlmis.hapifhir.service.LocalizedMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageExceptionHandlingInterceptor extends InterceptorAdapter {

  @Autowired
  private MessageService messageService;

  private ExceptionHandlingInterceptor delegate;

  @Autowired
  public MessageExceptionHandlingInterceptor(MessageService messageService) {
    this(messageService, new ExceptionHandlingInterceptor());
  }

  MessageExceptionHandlingInterceptor(MessageService messageService,
      ExceptionHandlingInterceptor delegate) {
    this.messageService = messageService;
    this.delegate = delegate;
  }

  @Override
  public BaseServerResponseException preProcessOutgoingException(RequestDetails details,
      Throwable throwable, HttpServletRequest request) throws ServletException {

    Throwable newThrowable = throwable;

    if (newThrowable instanceof BaseMessageException) {
      BaseMessageException messageException = (BaseMessageException) newThrowable;

      Message message = messageException.asMessage();
      LocalizedMessage localize = messageService.localize(message);

      newThrowable = new LocalizedMessageException(messageException, localize);
    }

    if (newThrowable instanceof ExternalApiException) {
      ExternalApiException externalApiException = (ExternalApiException) newThrowable;

      LocalizedMessageDto message = externalApiException.getMessageLocalized();
      newThrowable = new LocalizedMessageException(HttpStatus.SC_BAD_REQUEST, message.asMessage());
    }

    return delegate.preProcessOutgoingException(details, newThrowable, request);
  }

  @Override
  public boolean handleException(RequestDetails details, BaseServerResponseException exception,
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    return delegate.handleException(details, exception, request, response);
  }

  static final class LocalizedMessageException extends BaseServerResponseException {

    LocalizedMessageException(BaseMessageException exception, LocalizedMessage localize) {
      this(exception.getStatusCode(), localize.asMessage());
    }

    LocalizedMessageException(int statusCode, String message) {
      super(statusCode, message);
    }
  }

}
