/*
*  Copyright 2015 Unicommerce eSolutions (P) Limited . All Rights Reserved.
*  UNICOMMERCE ESOLUTIONS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*  
*  @version     1.0, 04/08/15
*  @author sunny
*/

package com.auth.server.utils.activemq;

public interface IActiveMQConnector {
    <T> boolean produceMessage(MessagingConstants.Queue queue, T message, String messageGroupId);
}