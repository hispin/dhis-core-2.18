/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URL;
import javax.swing.*;
import java.io.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.component.LifeCycle;

/**
 * @author Bob Jolliffe
 */
public class TrayApp
        implements LifeCycle.Listener {

    private static final Log log = LogFactory.getLog(TrayApp.class);

    private static final String CONFIG_DIR = "/conf";

    private static final String STOPPED_ICON = "/icons/stopped.png";

    private static final String STARTING_ICON = "/icons/starting.png";

    private static final String FAILED_ICON = "/icons/failed.png";

    private static final String RUNNING_ICON = "/icons/running.png";

    private static final String CMD_OPEN = "Open DHIS 2 Live";

    private static final String CMD_EXIT = "Exit";

    private static final String CONFIG_FILE = "conf/dhis2live.cfg";

    private WebAppServer appServer;

    private TrayIcon trayIcon;

    private String installDir;

    private SimpleConfigReader configReader;

    // -------------------------------------------------------------------------
    // Main method
    // -------------------------------------------------------------------------
    public static void main( String[] args )
             {
        log.info( "Environment variable DHIS2_HOME: " + System.getenv( "DHIS2_HOME" ) );
        if ( !SystemTray.isSupported() )
        {
            JOptionPane.showMessageDialog((JFrame) null, "SystemTray not supported on this platform");
            System.exit(0);
        }
        else
        {
                try
        {
            new TrayApp();
        } catch ( Exception ex )
        {
            log.fatal( "TrayApp Initialization failure", ex );
            JOptionPane.showMessageDialog( (JFrame) null, "DHIS2-live failed to initialize\nSee log for details" );
            System.exit( 0 );
        }
     }
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public TrayApp() throws AWTException, InterruptedException
    {
        log.info("Initialising DHIS 2 Live...");

        installDir = getInstallDir();

        if (installDir == null )
        {
            installDir = System.getenv( "DHIS2_HOME" );
            if ( installDir == null )
                {
                   log.fatal( "Neither DHIS Live Jar nor DHIS2_HOME could be found." );
                   JOptionPane.showMessageDialog( (JFrame) null, "DHIS2-live failed to initialize\nSee log for details" );
                   System.exit( 0 );
                }
            else
                {

            log.info("jar not installed, setting installdir to DHIS2_HOME: " + System.getenv( "DHIS2_HOME" ) );
            installDir = System.getenv( "DHIS2_HOME" );
                }
        }

        System.setProperty( "dhis2.home", installDir + CONFIG_DIR);
        System.setProperty( "jetty.home", installDir);

        System.setProperty( "birt.home", installDir + WebAppServer.BIRT_DIR);
        System.setProperty( "birt.context.path", WebAppServer.BIRT_CONTEXT_PATH);


        SystemTray tray = SystemTray.getSystemTray();

        Image image = createImage( STOPPED_ICON, "tray icon");

        PopupMenu popup = new PopupMenu();
        MenuItem openItem = new MenuItem( CMD_OPEN );
        MenuItem exitItem = new MenuItem( CMD_EXIT );
        popup.add( openItem );
        popup.add( exitItem );

        trayIcon = new TrayIcon( image, "DHIS 2 Live", popup );
        trayIcon.setImageAutoSize( true );

        ActionListener listener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();

                if (cmd.equals( CMD_OPEN )) {
                    launchBrowser();

                } else if (cmd.equals( CMD_EXIT )) {
                    shutdown();
                }
            }

            ;
        };

        openItem.addActionListener( listener );
        exitItem.addActionListener( listener );
        tray.add( trayIcon );

        appServer = new WebAppServer();
        try
        {
        appServer.init( installDir, this );
        }
        catch (Exception e)
            {
            log.fatal( "Application server could not be initialized" );
        }     
        try
        {
        appServer.start();
        }
        catch (Exception e)
        {
            log.fatal ( "Application server could not be started" );
        }
    }

    // -------------------------------------------------------------------------
    // Listener implementation
    // -------------------------------------------------------------------------
    public void lifeCycleFailure( LifeCycle arg0, Throwable arg1 )
    {
        log.warn( "Lifecycle: server failed" );
        trayIcon.setImage(createImage( FAILED_ICON, "Running icon" ) );
        String message = "Web server failed to start - see logs for details";
        JOptionPane.showMessageDialog( (JFrame) null, message );
        shutdown();
    }

    public void lifeCycleStarted(LifeCycle arg0)
    {
        log.info("Lifecycle: server started");
        trayIcon.displayMessage( "Started", "DHIS 2 is running. Your browser will\n be pointed to " + getUrl() + ".",
                TrayIcon.MessageType.INFO );
        trayIcon.setToolTip( "DHIS 2 Server running" );
        trayIcon.setImage(createImage( RUNNING_ICON, "Running icon" ) );
        launchBrowser();

    }

    public void lifeCycleStarting(LifeCycle arg0) {
        log.info("Lifecycle: server starting");
        trayIcon.displayMessage("Starting", "DHIS 2 is starting.\nPlease be patient.", TrayIcon.MessageType.INFO);
        trayIcon.setImage(createImage(STARTING_ICON, "Starting icon"));
    }

    public void lifeCycleStopped(LifeCycle arg0) {
        log.info("Lifecycle: server stopped");
        trayIcon.displayMessage("Stopped", "DHIS 2 has stopped.", TrayIcon.MessageType.INFO);
        trayIcon.setImage(createImage(STOPPED_ICON, "Running icon"));
    }

    public void lifeCycleStopping(LifeCycle arg0) {
        log.info("Lifecycle: server stopping");
    }

    private String defaultPreferredBrowserPath() {
        //initialize a return variable.false denotes failure.  true success
        configReader = new SimpleConfigReader();
        String preferredBrowserPath = null;
        try
        {
            preferredBrowserPath = configReader.preferredBrowserPath();
            log.info( "Config reports browser path to be" + preferredBrowserPath );
            boolean browserIsValid = new File( preferredBrowserPath ).exists();
            if ( !browserIsValid )
            {
                preferredBrowserPath = null;
                log.warn( "Browser does not appear to be valid.Please check that the browser exists." );
            }

        } catch (Exception e) {
            log.warn("There was a problem reading the preferred browser from the config file.");
        }
        log.info( "Preferred browser path reported to be " + preferredBrowserPath);
        return preferredBrowserPath;
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
        return "http://localhost:" + appServer.getConnectorPort();
    }

    /**
     * Launches the application in the default browser.
     */
    private void launchBrowser() {


            String preferredBrowserPath = defaultPreferredBrowserPath();
            
            if (preferredBrowserPath != null)
            {
                try {   //if the preferred browser has not been defined and appears to be valid
                    launchPreferredBrowser();
                } 
                catch (Exception ex)
                {
                    log.warn ("Couldn't open preferred browser.Will attempt to revert to default. " + ex);
                }
            }
            else {
            try {
                launchDefaultBrowser();
            } catch (Exception e) {
                log.error("Could not open any browser" + e);
            }
        }

    }

    /**
     * Launches the application in the custom embedded browser.
     */
    private void launchPreferredBrowser()  {   //initialize a return variable.false denotes failure.  true success
        try {
            String preferredBrowserPath = defaultPreferredBrowserPath();
            String thisurl = getUrl();
            log.info("About to open " + thisurl + " with " + preferredBrowserPath);
            String openPrefBrowser = (preferredBrowserPath + " " + thisurl);

            if (preferredBrowserPath != null && thisurl != null ) {

                //try and launch the prefered browser
                try
                {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec(openPrefBrowser);
                } catch (IOException e) {
                    log.error("There was a problem opening the preferred browser. " + e);
                    //Try and fall back to the default browser
                    launchDefaultBrowser();
                }
            }
        } catch (Exception ex) {
            log.error("An error occurred while attempting to open the preferred browser " + ex);
            //Try and fall back to the default browser
            launchDefaultBrowser();
        }
    }

    private void launchDefaultBrowser() {
        try {
            Desktop.getDesktop().browse( URI.create( getUrl() ) );
        } catch (IOException e) {
            log.error( "The default browser could not be launched" );
        }
    }

    /**
     * Shuts down the web application server.
     */
    private void shutdown()
    {
        log.info("Graceful shutdown...");
        try
        {
            appServer.stop();
        }
        catch (Exception ex) {
            log.warn ("Oops: " + ex.toString() );
        }
        log.info("Exiting...");
        System.exit(0);
    }

    /**
     * Creates an image based on the given path and description.
     * 
     * @param path the image path.
     * @param description the image description.
     * @return an Image.
     */
    private static Image createImage(String path, String description)
    {
        URL imageURL = TrayApp.class.getResource(path);
        if (imageURL == null) {
            log.warn( "Resource not found: " + path );
            return null;
        } 
        else
        {
            return ( new ImageIcon(imageURL, description) ).getImage();
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
    private static String getInstallDir()
    {
        // find a resource
        String resourceString = TrayApp.class.getResource("/icons/").toString();
        // we expect to see something of the form:
        // "jar:file:<install_dir>/dhis_xxx.jar!/icons"
        if (!resourceString.startsWith("jar:file:"))
        {
            // we're in trouble - its not in a jar file
            return null;
        }
        // find the last "/" just before the "!"
        int endIndex = resourceString.lastIndexOf("/", resourceString.lastIndexOf("!"));
        String result = resourceString.substring(9, endIndex);
        // replace encoded spaces
        result = result.replaceAll( "%20", " " );
        return result;
    }
}
