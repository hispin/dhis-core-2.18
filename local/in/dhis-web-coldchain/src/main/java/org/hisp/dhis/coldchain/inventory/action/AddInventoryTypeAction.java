package org.hisp.dhis.coldchain.inventory.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;

import com.opensymphony.xwork2.Action;

public class AddInventoryTypeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
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
    // Input/Output
    // -------------------------------------------------------------------------
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
        InventoryType inventoryType = new InventoryType();
        
        inventoryType.setName( name );
        inventoryType.setDescription( description );
        inventoryType.setTracking( tracking );
        
        inventoryType.setCatalogType( catalogTypeService.getCatalogType( catalogType ) );
        
        Set<InventoryTypeAttribute> inventoryTypeSet = new HashSet<InventoryTypeAttribute>();
        for( Integer inventoryTypeAttId : selectedList )
        {
            inventoryTypeSet.add( inventoryTypeAttributeService.getInventoryTypeAttribute( inventoryTypeAttId ) );
        }
        
        inventoryType.setInventoryTypeAttributes( inventoryTypeSet );
        
        inventoryTypeService.addInventoryType( inventoryType );
        
        return SUCCESS;
    }
}
