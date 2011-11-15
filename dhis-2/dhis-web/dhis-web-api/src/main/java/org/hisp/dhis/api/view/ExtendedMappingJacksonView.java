package org.hisp.dhis.api.view;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author mortenoh
 */
public class ExtendedMappingJacksonView extends MappingJacksonJsonView
{
    private boolean includeRootElement = false;

    private boolean withPadding = false;

    private String callbackParameter = "callback";

    private String paddingFunction = "callback";

    public ExtendedMappingJacksonView()
    {

    }

    public ExtendedMappingJacksonView( boolean withPadding )
    {
        this.withPadding = withPadding;

        if ( withPadding )
        {
            setContentType( "application/javascript" );
        }
    }

    public void setIncludeRootElement( boolean includeRootElement )
    {
        this.includeRootElement = includeRootElement;
    }

    public void setWithPadding( boolean withPadding )
    {
        this.withPadding = withPadding;
    }

    public void setCallbackParameter( String callbackParameter )
    {
        this.callbackParameter = callbackParameter;
    }

    public void setPaddingFunction( String paddingFunction )
    {
        this.paddingFunction = paddingFunction;
    }

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        Object value = filterModel( model );
        ObjectMapper objectMapper = new ObjectMapper();

        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        objectMapper.getDeserializationConfig().setAnnotationIntrospector( introspector );
        objectMapper.getSerializationConfig().setAnnotationIntrospector( introspector );

        JsonFactory jf = objectMapper.getJsonFactory();
        JsonGenerator jg = jf.createJsonGenerator( response.getOutputStream(), JsonEncoding.UTF8 );

        if ( !includeRootElement && value instanceof Map )
        {
            Map map = (Map) value;

            if ( map.size() == 1 )
            {
                value = map.values().toArray()[0];
            }
        }

        if ( withPadding )
        {
            String callback = request.getParameter( callbackParameter );

            if ( callback == null || callback.length() == 0 )
            {
                callback = paddingFunction;
            }

            JSONPObject valueWithPadding = new JSONPObject( callback, value );
            jg.writeObject( valueWithPadding );
        }
        else
        {
            jg.writeObject( value );
        }
    }
}
