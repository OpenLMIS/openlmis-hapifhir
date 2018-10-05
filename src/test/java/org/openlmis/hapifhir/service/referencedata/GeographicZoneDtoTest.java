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

package org.openlmis.hapifhir.service.referencedata;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.openlmis.hapifhir.DtoGenerator;
import org.openlmis.hapifhir.service.ToStringContractTest;

public class GeographicZoneDtoTest extends ToStringContractTest<GeographicZoneDto> {

  @Override
  protected Class<GeographicZoneDto> getTestClass() {
    return GeographicZoneDto.class;
  }

  @Override
  protected void prepare(EqualsVerifier<GeographicZoneDto> verifier) {
    GeometryFactory geometryFactory = new GeometryFactory();

    List<GeographicZoneDto> zones = DtoGenerator.of(GeographicZoneDto.class, 2);
    List<Polygon> polygons = Lists.newArrayList(
        geometryFactory.createPolygon(new Coordinate[]{
            new Coordinate(0, 0), new Coordinate(2, 0), new Coordinate(2, 2),
            new Coordinate(0, 2), new Coordinate(0, 0)}),
        geometryFactory.createPolygon(new Coordinate[]{
            new Coordinate(0, 0), new Coordinate(3, 0), new Coordinate(3, 3),
            new Coordinate(0, 3), new Coordinate(0, 0)})
    );

    verifier
        .withPrefabValues(GeographicZoneDto.class, zones.get(0), zones.get(1))
        .withPrefabValues(Polygon.class, polygons.get(0), polygons.get(1))
        .withRedefinedSuperclass();
  }

}
