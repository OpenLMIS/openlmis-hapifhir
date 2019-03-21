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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import ca.uhn.fhir.jpa.dao.data.IResourceSearchViewDao;
import ca.uhn.fhir.jpa.entity.ResourceSearchView;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

public class IResourceSearchViewDaoImplTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private EntityManager entityManager;

  @Mock
  private Metamodel metamodel;

  @Mock
  private EntityType<ResourceSearchView> managedType;

  @Mock
  private Query query;

  private IResourceSearchViewDao dao;

  @Before
  public void setUp() {
    given(entityManager.getMetamodel()).willReturn(metamodel);
    given(entityManager.getDelegate()).willReturn(entityManager);
    given(metamodel.managedType(ResourceSearchView.class)).willReturn(managedType);

    dao = new IResourceSearchViewDaoImpl(entityManager);
    ReflectionTestUtils.setField(dao, "defaultSchema", "hapifhir");

  }

  @Test
  public void shouldReturnEmptyListIfParameterIsEmpty() {
    assertThat(dao.findByResourceIds(Collections.emptyList())).isEmpty();
  }

  @Test
  public void shouldReturnDataForPids() {
    // given
    ResourceSearchView view = new ResourceSearchView();

    given(entityManager.createNativeQuery(anyString(), eq(ResourceSearchView.class)))
        .willReturn(query);
    given(query.getResultList()).willReturn(Lists.newArrayList(view));

    // when
    Collection<ResourceSearchView> list = dao.findByResourceIds(Collections.singleton(1L));

    // then
    assertThat(list)
        .hasSize(1)
        .contains(view);
  }
}
