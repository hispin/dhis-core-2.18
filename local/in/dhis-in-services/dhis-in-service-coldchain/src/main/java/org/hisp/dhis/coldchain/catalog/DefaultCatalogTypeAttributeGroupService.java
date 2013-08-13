package org.hisp.dhis.coldchain.catalog;

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;

import java.util.Collection;

import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultCatalogTypeAttributeGroupService.javaOct 9, 2012 3:54:39 PM	
 */
@Transactional
public class DefaultCatalogTypeAttributeGroupService implements CatalogTypeAttributeGroupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CatalogTypeAttributeGroupStore catalogTypeAttributeGroupStore;

    public void setCatalogTypeAttributeGroupStore( CatalogTypeAttributeGroupStore catalogTypeAttributeGroupStore )
    {
        this.catalogTypeAttributeGroupStore = catalogTypeAttributeGroupStore;
    }
    
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    // -------------------------------------------------------------------------
    // CatalogTypeAttributeGroup
    // -------------------------------------------------------------------------
    /*
    public void addCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        catalogTypeAttributeGroupStore.addCatalogTypeAttributeGroup( catalogTypeAttributeGroup );
    }
    
    public void deleteCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        catalogTypeAttributeGroupStore.deleteCatalogTypeAttributeGroup( catalogTypeAttributeGroup );
    }
    
    public void updateCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        catalogTypeAttributeGroupStore.updateCatalogTypeAttributeGroup( catalogTypeAttributeGroup );
    }
    */
    
    @Override
    public int addCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        return catalogTypeAttributeGroupStore.save( catalogTypeAttributeGroup );
    }
    
    @Override
    public void deleteCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        catalogTypeAttributeGroupStore.delete( catalogTypeAttributeGroup );
    }
    
    @Override
    public void updateCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        catalogTypeAttributeGroupStore.update( catalogTypeAttributeGroup );
    }
    
    
    
    @Override    
    public CatalogTypeAttributeGroup getCatalogTypeAttributeGroupById( int id )
    {
        return catalogTypeAttributeGroupStore.getCatalogTypeAttributeGroupById( id );
    }
    @Override
    public CatalogTypeAttributeGroup getCatalogTypeAttributeGroupByName( String name )
    {
        return catalogTypeAttributeGroupStore.getCatalogTypeAttributeGroupByName( name );
    }
    @Override
    public Collection<CatalogTypeAttributeGroup> getAllCatalogTypeAttributeGroups()
    {
        return catalogTypeAttributeGroupStore.getAllCatalogTypeAttributeGroups();
    }
    @Override
    public Collection<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupsByCatalogType( CatalogType catalogType )
    {
        return catalogTypeAttributeGroupStore.getCatalogTypeAttributeGroupsByCatalogType( catalogType );
    }
    
    
    //Methods
   
    public int getCatalogTypeAttributeGroupCount()
    {
        return catalogTypeAttributeGroupStore.getCount();
    }
    
    public int getCatalogTypeAttributeGroupCountByName( String name )
    {
        return getCountByName( i18nService, catalogTypeAttributeGroupStore, name );
    }

    public Collection<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, catalogTypeAttributeGroupStore, first, max );
    }

    public Collection<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, catalogTypeAttributeGroupStore, name, first, max );
    }
   
    
    
}

