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
    
    private List<Integer> selectedInventoryTypeAttributeList;
    
    public void setSelectedInventoryTypeAttributeList( List<Integer> selectedInventoryTypeAttributeList )
    {
        this.selectedInventoryTypeAttributeList = selectedInventoryTypeAttributeList;
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
        
        if( catalogType != null )
        {
            inventoryType.setCatalogType( catalogTypeService.getCatalogType( catalogType ) );
        }
        
        if( inventoryType != null )
        {
            inventoryType.getInventoryTypeAttributes().clear();
        }
        
        Set<InventoryTypeAttribute> inventoryTypeSet = new HashSet<InventoryTypeAttribute>();
        
        if ( selectedInventoryTypeAttributeList != null && selectedInventoryTypeAttributeList.size() > 0 )
        {
           
            for ( int i = 0; i < this.selectedInventoryTypeAttributeList.size(); i++ )
            {
                InventoryTypeAttribute inventoryTypeAttribute = inventoryTypeAttributeService.getInventoryTypeAttribute( selectedInventoryTypeAttributeList.get( i ) );
                /*
                System.out.println( "ID---" + inventoryTypeAttribute.getId() );
                System.out.println( "Name---" + inventoryTypeAttribute.getName());
                System.out.println( "ValueType---" + inventoryTypeAttribute.getValueType() );
                */
                inventoryTypeSet.add( inventoryTypeAttribute );
                
                //inventoryTypeSet.add( inventoryTypeAttributeService.getInventoryTypeAttribute( selectedInventoryTypeAttributeList.get( i ) ) );
            }
            /*
            for( Integer inventoryTypeAttId : selectedInventoryTypeAttributeList )
            {
                inventoryType.getInventoryTypeAttributes().add( inventoryTypeAttributeService.getInventoryTypeAttribute( inventoryTypeAttId ) );
            }
            */
        }
        inventoryType.setInventoryTypeAttributes( inventoryTypeSet );
        inventoryTypeService.updateInventoryType( inventoryType );
        
        return SUCCESS;
    }
}
