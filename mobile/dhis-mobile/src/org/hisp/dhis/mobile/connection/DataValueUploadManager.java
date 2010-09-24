package org.hisp.dhis.mobile.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.model.DataValue;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.User;
import org.hisp.dhis.mobile.ui.DHISMIDlet;
import org.hisp.dhis.mobile.util.AlertUtil;

public class DataValueUploadManager
    extends Thread
{
    private DHISMIDlet dhisMidlet;

    private Vector dataValueVector;

    private String url;

    private OrgUnit orgUnit;

    private User user;

    public DataValueUploadManager( DHISMIDlet dhisMidlet, Vector dataValueVector, String url, OrgUnit orgUnit,
        User user )
    {
        this.dataValueVector = dataValueVector;
        this.url = url;
        this.orgUnit = orgUnit;
        this.user = user;
        this.dhisMidlet = dhisMidlet;
    }

    public void run()

    {
        HttpConnection connection = null;
        OutputStream opt = null;
        DataOutputStream dos = null;
        Enumeration en = null;
        try
        {
            // for ( int redirectTimes = 0; redirectTimes < 5; redirectTimes++ )
            // {
            connection = (HttpConnection) Connector.open( url );
            configureConnection( connection );
            opt = connection.openOutputStream();
            // int status = connection.getResponseCode();
            // switch ( status )
            // {
            // case HttpConnection.HTTP_SEE_OTHER:
            // case HttpConnection.HTTP_TEMP_REDIRECT:
            // case HttpConnection.HTTP_MOVED_TEMP:
            // case HttpConnection.HTTP_MOVED_PERM:
            // url = connection.getHeaderField( "location" );
            //
            // if ( connection != null )
            // try
            // {
            // connection.close();
            // }
            // catch ( IOException ioe )
            // {
            // }
            // if ( opt != null )
            // try
            // {
            // opt.close();
            // }
            // catch ( IOException ioe )
            // {
            // }
            // connection = null;
            // break;
            // default:
            // }
            // System.out.println( "Status: " + connection.getResponseCode() );
            // }

            int numOfDataValue = dataValueVector.size();
            System.out.println( "No of DataValues: " + numOfDataValue );
            dos = new DataOutputStream( opt );

            dos.writeInt( numOfDataValue );
            dos.writeInt( orgUnit.getId() );
            en = dataValueVector.elements();
            while ( en.hasMoreElements() )
            {
                DataValue dataValue = (DataValue) en.nextElement();
                dos.writeInt( dataValue.getDataElementId() );
                dos.writeInt( dataValue.getProgramInstanceId() );
                dos.writeUTF( dataValue.getValue() );
            }
            dos.flush();

            InputStream input = connection.openInputStream();
            StringBuffer buffer = new StringBuffer();
            int ch = -1;
            while ( (ch = input.read()) != -1 )
            {
                buffer.append( (char) ch );
            }
            System.out.println( buffer.toString() );
            dhisMidlet.switchDisplayable( AlertUtil.getInfoAlert( "Result", buffer.toString() ),
                dhisMidlet.getMainMenuList() );
        }
        catch ( Exception e )
        {
            System.out.println( "Error in DOS: " + e.getMessage() );
            e.printStackTrace();
        }
        finally
        {
            try
            {
                dos.close();
                opt.close();
                connection.close();
            }
            catch ( Exception e )
            {
                System.out.println( e.getMessage() );
            }

        }

    }

    private void configureConnection( HttpConnection connection )
        throws IOException
    {
        String ua = "Profile/" + System.getProperty( "microedition.profiles" ) + " Configuration/"
            + System.getProperty( "microedition.configuration" );
        String locale = System.getProperty( "microedition.locale" );

        connection.setRequestProperty( "User-Agent", ua );
        connection.setRequestProperty( "Accept-Language", locale );
        connection.setRequestMethod( HttpConnection.GET );

        connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
        String hash = new String( Base64.encode( user.getUsername() + ":" + user.getPassword() ) );
        connection.setRequestProperty( "Authorization", "Basic " + hash );
        connection.setRequestProperty( "Accept", "application/xml" );

    }
}
