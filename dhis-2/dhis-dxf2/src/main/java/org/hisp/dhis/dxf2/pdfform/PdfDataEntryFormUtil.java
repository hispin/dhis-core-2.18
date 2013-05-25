package org.hisp.dhis.dxf2.pdfform;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.AcroFields.Item;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfReader;
import org.hisp.dhis.dxf2.datavalueset.DataValueSet;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


public class PdfDataEntryFormUtil
{

    // public Static Values
    public static final int DATATYPE_DATASET = 0;
    public static final int DATATYPE_PROGRAMSTAGE = 1;

    public static final float UNITSIZE_DEFAULT = 10;

    // Label Names
    public static final String LABELCODE_TEXTFIELD = "TXFD_";

    public static final String LABELCODE_BUTTON = "BTNFD_";

    public static final String LABELCODE_ORGID = LABELCODE_TEXTFIELD + "OrgID";

    public static final String LABELCODE_PERIODID = LABELCODE_TEXTFIELD + "PeriodID";

    public static final String LABELCODE_BUTTON_SAVEAS = LABELCODE_BUTTON + "SaveAs";

    public static final String LABELCODE_DATADATETEXTFIELD = "TXFDDT_";

    public static final String LABELCODE_DATAENTRYTEXTFIELD = "TXFDDV_";

    public static final String LABELCODE_PROGRAMSTAGEIDTEXTBOX = "TXPSTGID_";


    // Cell Related

    public final static float CELL_MIN_HEIGHT_DEFAULT = 13;

    public final static float CONTENT_HEIGHT_DEFAULT = 11;

    public final static int CELL_COLUMN_TYPE_LABEL = 0;

    public final static int CELL_COLUMN_TYPE_ENTRYFIELD = 1;


    // private static values
    private static final String DATAVALUE_IMPORT_STOREBY = "admin";

    private static final String DATAVALUE_IMPORT_COMMENT = "Imported by PDF Data Entry Form";

    private static final String DATAVALUE_IMPORT_TIMESTAMP_DATEFORMAT = "yyyy-MM-dd";

    private static final String FOOTERTEXT_DEFAULT = "PDF Template generated from DHIS %s on %s";

    private static final String DATEFORMAT_FOOTER_DEFAULT = "MMMM dd, yyyy";


    // -------------------------------------------------------------------------
    // METHODS
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // --- Document Setting Related [START]

    public static void setFooterOnDocument( Document document, String footerText, Font font )
    {
        boolean isNumbered = true;

        HeaderFooter footer = new HeaderFooter( new Phrase( footerText, font ), isNumbered );
        footer.setBorder( Rectangle.NO_BORDER );
        footer.setAlignment( Element.ALIGN_RIGHT );
        document.setFooter( footer );

    }

    // Set DefaultFooter
    public static void setDefaultFooterOnDocument( Document document, String serverName, Font font )
    {
        // Set Footer
        String strFooterText = String.format( FOOTERTEXT_DEFAULT, serverName, (new SimpleDateFormat(
            DATEFORMAT_FOOTER_DEFAULT )).format( new Date() ) );

        setFooterOnDocument( document, strFooterText, font );

    }

    public static Rectangle getDefaultPageSize( int typeId )
    {

        if ( typeId == PdfDataEntryFormUtil.DATATYPE_PROGRAMSTAGE )
        {
            return new Rectangle( PageSize.A4.getLeft(),
                PageSize.A4.getBottom(), PageSize.A4.getTop(), PageSize.A4.getRight() );
        }
        else
        {
            return PageSize.A4;
        }

    }

    // --- Document Setting Related [END]
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // --- PdfPCell Related [START]

    public static PdfPCell getPdfPCell( float minHeight )
    {
        return getPdfPCell( minHeight, CELL_COLUMN_TYPE_LABEL );
    }

    public static PdfPCell getPdfPCell( float minHeight, int cellContentType )
    {
        PdfPCell cell = new PdfPCell();
        cell.setMinimumHeight( minHeight );
        cell.setBorder( Rectangle.NO_BORDER );

        if ( cellContentType == CELL_COLUMN_TYPE_LABEL )
        {
            cell.setHorizontalAlignment( Element.ALIGN_RIGHT );
            cell.setVerticalAlignment( Element.ALIGN_TOP );
        }
        else if ( cellContentType == CELL_COLUMN_TYPE_ENTRYFIELD )
        {
            cell.setHorizontalAlignment( Element.ALIGN_CENTER );
            cell.setVerticalAlignment( Element.ALIGN_MIDDLE );
        }

        return cell;
    }


    // --- PdfPCell Related [END]
    // -------------------------------------------------------------------------


    // Retreive DataValue Informations from PDF inputStream.
    public static DataValueSet getDataValueSet( InputStream in )
        throws RuntimeException
    {
        PdfReader reader = null;

        DataValueSet dataValueSet = new DataValueSet();

        List<org.hisp.dhis.dxf2.datavalue.DataValue> dataValueList = new ArrayList<org.hisp.dhis.dxf2.datavalue.DataValue>();

        try
        {

            reader = new PdfReader( in ); // new PdfReader(in, null);

            AcroFields form = reader.getAcroFields();

            if ( form != null )
            {

                // TODO: MOVE THESE STATIC NAME VALUES TO inside of service
                // class or PDFForm Class <-- should be in PDFForm Class.
                String strOrgUID = form.getField( PdfDataEntryFormUtil.LABELCODE_ORGID );
                String strPeriodID = form.getField( PdfDataEntryFormUtil.LABELCODE_PERIODID );

                Period period = PeriodType.createPeriodExternalId( strPeriodID );

                // Loop Through the Fields and get data.
                HashMap<String, AcroFields.Item> fields = form.getFields();
                Set<Entry<String, Item>> entrySet = fields.entrySet();

                Set<String> fldNames = form.getFields().keySet();

                for ( String fldName : fldNames )
                {

                    if ( fldName.startsWith( PdfDataEntryFormUtil.LABELCODE_DATAENTRYTEXTFIELD ) )
                    {

                        String[] strArrFldName = fldName.split( "_" );

                        // Create DataValues to be put in a DataValueSet
                        org.hisp.dhis.dxf2.datavalue.DataValue dataValue = new org.hisp.dhis.dxf2.datavalue.DataValue();

                        dataValue.setDataElement( strArrFldName[1] );
                        dataValue.setCategoryOptionCombo( strArrFldName[2] );
                        dataValue.setOrgUnit( strOrgUID );
                        dataValue.setPeriod( period.getIsoDate() );

                        dataValue.setValue( form.getField( fldName ) );

                        dataValue.setStoredBy( DATAVALUE_IMPORT_STOREBY );
                        dataValue.setComment( DATAVALUE_IMPORT_COMMENT );
                        dataValue.setFollowup( false );
                        dataValue.setTimestamp( new SimpleDateFormat( DATAVALUE_IMPORT_TIMESTAMP_DATEFORMAT )
                            .format( new Date() ) );

                        dataValueList.add( dataValue );

                    }
                }

                dataValueSet.setDataValues( dataValueList );

            }
            else
            {
                throw new RuntimeException( "Could not generate PDF AcroFields form from the file." );
            }

        }
        catch ( Exception e )
        {
            throw new RuntimeException( e.getMessage() );
        }
        finally
        {
            reader.close();
        }

        return dataValueSet;
    }


    // -----------------------------------------------------------------------------
    // --- For Import - ProgramStage [START]


    // --- For Import - ProgramStage [END]
    // -----------------------------------------------------------------------------


}
