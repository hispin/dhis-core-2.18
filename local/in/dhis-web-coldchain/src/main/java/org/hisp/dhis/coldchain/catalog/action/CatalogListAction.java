package org.hisp.dhis.coldchain.catalog.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogService;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogComparator;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogTypeComparator;

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
    
    private CatalogTypeService catalogTypeService;
    
    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private List<Catalog> catalogs = new ArrayList<Catalog>();
    
    public List<Catalog> getCatalogs()
    {
        return catalogs;
    }

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private List<CatalogType> catalogTypes;
    
    public List<CatalogType> getCatalogTypes()
    {
        return catalogTypes;
    }
    
    private CatalogType catalogType;
    
    public CatalogType getCatalogType()
    {
        return catalogType;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        catalogTypes = new ArrayList<CatalogType>( catalogTypeService.getAllCatalogTypes());
        Collections.sort( catalogTypes, new CatalogTypeComparator() );
        
        if ( id != null )
        {
            catalogType = catalogTypeService.getCatalogType( id );
            
            catalogs = new ArrayList<Catalog>( catalogService.getCatalogs( catalogType ) );
            Collections.sort( catalogs, new CatalogComparator() );
        }
        else
        {
            catalogs = new ArrayList<Catalog>( catalogService.getAllCatalogs() );
            
            Collections.sort( catalogs, new CatalogComparator() );
        }
     
        return SUCCESS;
    }
    
}
