package org.hisp.dhis.coldchain.inventory;

import java.io.Serializable;
import java.util.Set;

import org.hisp.dhis.coldchain.catalog.CatalogType;

public class InventoryType implements Serializable
{
    private int id;
    
    private String name;
    
    private String description;
    
    private boolean tracking;
    
    private CatalogType catalogType;
    
    private Set<InventoryTypeAttribute> inventoryTypeAttributes;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public InventoryType()
    {
        
    }
    public InventoryType( String name, boolean tracking )
    {
        this.name = name;
        this.tracking = tracking;
    }
    
    public InventoryType( String name, String description, boolean tracking, CatalogType catalogType )
    {
        this.name = name;
        this.description = description;
        this.tracking = tracking;
        this.catalogType = catalogType;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof InventoryType) )
        {
            return false;
        }

        final InventoryType other = (InventoryType) o;

        return name.equals( other.getName() );
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public int getId()
    {
        return id;
    }
    public void setId( int id )
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription( String description )
    {
        this.description = description;
    }
    public boolean isTracking()
    {
        return tracking;
    }
    public void setTracking( boolean tracking )
    {
        this.tracking = tracking;
    }
    public CatalogType getCatalogType()
    {
        return catalogType;
    }
    public void setCatalogType( CatalogType catalogType )
    {
        this.catalogType = catalogType;
    }
    public Set<InventoryTypeAttribute> getInventoryTypeAttributes()
    {
        return inventoryTypeAttributes;
    }
    public void setInventoryTypeAttributes( Set<InventoryTypeAttribute> inventoryTypeAttributes )
    {
        this.inventoryTypeAttributes = inventoryTypeAttributes;
    }
    
}
