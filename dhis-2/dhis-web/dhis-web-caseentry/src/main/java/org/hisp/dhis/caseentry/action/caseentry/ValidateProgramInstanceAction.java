/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.caseentry.action.caseentry;

import static org.hisp.dhis.program.ProgramValidation.AFTER_CURRENT_DATE;
import static org.hisp.dhis.program.ProgramValidation.AFTER_DUE_DATE;
import static org.hisp.dhis.program.ProgramValidation.AFTER_OR_EQUALS_TO_CURRENT_DATE;
import static org.hisp.dhis.program.ProgramValidation.AFTER_OR_EQUALS_TO_DUE_DATE;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_CURRENT_DATE;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_DUE_DATE;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_DUE_DATE_PLUS_OR_MINUS_MAX_DAYS;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_OR_EQUALS_TO_CURRENT_DATE;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_OR_EQUALS_TO_DUE_DATE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramValidation;
import org.hisp.dhis.program.ProgramValidationResult;
import org.hisp.dhis.program.ProgramValidationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ ValidateProgramInstanceAction.java Apr 28, 2011 10:56:10 AM $
 */
public class ValidateProgramInstanceAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    private ProgramValidationService programValidationService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private I18n i18n;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<DataElement, String> resultDEMultiStages;

    private List<ProgramValidationResult> programValidationResults;

    private Map<Integer, String> leftsideFormulaMap;

    private Map<Integer, String> rightsideFormulaMap;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public Map<Integer, String> getLeftsideFormulaMap()
    {
        return leftsideFormulaMap;
    }

    public Map<Integer, String> getRightsideFormulaMap()
    {
        return rightsideFormulaMap;
    }

    public List<ProgramValidationResult> getProgramValidationResults()
    {
        return programValidationResults;
    }

    public void setProgramValidationService( ProgramValidationService programValidationService )
    {
        this.programValidationService = programValidationService;
    }
    
    public Map<DataElement, String> getResultDEMultiStages()
    {
        return resultDEMultiStages;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        resultDEMultiStages = new HashMap<DataElement, String>();

        programValidationResults = new ArrayList<ProgramValidationResult>();

        // ---------------------------------------------------------------------
        // Get selected objects
        // ---------------------------------------------------------------------

        ProgramStageInstance programStageInstance = selectedStateManager.getSelectedProgramStageInstance();
        
        // ---------------------------------------------------------------------
        // Check validations for dataelement into multi-stages
        // ---------------------------------------------------------------------

        runProgramValidation( programValidationService.getProgramValidation( programStageInstance.getProgramStage() ),
            programStageInstance );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Support method
    // -------------------------------------------------------------------------

    private void runProgramValidation( Collection<ProgramValidation> validations,
        ProgramStageInstance programStageInstance )
    {
        if ( validations != null )
        {
            for ( ProgramValidation validation : validations )
            {
                ProgramValidationResult validationResult = programValidationService.runValidation( validation, programStageInstance, format );

                if ( validationResult != null )
                {
                    programValidationResults.add( validationResult );
                }
            }
        }

        if ( !programValidationResults.isEmpty() )
        {
            leftsideFormulaMap = new HashMap<Integer, String>( programValidationResults.size() );
            rightsideFormulaMap = new HashMap<Integer, String>( programValidationResults.size() );

            for ( ProgramValidationResult validationResult : programValidationResults )
            {
                leftsideFormulaMap.put( validationResult.getProgramValidation().getId(),
                    programValidationService.getValidationDescription( validationResult.getProgramValidation().getLeftSide() ) );

                if ( validationResult.getProgramValidation().getDateType() )
                {
                    String rightSide = validationResult.getProgramValidation().getRightSide();
                    int index = rightSide.indexOf( 'D' );
                    if ( index < 0 )
                    {
                        int rightValidation = Integer.parseInt( rightSide );

                        switch ( rightValidation )
                        {
                        case BEFORE_CURRENT_DATE:
                            rightsideFormulaMap.put( validationResult.getProgramValidation().getId(), i18n.getString( "before_current_date" ) );
                            break;
                        case BEFORE_OR_EQUALS_TO_CURRENT_DATE:
                            rightsideFormulaMap.put( validationResult.getProgramValidation().getId(),
                                i18n.getString( "before_or_equals_to_current_date" ) );
                            break;
                        case AFTER_CURRENT_DATE:
                            rightsideFormulaMap.put( validationResult.getProgramValidation().getId(), i18n.getString( "after_current_date" ) );
                            break;
                        case AFTER_OR_EQUALS_TO_CURRENT_DATE:
                            rightsideFormulaMap.put( validationResult.getProgramValidation().getId(),
                                i18n.getString( "after_or_equals_to_current_date" ) );
                            break;
                        case BEFORE_DUE_DATE:
                            rightsideFormulaMap.put( validationResult.getProgramValidation().getId(), i18n.getString( "before_due_date" ) );
                            break;
                        case BEFORE_OR_EQUALS_TO_DUE_DATE:
                            rightsideFormulaMap.put( validationResult.getProgramValidation().getId(),
                                i18n.getString( "before_or_equals_to_due_date" ) );
                            break;
                        case AFTER_DUE_DATE:
                            rightsideFormulaMap.put( validationResult.getProgramValidation().getId(), i18n.getString( "after_due_date" ) );
                            break;
                        case AFTER_OR_EQUALS_TO_DUE_DATE:
                            rightsideFormulaMap
                                .put( validationResult.getProgramValidation().getId(), i18n.getString( "after_or_equals_to_due_date" ) );
                            break;
                        default:
                            rightsideFormulaMap.put( validationResult.getProgramValidation().getId(), "" );
                            break;

                        }
                    }
                    else
                    {
                        int rightValidation = Integer.parseInt( rightSide.substring( 0, index ) );

                        int daysValue = Integer.parseInt( rightSide.substring( index + 1, rightSide.length() ) );

                        if ( rightValidation == BEFORE_DUE_DATE_PLUS_OR_MINUS_MAX_DAYS )
                        {
                            rightsideFormulaMap.put(
                                validationResult.getProgramValidation().getId(),
                                i18n.getString( "in_range_due_date_plus_or_minus" ) + " " + daysValue
                                    + i18n.getString( "days" ) );
                        }
                    }
                }
                else if ( validationResult.getProgramValidation().getRightSide().equals( "1==1" ) )
                {
                    rightsideFormulaMap.put( validationResult.getProgramValidation().getId(), "" );
                }
                else
                {
                    rightsideFormulaMap.put( validationResult.getProgramValidation().getId(),
                        programValidationService.getValidationDescription( validationResult.getProgramValidation().getRightSide() ) );
                }
            }
        }
    }
}
