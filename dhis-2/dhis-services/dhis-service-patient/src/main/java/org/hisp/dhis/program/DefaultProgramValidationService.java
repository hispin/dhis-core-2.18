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

package org.hisp.dhis.program;

import static org.hisp.dhis.program.ProgramValidation.OBJECT_PROGRAM_STAGE_DATAELEMENT;
import static org.hisp.dhis.program.ProgramValidation.SEPARATOR_ID;
import static org.hisp.dhis.program.ProgramValidation.SEPARATOR_OBJECT;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.nfunk.jep.JEP;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * @version $ DefaultProgramValidationService.java Apr 28, 2011 10:36:50 AM $
 */
@Transactional
public class DefaultProgramValidationService
    implements ProgramValidationService
{
    private ProgramValidationStore validationStore;

    private ProgramStageService programStageService;

    private DataElementService dataElementService;

    private ProgramStageInstanceService stageInstanceService;

    private PatientDataValueService valueService;

    private DataElementCategoryService categoryService;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setValidationStore( ProgramValidationStore validationStore )
    {
        this.validationStore = validationStore;
    }

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setStageInstanceService( ProgramStageInstanceService stageInstanceService )
    {
        this.stageInstanceService = stageInstanceService;
    }

    public void setValueService( PatientDataValueService valueService )
    {
        this.valueService = valueService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------
    @Override
    public int addProgramValidation( ProgramValidation programValidation )
    {
        return validationStore.save( programValidation );
    }

    @Override
    public void updateProgramValidation( ProgramValidation programValidation )
    {
        validationStore.update( programValidation );
    }

    @Override
    public void deleteProgramValidation( ProgramValidation programValidation )
    {
        validationStore.delete( programValidation );
    }

    @Override
    public Collection<ProgramValidation> getAllProgramValidation()
    {
        return validationStore.getAll();
    }

    @Override
    public ProgramValidation getProgramValidation( int id )
    {
        return validationStore.get( id );
    }

    @Override
    public boolean runValidation( ProgramValidation validation, ProgramInstance programInstance,
        OrganisationUnit orgunit )
    {
        // ---------------------------------------------------------------------
        // parse left-expressions
        // ---------------------------------------------------------------------

        boolean resultLeft = runExpression( validation.getLeftSide(), programInstance, orgunit );

        // ---------------------------------------------------------------------
        // parse right-expressions
        // ---------------------------------------------------------------------

        boolean resultRight = runExpression( validation.getRightSide(), programInstance, orgunit );

        return (resultLeft == resultRight);

    }

    public Collection<ProgramValidation> getProgramValidation( Program program )
    {
        return validationStore.get( program );
    }

    private boolean runExpression( String expression, ProgramInstance programInstance, OrganisationUnit orgunit )
    {
        final String regExp = "\\[" + OBJECT_PROGRAM_STAGE_DATAELEMENT + SEPARATOR_OBJECT + "([a-zA-Z0-9\\- ]+["
            + SEPARATOR_ID + "[0-9]*]*)" + "\\]";

        StringBuffer description = new StringBuffer();

        Pattern pattern = Pattern.compile( regExp );

        Matcher matcher = pattern.matcher( expression );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            int programStageId = Integer.parseInt( ids[0] );
            ProgramStage programStage = programStageService.getProgramStage( programStageId );

            int dataElementId = Integer.parseInt( ids[1] );
            DataElement dataElement = dataElementService.getDataElement( dataElementId );

            int optionComboId = Integer.parseInt( ids[2] );
            DataElementCategoryOptionCombo optionCombo = categoryService
                .getDataElementCategoryOptionCombo( optionComboId );

            ProgramStageInstance stageInstance = stageInstanceService.getProgramStageInstance( programInstance,
                programStage );

            PatientDataValue dataValue = valueService.getPatientDataValue( stageInstance, dataElement, optionCombo,
                orgunit );

            if ( dataValue == null )
            {
                return true;
            }

            matcher.appendReplacement( description, dataValue.getValue() );
        }

        matcher.appendTail( description );

        final JEP parser = new JEP();

        parser.parseExpression( description.toString() );

        return (parser.getValue() == 1.0);
    }
}
