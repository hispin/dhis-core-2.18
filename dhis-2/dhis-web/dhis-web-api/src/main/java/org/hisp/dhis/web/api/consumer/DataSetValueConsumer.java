package org.hisp.dhis.web.api.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.hisp.dhis.web.api.model.DataSetValue;

import com.sun.jersey.spi.resource.Singleton;

@Provider
@Singleton
@Consumes( "application/vnd.org.dhis2.datasetvalue+serialized" )
public class DataSetValueConsumer implements MessageBodyReader<DataSetValue>{

	@Override
	public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2,
			MediaType arg3) {		
		return true;
	}

	@Override
	public DataSetValue readFrom(Class<DataSetValue> arg0, Type arg1,
			Annotation[] arg2, MediaType arg3,
			MultivaluedMap<String, String> arg4, InputStream stream )
			throws IOException, WebApplicationException {			
		
		DataSetValue dsValue = new DataSetValue();		
		arg0.cast(dsValue);
		
		return dsValue.deSerialize( stream );
		
	}

}
