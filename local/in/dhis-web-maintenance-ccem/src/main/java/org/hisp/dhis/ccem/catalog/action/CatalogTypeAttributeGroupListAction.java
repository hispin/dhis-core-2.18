package org.hisp.dhis.ccem.catalog.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroup;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroupService;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogTypeAttributeGroupOrderComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version CatalogTypeAttributeGroupListAction.javaOct 9, 2012 5:41:57 PM	
 */
//public class CatalogTypeAttributeGroupListAction extends ActionPagingSupport<CatalogTypeAttributeGroup>
public class CatalogTypeAttributeGroupListAction implements Action
{
  
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    private CatalogTypeService catalogTypeService;
    
    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }
    
    private CatalogTypeAttributeGroupService catalogTypeAttributeGroupService;
    
    public void setCatalogTypeAttributeGroupService( CatalogTypeAttributeGroupService catalogTypeAttributeGroupService )
    {
        this.catalogTypeAttributeGroupService = catalogTypeAttributeGroupService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------
    


    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }
    
    private CatalogType catalogType;

    public CatalogType getCatalogType()
    {
        return catalogType;
    }
    
    private List<CatalogTypeAttributeGroup> catalogTypeAttributeGroupList;
    
    public List<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupList()
    {
        return catalogTypeAttributeGroupList;
    }
    /*
    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
    */
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        catalogType = catalogTypeService.getCatalogType( id );
        
        //catalogTypeAttributeGroupList = new ArrayList<CatalogTypeAttributeGroup>( catalogType.getCatalogTypeAttributeGroups() );
        
        catalogTypeAttributeGroupList = new ArrayList<CatalogTypeAttributeGroup>( catalogTypeAttributeGroupService.getCatalogTypeAttributeGroupsByCatalogType( catalogType ) );       
        
        Collections.sort( catalogTypeAttributeGroupList, new CatalogTypeAttributeGroupOrderComparator() );
        
        /*
        for( CatalogTypeAttributeGroup catalogTypeAttributeGroup : catalogTypeAttributeGroupList )
        {
            System.out.println( catalogTypeAttributeGroup.getId() + " -- " + catalogTypeAttributeGroup.getName() );
        }
        */
        
        /*
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( catalogTypeAttributeGroupService..getCatalogTypeAttributeGroupCountByName( key ) );
            
            catalogTypeAttributeGroupList = new ArrayList<CatalogTypeAttributeGroup>( catalogTypeAttributeGroupService.getCatalogTypeAttributeGroupsBetweenByName( key, paging.getStartPos(), paging.getPageSize() ));
        }
        else
        {
            this.paging = createPaging( catalogTypeAttributeGroupService.getCatalogTypeAttributeGroupCount() );
            
            catalogTypeAttributeGroupList = new ArrayList<CatalogTypeAttributeGroup>( catalogTypeAttributeGroupService.getCatalogTypeAttributeGroupsBetween( paging.getStartPos(), paging.getPageSize() ));
        }
        */
        
        return SUCCESS;
    }
}




