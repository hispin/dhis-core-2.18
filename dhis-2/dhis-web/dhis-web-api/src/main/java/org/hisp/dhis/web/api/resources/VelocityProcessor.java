package org.hisp.dhis.web.api.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.ViewProcessor;

//@Provider
//@Singleton
public class VelocityProcessor
    implements ViewProcessor<Template>
{
    private static final String RESOURCE_LOADER_NAME = "class";

    private VelocityEngine velocity = new VelocityEngine();

    private String templatePrefix = "dhis-web-api/";

    public void init()
        throws Exception
    {
        velocity.setProperty( Velocity.RESOURCE_LOADER, RESOURCE_LOADER_NAME );
        velocity.setProperty( RESOURCE_LOADER_NAME + ".resource.loader.class", ClasspathResourceLoader.class.getName() );
        velocity.init();
    }

    public void render( Object o, String template, Writer writer )
        throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException, Exception
    {
        final VelocityContext context = new VelocityContext();

        if ( o != null )
            context.put( "object", o );

        velocity.getTemplate( templatePrefix + template + ".vm" ).merge( context, writer );

    }

    @Override
    public Template resolve( String name )
    {
        String templatePath = templatePrefix + name + ".vm";

        if ( !velocity.templateExists( templatePath ) )
        {
            System.out.println("Couldn't find " + templatePath);
            return null;
        }
        try
        {
            return velocity.getTemplate( templatePath );
        }
        catch ( Exception e )
        {
            // TODO: handle
            return null;
        }
    }

    @Override
    public void writeTo( Template t, Viewable viewable, OutputStream out )
        throws IOException
    {
        final VelocityContext context = new VelocityContext();
        
        Object model = viewable.getModel();
        if (model != null)
        {
            context.put( "object", model );
        }

        t.merge( context, new OutputStreamWriter( out ) );
    }
}
