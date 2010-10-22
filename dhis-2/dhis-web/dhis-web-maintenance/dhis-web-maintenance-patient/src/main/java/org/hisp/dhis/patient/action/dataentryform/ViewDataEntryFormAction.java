package org.hisp.dhis.patient.action.dataentryform;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.comparator.DataElementNameComparator;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.editor.EditorManager;
import org.hisp.dhis.patient.screen.DataEntryManager;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.program.comparator.ProgramStageNameComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Bharath Kumar
 * @modify Viet Nguyen 3-11-2009
 * @modify Tran Thanh Tri 13 Oct 2010
 * @version $Id$
 */
public class ViewDataEntryFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private DataEntryManager dataEntryManager;

    public void setDataEntryManager( DataEntryManager dataEntryManager )
    {
        this.dataEntryManager = dataEntryManager;
    }

    private EditorManager editorManager;

    public EditorManager getEditorManager()
    {
        return editorManager;
    }

    public void setEditorManager( EditorManager editorManager )
    {
        this.editorManager = editorManager;
    }

    private ProgramStageDataElementService programStageDataElementService;

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private int associationId;

    public void setAssociationId( int associationId )
    {
        this.associationId = associationId;
    }

    private Integer programStageId;

    public Integer getProgramStageId()
    {
        return programStageId;
    }

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    private DataEntryForm dataEntryForm;

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    private ProgramStage association;

    public ProgramStage getAssociation()
    {
        return association;
    }

    private List<DataEntryForm> existingDataEntryForms;

    public List<DataEntryForm> getExistingDataEntryForms()
    {
        return existingDataEntryForms;
    }

    public List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private List<ProgramStage> programStages;

    public List<ProgramStage> getProgramStages()
    {
        return programStages;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        association = programStageService.getProgramStage( associationId );

        dataEntryForm = association.getDataEntryForm();

        List<Integer> listAssociationIds = new ArrayList<Integer>();

        for ( ProgramStage ps : association.getProgram().getProgramStages() )
        {
            listAssociationIds.add( ps.getId() );
        }

        existingDataEntryForms = new ArrayList<DataEntryForm>( dataEntryFormService
            .listDisctinctDataEntryFormByProgramStageIds( listAssociationIds ) );

        editorManager.setValue( dataEntryForm == null ? "" : dataEntryManager.prepareDataEntryFormCode( dataEntryForm
            .getHtmlCode() ) );

        existingDataEntryForms.remove( dataEntryForm );

        dataElements = new ArrayList<DataElement>( programStageDataElementService.getListDataElement( association ) );

        Collections.sort( dataElements, new DataElementNameComparator() );
        
        programStages = new ArrayList<ProgramStage>( association.getProgram().getProgramStages() );
        
        programStages.remove( association );
        
        Collections.sort( programStages, new ProgramStageNameComparator() );

        return SUCCESS;
    }
}
