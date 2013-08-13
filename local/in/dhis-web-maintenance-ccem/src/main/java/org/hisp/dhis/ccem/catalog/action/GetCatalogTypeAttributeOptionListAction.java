package org.hisp.dhis.ccem.catalog.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeOption;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeService;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogTypeAttributeOptionComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetCatalogTypeAttributeOptionListAction.javaAug 18, 2012 1:55:48 PM	
 */

public class GetCatalogTypeAttributeOptionListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private CatalogTypeAttributeService catalogTypeAttributeService;
    
    public void setCatalogTypeAttributeService( CatalogTypeAttributeService catalogTypeAttributeService )
    {
        this.catalogTypeAttributeService = catalogTypeAttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private List<CatalogTypeAttributeOption> catalogTypeAttributeOption = new ArrayList<CatalogTypeAttributeOption>();
    
    public List<CatalogTypeAttributeOption> getCatalogTypeAttributeOption()
    {
        return catalogTypeAttributeOption;
    }

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        CatalogTypeAttribute catalogTypeAttribute = catalogTypeAttributeService.getCatalogTypeAttribute( id );
        
        if( CatalogTypeAttribute.TYPE_COMBO.equalsIgnoreCase( catalogTypeAttribute.getValueType() ) )
        {
            catalogTypeAttributeOption = new ArrayList<CatalogTypeAttributeOption>( catalogTypeAttribute.getAttributeOptions() );
            Collections.sort( catalogTypeAttributeOption, new CatalogTypeAttributeOptionComparator() );
        }
        
        //System.out.println("Size of catalogTypeAttributeOption List is  " + catalogTypeAttributeOption.size()+ "--- catalogTypeAttribute value type is---- " + catalogTypeAttribute.getValueType() );
        
        return SUCCESS;
    }
    
    
}
