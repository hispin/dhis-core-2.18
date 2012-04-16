package org.hisp.dhis.coldchain.catalog;

import java.io.Serializable;
import java.util.Set;

public class CatalogType implements Serializable
{
    private int id;
    
    private String name;
    
    private String description;
    
    private Set<CatalogTypeAttribute> catalogTypeAttributes;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public CatalogType()
    {

    }
    public CatalogType( String name )
    {
        this.name = name;
    }
    
    public CatalogType( String name, String description )
    {
        this.name = name;
        this.description = description;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
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

        if ( !(o instanceof CatalogType) )
        {
            return false;
        }

        final CatalogType other = (CatalogType) o;

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
    public Set<CatalogTypeAttribute> getCatalogTypeAttributes()
    {
        return catalogTypeAttributes;
    }
    public void setCatalogTypeAttributes( Set<CatalogTypeAttribute> catalogTypeAttributes )
    {
        this.catalogTypeAttributes = catalogTypeAttributes;
    }

}
