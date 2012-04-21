package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultCatalogTypeService implements CatalogTypeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CatalogTypeStore catalogTypeStore;
    
    public void setCatalogTypeStore( CatalogTypeStore catalogTypeStore )
    {
        this.catalogTypeStore = catalogTypeStore;
    }
    
    // -------------------------------------------------------------------------
    // CatalogType
    // -------------------------------------------------------------------------

    @Transactional
    @Override
    public int addCatalogType( CatalogType catalogType )
    {
        return catalogTypeStore.addCatalogType( catalogType );
    }
    
    @Transactional
    @Override
    public void deleteCatalogType( CatalogType catalogType )
    {
        catalogTypeStore.deleteCatalogType( catalogType );
    }

    @Transactional
    @Override
    public Collection<CatalogType> getAllCatalogTypes()
    {
        return catalogTypeStore.getAllCatalogTypes();
    }

    @Transactional
    @Override
    public void updateCatalogType( CatalogType catalogType )
    {
        catalogTypeStore.updateCatalogType( catalogType );
    }
}
