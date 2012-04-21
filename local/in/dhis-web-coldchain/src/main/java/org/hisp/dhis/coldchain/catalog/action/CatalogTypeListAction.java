package org.hisp.dhis.coldchain.catalog.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.paging.ActionPagingSupport;

public class CatalogTypeListAction
extends ActionPagingSupport<DataSet>
{
    

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CatalogTypeService catalogTypeService;
    
    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    
    private List<CatalogType> catalogTypes;
    
    public List<CatalogType> getCatalogTypes()
    {
        return catalogTypes;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------


    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        
        catalogTypes = new ArrayList<CatalogType>( catalogTypeService.getAllCatalogTypes());
        
        /*
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( dataSetService.getDataSetCountByName( key ) );

            
            
            dataSets = new ArrayList<DataSet>( dataSetService.getDataSetsBetweenByName( key, paging.getStartPos(),
                paging.getPageSize() ) );
        }
        else
        {
            this.paging = createPaging( dataSetService.getDataSetCount() );

            dataSets = new ArrayList<DataSet>( dataSetService.getDataSetsBetween( paging.getStartPos(), paging
                .getPageSize() ) );
        }
        */
        //Collections.sort( catalogTypes, new IdentifiableObjectNameComparator() );

        return SUCCESS;
    }
}

