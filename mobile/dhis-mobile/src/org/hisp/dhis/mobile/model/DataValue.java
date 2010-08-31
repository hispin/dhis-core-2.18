/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.mobile.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * @author abyotag_adm
 */
public class DataValue
{

    private int programInstanceId;

    private int dataElementId;

    private String value;

    public DataValue()
    {
    }

    /**
     * @return the programInstanceId
     */
    public int getProgramInstanceId()
    {
        return programInstanceId;
    }

    /**
     * @param programInstanceId the programInstanceId to set
     */
    public void setProgramInstanceId( int programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    /**
     * @return the dataElementId
     */
    public int getDataElementId()
    {
        return dataElementId;
    }

    /**
     * @param dataElementId the dataElementId to set
     */
    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue( String value )
    {
        this.value = value;
    }

    public static DataValue recordToDataValue( byte[] rec )
    {
        ByteArrayInputStream bin = new ByteArrayInputStream( rec );
        DataInputStream din = new DataInputStream( bin );
        DataValue dataValue = new DataValue();

        try
        {
            dataValue.setProgramInstanceId( din.readInt() );
            dataValue.setDataElementId( din.readInt() );
            dataValue.setValue( din.readUTF() );
        }
        catch ( IOException ioe )
        {
            System.out.println( ioe.getMessage() );
        }

        return dataValue;
    }

    public static byte[] dataValueToRecord( DataValue dataValue )
    {
        ByteArrayOutputStream deOs = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( deOs );

        try
        {
            dout.writeInt( dataValue.getProgramInstanceId() );
            dout.writeInt( dataValue.getDataElementId() );
            dout.writeUTF( dataValue.getValue() );
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
