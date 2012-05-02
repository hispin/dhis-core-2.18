package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;

public interface CatalogStore extends GenericStore<Catalog>
{

    String ID = CatalogStore.class.getName();
    
    int addCatalog( Catalog catalog );

    void updateCatalog( Catalog catalog );

    void deleteCatalog( Catalog catalog );
    
    Catalog getCatalog( int id );
    
    Catalog getCatalogByName( String name );

    Collection<Catalog> getAllCatalogs();
    
    Collection<Catalog> getCatalogs( CatalogType catalogType );
}
