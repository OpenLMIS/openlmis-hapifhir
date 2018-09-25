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

package org.openlmis.hapifhir.i18n;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Locale;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public class MessageTest {

  @Test(expected = NullPointerException.class)
  public void messageShouldRequireNonNullKey() {
    new Message(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void messageShouldRequireNonEmptyKey() {
    new Message(" ");
  }

  @Test(expected = NoSuchMessageException.class)
  public void humanStringShouldThrowExceptionIfKeyNotFound() {
    MessageSource messageSource = Mockito.mock(MessageSource.class);
    Locale locale = Locale.getDefault();

    String key = "foo.bar";
    String p1 = "some";
    String p2 = "stuff";
    Message msg = new Message("foo.bar", "some", "stuff");

    when(messageSource.getMessage(key, new Object[]{p1, p2}, locale))
        .thenThrow(NoSuchMessageException.class);
    msg.localMessage(messageSource, locale);
  }

  @Test
  public void toStringShouldHandleObjects() {
    String key = "key.something";
    Date today = new Date();
    Message message = new Message(key, "a", today);

    // expected is:  "key.something: a, <date>"
    assertEquals(key + ": " + "a" + ", " + today.toString(), message.toString());
  }

  @Test
  public void equalsAndHashCodeShouldUseKey() {
    Message foo1 = new Message("foo");
    Message foo2 = new Message("foo");
    assert foo1.equals(foo2);
    assert foo2.equals(foo1);
    assert foo1.hashCode() == foo2.hashCode();
  }

  @Test
  public void equalsAndHashCodeShouldIgnoreSpace() {
    Message foo1 = new Message("Foo");
    Message foo2 = new Message(" Foo ");
    assert foo1.equals(foo2);
    assert foo2.equals(foo1);
    assert foo1.hashCode() == foo2.hashCode();
  }
}
