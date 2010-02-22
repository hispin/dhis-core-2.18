package org.hisp.dhis.patient.action.caseaggregation;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;

import com.opensymphony.xwork2.Action;

public class GetProgramStagesAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<ProgramStage> programStageList;
    
    public List<ProgramStage> getProgramStageList()
    {
        return programStageList;
    }
    
    private Integer programId;
    
    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        programStageList = new ArrayList<ProgramStage>( programService.getProgram( programId ).getProgramStages() );
        
        return SUCCESS;
    }
}
