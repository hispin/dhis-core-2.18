package org.hisp.dhis.coldchain.catalog.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogService;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogComparator;

import com.opensymphony.xwork2.Action;

public class CatalogListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
   
    private CatalogService catalogService;
    
    public void setCatalogService( CatalogService catalogService )
    {
        this.catalogService = catalogService;
    }

    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private List<Catalog> catalogs = new ArrayList<Catalog>();
    
    public List<Catalog> getCatalogs()
    {
        return catalogs;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        catalogs = new ArrayList<Catalog>( catalogService.getAllCatalogs() );
        
        Collections.sort( catalogs, new CatalogComparator() );
        
        
        return SUCCESS;
    }
    
}
