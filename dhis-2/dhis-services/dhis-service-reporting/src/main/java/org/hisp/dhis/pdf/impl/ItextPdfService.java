package org.hisp.dhis.pdf.impl;

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

import static org.hisp.dhis.system.util.PDFUtils.ALIGN_CENTER;
import static org.hisp.dhis.system.util.PDFUtils.addTableToDocument;
import static org.hisp.dhis.system.util.PDFUtils.closeDocument;
import static org.hisp.dhis.system.util.PDFUtils.getCell;
import static org.hisp.dhis.system.util.PDFUtils.getHeader3Cell;
import static org.hisp.dhis.system.util.PDFUtils.getItalicCell;
import static org.hisp.dhis.system.util.PDFUtils.getPdfPTable;
import static org.hisp.dhis.system.util.PDFUtils.getTextCell;
import static org.hisp.dhis.system.util.PDFUtils.getTrueTypeFontByDimension;
import static org.hisp.dhis.system.util.PDFUtils.openDocument;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pdf.PdfService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.PDFUtils;
import org.hisp.dhis.validation.ValidationResult;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;

/**
 * @author Lars Helge Overland
 * @version $Id$
 * @modifier Dang Duy Hieu
 * @since 2010-05-20
 */
@Transactional
public class ItextPdfService
    implements PdfService
{
    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    private static BaseFont bf;

    private static Font TEXT;

    private static Font ITALIC;

    private static Font HEADER3;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // PdfService implementation
    // -------------------------------------------------------------------------

    public void writeAllDataElements( OutputStream outputStream, I18n i18n )
    {
        Document document = PDFUtils.openDocument( outputStream );
        initFont();
        for ( DataElement element : dataElementService.getAllDataElements() )
        {
            PdfPTable table = getPdfPTable( true, 0.40f, 0.60f );

            table.addCell( getHeader3Cell( element.getName(), 2, HEADER3 ) );

            table.addCell( getCell( 2, 15 ) );

            table.addCell( getItalicCell( i18n.getString( "short_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getShortName(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "alternative_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getAlternativeName(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "code" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getCode() ) );

            table.addCell( getItalicCell( i18n.getString( "description" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getDescription(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "active" ), 1, ITALIC ) );
            table.addCell( getTextCell( getBoolean().get( element.isActive() ), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "type" ), 1, ITALIC ) );
            table.addCell( getTextCell( getType().get( element.getType() ), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "aggregation_operator" ), 1, ITALIC ) );
            table.addCell( getTextCell( getAggregationOperator().get( element.getAggregationOperator() ) ) );

            table.addCell( getCell( 2, 30 ) );

            addTableToDocument( document, table );
        }

        PDFUtils.closeDocument( document );
    }

    public void writeAllIndicators( OutputStream outputStream, I18n i18n )
    {
        Document document = PDFUtils.openDocument( outputStream );
        initFont();
        for ( Indicator indicator : indicatorService.getAllIndicators() )
        {
            PdfPTable table = getPdfPTable( true, 0.40f, 0.60f );

            table.addCell( getHeader3Cell( indicator.getName(), 2, HEADER3 ) );

            table.addCell( getCell( 2, 15 ) );

            table.addCell( getItalicCell( i18n.getString( "short_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getShortName(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "alternative_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getAlternativeName(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "code" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getCode() ) );

            table.addCell( getItalicCell( i18n.getString( "description" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getDescription(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "annualized" ), 1, ITALIC ) );
            table.addCell( getTextCell( getBoolean().get( indicator.getAnnualized() ), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "indicator_type" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getIndicatorType().getName(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "numerator_description" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getNumeratorDescription(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "denominator_description" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getDenominatorDescription(), TEXT ) );

            table.addCell( getCell( 2, 30 ) );

            addTableToDocument( document, table );
        }

        PDFUtils.closeDocument( document );
    }

    public void writeAllOrganisationUnits( OutputStream outputStream, I18n i18n )
    {
        Document document = PDFUtils.openDocument( outputStream );
        initFont();
        for ( OrganisationUnit unit : organisationUnitService.getAllOrganisationUnits() )
        {
            PdfPTable table = getPdfPTable( true, 0.40f, 0.60f );

            table.addCell( getHeader3Cell( unit.getName(), 2, HEADER3 ) );

            table.addCell( getCell( 2, 15 ) );

            table.addCell( getItalicCell( i18n.getString( "short_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( unit.getShortName(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "code" ), 1, ITALIC ) );
            table.addCell( getTextCell( unit.getCode() ) );

            table.addCell( getItalicCell( i18n.getString( "opening_date" ), 1, ITALIC ) );
            table.addCell( getTextCell( unit.getOpeningDate() != null ? unit.getOpeningDate().toString() : "" ) );

            table.addCell( getItalicCell( i18n.getString( "closed_date" ), 1, ITALIC ) );
            table.addCell( getTextCell( unit.getClosedDate() != null ? unit.getClosedDate().toString() : "" ) );

            table.addCell( getItalicCell( i18n.getString( "active" ), 1, ITALIC ) );
            table.addCell( getTextCell( getBoolean().get( unit.isActive() ), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "comment" ), 1, ITALIC ) );
            table.addCell( getTextCell( unit.getComment(), TEXT ) );

            table.addCell( getCell( 2, 30 ) );

            addTableToDocument( document, table );
        }

        PDFUtils.closeDocument( document );
    }

    public void writeDataSetCompletenessResult( Collection<DataSetCompletenessResult> results, OutputStream out,
        I18n i18n, OrganisationUnit unit, DataSet dataSet )
    {
        Document document = openDocument( out );
        initFont();
        PdfPTable table = getPdfPTable( true, 0.501f, 0.10f, 0.10f, 0.10f, 0.10f, 0.10f );

        table.setHeaderRows( 1 );

        String dataSetName = dataSet != null ? " - " + dataSet.getName() : "";

        table.addCell( getHeader3Cell( i18n.getString( "data_completeness_report" ) + " - " + unit.getName()
            + dataSetName, 6, HEADER3 ) );

        table.addCell( getCell( 6, 8 ) );

        table.addCell( getTextCell( i18n.getString( "district_health_information_software" ) + " - "
            + DateUtils.getMediumDateString(), 6, TEXT ) );

        table.addCell( getCell( 6, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "name" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "actual" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "target" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "percent" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "on_time" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "percent" ), 1, ITALIC ) );

        table.addCell( getCell( 6, 8 ) );

        if ( results != null )
        {
            for ( DataSetCompletenessResult result : results )
            {
                table.addCell( getTextCell( result.getName(), TEXT ) );
                table.addCell( getTextCell( String.valueOf( result.getRegistrations() ) ) );
                table.addCell( getTextCell( String.valueOf( result.getSources() ) ) );
                table.addCell( getTextCell( String.valueOf( result.getPercentage() ) ) );
                table.addCell( getTextCell( String.valueOf( result.getRegistrationsOnTime() ) ) );
                table.addCell( getTextCell( String.valueOf( result.getPercentageOnTime() ) ) );
            }
        }

        addTableToDocument( document, table );

        closeDocument( document );
    }

    public void writeValidationResult( List<ValidationResult> results, OutputStream out, I18n i18n,
        I18nFormat format )
    {
        Document document = openDocument( out );
        initFont();
        PdfPTable table = getPdfPTable( true, 0.19f, 0.13f, 0.21f, 0.07f, 0.12f, 0.07f, 0.21f );

        table.setHeaderRows( 0 );

        table.addCell( getHeader3Cell( i18n.getString( "data_quality_report" ), 7, HEADER3 ) );

        table.addCell( getCell( 7, 8 ) );

        table.addCell( getTextCell( i18n.getString( "district_health_information_software" ) + " - "
            + format.parseDate( DateUtils.getMediumDateString() ), 7, TEXT ) );

        table.addCell( getCell( 7, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "source" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "period" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "left_side_description" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "value" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "operator" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "value" ), 1, ITALIC ) );
        table.addCell( getItalicCell( i18n.getString( "right_side_description" ), 1, ITALIC ) );

        table.addCell( getCell( 7, 8 ) );

        if ( results != null )
        {
            for ( ValidationResult validationResult : results )
            {
                OrganisationUnit unit = (OrganisationUnit) validationResult.getSource();

                Period period = validationResult.getPeriod();

                table.addCell( getTextCell( unit.getName(), TEXT ) );
                table.addCell( getTextCell( format.formatPeriod( period ), TEXT ) );
                table.addCell( getTextCell( validationResult.getValidationRule().getLeftSide().getDescription(),
                    TEXT ) );
                table.addCell( getTextCell( String.valueOf( validationResult.getLeftsideValue() ) ) );
                table.addCell( getTextCell( i18n.getString( validationResult.getValidationRule().getOperator() ),
                    1, ALIGN_CENTER ) );
                table.addCell( getTextCell( String.valueOf( validationResult.getRightsideValue() ) ) );
                table.addCell( getTextCell( validationResult.getValidationRule().getRightSide().getDescription(),
                    TEXT ) );
            }
        }

        addTableToDocument( document, table );

        closeDocument( document );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Map<Boolean, String> getBoolean()
    {
        Map<Boolean, String> map = new HashMap<Boolean, String>();
        map.put( true, "Yes" );
        map.put( false, "No" );
        return map;
    }

    private Map<String, String> getType()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.VALUE_TYPE_STRING, "Text" );
        map.put( DataElement.VALUE_TYPE_INT, "Number" );
        map.put( DataElement.VALUE_TYPE_BOOL, "Yes/No" );
        return map;
    }

    private Map<String, String> getAggregationOperator()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.AGGREGATION_OPERATOR_SUM, "Sum" );
        map.put( DataElement.AGGREGATION_OPERATOR_AVERAGE, "Average" );
        map.put( DataElement.AGGREGATION_OPERATOR_COUNT, "Count" );
        return map;
    }

    private void initFont()
    {
        bf = getTrueTypeFontByDimension( BaseFont.IDENTITY_H );

        TEXT = new Font( bf, 9, Font.NORMAL );
        ITALIC = new Font( bf, 9, Font.ITALIC );
        HEADER3 = new Font( bf, 12, Font.BOLD );
    }
}
