package org.hisp.dhis.survey.state;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultSelectedStateManager.java 5282 2008-05-28 10:41:06Z
 *          larshelg $
 */
public class DefaultSelectedStateManager
    implements SelectedStateManager
{

    public static final String SESSION_KEY_SELECTED_SURVEY_ID = "data_entry_selected_survey_id";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SurveyService surveyService;
    
    public void setSurveyService( SurveyService surveyService )
    {
        this.surveyService = surveyService;
    }
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // SelectedStateManager implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    public OrganisationUnit getSelectedOrganisationUnit()
    {
        return selectionManager.getSelectedOrganisationUnit();
    }

    // -------------------------------------------------------------------------
    // Survey
    // -------------------------------------------------------------------------

    public void setSelectedSurvey( Survey survey )
    {
        getSession().put( SESSION_KEY_SELECTED_SURVEY_ID, survey.getId() );
    }

    public Survey getSelectedSurvey()
    {
        Integer id = (Integer) getSession().get( SESSION_KEY_SELECTED_SURVEY_ID );

        return id != null ? surveyService.getSurvey( id ) : null;
    }

    public void clearSelectedSurvey()
    {
        getSession().remove( SESSION_KEY_SELECTED_SURVEY_ID );
    }

    public List<Survey> loadSurveysForSelectedOrgUnit( OrganisationUnit organisationUnit )
    {
        List<Survey> surveys = new ArrayList<Survey>( surveyService.getSurveysBySource( organisationUnit ));

        return surveys;
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private static final Map<Object, Object> getSession()
    {
        return ActionContext.getContext().getSession();
    }

 
   

   
}
