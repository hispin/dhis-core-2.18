package org.hisp.dhis.web.api.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ProgramStage
    extends AbstractModel
{

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    public byte[] serialize()
        throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        dout.writeInt( this.getId() );
        dout.writeUTF( this.getName() );
        dout.writeInt( dataElements.size() );

        for ( int i = 0; i < dataElements.size(); i++ )
        {
            DataElement de = (DataElement) dataElements.get( i );
            dout.writeInt( de.getId() );
            dout.writeUTF( de.getName() );
            dout.writeUTF( de.getType() );
        }

        return bout.toByteArray();
    }

    public void serialize( OutputStream out )
        throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        dout.writeInt( this.getId() );
        dout.writeUTF( this.getName() );

        dout.writeInt( dataElements.size() );

        for ( int i = 0; i < dataElements.size(); i++ )
        {
            DataElement de = (DataElement) dataElements.get( i );
            dout.writeInt( de.getId() );
            dout.writeUTF( de.getName() );
            dout.writeUTF( de.getType() );

            List<AbstractModel> cateOptCombos = de.getCategoryOptionCombos().getAbstractModels();
            if ( cateOptCombos == null || cateOptCombos.size() <= 0 )
            {
                dout.writeInt( 0 );
            }
            else
            {
                dout.writeInt( cateOptCombos.size() );
                for ( AbstractModel each : cateOptCombos )
                {
                    dout.writeInt( each.getId() );
                    dout.writeUTF( each.getName() );
                }
            }
        }

        bout.flush();
        bout.writeTo( out );
    }

    // public void serialize( DataOutputStream dout ) throws IOException
    // {
    // dout.writeInt(this.getId());
    // dout.writeUTF(this.getName());
    // dout.writeInt(dataElements.size());
    // System.out.println("add dataelement");
    // for(int i=0; i<dataElements.size(); i++)
    // {
    // DataElement de = (DataElement)dataElements.get(i);
    // dout.writeInt( de.getId() );
    // dout.writeUTF( de.getName() );
    // dout.writeUTF( de.getType() );
    // }
    //
    // dout.flush();
    // }

    public void deSerialize( byte[] data )
        throws IOException
    {
        ByteArrayInputStream bin = new ByteArrayInputStream( data );
        DataInputStream din = new DataInputStream( bin );

        this.setId( din.readInt() );
        this.setName( din.readUTF() );

        int size = din.readInt();

        for ( int i = 0; i < size; i++ )
        {
            DataElement de = new DataElement();
            de.setId( din.readInt() );
            de.setName( din.readUTF() );
            de.setType( din.readUTF() );
            this.dataElements.add( de );
        }
    }
}