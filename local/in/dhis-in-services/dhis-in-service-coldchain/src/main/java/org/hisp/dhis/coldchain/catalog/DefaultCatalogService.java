package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultCatalogService implements CatalogService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CatalogStore catalogStore;
    
    public void setCatalogStore( CatalogStore catalogStore )
    {
        this.catalogStore = catalogStore;
    }

    // -------------------------------------------------------------------------
    // Catalog
    // -------------------------------------------------------------------------
    @Transactional
    public int addCatalog( Catalog catalog )
    {
        return catalogStore.addCatalog( catalog );
    }

    @Transactional
    public void deleteCatalog( Catalog catalog )
    {
        catalogStore.deleteCatalog( catalog );
    }

    @Transactional
    public void updateCatalog( Catalog catalog )
    {
        catalogStore.updateCatalog( catalog );
    }

    @Transactional
    public Collection<Catalog> getAllCatalogs()
    {
        return catalogStore.getAllCatalogs();
    }
    
}
