package org.hisp.dhis.api.mobile.model.LWUITmodel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.hisp.dhis.api.mobile.model.Model;

public class RelationshipType
    extends Model
{
    private int id;

    private String aIsToB;

    private String bIsToA;
    
    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public void setId( int id )
    {
        this.id = id;
    }

    public String getAIsToB()
    {
        return aIsToB;
    }

    public void setAIsToB( String aIsToB )
    {
        this.aIsToB = aIsToB;
    }

    public String getBIsToA()
    {
        return bIsToA;
    }

    public void setBIsToA( String bIsToA )
    {
        this.bIsToA = bIsToA;
    }

    @Override
    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( this.id );
        dataOutputStream.writeUTF( this.getName() );
        dataOutputStream.writeUTF( this.aIsToB );
        dataOutputStream.writeUTF( this.bIsToA );
    }
    
    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        this.id = dataInputStream.readInt();
        this.setName( dataInputStream.readUTF() );
        this.aIsToB = dataInputStream.readUTF();
        this.bIsToA = dataInputStream.readUTF();
    }

    @Override
    public void serializeVersion2_8( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void serializeVersion2_9( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void serializeVersion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
    }
}