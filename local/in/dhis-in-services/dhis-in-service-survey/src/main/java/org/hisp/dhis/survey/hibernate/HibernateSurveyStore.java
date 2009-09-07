/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.survey.hibernate;

import java.util.Collection;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyStore;

/**
 * @author Brajesh Murari
 * @version $Id$
 */

public class HibernateSurveyStore implements SurveyStore
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
    // Survey
    // -------------------------------------------------------------------------


    public int addSurvey( Survey survey )
    {
        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( survey );
    }

    
    public void deleteSurvey( Survey survey )
    {
        Session session = sessionManager.getCurrentSession();

        session.delete( survey );       
    }

    @SuppressWarnings("unchecked")
    public Collection<Survey> getAllSurveys()
    {
        Session session = sessionManager.getCurrentSession();

        return session.createCriteria( Survey.class ).list();
    }

    public Survey getSurvey( int id )
    {
        Session session = sessionManager.getCurrentSession();

        return (Survey) session.get( Survey.class, id );
    }

    public Survey getSurveyByName( String name )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Survey.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (Survey) criteria.uniqueResult();
    }

    public Survey getSurveyByShortName( String shortName )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Survey.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );

        return (Survey) criteria.uniqueResult();
    }

  
    @SuppressWarnings("unchecked")
    public Collection<Survey> getSurveysBySource( Source source )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Survey.class );
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );

        return criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    public Collection<Survey> getSurveysByIndicator( Indicator indicator )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Survey.class );
        criteria.createAlias( "indicators", "i" );
        criteria.add( Restrictions.eq( "i.id", indicator.getId() ) );

        return criteria.list();
    }

    public void updateSurvey( Survey survey )
    {
        Session session = sessionManager.getCurrentSession();

        session.update( survey );
        
    }

 }
