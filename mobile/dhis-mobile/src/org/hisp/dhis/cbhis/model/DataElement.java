package org.hisp.dhis.cbhis.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataElement
    extends AbstractModel
{

    public static final int TYPE_STRING = 0;

    public static final int TYPE_INT = 1;

    public static final int TYPE_BOOL = 2;

    public static final int TYPE_DATE = 3;

    private int type;

    public DataElement()
    {
    }

    /**
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType( int type )
    {
        this.type = type;
    }

    public static DataElement recordToDataElement( byte[] rec )
    {
        ByteArrayInputStream bin = new ByteArrayInputStream( rec );
        DataInputStream din = new DataInputStream( bin );

        DataElement de = new DataElement();
        try
        {
            de.setId( din.readInt() );
            de.setName( din.readUTF() );
            de.setType( din.readInt() );
        }
        catch ( IOException ioe )
        {
        }

        return de;
    }

    public static byte[] dataElementToRecord( DataElement dataElement )
    {

        ByteArrayOutputStream deOs = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( deOs );
        try
        {
            dout.writeInt( dataElement.getId() );
            dout.writeUTF( dataElement.getName() );
            dout.writeInt( dataElement.getType() );
            dout.flush();
        }
        catch ( IOException e )
        {
            System.out.println( e );
            e.printStackTrace();
        }
        return deOs.toByteArray();
    }

}
