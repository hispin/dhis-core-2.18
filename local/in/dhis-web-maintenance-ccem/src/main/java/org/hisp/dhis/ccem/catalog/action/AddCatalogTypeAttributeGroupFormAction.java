package org.hisp.dhis.ccem.catalog.action;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version AddCatalogTypeAttributeGroupFormAction.javaOct 10, 2012 12:54:52 PM	
 */

public class AddCatalogTypeAttributeGroupFormAction implements Action
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
    // Input/Output
    // -------------------------------------------------------------------------
    
    private String catalogTypeId;
    
    public void setCatalogTypeId( String catalogTypeId )
    {
        this.catalogTypeId = catalogTypeId;
    }
    
    public String getCatalogTypeId()
    {
        return catalogTypeId;
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
        catalogType = catalogTypeService.getCatalogType( Integer.parseInt( catalogTypeId ) );
        return SUCCESS;
    }
}

