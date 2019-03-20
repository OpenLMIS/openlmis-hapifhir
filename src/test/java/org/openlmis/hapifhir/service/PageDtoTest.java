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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageDtoTest extends ToStringContractTest<PageDto> {

  @Override
  protected Class<PageDto> getTestClass() {
    return PageDto.class;
  }

  @Test
  public void shouldCreateInstanceFromAnotherPage() {
    String value = RandomStringUtils.randomAlphanumeric(10);
    PageDto<String> pageDto = new PageDto<>(getPage(value));

    assertThat(pageDto.hasContent()).isTrue();
    assertThat(pageDto.hasNext()).isFalse();
    assertThat(pageDto.hasPrevious()).isFalse();
    assertThat(pageDto.nextPageable()).isNull();
    assertThat(pageDto.previousPageable()).isEqualTo(PageRequest.of(0, 10));

    Iterator<String> iterator = pageDto.iterator();
    assertThat(iterator.hasNext()).isTrue();
    assertThat(iterator.next()).isEqualTo(value);
    assertThat(iterator.hasNext()).isFalse();
  }

  @Test
  public void shouldConvertContentToOtherType() {
    Function<String, Integer> converter = Integer::valueOf;
    String value = "10";

    Page<Integer> pageDto = new PageDto<>(getPage(value)).map(converter);

    assertThat(pageDto.getContent())
        .isNotEmpty()
        .hasSize(1)
        .contains(10);
  }

  private Page<String> getPage(String value) {
    Pageable pageable = PageRequest.of(0, 10);

    List<String> values = Lists.newArrayList(value);

    return new PageImpl<>(values, pageable, 1);
  }
}
