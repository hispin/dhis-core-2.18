package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;
import java.util.List;

public interface CatalogService
{
    String ID = CatalogService.class.getName();
    
    int addCatalog( Catalog catalog );

    void updateCatalog( Catalog catalog );

    void deleteCatalog( Catalog catalog );
    
    void deleteCatalogData( Catalog catalog );

    Collection<Catalog> getAllCatalogs();
    
    Catalog getCatalog( int id );
    
    Catalog getCatalogByName( String name );
    
    Collection<Catalog> getCatalogs( CatalogType catalogType );
    
    int createCatalog( Catalog catalog, List<CatalogDataValue> catalogDataValues );
    
    void updateCatalogAndDataValue(  Catalog catalog, List<CatalogDataValue> valuesForSave, List<CatalogDataValue> valuesForUpdate, Collection<CatalogDataValue> valuesForDelete );

    void deleteCatalogAndDataValue( Catalog catalog );
}
