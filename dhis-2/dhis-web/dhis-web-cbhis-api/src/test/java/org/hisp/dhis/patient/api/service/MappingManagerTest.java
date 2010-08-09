package org.hisp.dhis.patient.api.service;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class MappingManagerTest
{

    private MappingManager mappingManager = new MappingManager();

    @Before
    public void init() {
        Set<EntityModelBeanMapper<?,?>> mappers = new HashSet<EntityModelBeanMapper<?,?>>();

        mappers.add( new ActivityPlanMapper() );
        
        
        mappingManager.setMappers( mappers );
        mappingManager.init();
        
    }
    
    @Test
    public void testInitialization() {
        MappingManager manager = new MappingManager();
        
        EntityModelBeanMapper activityPlanMapper = new ActivityPlanMapper();

        Set<EntityModelBeanMapper<?,?>> mappers = new HashSet<EntityModelBeanMapper<?,?>>();
        mappers.add( activityPlanMapper );
        
        manager.setMappers( mappers );
        
        manager.init();
        
        assertEquals( activityPlanMapper, manager.getMapper( new ActivitiesWrapper(null) ) );
    }
    
}
