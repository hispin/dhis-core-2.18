package org.hisp.dhis.coldchain.catalog.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;

import com.opensymphony.xwork2.Action;

public class ShowAddCatalogFormAction
implements Action
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
    // Input/Output
    // -------------------------------------------------------------------------
    
    private int catalogTypeId;
    
    public void setCatalogTypeId( int catalogTypeId )
    {
        this.catalogTypeId = catalogTypeId;
    }
/*
    private Collection<CatalogTypeAttribute> catalogTypeAttributes;
    
    public Collection<CatalogTypeAttribute> getCatalogTypeAttributes()
    {
        return catalogTypeAttributes;
    }
*/    
    
    private List<CatalogTypeAttribute> catalogTypeAttributes = new ArrayList<CatalogTypeAttribute>();
    
    public List<CatalogTypeAttribute> getCatalogTypeAttributes()
    {
        return catalogTypeAttributes;
    }
    
    private CatalogType catalogType;
    
    public CatalogType getCatalogType()
    {
        return catalogType;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        catalogType = catalogTypeService.getCatalogType( catalogTypeId );
        
        //catalogTypeAttributes =  catalogType.getCatalogTypeAttributes();
        
        catalogTypeAttributes = new ArrayList<CatalogTypeAttribute> ( catalogType.getCatalogTypeAttributes());
        //Collections.sort( catalogTypeAttributes, new CatalogTypeAttributeComparator() );
        
        /*
        System.out.println( "Name of CatalogType is ======  :" + catalogType.getName() );
        System.out.println( "Size of catalogTypeAttributes  :" + catalogTypeAttributes.size() );
        for( CatalogTypeAttribute catalogTypeAttribute : catalogTypeAttributes )
        {
            System.out.println( "Name :" + catalogTypeAttribute.getName() );
            System.out.println( "valueType :" + catalogTypeAttribute.getValueType() );
            System.out.println( "Is mandatory :" + catalogTypeAttribute.isMandatory() );
        }
        */
        return SUCCESS;
    }


}
