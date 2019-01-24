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

package org.openlmis.hapifhir.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionSubscriptionRestHookInterceptorTest {

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();

  @Mock
  private PlatformTransactionManager transactionManager;

  @Mock
  private SubscribableChannel subscribableChannel;

  @Captor
  private ArgumentCaptor<MessageHandler> messageHandlerCaptor;

  private TransactionSubscriptionRestHookInterceptor interceptor;

  @Before
  public void setUp() {
    interceptor = new TransactionSubscriptionRestHookInterceptor(transactionManager);
    interceptor.setDeliveryChannel(subscribableChannel);
  }

  @Test
  public void shouldRegisterDeliverySubscriber() {
    // when
    interceptor.registerDeliverySubscriber();

    // then
    verify(subscribableChannel)
        .subscribe(any(TransactionSubscriptionDeliveringRestHookSubscriber.class));
  }

  @Test
  public void shouldNotRecreateDeliverySubscriber() {
    // when
    interceptor.registerDeliverySubscriber();
    interceptor.registerDeliverySubscriber();
    interceptor.registerDeliverySubscriber();

    // then
    verify(subscribableChannel, times(3)).subscribe(messageHandlerCaptor.capture());
    assertThat(Sets.newHashSet(messageHandlerCaptor.getAllValues())).hasSize(1);
  }

  @Test
  public void shouldUnregisterDeliverySubscriber() {
    // when
    interceptor.unregisterDeliverySubscriber();

    // then
    verify(subscribableChannel)
        .unsubscribe(any(TransactionSubscriptionDeliveringRestHookSubscriber.class));
  }
}
