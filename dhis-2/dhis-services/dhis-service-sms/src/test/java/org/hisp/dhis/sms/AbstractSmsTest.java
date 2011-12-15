package org.hisp.dhis.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "classpath:/test-beans.xml" } )
@Transactional
public abstract class AbstractSmsTest
{

    @Autowired
    protected SessionFactory sessionFactory;

    protected void flush()
    {
        sessionFactory.getCurrentSession().flush();
    }

    protected void evict( Object o )
    {
        sessionFactory.getCurrentSession().evict( o );
    }

    protected OutboundSms getOutboundSms()
    {
        OutboundSms sms = new OutboundSms();
        sms.setMessage( "1" );
        Set<String> recipients = new HashSet<String>() {{ add("1"); add("2");}};
        sms.setRecipients( recipients  );
        return sms;
    }

    protected void verifySms( OutboundSms expected, OutboundSms actual )
    {
        assertNotNull(actual);
        assertNotNull( actual.getDate() );
        assertEquals( expected.getId(), actual.getId());
        assertEquals( expected.getMessage(), actual.getMessage() );
        assertEquals( expected.getRecipients(), actual.getRecipients() );
    }

    protected void assertNotNullSize( Collection<?> c, int i )
    {
        assertNotNull( c );
        assertEquals(i, c.size());
    }

}
