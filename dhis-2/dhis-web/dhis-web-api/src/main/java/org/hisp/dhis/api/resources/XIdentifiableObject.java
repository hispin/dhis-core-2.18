package org.hisp.dhis.api.resources;

import org.hisp.dhis.common.IdentifiableObject;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class XIdentifiableObject
{
    private IdentifiableObject identifiableObject;

    private String href;

    public XIdentifiableObject()
    {

    }

    public XIdentifiableObject( IdentifiableObject identifiableObject )
    {
        this.identifiableObject = identifiableObject;
    }

    @XmlAttribute
    public int getId()
    {
        return identifiableObject.getId();
    }

    @XmlAttribute
    public String getUuid()
    {
        return identifiableObject.getUuid();
    }

    @XmlAttribute
    public String getUid()
    {
        return identifiableObject.getUid();
    }

    @XmlAttribute
    public String getName()
    {
        return identifiableObject.getName();
    }

    @XmlAttribute
    public String getCode()
    {
        return identifiableObject.getCode();
    }

    @XmlAttribute
    public Date getLastUpdated()
    {
        return identifiableObject.getLastUpdated();
    }

    @XmlAttribute
    public String getHref()
    {
        return href;
    }

    public void setHref( String href )
    {
        this.href = href;
    }
}
