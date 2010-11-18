package org.hisp.dhis.web.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.Beneficiary;
import org.hisp.dhis.web.api.model.PatientAttribute;
import org.hisp.dhis.web.api.model.comparator.ActivityComparator;
import org.hisp.dhis.web.api.service.mapping.TaskMapper;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

public class DefaultActivityPlanService
    implements IActivityPlanService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private org.hisp.dhis.activityplan.ActivityPlanService activityPlanService;

    private ActivityComparator activityComparator = new ActivityComparator();
    
    public org.hisp.dhis.activityplan.ActivityPlanService getActivityPlanService()
    {
        return activityPlanService;
    }

    public void setActivityPlanService( org.hisp.dhis.activityplan.ActivityPlanService activityPlanService )
    {
        this.activityPlanService = activityPlanService;
    }

    private PatientAttributeValueService patientAttValueService;

    public PatientAttributeValueService getPatientAttValueService()
    {
        return patientAttValueService;
    }

    public void setPatientAttValueService( PatientAttributeValueService patientAttValueService )
    {
        this.patientAttValueService = patientAttValueService;
    }

    private PatientAttributeService patientAttService;

    public PatientAttributeService getPatientAttService()
    {
        return patientAttService;
    }

    public void setPatientAttService( PatientAttributeService patientAttService )
    {
        this.patientAttService = patientAttService;
    }

    private CurrentUserService currentUserService;

    public CurrentUserService getCurrentUserService()
    {
        return currentUserService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------

    public ActivityPlan getCurrentActivityPlan( String localeString )
    {
        Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();
        OrganisationUnit unit = null;

        if ( units.size() > 0 )
        {
            unit = units.iterator().next();
        }
        else
        {
            return null;
        }

        DateTime dt = new DateTime();
        DateMidnight from = dt.withDayOfMonth( 1 ).toDateMidnight();
        DateMidnight to = from.plusMonths( 1 );

        Collection<Activity> allActivities = activityPlanService.getActivitiesByProvider( unit );

        ActivityPlan plan = new ActivityPlan();

        List<org.hisp.dhis.web.api.model.Activity> items = new ArrayList<org.hisp.dhis.web.api.model.Activity>();

        int i = 0;
        for ( Activity activity : allActivities )
        {
            // there are error on db with patientattributeid 14, so I limit the
            // patient to be downloaded
            if ( i > 10 )
            {
                break;
            }

            long dueTime = activity.getDueDate().getTime();
            if ( to.isBefore( dueTime ) )
            {
                continue;
            }

            if ( from.isBefore( dueTime ) )
            {
                items.add( getActivityModel( activity ) );
                i++;
            }
            else if ( !activity.getTask().isCompleted() )
            {
                org.hisp.dhis.web.api.model.Activity a = getActivityModel( activity );
                items.add( a );
                a.setLate( true );
                i++;
            }
        }
        if ( !items.isEmpty() )
        {
            Collections.sort( items, activityComparator );
            plan.setActivitiesList( items );
        }

        return plan;

    }
    
    

    private org.hisp.dhis.web.api.model.Activity getActivityModel( org.hisp.dhis.activityplan.Activity activity )
    {
        if ( activity == null )
        {
            return null;
        }
        org.hisp.dhis.web.api.model.Activity item = new org.hisp.dhis.web.api.model.Activity();
        Patient patient = activity.getBeneficiary();

        item.setBeneficiary( getBeneficiaryModel( patient ) );
        item.setDueDate( activity.getDueDate() );
        item.setTask( new TaskMapper().getModel( activity.getTask() ) );
        return item;
    }

    private org.hisp.dhis.web.api.model.Beneficiary getBeneficiaryModel( Patient patient )
    {

        Beneficiary beneficiary = new Beneficiary();

        List<PatientAttribute> patientAtts = new ArrayList<PatientAttribute>();

        beneficiary.setId( patient.getId() );
        beneficiary.setFirstName( patient.getFirstName() );
        beneficiary.setLastName( patient.getLastName() );
        beneficiary.setMiddleName( patient.getMiddleName() );
        int currentYear = new Date().getYear();
        int age = currentYear - patient.getBirthDate().getYear();
        beneficiary.setAge( age );

        // Set attribute which is used to group beneficiary on mobile (only if
        // there is attribute which is set to be group factor)
        PatientAttribute beneficiaryAttribute = null;
        org.hisp.dhis.patient.PatientAttribute patientAttribute = patientAttService.getPatientAttributeByGroupBy( true );

        if ( patientAttribute != null )
        {
            beneficiaryAttribute = new PatientAttribute();
            beneficiaryAttribute.setName( patientAttribute.getName() );
            PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, patientAttribute );
            beneficiaryAttribute.setValue( value == null ? "Unknown" : value.getValue() );
            beneficiary.setGroupAttribute( beneficiaryAttribute );
        }
        patientAttribute = null;

        // Set all attributes
        org.hisp.dhis.patient.PatientAttribute houseName = patientAttService.getPatientAttributeByName( "House Name" );
        org.hisp.dhis.patient.PatientAttribute houseNumber = patientAttService
            .getPatientAttributeByName( "House Number" );
        org.hisp.dhis.patient.PatientAttribute wardNumber = patientAttService.getPatientAttributeByName( "Ward Number" );
        org.hisp.dhis.patient.PatientAttribute nearestContact = patientAttService
            .getPatientAttributeByName( "Nearest Contact Person Name" );

        PatientAttributeValue houseNameValue = patientAttValueService.getPatientAttributeValue( patient, houseName );
        if ( houseNameValue != null )
        {
            patientAtts.add( new PatientAttribute( "House Name", houseNameValue.getValue() ) );
        }

        PatientAttributeValue houseNumberValue = patientAttValueService.getPatientAttributeValue( patient, houseNumber );
        if ( houseNumberValue != null )
        {
            patientAtts.add( new PatientAttribute( "House Number", houseNumberValue.getValue() ) );
        }

        PatientAttributeValue wardNumberValue = patientAttValueService.getPatientAttributeValue( patient, wardNumber );
        if ( wardNumberValue != null )
        {
            patientAtts.add( new PatientAttribute( "Ward Number", wardNumberValue.getValue() ) );
        }

        PatientAttributeValue nearestContactValue = patientAttValueService.getPatientAttributeValue( patient,
            nearestContact );
        if ( nearestContactValue != null )
        {
            patientAtts.add( new PatientAttribute( "Nearest Contact", nearestContactValue.getValue() ) );
        }

        beneficiary.setPatientAttValues( patientAtts );

        // for ( PatientAttributeValue patientAttributeValue :
        // patientAttValueService.getPatientAttributeValues( patient ) )
        // {
        // patientAttValues.add(
        // patientAttributeValue.getPatientAttribute().getName() + " : "
        // + patientAttributeValue.getValue() );
        // }
        // beneficiary.setPatientAttValues( patientAttValues );

        return beneficiary;
    }
}
