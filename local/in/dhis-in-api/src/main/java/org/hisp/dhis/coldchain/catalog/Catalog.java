package org.hisp.dhis.coldchain.catalog;

import java.io.Serializable;

public class Catalog implements Serializable
{
    private int id;
    
    private String name;
    
    private String description;

    private CatalogType catalogType;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public Catalog()
    {
        
    }

    public Catalog( String name, CatalogType catalogType )
    {
        this.name = name;
        this.catalogType = catalogType;
    }
    
    public Catalog( String name, String description, CatalogType catalogType )
    {
        this.name = name;
        this.description = description;
        this.catalogType = catalogType;
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

        if ( !(o instanceof Catalog) )
        {
            return false;
        }

        final Catalog other = (Catalog) o;

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

    public CatalogType getCatalogType()
    {
        return catalogType;
    }

    public void setCatalogType( CatalogType catalogType )
    {
        this.catalogType = catalogType;
    }
    
}
