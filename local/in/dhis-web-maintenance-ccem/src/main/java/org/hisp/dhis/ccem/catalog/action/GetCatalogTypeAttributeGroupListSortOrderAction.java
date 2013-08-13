package org.hisp.dhis.ccem.catalog.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroup;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogTypeAttributeGroupOrderComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetCatalogTypeAttributeGroupListSortOrderAction.javaOct 11, 2012 5:18:16 PM	
 */

public class GetCatalogTypeAttributeGroupListSortOrderAction implements Action
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
    // Input & Output
    // -------------------------------------------------------------------------

    private String catalogTypeId;
    
    public void setCatalogTypeId( String catalogTypeId )
    {
        this.catalogTypeId = catalogTypeId;
    }
    
    public String getCatalogTypeId()
    {
        return catalogTypeId;
    }

    private List<CatalogTypeAttributeGroup> catalogTypeAttributeGroups;

    public List<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroups()
    {
        return catalogTypeAttributeGroups;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        CatalogType catalogType = catalogTypeService.getCatalogType( Integer.parseInt( catalogTypeId ) );
        
        catalogTypeAttributeGroups = new ArrayList<CatalogTypeAttributeGroup>( catalogType.getCatalogTypeAttributeGroups() );

        Collections.sort( catalogTypeAttributeGroups, new CatalogTypeAttributeGroupOrderComparator() );

        return SUCCESS;
    }
}
