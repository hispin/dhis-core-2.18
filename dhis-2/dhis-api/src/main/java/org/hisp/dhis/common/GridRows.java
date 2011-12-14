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
@XmlRootElement( name = "rows", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class GridRows
{
    private List<GridRow> rows = new ArrayList<GridRow>();

    @XmlElement
    @JsonProperty
    public List<GridRow> getRows()
    {
        return rows;
    }

    public void setRows( List<GridRow> rows )
    {
        this.rows = rows;
    }
}
