package org.hisp.dhis.vn.dataelement.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementGroupNameComparator;

import com.opensymphony.xwork2.Action;

import edu.emory.mathcs.backport.java.util.Collections;

public class GetAllDataElementGroupsAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private DataElementService dataElementService;  

    private List<DataElementGroup> dataElementGroups;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------


    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    

    public String execute()
        throws Exception
    {
        dataElementGroups = new ArrayList<DataElementGroup>(dataElementService.getAllDataElementGroups());
        
        Collections.sort( dataElementGroups,  new DataElementGroupNameComparator() );
        
        return SUCCESS;
    }
}
