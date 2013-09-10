package org.hisp.dhis.mobile.service;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.api.mobile.ActivityReportingService;
import org.hisp.dhis.api.mobile.NotAllowedException;
import org.hisp.dhis.api.mobile.PatientMobileSettingService;
import org.hisp.dhis.api.mobile.model.Activity;
import org.hisp.dhis.api.mobile.model.ActivityPlan;
import org.hisp.dhis.api.mobile.model.ActivityValue;
import org.hisp.dhis.api.mobile.model.Beneficiary;
import org.hisp.dhis.api.mobile.model.DataValue;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.Task;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Section;
import org.hisp.dhis.api.mobile.model.comparator.ActivityComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientMobileSetting;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.util.PatientIdentifierGenerator;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageSectionService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.system.util.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class ActivityReportingServiceImpl
    implements ActivityReportingService
{
    private static final String PROGRAM_STAGE_UPLOADED = "program_stage_uploaded";

    private static final String PROGRAM_STAGE_SECTION_UPLOADED = "program_stage_section_uploaded";

    private static final String SINGLE_EVENT_UPLOADED = "single_event_uploaded";

    private ActivityComparator activityComparator = new ActivityComparator();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    // private ActivityPlanService activityPlanService;

    private PatientService patientService;

    private PatientAttributeValueService patientAttValueService;

    private PatientAttributeService patientAttService;

    private PatientDataValueService dataValueService;

    private PatientMobileSettingService patientMobileSettingService;

    private PatientIdentifierService patientIdentifierService;

    private ProgramStageSectionService programStageSectionService;

    private ProgramInstanceService programInstanceService;

    private RelationshipService relationshipService;

    private RelationshipTypeService relationshipTypeService;

    private DataElementService dataElementService;

    private PatientDataValueService patientDataValueService;

    private ProgramService programService;

    private ProgramStageService programStageService;

    private org.hisp.dhis.mobile.service.ModelMapping modelMapping;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    public PatientAttributeService getPatientAttributeService()
    {
        return patientAttributeService;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public PatientIdentifierTypeService getPatientIdentifierTypeService()
    {
        return patientIdentifierTypeService;
    }

    private Collection<PatientIdentifier> patientIdentifiers;

    public Collection<PatientIdentifier> getPatientIdentifiers()
    {
        return patientIdentifiers;
    }

    public void setPatientIdentifiers( Collection<PatientIdentifier> patientIdentifiers )
    {
        this.patientIdentifiers = patientIdentifiers;
    }

    private Collection<PatientIdentifierType> patientIdentifierTypes;

    public Collection<PatientIdentifierType> getPatientIdentifierTypes()
    {
        return patientIdentifierTypes;
    }

    public void setPatientIdentifierTypes( Collection<PatientIdentifierType> patientIdentifierTypes )
    {
        this.patientIdentifierTypes = patientIdentifierTypes;
    }

    private Collection<org.hisp.dhis.patient.PatientAttribute> patientAttributes;

    public Collection<org.hisp.dhis.patient.PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    public void setPatientAttributes( Collection<org.hisp.dhis.patient.PatientAttribute> patientAttributes )
    {
        this.patientAttributes = patientAttributes;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    // -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------

    private PatientMobileSetting setting;

    private org.hisp.dhis.patient.PatientAttribute groupByAttribute;

    @Override
    public ActivityPlan getCurrentActivityPlan( OrganisationUnit unit, String localeString )
    {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, 30 );

        long upperBound = cal.getTime().getTime();

        cal.add( Calendar.DATE, -60 );
        long lowerBound = cal.getTime().getTime();

        List<Activity> items = new ArrayList<Activity>();
        Collection<Patient> patients = patientService.getPatients( unit, null, null );

        for ( Patient patient : patients )
        {
            for ( ProgramStageInstance programStageInstance : programStageInstanceService.getProgramStageInstances(
                patient, false ) )
            {
                if ( programStageInstance.getDueDate().getTime() >= lowerBound
                    && programStageInstance.getDueDate().getTime() <= upperBound )
                {
                    items.add( getActivity( programStageInstance, false ) );
                }
            }
        }

        this.setGroupByAttribute( patientAttService.getPatientAttributeByGroupBy( true ) );

        if ( items.isEmpty() )
        {
            return null;
        }

        Collections.sort( items, activityComparator );

        return new ActivityPlan( items );
    }

    @Override
    public ActivityPlan getAllActivityPlan( OrganisationUnit unit, String localeString )
    {

        List<Activity> items = new ArrayList<Activity>();
        Collection<Patient> patients = patientService.getPatients( unit, null, null );

        for ( Patient patient : patients )
        {
            for ( ProgramStageInstance programStageInstance : programStageInstanceService.getProgramStageInstances(
                patient, false ) )
            {
                items.add( getActivity( programStageInstance, false ) );
            }
        }

        this.setGroupByAttribute( patientAttService.getPatientAttributeByGroupBy( true ) );

        if ( items.isEmpty() )
        {
            return null;
        }

        Collections.sort( items, activityComparator );
        return new ActivityPlan( items );
    }

    @Override
    public ActivityPlan getActivitiesByIdentifier( String keyword )
        throws NotAllowedException
    {

        long time = PeriodType.createCalendarInstance().getTime().getTime();

        Calendar expiredDate = Calendar.getInstance();

        List<Activity> items = new ArrayList<Activity>();

        Collection<Patient> patients = patientIdentifierService.getPatientsByIdentifier( keyword, 0,
            patientIdentifierService.countGetPatientsByIdentifier( keyword ) );

        // Make sure user input full beneficiary identifier number

        if ( patients.size() > 1 )
        {
            throw NotAllowedException.NEED_MORE_SPECIFIC;
        }
        else if ( patients.size() == 0 )
        {
            throw NotAllowedException.NO_BENEFICIARY_FOUND;
        }
        else
        {
            Iterator<Patient> iterator = patients.iterator();

            while ( iterator.hasNext() )
            {
                Patient patient = iterator.next();

                List<ProgramStageInstance> programStageInstances = programStageInstanceService
                    .getProgramStageInstances( patient, false );

                for ( int i = 0; i < programStageInstances.size(); i++ )
                {
                    ProgramStageInstance programStageInstance = programStageInstances.get( i );

                    // expiredDate.setTime( DateUtils.getDateAfterAddition(
                    // programStageInstance.getDueDate(), 0 ) );
                    expiredDate.setTime( DateUtils.getDateAfterAddition( programStageInstance.getDueDate(), 30 ) );

                    if ( programStageInstance.getDueDate().getTime() <= time && expiredDate.getTimeInMillis() > time )
                    {
                        items.add( getActivity( programStageInstance,
                            programStageInstance.getDueDate().getTime() < time ) );
                    }
                }
            }

            return new ActivityPlan( items );
        }

    }

    // -------------------------------------------------------------------------
    // DataValueService
    // -------------------------------------------------------------------------

    @Override
    public void saveActivityReport( OrganisationUnit unit, ActivityValue activityValue, Integer programStageSectionId )
        throws NotAllowedException
    {

        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( activityValue
            .getProgramInstanceId() );
        if ( programStageInstance == null )
        {
            throw NotAllowedException.INVALID_PROGRAM_STAGE;
        }

        programStageInstance.getProgramStage();
        Collection<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>();

        ProgramStageSection programStageSection = programStageSectionService
            .getProgramStageSection( programStageSectionId );

        if ( programStageSectionId != 0 )
        {
            for ( ProgramStageDataElement de : programStageSection.getProgramStageDataElements() )
            {
                dataElements.add( de.getDataElement() );
            }
        }
        else
        {
            for ( ProgramStageDataElement de : programStageInstance.getProgramStage().getProgramStageDataElements() )
            {
                dataElements.add( de.getDataElement() );
            }
        }

        programStageInstance.getProgramStage().getProgramStageDataElements();
        Collection<Integer> dataElementIds = new ArrayList<Integer>( activityValue.getDataValues().size() );

        for ( DataValue dv : activityValue.getDataValues() )
        {
            dataElementIds.add( dv.getId() );
        }

        if ( dataElements.size() != dataElementIds.size() )
        {
            throw NotAllowedException.INVALID_PROGRAM_STAGE;
        }

        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = new HashMap<Integer, org.hisp.dhis.dataelement.DataElement>();
        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            if ( !dataElementIds.contains( dataElement.getId() ) )
            {
                throw NotAllowedException.INVALID_PROGRAM_STAGE;
            }
            dataElementMap.put( dataElement.getId(), dataElement );
        }

        // Set ProgramStageInstance to completed
        if ( programStageSectionId == 0 )
        {
            programStageInstance.setCompleted( true );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
        }

        // Everything is fine, hence save
        saveDataValues( activityValue, programStageInstance, dataElementMap );

    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findPatient( String keyword, int orgUnitId )
        throws NotAllowedException
    {
        if ( isNumber( keyword ) == false )
        {

            int startIndex = keyword.indexOf( ' ' );
            int endIndex = keyword.lastIndexOf( ' ' );

            String firstName = keyword.toString();
            String middleName = " ";
            String lastName = " ";

            if ( keyword.indexOf( ' ' ) != -1 )
            {
                firstName = keyword.substring( 0, startIndex );
                if ( startIndex == endIndex )
                {
                    middleName = "  ";
                    lastName = keyword.substring( startIndex + 1, keyword.length() );
                }
                else
                {
                    middleName = " " + keyword.substring( startIndex + 1, endIndex ) + " ";
                    lastName = keyword.substring( endIndex + 1, keyword.length() );
                }
            }

            List<Patient> patients = (List<Patient>) this.patientService.getPatientByFullname( firstName + middleName
                + lastName, orgUnitId );

            if ( patients.size() > 1 )
            {
                String patientsInfo = new String();

                DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

                for ( Patient each : patients )
                {
                    patientsInfo += each.getId() + "/" + each.getFullName() + "/"
                        + dateFormat.format( each.getBirthDate() ) + "$";
                }

                throw new NotAllowedException( patientsInfo );
            }
            else if ( patients.size() == 0 )
            {
                throw NotAllowedException.NO_BENEFICIARY_FOUND;
            }
            else
            {
                org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patients.get( 0 ) );

                return patientMobile;
            }
        }
        else
        {
            Patient patient = patientService.getPatient( Integer.parseInt( keyword ) );

            org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patient );

            return patientMobile;
        }

    }

    @Override
    public String saveProgramStage( org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage,
        int patientId, int orgUnitId )
        throws NotAllowedException
    {
        if ( mobileProgramStage.isSingleEvent() )
        {
            Patient patient = patientService.getPatient( patientId );
            ProgramStageInstance prStageInstance = programStageInstanceService
                .getProgramStageInstance( mobileProgramStage.getId() );
            ProgramStage programStage = programStageService.getProgramStage( prStageInstance.getProgramStage().getId() );
            // ProgramStage programStage = programStageService.getProgramStage(
            // mobileProgramStage.getId() );
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

            // ---------------------------------------------------------------------
            // Add a new program-instance
            // ---------------------------------------------------------------------
            ProgramInstance programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( new Date() );
            programInstance.setDateOfIncident( new Date() );
            programInstance.setProgram( programStage.getProgram() );
            programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
            programInstance.setPatient( patient );

            patient.getPrograms().add( programStage.getProgram() );
            patientService.updatePatient( patient );

            programInstanceService.addProgramInstance( programInstance );

            // ---------------------------------------------------------------------
            // Add a new program-stage-instance
            // ---------------------------------------------------------------------

            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setDueDate( new Date() );
            programStageInstance.setExecutionDate( new Date() );
            programStageInstance.setOrganisationUnit( organisationUnit );
            programStageInstance.setCompleted( true );
            programStageInstanceService.addProgramStageInstance( programStageInstance );

            // ---------------------------------------------------------------------
            // Save value
            // ---------------------------------------------------------------------

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> dataElements = mobileProgramStage
                .getDataElements();

            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement dataElement = dataElementService.getDataElement( dataElements.get( i ).getId() );

                PatientDataValue patientDataValue = new PatientDataValue();
                patientDataValue.setDataElement( dataElement );

                patientDataValue.setValue( dataElements.get( i ).getValue() );
                patientDataValue.setProgramStageInstance( programStageInstance );
                patientDataValue.setTimestamp( new Date() );
                patientDataValueService.savePatientDataValue( patientDataValue );

            }

            return SINGLE_EVENT_UPLOADED;

        }
        else
        {
            ProgramStageInstance programStageInstance = programStageInstanceService
                .getProgramStageInstance( mobileProgramStage.getId() );

            /*
             * //Begin Changes ProgramStage programStage =
             * programStageService.getProgramStage( mobileProgramStage.getId()
             * ); Patient patient = patientService.getPatient( patientId );
             * Program program = programStage.getProgram();
             * 
             * Collection<ProgramInstance> programInstances =
             * programInstanceService.getProgramInstances( patient, program );
             * ProgramStageInstance programStageInstance = null; for (
             * ProgramInstance each : programInstances ) { if(
             * each.getStatus()==ProgramInstance.STATUS_ACTIVE ) {
             * programStageInstance =
             * programStageInstanceService.getProgramStageInstance( each,
             * programStage ); break; } }
             * 
             * //End Changes
             */
            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> dataElements = mobileProgramStage
                .getDataElements();

            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement dataElement = dataElementService.getDataElement( dataElements.get( i ).getId() );

                PatientDataValue previousPatientDataValue = patientDataValueService.getPatientDataValue(
                    programStageInstance, dataElement );

                if ( previousPatientDataValue == null )
                {
                    PatientDataValue patientDataValue = new PatientDataValue( programStageInstance, dataElement,
                        new Date(), dataElements.get( i ).getValue() );
                    patientDataValueService.savePatientDataValue( patientDataValue );
                }
                else
                {
                    previousPatientDataValue.setValue( dataElements.get( i ).getValue() );
                    previousPatientDataValue.setTimestamp( new Date() );
                    previousPatientDataValue.setProvidedElsewhere( false );
                    patientDataValueService.updatePatientDataValue( previousPatientDataValue );
                }

            }

            if ( PeriodUtil.stringToDate( mobileProgramStage.getReportDate() ) != null )
            {
                programStageInstance.setExecutionDate( PeriodUtil.stringToDate( mobileProgramStage.getReportDate() ) );
            }
            else
            {
                programStageInstance.setExecutionDate( new Date() );
            }

            if ( programStageInstance.getProgramStage().getProgramStageDataElements().size() > dataElements.size() )
            {
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
                return PROGRAM_STAGE_SECTION_UPLOADED;
            }
            else
            {
                programStageInstance.setCompleted( mobileProgramStage.isCompleted() );

                // check if any compulsory value is null
                for ( int i = 0; i < dataElements.size(); i++ )
                {
                    if ( dataElements.get( i ).isCompulsory() == true )
                    {
                        if ( dataElements.get( i ).getValue() == null )
                        {
                            programStageInstance.setCompleted( false );
                            // break;
                            throw NotAllowedException.INVALID_PROGRAM_STAGE;
                        }
                    }
                }
                programStageInstanceService.updateProgramStageInstance( programStageInstance );

                // check if all belonged program stage are completed
                if ( isAllProgramStageFinished( programStageInstance ) == true )
                {
                    ProgramInstance programInstance = programStageInstance.getProgramInstance();
                    programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
                    programInstanceService.updateProgramInstance( programInstance );
                }
                return PROGRAM_STAGE_UPLOADED;
            }
        }
    }

    private boolean isAllProgramStageFinished( ProgramStageInstance programStageInstance )
    {
        ProgramInstance programInstance = programStageInstance.getProgramInstance();
        Collection<ProgramStageInstance> programStageInstances = programInstance.getProgramStageInstances();
        if ( programStageInstances != null )
        {
            Iterator<ProgramStageInstance> iterator = programStageInstances.iterator();

            while ( iterator.hasNext() )
            {
                ProgramStageInstance each = iterator.next();
                if ( !each.isCompleted() )
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient enrollProgram( String enrollInfo )
        throws NotAllowedException
    {
        String[] enrollProgramInfos = enrollInfo.split( "-" );
        int patientId = Integer.parseInt( enrollProgramInfos[0] );
        int programId = Integer.parseInt( enrollProgramInfos[1] );

        Patient patient = patientService.getPatient( patientId );
        Program program = programService.getProgram( programId );

        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setEnrollmentDate( new Date() );
        programInstance.setDateOfIncident( new Date() );
        programInstance.setProgram( program );
        programInstance.setPatient( patient );
        programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );
        programInstanceService.addProgramInstance( programInstance );
        patient.getPrograms().add( program );
        patientService.updatePatient( patient );
        for ( ProgramStage programStage : program.getProgramStages() )
        {
            if ( programStage.getAutoGenerateEvent() )
            {
                ProgramStageInstance programStageInstance = new ProgramStageInstance();
                programStageInstance.setProgramInstance( programInstance );
                programStageInstance.setProgramStage( programStage );
                Date dateCreatedEvent = new Date();
                if ( programStage.getGeneratedByEnrollmentDate() )
                {
                    // dateCreatedEvent = sdf.parseDateTime( enrollmentDate
                    // ).toDate();
                }
                Date dueDate = DateUtils.getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

                programStageInstance.setDueDate( dueDate );

                if ( program.isSingleEvent() )
                {
                    programStageInstance.setExecutionDate( dueDate );
                }

                programStageInstanceService.addProgramStageInstance( programStageInstance );

            }

        }
        return getPatientModel( patient );
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private Activity getActivity( ProgramStageInstance instance, boolean late )
    {

        Activity activity = new Activity();
        Patient patient = instance.getProgramInstance().getPatient();

        activity.setBeneficiary( getBeneficiaryModel( patient ) );
        activity.setDueDate( instance.getDueDate() );
        activity.setTask( getTask( instance ) );
        activity.setLate( late );
        activity.setExpireDate( DateUtils.getDateAfterAddition( instance.getDueDate(), 30 ) );

        return activity;
    }

    private Task getTask( ProgramStageInstance instance )
    {
        if ( instance == null )
            return null;

        Task task = new Task();
        task.setCompleted( instance.isCompleted() );
        task.setId( instance.getId() );
        task.setProgramStageId( instance.getProgramStage().getId() );
        task.setProgramId( instance.getProgramInstance().getProgram().getId() );
        return task;
    }

    private Beneficiary getBeneficiaryModel( Patient patient )
    {
        Beneficiary beneficiary = new Beneficiary();
        List<PatientAttribute> patientAtts = new ArrayList<PatientAttribute>();
        List<org.hisp.dhis.patient.PatientAttribute> atts;

        beneficiary.setId( patient.getId() );
        beneficiary.setFirstName( patient.getFirstName() );
        beneficiary.setLastName( patient.getLastName() );
        beneficiary.setMiddleName( patient.getMiddleName() );

        Period period = new Period( new DateTime( patient.getBirthDate() ), new DateTime() );
        beneficiary.setAge( period.getYears() );

        this.setSetting( getSettings() );

        if ( setting != null )
        {
            if ( setting.getGender() )
            {
                beneficiary.setGender( patient.getGender() );
            }
            if ( setting.getDobtype() )
            {
                beneficiary.setDobType( patient.getDobType() );
            }
            if ( setting.getBirthdate() )
            {
                beneficiary.setBirthDate( patient.getBirthDate() );
            }
            if ( setting.getRegistrationdate() )
            {
                beneficiary.setRegistrationDate( patient.getRegistrationDate() );
            }

            atts = setting.getPatientAttributes();
            for ( org.hisp.dhis.patient.PatientAttribute each : atts )
            {
                PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, each );
                if ( value != null )
                {
                    patientAtts.add( new PatientAttribute( each.getName(), value.getValue(), each.getValueType(),
                        new ArrayList<String>() ) );
                }
            }

        }

        // Set attribute which is used to group beneficiary on mobile (only if
        // there is attribute which is set to be group factor)
        PatientAttribute beneficiaryAttribute = null;

        if ( groupByAttribute != null )
        {
            beneficiaryAttribute = new PatientAttribute();
            beneficiaryAttribute.setName( groupByAttribute.getName() );
            PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, groupByAttribute );
            beneficiaryAttribute.setValue( value == null ? "Unknown" : value.getValue() );
            beneficiary.setGroupAttribute( beneficiaryAttribute );
        }

        // Set all identifier
        Set<PatientIdentifier> patientIdentifiers = patient.getIdentifiers();
        List<org.hisp.dhis.api.mobile.model.PatientIdentifier> identifiers = new ArrayList<org.hisp.dhis.api.mobile.model.PatientIdentifier>();
        if ( patientIdentifiers.size() > 0 )
        {

            for ( PatientIdentifier id : patientIdentifiers )
            {

                String idTypeName = "DHIS2 ID";

                // MIGHT BE NULL because of strange design..
                PatientIdentifierType identifierType = id.getIdentifierType();

                if ( identifierType != null )
                {
                    idTypeName = identifierType.getName();
                }

                identifiers
                    .add( new org.hisp.dhis.api.mobile.model.PatientIdentifier( idTypeName, id.getIdentifier() ) );
            }

            beneficiary.setIdentifiers( identifiers );
        }

        beneficiary.setPatientAttValues( patientAtts );
        return beneficiary;
    }

    // get patient model for LWUIT
    private org.hisp.dhis.api.mobile.model.LWUITmodel.Patient getPatientModel( Patient patient )
    {
        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientModel = new org.hisp.dhis.api.mobile.model.LWUITmodel.Patient();
        
        List<PatientAttribute> patientAtts = new ArrayList<PatientAttribute>();
        
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Program> mobileProgramList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Program>();
        
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Program> mobileCompletedProgramList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Program>();
        
        List<org.hisp.dhis.patient.PatientAttribute> atts;

        patientModel.setId( patient.getId() );

        String firstName = "";
        String lastName = "";
        String middleName = "";

        if ( patient.getFirstName() != null )
        {
            firstName = patient.getFirstName();
        }

        if ( patient.getLastName() != null )
        {
            lastName = patient.getLastName();
        }

        if ( patient.getMiddleName() != null )
        {
            middleName = patient.getMiddleName();
        }

        patientModel.setFirstName( firstName );
        patientModel.setLastName( lastName );
        patientModel.setMiddleName( middleName );

        Period period = new Period( new DateTime( patient.getBirthDate() ), new DateTime() );
        patientModel.setAge( period.getYears() );
        if ( patient.getOrganisationUnit() != null )
        {
            patientModel.setOrganisationUnitName( patient.getOrganisationUnit().getName() );
        }
        patientModel.setPhoneNumber( patient.getPhoneNumber() );

        this.setSetting( getSettings() );

        if ( setting != null )
        {
            if ( setting.getGender() )
            {
                patientModel.setGender( patient.getGender() );
            }
            if ( setting.getDobtype() )
            {
                patientModel.setDobType( patient.getDobType() );
            }
            if ( setting.getBirthdate() )
            {
                patientModel.setBirthDate( patient.getBirthDate() );
            }
            if ( setting.getRegistrationdate() )
            {
                patientModel.setRegistrationDate( patient.getRegistrationDate() );
            }

            atts = setting.getPatientAttributes();
            for ( org.hisp.dhis.patient.PatientAttribute each : atts )
            {
                PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, each );
                if ( value != null )
                {
                    patientAtts.add( new PatientAttribute( each.getName(), value.getValue(), each.getValueType(),
                        new ArrayList<String>() ) );
                }
            }

        }

        // Set all identifier
        Set<PatientIdentifier> patientIdentifiers = patient.getIdentifiers();
        List<org.hisp.dhis.api.mobile.model.PatientIdentifier> identifiers = new ArrayList<org.hisp.dhis.api.mobile.model.PatientIdentifier>();
        if ( patientIdentifiers.size() > 0 )
        {

            for ( PatientIdentifier id : patientIdentifiers )
            {

                String idTypeName = "DHIS2 ID";

                // MIGHT BE NULL because of strange design..
                PatientIdentifierType identifierType = id.getIdentifierType();

                if ( identifierType != null )
                {
                    idTypeName = identifierType.getName();
                }

                identifiers
                    .add( new org.hisp.dhis.api.mobile.model.PatientIdentifier( idTypeName, id.getIdentifier() ) );
            }

            patientModel.setIdentifiers( identifiers );
        }

        patientModel.setPatientAttValues( patientAtts );

        // Set current programs
        List<ProgramInstance> listOfProgramInstance = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( patient, ProgramInstance.STATUS_ACTIVE ) );

        if ( listOfProgramInstance.size() > 0 )
        {
            System.out.println("current");
            for ( ProgramInstance each : listOfProgramInstance )
            {
                System.out.println(each.getProgram().getName());
                mobileProgramList.add( getMobileProgram( patient, each ) );
            }
        }
        patientModel.setPrograms( mobileProgramList );
        
        // Set completed programs
        List<ProgramInstance> listOfCompletedProgramInstance = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( patient, ProgramInstance.STATUS_COMPLETED ) );

        if ( listOfCompletedProgramInstance.size() > 0 )
        {
            System.out.println("completed");
            for ( ProgramInstance each : listOfCompletedProgramInstance )
            {
                System.out.println(each.getProgram().getName());
                mobileCompletedProgramList.add( getMobileProgram( patient, each ) );
            }
        }
        patientModel.setCompletedPrograms( mobileCompletedProgramList );
        
        /*
         * List<Integer> mobileProgramIDList = new ArrayList<Integer>(); for (
         * Program eachProgram : patient.getPrograms()) {
         * mobileProgramIDList.add( eachProgram.getId() ); }
         * patientModel.setProgramsID( mobileProgramIDList );
         * 
         * // Set patient Data value for off-line storage function Map<Integer,
         * String> patientDataValues = new HashMap<Integer, String>(); for (
         * ProgramInstance eachProgramInstance :
         * programInstanceService.getProgramInstances( patient,
         * ProgramInstance.STATUS_ACTIVE ) ) { for ( ProgramStageInstance
         * eachProgramStageInstance :
         * eachProgramInstance.getProgramStageInstances() ) { for (
         * PatientDataValue each : patientDataValueService.getPatientDataValues(
         * eachProgramStageInstance ) ) { if( each.getValue() != null &&
         * !each.getValue().isEmpty()) { Integer dataElementID =
         * each.getDataElement().getId(); String value = each.getValue();
         * patientDataValues.put( dataElementID, value ); } } } }
         * patientModel.setPatientDataValues( patientDataValues );
         */

        // Set Relationship
        List<Relationship> relationships = new ArrayList<Relationship>(
            relationshipService.getRelationshipsForPatient( patient ) );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship> relationshipList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>();

        for ( Relationship eachRelationship : relationships )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship relationshipMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship();
            relationshipMobile.setId( eachRelationship.getId() );
            if ( eachRelationship.getPatientA().getId() == patient.getId() )
            {
                relationshipMobile.setName( eachRelationship.getRelationshipType().getaIsToB() );
                relationshipMobile.setPersonBName( eachRelationship.getPatientB().getFullName() );
                relationshipMobile.setPersonBId( eachRelationship.getPatientB().getId() );
                // relationshipMobile.setPersonAName(
                // eachRelationship.getPatientA().getFullName() );
                // relationshipMobile.setPersonAId(
                // eachRelationship.getPatientA().getId() );
            }
            else
            {
                relationshipMobile.setName( eachRelationship.getRelationshipType().getbIsToA() );
                relationshipMobile.setPersonBName( eachRelationship.getPatientA().getFullName() );
                relationshipMobile.setPersonBId( eachRelationship.getPatientA().getId() );
                // relationshipMobile.setPersonAName(
                // eachRelationship.getPatientB().getFullName() );
                // relationshipMobile.setPersonAId(
                // eachRelationship.getPatientB().getId() );
            }
            relationshipList.add( relationshipMobile );
        }
        patientModel.setRelationships( relationshipList );

        // Set available enrollment programs
        List<Program> enrollmentProgramList = new ArrayList<Program>();
        enrollmentProgramList = generateEnrollmentProgramList( patient );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Program> enrollmentProgramListMobileList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Program>();

        for ( Program enrollmentProgram : enrollmentProgramList )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Program enrollmentProgramMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();
            enrollmentProgramMobile.setId( enrollmentProgram.getId() );
            enrollmentProgramMobile.setName( enrollmentProgram.getName() );
            enrollmentProgramMobile.setStatus( ProgramInstance.STATUS_ACTIVE );
            enrollmentProgramMobile.setVersion( enrollmentProgram.getVersion() );
            enrollmentProgramMobile.setProgramStages( null );
            enrollmentProgramListMobileList.add( enrollmentProgramMobile );
        }
        patientModel.setEnrollmentPrograms( enrollmentProgramListMobileList );

        // Set available enrollment relationships
        List<RelationshipType> enrollmentRelationshipList = new ArrayList<RelationshipType>(
            relationshipTypeService.getAllRelationshipTypes() );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship> enrollmentRelationshipMobileList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>();
        for ( RelationshipType enrollmentRelationship : enrollmentRelationshipList )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship enrollmentRelationshipMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship();
            enrollmentRelationshipMobile.setId( enrollmentRelationship.getId() );
            enrollmentRelationshipMobile.setName( enrollmentRelationship.getName() );
            enrollmentRelationshipMobile.setaIsToB( enrollmentRelationship.getaIsToB() );
            enrollmentRelationshipMobile.setbIsToA( enrollmentRelationship.getbIsToA() );
            enrollmentRelationshipMobileList.add( enrollmentRelationshipMobile );
        }
        patientModel.setEnrollmentRelationships( enrollmentRelationshipMobileList );
        return patientModel;
    }

    private org.hisp.dhis.api.mobile.model.LWUITmodel.Program getMobileProgram( Patient patient,
        ProgramInstance programInstance )
    {
        org.hisp.dhis.api.mobile.model.LWUITmodel.Program mobileProgram = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();

        mobileProgram.setVersion( programInstance.getProgram().getVersion() );
        mobileProgram.setId( programInstance.getId() );
        mobileProgram.setName( programInstance.getProgram().getName() );
        mobileProgram.setStatus( programInstance.getStatus() );
        mobileProgram.setProgramStages( getMobileProgramStages( patient, programInstance ) );
        return mobileProgram;
    }

    private List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> getMobileProgramStages( Patient patient,
        ProgramInstance programInstance )
    {
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> mobileProgramStages = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();
        for ( ProgramStage eachProgramStage : programInstance.getProgram().getProgramStages() )
        {
            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance(
                programInstance, eachProgramStage );

            // only for Mujhu database, because there is null program stage
            // instance. This condition should be removed in the future
            if ( programStageInstance != null )
            {
                org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();
                List<org.hisp.dhis.api.mobile.model.LWUITmodel.Section> mobileSections = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Section>();
                mobileProgramStage.setId( programStageInstance.getId() );
                mobileProgramStage.setName( eachProgramStage.getName() );

                // get report date
                if ( programStageInstance.getExecutionDate() != null )
                {
                    mobileProgramStage
                        .setReportDate( PeriodUtil.dateToString( programStageInstance.getExecutionDate() ) );
                }
                else
                {
                    mobileProgramStage.setReportDate( "" );
                }

                if ( programStageInstance.getProgramStage().getReportDateDescription() == null )
                {
                    mobileProgramStage.setReportDateDescription( "Report Date" );
                }
                else
                {
                    mobileProgramStage.setReportDateDescription( programStageInstance.getProgramStage()
                        .getReportDateDescription() );
                }

                // is repeatable
                mobileProgramStage.setRepeatable( eachProgramStage.getIrregular() );

                // is completed
                mobileProgramStage.setCompleted( checkIfProgramStageCompleted( patient, programInstance.getProgram(),
                    eachProgramStage ) );

                // is single event
                mobileProgramStage.setSingleEvent( programInstance.getProgram().isSingleEvent() );

                // Set all data elements
                mobileProgramStage.setDataElements( getDataElementsForMobile( eachProgramStage, programStageInstance ) );

                // Set all program sections
                if ( eachProgramStage.getProgramStageSections().size() > 0 )
                {
                    for ( ProgramStageSection eachSection : eachProgramStage.getProgramStageSections() )
                    {
                        org.hisp.dhis.api.mobile.model.LWUITmodel.Section mobileSection = new org.hisp.dhis.api.mobile.model.LWUITmodel.Section();
                        mobileSection.setId( eachSection.getId() );
                        mobileSection.setName( eachSection.getName() );

                        // Set all data elements' id, then we could have full
                        // from
                        // data element list of program stage
                        List<Integer> dataElementIds = new ArrayList<Integer>();
                        for ( ProgramStageDataElement eachPogramStageDataElement : eachSection
                            .getProgramStageDataElements() )
                        {
                            dataElementIds.add( eachPogramStageDataElement.getDataElement().getId() );
                        }
                        mobileSection.setDataElementIds( dataElementIds );
                        mobileSections.add( mobileSection );
                    }
                }
                mobileProgramStage.setSections( mobileSections );

                mobileProgramStages.add( mobileProgramStage );
            }
        }
        return mobileProgramStages;
    }

    private boolean checkIfProgramStageCompleted( Patient patient, Program program, ProgramStage programstage )
    {
        Collection<ProgramInstance> programIntances = programInstanceService.getProgramInstances( patient, program,
            ProgramInstance.STATUS_ACTIVE );
        ProgramStageInstance programStageInstance = new ProgramStageInstance();
        if ( programIntances != null && programIntances.size() == 1 )
        {
            for ( ProgramInstance each : programIntances )
            {
                programStageInstance = programStageInstanceService.getProgramStageInstance( each, programstage );
            }
        }
        return programStageInstance.isCompleted();
    }

    private List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> getDataElementsForMobile(
        ProgramStage programStage, ProgramStageInstance programStageInstance )
    {
        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(
            programStage.getProgramStageDataElements() );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> mobileDataElements = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();
        for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement();
            mobileDataElement.setId( programStageDataElement.getDataElement().getId() );

            String dataElementName;

            if ( programStageDataElement.getDataElement().getFormName() != null && !programStageDataElement.getDataElement().getFormName().trim().equals( "" ) )
            {
                dataElementName = programStageDataElement.getDataElement().getFormName();
            }
            else
            {
                dataElementName = programStageDataElement.getDataElement().getName();
            }

            mobileDataElement.setName( dataElementName );
            mobileDataElement.setType( programStageDataElement.getDataElement().getType() );

            // problem
            mobileDataElement.setCompulsory( programStageDataElement.isCompulsory() );

            mobileDataElement.setNumberType( programStageDataElement.getDataElement().getNumberType() );

            // Value
            PatientDataValue patientDataValue = dataValueService.getPatientDataValue( programStageInstance,
                programStageDataElement.getDataElement() );
            if ( patientDataValue != null )
            {
                mobileDataElement.setValue( patientDataValue.getValue() );
            }
            else
            {
                mobileDataElement.setValue( null );
            }

            // Option set
            if ( programStageDataElement.getDataElement().getOptionSet() != null )
            {
                mobileDataElement.setOptionSet( modelMapping.getOptionSet( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setOptionSet( null );
            }

            // Category Option Combo
            if ( programStageDataElement.getDataElement().getCategoryCombo() != null )
            {
                mobileDataElement.setCategoryOptionCombos( modelMapping
                    .getCategoryOptionCombos( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setCategoryOptionCombos( null );
            }
            mobileDataElements.add( mobileDataElement );
        }
        return mobileDataElements;
    }

    private PatientMobileSetting getSettings()
    {
        PatientMobileSetting setting = null;

        Collection<PatientMobileSetting> currentSetting = patientMobileSettingService.getCurrentSetting();
        if ( currentSetting != null && !currentSetting.isEmpty() )
            setting = currentSetting.iterator().next();
        return setting;
    }

    private List<Program> generateEnrollmentProgramList( Patient patient )
    {
        List<Program> programs = new ArrayList<Program>();

        // for ( Program program : programService.getPrograms(
        // orgUnitService.getOrganisationUnit( orgId ) ) )
        for ( Program program : programService.getPrograms( patient.getOrganisationUnit() ) )
        {
            if ( (program.isSingleEvent() && program.isRegistration()) || !program.isSingleEvent() )
            {
                // wrong here
                if ( programInstanceService.getProgramInstances( patient, program ).size() == 0 )
                {
                    programs.add( program );
                }
            }
        }
        return programs;
    }

    private boolean isNumber( String value )
    {
        try
        {
            Double.parseDouble( value );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
        return true;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient addRelationship(
        org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship enrollmentRelationship, int orgUnitId )
        throws NotAllowedException
    {
        Patient patientB;
        if ( enrollmentRelationship.getPersonBId() != 0 )
        {
            patientB = patientService.getPatient( enrollmentRelationship.getPersonBId() );
        }
        else
        {
            String fullName = enrollmentRelationship.getPersonBName();
            int startIndex = fullName.indexOf( ' ' );
            int endIndex = fullName.lastIndexOf( ' ' );

            String firstName = fullName.toString();
            String middleName = " ";
            String lastName = " ";

            if ( fullName.indexOf( ' ' ) != -1 )
            {
                firstName = fullName.substring( 0, startIndex );
                if ( startIndex == endIndex )
                {
                    middleName = "  ";
                    lastName = fullName.substring( startIndex + 1, fullName.length() );
                }
                else
                {
                    middleName = " " + fullName.substring( startIndex + 1, endIndex ) + " ";
                    lastName = fullName.substring( endIndex + 1, fullName.length() );
                }
            }
            List<Patient> patients = (List<Patient>) this.patientService.getPatientByFullname( firstName + middleName
                + lastName, orgUnitId );

            // remove the own searcher
            patients = removeIfDuplicated( patients, enrollmentRelationship.getPersonAId() );

            if ( patients.size() > 1 )
            {
                String patientsInfo = new String();

                DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

                for ( Patient each : patients )
                {
                    patientsInfo += each.getId() + "/" + each.getFullName() + "/"
                        + dateFormat.format( each.getBirthDate() ) + "$";
                }

                throw new NotAllowedException( patientsInfo );
            }
            else if ( patients.size() == 0 )
            {
                throw NotAllowedException.NO_BENEFICIARY_FOUND;
            }
            else
            {
                patientB = patients.get( 0 );
            }
        }
        Patient patientA = patientService.getPatient( enrollmentRelationship.getPersonAId() );
        RelationshipType relationshipType = relationshipTypeService
            .getRelationshipType( enrollmentRelationship.getId() );

        Relationship relationship = new Relationship();
        relationship.setRelationshipType( relationshipType );
        if ( enrollmentRelationship.getChosenRelationship().equals( relationshipType.getaIsToB() ) )
        {
            relationship.setPatientA( patientA );
            relationship.setPatientB( patientB );
        }
        else
        {
            relationship.setPatientA( patientB );
            relationship.setPatientB( patientA );
        }
        relationshipService.saveRelationship( relationship );
        // return getPatientModel( orgUnitId, patientA );
        return getPatientModel( patientA );
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program getAllAnonymousProgram( int orgUnitId )
        throws NotAllowedException
    {
        String programsInfo = "";

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        List<Program> tempPrograms = new ArrayList<Program>(
            programService.getProgramsByCurrentUser( Program.SINGLE_EVENT_WITHOUT_REGISTRATION ) );
        List<Program> programs = new ArrayList<Program>();

        for ( Program program : tempPrograms )
        {
            if ( program.getOrganisationUnits().contains( organisationUnit ) )
            {
                programs.add( program );
            }
        }

        if ( programs.size() != 0 )
        {
            if ( programs.size() == 1 )
            {
                Program anonymousProgram = programs.get( 0 );

                return getMobileAnonymousProgram( anonymousProgram );
            }
            else
            {
                for ( Program program : programs )
                {
                    if ( program.getOrganisationUnits().contains( organisationUnit ) )
                    {
                        programsInfo += program.getId() + "/" + program.getName() + "$";
                    }
                }
                throw new NotAllowedException( programsInfo );
            }
        }
        else
        {
            throw NotAllowedException.NO_PROGRAM_FOUND;
        }
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program findProgram( String programInfo )
        throws NotAllowedException
    {
        if ( isNumber( programInfo ) == false )
        {
            return null;
        }
        else
        {
            Program program = programService.getProgram( Integer.parseInt( programInfo ) );
            if ( program.isSingleEvent() )
            {
                return getMobileAnonymousProgram( program );
            }
            else
            {
                return null;
            }
        }
    }

    private org.hisp.dhis.api.mobile.model.LWUITmodel.Program getMobileAnonymousProgram( Program anonymousProgram )
    {
        Comparator<ProgramStageDataElement> OrderBySortOrder = new Comparator<ProgramStageDataElement>()
        {
            public int compare( ProgramStageDataElement i1, ProgramStageDataElement i2 )
            {
                return i1.getSortOrder().compareTo( i2.getSortOrder() );
            }
        };

        org.hisp.dhis.api.mobile.model.LWUITmodel.Program anonymousProgramMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();

        anonymousProgramMobile.setId( anonymousProgram.getId() );

        anonymousProgramMobile.setName( anonymousProgram.getName() );

        anonymousProgramMobile.setVersion( anonymousProgram.getVersion() );

        anonymousProgramMobile.setStatus( ProgramInstance.STATUS_ACTIVE );

        ProgramStage programStage = anonymousProgram.getProgramStages().iterator().next();

        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(
            programStage.getProgramStageDataElements() );
        Collections.sort( programStageDataElements, OrderBySortOrder );

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> mobileProgramStages = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();

        org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> mobileProgramStageDataElements = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();

        mobileProgramStage.setId( programStage.getId() );
        mobileProgramStage.setName( programStage.getName() );
        mobileProgramStage.setCompleted( false );
        mobileProgramStage.setRepeatable( false );
        mobileProgramStage.setSingleEvent( true );
        mobileProgramStage.setSections( new ArrayList<Section>() );

        for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement();
            mobileDataElement.setId( programStageDataElement.getDataElement().getId() );
            mobileDataElement.setName( programStageDataElement.getDataElement().getName() );
            mobileDataElement.setType( programStageDataElement.getDataElement().getType() );

            // problem
            mobileDataElement.setCompulsory( programStageDataElement.isCompulsory() );

            mobileDataElement.setNumberType( programStageDataElement.getDataElement().getNumberType() );

            mobileDataElement.setValue( "" );

            if ( programStageDataElement.getDataElement().getOptionSet() != null )
            {
                mobileDataElement.setOptionSet( modelMapping.getOptionSet( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setOptionSet( null );
            }
            if ( programStageDataElement.getDataElement().getCategoryCombo() != null )
            {
                mobileDataElement.setCategoryOptionCombos( modelMapping
                    .getCategoryOptionCombos( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setCategoryOptionCombos( null );
            }
            mobileProgramStageDataElements.add( mobileDataElement );
        }
        mobileProgramStage.setDataElements( mobileProgramStageDataElements );
        mobileProgramStages.add( mobileProgramStage );
        anonymousProgramMobile.setProgramStages( mobileProgramStages );

        return anonymousProgramMobile;
    }

    private List<Patient> removeIfDuplicated( List<Patient> patients, int patientId )
    {
        List<Patient> result = new ArrayList<Patient>( patients );
        for ( int i = 0; i < patients.size(); i++ )
        {
            if ( patients.get( i ).getId() == patientId )
            {
                result.remove( i );
            }
        }
        return result;
    }

    private void saveDataValues( ActivityValue activityValue, ProgramStageInstance programStageInstance,
        Map<Integer, DataElement> dataElementMap )
    {
        org.hisp.dhis.dataelement.DataElement dataElement;
        String value;

        for ( DataValue dv : activityValue.getDataValues() )
        {
            value = dv.getValue();

            if ( value != null && value.trim().length() == 0 )
            {
                value = null;
            }

            if ( value != null )
            {
                value = value.trim();
            }

            dataElement = dataElementMap.get( dv.getId() );
            PatientDataValue dataValue = dataValueService.getPatientDataValue( programStageInstance, dataElement );
            if ( dataValue == null )
            {
                if ( value != null )
                {
                    if ( programStageInstance.getExecutionDate() == null )
                    {
                        programStageInstance.setExecutionDate( new Date() );
                        programStageInstanceService.updateProgramStageInstance( programStageInstance );
                    }

                    dataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );

                    dataValueService.savePatientDataValue( dataValue );
                }
            }
            else
            {
                if ( programStageInstance.getExecutionDate() == null )
                {
                    programStageInstance.setExecutionDate( new Date() );
                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }

                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );

                dataValueService.updatePatientDataValue( dataValue );
            }
        }
    }

    // Setters...

    @Required
    public void setProgramStageInstanceService(
        org.hisp.dhis.program.ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    @Required
    public void setPatientAttValueService( PatientAttributeValueService patientAttValueService )
    {
        this.patientAttValueService = patientAttValueService;
    }

    @Required
    public void setPatientAttService( PatientAttributeService patientAttService )
    {
        this.patientAttService = patientAttService;
    }

    @Required
    public void setDataValueService( org.hisp.dhis.patientdatavalue.PatientDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setPatientMobileSettingService( PatientMobileSettingService patientMobileSettingService )
    {
        this.patientMobileSettingService = patientMobileSettingService;
    }

    @Required
    public void setModelMapping( org.hisp.dhis.mobile.service.ModelMapping modelMapping )
    {
        this.modelMapping = modelMapping;
    }

    public PatientMobileSetting getSetting()
    {
        return setting;
    }

    public void setSetting( PatientMobileSetting setting )
    {
        this.setting = setting;
    }

    public org.hisp.dhis.patient.PatientAttribute getGroupByAttribute()
    {
        return groupByAttribute;
    }

    public void setGroupByAttribute( org.hisp.dhis.patient.PatientAttribute groupByAttribute )
    {
        this.groupByAttribute = groupByAttribute;
    }

    @Required
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    @Required
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    @Required
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    @Required
    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    @Required
    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }

    @Required
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    @Required
    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    @Required
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        patientIdentifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        Collection<Program> programs = programService.getAllPrograms();
        for ( Program program : programs )
        {
            patientIdentifierTypes.removeAll( program.getPatientIdentifierTypes() );
        }
        return patientIdentifierTypes;
    }

    public Collection<org.hisp.dhis.patient.PatientAttribute> getPatientAtts()
    {

        patientAttributes = patientAttributeService.getAllPatientAttributes();

        // Collection<Program> programs = programService.getAllPrograms();
        //
        // for ( Program program : programs )
        // {
        // patientAttributes.removeAll( program.getPatientAttributes() );
        // }

        return patientAttributes;
    }

    public Collection<PatientIdentifierType> getIdentifiers()
    {
        patientIdentifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        // Collection<Program> programs = programService.getAllPrograms();
        // for ( Program program : programs )
        // {
        // patientIdentifierTypes.removeAll( program.getPatientIdentifierTypes()
        // );
        // }
        return patientIdentifierTypes;

    }

    public Collection<PatientAttribute> getAttsForMobile()
    {
        Collection<PatientAttribute> list = new HashSet<PatientAttribute>();

        for ( org.hisp.dhis.patient.PatientAttribute patientAtt : getPatientAtts() )
        {
            list.add( new PatientAttribute( patientAtt.getName(), null, patientAtt.getValueType(),
                new ArrayList<String>() ) );
        }

        return list;

    }

    @Override
    public Collection<org.hisp.dhis.api.mobile.model.PatientIdentifier> getIdentifiersForMobile()
    {
        Collection<org.hisp.dhis.api.mobile.model.PatientIdentifier> list = new HashSet<org.hisp.dhis.api.mobile.model.PatientIdentifier>();
        for ( PatientIdentifierType identifierType : getIdentifiers() )
        {
            String id = "";
            String idt = identifierType.getName();
            if ( identifierType.isMandatory() == true )
            {
                idt += " (*)";
            }
            list.add( new org.hisp.dhis.api.mobile.model.PatientIdentifier( idt, id ) );
        }
        return list;
    }

    @Override
    public Collection<PatientAttribute> getPatientAttributesForMobile()
    {
        Collection<PatientAttribute> list = new HashSet<PatientAttribute>();
        for ( org.hisp.dhis.patient.PatientAttribute pa : getPatientAtts() )
        {
            PatientAttribute patientAttribute = new PatientAttribute();
            String name = pa.getName();
            // if ( pa.isMandatory() == true )
            // {
            // name += " (*)";
            // }
            patientAttribute.setName( name );
            patientAttribute.setType( pa.getValueType() );
            patientAttribute.setValue( "" );
            List<String> optionList = new ArrayList<String>();
            if ( pa.getAttributeOptions() != null )
            {
                for ( PatientAttributeOption option : pa.getAttributeOptions() )
                {
                    optionList.add( option.getName() );
                }
            }
            // list.add( new PatientAttribute( name, value, pa.getValueType(),
            // pa.getP ) );
            patientAttribute.setPredefinedValues( optionList );
            list.add( patientAttribute );
        }
        return list;
    }

    @Required
    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Required
    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findLatestPatient()
        throws NotAllowedException
    {
        Patient patient = patientService.getPatient( this.patientId );

        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patient );
        return patientMobile;
    }

    @Override
    public Integer savePatient( org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patient, int orgUnitId, String programIdText )
        throws NotAllowedException
    {
        org.hisp.dhis.patient.Patient patientWeb = new org.hisp.dhis.patient.Patient();

        int startIndex = patient.getFirstName().indexOf( ' ' );
        int endIndex = patient.getFirstName().lastIndexOf( ' ' );

        String firstName = patient.getFirstName().toString();
        String middleName = "";
        String lastName = "";

        if ( patient.getFirstName().indexOf( ' ' ) != -1 )
        {
            firstName = patient.getFirstName().substring( 0, startIndex );
            if ( startIndex == endIndex )
            {
                middleName = "";
                lastName = patient.getFirstName().substring( startIndex + 1, patient.getFirstName().length() );
            }
            else
            {
                middleName = patient.getFirstName().substring( startIndex + 1, endIndex );
                lastName = patient.getFirstName().substring( endIndex + 1, patient.getFirstName().length() );
            }
        }

        patientWeb.setFirstName( firstName );
        patientWeb.setMiddleName( middleName );
        patientWeb.setLastName( lastName );
        patientWeb.setGender( patient.getGender() );
        patientWeb.setDobType( patient.getDobType() );
        patientWeb.setPhoneNumber( patient.getPhoneNumber() );
        patientWeb.setBirthDate( patient.getBirthDate() );
        patientWeb.setOrganisationUnit( organisationUnitService.getOrganisationUnit( orgUnitId ) );
        patientWeb.setRegistrationDate( new Date() );

        Set<org.hisp.dhis.patient.PatientIdentifier> patientIdentifierSet = new HashSet<org.hisp.dhis.patient.PatientIdentifier>();
        Set<org.hisp.dhis.patient.PatientAttribute> patientAttributeSet = new HashSet<org.hisp.dhis.patient.PatientAttribute>();
        List<PatientAttributeValue> patientAttributeValues = new ArrayList<PatientAttributeValue>();

        Collection<org.hisp.dhis.api.mobile.model.PatientIdentifier> identifiers = patient.getIdentifiers();

        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        Collection<org.hisp.dhis.api.mobile.model.PatientAttribute> patientAttributesMobile = patient
            .getPatientAttValues();

        if ( identifierTypes.size() > 0 )
        {
            for ( org.hisp.dhis.api.mobile.model.PatientIdentifier identifier : identifiers )
            {
                PatientIdentifierType patientIdentifierType = patientIdentifierTypeService
                    .getPatientIdentifierType( identifier.getIdentifierType() );

                org.hisp.dhis.patient.PatientIdentifier patientIdentifier = new org.hisp.dhis.patient.PatientIdentifier();

                patientIdentifier.setIdentifierType( patientIdentifierType );
                patientIdentifier.setPatient( patientWeb );
                patientIdentifier.setIdentifier( identifier.getIdentifier() );
                patientIdentifierSet.add( patientIdentifier );
            }
        }
        // --------------------------------------------------------------------------------
        // Generate system id with this format :
        // (BirthDate)(Gender)(XXXXXX)(checkdigit)
        // PatientIdentifierType will be null
        // --------------------------------------------------------------------------------
        if ( identifierTypes.size() == 0 )
        {
            String identifier = PatientIdentifierGenerator.getNewIdentifier( patient.getBirthDate(),
                patient.getGender() );

            org.hisp.dhis.patient.PatientIdentifier systemGenerateIdentifier = new org.hisp.dhis.patient.PatientIdentifier();
            systemGenerateIdentifier.setIdentifier( identifier );
            systemGenerateIdentifier.setIdentifierType( null );
            systemGenerateIdentifier.setPatient( patientWeb );
            patientIdentifierSet.add( systemGenerateIdentifier );
        }

        if ( patientAttributesMobile != null )
        {
            for ( org.hisp.dhis.api.mobile.model.PatientAttribute paAtt : patientAttributesMobile )
            {

                org.hisp.dhis.patient.PatientAttribute patientAttribute = patientAttributeService
                    .getPatientAttributeByName( paAtt.getName() );

                patientAttributeSet.add( patientAttribute );

                PatientAttributeValue patientAttributeValue = new PatientAttributeValue();

                patientAttributeValue.setPatient( patientWeb );
                patientAttributeValue.setPatientAttribute( patientAttribute );
                patientAttributeValue.setValue( paAtt.getValue() );
                patientAttributeValues.add( patientAttributeValue );

            }
        }

        patientWeb.setIdentifiers( patientIdentifierSet );
        patientWeb.setAttributes( patientAttributeSet );

        patientId = patientService.createPatient( patientWeb, null, null, patientAttributeValues );
        
        try
        {
            int programId = Integer.parseInt( programIdText );
            this.enrollProgram( patientId + "-" + programId );
        }
        catch ( Exception e )
        {
            return patientId;
        }
        

        return patientId;

    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findPatient( int patientId )
        throws NotAllowedException
    {
        Patient patient = patientService.getPatient( patientId );
        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patient );
        return patientMobile;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findPatientInAdvanced( String keyword, int orgUnitId,
        int programId )
        throws NotAllowedException
    {
        Collection<Patient> patients = new HashSet<Patient>( patientService.getPatientsForMobile( keyword, orgUnitId ) );
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        if ( programId != 0 )
        {
            Program program = programService.getProgram( programId );
            List<Patient> tempPatients = (List<Patient>) patientService.getPatients( orgUnit, program, null, null );
            patients.retainAll( tempPatients );
        }

        if ( programId != 0 && orgUnitId != 0 )
        {
            boolean isProgramBelongToOrgUnit = false;
            for ( Program program : programService.getPrograms( orgUnit ) )
            {
                if ( program.getId() == programId )
                {
                    isProgramBelongToOrgUnit = true;
                    break;
                }
            }
            if ( isProgramBelongToOrgUnit == false )
            {
                throw NotAllowedException.NO_PROGRAM_BELONG_ORGUNIT;
            }
        }

        if ( patients.size() > 1 )
        {
            String patientsInfo = new String();

            DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

            int i = 1;
            for ( Patient each : patients )
            {
                if ( i > 10 )
                {
                    break;
                }
                if ( each.getBirthDate() != null )
                {
                    patientsInfo += each.getId() + "/" + each.getFullName() + "/"
                        + dateFormat.format( each.getBirthDate() ) + "$";
                }
                else
                {
                    patientsInfo += each.getId() + "/" + each.getFullName() + "/DOB$";
                }
                i++;
            }

            throw new NotAllowedException( patientsInfo );
        }
        else if ( patients.size() == 0 )
        {
            throw NotAllowedException.NO_BENEFICIARY_FOUND;
        }
        else
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Patient();
            for ( Patient each : patients )
            {
                patientMobile = getPatientModel( each );
                break;
            }
            return patientMobile;
        }
    }
}
