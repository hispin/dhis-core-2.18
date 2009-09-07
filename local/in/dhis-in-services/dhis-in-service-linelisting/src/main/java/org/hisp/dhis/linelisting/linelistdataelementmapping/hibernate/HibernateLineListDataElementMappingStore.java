package org.hisp.dhis.linelisting.linelistdataelementmapping.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.linelisting.linelistdataelementmapping.LineListDataElementMapping;
import org.hisp.dhis.linelisting.linelistdataelementmapping.LineListDataElementMappingStore;

public class HibernateLineListDataElementMappingStore
    implements LineListDataElementMappingStore
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
    // Line List and Data Element Mapping
    // -------------------------------------------------------------------------

    public int addLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionManager.getCurrentSession();
        
        return (Integer) session.save( lineListDataElementMapping );
    }

    public void deleteLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionManager.getCurrentSession();
        
        session.delete( lineListDataElementMapping );
    }

    @SuppressWarnings("unchecked")
    public Collection<LineListDataElementMapping> getAllLineListDataElementMappings()
    {
        Session session = sessionManager.getCurrentSession();
        
        return session.createCriteria( LineListDataElementMapping.class ).list();
    }

    public LineListDataElementMapping getLineListDataElementMapping( int id )
    {
        Session session = sessionManager.getCurrentSession();
        
        return (LineListDataElementMapping) session.get( LineListDataElementMapping.class, id );
    }

    public void updateLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionManager.getCurrentSession();
        
        session.update( lineListDataElementMapping );
    }

}
