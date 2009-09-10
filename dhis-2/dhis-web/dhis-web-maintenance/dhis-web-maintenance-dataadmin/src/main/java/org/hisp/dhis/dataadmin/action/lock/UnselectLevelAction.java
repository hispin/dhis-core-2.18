/**
 * 
 */
package org.hisp.dhis.dataadmin.action.lock;

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
public class UnselectLevelAction implements Action
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
