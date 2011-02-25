package org.hisp.dhis.importexport.dxf2.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType( XmlAccessType.FIELD )
public class Link
{

    @XmlAttribute( name = "id", required = true )
    private String id;

    @XmlAttribute
    private String href;

    @XmlAttribute
    private String name;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref( String href )
    {
        this.href = href;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

}
