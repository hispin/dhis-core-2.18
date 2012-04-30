package org.hisp.dhis.coldchain.catalog;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
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
    
    private CatalogDataValueService catalogDataValueService;
    
    public void setCatalogDataValueService( CatalogDataValueService catalogDataValueService )
    {
        this.catalogDataValueService = catalogDataValueService;
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
    
    @Override
    public void deleteCatalogData( Catalog catalog )
    {
        catalogStore.delete( catalog );
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
    
    @Transactional
    @Override
    public Catalog getCatalog( int id )
    {
        return catalogStore.getCatalog( id );
    }
    
    @Transactional
    @Override
    public Catalog getCatalogByName( String name )
    {
        return catalogStore.getCatalogByName( name );
    }
    
    @Override
    public int  createCatalog( Catalog catalog, List<CatalogDataValue> catalogDataValues )
    {
        int catalogId = addCatalog( catalog );

        for ( CatalogDataValue catalogDataValue : catalogDataValues )
        {
            catalogDataValueService.addCatalogDataValue( catalogDataValue );
        }

        return catalogId;
    }

    @Override
    public void updateCatalogAndDataValue( Catalog catalog, List<CatalogDataValue> valuesForSave, List<CatalogDataValue> valuesForUpdate, Collection<CatalogDataValue> valuesForDelete )
    {
        //catalogStore.update( catalog );
        catalogStore.updateCatalog( catalog );
        
        for ( CatalogDataValue catalogDataValueAdd : valuesForSave )
        {
            catalogDataValueService.addCatalogDataValue( catalogDataValueAdd );
        }

        for ( CatalogDataValue catalogDataValueUpdate : valuesForUpdate )
        {
            catalogDataValueService.updateCatalogDataValue( catalogDataValueUpdate );
        }
        
        for ( CatalogDataValue catalogDataValueDelete : valuesForDelete )
        {
            catalogDataValueService.deleteCatalogDataValue( catalogDataValueDelete );
        }
    }
    
    @Override
    public void deleteCatalogAndDataValue( Catalog catalog )
    {
        Collection<CatalogDataValue> valuesForDelete = catalogDataValueService.getAllCatalogDataValuesByCatalog( catalog ) ;
        for ( CatalogDataValue catalogDataValueDelete : valuesForDelete )
        {
            catalogDataValueService.deleteCatalogDataValue( catalogDataValueDelete );
        }
        
        //catalogStore.deleteCatalog( catalog );
    }
}
