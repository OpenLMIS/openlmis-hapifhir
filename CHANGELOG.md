2.0.1 / 2021-10-29
==================
Improvement:
* [OLMIS-6983](https://openlmis.atlassian.net/browse/OLMIS-6983): Sonar analysis and contract tests runs only for snapshots

* Breaking changes:
* [OLMIS-7568](https://openlmis.atlassian.net/browse/OLMIS-7568): Use postgres v12

2.0.0 / 2019-05-27
==================

Breaking changes:
* [OLMIS-6066](https://openlmis.atlassian.net/browse/OLMIS-6066): Changed HAPI FHIR version to 3.7.0 and change structure to r4

Bug fixes, security and performance improvements, also backwards-compatible:
* [OLMIS-3773](https://openlmis.atlassian.net/browse/OLMIS-3773): Adjusted the service to use the new version of facility search endpoint
* [OLMIS-4531](https://openlmis.atlassian.net/browse/OLMIS-4531): Added compressing HTTP POST responses.
* [OLMIS-6307](https://openlmis.atlassian.net/browse/OLMIS-6307): Fixed the pagination links given in responses.
* [OLMIS-6245](https://openlmis.atlassian.net/browse/OLMIS-6245): Enabled in-memory subscription searching.

1.0.0 / 2018-12-12
==================

Released openlmis-hapifhir 1.0.0 as part of openlmis-ref-distro 3.5. This was the first stable release of openlmis-hapifhir.

Features:
* [OLMIS-5382](https://openlmis.atlassian.net/browse/OLMIS-5382): Create a HAPI FHIR microservice.
* [OLMIS-5385](https://openlmis.atlassian.net/browse/OLMIS-5385): Supports only service-based or API key tokens
* [OLMIS-5415](https://openlmis.atlassian.net/browse/OLMIS-5415): Syncing data with OpenLMIS reference data
  * location with the AREA as the physical type will be converted to a geographic zone
  * location with the SI as the physical type will be converted to a facility
  * if OpenLMIS's resource exists, it will be updated
