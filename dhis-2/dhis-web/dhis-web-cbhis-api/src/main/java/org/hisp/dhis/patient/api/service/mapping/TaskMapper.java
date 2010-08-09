package org.hisp.dhis.patient.api.service.mapping;

import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.patient.api.model.Task;
import org.hisp.dhis.patient.api.service.MappingFactory;
import org.hisp.dhis.program.ProgramStageInstance;

public class TaskMapper
    implements BeanMapper<ProgramStageInstance, Task>
{

    @Override
    public Task getModel( ProgramStageInstance stageInstance, MappingFactory mappingFactory, UriInfo uriInfo )
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
