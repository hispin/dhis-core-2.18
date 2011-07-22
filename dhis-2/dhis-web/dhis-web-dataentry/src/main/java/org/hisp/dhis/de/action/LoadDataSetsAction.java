package org.hisp.dhis.de.action;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class LoadDataSetsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public Collection<DataSet> getDataSets()
    {
        return dataSets;
    }
        
    private boolean dataSetValid;

    public boolean isDataSetValid()
    {
        return dataSetValid;
    }
        
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit != null )
        {        
            // -----------------------------------------------------------------
            // Load data sets for selected org unit
            // -----------------------------------------------------------------

            dataSets = loadDataSetsForSelectedOrgUnit( organisationUnit );
    
            Collections.sort( dataSets, new DataSetNameComparator() );

            // -----------------------------------------------------------------
            // Check if selected data set is associated with selected org unit
            // -----------------------------------------------------------------

            if ( dataSetId != null )
            {
                DataSet selectedDataSet = dataSetService.getDataSet( dataSetId );
                
                if ( selectedDataSet != null && dataSets.contains( selectedDataSet ) )
                {
                    dataSetValid = true;
                }
            }
        }
        
        return SUCCESS;
    }
    
    private List<DataSet> loadDataSetsForSelectedOrgUnit( OrganisationUnit organisationUnit )
    {
        List<DataSet> dataSets = new ArrayList<DataSet>( organisationUnit.getDataSets() );

        // ---------------------------------------------------------------------
        // Retain only DataSets from current user's authority groups
        // ---------------------------------------------------------------------

        User currentUser = currentUserService.getCurrentUser();
        
        if ( currentUser != null && !currentUserService.currentUserIsSuper() )
        {
            dataSets.retainAll( currentUser.getUserCredentials().getAllDataSets() );
        }

        // ---------------------------------------------------------------------
        // Remove DataSets which don't have a CalendarPeriodType
        // ---------------------------------------------------------------------

        Iterator<DataSet> iterator = dataSets.iterator();

        while ( iterator.hasNext() )
        {
            DataSet dataSet = iterator.next();

            if ( !( dataSet.getPeriodType() instanceof CalendarPeriodType) )
            {
                iterator.remove();
            }
        }

        return dataSets;
    }
}
