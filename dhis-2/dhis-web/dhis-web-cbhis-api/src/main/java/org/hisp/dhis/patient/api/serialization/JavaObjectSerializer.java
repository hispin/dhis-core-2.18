package org.hisp.dhis.patient.api.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for serializers implemented in the same fashion as openXdata/openMRS XForms module for mobile java clients.
 */
public interface JavaObjectSerializer<T>
{

    public void serialize( OutputStream os, T data )
        throws IOException;

    public T deSerialize( InputStream is )
        throws IOException;

}