package org.hisp.dhis.mobile.db;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.rms.RecordFilter;

public class DataValueFilter
    implements RecordFilter
{
    private int proStageInstanceID;

    private int dataElementID;

    public boolean matches( byte[] candidate )
    {

        ByteArrayInputStream bis = new ByteArrayInputStream( candidate );
        DataInputStream dis = new DataInputStream( bis );
        try
        {
            if ( dis.readInt() == this.proStageInstanceID && dis.readInt() == this.dataElementID )
                return true;
            else
                return false;
        }
        catch ( IOException e )
        {
            return false;
        }
        finally
        {
            try
            {
                bis.close();
                dis.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

        }
    }

    public int getProStageInstanceID()
    {
        return proStageInstanceID;
    }

    public void setProStageInstanceID( int proStageInstanceID )
    {
        this.proStageInstanceID = proStageInstanceID;
    }

    public int getDataElementID()
    {
        return dataElementID;
    }

    public void setDataElementID( int dataElementID )
    {
        this.dataElementID = dataElementID;
    }

}
