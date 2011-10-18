package org.hisp.dhis.mobile.web.mapping;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.hisp.dhis.mobile.api.NotAllowedException;

import com.sun.jersey.spi.resource.Singleton;

@Provider
@Singleton
public class NotAllowedExceptionMapper
    implements ExceptionMapper<NotAllowedException>
{

    @Override
    public Response toResponse( NotAllowedException exception )
    {
        return Response.status( Status.CONFLICT ).entity( exception.getReason() ).type( MediaType.TEXT_PLAIN ).build();
    }

}
