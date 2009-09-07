package org.hisp.dhis.config.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.config.ConfigurationStore;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.hibernate.HibernateSessionManager;

public class HibernateConfigurationStore
    implements ConfigurationStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }

    // -------------------------------------------------------------------------
    // Config
    // -------------------------------------------------------------------------
    
    public int addConfiguration( Configuration_IN con )
    {        
        Session session = sessionManager.getCurrentSession();
        
        return (Integer) session.save( con );        
    }
    
    public void updateConfiguration( Configuration_IN con )
    {
        Session session = sessionManager.getCurrentSession();
        
        session.update( con );
    }

    public void deleteConfiguration( Configuration_IN con )
    {
        Session session = sessionManager.getCurrentSession();
        
        session.delete( con );
    }
    
    public Configuration_IN getConfiguration( int id )
    {
        Session session = sessionManager.getCurrentSession();

        return (Configuration_IN) session.get( Configuration_IN.class, id );       
    }
    
    public Configuration_IN getConfigurationByKey( String ckey )
    {        
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Configuration_IN.class );
        criteria.add( Restrictions.eq( "key", ckey ) );

        return (Configuration_IN) criteria.uniqueResult();
    }
    
}
