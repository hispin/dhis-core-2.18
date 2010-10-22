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

import com.opensymphony.xwork2.Action;

import java.util.*;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;

public class AddSectionAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private SectionService sectionService;

    public void setSectionService( SectionService sectionService )
    {
        this.sectionService = sectionService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer categoryComboId;

    private Integer dataSetId;

    private String sectionName;

    private List<String> selectedList = new ArrayList<String>();

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    private DataElementCategoryCombo categoryCombo;

    private DataSet dataSet;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public Integer getDataSetId()
    {
        return dataSetId;
    }

    public void setSectionName( String sectionName )
    {
        this.sectionName = sectionName;
    }

    public List<String> getSelectedList()
    {
        return selectedList;
    }

    public void setSelectedList( List<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    public Integer getCategoryComboId()
    {
        return categoryComboId;
    }

    public void setCategoryComboId( Integer categoryComboId )
    {
        this.categoryComboId = categoryComboId;
    }

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public void setCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
    }

    public DataElementCategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {      

        dataSet = dataSetService.getDataSet( dataSetId.intValue() );

        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );

        for ( Section section : dataSet.getSections() )
        {
            dataElements.removeAll( section.getDataElements() );
        }

        categoryCombo = dataElementCategoryService.getDataElementCategoryCombo( categoryComboId.intValue() );

        Iterator<DataElement> dataElementIterator = dataElements.iterator();

        while ( dataElementIterator.hasNext() )
        {
            DataElement de = dataElementIterator.next();

            if ( !de.getCategoryCombo().getName().equalsIgnoreCase( categoryCombo.getName() ) )
            {
                dataElementIterator.remove();
            }
        }

        Collections.sort( dataElements, new DataElementNameComparator() );
        
        if ( this.sectionName == null )
        {
            return INPUT;
        }

        Section section = new Section();

        section.setDataSet( dataSet );
        section.setName( sectionName );

        section.setSortOrder( 0 );

        List<DataElement> selectedDataElements = new ArrayList<DataElement>();

        for ( String id : selectedList )
        {
            DataElement d = dataElementService.getDataElement( Integer.parseInt( id ) );
            selectedDataElements.add( d );
        }

        section.setDataElements( selectedDataElements );
        sectionService.addSection( section );

        return SUCCESS;
    }

}
