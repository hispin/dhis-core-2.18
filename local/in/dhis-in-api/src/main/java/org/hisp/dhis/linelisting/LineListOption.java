package org.hisp.dhis.linelisting;

import java.io.Serializable;

import org.hisp.dhis.common.MetaObject;

public class LineListOption implements Serializable, MetaObject
{
    /**
     * The unique identifier for this LineListing Option
     */
    private int id;

    /**
     * Name of LineListing Option. Required and unique.
     */
    private String name;
    
    /**
     * Short Name of LineListing Option
     */
    private String shortName;
    
    /**
     * Description of the LineListing Option - For instance Malaria option can be used to
     * record both Line Listing Maternal Death and Line Listing Death Hence making it an option
     * to be used in the combo box of cause of death while entering data
     */
    private String description;

    /**
     * Indicating position in the custom sort order.
     */
    private Integer sortOrder;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public LineListOption()
    {
    }
    
    public LineListOption(String name)
    {
        this.name = name;
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

        if ( !(o instanceof LineListOption) )
        {
            return false;
        }

        final LineListOption other = (LineListOption) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

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

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public String getCode()
    {
        return shortName;
    }

    public void setCode( String shortName )
    {
        this.shortName = shortName;
    }
    
    public String getAlternativeName()
    {
        return getShortName();
    }
    
    public void setAlternativeName( String alternativeName )
    {
        throw new UnsupportedOperationException( "Cannot set alternativename on DataSet: " + alternativeName );
    }
    
}
