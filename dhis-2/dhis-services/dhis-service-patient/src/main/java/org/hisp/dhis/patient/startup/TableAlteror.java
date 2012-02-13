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

package org.hisp.dhis.patient.startup;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.program.ProgramValidation;
import org.hisp.dhis.program.ProgramValidationService;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version TableAlteror.java Sep 9, 2010 10:22:29 PM
 */
public class TableAlteror
    extends AbstractStartupRoutine{
    private static final Log log = LogFactory.getLog( TableAlteror.class );

    Pattern IDENTIFIER_PATTERN = Pattern.compile( "DE:(\\d+)\\.(\\d+)\\.(\\d+)" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataElementCategoryService categoryService;
    
    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    private CaseAggregationConditionService aggregationConditionService;
    
    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }  
    
    private ProgramValidationService programValidationService;

    public void setProgramValidationService( ProgramValidationService programValidationService )
    {
        this.programValidationService = programValidationService;
    }
  
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
  
    @Transactional
    public void execute()
        throws Exception
    {
        updatePatientOrgunitAssociation();

        executeSql( "UPDATE patient SET dobType='A' WHERE birthdateestimated=true" );

        executeSql( "ALTER TABLE patient drop column birthdateestimated" );

        executeSql( "DELETE FROM validationcriteria where property='birthdateestimated'" );

        executeSql( "UPDATE patientattribute SET mandatory = false WHERE mandatory is NULL" );

        executeSql( "UPDATE program SET version = 1 WHERE version is NULL" );

        executeSql( "UPDATE patientidentifiertype SET type='" + PatientIdentifierType.VALUE_TYPE_TEXT
            + "' WHERE type IS NULL" );

        executeSql( "ALTER TABLE patientidentifiertype DROP COLUMN format" );

        executeSql( "ALTER TABLE program DROP COLUMN minDaysAllowedInputData" );

        executeSql( "UPDATE program SET maxDaysAllowedInputData=0 WHERE maxDaysAllowedInputData IS NULL" );

        executeSql( "UPDATE patient SET isdead=false WHERE isdead IS NULL" );

        executeSql( "UPDATE patient SET hasPatients=false WHERE hasPatients IS NULL" );

        executeSql( "UPDATE dataset SET mobile = false WHERE mobile is null" );

        executeSql( "UPDATE dataset SET version = 1 WHERE version is null" );
        
        executeSql( "UPDATE program SET singleEvent = false WHERE singleevent is null" );
        
        executeSql( "UPDATE program SET anonymous = false WHERE anonymous is null" );
        
        executeSql( "UPDATE programstage SET irregular = false WHERE irregular is null" );

        executeSql( "Alter table programinstance modify patientid integer null");
        		
        updateSingleProgramValidation();

        updateStageInProgram();

        executeSql( "UPDATE programvalidation SET dateType = false WHERE dateType is null");
        
        executeSql( "UPDATE programstage_dataelements SET showOnReport = false WHERE showOnReport is null");
        
        int categoryOptionId = categoryService.getDefaultDataElementCategoryOptionCombo().getId();
        executeSql( "UPDATE dataelement SET categoryoptioncomboid = " + categoryOptionId + " WHERE domain='patient'");
        
        upgradeCaseAggregationFormula();
        
        upgradeProgramValidationFormula();
        
        executeSql( "UPDATE program SET displayProvidedOtherFacility = false WHERE displayProvidedOtherFacility is null" );
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void updatePatientOrgunitAssociation()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet isUpdated = statement
                .executeQuery( "SELECT organisationunitid FROM patient where organisationunitid is null " );

            if ( isUpdated.next() )
            {
                ResultSet resultSet = statement
                    .executeQuery( "SELECT patientid, organisationunitid FROM patientidentifier" );
                while ( resultSet.next() )
                {
                    executeSql( "UPDATE patient SET organisationunitid=" + resultSet.getInt( 2 ) + " WHERE patientid="
                        + resultSet.getInt( 1 ) );
                }

                executeSql( "ALTER TABLE patientidentifier DROP COLUMN organisationunitid" );
            }
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private void updateSingleProgramValidation()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet isUpdated = statement.executeQuery( "SELECT * FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_NAME = 'programstage_dataelement_validation'" );

            if ( isUpdated.next() )
            {
                ResultSet rsCount = statement.executeQuery( "SELECT max(programvalidationid) FROM programvalidation" );
                rsCount.next();

                int max = rsCount.getInt( 1 ) + 1;

                ResultSet resultSet = statement
                    .executeQuery( "SELECT pdv.description, pdv.leftprogramstageid, pdv.leftdataelementid, "
                        + "pdv.rightprogramstageid, pdv.rightdataelementid, " + "pdv.operator, ps.programid "
                        + "FROM programstage_dataelement_validation pdv " + "INNER JOIN programstage_dataelements pd "
                        + "ON (pdv.leftprogramstageid=pd.dataelementid AND "
                        + "pdv.leftdataelementid=pd.programstageid) " + "INNER JOIN programstage ps "
                        + "ON pd.programstageid=ps.programstageid" );

                while ( resultSet.next() )
                {
                    max++;
                    String leftSide = "[" + resultSet.getString( 2 ) + "." + resultSet.getString( 3 ) + "." + "]";
                    String rightSide = "[" + resultSet.getString( 4 ) + "." + resultSet.getString( 5 ) + "." + "]";
                    String operator = resultSet.getInt( 6 ) > 0 ? ">" : (resultSet.getInt( 6 ) < 0) ? "<" : "==";

                    String fomular = leftSide + operator + rightSide;

                    executeSql( "INSERT INTO programvalidation (programvalidationid, description,leftSide, rightSide, programid )"
                        + "VALUES ( "
                        + max
                        + ",'"
                        + resultSet.getString( 1 )
                        + "', '"
                        + fomular
                        + "', '1==1', "
                        + resultSet.getInt( 7 ) + ")" );
                }

                executeSql( "DROP TABLE programstage_dataelement_validation" );
            }
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private void updateStageInProgram()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            Collection<Integer> programIds = getPrograms();

            for ( Integer programId : programIds )
            {
                ResultSet resultSet = statement.executeQuery( "SELECT programstageid "
                    + "FROM programstage WHERE programid = " + programId );

                int index = 1;
                while ( resultSet.next() )
                {
                    executeSql( "UPDATE programstage SET stageinprogram = " + index + " WHERE programstageid = "
                        + resultSet.getInt( 1 ) );
                    index++;
                }
            }
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private Collection<Integer> getPrograms()
    {
        Collection<Integer> result = new HashSet<Integer>();

        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet rsMax = statement.executeQuery( "SELECT max(stageinprogram), programid "
                + "FROM programstage GROUP BY programid" );

            while ( rsMax.next() )
            {
                if ( rsMax.getInt( 1 ) == 0 )
                {
                    result.add( rsMax.getInt( 2 ) );
                }
            }

            return result;
        }
        catch ( Exception ex )
        {
            log.debug( ex );

            return null;
        }
        finally
        {
            holder.close();
        }
    }
    
    private void upgradeCaseAggregationFormula()
    {
        Collection<CaseAggregationCondition> conditions = aggregationConditionService.getAllCaseAggregationCondition();
        
        for ( CaseAggregationCondition condition : conditions )
        {
            String formula = upgradeFormula( condition.getAggregationExpression() );
            condition.setAggregationExpression( formula );
            aggregationConditionService.updateCaseAggregationCondition( condition );
        }
    }
    
    private void upgradeProgramValidationFormula()
    {
        Collection<ProgramValidation> programValidations = programValidationService.getAllProgramValidation();
        
        for ( ProgramValidation programValidation : programValidations )
        {
            String leftSide = upgradeFormula( programValidation.getLeftSide() );
            String rightSide = upgradeFormula( programValidation.getRightSide() );
            programValidation.setLeftSide( leftSide );
            programValidation.setRightSide( rightSide );
            programValidationService.updateProgramValidation( programValidation );
        }
    }
    
    private String upgradeFormula( String formula )
    {
        Matcher matcher = IDENTIFIER_PATTERN.matcher( formula );

        StringBuffer out = new StringBuffer();

        while ( matcher.find() )
        {
            String upgradedId = "DE:" + matcher.group( 1 ) + "." + matcher.group( 2 );

            matcher.appendReplacement( out, upgradedId );
        }

        matcher.appendTail( out );
        
        return out.toString();
    }

    private int executeSql( String sql )
    {
        try
        {
            return statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( ex );

            return -1;
        }
    }
}
