package org.hisp.dhis.api.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.common.view.IdentifiableObjectView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JacksonUtils
{
    private static ObjectMapper jsonMapper = new ObjectMapper();

    private static XmlMapper xmlMapper = new XmlMapper();

    private static Map<String, Class<?>> viewClasses = new HashMap<String, Class<?>>();

    static
    {
        jsonMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
        jsonMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
        jsonMapper.configure( SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false );
        jsonMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_FIELDS );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_CREATORS );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_GETTERS );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_SETTERS );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_IS_GETTERS );

        jsonMapper.getJsonFactory().enable( JsonGenerator.Feature.QUOTE_FIELD_NAMES );

        xmlMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
        xmlMapper.configure( ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true );
        xmlMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
        xmlMapper.configure( SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false );
        xmlMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
        xmlMapper.disable( MapperFeature.AUTO_DETECT_FIELDS );
        xmlMapper.disable( MapperFeature.AUTO_DETECT_CREATORS );
        xmlMapper.disable( MapperFeature.AUTO_DETECT_GETTERS );
        xmlMapper.disable( MapperFeature.AUTO_DETECT_SETTERS );
        xmlMapper.disable( MapperFeature.AUTO_DETECT_IS_GETTERS );

        // register view classes
        viewClasses.put( "default", IdentifiableObjectView.class );
        viewClasses.put( "basic", IdentifiableObjectView.class );
        viewClasses.put( "detailed", DetailedView.class );
        viewClasses.put( "export", ExportView.class );
    }

    public static Class<?> getViewClass( Object viewName )
    {
        if ( viewName == null || !(viewName instanceof String && ((String) viewName).length() != 0) )
        {
            return viewClasses.get( "default" );
        }

        return viewClasses.get( viewName );
    }

    //---------------------------------------------------------------------------------------------------
    // JSON
    //---------------------------------------------------------------------------------------------------

    public static void toJson( OutputStream output, Object value ) throws IOException
    {
        jsonMapper.writeValue( output, value );
    }

    public static String toJsonAsString( Object value ) throws IOException
    {
        return jsonMapper.writeValueAsString( value );
    }

    public static void toJsonWithView( OutputStream output, Object value, Class<?> viewClass ) throws IOException
    {
        jsonMapper.writerWithView( viewClass ).writeValue( output, value );
    }

    public static String toJsonWithViewAsString( Object value, Class<?> viewClass ) throws IOException
    {
        return jsonMapper.writerWithView( viewClass ).writeValueAsString( value );
    }

    public static <T> T fromJson( InputStream input, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( input, clazz );
    }

    //---------------------------------------------------------------------------------------------------
    // XML
    //---------------------------------------------------------------------------------------------------

    public static void toXml( OutputStream output, Object value ) throws IOException
    {
        xmlMapper.writeValue( output, value );
    }

    public static String toXmlAsString( Object value ) throws IOException
    {
        return xmlMapper.writeValueAsString( value );
    }

    public static void toXmlWithView( OutputStream output, Object value, Class<?> viewClass ) throws IOException
    {
        xmlMapper.writerWithView( viewClass ).writeValue( output, value );
    }

    public static String toXmlWithViewAsString( Object value, Class<?> viewClass ) throws IOException
    {
        return xmlMapper.writerWithView( viewClass ).writeValueAsString( value );
    }

    public static <T> T fromXml( InputStream input, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( input, clazz );
    }
}
