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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.subscription.BaseSubscriptionInterceptor;
import ca.uhn.fhir.jpa.subscription.CanonicalSubscription;
import ca.uhn.fhir.jpa.subscription.ResourceDeliveryMessage;
import ca.uhn.fhir.jpa.subscription.resthook.SubscriptionDeliveringRestHookSubscriber;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.client.interceptor.SimpleRequestHeaderInterceptor;
import java.util.List;
import org.hl7.fhir.r4.model.Subscription.SubscriptionChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

class TransactionSubscriptionDeliveringRestHookSubscriber
    extends SubscriptionDeliveringRestHookSubscriber {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(TransactionSubscriptionDeliveringRestHookSubscriber.class);

  private PlatformTransactionManager transactionManager;

  TransactionSubscriptionDeliveringRestHookSubscriber(IFhirResourceDao<?> subscriptionDao,
      SubscriptionChannelType channelType, BaseSubscriptionInterceptor subscriptionInterceptor,
      PlatformTransactionManager transactionManager) {
    super(subscriptionDao, channelType, subscriptionInterceptor);
    this.transactionManager = transactionManager;
    LOGGER.trace("Created TransactionSubscriptionDeliveringRestHookSubscriber instance");

  }

  @Override
  public void handleMessage(Message<?> message) {
    // workaround for the problem which has been described in the following link:
    // https://groups.google.com/forum/#!topic/hapi-fhir/Hm2I3UPACCw
    LOGGER.trace("Create transaction definition");
    TransactionDefinition definition = new DefaultTransactionDefinition();

    LOGGER.debug("Get/create transaction");
    TransactionStatus transaction = transactionManager.getTransaction(definition);

    try {
      LOGGER.debug("Handling message");
      super.handleMessage(message);

      if (!transaction.isCompleted() && !transaction.isRollbackOnly()) {
        LOGGER.debug("Commit transaction");
        transactionManager.commit(transaction);
      }

      LOGGER.debug("Handled message");
    } catch (Exception exp) {
      if (!transaction.isCompleted()) {
        LOGGER.debug("Rollback transaction");
        transactionManager.rollback(transaction);
      }

      LOGGER.error("Exception has been thrown while message has been handled", exp);
      throw exp;
    }
  }

  // This method is exactly the same as in the parent class. Added loggers to checks what happens.
  @Override
  public void handleMessage(ResourceDeliveryMessage theMessage) {
    CanonicalSubscription subscription = theMessage.getSubscription();

    // Grab the endpoint from the subscription
    String endpointUrl = subscription.getEndpointUrl();
    LOGGER.debug("Send request to {}", endpointUrl);

    // Grab the payload type (encoding mimetype) from the subscription
    String payloadString = subscription.getPayloadString();
    EncodingEnum payloadType = null;
    if (payloadString != null) {
      if (payloadString.contains(";")) {
        payloadString = payloadString.substring(0, payloadString.indexOf(';'));
      }
      payloadString = payloadString.trim();
      payloadType = EncodingEnum.forContentType(payloadString);
    }

    LOGGER.debug("Payload as string: {}", payloadString);
    LOGGER.debug("Payload type: {}", payloadType);

    // Create the client request
    getContext().getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
    IGenericClient client = null;
    if (isNotBlank(endpointUrl)) {
      LoggingInterceptor loggingInterceptor = new LoggingInterceptor(true);
      loggingInterceptor.setLogger(LOGGER);

      client = getContext().newRestfulGenericClient(endpointUrl);
      client.registerInterceptor(loggingInterceptor);
      LOGGER.debug("Created RESTful generic client for {}", endpointUrl);

      // Additional headers specified in the subscription
      List<String> headers = subscription.getHeaders();
      LOGGER.debug("Try to set the following headers {}", headers);

      for (String next : headers) {
        if (isNotBlank(next)) {
          LOGGER.debug("Set header: {}", next);
          client.registerInterceptor(new SimpleRequestHeaderInterceptor(next));
        }
      }
    }

    LOGGER.debug("Delivering payload");
    deliverPayload(theMessage, subscription, payloadType, client);
    LOGGER.debug("Delivered payload");
  }
}
