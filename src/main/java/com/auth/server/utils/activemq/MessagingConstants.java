/*
 *  Copyright 2014 Unicommerce eSolutions (P) Limited . All Rights Reserved.
 *  UNICOMMERCE ESOLUTIONS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *  
 *  @version     1.0, 06-Jan-2014
 *  @author parijat
 */
package com.auth.server.utils.activemq;

/**
 * @author parijat
 */
public final class MessagingConstants {

    public static final String MESSAGE_IDENTIFIER_KEY = "messageIdentifier";

    public enum Queue {
        TRANSFER_PRICE_RECONCILIATION_QUEUE(false, true),
        EMAIL_MESSAGE_QUEUE(false, true),
        INVOICE_QUEUE(false, true),
        SMS_MESSAGE_QUEUE(false, true),
        CACHE_DIRTY(true, false),
        IMPORT_QUEUE(false, false),
        EXPORT_QUEUE(false, false),
        EXPORT_SMALL_COUNT_QUEUE(false, false),
        EXPORT_LARGE_COUNT_QUEUE(false, false),
        TENANT_QUEUE(false, false),
        ORDER_SYNC_QUEUE(false, false),
        RECONCILIATION_INVOICE_QUEUE(false, false),
        RECONCILIATION_INVOICE_RESYNC_QUEUE(false, false),
        INVENTORY_SYNC_QUEUE(false, false),
        CATALOG_SYNC_QUEUE(false, false),
        STOMP_MESSAGES_TOPIC(true, false),
        PRICE_SYNC_QUEUE(false, false),
        RECOMMENDATION_MESSAGE_QUEUE(false, true),
        AVAILABILITY_QUEUE(false, true),
        REGISTRATION_QUEUE(false, true),
        SERVICABILITY_CHANGE(false, true),
        JOB_QUEUE(false, false),
        CHANNEL_WAREHOUSE_INVENTORY_SYNC_QUEUE(false,false),
        LOGGING_QUEUE(false, false),
        RESCHEDULE_JOB(true, false),
        GST_EINVOICE_QUEUE(false, true),
        GLOBAL_INVOICE_QUEUE(false, true);


        private final boolean topic;
        private final boolean global;

        Queue(boolean topic, boolean global) {
            this.topic = topic;
            this.global = global;
        }

        public boolean isTopic() {
            return topic;
        }

        public boolean isGlobal() {
            return global;
        }
    }

}
