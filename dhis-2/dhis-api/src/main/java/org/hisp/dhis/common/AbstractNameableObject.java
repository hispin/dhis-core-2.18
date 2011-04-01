package org.hisp.dhis.common;

public class AbstractNameableObject
    extends AbstractIdentifiableObject implements NameableObject
{

    /**
     * An alternative name of this Object. Optional but unique.
     */
    protected String alternativeName;

    /**
     * An short name representing this Object. Optional but unique.
     */
    protected String shortName;

    /**
     * An code representing this Object. Optional but unique.
     */
    protected String code;

    /**
     * Description of this Object.
     */
    protected String description;

    public AbstractNameableObject()
    {
    }

    public AbstractNameableObject( int id, String uuid, String name, String alternativeName, String shortName,
        String code, String description )
    {
        super( id, uuid, name );
        this.alternativeName = alternativeName;
        this.shortName = shortName;
        this.code = code;
        this.description = description;
    }

    public String getAlternativeName()
    {
        return alternativeName;
    }

    public void setAlternativeName( String alternativeName )
    {
        this.alternativeName = alternativeName;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

}
