package org.hisp.dhis.reports.dataset.action;

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
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.organisationunit.OrgUnitTypePopulator;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reports.dataset.state.SelectedStateManager;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SelectReportAction
    extends AbstractAction
{
    // -----------------------------------------------------------------------
    // Parameters
    // -----------------------------------------------------------------------    
	
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
    
    private Period period;

    public Period getPeriod()
    {
        return period;
    }
    
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
    
    private String selectedUnitOnly;
    
    public void setSelectedUnitOnly( String selectedUnitOnly )
    {
    	this.selectedUnitOnly = selectedUnitOnly;
    }
    
    public String getSelectedUnitOnly()
    {
    	return selectedUnitOnly;
    }
    
    private String refreshDataMart;
    
    public void setRefreshDataMart( String refreshDataMart )
    {
    	this.refreshDataMart = refreshDataMart;
    } 
    
    public String getRefreshDataMart()
    {
    	return refreshDataMart;
    }  
    
    private List<OrganisationUnitGroup> orgUnitGroupMembers;
    
    public List<OrganisationUnitGroup> getOrgUnitGroupMembers()
    {
        return orgUnitGroupMembers;
    }
    
    
    // -----------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------   
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }   
    
    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    // -----------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------

    public String execute()
        throws Exception
    {  
    	dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
    	
    	// ---------------------------------------------------------------------
        // Remove DataSets which don't have a CalendarPeriodType
        // ---------------------------------------------------------------------

        Iterator<DataSet> iterator = dataSets.iterator();

        while ( iterator.hasNext() )
        {
            DataSet dataSet = iterator.next();

            if ( !(dataSet.getPeriodType() instanceof CalendarPeriodType) )
            {
                iterator.remove();
            }
        }
        OrganisationUnitGroupSet organisationUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSetByName( OrgUnitTypePopulator.ORGUNIT_TYPE_NAME );
        
        orgUnitGroupMembers = new ArrayList<OrganisationUnitGroup>(organisationUnitGroupSet.getOrganisationUnitGroups());

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
            
            
            
            //System.out.println("The size of OrgUnitGroupMembers is: "+ orgUnitGroupMembers.size());

            return SUCCESS;
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

            if ( selectedDataSet != null )
            {
                period = selectedStateManager.getSelectedPeriod();                
            }
        }
        else
        {
            selectedPeriodIndex = null;
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        period = selectedStateManager.getSelectedPeriod();           
         
        return SUCCESS;
    }

 }
