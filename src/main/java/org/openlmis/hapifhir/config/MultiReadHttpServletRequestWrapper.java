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

package org.openlmis.hapifhir.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class MultiReadHttpServletRequestWrapper extends HttpServletRequestWrapper {

  private final XLogger logger = XLoggerFactory.getXLogger(getClass());

  private final String body;

  MultiReadHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);

    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader;

    try (InputStream inputStream = request.getInputStream()) {
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        char[] charBuffer = new char[128];
        int bytesRead;

        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      }
    } catch (Exception exc) {
      logger.warn("Could not read the request body, exception = {}", exc);
    }

    body = stringBuilder.toString();
  }

  @Override
  public ServletInputStream getInputStream() {
    return new StringServletInputStream(body);
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new StringReader(body));
  }

  public String getBody() {
    return body;
  }
}
