package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogDataValueService
{
    String ID = CatalogDataValueService.class.getName();
    
    int addCatalogDataValue( CatalogDataValue catalogDataValue );

    void updateCatalogDataValue( CatalogDataValue catalogDataValue );

    void deleteCatalogDataValue( CatalogDataValue catalogDataValue );

    Collection<CatalogDataValue> getAllCatalogDataValues();

}
