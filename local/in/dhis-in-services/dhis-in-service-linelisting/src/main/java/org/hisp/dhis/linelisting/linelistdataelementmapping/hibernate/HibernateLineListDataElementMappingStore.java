package org.hisp.dhis.linelisting.linelistdataelementmapping.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.linelisting.linelistdataelementmapping.LineListDataElementMapping;
import org.hisp.dhis.linelisting.linelistdataelementmapping.LineListDataElementMappingStore;

public class HibernateLineListDataElementMappingStore
    implements LineListDataElementMappingStore
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
    // Line List and Data Element Mapping
    // -------------------------------------------------------------------------

    public int addLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionFactory.getCurrentSession();
        
        return (Integer) session.save( lineListDataElementMapping );
    }

    public void deleteLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionFactory.getCurrentSession();
        
        session.delete( lineListDataElementMapping );
    }

    @SuppressWarnings("unchecked")
    public Collection<LineListDataElementMapping> getAllLineListDataElementMappings()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createCriteria( LineListDataElementMapping.class ).list();
    }

    public LineListDataElementMapping getLineListDataElementMapping( int id )
    {
        Session session = sessionFactory.getCurrentSession();
        
        return (LineListDataElementMapping) session.get( LineListDataElementMapping.class, id );
    }

    public void updateLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        Session session = sessionFactory.getCurrentSession();
        
        session.update( lineListDataElementMapping );
    }
}
