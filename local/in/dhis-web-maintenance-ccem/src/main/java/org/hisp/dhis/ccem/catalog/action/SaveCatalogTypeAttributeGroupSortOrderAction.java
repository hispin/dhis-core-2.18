package org.hisp.dhis.ccem.catalog.action;

import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroup;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version SaveCatalogTypeAttributeGroupSortOrderAction.javaOct 11, 2012 5:55:22 PM	
 */

public class SaveCatalogTypeAttributeGroupSortOrderAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CatalogTypeAttributeGroupService catalogTypeAttributeGroupService;
    
    public void setCatalogTypeAttributeGroupService( CatalogTypeAttributeGroupService catalogTypeAttributeGroupService )
    {
        this.catalogTypeAttributeGroupService = catalogTypeAttributeGroupService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private List<String> catalogTypeAttributeGroups;
        
    public void setCatalogTypeAttributeGroups( List<String> catalogTypeAttributeGroups )
    {
        this.catalogTypeAttributeGroups = catalogTypeAttributeGroups;
    }
    
    private String catalogTypeId;
    
    public void setCatalogTypeId( String catalogTypeId )
    {
        this.catalogTypeId = catalogTypeId;
    }
    
    public String getCatalogTypeId()
    {
        return catalogTypeId;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    

    public String execute() throws Exception
    {
        int sortOrder = 1;
        
        for ( String id : catalogTypeAttributeGroups )
        {
            CatalogTypeAttributeGroup catalogTypeAttributeGroup = catalogTypeAttributeGroupService.getCatalogTypeAttributeGroupById( Integer.parseInt( id ) );
            
            catalogTypeAttributeGroup.setSortOrder( sortOrder++ );
            
            catalogTypeAttributeGroupService.updateCatalogTypeAttributeGroup( catalogTypeAttributeGroup );
        }
        
        return SUCCESS;
    }
}


