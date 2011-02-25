package org.hisp.dhis.dataset.action;

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import com.opensymphony.xwork2.Action;

public class MobileDataSetListAction
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
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    private Collection<DataSet> dataSets;
    
    public void setDataSets( Collection<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }
    
    public Collection<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    private Collection<DataSet> mobileDatasets;

    public Collection<DataSet> getMobileDatasets()
    {
        return mobileDatasets;
    }

    public void setMobileDatasets( Collection<DataSet> mobileDatasets )
    {
        this.mobileDatasets = mobileDatasets;
    }

    @Override
    public String execute()
        throws Exception
    {
        OrganisationUnit selectedUnits =  selectionManager.getSelectedOrganisationUnit();
        dataSets = dataSetService.getDataSetsBySource( selectedUnits );
        mobileDatasets = dataSetService.getDataSetsForMobile( selectedUnits );
        dataSets.removeAll( mobileDatasets );
        System.out.println("Number of datasets:"+ dataSets.size());
        return SUCCESS;
    }

}
