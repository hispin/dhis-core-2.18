package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogService
{
    String ID = CatalogService.class.getName();
    
    int addCatalog( Catalog catalog );

    void updateCatalog( Catalog catalog );

    void deleteCatalog( Catalog catalog );

    Collection<Catalog> getAllCatalogs();

}
