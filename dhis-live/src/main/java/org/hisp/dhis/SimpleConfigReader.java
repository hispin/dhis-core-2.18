/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis;
import java.util.Properties;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jason P. Pickering
 */
 class SimpleConfigReader
 {

    private static final String CONFIG_FILE = "conf/dhis2live.cfg";
    private static final int DEFAULT_JETTY_PORT = 8080;
    private static final String PREFERRED_BROWSER_PROPERTY = "preferredBrowser";
    private static final String JETTY_PORT_PROPERTY = "jettyPort";
    private static final Log log = LogFactory.getLog( SimpleConfigReader.class );
    
    protected Properties getDefaultProperties()
    {
        FileInputStream configInputStream;
        Properties defaultProps = new Properties();
        try {
            configInputStream = new FileInputStream( CONFIG_FILE );
            defaultProps.load( configInputStream );
            configInputStream.close();
        } catch (FileNotFoundException e)
        {
            log.info ("No properties file found.");
        } catch ( IOException ex)
        {
            log.error("There was an input/output error while loading the properties file.");
        }
        return defaultProps;
    }
protected String preferredBrowserPath ()
    {
        String thisBrowserPath = null;
   if (getDefaultProperties().containsKey( PREFERRED_BROWSER_PROPERTY ) )
    try
    {
        thisBrowserPath = getDefaultProperties().getProperty( PREFERRED_BROWSER_PROPERTY );
         log.info("Browser path reported as" + thisBrowserPath);
    }
    catch (Exception e)
        {
        log.error ("Could not load preferred browser property");
        thisBrowserPath = null;
        }
        return thisBrowserPath;
    }

    protected int preferredJettyPort()
    {
        int preferredJettyPort = WebAppServer.DEFAULT_JETTY_PORT;
        if ( getDefaultProperties().containsKey( JETTY_PORT_PROPERTY ) )
        {
            try
            {
                preferredJettyPort = Integer.parseInt( getDefaultProperties().getProperty( JETTY_PORT_PROPERTY ) );
            } catch ( NumberFormatException e )
            {
                log.error( "Port is not in the specified format.Using default." );
                preferredJettyPort = WebAppServer.DEFAULT_JETTY_PORT;
            }
         }
    log.info ("Preferred jetty port will be configured to be " + preferredJettyPort);

return preferredJettyPort;
     }

}
