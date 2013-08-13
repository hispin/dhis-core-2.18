package org.hisp.dhis.coldchain.equipment.manager.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version EquipmentCatalogsAction.javaOct 22, 2012 3:54:13 PM	
 */

public class EquipmentCatalogsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CatalogTypeService catalogTypeService;
    
    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }

    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private List<CatalogType> catalogTypes;
    
    public List<CatalogType> getCatalogTypes()
    {
        return catalogTypes;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        catalogTypes = new ArrayList<CatalogType>( catalogTypeService.getAllCatalogTypes() );
        
        Iterator<CatalogType> catalogTypesIterator = catalogTypes.iterator();
        while( catalogTypesIterator.hasNext() )
        {
            CatalogType catalogType = catalogTypesIterator.next();
            
            if ( catalogType.getDescription().equalsIgnoreCase( CatalogType.PREFIX_CATALOG_TYPE ) )
            {
                catalogTypesIterator.remove( );
            }
            
        }
        
        return SUCCESS;
    }

}



