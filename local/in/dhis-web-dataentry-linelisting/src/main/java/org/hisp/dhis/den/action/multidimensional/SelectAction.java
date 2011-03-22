package org.hisp.dhis.den.action.multidimensional;

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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.den.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Abyot Asalefew, abyota@yahoo.com
 * @version $Id$
 */

@SuppressWarnings( "serial" )
public class SelectAction
    extends ActionSupport
{

    private static final String DEFAULT_FORM = "defaultform";

    private static final String MULTI_DIMENSIONAL_FORM = "multidimensionalform";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public Collection<DataSet> getDataSets()
    {
        return dataSets;
    }

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    public SelectedStateManager getSelectedStateManager()
    {
        return selectedStateManager;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer selectedDataSetId;

    public void setSelectedDataSetId( Integer selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }

    public Integer getSelectedDataSetId()
    {
        return selectedDataSetId;
    }

    private Integer selectedPeriodIndex;

    public void setSelectedPeriodIndex( Integer selectedPeriodIndex )
    {
        this.selectedPeriodIndex = selectedPeriodIndex;
    }

    public Integer getSelectedPeriodIndex()
    {
        return selectedPeriodIndex;
    }

    private String useShortName;

    public void setUseShortName( String useShortName )
    {
        this.useShortName = useShortName;
    }

    public String getUseShortName()
    {
        return useShortName;
    }

    private Collection<Integer> calculatedDataElementIds;

    public Collection<Integer> getCalculatedDataElementIds()
    {
        return calculatedDataElementIds;
    }

    private Map<CalculatedDataElement, Map<DataElement, Double>> calculatedDataElementMap;

    public Map<CalculatedDataElement, Map<DataElement, Double>> getCalculatedDataElementMap()
    {
        return calculatedDataElementMap;
    }

    private String multiDimensionalEntry;

    public void setMultiDimensionalEntry( String multiDimensionalEntry )
    {
        this.multiDimensionalEntry = multiDimensionalEntry;
    }

    public String getMultiDimensionalEntry()
    {
        return multiDimensionalEntry;
    }

    private Integer isMultiDimensional;

    public Integer getIsMultiDimensional()
    {
        return isMultiDimensional;
    }

    public void setIsMultiDimensional( Integer isMultiDimensional )
    {
        this.isMultiDimensional = isMultiDimensional;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit
        // ---------------------------------------------------------------------

        isMultiDimensional = 0;

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            selectedDataSetId = null;
            selectedPeriodIndex = null;

            selectedStateManager.clearSelectedDataSet();
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Load DataSets
        // ---------------------------------------------------------------------

        dataSets = new ArrayList<DataSet>();

        for ( DataSet dataSet : dataSetService.getAllDataSets() )
        {
            if ( dataSet.getSources().contains( organisationUnit )
                && dataSet.getPeriodType() instanceof CalendarPeriodType )
            {
                dataSets.add( dataSet );
            }
        }

        Collections.sort( dataSets, new DataSetNameComparator() );

        // ---------------------------------------------------------------------
        // Validate selected DataSet
        // ---------------------------------------------------------------------

        DataSet selectedDataSet;

        if ( selectedDataSetId != null )
        {
            selectedDataSet = dataSetService.getDataSet( selectedDataSetId );
        }
        else
        {
            selectedDataSet = selectedStateManager.getSelectedDataSet();
        }

        if ( selectedDataSet != null && dataSets.contains( selectedDataSet ) )
        {
            selectedDataSetId = selectedDataSet.getId();
            selectedStateManager.setSelectedDataSet( selectedDataSet );
        }
        else
        {
            selectedDataSetId = null;
            selectedPeriodIndex = null;

            selectedStateManager.clearSelectedDataSet();
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Prepare for multidimensional dataentry
        // ---------------------------------------------------------------------
        Collection<DataElementCategoryCombo> decbo = new ArrayList<DataElementCategoryCombo>();
        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        for ( DataElement de : dataSet.getDataElements() )
        {
            if ( de.getCategoryCombo() != null )
            {
                decbo.add( de.getCategoryCombo() );
            }
        }
        if ( decbo.size() > 0 )
        {
            isMultiDimensional = 1;
        }

        // ---------------------------------------------------------------------
        // Generate Periods
        // ---------------------------------------------------------------------

        periods = selectedStateManager.getPeriodList();

        // ---------------------------------------------------------------------
        // Validate selected Period
        // ---------------------------------------------------------------------

        if ( selectedPeriodIndex == null )
        {
            selectedPeriodIndex = selectedStateManager.getSelectedPeriodIndex();
        }

        if ( selectedPeriodIndex != null && selectedPeriodIndex >= 0 && selectedPeriodIndex < periods.size() )
        {
            selectedStateManager.setSelectedPeriodIndex( selectedPeriodIndex );
        }
        else
        {
            selectedPeriodIndex = null;
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Prepare CalculatedDataElementInformation
        // ---------------------------------------------------------------------

        calculatedDataElementIds = new HashSet<Integer>();
        calculatedDataElementMap = new HashMap<CalculatedDataElement, Map<DataElement, Double>>();
        CalculatedDataElement cde;

        for ( DataElement dataElement : selectedDataSet.getDataElements() )
        {
            if ( dataElement instanceof CalculatedDataElement )
            {
                cde = (CalculatedDataElement) dataElement;
                calculatedDataElementIds.add( cde.getId() );

                if ( cde.isSaved() )
                {
                    continue;
                }

                calculatedDataElementMap.put( cde, dataElementService.getDataElementFactors( cde ) );
            }
        }

        if ( multiDimensionalEntry != null )
        {
            return MULTI_DIMENSIONAL_FORM;
        }
        else

            return DEFAULT_FORM;
    }
}
