package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogTypeStore
{
    String ID = CatalogTypeStore.class.getName();
    
    int addCatalogType( CatalogType catalogType );

    void updateCatalogType( CatalogType catalogType );

    void deleteCatalogType( CatalogType catalogType );
    
    CatalogType getCatalogType( int id );
    
    CatalogType getCatalogTypeByName( String name );
    
    //CatalogType getCatalogTypeByAttribute( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute);

    Collection<CatalogType> getAllCatalogTypes();

}
