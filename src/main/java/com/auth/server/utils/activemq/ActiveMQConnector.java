/*
 *  Copyright 2013 Unicommerce eSolutions (P) Limited . All Rights Reserved.
 *  UNICOMMERCE ESOLUTIONS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *  
 *  @version     1.0, 10-Dec-2013
 *  @author parijat
 */
package com.auth.server.utils.activemq;

import com.auth.server.utils.json.JsonUtils;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq .ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQConnector implements IActiveMQConnector {

    private static final Logger           LOG        = LoggerFactory.getLogger(ActiveMQConnector.class);

    private PooledConnectionFactory factory    = null;

    private String                        clusterName;

    private Map<String, ActiveMQProducer> _producers = new ConcurrentHashMap<>();

    public ActiveMQConnector() {
    }

    public ActiveMQConnector(String jmsBrokerUserName, String jmsBrokerPassword, String activeMQBrokerURL, String clusterName) {
        this(jmsBrokerUserName, jmsBrokerPassword, activeMQBrokerURL, clusterName, 1);
    }

    public ActiveMQConnector(String jmsBrokerUserName, String jmsBrokerPassword, String activeMQBrokerURL, String clusterName, int maxConnections) {
        this(jmsBrokerUserName, jmsBrokerPassword, activeMQBrokerURL, clusterName, maxConnections, ActiveMQPrefetchPolicy.DEFAULT_QUEUE_PREFETCH, false);
    }

    public ActiveMQConnector(String jmsBrokerUserName, String jmsBrokerPassword, String activeMQBrokerURL, String clusterName, int maxConnections, int queuePrefetch,
                             boolean useAsyncSend) {
        this.clusterName = clusterName;
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(jmsBrokerUserName, jmsBrokerPassword, activeMQBrokerURL);
        activeMQConnectionFactory.setUseAsyncSend(useAsyncSend);
        ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
        activeMQPrefetchPolicy.setQueuePrefetch(queuePrefetch);
        activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy);
        factory = new PooledConnectionFactory(activeMQConnectionFactory);
        factory.setMaxConnections(maxConnections);
        LOG.info("Created connection factory for broker [" + activeMQBrokerURL + "] with maximum allowed connections [" + factory.getMaxConnections() + "]");
    }

    public ActiveMQConnector(String jmsBrokerUserName, String jmsBrokerPassword, String activeMQBrokerURL, int maxConnections, int queuePrefetch,
                             boolean useAsyncSend) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(jmsBrokerUserName, jmsBrokerPassword, activeMQBrokerURL);
        activeMQConnectionFactory.setUseAsyncSend(useAsyncSend);
        ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
        activeMQPrefetchPolicy.setQueuePrefetch(queuePrefetch);
        activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy);
        factory = new PooledConnectionFactory(activeMQConnectionFactory);
        factory.setMaxConnections(maxConnections);
        LOG.info("Created connection factory for broker [" + activeMQBrokerURL + "] with maximum allowed connections [" + factory.getMaxConnections() + "]");
    }

    private class ActiveMQProducer {
        private final Connection      connection;
        private final Session         session;
        private final MessageProducer messageProducer;

        public ActiveMQProducer(Session session, MessageProducer producer, Connection connection) {
            this.session = session;
            this.messageProducer = producer;
            this.connection = connection;
        }

        public Session getSession() {
            return session;
        }

        public MessageProducer getMessageProducer() {
            return messageProducer;
        }

        public Connection getConnection() {
            return connection;
        }
    }

    private ActiveMQProducer createMessageProducer(MessagingConstants.Queue queue, boolean isTransacted) {
        String queueName = queue.isGlobal() ? queue.name() : queue.name() + (StringUtils.isBlank(clusterName) ? "" : "-" + clusterName);
        try {
            LOG.info("Creating producer for queue [{}]", queueName);
            Connection connection = factory.createConnection();
            connection.start();
            Session session;
            MessageProducer producer;
            if (isTransacted) {
                session = connection.createSession(true, Session.SESSION_TRANSACTED);
                Destination destination = queue.isTopic() ? session.createTopic(queueName) : session.createQueue(queueName);
                producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            } else {
                LOG.info("Creating session");
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                LOG.info("Creating destination");
                Destination destination = queue.isTopic() ? session.createTopic(queueName) : session.createQueue(queueName);
                LOG.info("Creating producer");
                producer = session.createProducer(destination);
                LOG.info("Done Creating producer");
            }
            return new ActiveMQProducer(session, producer, connection);
        } catch (Exception e) {
            LOG.error("Error creating persistent JMS producer for queue " + queueName, e);
            throw new RuntimeException(e);
        }
    }

    private ActiveMQProducer getMessageProducer(MessagingConstants.Queue queue) {
        ActiveMQProducer producer = _producers.get(queue.name());
        if (producer == null) {
            synchronized (this) {
                producer = _producers.get(queue.name());
                if (producer == null) {
                    producer = createMessageProducer(queue, false);
                    _producers.put(queue.name(), producer);
                }
            }
        }
        return producer;
    }

    /**
     * Produces message with Log Routing Key but without tenant and facility
     * @param queue
     * @param message
     * @param logRoutingKey
     * @param <T>
     * @return
     */
    public <T> boolean produceContextAwareMessage(MessagingConstants.Queue queue, T message, String logRoutingKey) {
        UnifierMessage unifierMessage = new UnifierMessage();
        unifierMessage.setLogRoutingKey(logRoutingKey);
        unifierMessage.setObjectJson(JsonUtils.objectToString(message));
        return produceMessage(queue, unifierMessage, logRoutingKey);
    }

    public <T> boolean produceMessage(MessagingConstants.Queue queue, T message) {
        return produceMessage(queue, message, null);
    }

    public <T> boolean produceMessage(MessagingConstants.Queue queue, T message, String messageGroupId) {
        ActiveMQProducer producer = getMessageProducer(queue);
        try {
            sendMessage(producer, message, messageGroupId);
        } catch (Exception e) {
            //retry if session is closed.
            destroyMessageProducer(queue);
            producer = getMessageProducer(queue);
            try {
                sendMessage(producer, message, messageGroupId);
            } catch (JMSException jmse) {
                throw new RuntimeException(jmse);
            }
        }
        return true;
    }

    private void destroyMessageProducer(MessagingConstants.Queue queue) {
        ActiveMQProducer producer = _producers.remove(queue.name());
        try {
            producer.getMessageProducer().close();
        } catch (Exception e) {
            LOG.error("Unable to close message producer for queue [{}]", queue.name());
        }
        try {
            producer.getConnection().close();
        } catch (Exception e) {
            LOG.error("Unable to close connection for queue [{}]", queue.name());
        }
        try {
            producer.getSession().close();
        } catch (Exception e) {
            LOG.error("Unable to close session for queue [{}]", queue.name());
        }
    }


    public PooledConnectionFactory getPooledFactory(){
        return this.factory;
    }

    private <T> void sendMessage(ActiveMQProducer producer, T message, String messageGroupId) throws JMSException {
        TextMessage textMessage = producer.getSession().createTextMessage(JsonUtils.objectToString(message));
        if(StringUtils.isNotBlank(messageGroupId)) {
            textMessage.setStringProperty("JMSXGroupID", messageGroupId);
        }
        producer.getMessageProducer().send(textMessage);
    }

    public class UnifierMessage {
        private String tenantCode;
        private String facilityCode;
        private String logRoutingKey;
        private String requestIdentifier;
        private String objectJson;
        private Date   requestTimestamp;

        public String getObjectJson() {
            return objectJson;
        }

        public void setObjectJson(String objectJson) {
            this.objectJson = objectJson;
        }

        public String getTenantCode() {
            return tenantCode;
        }

        public void setTenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
        }

        public String getFacilityCode() {
            return facilityCode;
        }

        public void setFacilityCode(String facilityCode) {
            this.facilityCode = facilityCode;
        }

        public String getRequestIdentifier() {
            return requestIdentifier;
        }

        public void setRequestIdentifier(String requestIdentifier) {
            this.requestIdentifier = requestIdentifier;
        }

        public String getLogRoutingKey() {
            return logRoutingKey;
        }

        public void setLogRoutingKey(String logRoutingKey) {
            this.logRoutingKey = logRoutingKey;
        }

        public Date getRequestTimestamp() { return requestTimestamp; }

        public void setRequestTimestamp(Date requestTimestamp) { this.requestTimestamp = requestTimestamp; }

        @Override
        public String toString() {
            return "UnifierMessage{" + "tenantCode='" + tenantCode + '\'' + ", facilityCode='" + facilityCode + '\'' + ", logRoutingKey='" + logRoutingKey + '\'' + ", objectJson='"
                    + objectJson + '\'' + '}';
        }


    }
}
