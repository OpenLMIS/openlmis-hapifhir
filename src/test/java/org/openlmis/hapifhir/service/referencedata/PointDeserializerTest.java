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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Test;

public class PointDeserializerTest {

  private ObjectMapper mapper;
  private PointDeserializer pointDeserializer;

  private String json = "{ \"coordinates\": [10.0,-10.0] }";

  @Before
  public void setup() {
    mapper = new ObjectMapper();
    pointDeserializer = new PointDeserializer();
  }

  @Test
  public void deserializeShouldReturnNullIfCoordinatesAreNotFound() throws IOException {
    json = "{ \"key\": \"value\" }";
    assertNull(deserializePoint(json));
  }

  @Test
  public void deserializeShouldReturnNullIfCoordinatesAreFoundButNotAnArray() throws IOException {
    json = "{ \"coordinates\": \"10.0\" }";
    assertNull(deserializePoint(json));
  }

  @Test
  public void deserializeShouldDeserializeIfCoordinatesAreFoundAndAnArray() throws IOException {
    Point point = deserializePoint(json);

    assertEquals(10.0, point.getX(), 0.01);
    assertEquals(-10.0, point.getY(), 0.01);
  }

  private Point deserializePoint(String json) throws IOException {
    InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    JsonParser parser = mapper.getFactory().createParser(stream);
    DeserializationContext ctxt = mapper.getDeserializationContext();

    return pointDeserializer.deserialize(parser, ctxt);
  }
}