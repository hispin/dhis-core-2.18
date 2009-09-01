package org.hisp.dhis.vn.chr.hibernate;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormReport;
import org.hisp.dhis.vn.chr.FormReportStore;

public class HibernateFormReportStore
    implements FormReportStore
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private HibernateSessionManager hibernateSessionManager;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------
    public void setHibernateSessionManager( HibernateSessionManager hibernateSessionManager )
    {
        this.hibernateSessionManager = hibernateSessionManager;
    }

    // -----------------------------------------------------------------------------------------------
    // Implements
    // -----------------------------------------------------------------------------------------------

    public int addFormReport( FormReport formReport )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        return (Integer) session.save( formReport );
    }

    public void updateFormReport( FormReport formReport )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        session.update( formReport );

    }

    public void deleteFormReport( int id )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        session.delete( getFormReport( id ) );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<FormReport> getAllFormReports()
    {

        Session session = hibernateSessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( FormReport.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<FormReport> getFormReports( Form form )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( FormReport.class );

        criteria.add( Restrictions.eq( "Form", form ) );

        return criteria.list();
    }

    public FormReport getFormReport( int id )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        return (FormReport) session.get( FormReport.class, id );
    }

}
