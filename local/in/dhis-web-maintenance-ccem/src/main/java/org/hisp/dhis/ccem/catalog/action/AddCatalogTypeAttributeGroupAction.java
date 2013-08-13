package org.hisp.dhis.ccem.catalog.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroup;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroupService;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeService;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version AddCatalogTypeAttributeGroupAction.javaOct 10, 2012 2:58:01 PM	
 */

public class AddCatalogTypeAttributeGroupAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CatalogTypeService catalogTypeService;
    
    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }

    private CatalogTypeAttributeService catalogTypeAttributeService;

    public void setCatalogTypeAttributeService( CatalogTypeAttributeService catalogTypeAttributeService )
    {
        this.catalogTypeAttributeService = catalogTypeAttributeService;
    }
    
    private CatalogTypeAttributeGroupService catalogTypeAttributeGroupService;
    
    public void setCatalogTypeAttributeGroupService( CatalogTypeAttributeGroupService catalogTypeAttributeGroupService )
    {
        this.catalogTypeAttributeGroupService = catalogTypeAttributeGroupService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------


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

    private List<Integer> selectedCatalogTypeAttributesValidator = new ArrayList<Integer>();

    public void setSelectedCatalogTypeAttributesValidator( List<Integer> selectedCatalogTypeAttributesValidator )
    {
        this.selectedCatalogTypeAttributesValidator = selectedCatalogTypeAttributesValidator;
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
        CatalogTypeAttributeGroup catalogTypeAttributeGroup = new CatalogTypeAttributeGroup();
        
        catalogTypeAttributeGroup.setName( name );
        catalogTypeAttributeGroup.setDescription( description );
        
        CatalogType catalogType = catalogTypeService.getCatalogType( Integer.parseInt( catalogTypeId ) );
        catalogTypeAttributeGroup.setCatalogType( catalogType );
        
        catalogTypeAttributeGroup.setSortOrder( 0 );
        
        List<CatalogTypeAttribute> catalogTypeAttributes = new ArrayList<CatalogTypeAttribute>();
        
        //System.out.println( "- Size of selectedCatalogTypeAttributesValidator List is : " + selectedCatalogTypeAttributesValidator.size());
        
        for ( int i = 0; i < this.selectedCatalogTypeAttributesValidator.size(); i++ )
        {
            CatalogTypeAttribute catalogTypeAttribute = catalogTypeAttributeService.getCatalogTypeAttribute( selectedCatalogTypeAttributesValidator.get( i ) );
            
            catalogTypeAttributes.add( catalogTypeAttribute );
            
        }
        //System.out.println( "- Size of catalogTypeAttributes List is : " + catalogTypeAttributes.size());
        
        catalogTypeAttributeGroup.setCatalogTypeAttributes( catalogTypeAttributes );
        
        catalogTypeAttributeGroupService.addCatalogTypeAttributeGroup( catalogTypeAttributeGroup );
        
        return SUCCESS;
    }
}


