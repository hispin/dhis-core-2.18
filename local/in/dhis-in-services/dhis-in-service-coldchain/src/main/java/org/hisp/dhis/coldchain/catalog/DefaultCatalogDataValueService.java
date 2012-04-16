package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultCatalogDataValueService implements CatalogDataValueService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CatalogDataValueStore catalogDataValueStore;
    
    public void setCatalogDataValueStore( CatalogDataValueStore catalogDataValueStore )
    {
        this.catalogDataValueStore = catalogDataValueStore;
    }
    
    // -------------------------------------------------------------------------
    // CatalogDataValue
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addCatalogDataValue( CatalogDataValue catalogDataValue )
    {
        return catalogDataValueStore.addCatalogDataValue( catalogDataValue );
    }
    
    @Transactional
    @Override
    public void deleteCatalogDataValue( CatalogDataValue catalogDataValue )
    {
        catalogDataValueStore.deleteCatalogDataValue( catalogDataValue );
    }
    
    @Transactional
    @Override
    public Collection<CatalogDataValue> getAllCatalogDataValues()
    {
        return catalogDataValueStore.getAllCatalogDataValues();
    }
    
    @Transactional
    @Override
    public void updateCatalogDataValue( CatalogDataValue catalogDataValue )
    {
        catalogDataValueStore.updateCatalogDataValue( catalogDataValue );
    }

}
