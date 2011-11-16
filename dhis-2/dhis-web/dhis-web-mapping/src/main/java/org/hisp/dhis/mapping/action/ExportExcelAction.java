package org.hisp.dhis.mapping.action;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.io.OutputStream;

import jxl.CellView;
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

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.mapping.export.SVGDocument;
import org.hisp.dhis.mapping.export.SVGUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.util.StreamActionSupport;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ExportExcelAction
    extends StreamActionSupport
{
    private static final Log log = LogFactory.getLog( ExportExcelAction.class );
    
    // -------------------------------------------------------------------------
    // Map position in excel
    // -------------------------------------------------------------------------

    int titlePositionCol = 1;

    int titlePositionRow = 1;

    int mapPositionCol = 1;

    int mapPositionCRow = 5;

    int legendPositionCol = 9;

    int legendPositionRow = 5;

    int orgunitPositionCol = 9;

    int orgunitPositionRow = 5;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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
    // Input
    // -------------------------------------------

    private String svg;

    public void setSvg( String svg )
    {
        this.svg = svg;
    }

    private String legends;

    public void setLegends( String legends )
    {
        this.legends = legends;
    }

    private String title;

    public void setTitle( String title )
    {
        this.title = title;
    }

    private String indicator;

    public void setIndicator( String indicator )
    {
        this.indicator = indicator;
    }

    private String period;

    public void setPeriod( String period )
    {
        this.period = period;
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

    private boolean includeLegends;

    public void setIncludeLegends( boolean includeLegends )
    {
        this.includeLegends = includeLegends;
    }

    // -------------------------------------------
    // Output
    // -------------------------------------------
    
    @Override
    protected String execute( HttpServletResponse response, OutputStream out )
        throws Exception
    {
        log.info( "Exporting workbook, width: " + width + ", height: " + height );
        
        // ---------------------------------------------------------------------
        // Write map image to byte array
        // ---------------------------------------------------------------------

        SVGDocument svgDocument = new SVGDocument();

        svgDocument.setTitle( this.title );
        svgDocument.setSvg( this.svg );
        svgDocument.setPeriod( period );
        svgDocument.setIndicator( indicator );        
        svgDocument.setLegends( this.legends );        
        svgDocument.setIncludeLegends( this.includeLegends );        

        ByteArrayOutputStream image = new ByteArrayOutputStream();
        
        SVGUtils.convertToPNG( svgDocument.getSVGForExcel(), image, width, height );

        // ---------------------------------------------------------------------
        // Write workbook
        // ---------------------------------------------------------------------

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( out );

        WritableSheet sheet = outputReportWorkbook.createSheet( indicator, 1 );

        // ---------------------------------------------------------------------
        // Write map image to workbook
        // ---------------------------------------------------------------------

        sheet.mergeCells( mapPositionCol, mapPositionCRow, mapPositionCol + 6, mapPositionCRow + 25 );
        
        WritableImage map = new WritableImage( mapPositionCol, mapPositionCRow, 7, 26, image.toByteArray() );

        sheet.addImage( map );

        WritableCellFormat map_format = new WritableCellFormat();
        map_format.setBorder( Border.ALL, BorderLineStyle.THIN );
        map_format.setAlignment( Alignment.CENTRE );

        sheet.addCell( new Label( mapPositionCol, mapPositionCRow, "", map_format ) );

        // ---------------------------------------------------------------------
        // Write title to workbook
        // ---------------------------------------------------------------------

        WritableCellFormat header = new WritableCellFormat();
        header.setBackground( Colour.GREY_25_PERCENT );
        header.setBorder( Border.ALL, BorderLineStyle.THIN );
        
        WritableCellFormat headerContent = new WritableCellFormat();
        headerContent.setBorder( Border.ALL, BorderLineStyle.THIN );
        
        sheet.mergeCells( titlePositionCol, titlePositionRow, titlePositionCol + 1, titlePositionRow );
        sheet.mergeCells( titlePositionCol + 2, titlePositionRow, titlePositionCol + 6, titlePositionRow );
        sheet.mergeCells( titlePositionCol, titlePositionRow + 1, titlePositionCol + 1, titlePositionRow + 1 );
        sheet.mergeCells( titlePositionCol + 2, titlePositionRow + 1, titlePositionCol + 6, titlePositionRow + 1 );
        sheet.mergeCells( titlePositionCol, titlePositionRow + 2, titlePositionCol + 1, titlePositionRow + 2 );
        sheet.mergeCells( titlePositionCol + 2, titlePositionRow + 2, titlePositionCol + 6, titlePositionRow + 2 );
        
        sheet.addCell( new Label( titlePositionCol, titlePositionRow, i18n.getString( "Title" ), header ) );
        sheet.addCell( new Label( titlePositionCol + 2, titlePositionRow, this.title, headerContent ) );
        sheet.addCell( new Label( titlePositionCol, titlePositionRow + 1, i18n.getString( "Indicator" ), header ) );
        sheet.addCell( new Label( titlePositionCol + 2, titlePositionRow + 1, indicator, headerContent ) );
        sheet.addCell( new Label( titlePositionCol, titlePositionRow + 2, i18n.getString( "Period" ), header ) );
        sheet.addCell( new Label( titlePositionCol + 2, titlePositionRow + 2, period, headerContent ) );

        // ---------------------------------------------------------------------
        // Write data values to workbook
        // ---------------------------------------------------------------------

        if ( includeValues )
        {
            WritableCellFormat datavalueHeader = new WritableCellFormat();
            datavalueHeader.setBorder( Border.ALL, BorderLineStyle.THIN );
            datavalueHeader.setAlignment( Alignment.CENTRE );
            datavalueHeader.setBackground( Colour.GREY_25_PERCENT );

            sheet.addCell( new Label( orgunitPositionCol, orgunitPositionRow, i18n.getString( "Name" ), datavalueHeader ) );

            sheet.addCell( new Label( orgunitPositionCol + 1, orgunitPositionRow, i18n.getString( "Value" ), datavalueHeader ) );

            WritableCellFormat valCellFormat = new WritableCellFormat();
            valCellFormat.setAlignment( Alignment.LEFT );
            valCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );

            int rowValue = orgunitPositionRow + 1;

            JSONObject datavalue;

            OrganisationUnit organisationUnit;

            JSONObject json = (JSONObject) JSONSerializer.toJSON( datavalues );

            JSONArray jsonDataValues = json.getJSONArray( "datavalues" );
            
            CellView cellView = new CellView();
            
            cellView.setSize( 7000 );

            for ( int index = 0; index < jsonDataValues.size(); index++ )
            {
                datavalue = jsonDataValues.getJSONObject( index );

                organisationUnit = organisationUnitService.getOrganisationUnit( datavalue.getInt( "organisation" ) );

                double value = datavalue.getDouble( "value" );

                sheet.addCell( new Label( orgunitPositionCol, rowValue, organisationUnit.getName(), valCellFormat ) );
                
                sheet.setColumnView( orgunitPositionCol, cellView ); 

                sheet.addCell( new Number( orgunitPositionCol + 1, rowValue, value, valCellFormat ) );

                rowValue++;
            }
        }

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        return SUCCESS;
    }
    
    @Override
    protected String getContentType()
    {
        return ContextUtils.CONTENT_TYPE_EXCEL;
    }

    @Override
    protected String getFilename()
    {
        return "DHIS_2_GIS.xls";
    }
}