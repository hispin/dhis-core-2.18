package org.hisp.dhis.mobile.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.ProgramStageForm;
import org.hisp.dhis.mobile.model.User;
import org.hisp.dhis.mobile.parser.ActivityPlanParser;
import org.hisp.dhis.mobile.parser.FormParser;
import org.hisp.dhis.mobile.parser.FormsParser;
import org.hisp.dhis.mobile.parser.OrgUnitParser;
import org.hisp.dhis.mobile.parser.Parser;
import org.hisp.dhis.mobile.ui.DHISMIDlet;
import org.hisp.dhis.mobile.util.StringUtil;

public class DownloadManager
    extends Thread
{

    public static final String DOWNLOAD_ALL = "ALL";

    public static final String DOWNLOAD_FORMS = "forms";

    public static final String DOWNLOAD_FORM = "form";

    public static final String DOWNLOAD_ACTIVITYPLAN = "activityPlan";

    public static final String DOWNLOAD_ORGUNIT = "orgUnit";

    private String ua;

    private DHISMIDlet dhisMIDlet;

    private String url;

    private User user;

    private String task;

    public DownloadManager()
    {
    }

    public DownloadManager( DHISMIDlet dhisMIDlet, String url, User user, String task )
    {

        this.dhisMIDlet = dhisMIDlet;
        this.url = url;
        this.user = user;
        this.task = task;

        ua = "Profile/" + System.getProperty( "microedition.profiles" ) + " Configuration/"
            + System.getProperty( "microedition.configuration" );
    }

    public void run()
    {
        try
        {

            if ( task.equals( DOWNLOAD_FORMS ) )
            {
                Vector programStagesVector = (Vector) download( url, new FormsParser() );
                dhisMIDlet.displayFormsForDownload( programStagesVector );
            }
            else if ( task.equals( DOWNLOAD_FORM ) )
            {
                ProgramStageForm form = (ProgramStageForm) download( url, new FormParser() );
                dhisMIDlet.saveForm( form );
                dhisMIDlet.renderForm( form, dhisMIDlet.getDataEntryForm() );
            }
            else if ( task.equals( DOWNLOAD_ORGUNIT ) )
            {
                OrgUnit orgunit = (OrgUnit) download( url, new OrgUnitParser() );
                dhisMIDlet.saveOrgUnit( orgunit );
                dhisMIDlet.switchDisplayable( null, dhisMIDlet.getPinForm() );
                // dhisMIDlet.downloadActivities();
            }
            else if ( task.equals( DOWNLOAD_ACTIVITYPLAN ) )
            {
                Vector activitiesVector = (Vector) download( url, new ActivityPlanParser() );
                dhisMIDlet.saveActivities( activitiesVector );
                dhisMIDlet.displayCurActivities();
            }
            else if ( task.equals( DOWNLOAD_ALL ) )
            {
                OrgUnit orgunit = (OrgUnit) download( url, new OrgUnitParser() );
                dhisMIDlet.saveOrgUnit( orgunit );
                dhisMIDlet.downloadActivities();
            }
        }
        catch ( AuthenticationException e )
        {
            // The user on the phone does not match server side user..
            dhisMIDlet.loginNeeded();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            dhisMIDlet.error( e.getMessage() );
            // Handle!
        }
    }

    private Object download( String url, Parser parser )
        throws Exception
    {
        HttpConnection connection = null;
        InputStream inStream = null;

        try
        {
            for ( int redirectTimes = 0; redirectTimes < 5; redirectTimes++ )
            {
                connection = (HttpConnection) Connector.open( url );
                configureConnection( connection );
                inStream = connection.openInputStream();

                int status = connection.getResponseCode();

                System.out.println( "Server Response Code: " + status );

                switch ( status )
                {
                case HttpConnection.HTTP_OK: // Success!
                    return parser.read( inStream );
                case HttpConnection.HTTP_SEE_OTHER:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
                    // Redirect: get the new location
                    url = connection.getHeaderField( "location" );

                    if ( connection != null )
                        try
                        {
                            connection.close();
                        }
                        catch ( IOException ioe )
                        {
                        }
                    if ( inStream != null )
                        try
                        {
                            inStream.close();
                        }
                        catch ( IOException ioe )
                        {
                        }

                    connection = null;
                    break;
                case HttpConnection.HTTP_CONFLICT:
                    String error = StringUtil.streamToString( inStream );
                    dhisMIDlet.error( error );
                    throw new IOException( error );
                case HttpConnection.HTTP_UNAUTHORIZED:
                    throw new AuthenticationException();
                case HttpConnection.HTTP_NOT_FOUND:
                    connection.close();
                    throw new IOException("Server not found");
                default:
                    // Error: throw exception
                    connection.close();
                    throw new IOException( "Response status not OK: " + status );
                }
            }

            throw new IOException( "Too much redirects" );
        }
        finally
        {
            if ( connection != null )
                try
                {
                    connection.close();
                }
                catch ( IOException ioe )
                {
                }
            if ( inStream != null )
                try
                {
                    inStream.close();
                }
                catch ( IOException ioe )
                {
                }
        }
    }

    private void configureConnection( HttpConnection conn )
        throws IOException
    {

        conn.setRequestProperty( "User-Agent", ua );
        String locale = System.getProperty( "microedition.locale" );
        if ( locale == null )
        {
            locale = "en-US";
        }
        conn.setRequestMethod( HttpConnection.GET );
        conn.setRequestProperty( "Accept-Language", locale );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );

        if ( user != null )
        {
            // set HTTP basic authentication
            String hash = new String( Base64.encode( user.getUsername() + ":" + user.getPassword() ) );
            conn.setRequestProperty( "Authorization", "Basic " + hash );
        }

        conn.setRequestProperty( "Accept", "application/xml" );
    }

}