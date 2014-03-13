package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.common.collect.Maps;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.metadata.ExchangeClasses;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "", method = RequestMethod.GET )
public class SchemaController
{
    @RequestMapping( value = { "/schemas", "/schemas.json" }, method = RequestMethod.GET )
    public void getTypesJson( HttpServletResponse response ) throws IOException
    {
        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        Map<String, Map<String, ReflectionUtils.PropertyDescriptor>> output = Maps.newHashMap();

        for ( Class<? extends IdentifiableObject> key : ExchangeClasses.getAllExportMap().keySet() )
        {
            Map<String, ReflectionUtils.PropertyDescriptor> classMap = ReflectionUtils.getJacksonClassMap( key );
            output.put( ExchangeClasses.getAllExportMap().get( key ), classMap );
        }

        JacksonUtils.toJson( response.getOutputStream(), output );
    }

    @RequestMapping( value = { "/schemas.xml" }, method = RequestMethod.GET )
    public void getTypesXml( HttpServletResponse response ) throws IOException
    {
        response.setContentType( MediaType.APPLICATION_XML_VALUE );
        Map<String, Map<String, ReflectionUtils.PropertyDescriptor>> output = Maps.newHashMap();

        for ( Class<? extends IdentifiableObject> key : ExchangeClasses.getAllExportMap().keySet() )
        {
            Map<String, ReflectionUtils.PropertyDescriptor> classMap = ReflectionUtils.getJacksonClassMap( key );
            output.put( ExchangeClasses.getAllExportMap().get( key ), classMap );
        }

        try
        {
            ToXmlGenerator generator = (ToXmlGenerator) JacksonUtils.getXmlMapper().getJsonFactory().createJsonGenerator( response.getOutputStream() );
            XMLStreamWriter staxWriter = generator.getStaxWriter();

            staxWriter.writeStartElement( "", "schemas", DxfNamespaces.DXF_2_0 );

            for ( String key : output.keySet() )
            {
                Map<String, ReflectionUtils.PropertyDescriptor> map = output.get( key );
                writeClassMap( staxWriter, key, map );
            }

            staxWriter.writeEndElement();
            staxWriter.close();
        }
        catch ( XMLStreamException ex )
        {
            ex.printStackTrace();
        }
    }

    private void writeClassMap( XMLStreamWriter staxWriter, String type, Map<String, ReflectionUtils.PropertyDescriptor> classMap )
    {
        try
        {
            staxWriter.writeStartElement( "", "schema", DxfNamespaces.DXF_2_0 );
            staxWriter.writeAttribute( "type", type );

            for ( String field : classMap.keySet() )
            {
                staxWriter.writeStartElement( "", field, DxfNamespaces.DXF_2_0 );
                ReflectionUtils.PropertyDescriptor descriptor = classMap.get( field );
                writeDescriptor( staxWriter, descriptor );
                staxWriter.writeEndElement();
            }

            staxWriter.writeEndElement();
        }
        catch ( XMLStreamException ignored )
        {
        }
    }

    private void writeDescriptor( XMLStreamWriter staxWriter, ReflectionUtils.PropertyDescriptor descriptor )
    {
        writeSimpleElement( staxWriter, "name", descriptor.getName() );
        writeSimpleElement( staxWriter, "xmlName", descriptor.getXmlName() );
        writeSimpleElement( staxWriter, "xmlAttribute", descriptor.isXmlAttribute() );
        writeSimpleElement( staxWriter, "clazz", descriptor.getClazz() );
        writeSimpleElement( staxWriter, "collection", descriptor.isCollection() );
        writeSimpleElement( staxWriter, "identifiableObject", descriptor.isIdentifiableObject() );
        writeSimpleElement( staxWriter, "description", descriptor.getDescription() );
    }

    private void writeSimpleElement( XMLStreamWriter staxWriter, String fieldName, Object text )
    {
        if ( text == null )
        {
            return;
        }

        try
        {
            staxWriter.writeStartElement( "", fieldName, DxfNamespaces.DXF_2_0 );
            staxWriter.writeCharacters( text.toString() );
            staxWriter.writeEndElement();
        }
        catch ( XMLStreamException ignored )
        {
        }
    }
}
