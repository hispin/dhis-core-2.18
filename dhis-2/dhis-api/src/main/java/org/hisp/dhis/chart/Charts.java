package org.hisp.dhis.chart;

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
@XmlRootElement( name = "charts" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Charts
{
    private List<Chart> charts = new ArrayList<Chart>();

    public Charts()
    {

    }

    @XmlElement( name = "chart" )
    @JsonProperty( value = "charts" )
    public List<Chart> getCharts()
    {
        return charts;
    }

    public void setCharts( List<Chart> charts )
    {
        this.charts = charts;
    }
}
