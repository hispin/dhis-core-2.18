package org.hisp.dhis.de.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.de.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

public class LoadDataSetsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public Collection<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private boolean dataSetValid;

    public boolean isDataSetValid()
    {
        return dataSetValid;
    }
    
    private boolean periodValid;

    public boolean isPeriodValid()
    {
        return periodValid;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        if ( organisationUnit != null )
        {        
            // -----------------------------------------------------------------
            // Load data sets for selected org unit
            // -----------------------------------------------------------------

            dataSets = selectedStateManager.loadDataSetsForSelectedOrgUnit();
    
            Collections.sort( dataSets, new DataSetNameComparator() );

            // -----------------------------------------------------------------
            // Check if selected data set is associated with selected org unit
            // -----------------------------------------------------------------

            DataSet selectedDataSet = selectedStateManager.getSelectedDataSet();
            
            if ( selectedDataSet != null && dataSets.contains( selectedDataSet ) )
            {
                dataSetValid = true;
                
                periodValid = selectedStateManager.getSelectedPeriod() != null;
            }
            else
            {
                selectedStateManager.clearSelectedDataSet();
                selectedStateManager.clearSelectedPeriod();
            }
        }
        
        return SUCCESS;
    }
}
