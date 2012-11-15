package org.hisp.dhis.sms.parse;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.hisp.dhis.sms.incoming.IncomingSms;

public class SMSPublisher {
    private ConnectionFactory factory;

    private String queue;

    public void putObject(IncomingSms sms) throws JMSException {
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        ObjectMessage objMessage = session.createObjectMessage();
        objMessage.setObject(sms);

        Queue destination = session.createQueue(queue);
        MessageProducer producer = session.createProducer(destination);

        producer.send(objMessage);

        session.close();
        connection.close();
    }

    public void setFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

}
