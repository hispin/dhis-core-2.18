/**
 * 
 */
package org.hisp.dhis.coldchain.reports.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

/**
 * @author Samta Bajpai
 * 
 * @version DynamicJasperTemplate.java Jul 19, 2012 5:41:06 PM
 */
public class DynamicJasperTemplate
{

    protected JasperPrint jp;

    protected JasperReport jr;

    protected Map params = new HashMap();

    protected DynamicReport dr;

    public void buildReport()
        throws Exception
    {  

        FastReportBuilder frb = new FastReportBuilder();

        Font font = new Font( 10, "Arial", true );

        Style headerStyle = new Style();

        headerStyle.setFont( font );

        headerStyle.setHorizontalAlign( HorizontalAlign.LEFT );

        headerStyle.setVerticalAlign( VerticalAlign.MIDDLE );

        font = new Font( 8, "Arial", false );

        Style detailStyle = new Style();

        detailStyle.setFont( font );

        detailStyle.setHorizontalAlign( HorizontalAlign.LEFT );

        detailStyle.setVerticalAlign( VerticalAlign.MIDDLE );
        AbstractColumn column;
        for ( int i = 1; i <= 10; i++ )
        {
            frb.addColumn("Column" + i, "Column" + i, String.class.getName(), 50,true);          
        }
        frb.setColumnsPerPage(1, 10).setUseFullPageWidth(true).setColspan(1, 2, "Estimated");
        //frb.setUseFullPageWidth( true );

        // Pass the JasperReport Template to DynamicJasper

        frb.setTemplateFile( "C:/report1.jrxml" );

        DynamicReport dr = frb.build();

        List records = new ArrayList();

        for ( int i = 1; i < 10; i++ )
        {

            Map columns = new HashMap();

           for ( int j = 1; j <= 10; j++ )
            {

                // The HashMap Key must save with ColumnProperty Name

                columns.put( "Column" + j, "Record " + i + " Column " +j + " data." );

            }

            records.add( columns );

        }

        JRDataSource ds = new JRMapCollectionDataSource( records );

        jr = DynamicJasperHelper.generateJasperReport( dr, new ClassicLayoutManager(), params );

        if ( ds != null )
        {

            jp = JasperFillManager.fillReport( jr, params, ds );

        }
        else
        {

            jp = JasperFillManager.fillReport( jr, params );

        }

        JasperExportManager.exportReportToPdfFile( jp, "C:/report-out.pdf" );

    }

    protected JRDataSource getDataSource()
    {

        // Generate dummy data to show in the report.

        List records = new ArrayList();

        for ( int i = 1; i < 10; i++ )
        {

            Map columns = new HashMap();

            for ( int j = 1; j <= 10; j++ )
            {

                // The HashMap Key must save with ColumnProperty Name

                columns.put( "Column" + j, "Record " + i + " Column " + j + " data." );

            }

            records.add( columns );

        }

        JRDataSource ds = new JRMapCollectionDataSource( records );

        return ds;

    }

    public static void main( String[] args )
        throws Exception
    {

        DynamicJasperTemplate djt = new DynamicJasperTemplate();

        djt.buildReport();

    }

}
