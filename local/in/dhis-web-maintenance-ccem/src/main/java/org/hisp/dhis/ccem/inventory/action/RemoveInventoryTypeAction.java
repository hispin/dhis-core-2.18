package org.hisp.dhis.ccem.inventory.action;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType_Attribute;
import org.hisp.dhis.coldchain.inventory.InventoryType_AttributeService;
import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;
import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork2.Action;

public class RemoveInventoryTypeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private InventoryTypeService inventoryTypeService;

    public void setInventoryTypeService( InventoryTypeService inventoryTypeService )
    {
        this.inventoryTypeService = inventoryTypeService;
    }
    
    private InventoryType_AttributeService inventoryType_AttributeService;
    
    public void setInventoryType_AttributeService( InventoryType_AttributeService inventoryType_AttributeService )
    {
        this.inventoryType_AttributeService = inventoryType_AttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        try
        {
            InventoryType inventoryType = inventoryTypeService.getInventoryType( id );
            
            if( inventoryType != null)
            {
                Set<InventoryType_Attribute> inventoryType_Attributes = new HashSet<InventoryType_Attribute>( inventoryType.getInventoryType_Attributes());
                
                for ( InventoryType_Attribute inventoryType_AttributeForDelete : inventoryType_Attributes )
                {
                    inventoryType_AttributeService.deleteInventoryType_Attribute( inventoryType_AttributeForDelete );
                }
            }
            
            /*
            if( inventoryType != null)
            {
                inventoryType.getInventoryType_Attributes().clear();
            }
            */
            
            inventoryTypeService.deleteInventoryType( inventoryType );
        }
        
        
        catch ( DataIntegrityViolationException ex )
        {
            message = i18n.getString( "object_not_deleted_associated_by_objects" );

            return ERROR;
        }
        
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();
            }
            
            return ERROR;
        }
        
        return SUCCESS;
    }
}
