package org.hisp.dhis.importexport.pdf.converter;

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

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.PDFConverter;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.PDFUtils;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;

/**
 * @author Lars Helge Overland
 * @version $Id: ExtendedDataElementConverter.java 4646 2008-02-26 14:54:29Z
 *          larshelg $
 * @modifier Dang Duy Hieu
 * @since 2010-05-19
 */
public class ExtendedDataElementConverter
    extends PDFUtils
    implements PDFConverter
{
    private DataElementService dataElementService;

    public ExtendedDataElementConverter( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // PDFConverter implementation
    // -------------------------------------------------------------------------

    public void write( Document document, ExportParams params )
    {
        I18n i18n = params.getI18n();
        I18nFormat format = params.getFormat();

        PDFUtils.printDataElementFrontPage( document, params.getDataElements(), i18n, format );

        Collection<DataElement> elements = dataElementService.getDataElements( params.getDataElements() );

        BaseFont bf = getTrueTypeFontByDimension( BaseFont.IDENTITY_H );
        Font TEXT = new Font( bf, 9, Font.NORMAL );
        Font ITALIC = new Font( bf, 9, Font.ITALIC );
        Font HEADER3 = new Font( bf, 12, Font.BOLD );
        Font HEADER4 = new Font( bf, 9, Font.BOLD );

        for ( DataElement element : elements )
        {
            PdfPTable table = getPdfPTable( true, 0.40f, 0.60f );

            table.addCell( getHeader3Cell( element.getName(), 2, HEADER3 ) );

            table.addCell( getCell( 2, 15 ) );

            // -------------------------------------------------------------------------
            // Identifying and definitional attributes
            // -------------------------------------------------------------------------

            table.addCell( getHeader4Cell( i18n.getString( "identifying_and_definitional_attributes" ), 2, HEADER4 ) );

            table.addCell( getCell( 2, 8 ) );

            table.addCell( getItalicCell( i18n.getString( "short_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getShortName(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "alternative_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getAlternativeName(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "code" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getCode(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "description" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getDescription(), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "active" ), 1, ITALIC ) );
            table.addCell( getTextCell( i18n.getString( String.valueOf( element.isActive() ) ), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "type" ), 1, ITALIC ) );
            table.addCell( getTextCell( i18n.getString( element.getType() ), TEXT ) );

            table.addCell( getItalicCell( i18n.getString( "aggregation_operator" ), 1, ITALIC ) );
            table.addCell( getTextCell( i18n.getString( element.getAggregationOperator() ) ) );

            if ( element.getExtended() != null )
            {
                table.addCell( getItalicCell( i18n.getString( "mnemonic" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getMnemonic(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "version" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getVersion(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "context" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getContext(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "synonyms" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getSynonyms(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "hononyms" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getHononyms(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "keywords" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getKeywords(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "status" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getStatus(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "status_date" ), 1, ITALIC ) );
                table.addCell( getTextCell( DateUtils.getMediumDateString( element.getExtended().getStatusDate() ) ) );

                table.addCell( getCell( 2, 15 ) );

                // -------------------------------------------------------------------------
                // Relational and representational attributes
                // -------------------------------------------------------------------------

                table.addCell( getHeader4Cell( i18n.getString( "relational_and_representational_attributes" ), 2,
                    HEADER4 ) );

                table.addCell( getCell( 2, 8 ) );

                table.addCell( getItalicCell( i18n.getString( "data_type" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getDataType(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "representational_form" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getRepresentationalForm(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "representational_layout" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getRepresentationalLayout(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "minimum_size" ), 1, ITALIC ) );
                table.addCell( getTextCell( String.valueOf( element.getExtended().getMinimumSize() ) ) );

                table.addCell( getItalicCell( i18n.getString( "maximum_size" ), 1, ITALIC ) );
                table.addCell( getTextCell( String.valueOf( element.getExtended().getMaximumSize() ) ) );

                table.addCell( getItalicCell( i18n.getString( "data_domain" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getDataDomain(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "validation_rules" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getValidationRules(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "related_data_references" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getRelatedDataReferences(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "guide_for_use" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getGuideForUse(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "collection_methods" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getCollectionMethods(), TEXT ) );

                table.addCell( getCell( 2, 15 ) );

                // -------------------------------------------------------------------------
                // Administrative attributes
                // -------------------------------------------------------------------------

                table.addCell( getHeader4Cell( i18n.getString( "administrative_attributes" ), 2, HEADER4 ) );

                table.addCell( getCell( 2, 8 ) );

                table.addCell( getItalicCell( i18n.getString( "responsible_authority" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getResponsibleAuthority(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "update_rules" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getUpdateRules(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "access_authority" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getAccessAuthority(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "update_frequency" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getUpdateFrequency(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "location" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getLocation(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "reporting_methods" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getReportingMethods(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "version_status" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getVersionStatus(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "previous_version_references" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getPreviousVersionReferences(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "source_document" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getSourceDocument(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "source_organisation" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getSourceOrganisation(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "comment" ), 1, ITALIC ) );
                table.addCell( getTextCell( element.getExtended().getComment(), TEXT ) );

                table.addCell( getItalicCell( i18n.getString( "saved" ), 1, ITALIC ) );
                table.addCell( getTextCell( DateUtils.getMediumDateString( element.getExtended().getSaved() ) ) );

                table.addCell( getItalicCell( i18n.getString( "last_updated" ), 1, ITALIC ) );
                table.addCell( getTextCell( DateUtils.getMediumDateString( element.getExtended().getLastUpdated() ) ) );
            }

            table.addCell( getCell( 2, 30 ) );

            addTableToDocument( document, table );
        }
    }
}
