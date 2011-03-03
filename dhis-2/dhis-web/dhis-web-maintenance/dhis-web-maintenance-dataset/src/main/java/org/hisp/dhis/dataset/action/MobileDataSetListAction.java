package org.hisp.dhis.dataset.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
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
    
    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    private List<DataSet> dataSets;
    
    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }
    
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    private List<DataSet> mobileDatasets;

    public List<DataSet> getMobileDatasets()
    {
        return mobileDatasets;
    }

    public void setMobileDatasets( List<DataSet> mobileDatasets )
    {
        this.mobileDatasets = mobileDatasets;
    }

    @Override
    public String execute()
        throws Exception
    {
        dataSets = new ArrayList<DataSet>(dataSetService.getAllDataSets());
        mobileDatasets = new ArrayList<DataSet>(dataSetService.getDataSetsForMobile());
        dataSets.removeAll( mobileDatasets );
        Collections.sort( dataSets, new DataSetNameComparator() );
        Collections.sort( mobileDatasets, new DataSetNameComparator() );
        return SUCCESS;
    }

}
