/**
 * 
 */
package org.hisp.dhis.dataadmin.action.lock;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.datalock.DataSetLockService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.source.Source;

import com.opensymphony.xwork2.Action;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public class UnselectAllAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DataSetLockService dataSetLockService;
    
    public void setDataSetLockService( DataSetLockService dataSetLockService)
    {
        this.dataSetLockService = dataSetLockService;
    }
    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
   
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
    
    private DataSet dataSet;

    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
   
    public String execute()
        throws Exception
        {
            Period period = new Period();    
            period = periodService.getPeriod(periodId.intValue());      
            DataSet dataSet = new DataSet();      
            dataSet = dataSetService.getDataSet(selectedLockedDataSetId.intValue());           
       
		    if (dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period )!=null)
		    {
				 selectionTreeManager.clearSelectedOrganisationUnits();
				 selectionTreeManager.clearLockOnSelectedOrganisationUnits();
				 selectionTreeManager.setSelectedOrganisationUnits( convert( dataSet.getSources() ) );
				 //selectionTreeManager.setLockOnSelectedOrganisationUnits( convert( dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period ).getSources() ) );
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
