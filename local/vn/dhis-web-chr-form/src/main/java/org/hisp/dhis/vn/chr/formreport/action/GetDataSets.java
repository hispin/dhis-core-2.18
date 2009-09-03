package org.hisp.dhis.vn.chr.formreport.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collections;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class GetDataSets
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private ArrayList<DataSet> dataSets;

    public ArrayList<DataSet> getDataSets()
    {
        return dataSets;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

        Collections.sort( dataSets, new DataSetNameComparator() );

        message = i18n.getString( "success" );

        return SUCCESS;
    }

}
