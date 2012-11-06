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

import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_ID;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_OBJECT;
import static org.hisp.dhis.program.ProgramValidation.AFTER_CURRENT_DATE;
import static org.hisp.dhis.program.ProgramValidation.AFTER_DUE_DATE;
import static org.hisp.dhis.program.ProgramValidation.AFTER_OR_EQUALS_TO_CURRENT_DATE;
import static org.hisp.dhis.program.ProgramValidation.AFTER_OR_EQUALS_TO_DUE_DATE;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_CURRENT_DATE;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_DUE_DATE;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_DUE_DATE_PLUS_OR_MINUS_MAX_DAYS;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_OR_EQUALS_TO_CURRENT_DATE;
import static org.hisp.dhis.program.ProgramValidation.BEFORE_OR_EQUALS_TO_DUE_DATE;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
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
    private final String regExp = "\\[" + OBJECT_PROGRAM_STAGE_DATAELEMENT + SEPARATOR_OBJECT + "([a-zA-Z0-9\\- ]+["
        + SEPARATOR_ID + "[0-9]*]*)" + "\\]";

    private final String regExpComparator = "(<=|>=|==|!=|<|>|>)+";

    private final String SEPARATE_SIDE_VALUE = "&&";

    private final String INVALID_CONDITION = "Invalid condition";

    private ProgramValidationStore validationStore;

    private ProgramStageService programStageService;

    private DataElementService dataElementService;

    private PatientDataValueService valueService;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setValidationStore( ProgramValidationStore validationStore )
    {
        this.validationStore = validationStore;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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
    public ProgramValidationResult runValidation( ProgramValidation validation,
        ProgramStageInstance programStageInstance, I18nFormat format )
    {
        if ( validation.getDateType() )
        {
            return runDateExpression( validation, programStageInstance, format );
        }
        else
        {
            String resultLeft = runExpression( validation.getLeftSide(), programStageInstance );
            String resultRight = runExpression( validation.getRightSide(), programStageInstance );
            if ( resultLeft != null && resultRight != null )
            {
                boolean validLeftSide = Boolean.parseBoolean( resultLeft.split( SEPARATE_SIDE_VALUE )[0] );
                boolean validRightSide = Boolean.parseBoolean( resultRight.split( SEPARATE_SIDE_VALUE )[0] );
                if ( validLeftSide != validRightSide )
                {
                    return new ProgramValidationResult( programStageInstance, validation,
                        resultLeft.split( SEPARATE_SIDE_VALUE )[1], resultRight.split( SEPARATE_SIDE_VALUE )[1] );
                }
            }
        }

        return null;
    }

    public Collection<ProgramValidation> getProgramValidation( Program program )
    {
        return validationStore.get( program );
    }

    public Collection<ProgramValidation> getProgramValidation( Program program, Boolean dateType )
    {
        return validationStore.get( program, dateType );
    }

    public Collection<ProgramValidation> getProgramValidation( ProgramStageDataElement psdataElement )
    {
        Collection<ProgramValidation> programValidation = validationStore.get( psdataElement.getProgramStage()
            .getProgram() );

        Collection<ProgramValidation> result = new HashSet<ProgramValidation>();

        for ( ProgramValidation validation : programValidation )
        {
            Collection<DataElement> dataElements = getDataElementInExpression( validation );
            Collection<ProgramStage> programStages = getProgramStageInExpression( validation );

            if ( dataElements.contains( psdataElement.getDataElement() )
                && programStages.contains( psdataElement.getProgramStage() ) )
            {
                result.add( validation );
            }
        }

        return result;
    }

    private ProgramValidationResult runDateExpression( ProgramValidation programValidation,
        ProgramStageInstance programStageInstance, I18nFormat format )
    {
        boolean valid = true;

        Pattern pattern = Pattern.compile( regExp );
        Matcher matcher = pattern.matcher( programValidation.getLeftSide() );

        if ( matcher.find() )
        {
            String match = matcher.group();

            PatientDataValue dataValue = getPatientDataValue( match, programStageInstance );

            if ( dataValue != null )
            {

                String rightSide = programValidation.getRightSide();
                Date dueDate = dataValue.getProgramStageInstance().getDueDate();
                Date currentDate = dataValue.getTimestamp();
                Date value = format.parseDate( dataValue.getValue() );
                String operator = "";
                int daysValue = 0;

                int index = rightSide.indexOf( 'D' );
                if ( index < 0 )
                {
                    int rightValidation = Integer.parseInt( rightSide );

                    switch ( rightValidation )
                    {
                    case BEFORE_CURRENT_DATE:
                        operator = "<";
                        valid = value.before( currentDate );
                        break;
                    case BEFORE_OR_EQUALS_TO_CURRENT_DATE:
                        operator = "<=";
                        valid = (value.before( currentDate ) || value.equals( currentDate ));
                        break;
                    case AFTER_CURRENT_DATE:
                        operator = ">";
                        valid = value.after( currentDate );
                        break;
                    case AFTER_OR_EQUALS_TO_CURRENT_DATE:
                        operator = ">=";
                        valid = (value.after( currentDate ) || value.equals( currentDate ));
                        break;
                    case BEFORE_DUE_DATE:
                        operator = "<";
                        currentDate = dueDate;
                        valid = value.before( dueDate );
                        break;
                    case BEFORE_OR_EQUALS_TO_DUE_DATE:
                        operator = "<=";
                        currentDate = dueDate;
                        valid = (value.before( dueDate ) || value.equals( dueDate ));
                    case AFTER_DUE_DATE:
                        operator = ">";
                        currentDate = dueDate;
                        valid = value.after( dueDate );
                        break;
                    case AFTER_OR_EQUALS_TO_DUE_DATE:
                        operator = ">=";
                        currentDate = dueDate;
                        valid = (value.after( dueDate ) || value.equals( dueDate ));
                        break;
                    default:
                        break;
                    }
                }
                else
                {
                    int rightValidation = Integer.parseInt( rightSide.substring( 0, index ) );
                    daysValue = Integer.parseInt( rightSide.substring( index + 1, rightSide.length() ) );
                    if ( rightValidation == BEFORE_DUE_DATE_PLUS_OR_MINUS_MAX_DAYS )
                    {
                        long maxDays = dueDate.getTime() / 86400000 + daysValue;
                        long minDays = dueDate.getTime() / 86400000 - daysValue;
                        long valueDays = value.getTime() / 86400000;
                        valid = (valueDays <= maxDays && valueDays >= minDays);
                        operator = "=";
                    }
                }
                
                if ( !valid )
                {
                    String result = dataValue.getValue() + " " + operator + " " + format.formatDate( currentDate );
                    if( daysValue!=0 )
                    {
                        result += " +/- " + daysValue;
                    }
                    return new ProgramValidationResult( programStageInstance, programValidation, result, null );
                }
            }
        }

        return null;
    }

    public Collection<ProgramValidation> getProgramValidation( ProgramStage programStage )
    {
        Collection<ProgramValidation> programValidation = getProgramValidation( programStage.getProgram() );

        Iterator<ProgramValidation> iter = programValidation.iterator();

        Pattern pattern = Pattern.compile( regExp );

        while ( iter.hasNext() )
        {
            ProgramValidation validation = iter.next();

            String expression = validation.getLeftSide() + " " + validation.getRightSide();
            Matcher matcher = pattern.matcher( expression );

            boolean flag = false;
            while ( matcher.find() )
            {
                String match = matcher.group();
                match = match.replaceAll( "[\\[\\]]", "" );

                String[] info = match.split( SEPARATOR_OBJECT );
                String[] ids = info[1].split( SEPARATOR_ID );

                int programStageId = Integer.parseInt( ids[0] );

                if ( programStageId == programStage.getId() )
                {
                    flag = true;
                    break;
                }
            }

            if ( !flag )
            {
                iter.remove();
            }
        }

        return programValidation;
    }

    public String getValidationDescription( String condition )
    {
        StringBuffer description = new StringBuffer();

        Pattern patternCondition = Pattern.compile( regExp );

        Matcher matcher = patternCondition.matcher( condition );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            String programStageId = ids[0];
            ProgramStage programStage = programStageService.getProgramStage( Integer.parseInt( programStageId ) );

            int dataElementId = Integer.parseInt( ids[1] );
            DataElement dataElement = dataElementService.getDataElement( dataElementId );

            if ( programStage == null || dataElement == null )
            {
                return INVALID_CONDITION;
            }

            matcher.appendReplacement( description, programStage.getName() + SEPARATOR_ID + dataElement.getName() );

        }

        matcher.appendTail( description );

        return description.toString();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String SUM_OPERATOR_IN_EXPRESSION = "+";

    private String NOT_NULL_VALUE_IN_EXPRESSION = "{NOT-NULL-VALUE}";

    private String runExpression( String expression, ProgramStageInstance programStageInstance )
    {
        boolean valid = true;
        String comparetor = "";
        Pattern pattern = Pattern.compile( regExpComparator );
        Matcher matcher = pattern.matcher( expression );
        if ( matcher.find() )
        {
            comparetor = matcher.group();
        }

        String[] sides = expression.split( regExpComparator );
        String leftSideValue = getOneSideExpressionValue( sides[0].trim(), programStageInstance );
        String rightSideValue = getOneSideExpressionValue( sides[1].trim(), programStageInstance );

        if ( leftSideValue == null && rightSideValue == null )
        {
            return "true&&null";
        }
        else if ( expression.indexOf( SUM_OPERATOR_IN_EXPRESSION ) != -1 )
        {
            if ( leftSideValue != null && rightSideValue != null )
            {
                String result = leftSideValue + comparetor + rightSideValue;
                final JEP parser = new JEP();
                parser.parseExpression( result );
                valid = (parser.getValue() == 1.0);
            }
        }
        else
        {
            if ( rightSideValue != null && rightSideValue.equals( NOT_NULL_VALUE_IN_EXPRESSION ) )
            {
                valid = !(leftSideValue == null);
            }
            else if ( leftSideValue != null
                && rightSideValue != null
                && ((comparetor.equals( "==" ) && leftSideValue.equals( rightSideValue ))
                    || (comparetor.equals( "<" ) && leftSideValue.compareTo( rightSideValue ) < 0)
                    || (comparetor.equals( "<=" ) && (leftSideValue.equals( rightSideValue ) || leftSideValue
                        .compareTo( rightSideValue ) < 0))
                    || (comparetor.equals( ">" ) && leftSideValue.compareTo( rightSideValue ) > 0)
                    || (comparetor.equals( ">=" ) && (leftSideValue.equals( rightSideValue ) || leftSideValue
                        .compareTo( rightSideValue ) > 0)) || (comparetor.equals( "!=" ) && !leftSideValue
                    .equals( rightSideValue ))) )
            {
                valid = true;
            }
            else
            {
                valid = false;
            }
        }

        return valid + SEPARATE_SIDE_VALUE + leftSideValue + " " + comparetor + " " + rightSideValue;
    }

    private String getOneSideExpressionValue( String expression, ProgramStageInstance programStageInstance )
    {
        StringBuffer description = new StringBuffer();

        Pattern pattern = Pattern.compile( regExp );

        Matcher matcher = pattern.matcher( expression );

        while ( matcher.find() )
        {
            String match = matcher.group();

            PatientDataValue dataValue = getPatientDataValue( match, programStageInstance );

            if ( dataValue == null )
            {
                return null;
            }

            matcher.appendReplacement( description, dataValue.getValue() );
        }

        matcher.appendTail( description );

        return description.toString();
    }

    private PatientDataValue getPatientDataValue( String match, ProgramStageInstance programStageInstance )
    {
        match = match.replaceAll( "[\\[\\]]", "" );

        String[] info = match.split( SEPARATOR_OBJECT );
        String[] ids = info[1].split( SEPARATOR_ID );

        int dataElementId = Integer.parseInt( ids[1] );
        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        PatientDataValue dataValue = valueService.getPatientDataValue( programStageInstance, dataElement );

        return dataValue;
    }

    private Collection<DataElement> getDataElementInExpression( ProgramValidation programValidation )
    {
        Collection<DataElement> dataElements = new HashSet<DataElement>();

        Pattern pattern = Pattern.compile( regExp );
        String expression = programValidation.getLeftSide() + " " + programValidation.getRightSide();
        Matcher matcher = pattern.matcher( expression );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            int dataElementId = Integer.parseInt( ids[1] );
            DataElement dataElement = dataElementService.getDataElement( dataElementId );

            dataElements.add( dataElement );
        }

        return dataElements;
    }

    private Collection<ProgramStage> getProgramStageInExpression( ProgramValidation programValidation )
    {
        Collection<ProgramStage> programStages = new HashSet<ProgramStage>();

        Pattern pattern = Pattern.compile( regExp );
        String expression = programValidation.getLeftSide() + " " + programValidation.getRightSide();
        Matcher matcher = pattern.matcher( expression );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            int programStageId = Integer.parseInt( ids[0] );
            ProgramStage programStage = programStageService.getProgramStage( programStageId );

            programStages.add( programStage );
        }

        return programStages;
    }

}
