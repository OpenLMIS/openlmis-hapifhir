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

package org.openlmis.hapifhir.repository.impl;

import ca.uhn.fhir.jpa.dao.data.IResourceSearchViewDao;
import ca.uhn.fhir.jpa.entity.ResourceSearchView;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Primary
@Component
public class IResourceSearchViewDaoImpl
    extends SimpleJpaRepository<ResourceSearchView, Long>
    implements IResourceSearchViewDao {

  private static final String NATIVE_QUERY = "SELECT"
      + "   h.pid as pid,"
      + "   h.res_id as res_id,"
      + "   h.res_type as res_type,"
      + "   h.res_version as res_version,"
      + "   h.res_ver as res_ver,"
      + "   h.has_tags as has_tags,"
      + "   h.res_deleted_at as res_deleted_at,"
      + "   h.res_published as res_published,"
      + "   h.res_updated as res_updated,"
      + "   h.res_text as res_text,"
      + "   h.res_encoding as res_encoding,"
      + "   f.forced_id as forced_pid"
      + " FROM"
      + "   ${schema}.HFJ_RES_VER h"
      + "   LEFT OUTER JOIN ${schema}.HFJ_FORCED_ID f ON f.resource_pid = h.res_id"
      + "   INNER JOIN ${schema}.HFJ_RESOURCE r ON r.res_id = h.res_id and r.res_ver = h.res_ver"
      + " WHERE"
      + "   h.res_id IN (${pidList})";

  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String defaultSchema;

  private EntityManager entityManager;

  @Autowired
  public IResourceSearchViewDaoImpl(EntityManager entityManager) {
    super(ResourceSearchView.class, entityManager);
    this.entityManager = entityManager;
  }

  @Override
  public Collection<ResourceSearchView> findByResourceIds(Collection<Long> pids) {
    if (CollectionUtils.isEmpty(pids)) {
      return Collections.emptyList();
    }

    String pidList = pids
        .stream()
        .map(Objects::toString)
        .collect(Collectors.joining(", "));

    Map<String, String> valueMap = new HashMap<>();
    valueMap.put("schema", defaultSchema);
    valueMap.put("pidList", pidList);

    StrSubstitutor sub = new StrSubstitutor(valueMap);
    String query = sub.replace(NATIVE_QUERY);

    return entityManager
        .createNativeQuery(query, ResourceSearchView.class)
        .getResultList();
  }

}
