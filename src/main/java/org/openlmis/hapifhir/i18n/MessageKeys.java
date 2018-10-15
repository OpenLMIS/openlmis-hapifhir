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

import java.util.Arrays;

public abstract class MessageKeys {
  private static final String DELIMITER = ".";

  private static final String SERVICE_PREFIX = "hapifhir";

  private static final String ERROR = "error";

  private static final String AUTHORIZATION = "authorization";
  private static final String IO = "io";
  private static final String GEOGRAPHIC_ZONE = "geographicZone";
  private static final String FACILITY = "facility";

  private static final String LEVEL = "level";
  private static final String CODE = "code";
  private static final String FACILITY_TYPE = "facilityType";

  private static final String MISSING = "missing";
  private static final String INCORRECT = "incorrect";
  private static final String NOT_FOUND = "notFound";
  private static final String REQUIRED = "required";

  private static final String ERROR_PREFIX = join(SERVICE_PREFIX, ERROR);

  public static final String MISSING_AUTHORIZATION = join(ERROR_PREFIX, AUTHORIZATION, MISSING);
  public static final String INCORRECT_AUTHORIZATION = join(ERROR_PREFIX, AUTHORIZATION, INCORRECT);

  public static final String ERROR_IO = join(ERROR_PREFIX, IO);

  public static final String ERROR_FACILITY_CODE_REQUIRED =
      join(ERROR_PREFIX, FACILITY, CODE, REQUIRED);
  public static final String ERROR_NOT_FOUND_FACILITY_TYPE =
      join(ERROR_PREFIX, FACILITY, FACILITY_TYPE, NOT_FOUND);

  public static final String ERROR_NOT_FOUND_GEO_ZONE =
      join(ERROR_PREFIX, GEOGRAPHIC_ZONE, NOT_FOUND);
  public static final String ERROR_GEO_ZONE_CODE_REQUIRED =
      join(ERROR_PREFIX, GEOGRAPHIC_ZONE, CODE, REQUIRED);
  public static final String ERROR_NOT_FOUND_GEO_LEVEL =
      join(ERROR_PREFIX, GEOGRAPHIC_ZONE, LEVEL, NOT_FOUND);

  private MessageKeys() {
    throw new UnsupportedOperationException();
  }

  private static String join(String... params) {
    return String.join(DELIMITER, Arrays.asList(params));
  }
}
