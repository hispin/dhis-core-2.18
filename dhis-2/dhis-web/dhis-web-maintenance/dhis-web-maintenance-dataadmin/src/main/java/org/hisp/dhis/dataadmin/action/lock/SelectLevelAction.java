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
public class SelectLevelAction implements Action
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
    
    public Integer getLevel()
    {
        return level;
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
        
        Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits();
        selectionTreeManager.setSelectedOrganisationUnits( convert( dataSet.getSources() ) );
        DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period );
        Set<OrganisationUnit> selectedUnits = new HashSet<OrganisationUnit>(selectionTreeManager.getSelectedOrganisationUnits().size());
    
        for ( OrganisationUnit rootUnit : rootUnits )
        {         
            selectLevel( rootUnit, FIRST_LEVEL, selectedUnits );        
        }
                   
        selectionTreeManager.setSelectedOrganisationUnits( convert( dataSet.getSources() ) );
        
        if( dataSetLock.getSources() == null )
        {
            //selectionTreeManager.clearLockOnSelectedOrganisationUnits();
            selectionTreeManager.setLockOnSelectedOrganisationUnits( selectedUnits );
        }
        else
        {  
            //selectionTreeManager.clearLockOnSelectedOrganisationUnits();
            selectedUnits.addAll(( convert( dataSetLock.getSources() )));
            selectionTreeManager.setLockOnSelectedOrganisationUnits( selectedUnits ) ;
        }
    
        selectLevel = level;
        
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
     
    private void selectLevel( OrganisationUnit orgUnit, int currentLevel, Collection<OrganisationUnit> selectedUnits )
    {
        if ( currentLevel == level )
        {
            if( selectionTreeManager.getSelectedOrganisationUnits().contains( orgUnit ))
            {
                selectedUnits.add( orgUnit );
            }
        }
        else
        {
            for ( OrganisationUnit child : orgUnit.getChildren() )
            {
                selectLevel( child, currentLevel + 1, selectedUnits );
            }
        }
    }
}
