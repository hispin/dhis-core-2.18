package org.hisp.dhis.patient.api.service;

import org.hisp.dhis.patient.api.model.Task;
import org.hisp.dhis.program.ProgramStageInstance;

public class TaskMapper
    extends AbstractEntitiyModelBeanMapper<ProgramStageInstance, Task>
{

    @Override
    public Task getModel( ProgramStageInstance stageInstance, MappingManager mappingManager )
    {
        if (stageInstance == null) {
            return null;
        }
        
        Task task = new Task();
        
        task.setCompleted( stageInstance.isCompleted() );
        task.setId( stageInstance.getId() );
        task.setProgramStageId( stageInstance.getProgramStage().getId() );
        task.setProgramStageName(stageInstance.getProgramStage().getName());

        return task;
    }

}
