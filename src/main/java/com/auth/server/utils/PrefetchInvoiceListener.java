package com.auth.server.utils;

import com.auth.server.security.jwt.JwtUtils;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class PrefetchInvoiceListener {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JwtUtils.class);
//    @Autowired
//    private FlipkartSmartService flipkartSmartService;

    @JmsListener(destination = "craete-job", concurrency = "${spring.activemq.craete-job.listener.concurrency:5}")
    public void prefetchInvoice(final Message message) throws JMSException {
        try {
            logger.info("Message Received:{}", message);
            boolean contextSuccess = setupContext(message);
            if (!contextSuccess) {
                logger.error("Invalid message context headers");
                return;
            }
            processPrefetchRequest(message);
        } finally {
//            FlipkartSmartRequestContext.destroy();
//            TenantRequestContext.destroy();
        }
    }

    private void processPrefetchRequest(Message message) throws JMSException {
        if (!(message instanceof TextMessage)) {
            logger.error("Message Received is not text message:{}", message);
            return;
        }
        TextMessage textMessage = (TextMessage) message;
        String messageText = textMessage.getText();
        logger.info("Message Text Received:{}", messageText);
        try {
            logger.info("messageText : {}",messageText);
//            CreateInvoiceRequest createInvoiceRequest = new Gson().fromJson(messageText, CreateInvoiceRequest.class);
//            flipkartSmartService.createInvoice(createInvoiceRequest);
        } catch (Exception e) {
            logger.error("Exception while processing message", e);
        }
    }

    private boolean setupContext(Message message) throws JMSException {
        return true;

    }
}