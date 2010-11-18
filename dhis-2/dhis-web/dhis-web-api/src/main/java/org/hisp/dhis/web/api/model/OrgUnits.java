package org.hisp.dhis.web.api.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class OrgUnits
    extends ArrayList<OrgUnit>
    implements DataStreamSerializable
{

    @Override
    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( size() );
        for ( OrgUnit unit : this )
        {
            unit.serialize( dataOutputStream );
        }

    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        this.clear();
        int size = dataInputStream.readInt();

        for ( int i = 0; i < size; i++ )
        {
            OrgUnit unit = new OrgUnit();
            unit.deSerialize( dataInputStream );
            add( unit );
        }
    }

}
