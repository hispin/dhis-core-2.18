package org.hisp.dhis.patient.api.support;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.resources.DhisMediaType;
import org.hisp.dhis.patient.api.serialization.ActivityListSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.spi.resource.Singleton;

@Provider
@Singleton
@Produces(DhisMediaType.ACTIVITYPLAN_SERIALIZED)
public class ActivityPlanSerializer implements MessageBodyWriter<ActivityPlan>
{

    @Autowired
    private ActivityListSerializer serializer;

    @Override
    public boolean isWriteable( Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType )
    {
        return ActivityPlan.class.isAssignableFrom(type);
    }

    @Override
    public long getSize( ActivityPlan t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType )
    {
        // XXX: Figure out size..
        return -1;
    }

    @Override
    public void writeTo( ActivityPlan activityPlan, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream )
        throws IOException, WebApplicationException
    {
        serializer.serialize( entityStream, activityPlan.getActivitiesList() );
    }

}
