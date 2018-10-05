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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public class CustomSortDeserializer extends JsonDeserializer<Sort> {

  @Override
  public Sort deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ArrayNode node = parser.getCodec().readTree(parser);
    List<Order> orders = Lists.newArrayList();

    for (JsonNode obj : node) {
      String directionAsString = obj.get("direction").asText();
      Direction direction = Direction.valueOf(directionAsString);

      String property = obj.get("property").asText();
      orders.add(new Sort.Order(direction, property));
    }

    return new Sort(orders);
  }
}
