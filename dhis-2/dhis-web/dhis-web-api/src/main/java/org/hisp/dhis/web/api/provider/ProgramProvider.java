/**
 * 
 */
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

import org.hisp.dhis.web.api.model.Program;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import com.sun.jersey.spi.resource.Singleton;

/**
 * @author abyotag_adm
 *
 */
@Provider
@Singleton
@Produces( "application/vnd.org.dhis2.program+serialized" ) 
public class ProgramProvider implements MessageBodyWriter<Program> {

	@Override
	public long getSize(Program arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4) {		
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
			MediaType arg3) {		
		return true;
	}

	@Override
	public void writeTo(Program program, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4,
			MultivaluedMap<String, Object> arg5, OutputStream stream)
			throws IOException, WebApplicationException {		
		
		serializeZipped(program, stream);
	}
	
	public void serializeZipped( Program program, OutputStream os ) throws IOException
	{
		ByteArrayOutputStream baos = serializePersistent( program );		
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
	
	public ByteArrayOutputStream serializePersistent( Program program ) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		program.serialize(out);
		out.flush();
		return baos;
	}
}
