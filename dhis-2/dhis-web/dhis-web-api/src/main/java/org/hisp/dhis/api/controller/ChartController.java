package org.hisp.dhis.api.controller;

import org.hisp.dhis.api.listener.IdentifiableObjectListener;
import org.hisp.dhis.api.resources.XChart;
import org.hisp.dhis.api.resources.XCharts;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Controller
@RequestMapping( value = "/charts" )
public class ChartController
{
    @Autowired
    private ChartService chartService;

    @RequestMapping( method = RequestMethod.GET )
    public XCharts getCharts( HttpServletRequest request )
    {
        XCharts charts = new XCharts();

        Collection<Chart> allCharts = chartService.getAllCharts();

        for ( Chart chart : allCharts )
        {
            charts.getCharts().add( new XChart( chart ) );
        }

        new IdentifiableObjectListener( request ).beforeMarshal( charts );

        return charts;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public XChart getChart( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        XChart chart = new XChart( chartService.getChart( uid ) );

        new IdentifiableObjectListener( request ).beforeMarshal( chart );

        return chart;
    }

    @RequestMapping( method = RequestMethod.POST )
    @ResponseBody
    public void postChart( XChart chart )
    {

    }

    /*
    @RequestMapping( method = RequestMethod.POST )
    public void postChart( InputStream inputStream ) throws IOException
    {
        StringWriter writer = new StringWriter();
        IOUtils.copy( inputStream, writer, "UTF-8" );
        System.err.println( writer.toString() );
    }
    */
}
