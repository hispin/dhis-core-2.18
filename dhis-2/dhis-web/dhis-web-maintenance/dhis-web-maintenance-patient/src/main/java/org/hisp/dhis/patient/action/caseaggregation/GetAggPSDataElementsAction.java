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

package org.hisp.dhis.patient.action.caseaggregation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $Id: GetAggPSDataElementsAction.java Dec 22, 2011 9:24:49 AM $
 */
public class GetAggPSDataElementsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageDataElementService programStageDataElementService;

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<String> optionComboNames;

    public List<String> getOptionComboNames()
    {
        return optionComboNames;
    }

    private List<String> optionComboIds;

    public List<String> getOptionComboIds()
    {
        return optionComboIds;
    }

    private List<String> optionComboType;

    public List<String> getOptionComboType()
    {
        return optionComboType;
    }

    private Integer psId;

    public void setPsId( Integer psId )
    {
        this.psId = psId;
    }

    private List<DataElement> dataElementList;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        optionComboNames = new ArrayList<String>();

        optionComboIds = new ArrayList<String>();

        optionComboType = new ArrayList<String>();

        dataElementList = new ArrayList<DataElement>( programStageDataElementService
            .getListDataElement( programStageService.getProgramStage( psId ) ) );

        if ( dataElementList != null && !dataElementList.isEmpty() )
        {
            Iterator<DataElement> deIterator = dataElementList.iterator();

            while ( deIterator.hasNext() )
            {
                DataElement de = deIterator.next();

                if ( de.getType().equals( DataElement.VALUE_TYPE_INT ) )
                {
                    DataElementCategoryCombo dataElementCategoryCombo = de.getCategoryCombo();

                    List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                        dataElementCategoryCombo.getOptionCombos() );

                    Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();

                    while ( optionComboIterator.hasNext() )
                    {
                        DataElementCategoryOptionCombo decoc = optionComboIterator.next();

                        optionComboIds.add( "[" + CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT + ":"
                            + psId + "." + de.getId() + "." + decoc.getId() + "]" );

                        optionComboNames.add( de.getName() + " " + decoc.getName() );

                        if ( de.isMultiDimensional() )
                            optionComboType.add( "1:" + de.getType() );
                        else
                            optionComboType.add( "0:" + de.getType() );
                    }
                }
            }
        }

        return SUCCESS;
    }
}
