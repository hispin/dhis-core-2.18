package org.hisp.dhis.reportexcel.excelitem.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.excelitem.ExcelItem;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemStore;

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

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class HibernateExcelItemStore implements ExcelItemStore {

	// ----------------------------------------------------------------------
	// Dependencies
	// ----------------------------------------------------------------------

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	// ----------------------------------------------------------------------
	// ExcelItemStore implementation
	// ----------------------------------------------------------------------

	public void addExcelItem(ExcelItem excelItem) {

		sessionFactory.getCurrentSession().save(excelItem);
	}

	public void deleteExcelItem(int id) {

		Session session = sessionFactory.getCurrentSession();

		session.delete( getExcelItem(id));
	}

	@SuppressWarnings("unchecked")
	public Collection<ExcelItem> getAllExcelItem() {

		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(ExcelItem.class);

		return criteria.list();
	}

	public void updateExcelItem(ExcelItem excelItem) {

		Session session = sessionFactory.getCurrentSession();

		session.saveOrUpdate(excelItem);
	}
	
	public ExcelItem getExcelItem(int id){
		
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(ExcelItem.class);

		criteria.add(Restrictions.eq("id", id));

		return (ExcelItem)criteria.uniqueResult();
	}

	public void addExcelItemGroup(ExcelItemGroup excelItemGroup) {
		
		sessionFactory.getCurrentSession().save(excelItemGroup);
	}

	public void deleteExcelItemGroup(int id) {

		Session session = sessionFactory.getCurrentSession();

		session.delete( getExcelItemGroup(id));
	}

	@SuppressWarnings("unchecked")
	public Collection<ExcelItemGroup> getAllExcelItemGroup() {
		
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(ExcelItemGroup.class);

		return criteria.list();
	}

	public ExcelItemGroup getExcelItemGroup(int id) {

		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(ExcelItemGroup.class);

		criteria.add(Restrictions.eq("id", id));

		return (ExcelItemGroup)criteria.uniqueResult();
	}

	public void updateExcelItemGroup(ExcelItemGroup excelItemGroup) {
		
		Session session = sessionFactory.getCurrentSession();

		session.saveOrUpdate(excelItemGroup);

	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<ExcelItemGroup> getExcelItemGroupsByOrganisationUnit(
			OrganisationUnit organisationUnit) {
		
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ReportExcel.class );

        criteria.createAlias( "organisationAssocitions", "o" );

        criteria.add( Restrictions.eq( "o.id", organisationUnit.getId() ) );

        return criteria.list();
	}
}
