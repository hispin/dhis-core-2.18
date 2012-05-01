package org.hisp.dhis.coldchain.inventory.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;

import com.opensymphony.xwork2.Action;

public class ShowUpdateInventoryTypeAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private InventoryTypeService inventoryTypeService;

    public void setInventoryTypeService( InventoryTypeService inventoryTypeService )
    {
        this.inventoryTypeService = inventoryTypeService;
    }
    private InventoryTypeAttributeService inventoryTypeAttributeService;
    
    public void setInventoryTypeAttributeService( InventoryTypeAttributeService inventoryTypeAttributeService )
    {
        this.inventoryTypeAttributeService = inventoryTypeAttributeService;
    }
    
    private CatalogTypeService catalogTypeService;

    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    private String id;

    public void setId( String id )
    {
        this.id = id;
    }
    
    private InventoryType inventoryType;
    
    public InventoryType getInventoryType()
    {
        return inventoryType;
    }

    private List<InventoryTypeAttribute> availInventoryTypeAttributes;

    public List<InventoryTypeAttribute> getAvailInventoryTypeAttributes()
    {
        return availInventoryTypeAttributes;
    }

    private List<InventoryTypeAttribute> selInventoryTypeAttributes;

    public List<InventoryTypeAttribute> getSelInventoryTypeAttributes()
    {
        return selInventoryTypeAttributes;
    }
    
    private List<CatalogType> catalogTypes;

    public List<CatalogType> getCatalogTypes()
    {
        return catalogTypes;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        inventoryType = inventoryTypeService.getInventoryType( Integer.parseInt( id ) );
        
        catalogTypes =  new ArrayList<CatalogType>( catalogTypeService.getAllCatalogTypes() );
        
        availInventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( inventoryTypeAttributeService.getAllInventoryTypeAttributes() );
        
        selInventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( inventoryType.getInventoryTypeAttributes() );
        
        availInventoryTypeAttributes.removeAll( selInventoryTypeAttributes );
        
        return SUCCESS;        
    }
}
