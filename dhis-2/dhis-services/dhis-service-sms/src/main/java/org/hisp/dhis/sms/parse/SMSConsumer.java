package org.hisp.dhis.sms.parse;

import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.queue.MessageQueue;

// IEatSMS
public class SMSConsumer
{

    private ParserManager parserManager;

    private MessageQueue messageQueue;

    public void setMessageQueue( MessageQueue messageQueue )
    {
        this.messageQueue = messageQueue;
    }

    SMSConsumerThread thread;

    public void start()
    {
        messageQueue.initialize();
        if ( thread == null )
        {
            thread = new SMSConsumerThread();
            thread.start();
        }
    }

    public void setParserManager( ParserManager parserManager )
    {
        this.parserManager = parserManager;
    }

    public void stop()
    {
        thread.stopFetching();
        thread = null;
    }

    private class SMSConsumerThread
        extends Thread
    {
        private boolean stop;

        public void run()
        {
            while ( !stop )
            {
                try
                {
                    fetchAndParseSMS();
                }
                catch ( Exception e )
                {
                    // ignore
                }
                try
                {
                    Thread.sleep( 3000 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
        }

        private void fetchAndParseSMS()
        {
            IncomingSms message = messageQueue.get();
            while ( message != null )
            {
                parserManager.parse( message );
                messageQueue.remove( message );
                message = messageQueue.get();
            }
        }

        public void stopFetching()
        {
            this.stop = true;
        }

    }

}
