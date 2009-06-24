package org.hisp.dhis.importexport.dxf.converter;

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

import java.util.Collection;
import java.util.Map;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.converter.AbstractOlapUrlConverter;
import org.hisp.dhis.olap.OlapURL;
import org.hisp.dhis.olap.OlapURLService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class OlapUrlConverter
    extends AbstractOlapUrlConverter implements XMLConverter
{
    public static final String COLLECTION_NAME = "olapUrls";
    public static final String ELEMENT_NAME = "olapUrl";

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_URL = "url";

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public OlapUrlConverter( OlapURLService olapURLService )
    {   
        this.olapURLService = olapURLService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param importObjectService the importObjectService to use.
     * @param olapURLService the olapURLService to use.
     */
    public OlapUrlConverter( ImportObjectService importObjectService,
        OlapURLService olapURLService )
    {
        this.importObjectService = importObjectService;
        this.olapURLService = olapURLService;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<OlapURL> olapUrls = olapURLService.getOlapURLs( params.getOlapUrls() );
        
        if ( olapUrls != null && olapUrls.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );            

            for ( OlapURL url : olapUrls )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( url.getId() ) );
                writer.writeElement( FIELD_NAME, url.getName() );
                writer.writeElement( FIELD_URL, url.getUrl() );
                
                writer.closeElement();
            }
            
            writer.closeElement();
        }        
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final OlapURL olapUrl = new OlapURL();
            
            olapUrl.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            olapUrl.setName( values.get( FIELD_NAME ) );
            olapUrl.setUrl( values.get( FIELD_URL ) );
            
            read( olapUrl, GroupMemberType.NONE, params );
        }
    }
}
