package org.hisp.dhis.coldchain.catalog.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogDataValue;
import org.hisp.dhis.coldchain.catalog.CatalogDataValueService;
import org.hisp.dhis.coldchain.catalog.CatalogService;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.coldchain.catalog.CatalogTypeService;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogTypeAttributeComparator;

import com.opensymphony.xwork2.Action;

public class UpdateCatalogFormAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CatalogService catalogService;
    
    public void setCatalogService( CatalogService catalogService )
    {
        this.catalogService = catalogService;
    }
    
    private CatalogTypeService catalogTypeService;
    
    public void setCatalogTypeService( CatalogTypeService catalogTypeService )
    {
        this.catalogTypeService = catalogTypeService;
    }
    
    private CatalogDataValueService catalogDataValueService;
    
    
    public void setCatalogDataValueService( CatalogDataValueService catalogDataValueService )
    {
        this.catalogDataValueService = catalogDataValueService;
    }


    // -------------------------------------------------------------------------
    // Input/Output and Getter / Setter
    // -------------------------------------------------------------------------

    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }


    private Catalog catalog;
    

    public Catalog getCatalog()
    {
        return catalog;
    }
    
    private Map<Integer, String> catalogTypeAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getCatalogTypeAttributeValueMap()
    {
        return catalogTypeAttributeValueMap;
    }

    private List<CatalogType> catalogTypes;
    
    public List<CatalogType> getCatalogTypes()
    {
        return catalogTypes;
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
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        catalog = catalogService.getCatalog( id );
        
        catalogTypes = new ArrayList<CatalogType>( catalogTypeService.getAllCatalogTypes());
        
 
        // -------------------------------------------------------------------------
        // Get catalog attribute values
        // -------------------------------------------------------------------------

        Catalog tempCatalog = catalogService.getCatalog( id );
        
        CatalogType catalogType = catalogTypeService.getCatalogType( tempCatalog.getCatalogType().getId() );
        
        //catalogTypeAttributes = catalogType.getCatalogTypeAttributes();
        
        catalogTypeAttributes = new ArrayList<CatalogTypeAttribute> ( catalogType.getCatalogTypeAttributes());
        //Collections.sort( catalogTypeAttributes, new CatalogTypeAttributeComparator() );
        
        List<CatalogDataValue> catalogDataValues = new ArrayList<CatalogDataValue>( catalogDataValueService.getAllCatalogDataValuesByCatalog( catalogService.getCatalog( id )) );
        
        
        for( CatalogDataValue catalogDataValue : catalogDataValues )
        {
            if ( CatalogTypeAttribute.TYPE_COMBO.equalsIgnoreCase( catalogDataValue.getCatalogTypeAttribute().getValueType() ) )
            {
                catalogTypeAttributeValueMap.put( catalogDataValue.getCatalogTypeAttribute().getId(), catalogDataValue.getCatalogTypeAttributeOption().getName() );
            }
            
            else
            {
                catalogTypeAttributeValueMap.put( catalogDataValue.getCatalogTypeAttribute().getId(), catalogDataValue.getValue() );
            }
        }
       /*
        System.out.println( "Size of catalog Data Values Map  :" + catalogTypeAttributeValueMap.size() );
        for( CatalogDataValue  tempcatalogDataValue  : catalogDataValues )
        {
            System.out.println( "Map value is ------- :" + catalogTypeAttributeValueMap.get( tempcatalogDataValue.getCatalogTypeAttribute().getId() ));
            
        }
        */
        
        

        return SUCCESS;

    }

}

