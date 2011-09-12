package org.hisp.dhis.web.api.mapping;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.resource.Singleton;

@Provider
@Singleton
public class IllegalArgumentExceptionMapper
    implements ExceptionMapper<IllegalArgumentException>
{
    @Override
    public Response toResponse( IllegalArgumentException e )
    {
        return Response.status( Status.CONFLICT ).entity( "Problem with input: " + e.getMessage() ).type( MediaType.TEXT_PLAIN ).build();
    }
}
