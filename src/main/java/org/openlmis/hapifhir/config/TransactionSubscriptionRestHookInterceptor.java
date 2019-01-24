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

import ca.uhn.fhir.jpa.subscription.resthook.SubscriptionDeliveringRestHookSubscriber;
import ca.uhn.fhir.jpa.subscription.resthook.SubscriptionRestHookInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionSubscriptionRestHookInterceptor extends SubscriptionRestHookInterceptor {
  private static final Logger LOGGER = LoggerFactory
      .getLogger(TransactionSubscriptionRestHookInterceptor.class);

  private SubscriptionDeliveringRestHookSubscriber deliverySubscriber;
  private PlatformTransactionManager transactionManager;

  TransactionSubscriptionRestHookInterceptor(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
    LOGGER.trace("Created TransactionSubscriptionRestHookInterceptor instance");
  }

  @Override
  protected void registerDeliverySubscriber() {
    if (null == deliverySubscriber) {
      deliverySubscriber = new TransactionSubscriptionDeliveringRestHookSubscriber(
          getSubscriptionDao(), getChannelType(), this, transactionManager);
      LOGGER.trace("Initialized delivery subscriber");
    }

    LOGGER.debug("Subscribing to delivery channel");
    getDeliveryChannel().subscribe(deliverySubscriber);
    LOGGER.debug("Subscribed to delivery channel");
  }

  @Override
  protected void unregisterDeliverySubscriber() {
    LOGGER.debug("Unsubscribing to delivery channel");
    getDeliveryChannel().unsubscribe(deliverySubscriber);
    LOGGER.debug("Unsubscribed to delivery channel");

  }
}
