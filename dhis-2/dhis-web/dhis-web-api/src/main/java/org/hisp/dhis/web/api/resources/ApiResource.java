package org.hisp.dhis.web.api.resources;

import java.io.IOException;
import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.sun.jersey.api.view.ImplicitProduces;

@Path( "/" )
@ImplicitProduces( MediaType.TEXT_HTML )
public class ApiResource
{
    private VelocityManager velocityManager;

    public void setVelocityManager( VelocityManager velocityManager )
    {
        this.velocityManager = velocityManager;
    }

    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getDescription()
        throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException, Exception
    {
        StringWriter writer = new StringWriter();

        velocityManager.render( null, "index", writer );

        return writer.toString();
    }
}
