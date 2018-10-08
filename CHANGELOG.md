1.0.0 / WIP
==================

Released openlmis-hapifhir 1.0.0 as part of openlmis-ref-distro 3.5. This was the first stable release of openlmis-hapifhir.

Features:
* [OLMIS-5382](https://openlmis.atlassian.net/browse/OLMIS-5382): Create a HAPI FHIR microservice.
* [OLMIS-5385](https://openlmis.atlassian.net/browse/OLMIS-5385): Supports only service-based or API key tokens
* [OLMIS-5415](https://openlmis.atlassian.net/browse/OLMIS-5415): Syncing data with OpenLMIS reference data
  * location with the AREA as the physical type will be converted to a geographic zone
  * location with the SI as the physical type will be converted to a facility
  * if OpenLMIS's resource exists, it will be updated