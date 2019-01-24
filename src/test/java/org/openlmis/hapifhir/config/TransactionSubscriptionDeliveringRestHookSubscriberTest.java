package org.openlmis.hapifhir.config;

import static org.hl7.fhir.r4.model.Subscription.SubscriptionChannelType.RESTHOOK;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.subscription.BaseSubscriptionInterceptor;
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
  private IFhirResourceDao<?> subscriptionDao;

  @Mock
  private BaseSubscriptionInterceptor subscriptionInterceptor;

  @Mock
  private TransactionStatus transactionStatus;

  @Mock
  private Message<?> message;

  private TransactionSubscriptionDeliveringRestHookSubscriber subscriber;

  @Before
  public void setUp() {
    subscriber = spy(new TransactionSubscriptionDeliveringRestHookSubscriber(
        subscriptionDao, RESTHOOK, subscriptionInterceptor, transactionManager));

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
