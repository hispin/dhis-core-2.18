package org.hisp.dhis.gis.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.gis.GISConfiguration;
import org.hisp.dhis.gis.GISConfigurationStore;
import org.springframework.transaction.annotation.Transactional;

public class HibernateGISConfigurationStore
    implements GISConfigurationStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Transactional
    public void add( String arg0, String arg1 )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( new GISConfiguration( arg0, arg1 ) );
    }

    @Transactional
    public void delete( String key )
    {
        Session session = sessionFactory.getCurrentSession();

        session.createQuery( "delete GISConfiguration as f where f.key = ?" ).setEntity( 0, key );
    }

    @Transactional
    public GISConfiguration get( String key )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( GISConfiguration.class );
        criteria.add( Restrictions.eq( "key", key ) );

        return (GISConfiguration) criteria.uniqueResult();
    }

    @Transactional
    @SuppressWarnings( "unchecked" )
    public Collection<GISConfiguration> getALL()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( GISConfiguration.class );

        return criteria.list();
    }

    @Transactional
    public String getValue( String key )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( GISConfiguration.class );
        criteria.add( Restrictions.eq( "key", key ) );
        
        GISConfiguration gisConfiguration = (GISConfiguration) criteria.uniqueResult();
        if(gisConfiguration==null) return null;
        return gisConfiguration.getValue();
    }

    @Transactional
    public void update( String key, String value )
    {
        GISConfiguration gisConfiguration = get( key );

        gisConfiguration.setValue( value );

        Session session = sessionFactory.getCurrentSession();

        session.update( gisConfiguration );
    }
}
