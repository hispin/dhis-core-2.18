package org.hisp.dhis.dataadmin.action.lock;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.datalock.DataSetLock;
import org.hisp.dhis.datalock.DataSetLockService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.source.Source;

import com.opensymphony.xwork2.Action;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public class UnselectLevelAction 
    implements Action
{
	private static final int FIRST_LEVEL = 1;
	
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
    
    public void setDataSetLockService( DataSetLockService dataSetLockService)
    {
        this.dataSetLockService = dataSetLockService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer level;

    public void setLevel( Integer level )
    {
        this.level = level;
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
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer selectLevel;

    public Integer getSelectLevel()
    {
        return selectLevel;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
    throws Exception
    {       
        selectionTreeManager.clearSelectedOrganisationUnits();
        selectionTreeManager.clearLockOnSelectedOrganisationUnits();
        
        Period period = new Period();       
        period = periodService.getPeriod(periodId.intValue());
        
        DataSet dataSet = new DataSet();        
        dataSet = dataSetService.getDataSet(selectedLockedDataSetId.intValue());                  
        
        DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period );      
        selectionTreeManager.setSelectedOrganisationUnits( convert( dataSet.getSources() ) );
        
        if( dataSetLock.getSources() == null )
        {
            selectionTreeManager.setSelectedOrganisationUnits( selectionTreeManager.getSelectedOrganisationUnits() );
            selectionTreeManager.setLockOnSelectedOrganisationUnits( convert( dataSetLock.getSources() ) );
        }
        else
        {  
            Collection<OrganisationUnit> tt = organisationUnitService.getOrganisationUnitsAtLevel( level );
            Set<OrganisationUnit> temp = new HashSet<OrganisationUnit>(convert( dataSetLock.getSources() ));            
            selectionTreeManager.clearSelectedOrganisationUnits();
            selectionTreeManager.clearLockOnSelectedOrganisationUnits();
            
            selectionTreeManager.setSelectedOrganisationUnits( convert( dataSet.getSources() ) );
            temp.removeAll( tt );
            selectionTreeManager.setLockOnSelectedOrganisationUnits( temp ) ;
        }
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private Set<OrganisationUnit> convert( Collection<Source> sources )
    {
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
        
        for ( Source source : sources )
        {               
            organisationUnits.add( (OrganisationUnit) source );
        }       
        
        return organisationUnits;
    }  
}
