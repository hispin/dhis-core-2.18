package org.hisp.dhis.databrowser.jdbc;

import java.util.List;

import org.hisp.dhis.databrowser.DataBrowserStore;
import org.hisp.dhis.databrowser.DataBrowserTable;

/**
 * @author joakibj, martinwa
 * @version $Id$
 */
public class StatementManagerDataBrowserStore
    implements DataBrowserStore
{

    public DataBrowserTable getDataSetsInPeriod( List<Integer> betweenPeriodIds )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer setCountDataElementsInOnePeriod( DataBrowserTable table, Integer dataSetId, Integer periodId )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer setCountOrgUnitsInOnePeriod( DataBrowserTable table, Integer orgUnitParent, Integer periodId )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDataElementStructureForDataSetBetweenPeriods( DataBrowserTable table, Integer dataSetId,
        List<Integer> betweenPeriods )
    {
        // TODO Auto-generated method stub
        
    }

    public void setStructureForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitParent,
        List<Integer> betweenPeriods )
    {
        // TODO Auto-generated method stub
        
    }
    
}