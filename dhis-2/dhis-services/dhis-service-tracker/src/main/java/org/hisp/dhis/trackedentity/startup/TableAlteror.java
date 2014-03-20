package org.hisp.dhis.trackedentity.startup;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.hisp.dhis.system.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version TableAlteror.java Sep 9, 2010 10:22:29 PM
 */
public class TableAlteror
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( TableAlteror.class );

    final Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );

    final Pattern IDENTIFIER_PATTERN_FIELD = Pattern.compile( "id=\"(\\d+)-(\\d+)-val\"" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StatementBuilder statementBuilder;

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
        throws Exception
    {
        executeSql( "ALTER TABLE relationshiptype RENAME description TO name" );

        executeSql( "ALTER TABLE programstage_dataelements DROP COLUMN showOnReport" );

        executeSql( "ALTER TABLE program DROP COLUMN hidedateofincident" );

        executeSql( "UPDATE program SET type=2 where singleevent=true" );
        executeSql( "UPDATE program SET type=3 where anonymous=true" );
        executeSql( "ALTER TABLE program DROP COLUMN singleevent" );
        executeSql( "ALTER TABLE program DROP COLUMN anonymous" );
        executeSql( "UPDATE program SET type=1 where type is null" );

        executeSql( "UPDATE programstage SET irregular=false WHERE irregular is null" );

        executeSql( "DROP TABLE programattributevalue" );
        executeSql( "DROP TABLE programinstance_attributes" );
        executeSql( "DROP TABLE programattributeoption" );
        executeSql( "DROP TABLE programattribute" );
        executeSql( "ALTER TABLE programstageinstance ALTER executiondate TYPE date" );

        executeSql( "ALTER TABLE caseaggregationcondition RENAME description TO name" );

        executeSql( "UPDATE programstage_dataelements SET allowProvidedElsewhere=false WHERE allowProvidedElsewhere is null" );
        executeSql( "ALTER TABLE programstageinstance DROP COLUMN providedbyanotherfacility" );

        executeSql( "ALTER TABLE programstageinstance DROP COLUMN stageInProgram" );

        executeSql( "UPDATE programstage SET reportDateDescription='Report date' WHERE reportDateDescription is null" );

        executeSql( "CREATE INDEX programstageinstance_executiondate ON programstageinstance (executiondate)" );

        executeSql( "UPDATE programstage SET autoGenerateEvent=true WHERE autoGenerateEvent is null" );

        executeSql( "UPDATE program SET generatedByEnrollmentDate=false WHERE generatedByEnrollmentDate is null" );

        executeSql( "ALTER TABLE programstage DROP COLUMN stageinprogram" );

        executeSql( "CREATE INDEX index_programinstance ON programinstance( programinstanceid )" );

        executeSql( "ALTER TABLE program DROP COLUMN maxDaysAllowedInputData" );

        executeSql( "ALTER TABLE period modify periodid int AUTO_INCREMENT" );
        executeSql( "CREATE SEQUENCE period_periodid_seq" );
        executeSql( "ALTER TABLE period ALTER COLUMN periodid SET DEFAULT NEXTVAL('period_periodid_seq')" );

        executeSql( "UPDATE program SET programstage_dataelements=false WHERE displayInReports is null" );

        executeSql( "ALTER TABLE programvalidation DROP COLUMN leftside" );
        executeSql( "ALTER TABLE programvalidation DROP COLUMN rightside" );
        executeSql( "ALTER TABLE programvalidation DROP COLUMN dateType" );

        executeSql( "UPDATE programstage SET validCompleteOnly=false WHERE validCompleteOnly is null" );
        executeSql( "UPDATE program SET ignoreOverdueEvents=false WHERE ignoreOverdueEvents is null" );

        executeSql( "UPDATE programstage SET displayGenerateEventBox=true WHERE displayGenerateEventBox is null" );

        executeSql( "ALTER TABLE programvalidation RENAME description TO name" );

        executeSql( "UPDATE program SET blockEntryForm=false WHERE blockEntryForm is null" );
        executeSql( "ALTER TABLE dataset DROP CONSTRAINT program_name_key" );
        executeSql( "UPDATE userroleauthorities SET authority='F_PROGRAM_PUBLIC_ADD' WHERE authority='F_PROGRAM_ADD'" );

        executeSql( "UPDATE program SET onlyEnrollOnce='false' WHERE onlyEnrollOnce is null" );
        executeSql( "UPDATE programStage SET captureCoordinates=false WHERE captureCoordinates is null" );

        executeSql( "update caseaggregationcondition set \"operator\"='times' where \"operator\"='SUM'" );

        executeSql( "update prorgam set \"operator\"='times' where \"operator\"='SUM'" );

        executeSql( "update program set remindCompleted=false where remindCompleted is null" );
        executeSql( "UPDATE programinstance SET followup=false where followup is null" );

        updateUidInDataEntryFrom();

        updateProgramInstanceStatus();

        executeSql( "ALTER TABLE program DROP COLUMN disableRegistrationFields" );
        executeSql( "ALTER TABLE program ALTER COLUMN dateofincidentdescription DROP NOT NULL" );
        executeSql( "UPDATE program SET displayOnAllOrgunit=true where displayOnAllOrgunit is null" );
        executeSql( "UPDATE program SET useFormNameDataElement=true where useFormNameDataElement is null" );
        executeSql( "ALTER TABLE caseaggregationcondition ALTER COLUMN aggregationexpression TYPE varchar(1000)" );
        executeSql( "update program set selectEnrollmentDatesInFuture = false where selectEnrollmentDatesInFuture is null" );
        executeSql( "update program set selectIncidentDatesInFuture = false where selectIncidentDatesInFuture is null" );
        executeSql( "update validationcriteria set description = name where description is null or description='' " );
        executeSql( "update programstage set generatedByEnrollmentDate = false where generatedByEnrollmentDate is null " );
        executeSql( "update programstage set blockEntryForm = false where blockEntryForm is null " );
        executeSql( "update programstage set remindCompleted = false where remindCompleted is null " );
        executeSql( "ALTER TABLE program DROP COLUMN generatedByEnrollmentDate" );
        executeSql( "ALTER TABLE program DROP COLUMN blockEntryForm" );
        executeSql( "ALTER TABLE program DROP COLUMN remindCompleted" );
        executeSql( "ALTER TABLE program DROP COLUMN displayProvidedOtherFacility" );
        executeSql( "UPDATE program SET dataEntryMethod=false WHERE dataEntryMethod is null" );
        executeSql( "UPDATE programstage SET allowGenerateNextVisit=false WHERE allowGenerateNextVisit is null" );
        executeSql( "update programstage set openAfterEnrollment=false where openAfterEnrollment is null" );

        executeSql( "update programstageinstance set status=0 where status is null" );
        executeSql( "ALTER TABLE program DROP COLUMN facilityLB" );
        executeSql( "update programstage_dataelements set allowDateInFuture=false where allowDateInFuture is null" );
        executeSql( "update programstage set autoGenerateEvent=true where programid in ( select programid from program where type=2 )" );

        executeSql( "ALTER TABLE programstageinstance ALTER COLUMN executiondate TYPE timestamp" );

        updateCoordinatesProgramStageInstance();

        executeSql( "ALTER TABLE program DROP COLUMN useBirthDateAsIncidentDate" );
        executeSql( "ALTER TABLE program DROP COLUMN useBirthDateAsEnrollmentDate" );

        updateProgramAttributes();

        executeSql( "UPDATE program SET displayIncidentDate=false WHERE displayIncidentDate is null" );
        executeSql( "UPDATE program SET relationshipFromA=false WHERE relationshipFromA is null" );

        executeSql( "update userroleauthorities set \"name\"='trackedEntityExcelTemplateFileName' where \"name\"='patientExcelTemplateFileName'" );
        executeSql( "update systemsetting set \"name\"='autoSavetTrackedEntityForm' where \"name\"='autoSavePatientRegistration'" );

        executeSql( "UPDATE trackedentityattribute SET uniquefield=false WHERE uniquefield is null" );

        executeSql( "INSERT INTO trackedentityattribute "
            + "( trackedentityattributeid, uid, lastUpdated, name, description, valueType, mandatory, inherit, displayOnVisitSchedule, uniquefield, orgunitScope, programScope )"
            + " select "
            + statementBuilder.getAutoIncrementValue()
            + ", uid, lastUpdated, name,  description, type, mandatory, false, false, true, orgunitScope, programScope from patientidentifiertype" );

        executeSql( "INSERT INTO trackedentityattributevalue (trackedentityinstanceid, trackedentityattributeid, value ) "
            + "select trackedentityinstanceid, pa.trackedentityattributeid, identifier "
            + "from patientidentifier pi inner join patientidentifiertype pit "
            + "on pi.patientidentifiertypeid=pit.patientidentifiertypeid inner join trackedentityattribute pa "
            + "on pa.uid=pit.uid where pi.trackedentityinstanceid is not null" );
        executeSql( "DROP TABLE program_identifiertypes" );
        executeSql( "DROP TABLE patientidentifier" );
        executeSql( "DROP TABLE patientidentifiertype" );
        executeSql( "ALTER TABLE trackedentityattribute RENAME CONSTRAINT fk_patientidentifiertype_periodtypeid TO trackedentityattribute_periodtypeid" );

        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_REMOVE_EMPTY_EVENTS' where authority='F_PATIENT_REMOVE_EMPTY_EVENTS'" );
        executeSql( "update userroleauthorities set authority='F_ACCESS_TRACKED_ENTITY_ATTRIBUTES' where authority='F_ACCESS_PATIENT_ATTRIBUTES'" );
        executeSql( "update userroleauthorities set authority='F_ALLOW_EDIT_TRACKED_ENTITY_ATTRIBUTES' where authority='F_ALLOW_EDIT_PATIENT_ATTRIBUTES'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_ATTRIBUTE_ADD' where authority='F_TRACKED_ENTITY_INSTANCEATTRIBUTE_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_ATTRIBUTE_DELETE' where authority='F_TRACKED_ENTITY_INSTANCEATTRIBUTE_DELETE'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_ATTRIBUTEVALUE_ADD' where authority='F_TRACKED_ENTITY_INSTANCEATTRIBUTE_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_CHANGE_LOCATION' where authority='F_PATIENT_CHANGE_LOCATION'" );
        executeSql( "update userroleauthorities set authority='F_SEARCH_TRACKED_ENTITY_INSTANCE_IN_ALL_FACILITIES' where authority='F_SEARCH_PATIENT_IN_ALL_FACILITIES'" );
        executeSql( "update userroleauthorities set authority='F_SEARCH_TRACKED_ENTITY_INSTANCE_IN_OTHER_ORGUNITS' where authority='F_SEARCH_PATIENT_IN_OTHER_ORGUNITS'" );
        executeSql( "update userroleauthorities set authority='F_ADD_TRACKED_ENTITY_FORM' where authority='F_ADD_PATIENT_REGISTRATION_FORM'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_DATAVALUE_ADD' where authority='F_PATIENT_DATAVALUE_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_DATAVALUE_DELETE' where authority='F_PATIENT_DATAVALUE_DELETE'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_ADD' where authority='F_PATIENT_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_DELETE' where authority='F_PATIENT_DELETE'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_SEARCH' where authority='F_PATIENT_SEARCH'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_LIST' where authority='F_PATIENT_LIST'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_HISTORY' where authority='F_PATIENT_HISTORY'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_DASHBOARD' where authority='F_PATIENT_DASHBOARD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_COMMENT_ADD' where authority='F_PATIENT_COMMENT_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_COMMENT_DELETE' where authority='F_PATIENT_COMMENT_DELETE'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_AGGREGATE_REPORT_PUBLIC_ADD' where authority='F_PATIENT_AGGREGATE_REPORT_PUBLIC_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_AGGREGATE_REPORT_PRIVATE_ADD' where authority='F_PATIENT_AGGREGATE_REPORT_PRIVATE_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_TABULAR_REPORT_PUBLIC_ADD' where authority='F_PATIENT_TABULAR_REPORT_PUBLIC_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_TABULAR_REPORT_PRIVATE_ADD' where authority='F_PATIENT_TABULAR_REPORT_PRIVATE_ADD'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_AGGREGATION' where authority='F_PATIENT_AGGREGATION'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_MANAGEMENT' where authority='F_PATIENT_MANAGEMENT'" );
        executeSql( "update userroleauthorities set authority='F_NAME_BASED_DATA_ENTRY' where authority='F_NAME_BASED_DATA_ENTRY'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_ATTRIBUTEVALUE_DELETE' where authority='F_PATIENTATTRIBUTEVALUE_DELETE'" );
        executeSql( "update userroleauthorities set authority='F_TRACKED_ENTITY_INSTANCE_REMINDER_MANAGEMENT' where authority='F_PATIENT_REMINDER_MANAGEMENT'" );

        executeSql( "ALTER TABLE program_attributes RENAME COLUMN programattributeid TO programtrackedentityattributeid" );
        createPersonTrackedEntity();

        executeSql( "ALTER TABLE trackedentityattributevalue DROP COLUMN trackedentityattributeoptionid" );
        executeSql( "DROP TABLE trackedentityattributeoption" );

        executeSql( "UPDATE program_attributes SET mandatory = trackedentityattribute.mandatory "
            + "FROM program_attributes pa " + "INNER JOIN trackedentityattribute  "
            + "ON pa.trackedentityattributeid=trackedentityattribute.trackedentityattributeid  "
            + "where trackedentityattribute.mandatory is not null" );
        executeSql( "ALTER TABLE trackedentityattribute DROP COLUMN mandatory" );

        executeSql( "update datavalue set storedby='aggregated_from_tracker' where storedby='DHIS-System'");
        
        executeSql( "ALTER TABLE trackedentityattribute DROP COLUMN groupBy" );
        
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void updateProgramAttributes()
    {
        String autoIncrVal = statementBuilder.getAutoIncrementValue();

        String attributeSql = "INSERT INTO program_attributes (programattributeid, attributeid, sort_order, displayinlist, programid) "
            + "SELECT "
            + autoIncrVal
            + ", pp.trackedentityattributeid, pp.sort_order, false, pp.programid "
            + "FROM program_trackedentityattributes pp";
        executeSql( attributeSql );

        log.info( "Inserted data into program_attributes table." );

        executeSql( "DROP TABLE program_attributes." );
        log.info( "Dropped program_attributes table." );
    }

    private void updateUidInDataEntryFrom()
    {
        Collection<ProgramStage> programStages = programStageService.getAllProgramStages();

        for ( ProgramStage programStage : programStages )
        {
            DataEntryForm dataEntryForm = programStage.getDataEntryForm();
            if ( dataEntryForm != null && dataEntryForm.getFormat() != DataEntryForm.CURRENT_FORMAT )
            {
                String programStageUid = programStage.getUid();
                String htmlCode = programStage.getDataEntryForm().getHtmlCode();

                // ---------------------------------------------------------------------
                // Metadata code to add to HTML before outputting
                // ---------------------------------------------------------------------

                StringBuffer sb = new StringBuffer();

                // ---------------------------------------------------------------------
                // Pattern to match data elements in the HTML code
                // ---------------------------------------------------------------------

                Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

                // ---------------------------------------------------------------------
                // Iterate through all matching data element fields
                // ---------------------------------------------------------------------

                while ( inputMatcher.find() )
                {
                    String inputHTML = inputMatcher.group();

                    // -----------------------------------------------------------------
                    // Get HTML input field code
                    // -----------------------------------------------------------------

                    String dataElementCode = inputMatcher.group( 1 );

                    Matcher identifierMatcher = IDENTIFIER_PATTERN_FIELD.matcher( dataElementCode );

                    if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
                    {
                        // -------------------------------------------------------------
                        // Get data element ID of data element
                        // -------------------------------------------------------------

                        int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );
                        DataElement dataElement = dataElementService.getDataElement( dataElementId );

                        if ( dataElement != null )
                        {
                            inputHTML = inputHTML.replaceFirst( identifierMatcher.group( 1 ), programStageUid );
                            inputHTML = inputHTML.replaceFirst( identifierMatcher.group( 2 ), dataElement.getUid() );
                            inputMatcher.appendReplacement( sb, inputHTML );
                        }

                    }
                }

                inputMatcher.appendTail( sb );

                htmlCode = (sb.toString().isEmpty()) ? htmlCode : sb.toString();
                dataEntryForm.setHtmlCode( htmlCode );
                dataEntryForm.setFormat( DataEntryForm.CURRENT_FORMAT );
                dataEntryFormService.updateDataEntryForm( dataEntryForm );
            }
        }
    }

    private void updateProgramInstanceStatus()
    {
        // Set active status for events
        executeSql( "UPDATE programinstance SET status=0 WHERE completed=false" );

        // Set un-completed status for events
        executeSql( "UPDATE programinstance SET status=2 WHERE programinstanceid in "
            + "( select psi.programinstanceid from programinstance pi join programstageinstance psi "
            + "on psi.programinstanceid = psi.programstageinstanceid "
            + "where pi.completed=true and psi.completed = false )" );

        // Set completed status for events
        executeSql( "UPDATE programinstance SET status=1 WHERE status is null" );

        // Drop the column with name as completed
        executeSql( "ALTER TABLE programinstance DROP COLUMN completed" );
    }

    private void updateCoordinatesProgramStageInstance()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement
                .executeQuery( "SELECT programstageinstanceid, coordinates FROM programstageinstance where coordinates is not null" );

            while ( resultSet.next() )
            {
                String coordinates = resultSet.getString( "coordinates" );
                String longitude = ValidationUtils.getLongitude( coordinates );
                String latitude = ValidationUtils.getLatitude( coordinates );
                executeSql( "UPDATE programstageinstance SET longitude='" + longitude + "', latitude='" + latitude
                    + "'  WHERE programstageinstanceid=" + resultSet.getInt( "programstageinstanceid" ) );
            }

            executeSql( "ALTER TABLE programstageinstance DROP COLUMN coordinates" );
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

    private void createPersonTrackedEntity()
    {
        int exist = jdbcTemplate.queryForObject( "SELECT count(*) FROM trackedentity where name='Person'",
            Integer.class );
        if ( exist == 0 )
        {
            String id = statementBuilder.getAutoIncrementValue();

            jdbcTemplate.execute( "INSERT INTO trackedentity(trackedentityid,uid, name, description) values(" + id
                + ",'" + CodeGenerator.generateCode() + "','Person','Person')" );

            jdbcTemplate.execute( "UPDATE program SET trackedentityid="
                + "  (SELECT trackedentityid FROM trackedentity where name='Person') where trackedentityid is null" );

            jdbcTemplate.execute( "UPDATE trackedentityinstance SET trackedentityid="
                + "  (SELECT trackedentityid FROM trackedentity where name='Person') where trackedentityid is null" );
        }
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
