package org.hisp.dhis.pdf.impl;

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

import static org.hisp.dhis.system.util.PDFUtils.addTableToDocument;
import static org.hisp.dhis.system.util.PDFUtils.getCell;
import static org.hisp.dhis.system.util.PDFUtils.getHeader3Cell;
import static org.hisp.dhis.system.util.PDFUtils.getItalicCell;
import static org.hisp.dhis.system.util.PDFUtils.getPdfPTable;
import static org.hisp.dhis.system.util.PDFUtils.getTextCell;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pdf.PdfService;
import org.hisp.dhis.system.util.PDFUtils;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPTable;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ItextPdfService
    implements PdfService
{
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

    public void writeAllDataElements( OutputStream outputStream )
    {
        Document document = PDFUtils.openDocument( outputStream );
        
        for ( DataElement element : dataElementService.getAllDataElements() )        
        {
            PdfPTable table = getPdfPTable( true, 0.40f, 0.60f );
            
            table.addCell( getHeader3Cell( element.getName(), 2 ) );
            
            table.addCell( getCell( 2, 15 ) );
            
            table.addCell( getItalicCell( "Short name", 1 ) );
            table.addCell( getTextCell( element.getShortName() ) );
            
            table.addCell( getItalicCell( "Alternative name", 1 ) );
            table.addCell( getTextCell( element.getAlternativeName() ) );
            
            table.addCell( getItalicCell( "Code", 1 ) );
            table.addCell( getTextCell( element.getCode() ) );
            
            table.addCell( getItalicCell( "Description", 1 ) );
            table.addCell( getTextCell( element.getDescription() ) );
            
            table.addCell( getItalicCell( "Active", 1 ) );
            table.addCell( getTextCell( getBoolean().get( element.isActive() ) ) );
            
            table.addCell( getItalicCell( "Type", 1 ) );
            table.addCell( getTextCell( getType().get( element.getType() ) ) );            
            
            table.addCell( getItalicCell( "Aggregaton operator", 1 ) );
            table.addCell( getTextCell( getAggregationOperator().get( element.getAggregationOperator() ) ) );

            table.addCell( getCell( 2, 30 ) );
            
            addTableToDocument( document, table );
        }
        
        PDFUtils.closeDocument( document );
    }

    public void writeAllIndicators( OutputStream outputStream )
    {
        Document document = PDFUtils.openDocument( outputStream );
        
        for ( Indicator indicator : indicatorService.getAllIndicators() )
        {
            PdfPTable table = getPdfPTable( true, 0.40f, 0.60f );
    
            table.addCell( getHeader3Cell( indicator.getName(), 2 ) );

            table.addCell( getCell( 2, 15 ) );
            
            table.addCell( getItalicCell( "Short name", 1 ) );
            table.addCell( getTextCell( indicator.getShortName() ) );

            table.addCell( getItalicCell( "Alternative name", 1 ) );
            table.addCell( getTextCell( indicator.getAlternativeName() ) );

            table.addCell( getItalicCell( "Code", 1 ) );
            table.addCell( getTextCell( indicator.getCode() ) );

            table.addCell( getItalicCell( "Description", 1 ) );
            table.addCell( getTextCell( indicator.getDescription() ) );

            table.addCell( getItalicCell( "Annualized", 1 ) );
            table.addCell( getTextCell( getBoolean().get( indicator.getAnnualized() ) ) );
            
            table.addCell( getItalicCell( "Indicator type", 1 ) );
            table.addCell( getTextCell( indicator.getIndicatorType().getName() ) );

            table.addCell( getItalicCell( "Numerator description", 1 ) );
            table.addCell( getTextCell( indicator.getNumeratorDescription() ) );

            table.addCell( getItalicCell( "Denominator description", 1 ) );
            table.addCell( getTextCell( indicator.getDenominatorDescription() ) );

            table.addCell( getCell( 2, 30 ) );
            
            addTableToDocument( document, table );
        }
        
        PDFUtils.closeDocument( document );
    }
    
    public void writeAllOrganisationUnits( OutputStream outputStream )
    {
        Document document = PDFUtils.openDocument( outputStream );
        
        for ( OrganisationUnit unit : organisationUnitService.getAllOrganisationUnits() )
        {
            PdfPTable table = getPdfPTable( true, 0.40f, 0.60f );
            
            table.addCell( getHeader3Cell( unit.getName(), 2 ) );
            
            table.addCell( getCell( 2, 15 ) );            

            table.addCell( getItalicCell( "Short name", 1 ) );
            table.addCell( getTextCell( unit.getShortName() ) );

            table.addCell( getItalicCell( "Code", 1 ) );
            table.addCell( getTextCell( unit.getCode() ) );

            table.addCell( getItalicCell( "Opening date", 1 ) );
            table.addCell( getTextCell( unit.getOpeningDate() != null ? unit.getOpeningDate().toString() : "" ) );

            table.addCell( getItalicCell( "Closed date", 1 ) );
            table.addCell( getTextCell( unit.getClosedDate() != null ? unit.getClosedDate().toString() : "" ) );

            table.addCell( getItalicCell( "Active", 1 ) );
            table.addCell( getTextCell( getBoolean().get( unit.isActive() ) ) );

            table.addCell( getItalicCell( "Comment", 1 ) );
            table.addCell( getTextCell( unit.getComment() ) );

            table.addCell( getCell( 2, 30 ) );
            
            addTableToDocument( document, table );     
        }
        
        PDFUtils.closeDocument( document );
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
        map.put( DataElement.TYPE_STRING, "Text" );
        map.put( DataElement.TYPE_INT, "Number" );
        map.put( DataElement.TYPE_BOOL, "Yes/No" );
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
}
