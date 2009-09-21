/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.dataadmin.action.lock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.datalock.DataSetLock;
import org.hisp.dhis.datalock.DataSetLockService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public class DefineLockOnDataSetOrgunitAndPeriod
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetLockService dataSetLockService;

    public void setDataSetLockService( DataSetLockService dataSetLockService )
    {
        this.dataSetLockService = dataSetLockService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer lockedDataSets;
    
    public void setLockedDataSets( Integer lockedDataSets )
    {
        this.lockedDataSets = lockedDataSets;
    }

    public Integer getLockedDataSets()
    {
        return lockedDataSets;
    }

    private Collection<String> unlockedDataSets = new ArrayList<String>();

    public void setUnlockedDataSets( Collection<String> unlockedDataSets )
    {
        this.unlockedDataSets = unlockedDataSets;
    }

    public Collection<String> getUnlockedDataSets()
    {
        return unlockedDataSets;
    }

    private Integer selectedLockedDataSetId;

    public void setSelectedLockedDataSetId( Integer selectedLockedDataSetId )
    {
        this.selectedLockedDataSetId = selectedLockedDataSetId;
    }

    public Integer getSelectedLockedDataSetId()
    {
        return selectedLockedDataSetId;
    }

    private Integer periodId;

    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }

    public Integer getPeriodId()
    {
        return periodId;
    }

    private Date timestamp;

    public Date getTimestamp()
    {
        return timestamp;
    }

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Period period = new Period();
		
		if( periodId != null )
		{
			period = periodService.getPeriod( periodId.intValue() );
		}
		else
		{
			return SUCCESS;
		}
        
		storedBy = currentUserService.getCurrentUsername();

        // -------------------------------------------------------------------------------
        // For data set movement from locked to unlocked data set list box and
        // vice versa according to lock status
        // -------------------------------------------------------------------------------

        for ( String id : unlockedDataSets )
        {      	
        	DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( id ) );
            DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period );
            
            if ( dataSetLock != null )
            {                
                dataSet.setLocked( false );
                dataSetService.updateDataSet( dataSet );                               
                dataSetLock.getSources().removeAll( dataSetLock.getSources() );
                dataSetLockService.deleteDataSetLock( dataSetLock );
            }
        }

        // ----------------------------------------------------------------------------------------
        // Data sets lock for specific selected period, and selected
        // organization unit ( or units )
        // ----------------------------------------------------------------------------------------

        if ( lockedDataSets != null )
        {
            DataSet dataSet = dataSetService.getDataSet(  lockedDataSets.intValue() );
            Set<Source> organisationUnitsSelectedForLocking = new HashSet<Source>( selectionTreeManager
                .getLockOnSelectedOrganisationUnits() );
            DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period );

            if ( organisationUnitsSelectedForLocking.size() < 1 )
            {
                dataSet.setLocked( false );
                dataSetService.updateDataSet( dataSet );
                dataSetLock.getSources().removeAll( dataSetLock.getSources() );
                dataSetLockService.deleteDataSetLock( dataSetLock );
                return SUCCESS;
            }

            dataSetLock.getSources().removeAll( dataSetLock.getSources() );
            dataSetLock.getSources().addAll( organisationUnitsSelectedForLocking );
            dataSetLock.setTimestamp( new Date() );
            dataSetLock.setStoredBy( storedBy );
            dataSetLockService.updateDataSetLock( dataSetLock );
        }

        return SUCCESS;
    }
}
