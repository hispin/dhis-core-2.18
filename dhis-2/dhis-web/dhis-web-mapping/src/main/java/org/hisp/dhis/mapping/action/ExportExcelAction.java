package org.hisp.dhis.mapping.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.File;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.mapping.export.SVGDocument;
import org.hisp.dhis.mapping.export.SVGUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.StreamUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

public class ExportExcelAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Map position in excel
    // -------------------------------------------------------------------------

    int titlePositionCol = 1;

    int titlePositionRow = 1;

    int mapPositionCol = 1;

    int mapPositionCRow = 5;

    int legendPositionCol = 9;

    int legendPositionRow = 5;

    int orgunitPositionCol = 13;

    int orgunitPositionRow = 5;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    protected I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------
    // Output & Input
    // -------------------------------------------

    private String svg;

    public void setSvg( String svg )
    {
        this.svg = svg;
    }

    private String title;

    public void setTitle( String title )
    {
        this.title = title;
    }

    private Integer indicator;

    public void setIndicator( Integer indicator )
    {
        this.indicator = indicator;
    }

    private Integer period;

    public void setPeriod( Integer period )
    {
        this.period = period;
    }

    private String outputFile;

    public String getOutputFile()
    {
        return outputFile;
    }

    private Integer width;

    public void setWidth( Integer width )
    {
        this.width = width;
    }

    private Integer height;

    public void setHeight( Integer height )
    {
        this.height = height;
    }

    private String datavalues;

    public void setDatavalues( String datavalues )
    {
        this.datavalues = datavalues;
    }

    private boolean includeValues;

    public void setIncludeValues( boolean includeValues )
    {
        this.includeValues = includeValues;
    }

    private boolean includeLegend;

    public void setIncludeLegend( boolean includeLegend )
    {
        this.includeLegend = includeLegend;
    }

    @Override
    public String execute()
        throws Exception
    {

        Period p = periodService.getPeriod( period );

        p.setName( format.formatPeriod( p ) );

        Indicator i = indicatorService.getIndicator( indicator );

        /* TODO write map to file */

        SVGDocument svgDocument = new SVGDocument();

        svgDocument.setTitle( this.title );

        svgDocument.setSvg( this.svg );

        svgDocument.setPeriod( p );

        svgDocument.setIndicator( i );

        int random = (int) (Math.random() * 1000);

        File temporaryDir = locationManager.getFileForWriting( MappingService.MAP_TEMPL_DIR );

        File svgTemporary = new File( temporaryDir, "svg_" + random + ".svg" );

        StreamUtils.writeContent( svgTemporary, svgDocument.getSVGForExcel() );

        File image = new File( temporaryDir, "svg_" + random + ".png" );

        SVGUtils.convertSVG2PNG( svgTemporary, image, width, height );

        /* TODO write excel to file */

        File output = new File( temporaryDir, "excel_" + random + ".xls" );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( output );

        WritableSheet sheet = outputReportWorkbook.createSheet( i.getName(), 1 );

        /* TODO add map to excel */

        sheet.mergeCells( mapPositionCol, mapPositionCRow, mapPositionCol + 6, mapPositionCRow + 25 );

        WritableImage map = new WritableImage( mapPositionCol, mapPositionCRow, 7, 26, image );

        sheet.addImage( map );

        WritableCellFormat map_format = new WritableCellFormat();
        map_format.setBorder( Border.ALL, BorderLineStyle.THIN );
        map_format.setAlignment( Alignment.CENTRE );

        sheet.addCell( new Label( mapPositionCol, mapPositionCRow, "", map_format ) );

        /* TODO add title */

        WritableCellFormat header = new WritableCellFormat();
        header.setBackground( Colour.ICE_BLUE );
        header.setBorder( Border.ALL, BorderLineStyle.THIN );

        sheet.mergeCells( titlePositionCol, titlePositionRow, titlePositionCol + 1, titlePositionRow );
        sheet.mergeCells( titlePositionCol + 2, titlePositionRow, titlePositionCol + 6, titlePositionRow );
        sheet.mergeCells( titlePositionCol, titlePositionRow + 1, titlePositionCol + 1, titlePositionRow + 1 );
        sheet.mergeCells( titlePositionCol + 2, titlePositionRow + 1, titlePositionCol + 6, titlePositionRow + 1 );
        sheet.mergeCells( titlePositionCol, titlePositionRow + 2, titlePositionCol + 1, titlePositionRow + 2 );
        sheet.mergeCells( titlePositionCol + 2, titlePositionRow + 2, titlePositionCol + 6, titlePositionRow + 2 );

        sheet.addCell( new Label( titlePositionCol, titlePositionRow, i18n.getString( "title" ), header ) );
        sheet.addCell( new Label( titlePositionCol + 2, titlePositionRow, this.title, header ) );
        sheet.addCell( new Label( titlePositionCol, titlePositionRow + 1, i18n.getString( "indicator" ), header ) );
        sheet.addCell( new Label( titlePositionCol + 2, titlePositionRow + 1, i.getName(), header ) );
        sheet.addCell( new Label( titlePositionCol, titlePositionRow + 2, i18n.getString( "period" ), header ) );
        sheet.addCell( new Label( titlePositionCol + 2, titlePositionRow + 2, p.getName(), header ) );

        if ( includeLegend )
        {

            WritableCellFormat legendHeader = new WritableCellFormat();
            legendHeader.setBorder( Border.ALL, BorderLineStyle.THIN );
            legendHeader.setAlignment( Alignment.CENTRE );
            legendHeader.setBackground( Colour.ICE_BLUE );

            sheet.mergeCells( legendPositionCol, legendPositionRow, legendPositionCol + 2, legendPositionRow );
            sheet.addCell( new Label( legendPositionCol, legendPositionRow, i18n.getString( "legend" ), legendHeader ) );
            sheet.addCell( new Label( legendPositionCol, legendPositionRow + 1, i18n.getString( "color" ), legendHeader ) );
            sheet.addCell( new Label( legendPositionCol + 1, legendPositionRow + 1, i18n.getString( "min" ), legendHeader ) );
            sheet.addCell( new Label( legendPositionCol + 2, legendPositionRow + 1, i18n.getString( "max" ), legendHeader ) );

        }

        if ( includeValues )
        {
            WritableCellFormat datavalueHeader = new WritableCellFormat();
            datavalueHeader.setBorder( Border.ALL, BorderLineStyle.THIN );
            datavalueHeader.setAlignment( Alignment.CENTRE );
            datavalueHeader.setBackground( Colour.ICE_BLUE );

            sheet.addCell( new Label( orgunitPositionCol, orgunitPositionRow, i18n.getString( "name" ), datavalueHeader ) );

            sheet.addCell( new Label( orgunitPositionCol + 1, orgunitPositionRow, i18n.getString( "value" ), datavalueHeader ) );
            
            
            WritableCellFormat valCellFormat = new WritableCellFormat();
            valCellFormat.setAlignment( Alignment.LEFT );
            valCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );      
            
            int rowValue = orgunitPositionRow + 1;            
            
            JSONObject datavalue;

            OrganisationUnit organisationUnit;         
           

            JSONObject json = (JSONObject) JSONSerializer.toJSON( datavalues );

            JSONArray jsonDataValues = json.getJSONArray( "datavalues" );

            for ( int index = 0; index < jsonDataValues.size(); index++ )
            {

                datavalue = jsonDataValues.getJSONObject( index );

                organisationUnit = organisationUnitService.getOrganisationUnit( datavalue.getInt( "organisation" ) );

                double value = datavalue.getDouble( "value" );   
                         
                sheet.addCell( new Label( orgunitPositionCol, rowValue, organisationUnit.getName(), valCellFormat ) );
                
                sheet.addCell( new Number( orgunitPositionCol + 1, rowValue, value , valCellFormat ) );     

                rowValue++;

            }

        }

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        outputFile = output.getAbsolutePath();

        return SUCCESS;
    }
}
