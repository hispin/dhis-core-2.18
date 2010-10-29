package org.hisp.dhis.reportexcel.hibernate;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.util.Collection;
import java.util.HashSet;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.hisp.dhis.reportexcel.PeriodColumn;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelStore;
import org.hisp.dhis.reportexcel.status.DataEntryStatus;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class HibernateReportExcelStore
    implements ReportExcelStore
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // --------------------------------------
    // Service of Report
    // --------------------------------------

    public int addReportExcel( ReportExcel report )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( report );
    }

    public void updateReportExcel( ReportExcel report )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( report );
    }

    public void deleteReportExcel( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( getReportExcel( id ) );
    }

    public ReportExcel getReportExcel( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (ReportExcel) session.get( ReportExcel.class, id );
    }

    public ReportExcel getReportExcel( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        criteria.add( Restrictions.eq( "name", name ) );

        return (ReportExcel) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportExcel> getReportExcelsByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        criteria.createAlias( "organisationAssocitions", "o" );

        criteria.add( Restrictions.eq( "o.id", organisationUnit.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportExcel> getALLReportExcel()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        return criteria.list();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<String> getReportExcelGroups()
    {
        String sql;

        if ( currentUserService.currentUserIsSuper() )
        {
            sql = "SELECT DISTINCT(reportgroup) FROM reportexcels ";
        }
        else
        {
            sql = "SELECT DISTINCT(reportgroup) FROM reportexcel_userroles, reportexcels "
                + " WHERE reportexcels.reportexcelid=reportexcel_userroles.reportexcelid "
                + " AND reportexcel_userroles.userroleid IN ( "
                + " SELECT userrole.userroleid FROM userrole, userrolemembers " + " WHERE userrolemembers.userid="
                + currentUserService.getCurrentUser().getId() + " AND userrole.userroleid=userrolemembers.userroleid)";
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery( sql );

        return sqlQuery.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportExcel> getReportsByGroup( String group )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        criteria.add( Restrictions.eq( "group", group ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<String> getALLReportExcelTemplates()
    {
        Session session = sessionFactory.getCurrentSession();

        SQLQuery sqlQuery = session.createSQLQuery( "select DISTINCT(exceltemplate) from reportexcels" );

        return sqlQuery.list();
    }

    // --------------------------------------
    // Service of Report Item
    // --------------------------------------

    public void addReportExcelItem( ReportExcelItem reportItem )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( reportItem );
    }

    public void updateReportExcelItem( ReportExcelItem reportItem )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( reportItem );
    }

    public void deleteReportExcelItem( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( this.getReportExcelItem( id ) );
    }

    public ReportExcelItem getReportExcelItem( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (ReportExcelItem) session.get( ReportExcelItem.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportExcelItem> getALLReportExcelItem()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcelItem.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportExcelItem> getReportExcelItem( int sheetNo, Integer reportId )
    {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery( "SELECT * from reportexcel_items where reportexcel_items.sheetno="
            + sheetNo + " and reportexcel_items.reportexcelid=" + reportId.intValue() );
        sqlQuery.addEntity( ReportExcelItem.class );
        return sqlQuery.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Integer> getSheets( Integer reportId )
    {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session
            .createSQLQuery( "select DISTINCT(sheetno) from reportexcel_items where reportexcel_items.reportexcelid="
                + reportId.intValue() );

        return sqlQuery.list();
    }

    @Override
    public void deleteMultiReportExcelItem( Collection<Integer> ids )
    {
        String sql = "delete ReportExcelItem d where d.id in (:ids)";

        Query query = sessionFactory.getCurrentSession().createQuery( sql );
        query.setParameterList( "ids", ids );

        query.executeUpdate();
    }

    // --------------------------------------
    // Report DataElement Order
    // --------------------------------------

    public DataElementGroupOrder getDataElementGroupOrder( Integer id )
    {
        Session session = sessionFactory.getCurrentSession();
        return (DataElementGroupOrder) session.get( DataElementGroupOrder.class, id );
    }

    public void updateDataElementGroupOrder( DataElementGroupOrder dataElementGroupOrder )
    {
        Session session = sessionFactory.getCurrentSession();
        session.update( dataElementGroupOrder );
    }

    public void deleteDataElementGroupOrder( Integer id )
    {
        Session session = sessionFactory.getCurrentSession();
        session.delete( this.getDataElementGroupOrder( id ) );
    }

    // -------------------------------------------------
    // Data Entry Status
    // -------------------------------------------------

    public int countDataValueOfDataSet( DataSet dataSet, OrganisationUnit organisationUnit, Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        String sql = "select count(*) as c from datavalue where sourceid=" + organisationUnit.getId()
            + " and dataelementid in (";

        int i = 0;

        for ( DataElement element : dataSet.getDataElements() )
        {
            sql += element.getId();

            if ( i++ < dataSet.getDataElements().size() - 1 )
            {
                sql += ",";
            }
        }

        sql += ") and periodid=" + period.getId();

        SQLQuery query = session.createSQLQuery( sql );

        query.addScalar( "c", Hibernate.INTEGER );

        return Integer.valueOf( String.valueOf( query.uniqueResult() ) );

    }

    public void deleteDataEntryStatus( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( getDataEntryStatus( id ) );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getALLDataEntryStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );

        return criteria.list();
    }

    public DataEntryStatus getDataEntryStatus( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (DataEntryStatus) session.get( DataEntryStatus.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusDefault()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "makeDefault", true ) );
        return criteria.list();
    }

    public int saveDataEntryStatus( DataEntryStatus arg0 )
    {
        Session session = sessionFactory.getCurrentSession();
        return (Integer) session.save( arg0 );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusDefaultByDataSets( Collection<DataSet> dataSets )
    {
        Collection<DataEntryStatus> result = new HashSet<DataEntryStatus>();

        Session session = sessionFactory.getCurrentSession();

        for ( DataSet dataSet : dataSets )
        {
            Criteria criteria = session.createCriteria( DataEntryStatus.class );
            criteria.add( Restrictions.eq( "dataSet", dataSet ) );
            result.addAll( criteria.list() );
        }

        return result;
    }

    public void updateDataEntryStatus( DataEntryStatus arg0 )
    {
        Session session = sessionFactory.getCurrentSession();
        session.update( arg0 );
    }

    @Override
    public PeriodColumn getPeriodColumn( Integer id )
    {
        Session session = sessionFactory.getCurrentSession();
        return (PeriodColumn) session.get( PeriodColumn.class, id );
    }

    @Override
    public void updatePeriodColumn( PeriodColumn periodColumn )
    {
        Session session = sessionFactory.getCurrentSession();
        session.update( periodColumn );
    }

    @Transactional
    public void updateReportWithExcelTemplate( String curTemplateName, String newTemplateName )
    {
        Session session = sessionFactory.getCurrentSession();

        String hqlQuery = "update reportexcels set exceltemplate = :newName where exceltemplate = :curName";

        SQLQuery query = session.createSQLQuery( hqlQuery );

        query.setString( "newName", newTemplateName ).setString( "curName", curTemplateName );

        query.executeUpdate();

    }

}
