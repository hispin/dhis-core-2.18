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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.DataEntryFormImporter;

/**
 * @author Chau Thu Tran
 * 
 * @version $ID: DataEntryFormConverter.java Dec 20, 2010 09:34:28 AM $
 */
public class DataEntryFormConverter
    extends DataEntryFormImporter
    implements XMLConverter
{
    private static final Log log = LogFactory.getLog( DataEntryFormConverter.class );
    
    private static final Pattern ID_PATTERN = Pattern.compile( "value\\[(.*)\\].value:value\\[(.*)\\].value" );
    
    public static final String COLLECTION_NAME = "dataEntryForms";
    public static final String ELEMENT_NAME = "dataEntryForm";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_HTMLCODE = "htmlCode";

    private Map<Object, Integer> dataElementMapping;
    private Map<Object, Integer> categoryOptionComboMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataEntryFormConverter( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    /**
     * Constructor for read operations.
     */
    public DataEntryFormConverter( ImportObjectService importObjectService,
        DataEntryFormService dataEntryFormService,
        Map<Object, Integer> dataElementMapping, 
        Map<Object, Integer> categoryOptionComboMapping )
    {
        this.importObjectService = importObjectService;
        this.dataEntryFormService = dataEntryFormService;
        this.dataElementMapping = dataElementMapping;
        this.categoryOptionComboMapping = categoryOptionComboMapping;
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
            dataEntryForm.setHtmlCode( proccessHtmlCode( reader.getElementValue(), dataEntryForm.getName() ) );

            importObject( dataEntryForm, params );
        }
    }

    // -------------------------------------------------------------------------
    // Support method
    // -------------------------------------------------------------------------

    private String proccessHtmlCode( String htmlCode, String name )
    {
        if ( htmlCode == null )
        {
            return null;
        }

        StringBuffer buffer = new StringBuffer();

        Matcher matcher = ID_PATTERN.matcher( htmlCode );
        
        while ( matcher.find() )
        {
            if ( matcher.groupCount() > 0 )
            {
                String dataElement = matcher.group( 1 );
                String categoryOptionCombo = matcher.group( 2 );

                Integer dataElementId = dataElementMapping.get( Integer.valueOf( dataElement ) );
                Integer categoryOptionComboId = categoryOptionComboMapping.get( Integer.valueOf( categoryOptionCombo ) );
                
                if ( dataElement == null )
                {
                    log.warn( "Data element or category option combo does not exist for data entry form: " + name );
                    continue;
                }
                
                if ( categoryOptionComboId == null )
                {
                    log.warn( "Category option combo does not exist for data entry form: " + name );
                    continue;
                }
                
                matcher.appendReplacement( buffer, String.valueOf( dataElementId ) );
                matcher.appendReplacement( buffer, String.valueOf( categoryOptionComboId ) );
            }
        }
        
        matcher.appendTail( buffer );
        
        return buffer.toString();
    }
}
