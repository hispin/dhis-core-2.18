package org.hisp.dhis.linelisting;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.common.MetaObject;

public class LineListElement implements Serializable, MetaObject
{
    public static final String TYPE_STRING = "string";

    public static final String TYPE_INT = "int";

    public static final String TYPE_BOOL = "bool";
    
    public static final String TYPE_DATE = "date";
    
    /**
     * The unique identifier for this LineListing Element / Entry Element
     */
    private int id;

    /**
     * Name of Element / Entry Element. Required and unique.
     */
    private String name;
    
    /**
     * Short Name of Element / Entry Element. Required and unique.
     */
    private String shortName;
    
    
    /**
     * Description of the LineListing Element.
     */
    private String description;
    
    /**
     * Data Type of the Line List Element - Used to determine what type of data
     * is required to be collected: Possible data types could be Text, Number, Yes-No
     * Date, Time, 
     */    
    private String dataType;
    
    /**
     * Data Collection format of the Line List Element at Data Entry Screen - Possible 
     * presentation types could be: Combo box, text field, radio button
     */
    private String presentationType;
    
    /**
     * Options of the presentation type - Used for displaying the options in combo
     * box presenation
     */
    private Collection<LineListOption> lineListElementOptions = new HashSet<LineListOption>();
    
    /**
     * Indicating position in the custom sort order.
     */
    
    private Integer sortOrder;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public LineListElement()
    {
    }
    
    public LineListElement(String name)
    {
        this.name = name;
    }
    
    public LineListElement(String name, String dataType, String presentationType)
    {
        this.name = name;
        this.dataType = dataType;
        this.presentationType = presentationType;
    }
    
    public LineListElement (String name, String shortName, String dataType, String presentationType)
    {
        this.name = name;
        this.shortName = shortName;
        this.dataType = dataType;
        this.presentationType = presentationType;
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

        if ( !(o instanceof LineListElement) )
        {
            return false;
        }

        final LineListElement other = (LineListElement) o;

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

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType( String dataType )
    {
        this.dataType = dataType;
    }

    public String getPresentationType()
    {
        return presentationType;
    }

    public void setPresentationType( String presentationType )
    {
        this.presentationType = presentationType;
    }

    public Collection<LineListOption> getLineListElementOptions()
    {
        return lineListElementOptions;
    }

    public void setLineListElementOptions( Collection<LineListOption> lineListElementOptions )
    {
        this.lineListElementOptions = lineListElementOptions;
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
        throw new UnsupportedOperationException( "Cannot set alternativename on LineListElement: " + alternativeName );
    }

}
