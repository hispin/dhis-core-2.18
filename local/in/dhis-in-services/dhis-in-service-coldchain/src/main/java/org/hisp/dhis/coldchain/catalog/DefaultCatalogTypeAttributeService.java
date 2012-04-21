package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultCatalogTypeAttributeService implements CatalogTypeAttributeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CatalogTypeAttributeStore catalogTypeAttributeStore;

    public void setCatalogTypeAttributeStore( CatalogTypeAttributeStore catalogTypeAttributeStore )
    {
        this.catalogTypeAttributeStore = catalogTypeAttributeStore;
    }

    // -------------------------------------------------------------------------
    // CatalogTypeAttribute
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute )
    {
        return catalogTypeAttributeStore.addCatalogTypeAttribute( catalogTypeAttribute );
    }
    
    @Transactional
    @Override
    public void deleteCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute )
    {
        catalogTypeAttributeStore.deleteCatalogTypeAttribute( catalogTypeAttribute );
    }
    
    @Transactional
    @Override
    public Collection<CatalogTypeAttribute> getAllCatalogTypeAttributes()
    {
        return catalogTypeAttributeStore.getAllCatalogTypeAttributes();
    }
    
    @Transactional
    @Override
    public void updateCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute )
    {
        catalogTypeAttributeStore.updateCatalogTypeAttribute( catalogTypeAttribute );
    }
    
    @Transactional
    @Override
    public CatalogTypeAttribute getCatalogTypeAttribute( int id )
    {
        return catalogTypeAttributeStore.getCatalogTypeAttribute( id );
    }
    
    @Transactional
    @Override
    public CatalogTypeAttribute getCatalogTypeAttributeByName( String name )
    {
        return catalogTypeAttributeStore.getCatalogTypeAttributeByName( name );
        
    }
}
