package org.hisp.dhis.patient.api.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.activityplan.ActivityPlanService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.model.ActivityPlanItem;
import org.hisp.dhis.patient.api.model.Beneficiary;
import org.hisp.dhis.patient.api.model.Task;
import org.hisp.dhis.patient.api.service.mapping.ActivityPlanItemMapper;
import org.hisp.dhis.patient.api.service.mapping.ActivityPlanMapper;
import org.hisp.dhis.patient.api.service.mapping.BeanMapper;
import org.hisp.dhis.patient.api.service.mapping.BeneficiaryMapper;
import org.hisp.dhis.patient.api.service.mapping.TaskMapper;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.junit.Before;
import org.junit.Test;

public class ActivityPlanModelServiceTest
{
    private MappingFactory mapper = new MappingFactory();

    private ActivityPlanModelService service;

    private Date date = new Date();

    private OrganisationUnitService mockedOrgUnitService;

    private ActivityPlanService mockedActivityPlanService;

    private OrganisationUnit orgUnit;

    private Collection<Activity> activities;

    @Before
    public void setup()
    {
        orgUnit = new OrganisationUnit();
        orgUnit.setId( 1 );

        activities = new ArrayList<Activity>();
        activities.add( createActivity( 1 ) );

        mockedOrgUnitService = mock( OrganisationUnitService.class );
        when( mockedOrgUnitService.getOrganisationUnit( 1 ) ).thenReturn( orgUnit );

        mockedActivityPlanService = mock( ActivityPlanService.class );
        when( mockedActivityPlanService.getActivitiesByProvider( orgUnit ) ).thenReturn( activities );

        Set<BeanMapper<?, ?>> mappers = new HashSet<BeanMapper<?, ?>>();
        mappers.add( new ActivityPlanMapper() );
        mappers.add( new ActivityPlanItemMapper() );
        mappers.add( new BeneficiaryMapper() );
        mappers.add( new TaskMapper() );
        mapper.setMappers( mappers );

        service = new ActivityPlanModelService();
        service.setActivityPlanService( mockedActivityPlanService );
        service.setOrganisationUnitService( mockedOrgUnitService );
        service.setMappingManager( mapper );
    }

    @Test
    public void getActivityPlan()
    {
        ActivityPlan activityPlan = service.getCurrentActivityPlan( orgUnit );

        assertNotNull( activityPlan );
        List<ActivityPlanItem> activities = activityPlan.getActivitiesList();
        assertNotNull( activities );
        assertEquals( 1, activities.size() );

        int i = 1;
        for ( ActivityPlanItem item : activities )
        {
            assertItem( item, i++ );
        }
    }

    private void assertItem( ActivityPlanItem item, int i )
    {
        Beneficiary beneficiary = item.getBeneficiary();
        assertNotNull( beneficiary );
        assertEquals( i, beneficiary.getId() );
        assertEquals( i, beneficiary.getId() );
        assertEquals( date, item.getDueDate() );
        Task task = item.getTask();
        assertNotNull( task );
        assertEquals( i, task.getId() );
        assertEquals( i, task.getProgramStageId() );
        assertEquals( "Name" + i, task.getProgramStageName() );
    }

    private Activity createActivity( int i )
    {
        Patient beneficiary = new Patient();
        beneficiary.setId( i );
        beneficiary.setFirstName( "First" + i );
        beneficiary.setLastName( "Last" + i );

        ProgramStage programStage = new ProgramStage();
        programStage.setId( i );
        programStage.setName( "Name" + i );

        ProgramStageInstance task = new ProgramStageInstance();
        task.setId( i );
        task.setProgramStage( programStage );
        task.setCompleted( false );

        Activity activity = new Activity();
        activity.setBeneficiary( beneficiary );
        activity.setDueDate( date );
        activity.setProvider( orgUnit );
        activity.setTask( task );
        activity.setDueDate( date );

        return activity;
    }

}
