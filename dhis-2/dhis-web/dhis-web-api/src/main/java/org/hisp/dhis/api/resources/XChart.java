package org.hisp.dhis.api.resources;

import javax.xml.bind.annotation.XmlRootElement;

import org.hisp.dhis.chart.Chart;

@XmlRootElement( name = "chart" )
public class XChart
    extends XIdentifiableObject
{
    private Chart chart;

    public XChart()
    {

    }

    public XChart( Chart chart )
    {
        super( chart );
        this.chart = chart;
    }
}
