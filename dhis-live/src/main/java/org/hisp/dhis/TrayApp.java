/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis;


import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.config.ConfigType;
import org.hisp.dhis.config.ConfigType.AppConfiguration;
import org.hisp.dhis.config.ConfigType.DatabaseConfiguration;
import org.hisp.dhis.config.ConfigType.DatabaseConfiguration.ConnectionTypes.ConnectionType;
import org.hisp.dhis.config.ConfigType.DatabaseConfiguration.DatabaseConnections.Connection;
import org.mortbay.component.LifeCycle;

/**
 * @author Bob Jolliffe
 */
public class TrayApp
    implements LifeCycle.Listener
{

    private static final Log log = LogFactory.getLog( TrayApp.class );

    private static final String CONFIG_DIR = "/conf";

    private static final String CONFIG_FILE_NAME = "/conf/config.xml";

    private static final String CONFIG_DEFAULT = "/defaultConfig.xml";

    private static final String STOPPED_ICON = "/icons/stopped.png";

    private static final String STARTING_ICON = "/icons/starting.gif";

    private static final String FAILED_ICON = "/icons/failed.png";

    private static final String RUNNING_ICON = "/icons/running.png";

    protected static LiveMessagingService messageService = new LiveMessagingService();

    private static TrayApp instance;

    private WebAppServer appServer;

    private TrayIcon trayIcon;

    private SettingsWindow settingsWindow;

    private JAXBElement<ConfigType> configElement;

    private Menu databaseMenu;

    public static ConfigType config;

    public static DatabaseConfiguration databaseConfig;

    public static AppConfiguration appConfig;

    public static String installDir;

    // -------------------------------------------------------------------------
    // Main method
    // -------------------------------------------------------------------------
    public static void main( String[] args )
    {
        log.info( "Environment variable DHIS2_HOME: " + System.getenv( "DHIS2_HOME" ) );
        EventQueue.invokeLater( new Runnable()
        {

            @Override
            public void run()
            {
                if ( !SystemTray.isSupported() )
                {
                    JOptionPane.showMessageDialog( (JFrame) null, messageService.getString( "dialogbox.unsupportedPlatform" ) );
                    System.exit( 0 );
                } else
                {
                    try
                    {
                        TrayApp trayApp = TrayApp.getInstance();
                        trayApp.init();
                    } catch ( Exception ex )
                    {
                        log.fatal( "TrayApp Initialization failure", ex );
                        JOptionPane.showMessageDialog( (JFrame) null, messageService.getString( "dialogbox.initFailure" ) );
                        System.exit( 0 );
                    }
                }
            }
        } );
    }

    /**
     * TrayApp is singleton - hide constructor
     * Loads all the configurations of the application
     */
    private TrayApp()
    {
        log.info( "Initialising DHIS 2 Live..." );
        installDir = getInstallDir();
        if ( installDir == null )
        {
            installDir = System.getenv( "DHIS2_HOME" );
            if ( installDir == null )
            {
                log.fatal( "Neither DHIS Live Jar nor DHIS2_HOME could be found." );
                JOptionPane.showMessageDialog( (JFrame) null, messageService.getString( "dialogbox.initFailure" ) );
                System.exit( 0 );
            } else
            {
                log.info( "jar not installed, setting installdir to DHIS2_HOME: " + System.getenv( "DHIS2_HOME" ) );
                installDir = System.getenv( "DHIS2_HOME" );
            }
        }

        InputStream configStream = null;
        try
        {
            configStream = new java.io.FileInputStream( installDir + CONFIG_FILE_NAME );
            readConfigFromStream( configStream );
        } catch ( FileNotFoundException ex )
        {
            log.info( "Can't locate external config - falling back to default" );
            configStream = TrayApp.class.getResourceAsStream( CONFIG_DEFAULT );
            readConfigFromStream( configStream );
            writeConfigToFile();
            try
            {
                configStream = new java.io.FileInputStream( installDir + CONFIG_FILE_NAME );
            } catch ( FileNotFoundException fnfex )
            {
                log.info( "Can't locate external config - falling back to default", fnfex );
                JOptionPane.showMessageDialog( null, "Unexpected Error", "File Error", JOptionPane.ERROR_MESSAGE );
                System.exit( 1 );
            }
        }
        databaseConfig = config.getDatabaseConfiguration();
        appConfig = config.getAppConfiguration();
        log.info( "Locale: " + appConfig.getLocaleLanguage() + ":" + appConfig.getLocaleCountry() );

        // get the selected database
        Connection conn = (Connection) databaseConfig.getDatabaseConnections().getSelected();
        log.info( "Selected db: " + conn.getName() + "; " + conn.getUserName() + ":" + conn.getPassword() + " " + conn.getURL() );

        System.setProperty( "dhis2.home", installDir + CONFIG_DIR );
        System.setProperty( "jetty.home", installDir );
        System.setProperty( "birt.home", installDir + WebAppServer.BIRT_DIR );
        System.setProperty( "birt.context.path", WebAppServer.BIRT_CONTEXT_PATH );

        writeHibernateProperties();
    }

    public static TrayApp getInstance()
    {
        if ( instance == null )
        {
            instance = new TrayApp();
        }
        return instance;
    }

    public void init() throws AWTException, InterruptedException
    {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = createImage( STOPPED_ICON, "tray icon" );
        PopupMenu popup = new PopupMenu();

        // <editor-fold defaultstate="collapsed" desc="Open Item on Popup">
        MenuItem openItem = new MenuItem( messageService.getString( "menuitem.open" ) );
        openItem.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                launchBrowser();
            }
        } );
        popup.add( openItem );
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Databases Menu on Popup">
        databaseMenu = new Menu( messageService.getString( "menuitem.database" ) );
        updateDatabaseMenus();
        popup.add( databaseMenu );
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Settings Item on Popup">
        MenuItem settingsItem = new MenuItem( messageService.getString( "menuitem.settings" ) );
        settingsItem.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                if ( settingsWindow!= null && settingsWindow.isVisible() )
                {
                    settingsWindow.dispose();
                }
                settingsWindow = new SettingsWindow();
                settingsWindow.setVisible( true );
            }
        } );
        popup.add( settingsItem );
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Exit Item on Popup">
        MenuItem exitItem = new MenuItem( messageService.getString( "menuitem.exit" ) );
        exitItem.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                shutdown();
            }
        } );
        popup.add( exitItem );
        //</editor-fold>

        trayIcon = new TrayIcon( image, "DHIS 2 Live", popup );
        trayIcon.setImageAutoSize( true );
        tray.add( trayIcon );

        new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                appServer = new WebAppServer();
                try
                {
                    appServer.init();
                } catch ( Exception e )
                {
                    log.fatal( "Application server could not be initialized" );
                }
                try
                {
                    appServer.start();
                } catch ( Exception e )
                {
                    log.fatal( "Application server could not be started" );
                }
            }
        } ).start();
    }

    public void updateDatabaseMenus()
    {
        databaseMenu.removeAll();

        MenuItem blankItem = new MenuItem( messageService.getString( "menuitem.blank" ) );
        blankItem.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                String s = (String) JOptionPane.showInputDialog( null, "menuitem.dbname", "Blank DB",
                    JOptionPane.QUESTION_MESSAGE );
                Connection conn = new Connection();
                conn.setId( s );
                conn.setName( s );
                conn.setType( config.getDatabaseConfiguration().getConnectionTypes().getConnectionType().get( 0 ) );
                conn.setUserName( s );
                conn.setURL( "jdbc:h2:./database/" + s + ";AUTO_SERVER=TRUE" );
                databaseConfig.getDatabaseConnections().getConnection().add( conn );
                databaseConfig.getDatabaseConnections().setSelected( conn );
                writeConfigToFile();
                writeHibernateProperties();
                new Thread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            appServer.stop();
                            appServer.start();
                        } catch ( Exception ex )
                        {
                            log.error( "Error restarting jetty server: ", ex );
                        }
                    }
                } ).start();
                MenuItem newItem = new MenuItem( s );
                newItem.addActionListener( new ActionListener()
                {

                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        new Thread( new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                try
                                {
                                    appServer.stop();
                                    appServer.start();
                                } catch ( Exception ex )
                                {
                                    log.error( "Error restarting jetty server: ", ex );
                                }
                            }
                        } ).start();
                    }
                } );
                databaseMenu.add( newItem );
            }
        } );
        databaseMenu.add( blankItem );
        databaseMenu.addSeparator();

        List<Connection> dbConns = (List) config.getDatabaseConfiguration().getDatabaseConnections().getConnection();
        for ( final Connection dbConn : dbConns )
        {
            MenuItem connItem = new MenuItem( dbConn.getName() );
            connItem.addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent evt )
                {
                    config.getDatabaseConfiguration().getDatabaseConnections().setSelected( dbConn );
                    writeConfigToFile();
                    writeHibernateProperties();
                    new Thread( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                appServer.stop();
                                appServer.start();
                            } catch ( Exception ex )
                            {
                                log.error( "Error restarting jetty server: ", ex );
                            }
                        }
                    } ).start();
                }
            } );
            databaseMenu.add( connItem );
        }
    }

    // -------------------------------------------------------------------------
    // Listener implementation
    // -------------------------------------------------------------------------
    @Override
    public void lifeCycleFailure( LifeCycle arg0, Throwable arg1 )
    {
        log.warn( "Lifecycle: server failed" );
        trayIcon.setImage( createImage( FAILED_ICON, "Running icon" ) );
        JOptionPane.showMessageDialog( (JFrame) null, messageService.getString( "dialogbox.webserverFailure" ) );
        shutdown();
    }

    @Override
    public void lifeCycleStarted( LifeCycle arg0 )
    {
        log.info( "Lifecycle: server started" );
        trayIcon.displayMessage( messageService.getString( "notification.started" ), messageService.getString( "notification.startedDetails" ) + " " + getUrl() + ".",
            TrayIcon.MessageType.INFO );
        trayIcon.setToolTip( messageService.getString( "tooltip.running" ) );
        trayIcon.setImage( createImage( RUNNING_ICON, "Running icon" ) );
        launchBrowser();

    }

    @Override
    public void lifeCycleStarting( LifeCycle arg0 )
    {
        log.info( "Lifecycle: server starting" );
        trayIcon.displayMessage( messageService.getString( "notification.starting" ), messageService.getString( "notification.startingDetails" ), TrayIcon.MessageType.INFO );
        trayIcon.setImage( createImage( STARTING_ICON, "Starting icon" ) );
    }

    @Override
    public void lifeCycleStopped( LifeCycle arg0 )
    {
        log.info( "Lifecycle: server stopped" );
        trayIcon.displayMessage( messageService.getString( "notification.stopped" ), messageService.getString( "notification.stoppedDetails" ), TrayIcon.MessageType.INFO );
        trayIcon.setImage( createImage( STOPPED_ICON, "Running icon" ) );
    }

    @Override
    public void lifeCycleStopping( LifeCycle arg0 )
    {
        log.info( "Lifecycle: server stopping" );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    /**
     * Returns the URL where the application can be accessed.
     * 
     * @return the URL where the application can be accessed.
     */
    private String getUrl()
    {
        return "http://" + appConfig.getHost() + ":" + appConfig.getPort();
    }

    /**
     * Reads the config file for the path of the preferred browser
     *
     * @return the path to the default browser
     */
    private String defaultPreferredBrowserPath()
    {
        String preferredBrowserPath = appConfig.getPreferredBrowser();

        if ( preferredBrowserPath.length() > 0 )
        {
            try
            {
                log.debug( "Preferred browser path read from config: " + preferredBrowserPath );
                
                boolean browserExists = new File( preferredBrowserPath ).exists();
                if ( !browserExists )
                {
                    preferredBrowserPath = "";
                    log.warn( "Preferred browser could not be found." );
                }    
            } catch ( Exception e )
            {
                log.warn( "There was a problem loading the preferred browser from the config file." );
            }
            
            log.info( "Preferred browser path: " + preferredBrowserPath );
        }
        
        return preferredBrowserPath;
    }

    /**
     * Launches the application in the default browser.
     */
    private void launchBrowser()
    {
        String preferredBrowserPath = defaultPreferredBrowserPath();

        if ( preferredBrowserPath.length() > 0 )
        {
            try
            {   //if the preferred browser has not been defined and appears to be valid
                launchPreferredBrowser();
            } catch ( Exception ex )
            {
                log.warn( "Couldn't open preferred browser. Will attempt to use default browser. " + ex );
            }
        } else
        {
            try
            {
                launchDefaultBrowser();
            } catch ( Exception e )
            {
                log.error( "Could not open any browser" + e );
            }
        }
    }

    /**
     * Launches the application in the custom embedded browser.
     * The path of which is given in the config.xml in the
     * <preferredBrowser> tag
     */
    private void launchPreferredBrowser()
    {
        String preferredBrowserPath =  defaultPreferredBrowserPath();
       
        if ( preferredBrowserPath != null )
        {
            String thisurl = getUrl();
            log.debug( "About to open " + thisurl + " with " + preferredBrowserPath );
            String openPrefBrowser = ( preferredBrowserPath + " " + thisurl );
            //try and launch the prefered browser
            try
            {
                Runtime rt = Runtime.getRuntime();
                rt.exec( openPrefBrowser );
            } catch ( IOException e )
            {
                log.error( "There was a problem opening the preferred browser. " + e );
            }
        }
        else
        {
            launchDefaultBrowser();
        }
    }

    /**
     * Launches the default browser to open the URL
     */
    private void launchDefaultBrowser()
    {
        try
        {
            Desktop.getDesktop().browse( URI.create( getUrl() ) );
        } catch ( IOException e )
        {
            log.error( "The default browser could not be launched" );
        }
    }

    /**
     * Shuts down the web application server.
     */
    private void shutdown()
    {
        log.info( "Graceful shutdown..." );
        try
        {
            appServer.stop();
        } catch ( Exception ex )
        {
            log.warn( "Oops: " + ex.toString() );
        }
        log.info( "Exiting..." );
        System.exit( 0 );
    }

    /**
     * Creates an image based on the given path and description.
     * 
     * @param path the image path.
     * @param description the image description.
     * @return an Image.
     */
    private static Image createImage( String path, String description )
    {
        URL imageURL = TrayApp.class.getResource( path );
        if ( imageURL == null )
        {
            log.warn( "Resource not found: " + path );
            return null;
        } else
        {
            return ( new ImageIcon( imageURL, description ) ).getImage();
        }
    }

    /**
     * The <code>getInstallDir</code> method is a hack to determine the current
     * directory the DHIS 2 Live package is installed in. It does this by
     * finding the file URL of a resource within the executable jar and
     * extracting the installation path from that.
     * 
     * @return a <code>String</code> value representing the installation directory
     */
    public static String getInstallDir()
    {
        // find a resource
        String resourceString = TrayApp.class.getResource( "/icons/" ).toString();
        // we expect to see something of the form:
        // "jar:file:<install_dir>/dhis_xxx.jar!/icons"
        if ( !resourceString.startsWith( "jar:file:" ) )
        {
            // we're in trouble - its not in a jar file
            return null;
        }
        // find the last "/" just before the "!"
        int endIndex = resourceString.lastIndexOf( "/", resourceString.lastIndexOf( "!" ) );
        String result = resourceString.substring( 9, endIndex );
        // replace encoded spaces
        result = result.replaceAll( "%20", " " );
        return result;
    }

    /**
     * Reads the config.xml and creates the Config Object
     * Uses JAXBElement to config back unmarshal the config.xml
     */
    private void readConfigFromStream( InputStream configStream )
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance( "org.hisp.dhis.config" );
            //Create unmarshaller
            Unmarshaller um = jc.createUnmarshaller();
            //Unmarshal XML contents of the file config.xml into your Java object instance.
            configElement = (JAXBElement<ConfigType>) um.unmarshal( configStream );
            config = configElement.getValue();
        } catch ( JAXBException ex )
        {
            log.error( "Error parsing config file", ex );
            JOptionPane.showMessageDialog( null, "Error loading configuration xml", "Configuration Error", JOptionPane.ERROR_MESSAGE );
            System.exit( 1 );
        }
    }

    /**
     * Writes the config.xml based on the changed config by marshalling the config object
     * Uses JAXBElement to write the config back to xml
     */
    public void writeConfigToFile()
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance( "org.hisp.dhis.config" );
            //Create marshaller
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            //Marshal object into file.
            boolean confDirExists = createConfigDirectory();
            m.marshal( configElement, new FileOutputStream( installDir + CONFIG_FILE_NAME ) );
            log.info( "Config Saved at: " + installDir + CONFIG_FILE_NAME );
        } catch ( FileNotFoundException ex )
        {
            log.error( "Can't find configuration xml", ex );
            JOptionPane.showMessageDialog( null, "Can't find configuration xml", "Configuration Error", JOptionPane.ERROR_MESSAGE );
            System.exit( 1 );
        } catch ( JAXBException ex )
        {
            log.error( "Error serializing config to file", ex );
            JOptionPane.showMessageDialog( null, "Error serializing config to file", "Configuration Error", JOptionPane.ERROR_MESSAGE );
            System.exit( 1 );
        }
    }

    /**
     * Writes the hibernate.properties file to the /conf folder based on the selected
     * connection from the config.xml or the defaultConfig.xml
     *
     * @return  Returns whether successful in writing hibernate.properties
     */
    private void writeHibernateProperties()
    {
        String url = ( (Connection) databaseConfig.getDatabaseConnections().getSelected() ).getURL();
        String userName = ( (Connection) databaseConfig.getDatabaseConnections().getSelected() ).getUserName();
        String password = ( (Connection) databaseConfig.getDatabaseConnections().getSelected() ).getPassword();
        String dialect = ( (ConnectionType) ( (Connection) databaseConfig.getDatabaseConnections().getSelected() ).getType() ).getDialect();
        String driver = ( (ConnectionType) ( (Connection) databaseConfig.getDatabaseConnections().getSelected() ).getType() ).getDriverClass();
        Properties props = new Properties();
        try
        {
            props.setProperty( "hibernate.dialect", dialect );
            props.setProperty( "hibernate.connection.driver_class", driver );
            props.setProperty( "hibernate.connection.url", url );
            props.setProperty( "hibernate.connection.username", userName );
            if ( password != null )
            {
                props.setProperty( "hibernate.connection.password", password );
            } else
            {
                props.setProperty( "hibernate.connection.password", "" );
            }
            props.setProperty( "hibernate.hbm2ddl.auto", "update" );
            boolean confDirExists = createConfigDirectory();
            props.store( new FileWriter( System.getProperty( "dhis2.home" ) + "/hibernate.properties" ), "DHIS2 Live Created" );
            log.info( "Hibernate properties written at: " + System.getProperty( "dhis2.home" ) + "/hibernate.properties" );
        } catch ( IOException ex )
        {
            log.error( "Error with Hibernate Properties: " + ex.getMessage() );
            JOptionPane.showMessageDialog( null, "Error with Hibernate Properties: \n" + ex.getMessage(), "Configuration error", JOptionPane.ERROR_MESSAGE );
            System.exit( 1 );
        }
    }
    
    private boolean createConfigDirectory()
    {
        File   fileDirectory = new File(getInstallDir() + "/conf");
        boolean success = fileDirectory.exists();
        if ( !success )
        {
            try
            {
                success = fileDirectory.mkdir();
        
             }
            catch (Exception e)
            {
                log.error ("Could not create config directory");
            }
        }
         return success;
    }
}
