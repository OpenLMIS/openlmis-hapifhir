/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2018 VillageReach
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;

public class PointDeserializer extends JsonDeserializer<Point> {

  @Override
  public Point deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    JsonNode coordinatesNode = node.get("coordinates");
    if (null != coordinatesNode && coordinatesNode.isArray()) {
      ArrayNode coordinateArray = (ArrayNode) coordinatesNode;
      return new GeometryFactory().createPoint(new Coordinate(coordinateArray.get(0).doubleValue(),
              coordinateArray.get(1).doubleValue()));
    } else {
      return null;
    }
  }
}
