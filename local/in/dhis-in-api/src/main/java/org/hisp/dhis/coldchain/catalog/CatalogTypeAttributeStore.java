package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogTypeAttributeStore
{
    String ID = CatalogTypeAttributeStore.class.getName();
    
    int addCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );

    void updateCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );

    void deleteCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute );
    
    CatalogTypeAttribute getCatalogTypeAttribute( int id );
    
    CatalogTypeAttribute getCatalogTypeAttributeByName( String name );
    
    Collection<CatalogTypeAttribute> getAllCatalogTypeAttributes();

}
