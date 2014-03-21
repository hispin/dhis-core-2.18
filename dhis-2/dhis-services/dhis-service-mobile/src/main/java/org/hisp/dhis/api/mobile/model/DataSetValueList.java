package org.hisp.dhis.api.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSetValueList
    extends Model
{
    private String clientVersion;

    private List<DataSetValue> dataSetValues = new ArrayList<DataSetValue>();

    public DataSetValueList()
    {
    }

    public List<DataSetValue> getDataSetValues()
    {
        return dataSetValues;
    }

    public void setDataSetValues( List<DataSetValue> dataSetValues )
    {
        this.dataSetValues = dataSetValues;
    }

    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
    }

    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt( dataSetValues.size() );
        for ( DataSetValue dataSetValue : dataSetValues )
        {
            dataSetValue.serialize( dout );
        }
    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        int size = 0;
        size = dataInputStream.readInt();
        if ( size > 0 )
        {
            dataSetValues = new ArrayList<DataSetValue>();
            for ( int i = 0; i < size; i++ )
            {
                DataSetValue dataSetValue = new DataSetValue();
                dataSetValue.deSerialize( dataInputStream );
                dataSetValues.add( dataSetValue );
            }
        }
    }
}
