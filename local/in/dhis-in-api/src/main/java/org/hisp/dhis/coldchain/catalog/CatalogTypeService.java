package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogTypeService
{
    String ID = CatalogTypeService.class.getName();
    
    int addCatalogType( CatalogType catalogType );

    void updateCatalogType( CatalogType catalogType );

    void deleteCatalogType( CatalogType catalogType );

    Collection<CatalogType> getAllCatalogTypes();

}
