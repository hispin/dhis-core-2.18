package org.hisp.dhis.coldchain.inventory.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType_Attribute;
import org.hisp.dhis.coldchain.inventory.InventoryType_AttributeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetInventoryTypeAttributeListForDisplayAction.javaOct 25, 2012 2:31:21 PM	
 */

public class GetInventoryTypeAttributeListForDisplayAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
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
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------
   
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    public List<InventoryType_Attribute> inventoryTypeAttributeList;
    
    public List<InventoryType_Attribute> getInventoryTypeAttributeList()
    {
        return inventoryTypeAttributeList;
    }
    
    private List<InventoryTypeAttribute> inventoryTypeAttributes;

    public List<InventoryTypeAttribute> getInventoryTypeAttributes()
    {
        return inventoryTypeAttributes;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        InventoryType inventoryType = inventoryTypeService.getInventoryType( id );
        
        inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>( inventoryType_AttributeService.getAllInventoryTypeAttributeForDisplay( inventoryType, true ) );
        
        if( inventoryTypeAttributeList == null || inventoryTypeAttributeList.size() == 0  )
        {
            inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>( inventoryType_AttributeService.getAllInventoryTypeAttributesByInventoryType( inventoryType ) );
            
            if( inventoryTypeAttributeList != null && inventoryTypeAttributeList.size() > 3 )
            {
                int count = 1;
                Iterator<InventoryType_Attribute> iterator = inventoryTypeAttributeList.iterator();
                while( iterator.hasNext() )
                {
                    iterator.next();
                    
                    if( count > 3 )
                        iterator.remove();
                    
                    count++;
                }            
            }
            
        }
        
        inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>();
        
        for( InventoryType_Attribute inventoryType_Attribute : inventoryTypeAttributeList )
        {
            inventoryTypeAttributes.add( inventoryType_Attribute.getInventoryTypeAttribute() );
        }
        
        return SUCCESS;
    }

}

