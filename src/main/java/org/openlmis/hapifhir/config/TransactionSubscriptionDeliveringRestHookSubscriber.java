package org.openlmis.hapifhir.config;

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.subscription.BaseSubscriptionInterceptor;
import ca.uhn.fhir.jpa.subscription.resthook.SubscriptionDeliveringRestHookSubscriber;
import org.hl7.fhir.r4.model.Subscription.SubscriptionChannelType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

class TransactionSubscriptionDeliveringRestHookSubscriber
    extends SubscriptionDeliveringRestHookSubscriber {

  private PlatformTransactionManager transactionManager;

  TransactionSubscriptionDeliveringRestHookSubscriber(IFhirResourceDao<?> subscriptionDao,
      SubscriptionChannelType channelType, BaseSubscriptionInterceptor subscriptionInterceptor,
      PlatformTransactionManager transactionManager) {
    super(subscriptionDao, channelType, subscriptionInterceptor);
    this.transactionManager = transactionManager;
  }

  @Override
  public void handleMessage(Message<?> message) throws MessagingException {
    // workaround for the problem which has been described in the following link:
    // https://groups.google.com/forum/#!topic/hapi-fhir/Hm2I3UPACCw
    TransactionDefinition definition = new DefaultTransactionDefinition();
    TransactionStatus transaction = transactionManager.getTransaction(definition);

    try {
      super.handleMessage(message);

      if (!transaction.isCompleted() && !transaction.isRollbackOnly()) {
        transactionManager.commit(transaction);
      }
    } catch (Exception exp) {
      if (!transaction.isCompleted()) {
        transactionManager.rollback(transaction);
      }

      throw exp;
    }
  }
}
