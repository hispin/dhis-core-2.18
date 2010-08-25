package org.hisp.dhis.patient.api.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.model.OrgUnits;
import org.hisp.dhis.patient.api.service.mapping.ActivityPlanMapper;
import org.hisp.dhis.patient.api.service.mapping.BeanMapper;
import org.hisp.dhis.patient.api.service.mapping.OrgUnitsMapper;
import org.junit.Before;
import org.junit.Test;

public class ModelMapperTest
{

    private MappingFactory mappingManager = new MappingFactory();

//    @Before
//    public void init() {
//        Set<EntityModelBeanMapper<?,?>> mappers = new HashSet<EntityModelBeanMapper<?,?>>();
//
//        mappers.add( new ActivityPlanMapper() );
//        
//        
//        mappingManager.setMappers( mappers );
//        mappingManager.init();
//        
//    }
    
    @Test
    public void testInitialization() {
        MappingFactory factory = new MappingFactory();
        
        BeanMapper activityPlanMapper = new ActivityPlanMapper();

        Set<BeanMapper<?,?>> mappers = new HashSet<BeanMapper<?,?>>();
        mappers.add( activityPlanMapper );
        OrgUnitsMapper orgUnitsMapper = new OrgUnitsMapper();
        mappers.add( orgUnitsMapper );
        
        factory.setMappers( mappers );
        
        assertEquals( activityPlanMapper, factory.mappingIndex.get( ActivityPlan.class ) );
        assertEquals( activityPlanMapper, factory.getBeanMapper( new ArrayList<Activity>(), ActivityPlan.class ));

        assertEquals( orgUnitsMapper, factory.getBeanMapper( new ArrayList<OrganisationUnit>(), OrgUnits.class ));

    }
    
}
