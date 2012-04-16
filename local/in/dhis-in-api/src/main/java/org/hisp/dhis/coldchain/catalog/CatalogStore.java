package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogStore
{

    String ID = CatalogStore.class.getName();
    
    int addCatalog( Catalog catalog );

    void updateCatalog( Catalog catalog );

    void deleteCatalog( Catalog catalog );

    Collection<Catalog> getAllCatalogs();
    
}
