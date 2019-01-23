package org.openlmis.hapifhir.config;


import ca.uhn.fhir.jpa.subscription.resthook.SubscriptionDeliveringRestHookSubscriber;
import ca.uhn.fhir.jpa.subscription.resthook.SubscriptionRestHookInterceptor;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionSubscriptionRestHookInterceptor extends SubscriptionRestHookInterceptor {

  private SubscriptionDeliveringRestHookSubscriber deliverySubscriber;

  TransactionSubscriptionRestHookInterceptor(PlatformTransactionManager transactionManager) {
    deliverySubscriber = new TransactionSubscriptionDeliveringRestHookSubscriber(
        getSubscriptionDao(), getChannelType(), this, transactionManager);
  }

  @Override
  protected void registerDeliverySubscriber() {
    getDeliveryChannel().subscribe(deliverySubscriber);
  }

  @Override
  protected void unregisterDeliverySubscriber() {
    getDeliveryChannel().unsubscribe(deliverySubscriber);
  }
}
