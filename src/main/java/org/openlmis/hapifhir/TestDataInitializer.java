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

package org.openlmis.hapifhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.openlmis.hapifhir.service.LocationLoadingService;
import org.openlmis.hapifhir.service.VersionDto;
import org.openlmis.hapifhir.service.referencedata.ReferenceDataVersionService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("demo-data")
@Order(5)
public class TestDataInitializer implements CommandLineRunner {

  private final XLogger logger = XLoggerFactory.getXLogger(getClass());

  @Autowired
  LocationLoadingService locationLoadingService;
  
  @Autowired
  ReferenceDataVersionService referenceDataVersionService;
  
  /**
   * Initializes test data.
   *
   * @param args command line arguments
   */
  public void run(String... args) throws InterruptedException {
    logger.entry();

    while (true) {
      VersionDto version = null;
      try {
        version = referenceDataVersionService.getInfo();
      } catch (Exception exc) {
        logger.info("Reference data version not found, trying again");
      }
      if (null != version) {
        logger.info("Reference data version found, load FHIR demo data");
        break;
      }
      Thread.sleep(5000);
    }

    IGenericClient client = locationLoadingService.initialize();
    locationLoadingService.loadGeographicZones(client);
    locationLoadingService.loadFacilities(client);
    
    logger.exit();
  }
}
