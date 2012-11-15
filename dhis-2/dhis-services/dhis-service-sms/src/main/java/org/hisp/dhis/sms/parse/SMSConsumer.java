package org.hisp.dhis.sms.parse;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.broker.BrokerService;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.springframework.beans.factory.InitializingBean;

// IEatSMS
public class SMSConsumer implements InitializingBean {

    private ParserManager parserManager;

    private ConnectionFactory factory;

    private String brokerURL;

    private String queue;

    SMSConsumerThread thread;

    public void start() {
        if (thread == null) {
            thread = new SMSConsumerThread();
            thread.start();
        }
    }

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

    public void setParserManager(ParserManager parserManager) {
        this.parserManager = parserManager;
    }

    public void stop() {
        thread.stopFetching();
        thread = null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setPersistent(true);
        broker.addConnector(brokerURL);
        broker.start();
    }

    private class SMSConsumerThread extends Thread {
        private boolean stop;

        public void run() {
            while (!stop) {
                try{
                    fetchAndParseSMS();
                }catch(Exception e){
                    // ignore 
                }
                try {
                    // Maybe we should speed up on successful receive?
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /* Returns true when a message is found */
        private void fetchAndParseSMS() {

            try {
                Connection connection = factory.createConnection();
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue destination = session.createQueue(queue);
                MessageConsumer c = session.createConsumer(destination);

                // Wait the maximum time of one second
                Message m = c.receive(1000);
                while (m != null) {
                    if (m instanceof ObjectMessage) {
                        ObjectMessage objMessage = (ObjectMessage) m;
                        if (objMessage.getObject() instanceof IncomingSms) {
                            IncomingSms sms = (IncomingSms) objMessage.getObject();
                            parserManager.parse(sms);
                        }
                    }
                    m = c.receive(1000);
                }

                connection.close();
                session.close();


            } catch (JMSException e) {
                e.printStackTrace();

            }
        }

        public void stopFetching() {
            this.stop = true;
        }

    }

}
