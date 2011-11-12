package org.hisp.dhis.api.resources;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "charts" )
public class XCharts
{
    private List<XChart> charts = new ArrayList<XChart>();

    public XCharts()
    {

    }

    @XmlElement( name = "chart" )
    public List<XChart> getCharts()
    {
        return charts;
    }
}
