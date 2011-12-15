package org.hisp.dhis.sms.outbound;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsStore;

public class HibernateOutboundSmsStore
    implements OutboundSmsStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int save( OutboundSms sms )
    {
        checkDate(sms);
        return (Integer) sessionFactory.getCurrentSession().save( sms );
    }
    
    private void checkDate( OutboundSms sms )
    {
        if (sms.getDate() == null) {
            sms.setDate( new Date() );
        }
        
    }

    @Override
    public OutboundSms get( int id) {
        Session session = sessionFactory.getCurrentSession();
        return (OutboundSms) session.get( OutboundSms.class, id );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<OutboundSms> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return (List<OutboundSms>) session.createCriteria( OutboundSms.class ).list();
    }
}
