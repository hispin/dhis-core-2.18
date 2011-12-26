package org.hisp.dhis.patient.action.caseaggregation;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

public class GetAggDataElementsAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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

    private Integer dataElementGroupId;

    public void setDataElementGroupId( Integer dataElementGroupId )
    {
        this.dataElementGroupId = dataElementGroupId;
    }

    private List<DataElement> dataElementList;

    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        optionComboNames = new ArrayList<String>();

        optionComboIds = new ArrayList<String>();

        if ( dataElementGroupId == 0 )
        {
            dataElementList = new ArrayList<DataElement>( dataElementService.getDataElementsWithoutGroups() );
        }
        else
        {
            dataElementList = new ArrayList<DataElement>( dataElementService.getDataElementGroup( dataElementGroupId )
                .getMembers() );
        }
        Iterator<DataElement> deIterator = dataElementList.iterator();

        while ( deIterator.hasNext() )
        {
            DataElement dataElement = deIterator.next();

            if ( dataElement.getDomainType().equalsIgnoreCase( DataElement.DOMAIN_TYPE_PATIENT )
                || !dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
            {
                deIterator.remove();
            }

        }

        if ( dataElementList != null && !dataElementList.isEmpty() )
        {
            deIterator = dataElementList.iterator();

            while ( deIterator.hasNext() )
            {
                DataElement de = deIterator.next();

                DataElementCategoryCombo dataElementCategoryCombo = de.getCategoryCombo();

                List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                    dataElementCategoryCombo.getOptionCombos() );

                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();

                while ( optionComboIterator.hasNext() )
                {
                    DataElementCategoryOptionCombo decoc = optionComboIterator.next();

                    optionComboIds.add( de.getId() + "." + decoc.getId() );

                    optionComboNames.add( de.getName() + " " + decoc.getName() );
                }
            }
        }

        return SUCCESS;
    }
}
