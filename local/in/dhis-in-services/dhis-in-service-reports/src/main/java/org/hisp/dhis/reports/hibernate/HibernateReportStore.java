package org.hisp.dhis.reports.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.ReportStore;
import org.hisp.dhis.source.Source;

public class HibernateReportStore
    implements ReportStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    // -------------------------------------------------------------------------
    // Report_in
    // -------------------------------------------------------------------------

    public int addReport( Report_in report )
    {
        PeriodType periodType = periodStore.getPeriodType( report.getPeriodType().getClass() );

        report.setPeriodType( periodType );

        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( report );
    }

    public void deleteReport( Report_in report )
    {
        Session session = sessionManager.getCurrentSession();

        session.delete( report );
    }

    public void updateReport( Report_in report )
    {
        PeriodType periodType = periodStore.getPeriodType( report.getPeriodType().getClass() );

        report.setPeriodType( periodType );

        Session session = sessionManager.getCurrentSession();

        session.update( report );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getAllReports()
    {
        Session session = sessionManager.getCurrentSession();

        return session.createCriteria( Report_in.class ).list();
    }

    public Report_in getReport( int id )
    {
        Session session = sessionManager.getCurrentSession();

        return (Report_in) session.get( Report_in.class, id );
    }

    public Report_in getReportByName( String name )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (Report_in) criteria.uniqueResult();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportBySource( Source source )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportsByPeriodAndReportType( PeriodType periodType, String reportType )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );

        PeriodType selPeriodType = periodStore.getPeriodType( periodType.getClass() );
        criteria.add( Restrictions.eq( "periodType", selPeriodType ) );
        
        criteria.add( Restrictions.eq( "reportType", reportType ) );

        return criteria.list();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportsByPeriodType( PeriodType periodType )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );

        PeriodType selPeriodType = periodStore.getPeriodType( periodType.getClass() );
        criteria.add( Restrictions.eq( "periodType", selPeriodType ) );

        return criteria.list();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportsByReportType( String reportType )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );
        criteria.add( Restrictions.eq( "reportType", reportType ) );

        return criteria.list();

    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportsByPeriodSourceAndReportType( PeriodType periodType, Source source, String reportType )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );
        
        PeriodType selPeriodType = periodStore.getPeriodType( periodType.getClass() );
        criteria.add( Restrictions.eq( "periodType", selPeriodType ) );
        
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );

        criteria.add( Restrictions.eq( "reportType", reportType ) );

        return criteria.list();

    }

}
