package org.hisp.dhis.coldchain.inventory.action;

import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;

import com.opensymphony.xwork2.Action;

public class UpdateInventoryTypeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private InventoryTypeService inventoryTypeService;

    public void setInventoryTypeService( InventoryTypeService inventoryTypeService )
    {
        this.inventoryTypeService = inventoryTypeService;
    }
    
    private CatalogTypeService catalogTypeService;

    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }

    private InventoryTypeAttributeService inventoryTypeAttributeService;
    
    public void setInventoryTypeAttributeService( InventoryTypeAttributeService inventoryTypeAttributeService )
    {
        this.inventoryTypeAttributeService = inventoryTypeAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private Integer catalogType;

    public void setCatalogType( Integer catalogType )
    {
        this.catalogType = catalogType;
    }

    private boolean tracking;

    public void setTracking( boolean tracking )
    {
        this.tracking = tracking;
    }
    
    private List<Integer> selectedList;
    
    public void setSelectedList( List<Integer> selectedList )
    {
        this.selectedList = selectedList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        InventoryType inventoryType = inventoryTypeService.getInventoryType( id );
        
        inventoryType.setName( name );
        inventoryType.setDescription( description );
        inventoryType.setTracking( tracking );
        
        inventoryType.setCatalogType( catalogTypeService.getCatalogType( catalogType ) );
        
        inventoryType.getInventoryTypeAttributes().clear();
        
        for( Integer inventoryTypeAttId : selectedList )
        {
            
            inventoryType.getInventoryTypeAttributes().add( inventoryTypeAttributeService.getInventoryTypeAttribute( inventoryTypeAttId ) );
        }

        inventoryTypeService.updateInventoryType( inventoryType );
        
        return SUCCESS;
    }
}
