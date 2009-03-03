package org.hisp.dhis.integration.rims.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.integration.rims.api.RIMSService;
import org.hisp.dhis.integration.rims.api.RIMS_DataElementNameComparator;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;

import com.opensymphony.xwork.ActionSupport;

public class RIMSDataElementsListAction
    extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private RIMSService rimsService;

    public void setRimsService( RIMSService rimsService )
    {
        this.rimsService = rimsService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    // --------------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------------
    Map<String, String> rimsDEGroups;

    public Map<String, String> getRimsDEGroups()
    {
        return rimsDEGroups;
    }

    List<RIMS_Mapping_DataElement> rimsDataElements;

    public List<RIMS_Mapping_DataElement> getRimsDataElements()
    {
        return rimsDataElements;
    }

    Map<String, String> rimsDENameMap;

    public Map<String, String> getRimsDENameMap()
    {
        return rimsDENameMap;
    }

    // --------------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        rimsDENameMap = new HashMap<String, String>();
        rimsDEGroups = new HashMap<String, String>( rimsService.getAllRIMSDEGroups() );
        rimsDataElements = new ArrayList<RIMS_Mapping_DataElement>( rimsService.getAllMappingDataElements() );

        Collections.sort( rimsDataElements, new RIMS_DataElementNameComparator() );

        Iterator<RIMS_Mapping_DataElement> iterator1 = rimsDataElements.iterator();
        while ( iterator1.hasNext() )
        {
            RIMS_Mapping_DataElement rimsDE = (RIMS_Mapping_DataElement) iterator1.next();

            rimsDENameMap.put( rimsDE.getDeName(), expressionService.getExpressionDescription( rimsDE.getDhisExpression() ) );
        }

        return SUCCESS;
    }

}
