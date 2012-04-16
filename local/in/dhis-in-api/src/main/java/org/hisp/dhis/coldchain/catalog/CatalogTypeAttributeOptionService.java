package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

public interface CatalogTypeAttributeOptionService
{
    String ID = CatalogTypeAttributeOptionService.class.getName();
    
    int addCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    void updateCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    void deleteCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption );

    Collection<CatalogTypeAttributeOption> getAllCatalogTypeAttributeOptions();

}
