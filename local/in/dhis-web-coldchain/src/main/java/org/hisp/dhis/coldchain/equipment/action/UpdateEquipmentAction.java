package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.inventory.EquipmentDetails;
import org.hisp.dhis.coldchain.inventory.EquipmentDetailsService;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeOption;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeOptionService;

import com.opensymphony.xwork2.Action;

public class UpdateEquipmentAction implements Action
{

    public static final String PREFIX_ATTRIBUTE = "attr";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private InventoryTypeAttributeOptionService inventoryTypeAttributeOptionService;
    
    private EquipmentInstanceService equipmentInstanceService;

    private EquipmentDetailsService equipmentDetailsService;
    
    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
    
    private Integer equipmentInstanceID;
    
    private String message;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {

        System.out.println("inside UpdateEquipmentAction : "+equipmentInstanceID);
        
        EquipmentInstance equipmentInstance = equipmentInstanceService.getEquipmentInstance( equipmentInstanceID );
        
        InventoryType inventoryType = equipmentInstance.getInventoryType();
        
        // -----------------------------------------------------------------------------
        // Preparing Equipment Details
        // -----------------------------------------------------------------------------
        HttpServletRequest request = ServletActionContext.getRequest();
        String value = null;
        
        List<InventoryTypeAttribute> inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( inventoryType.getInventoryTypeAttributes() );
        
        EquipmentDetails equipmentDetails = null;
        for ( InventoryTypeAttribute attribute : inventoryTypeAttributes )
        {
            value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
            
            equipmentDetails = equipmentDetailsService.getEquipmentDetails( equipmentInstance, attribute );
            
            if( equipmentDetails == null && value != null )
            {
                equipmentDetails = new EquipmentDetails();
                equipmentDetails.setEquipmentInstance( equipmentInstance );
                equipmentDetails.setInventoryTypeAttribute( attribute );

                if ( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    InventoryTypeAttributeOption option = inventoryTypeAttributeOptionService.getInventoryTypeAttributeOption( NumberUtils.toInt( value, 0 ) );
                    if ( option != null )
                    {
                        equipmentDetails.setInventoryTypeAttributeOption( option );
                        equipmentDetails.setValue( option.getName() );
                    }
                    else
                    {
                        // Someone deleted this option ...
                    }
                }
                else
                {
                    equipmentDetails.setValue( value.trim() );
                }
                
                equipmentDetailsService.addEquipmentDetails( equipmentDetails );
            }
            else
            {
                if ( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    InventoryTypeAttributeOption option = inventoryTypeAttributeOptionService.getInventoryTypeAttributeOption( NumberUtils.toInt( value, 0 ) );
                    if ( option != null )
                    {
                        equipmentDetails.setInventoryTypeAttributeOption( option );
                        equipmentDetails.setValue( option.getName() );
                    }
                    else
                    {
                        // Someone deleted this option ...
                    }
                }
                else
                {
                    equipmentDetails.setValue( value.trim() );
                }

                equipmentDetailsService.updateEquipmentDetails( equipmentDetails );
            }
                
        }
         
        message = ""+ equipmentInstanceID;
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    public String getMessage()
    {
        return message;
    }

    public void setInventoryTypeAttributeOptionService(
        InventoryTypeAttributeOptionService inventoryTypeAttributeOptionService )
    {
        this.inventoryTypeAttributeOptionService = inventoryTypeAttributeOptionService;
    }

    public void setEquipmentInstanceService( EquipmentInstanceService equipmentInstanceService )
    {
        this.equipmentInstanceService = equipmentInstanceService;
    }

    public void setEquipmentDetailsService( EquipmentDetailsService equipmentDetailsService )
    {
        this.equipmentDetailsService = equipmentDetailsService;
    }

    public void setEquipmentInstanceID( Integer equipmentInstanceID )
    {
        this.equipmentInstanceID = equipmentInstanceID;
    }


}
