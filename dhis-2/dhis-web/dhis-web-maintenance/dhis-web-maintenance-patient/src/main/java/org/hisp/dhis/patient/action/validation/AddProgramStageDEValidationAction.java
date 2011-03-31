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

package org.hisp.dhis.patient.action.validation;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageDataElementValidation;
import org.hisp.dhis.program.ProgramStageDataElementValidationService;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version AddProgramStageDataElementValidation.java May 6, 2010 1:28:06 PM
 */
public class AddProgramStageDEValidationAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageDataElementValidationService validationService;

    private ProgramStageService programStageService;

    private DataElementService dataElementService;

    private ProgramStageDataElementService programStageDataElementService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String description;

    private Integer leftProgramStageId;

    private Integer leftDataElementId;

    private int operator;

    private Integer rightProgramStageId;

    private Integer rightDataElementId;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setValidationService( ProgramStageDataElementValidationService validationService )
    {
        this.validationService = validationService;
    }

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setLeftProgramStageId( Integer leftProgramStageId )
    {
        this.leftProgramStageId = leftProgramStageId;
    }

    public void setLeftDataElementId( Integer leftDataElementId )
    {
        this.leftDataElementId = leftDataElementId;
    }

    public void setRightProgramStageId( Integer rightProgramStageId )
    {
        this.rightProgramStageId = rightProgramStageId;
    }

    public void setRightDataElementId( Integer rightDataElementId )
    {
        this.rightDataElementId = rightDataElementId;
    }

    public void setOperator( int operator )
    {
        this.operator = operator;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        ProgramStageDataElementValidation validation = new ProgramStageDataElementValidation();

        validation.setDescription( description );

        // ---------------------------------------------------------------------
        // leftProgramStageDataElement
        // ---------------------------------------------------------------------

        ProgramStage programStage = programStageService.getProgramStage( leftProgramStageId );

        DataElement dataElement = dataElementService.getDataElement( leftDataElementId );

        ProgramStageDataElement leftProgramStageDataElement = programStageDataElementService.get( programStage,
            dataElement );

        if ( leftProgramStageDataElement == null )
        {
            leftProgramStageDataElement = new ProgramStageDataElement( programStage, dataElement, false );
        }

        validation.setLeftProgramStageDataElement( leftProgramStageDataElement );

        // ---------------------------------------------------------------------
        // rightProgramStageDataElement
        // ---------------------------------------------------------------------

        programStage = programStageService.getProgramStage( rightProgramStageId );

        dataElement = dataElementService.getDataElement( rightDataElementId );

        ProgramStageDataElement rightProgramStageDataElement = programStageDataElementService.get( programStage,
            dataElement );

        if ( rightProgramStageDataElement == null )
        {
            rightProgramStageDataElement = new ProgramStageDataElement( programStage, dataElement, false );
        }

        validation.setRightProgramStageDataElement( rightProgramStageDataElement );

        // ---------------------------------------------------------------------
        // Operator
        // ---------------------------------------------------------------------
        
        validation.setOperator( operator );

        validationService.saveProgramStageDataElementValidation( validation );

        return SUCCESS;
    }
}
