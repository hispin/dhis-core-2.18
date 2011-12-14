package org.hisp.dhis.common;

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
@XmlRootElement( name = "row", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class GridRow
{
    private List<Object> row = new ArrayList<Object>();

    @XmlElement
    @JsonProperty
    public List<Object> getRow()
    {
        return row;
    }

    public void setRow( List<Object> row )
    {
        this.row = row;
    }
}
