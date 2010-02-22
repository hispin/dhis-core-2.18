package org.hisp.dhis.patient.action.dataentryform;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.program.comparator.ProgramStageNameComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Viet Nguyen
 * @version $Id$
 */
public class GetSelectedProgramStagesAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageService programStageService;

    public void setProgramStageService(ProgramStageService programStageService) {
        this.programStageService = programStageService;
    }
    

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private int programStageId;
    

    public void setProgramStageId(int programStageId) {
		this.programStageId = programStageId;
	}
    

	public int getProgramStageId() {
		return programStageId;
	}


	private List<ProgramStage> listProgramStage;

    public List<ProgramStage> getListProgramStage()
    {
        return listProgramStage;
    }


    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {
    	
        ProgramStage programStage = programStageService.getProgramStage( programStageId );

        if( programStage != null ){
        	
        	Program program = programStage.getProgram();
        	
        	if( program != null ){
        		
        		listProgramStage = new ArrayList(program.getProgramStages());
        		for( ProgramStage ps : listProgramStage )
            	{
            		if( ps.equals( programStage ) )
            		{
            			listProgramStage.remove(ps);
            			break;
            		}
            	}
        		
        		Collections.sort( listProgramStage , new ProgramStageNameComparator());

        	}
        	
        }
           

        return SUCCESS;
    }
}
