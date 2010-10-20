package org.hisp.dhis.web.api.provider;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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

import org.hisp.dhis.web.api.model.ActivityWrapper;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import com.sun.jersey.spi.resource.Singleton;

@Provider
@Singleton
@Produces( "application/vnd.org.dhis2.activitywrapper+serialized" ) 
public class ActivityWrapperProvider implements MessageBodyWriter<ActivityWrapper>
{
    @Override
    public long getSize(ActivityWrapper arg0, Class<?> arg1, Type arg2,
                    Annotation[] arg3, MediaType arg4) {
            return -1;
    }

    @Override
    public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
                    MediaType arg3) {
            return true; 
    }

    @Override
    public void writeTo(ActivityWrapper activityWrapper, Class<?> arg1, Type arg2,
                    Annotation[] arg3, MediaType arg4,
                    MultivaluedMap<String, Object> arg5, OutputStream stream)
                    throws IOException, WebApplicationException {           
            
            //activityPlan.serialize( stream );
            serializeZipped(activityWrapper, stream);
    }
    
    public void serializeZipped( ActivityWrapper activityWrapper, OutputStream os ) throws IOException
    {
            ByteArrayOutputStream baos = serializePersistent( activityWrapper );               
            ZOutputStream gzip = new ZOutputStream( os, JZlib.Z_BEST_COMPRESSION );
            DataOutputStream dos = new DataOutputStream( gzip );
            
            try
            {       
                    byte[] res = baos.toByteArray();
                    dos.write( res );
            }
            finally
            {
                    dos.flush();    
                    gzip.finish();
            }
    }
    
    public ByteArrayOutputStream serializePersistent( ActivityWrapper activityWrapper ) throws IOException
    {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream( baos );
            activityWrapper.serialize(out);
            out.flush();
            return baos;
    }

    
}
