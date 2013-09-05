package org.hisp.dhis.coldchain.equipment;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseNameableObject;

//public class EquipmentTypeAttribute implements Serializable
public class EquipmentTypeAttribute extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static final String TYPE_DATE = "DATE";

    public static final String TYPE_STRING = "TEXT";

    public static final String TYPE_INT = "NUMBER";

    public static final String TYPE_BOOL = "YES/NO";

    public static final String TYPE_COMBO = "COMBO";

    public static final String TYPE_MODEL = "MODEL";
    
    private int id;
    
    private String name;
    
    private String description;

    private String valueType;
    
    private boolean mandatory;
    
    /*
     * True if this EquipmentTypeAttribute is for display in list
     */
    //private boolean display = false;
    
    private Integer noChars;

    private Set<EquipmentTypeAttributeOption> attributeOptions;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EquipmentTypeAttribute()
    {
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

        if ( !(o instanceof EquipmentTypeAttribute) )
        {
            return false;
        }

        final EquipmentTypeAttribute other = (EquipmentTypeAttribute) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
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

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    public Integer getNoChars()
    {
        return noChars;
    }

    public void setNoChars( Integer noChars )
    {
        this.noChars = noChars;
    }

    public Set<EquipmentTypeAttributeOption> getAttributeOptions()
    {
        return attributeOptions;
    }

    public void setAttributeOptions( Set<EquipmentTypeAttributeOption> attributeOptions )
    {
        this.attributeOptions = attributeOptions;
    }
    
    public void addAttributeOptions( EquipmentTypeAttributeOption option )
    {
        if ( attributeOptions == null )
            attributeOptions = new HashSet<EquipmentTypeAttributeOption>();
        attributeOptions.add( option );
    }
    
    /*
    public boolean isDisplay()
    {
        return display;
    }

    public void setDisplay( boolean display )
    {
        this.display = display;
    }
    */
}
