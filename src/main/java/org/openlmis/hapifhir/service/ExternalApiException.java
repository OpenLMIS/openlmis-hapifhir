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

/**
 * The following exception presents situations where there is a call to an external service and the
 * service returns response other than 200 OK.
 */
public class ExternalApiException extends RuntimeException {

  private final transient LocalizedMessageDto localizedMessage;

  ExternalApiException(Throwable cause, LocalizedMessageDto localizedMessage) {
    super(cause);
    this.localizedMessage = localizedMessage;
  }

  @Override
  public String getMessage() {
    return localizedMessage.toString();
  }

  public LocalizedMessageDto getMessageLocalized() {
    return localizedMessage;
  }

}
