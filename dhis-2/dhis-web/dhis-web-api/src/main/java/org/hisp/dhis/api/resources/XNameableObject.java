package org.hisp.dhis.api.resources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hisp.dhis.common.NameableObject;

@XmlRootElement
public class XNameableObject
    extends XIdentifiableObject
{
    private NameableObject nameableObject;

    public XNameableObject()
    {

    }

    public XNameableObject( NameableObject nameableObject )
    {
        super( nameableObject );
        this.nameableObject = nameableObject;
    }

    @XmlElement
    public String getAlternativeName()
    {
        return nameableObject.getAlternativeName();
    }

    @XmlElement
    public String getShortName()
    {
        return nameableObject.getShortName();
    }

    @XmlElement
    public String getDescription()
    {
        return nameableObject.getDescription();
    }

}
