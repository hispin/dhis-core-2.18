package org.hisp.dhis.api.resources;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Element
{
    @XmlAttribute
    private Integer uid;

    @XmlAttribute
    private String name;

    public Element()
    {
    }

    public Element( String name )
    {
        this();
        this.name = name;
    }

    public Element( Integer uid, String name )
    {
        this.uid = uid;
        this.name = name;
    }

    public Integer getUid()
    {
        return uid;
    }

    public void setUid( Integer uid )
    {
        this.uid = uid;
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
