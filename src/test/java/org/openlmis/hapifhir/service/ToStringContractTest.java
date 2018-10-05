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

import be.joengenduvel.java.verifiers.ToStringVerifier;
import java.util.Optional;
import org.junit.Test;

public abstract class ToStringContractTest<T> extends EqualsContractTest<T> {

  @Test
  public void shouldImplementToString() {
    Class<T> definition = getTestClass();
    T instance = getInstance().orElseGet(() -> {
      try {
        return definition.newInstance();
      } catch (ReflectiveOperationException exp) {
        throw new IllegalStateException(exp);
      }
    });

    ToStringVerifier<T> verifier = ToStringVerifier
        .forClass(definition)
        .ignore("$jacocoData");// external library is checking for this field, has to be ignored

    prepare(verifier);
    verifier.containsAllPrivateFields(instance);
  }

  protected Optional<T> getInstance() {
    return Optional.empty();
  }

  protected void prepare(ToStringVerifier<T> verifier) {
    // nothing to do here
  }

}
