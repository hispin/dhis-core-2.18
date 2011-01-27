package org.hisp.dhis.result;

/**
 * This file is modified and included in DHIS because the original version
 * wont allow dynamic setting of the chart height and width.
 */

/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 *
 *
 * Created on Sep 20, 2003
 *
 */

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

import org.hisp.dhis.util.ContextUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * A custom Result type for chart data. Built on top of <a
 * href="http://www.jfree.org/jfreechart/" target="_blank">JFreeChart</a>. When
 * executed this Result will write the given chart as a PNG to the servlet
 * output stream.
 * 
 * @author Bernard Choi
 * @author Lars Helge Overland
 */
public class ChartResult
    implements Result
{
    private static final String DEFAULT_FILENAME = "chart.png";
    
    private JFreeChart chart = null;

    private Integer height;

    private Integer width;
    
    private String filename;
    
    /**
     * Sets the JFreeChart to use.
     * 
     * @param chart a JFreeChart object.
     */
    public void setChart( JFreeChart chart )
    {
        this.chart = chart;
    }

    /**
     * Sets the chart height.
     * 
     * @param height the height of the chart in pixels.
     */
    public void setHeight( Integer height )
    {
        this.height = height;
    }

    /**
     * Sets the chart width.
     * 
     * @param width the width of the chart in pixels.
     */
    public void setWidth( Integer width )
    {
        this.width = width;
    }
    
    /**
     * Sets the filename.
     * 
     * @param filename the filename.
     */
    public void setFilename( String filename )
    {
        this.filename = filename;
    }

    /**
     * Executes the result. Writes the given chart as a PNG to the servlet
     * output stream.
     * 
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when creating or writing the chart
     *         to the servlet output stream.
     */
    public void execute( ActionInvocation invocation )
        throws Exception
    {
        JFreeChart stackChart = (JFreeChart) invocation.getStack().findValue( "chart" );
        
        chart = stackChart != null ? stackChart : chart;
        
        Integer stackHeight = (Integer) invocation.getStack().findValue( "height" );
        
        height = stackHeight != null && stackHeight > 0 ? stackHeight : height;
        
        Integer stackWidth = (Integer) invocation.getStack().findValue( "width" );
        
        width = stackWidth != null && stackWidth > 0 ? stackWidth : width;
        
        String stackFilename = (String) invocation.getStack().findValue( "filename" );
        
        filename = StringUtils.defaultIfEmpty( stackFilename, DEFAULT_FILENAME );
        
        if ( chart == null )
        {
            throw new NullPointerException( "No chart found" );
        }
        
        if ( height == null )
        {
            throw new IllegalArgumentException( "Height not set" );
        }

        if ( width == null )
        {
            throw new IllegalArgumentException( "Width not set" );
        }
        
        HttpServletResponse response = ServletActionContext.getResponse();
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PNG, true, filename, false );
        
        OutputStream os = response.getOutputStream();
        ChartUtilities.writeChartAsPNG( os, chart, width, height );
        os.flush();
    }
}
