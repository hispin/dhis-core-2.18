package org.hisp.dhis.api.resources;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Elements
{
    @XmlElement( name = "element" )
    private List<Element> elements = new ArrayList<Element>();

    public List<Element> getElements()
    {
        return elements;
    }

    public void setElements( List<Element> elements )
    {
        this.elements = elements;
    }

    public Element getElement( Integer uid )
    {
        for ( Element el : elements )
        {
            if ( el.getUid().equals( uid ) )
            {
                return el;
            }
        }

        return null;
    }
}
