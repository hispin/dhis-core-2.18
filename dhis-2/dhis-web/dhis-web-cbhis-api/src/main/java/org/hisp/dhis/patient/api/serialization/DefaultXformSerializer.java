package org.hisp.dhis.patient.api.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Privides the default xform serialization and deserialization from and to the
 * sever. An example of such clients could be mobile devices collecting data in
 * for instance offline mode, and then send it to the server when connected.
 * 
 * For those who want a different serialization format for xforms, just
 * implement the SerializableData interface and specify the class using the
 * openmrs global property {xforms.xformSerializer}. The jar containing this
 * class can then be put under the webapps/openmrs/web-inf/lib folder. One of
 * the reasons one could want a different serialization format is for
 * performance by doing a more optimized and compact format. Such an example
 * exists in the EpiHandy compact implementation of xforms.
 * 
 * @author Daniel
 * 
 */
public class DefaultXformSerializer
    implements JavaObjectSerializer<List<String>>
{

    private static final Log log = LogFactory.getLog( DefaultXformSerializer.class );

    public void serialize( OutputStream os, List<String> xforms )
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream( os );

        dos.writeByte( xforms.size() ); // Write the size such that the party at
                                        // the other end knows how many times to
                                        // loop.
        for ( String xml : xforms )
            dos.writeUTF( xml );
    }

    public List<String> deSerialize( InputStream is )
        throws IOException
    {

        DataInputStream dis = new DataInputStream( is );

        List<String> forms = new ArrayList<String>();
        int len = dis.readByte();
        for ( int i = 0; i < len; i++ )
            forms.add( dis.readUTF() );

        return forms;
    }
}
