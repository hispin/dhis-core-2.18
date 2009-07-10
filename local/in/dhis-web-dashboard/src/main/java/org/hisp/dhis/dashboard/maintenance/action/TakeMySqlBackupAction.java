package org.hisp.dhis.dashboard.maintenance.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dashboard.util.DBConnection;
import org.hisp.dhis.dashboard.util.DashBoardService;

import com.opensymphony.xwork.Action;

public class TakeMySqlBackupAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }

    private DBConnection dbConnection;

    public void setDbConnection( DBConnection dbConnection )
    {
        this.dbConnection = dbConnection;
    }

    // -------------------------------------------------------------------------
    // Input and Output Parameters
    // -------------------------------------------------------------------------

    private String statusMessage;

    public String getStatusMessage()
    {
        return statusMessage;
    }

    private String backupFilePath;

    public String getBackupFilePath()
    {
        return backupFilePath;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        List<String> dbInfoList = new ArrayList<String>( dbConnection.getDBInfo() );

        String dbName = dbInfoList.get( 0 );
        String userName = dbInfoList.get( 1 );
        String password = dbInfoList.get( 2 );

        String mySqlPath = dashBoardService.getMYSqlPath();

        Calendar curDateTime = Calendar.getInstance();
        Date curDate = new Date();
        curDateTime.setTime( curDate );

        simpleDateFormat = new SimpleDateFormat( "ddMMMyyyy-HHmmssSSS" );

        String tempFolderName = simpleDateFormat.format( curDate );
        System.out.println( tempFolderName );

        backupFilePath = dashBoardService.getRootDataPath();
        backupFilePath += tempFolderName;

        File newdir = new File( backupFilePath );
        if ( !newdir.exists() )
            newdir.mkdirs();

        backupFilePath += "/" + "dhis2.sql";

        String backupCommand = "";

        try
        {
            if ( password == null || password.trim().equals( "" ) )
                backupCommand = mySqlPath + "mysqldump -u " + userName + " " + dbName + " -r " + backupFilePath;
            else
                backupCommand = mySqlPath + "mysqldump -u " + userName + " -p" + password + " " + dbName + " -r "
                    + backupFilePath;

            System.out.println( backupCommand );

            Runtime rt = Runtime.getRuntime();
            rt.exec( backupCommand );

            statusMessage = "Backup taken succussfully at : " + backupFilePath;
        }
        catch ( Exception e )
        {
            System.out.println( "Exception : " + e.getMessage() );
            statusMessage = "Not able to take Backup, Please check MySQL configuration and SQL file path.";
        }

        return SUCCESS;
    }

}
