package org.hisp.dhis.dxf2.events.trackedentity;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
public class JacksonTrackedEntityInstanceService extends AbstractTrackedEntityInstanceService
{
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    private final static ObjectMapper xmlMapper = new XmlMapper();

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @SuppressWarnings( "unchecked" )
    private static <T> T fromXml( InputStream inputStream, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( inputStream, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromXml( String input, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( input, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromJson( InputStream inputStream, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( inputStream, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromJson( String input, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( input, clazz );
    }

    static
    {
        xmlMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true );
        xmlMapper.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        xmlMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );
        jsonMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true );
        jsonMapper.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        jsonMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummaries addTrackedEntityInstanceXml( InputStream inputStream ) throws IOException
    {
        ImportSummaries importSummaries = new ImportSummaries();
        String input = StreamUtils.copyToString( inputStream, Charset.forName( "UTF-8" ) );

        try
        {
            TrackedEntityInstances trackedEntityInstances = fromXml( input, TrackedEntityInstances.class );

            for ( TrackedEntityInstance trackedEntityInstance : trackedEntityInstances.getTrackedEntityInstances() )
            {
                trackedEntityInstance.setTrackedEntityInstance( null );
                importSummaries.addImportSummary( addTrackedEntityInstance( trackedEntityInstance ) );
            }
        }
        catch ( Exception ex )
        {
            TrackedEntityInstance trackedEntityInstance = fromXml( input, TrackedEntityInstance.class );
            trackedEntityInstance.setTrackedEntityInstance( null );
            importSummaries.addImportSummary( addTrackedEntityInstance( trackedEntityInstance ) );
        }

        return importSummaries;
    }

    @Override
    public ImportSummaries addTrackedEntityInstanceJson( InputStream inputStream ) throws IOException
    {
        ImportSummaries importSummaries = new ImportSummaries();
        String input = StreamUtils.copyToString( inputStream, Charset.forName( "UTF-8" ) );

        try
        {
            TrackedEntityInstances trackedEntityInstances = fromJson( input, TrackedEntityInstances.class );

            for ( TrackedEntityInstance trackedEntityInstance : trackedEntityInstances.getTrackedEntityInstances() )
            {
                trackedEntityInstance.setTrackedEntityInstance( null );
                importSummaries.addImportSummary( addTrackedEntityInstance( trackedEntityInstance ) );
            }
        }
        catch ( Exception ex )
        {
            TrackedEntityInstance trackedEntityInstance = fromJson( input, TrackedEntityInstance.class );
            trackedEntityInstance.setTrackedEntityInstance( null );
            importSummaries.addImportSummary( addTrackedEntityInstance( trackedEntityInstance ) );
        }

        return importSummaries;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary updateTrackedEntityInstanceXml( String id, InputStream inputStream ) throws IOException
    {
        TrackedEntityInstance trackedEntityInstance = fromXml( inputStream, TrackedEntityInstance.class );
        trackedEntityInstance.setTrackedEntityInstance( id );

        return updateTrackedEntityInstance( trackedEntityInstance );
    }

    @Override
    public ImportSummary updateTrackedEntityInstanceJson( String id, InputStream inputStream ) throws IOException
    {
        TrackedEntityInstance trackedEntityInstance = fromJson( inputStream, TrackedEntityInstance.class );
        trackedEntityInstance.setTrackedEntityInstance( id );

        return updateTrackedEntityInstance( trackedEntityInstance );
    }
}
