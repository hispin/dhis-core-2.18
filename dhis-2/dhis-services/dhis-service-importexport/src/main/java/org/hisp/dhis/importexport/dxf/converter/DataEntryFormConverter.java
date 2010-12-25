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

package org.hisp.dhis.importexport.dxf.converter;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.DataEntryFormImporter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;

/**
 * @author Chau Thu Tran
 * 
 * @version $ID: DataEntryFormConverter.java Dec 20, 2010 09:34:28 AM $
 */
public class DataEntryFormConverter
    extends DataEntryFormImporter
    implements XMLConverter
{
    public static final String COLLECTION_NAME = "dataEntryForms";

    public static final String ELEMENT_NAME = "dataEntryForm";

    private static final String FIELD_ID = "id";

    private static final String FIELD_NAME = "name";

    private static final String FIELD_HTMLCODE = "htmlCode";

    private DataElementService dataElementService;

    private DataElementCategoryService categoryService;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataEntryFormConverter( DataEntryFormService dataEntryFormService, DataElementService dataElementService,
        DataElementCategoryService categoryService )
    {
        this.dataEntryFormService = dataEntryFormService;
        this.dataElementService = dataElementService;
        this.categoryService = categoryService;
    }

    public DataEntryFormConverter( BatchHandler<DataEntryForm> batchHandler, ImportObjectService importObjectService,
        DataEntryFormService dataEntryFormService, DataElementService dataElementService,
        DataElementCategoryService categoryService )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataEntryFormService = dataEntryFormService;
        this.dataElementService = dataElementService;
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Override methods
    // -------------------------------------------------------------------------

    @Override
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataEntryForm> dataEntryForms = dataEntryFormService.getDataEntryForms( params.getDataEntryForms() );

        if ( dataEntryForms != null && dataEntryForms.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );

            for ( DataEntryForm dataEntryForm : dataEntryForms )
            {
                writer.openElement( ELEMENT_NAME );

                writer.writeElement( FIELD_ID, String.valueOf( dataEntryForm.getId() ) );
                writer.writeElement( FIELD_NAME, dataEntryForm.getName() );
                writer.writeCData( FIELD_HTMLCODE, dataEntryForm.getHtmlCode() );

                writer.closeElement();
            }

            writer.closeElement();
        }
    }

    @Override
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final DataEntryForm dataEntryForm = new DataEntryForm();

            reader.moveToStartElement( FIELD_ID );
            dataEntryForm.setId( Integer.parseInt( reader.getElementValue() ) );

            reader.moveToStartElement( FIELD_NAME );
            dataEntryForm.setName( reader.getElementValue() );
            reader.moveToStartElement( FIELD_HTMLCODE );
            dataEntryForm.setHtmlCode( proccessHtmlCode( reader.getElementValue() ) );

            importObject( dataEntryForm, params );
        }
    }

    // -------------------------------------------------------------------------
    // Support method
    // -------------------------------------------------------------------------

    private String proccessHtmlCode( String htmlCode )
    {
        if ( htmlCode == null )
        {
            return null;
        }

        StringBuffer buffer = new StringBuffer();

        Map<Object, String> dataElementMap = NameMappingUtil.getDataElementMap();
        Map<Object, DataElementCategoryOptionCombo> categoryOptionMap = NameMappingUtil.getCategoryOptionComboMap();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Pattern dataElementPattern = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
        Matcher dataElementMatcher = dataElementPattern.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Pattern to extract data element ID from data element field
        // ---------------------------------------------------------------------

        Pattern identifierPattern = Pattern.compile( "value\\[(.*)\\].value:value\\[(.*)\\].value" );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        while ( dataElementMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String dataElementCode = dataElementMatcher.group( 1 );

            Matcher identifierMatcher = identifierPattern.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get ids of old data element and category into HtmlCode
                // -------------------------------------------------------------

                String oldDataElementId = identifierMatcher.group( 1 );
                String oldCategoryId = identifierMatcher.group( 2 );

                // -------------------------------------------------------------
                // Get new data element and new category
                // -------------------------------------------------------------

                String dataElementName = dataElementMap.get( Integer.parseInt( oldDataElementId ) );
                DataElement dataElement = dataElementService.getDataElementByName( dataElementName );

                DataElementCategoryOptionCombo _categoryOption = categoryOptionMap.get( Integer
                    .parseInt( oldCategoryId ) );
                DataElementCategoryOptionCombo categoryOption = categoryService
                    .getDataElementCategoryOptionCombo( _categoryOption );
                
                //TODO can we avoid getting the dataelement and categoryoptioncombo
                //TODO from database since we only want to get the new identifier
                //TODO through the ObjectMappingGenerator?
                
                //TODO we can also use DataElementOperand.getOperand to centralize

                // -------------------------------------------------------------
                // update the new ids for htmlCode
                // -------------------------------------------------------------

                dataElementCode = dataElementCode.replace( oldDataElementId, dataElement.getId() + "" );
                dataElementCode = dataElementCode.replace( oldCategoryId, categoryOption.getId() + "" );

                // -------------------------------------------------------------
                // update htmlCode
                // -------------------------------------------------------------

                dataElementMatcher.appendReplacement( buffer, dataElementCode.toString() );
            }

        }
        
        return buffer.toString();
    }

}
