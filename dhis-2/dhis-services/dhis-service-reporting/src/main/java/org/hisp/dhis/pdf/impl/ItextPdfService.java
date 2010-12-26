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
import java.util.List;

import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pdf.PdfService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.DateUtils;
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
    // PdfService implementation
    // -------------------------------------------------------------------------

    public void writeDataSetCompletenessResult( Collection<DataSetCompletenessResult> results, OutputStream out,
        I18n i18n, OrganisationUnit unit, DataSet dataSet )
    {
        initFont();
        Document document = openDocument( out );

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

    public void writeValidationResult( List<ValidationResult> results, OutputStream out, I18n i18n, I18nFormat format )
    {
        initFont();
        Document document = openDocument( out );

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
                table
                    .addCell( getTextCell( validationResult.getValidationRule().getLeftSide().getDescription(), TEXT ) );
                table.addCell( getTextCell( String.valueOf( validationResult.getLeftsideValue() ) ) );
                table.addCell( getTextCell( i18n.getString( validationResult.getValidationRule().getOperator() ), 1,
                    ALIGN_CENTER ) );
                table.addCell( getTextCell( String.valueOf( validationResult.getRightsideValue() ) ) );
                table
                    .addCell( getTextCell( validationResult.getValidationRule().getRightSide().getDescription(), TEXT ) );
            }
        }

        addTableToDocument( document, table );

        closeDocument( document );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void initFont()
    {
        bf = getTrueTypeFontByDimension( BaseFont.IDENTITY_H );

        TEXT = new Font( bf, 9, Font.NORMAL );
        ITALIC = new Font( bf, 9, Font.ITALIC );
        HEADER3 = new Font( bf, 12, Font.BOLD );
    }

}
