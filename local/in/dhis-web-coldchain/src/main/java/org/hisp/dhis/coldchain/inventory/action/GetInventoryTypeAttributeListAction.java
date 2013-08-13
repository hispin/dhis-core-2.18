package org.hisp.dhis.coldchain.inventory.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType_Attribute;
import org.hisp.dhis.coldchain.inventory.InventoryType_AttributeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;

public class GetInventoryTypeAttributeListAction extends ActionPagingSupport<InventoryTypeAttribute>
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private InventoryTypeAttributeService inventoryTypeAttributeService;

    public void setInventoryTypeAttributeService( InventoryTypeAttributeService inventoryTypeAttributeService )
    {
        this.inventoryTypeAttributeService = inventoryTypeAttributeService;
    }
    
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
    private List<InventoryTypeAttribute> inventoryTypeAttributes;

    public List<InventoryTypeAttribute> getInventoryTypeAttributes()
    {
        return inventoryTypeAttributes;
    }

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String key;
    
    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
    
    public List<InventoryType_Attribute> inventoryTypeAttributeList;
    
    public List<InventoryType_Attribute> getInventoryTypeAttributeList()
    {
        return inventoryTypeAttributeList;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    throws Exception
    {
        
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( inventoryTypeAttributeService.getInventoryTypeAttributeCountByName( key ) );
            
            inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( inventoryTypeAttributeService.getInventoryTypeAttributesBetweenByName( key, paging.getStartPos(), paging.getPageSize() ));
            Collections.sort( inventoryTypeAttributes, new IdentifiableObjectNameComparator() );
        }
        
        else if ( id != null )
        {
            InventoryType inventoryType = inventoryTypeService.getInventoryType( id );
            /*
            inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>();
            
            for( InventoryType_Attribute inventoryType_Attribute : inventoryType.getInventoryType_Attributes() )
            {
                inventoryTypeAttributes.add( inventoryType_Attribute.getInventoryTypeAttribute() );
            }
            */
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
            /*
            inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>();
            for( InventoryType_Attribute inventoryType_Attribute : inventoryTypeAttributeList )
            {
                inventoryTypeAttributes.add( inventoryType_Attribute.getInventoryTypeAttribute() );
            }
            */
        }
       
        else
        {
            this.paging = createPaging( inventoryTypeAttributeService.getInventoryTypeAttributeCount() );
         
            inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( inventoryTypeAttributeService.getInventoryTypeAttributesBetween( paging.getStartPos(), paging.getPageSize() ));
            Collections.sort( inventoryTypeAttributes, new IdentifiableObjectNameComparator() );
        }
        
        //Collections.sort( inventoryTypeAttributes, new IdentifiableObjectNameComparator() );
        
        //System.out.println(" Inside GetInventoryTypeAttributeListAction");
        /*
        if( id != null )
        {
            InventoryType inventoryType = inventoryTypeService.getInventoryType( id );
            
            inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( inventoryType.getInventoryTypeAttributes() );
        }
        else
        {
            inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( inventoryTypeAttributeService.getAllInventoryTypeAttributes() );
        }
        Collections.sort( inventoryTypeAttributes, new InventoryTypeAttributeComparator() );
        */
        
        /**
         * TODO - need to write comparator for sorting the list
         */
        
        return SUCCESS;
    }

}
