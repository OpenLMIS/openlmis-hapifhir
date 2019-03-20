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

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.messaging.Message;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionSubscriptionDeliveringRestHookSubscriberTest {

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();

  @Mock
  private PlatformTransactionManager transactionManager;

  @Mock
  private TransactionStatus transactionStatus;

  @Mock
  private Message<?> message;

  private TransactionSubscriptionDeliveringRestHookSubscriber subscriber;

  @Before
  public void setUp() {
    subscriber = spy(new TransactionSubscriptionDeliveringRestHookSubscriber(transactionManager));

    willDoNothing().given(subscriber).parentHandleMessage(message);
    given(transactionManager.getTransaction(any(DefaultTransactionDefinition.class)))
        .willReturn(transactionStatus);
  }

  @Test
  public void shouldHandleMessageAndCommitTransaction() {
    // given
    given(transactionStatus.isCompleted()).willReturn(false);

    // when
    subscriber.handleMessage(message);

    // then
    verify(transactionManager).commit(transactionStatus);
    verify(transactionManager, never()).rollback(transactionStatus);
  }

  @Test
  public void shouldHandleMessageAndNotCommitTransactionIfItIsCompleted() {
    // given
    given(transactionStatus.isCompleted()).willReturn(true);

    // when
    subscriber.handleMessage(message);

    // then
    verify(transactionManager, never()).commit(transactionStatus);
    verify(transactionManager, never()).rollback(transactionStatus);
  }

  @Test
  public void shouldHandleMessageAndNotCommitTransactionIfItItRollbackOnly() {
    // given
    given(transactionStatus.isRollbackOnly()).willReturn(true);

    // when
    subscriber.handleMessage(message);

    // then
    verify(transactionManager, never()).commit(transactionStatus);
    verify(transactionManager, never()).rollback(transactionStatus);
  }

  @Test
  public void shouldHandleMessageAndRollbackTransactionOnException() {
    // given
    willThrow(new RuntimeException()).given(subscriber).parentHandleMessage(message);
    given(transactionStatus.isCompleted()).willReturn(false);

    try {
      // when
      subscriber.handleMessage(message);
      fail("Subscriber should throw exception");
    } catch (RuntimeException e) {
      // then
      verify(transactionManager).rollback(transactionStatus);
      verify(transactionManager, never()).commit(transactionStatus);
    }
  }

  @Test
  public void shouldHandleMessageAndNotRollbackTransactionIfItIsCompleted() {
    // given
    willThrow(new RuntimeException()).given(subscriber).parentHandleMessage(message);
    given(transactionStatus.isCompleted()).willReturn(true);

    try {
      // when
      subscriber.handleMessage(message);
      fail("Subscriber should throw exception");
    } catch (RuntimeException e) {
      // then
      verify(transactionManager, never()).rollback(transactionStatus);
      verify(transactionManager, never()).commit(transactionStatus);
    }
  }
}
