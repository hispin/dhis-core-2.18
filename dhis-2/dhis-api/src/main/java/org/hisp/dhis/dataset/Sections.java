package org.hisp.dhis.dataset;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "sections" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Sections
{
    private List<Section> sections = new ArrayList<Section>();

    public Sections()
    {

    }

    @XmlElement( name = "section" )
    @JsonProperty( value = "sections" )
    public List<Section> getSections()
    {
        return sections;
    }

    public void setSections( List<Section> sections )
    {
        this.sections = sections;
    }
}
