package org.hisp.dhis.config.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.system.database.DatabaseInfoProvider;

import com.opensymphony.xwork2.Action;

public class TakeMySqlBackupAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private DatabaseInfoProvider provider;
    
    public void setProvider( DatabaseInfoProvider provider )
    {
        this.provider = provider;
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
        
        //List<String> dbInfoList = new ArrayList<String>( dbConnection.getDBInfo() );
                                
        //String dbName = dbInfoList.get( 0 );
        //String userName = dbInfoList.get( 1 );
        //String password = dbInfoList.get( 2 );
        
        //String mySqlPath = dashBoardService.getMYSqlPath();
        
        String dbName = provider.getDatabaseInfo().getName();
        String userName = provider.getDatabaseInfo().getUser();
        String password = provider.getDatabaseInfo().getPassword();
        
        String mySqlPath = configurationService.getConfigurationByKey( Configuration_IN.KEY_MYSQLPATH ).getValue();

        
        Calendar curDateTime = Calendar.getInstance();
        Date curDate = new Date();                
        curDateTime.setTime( curDate );
        
        simpleDateFormat = new SimpleDateFormat( "ddMMMyyyy-HHmmssSSS" );
                
        String tempFolderName = simpleDateFormat.format( curDate );
        System.out.println( tempFolderName );
        
        //backupFilePath = dashBoardService.getRootDataPath();
        backupFilePath = configurationService.getConfigurationByKey( Configuration_IN.KEY_BACKUPDATAPATH ).getValue();
        backupFilePath += tempFolderName;
        
        File newdir = new File( backupFilePath );
        if( !newdir.exists() )
            newdir.mkdirs();
        
        backupFilePath += "/" + "dhis2.sql";
        
        String backupCommand = "";
        
        try
        {
            if(password == null || password.trim().equals( "" ))
                backupCommand = mySqlPath + "mysqldump -u "+ userName +" "+ dbName +" -r "+backupFilePath;
            else
                backupCommand = mySqlPath + "mysqldump -u "+ userName +" -p"+ password +" "+ dbName +" -r "+backupFilePath;

            System.out.println( backupCommand );

            Runtime rt = Runtime.getRuntime();
            rt.exec( backupCommand );

            statusMessage = "Backup taken succussfully at : "+backupFilePath;
        }
        catch ( Exception e )
        {
            System.out.println("Exception : "+e.getMessage());
            statusMessage = "Not able to take Backup, Please check MySQL configuration and SQL file path.";
        }

        return SUCCESS;
    }


    
}
