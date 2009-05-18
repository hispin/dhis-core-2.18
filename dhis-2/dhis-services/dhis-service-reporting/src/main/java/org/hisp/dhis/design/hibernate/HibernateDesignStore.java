package org.hisp.dhis.design.hibernate;

import java.util.Collection;

import org.hisp.dhis.design.Design;
import org.hisp.dhis.design.DesignStore;
import org.hisp.dhis.hibernate.HibernateSessionManager;

public class HibernateDesignStore
    implements DesignStore
{
    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }
    
    public int saveDesign( Design design )
    {
        return (Integer) sessionManager.getCurrentSession().save( design );
    }
    
    public void updateDesign( Design design )
    {
        sessionManager.getCurrentSession().update( design );
    }
    
    public Design getDesign( int id )
    {
        return (Design) sessionManager.getCurrentSession().get( Design.class, id );
    }
    
    public void deleteDesign( Design design )
    {
        sessionManager.getCurrentSession().delete( design );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Design> getAllDesigns()
    {
        return sessionManager.getCurrentSession().createCriteria( Design.class ).list();
    }
}
