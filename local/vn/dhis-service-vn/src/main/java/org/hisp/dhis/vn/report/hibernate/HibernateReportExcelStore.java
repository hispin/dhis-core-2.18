/*
 * Copyright (c) 2004-2007, University of Oslo
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
package org.hisp.dhis.vn.report.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.vn.report.ReportExcel;
import org.hisp.dhis.vn.report.ReportExcelInterface;
import org.hisp.dhis.vn.report.ReportExcelNormal;
import org.hisp.dhis.vn.report.ReportExcelStore;
import org.hisp.dhis.vn.report.ReportItem;

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

    // --------------------------------------
    // Service of Report
    // --------------------------------------

    public int addReport( ReportExcelInterface report )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( report );
    }

    public void updateReport( ReportExcelInterface report )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( report );
    }

    public void deleteReport( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( getReport( id ) );
    }

    public ReportExcelInterface getReport( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (ReportExcelInterface) session.get( ReportExcel.class, id );
    }

    public ReportExcelInterface getReport( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        criteria.add( Restrictions.eq( "name", name ) );

        return (ReportExcelInterface) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportExcelInterface> getReportsByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        criteria.createAlias( "organisationAssocitions", "o" );

        criteria.add( Restrictions.eq( "o.id", organisationUnit.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportExcelInterface> getALLReport()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        return criteria.list();

    }
    
    @SuppressWarnings("unchecked")
	public Collection<String> getReportGroups(){
    	  Session session = sessionFactory.getCurrentSession();
          SQLQuery sqlQuery = session
              .createSQLQuery( "select DISTINCT(reportgroup) from reportexcel" );

          return sqlQuery.list(); 	
    }
    
    
    @SuppressWarnings("unchecked")
    public Collection<ReportExcelInterface> getReportsByGroup(String group){
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        criteria.add( Restrictions.eq( "group", group ) );

        return criteria.list();
    }

    // --------------------------------------
    // Service of Report Item
    // --------------------------------------

    public void addReportItem( ReportItem reportItem )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( reportItem );

    }

    public void updateReportItem( ReportItem reportItem )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( reportItem );

    }

    public void deleteReportItem( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( getReportItem( id ) );

    }

    public ReportItem getReportItem( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (ReportItem) session.get( ReportItem.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportItem> getALLReportItem()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportItem.class );

        return criteria.list();
    }

    public ReportItem getReportItem( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportItem.class );

        criteria.add( Restrictions.eq( "name", name ) );

        return (ReportItem) criteria.uniqueResult();
    }

    public Collection<ReportItem> getReportItem( String arg0, ReportExcelNormal arg1 )
    {
        return null;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ReportItem> getReportItem( int sheetNo, Integer reportId )
    {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery( "SELECT * from reportitem where reportitem.sheetno=" + sheetNo
            + " and reportitem.reportid=" + reportId.intValue() );
        sqlQuery.addEntity( ReportItem.class );
        return sqlQuery.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Integer> getSheets( Integer reportId )
    {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session
            .createSQLQuery( "select DISTINCT(sheetno) from reportitem where reportitem.reportid="
                + reportId.intValue() );

        return sqlQuery.list();
    }
}
