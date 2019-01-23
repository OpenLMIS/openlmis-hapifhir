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
