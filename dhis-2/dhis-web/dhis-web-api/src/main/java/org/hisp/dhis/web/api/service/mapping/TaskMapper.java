package org.hisp.dhis.web.api.service.mapping;

import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.web.api.model.Task;

public class TaskMapper
    implements BeanMapper<ProgramStageInstance, Task>
{

    @Override
    public Task getModel( ProgramStageInstance stageInstance )
    {
        if (stageInstance == null) {
            return null;
        }
        
        Task task = new Task();
        
        task.setCompleted( stageInstance.isCompleted() );
        task.setId( stageInstance.getId() );
        task.setProgramStageId( stageInstance.getProgramStage().getId() );

        return task;
    }

}
