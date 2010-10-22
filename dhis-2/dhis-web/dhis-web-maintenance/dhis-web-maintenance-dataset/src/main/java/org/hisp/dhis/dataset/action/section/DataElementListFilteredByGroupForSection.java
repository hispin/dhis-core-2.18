package org.hisp.dhis.dataset.action.section;

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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class DataElementListFilteredByGroupForSection
    implements Action
{
    private static final String ALL = "ALL";

    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    private Integer categoryComboId;

    private String dataElementGroupId;

    private String[] selectedList;

    private List<DataElement> dataElements;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public void setCategoryComboId( Integer categoryComboId )
    {
        this.categoryComboId = categoryComboId;
    }

    public void setDataElementGroupId( String dataElementGroupId )
    {
        this.dataElementGroupId = dataElementGroupId;
    }

    public void setSelectedList( String[] selectedList )
    {
        this.selectedList = selectedList;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // dataSetId
        if ( dataSetId != null )
        {
            DataSet dataSet = dataSetService.getDataSet( dataSetId );

            dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );

            for ( Section section : dataSet.getSections() )
            {
                dataElements.removeAll( section.getDataElements() );
            }
        }

        // categoryComboId
        if ( categoryComboId != null )
        {
            DataElementCategoryCombo categoryCombo = dataElementCategoryService
                .getDataElementCategoryCombo( categoryComboId.intValue() );

            Iterator<DataElement> dataElementIterator = dataElements.iterator();

            while ( dataElementIterator.hasNext() )
            {
                DataElement de = dataElementIterator.next();

                if ( !de.getCategoryCombo().getName().equalsIgnoreCase( categoryCombo.getName() ) )
                {
                    dataElementIterator.remove();
                }
            }
        }

        // dataElementGroupId
        if ( dataElementGroupId == null || dataElementGroupId.equals( ALL ) )
        {
            dataElements.retainAll( dataElementService.getAllActiveDataElements() );
        }
        else
        {
            DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( Integer
                .parseInt( dataElementGroupId ) );

            dataElements.retainAll( dataElementGroup.getMembers() );
        }

        // selectedList
        if ( selectedList != null && selectedList.length > 0 )
        {
            Iterator<DataElement> iter = dataElements.iterator();

            while ( iter.hasNext() )
            {
                DataElement dataElement = iter.next();

                for ( int i = 0; i < selectedList.length; i++ )
                {
                    if ( dataElement.getId() == Integer.parseInt( selectedList[i] ) )
                    {
                        iter.remove();
                    }
                }
            }
        }

        Collections.sort( dataElements, dataElementComparator );

        displayPropertyHandler.handle( dataElements );

        return SUCCESS;
    }
}
