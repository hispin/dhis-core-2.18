package org.hisp.dhis.coldchain.catalog.dataentryform.action;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;

import com.opensymphony.xwork2.Action;

public class ViewCatalogTypeDataEntryFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private CatalogTypeService catalogTypeService;
    
    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private Integer catalogTypeId;

    public void setCatalogTypeId( Integer catalogTypeId )
    {
        this.catalogTypeId = catalogTypeId;
    }
    
    private DataEntryForm dataEntryForm;

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        CatalogType catalogType = catalogTypeService.getCatalogType( catalogTypeId );
        
        // ---------------------------------------------------------------------
        // Get dataEntryForm of selected catalogtype
        // ---------------------------------------------------------------------

        return SUCCESS;
    }
}
