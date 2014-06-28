package org.hisp.dhis.api.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InterpretationComment
    implements DataStreamSerializable
{
    private String text;

    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeUTF( this.getText() );

    }

    @Override
    public void deSerialize( DataInputStream din )
        throws IOException
    {
        text = din.readUTF();

    }

    @Override
    public void serializeVersion2_8( DataOutputStream dataOutputStream )
        throws IOException
    {

    }

    @Override
    public void serializeVersion2_9( DataOutputStream dataOutputStream )
        throws IOException
    {

    }

    @Override
    public void serializeVersion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {

    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

}
