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
import java.net.URL;
import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.component.LifeCycle;

/**
 * Describe class <code>TrayApp</code> here.
 *
 * @author <a href="mailto:bobj@bobjolliffe@gmail.com">Bob Jolliffe</a>
 * @version $$Id$$
 * @version 1.0
 */
public class TrayApp
  implements LifeCycle.Listener
{
  private static final Log log = LogFactory.getLog( TrayApp.class );
  private static final String CONFIG_DIR = "/conf";
  
  /**
   * Describe variable <code>appServer</code> here.
   *
   */
  protected WebAppServer appServer;

  /**
   * Describe variable <code>trayIcon</code> here.
   *
   */
  protected TrayIcon trayIcon;

  /**
   * Describe variable <code>installDir</code> here.
   *
   */
  private String installDir;

  /**
   * Creates a new <code>TrayApp</code> instance.
   *
   * @exception Exception if an error occurs
   */
  public TrayApp()
    throws Exception
  {
    installDir = System.getProperty("user.dir");
   
    System.setProperty("dhis2.home", installDir + CONFIG_DIR);
    System.setProperty("jetty.home", installDir);
    
    System.setProperty("birt.home", installDir + WebAppServer.BIRT_DIR);
    System.setProperty("birt.context.path", WebAppServer.BIRT_CONTEXT_PATH);

    SystemTray tray = SystemTray.getSystemTray();

    Image image = createImage("/icons/stopped.png", "tray icon");

    PopupMenu popup = new PopupMenu();
    MenuItem defaultItem = new MenuItem("Exit");
    popup.add(defaultItem);

    trayIcon = new TrayIcon(image,"DHIS2 Lite", popup);
    trayIcon.setImageAutoSize(true);

    ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String cmd = e.getActionCommand();
          
          if (cmd.equals("Exit"))
          {
            shutdown();
          }
        };
      };

    defaultItem.addActionListener(listener);

    try {
        tray.add(trayIcon);
    } catch (AWTException ex) {
        log.info("Oops: "+ex.toString());
    }

    appServer = new WebAppServer();
    appServer.init(installDir,this);
    try {
        appServer.start();
    } catch (Exception ex) {
        String message = "Web server failed to start: \n"+ex.toString();
        JOptionPane.showMessageDialog((JFrame)null, message);
    }
  }

  /**
   * Describe <code>shutdown</code> method here.
   *
   */
  void shutdown()
  {
    log.info("Graceful shutdown...");
    try {
      appServer.stop();
    }
    catch (Exception ex) {
      log.warn("Oops: "+ex.toString());
    }
    log.debug("Exiting...");
    System.exit(0);
  }

  /**
   * Describe <code>lifeCycleFailure</code> method here.
   *
   * @param arg0 a <code>LifeCycle</code> value
   * @param arg1 a <code>Throwable</code> value
   */
  public void lifeCycleFailure(LifeCycle arg0, Throwable arg1) {
    log.warn("Lifecycle: server failed");
    trayIcon.setImage(createImage("/icons/failed.png", "Running icon"));
    String message = "Web server failed to start - see logs for details";
    JOptionPane.showMessageDialog((JFrame)null, message);
    shutdown();
  }

  /**
   * Describe <code>lifeCycleStarted</code> method here.
   *
   * @param arg0 a <code>LifeCycle</code> value
   */
  public void lifeCycleStarted(LifeCycle arg0) {
    log.info("Lifecycle: server started");
    String url = "http://localhost:" + appServer.getConnectorPort();
    trayIcon.displayMessage("Started","DHIS2 is running. Point your\nbrowser to " + url,TrayIcon.MessageType.INFO);
    trayIcon.setToolTip("DHIS 2 Server running");
    trayIcon.setImage(createImage("/icons/running.png", "Running icon"));
  }

  /**
   * Describe <code>lifeCycleStarting</code> method here.
   *
   * @param arg0 a <code>LifeCycle</code> value
   */
  public void lifeCycleStarting(LifeCycle arg0) {
    log.info("Lifecycle: server starting");
    trayIcon.displayMessage("Starting","DHIS 2 is starting.\nPlease be patient.",TrayIcon.MessageType.INFO);
    trayIcon.setImage(createImage("/icons/starting.gif", "Starting icon"));
  }

  /**
   * Describe <code>lifeCycleStopped</code> method here.
   *
   * @param arg0 a <code>LifeCycle</code> value
   */
  public void lifeCycleStopped(LifeCycle arg0) {
    log.info("Lifecycle: server stopped");
    trayIcon.setImage(createImage("/icons/stopped.png", "Running icon"));
  }

  /**
   * Describe <code>lifeCycleStopping</code> method here.
   *
   * @param arg0 a <code>LifeCycle</code> value
   */
  public void lifeCycleStopping(LifeCycle arg0) {
    log.info("Lifecycle: server stopping");
  }

  /**
   * Describe <code>createImage</code> method here.
   *
   * @param path a <code>String</code> value
   * @param description a <code>String</code> value
   * @return an <code>Image</code> value
   */
  protected static Image createImage(String path, String description) {
    URL imageURL = TrayApp.class.getResource(path);

    if (imageURL == null) {
      log.warn("Resource not found: " + path);
      return null;
    } else {
      return (new ImageIcon(imageURL, description)).getImage();
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
    throws Exception
  {
    log.info("Environment variable DHIS2_HOME: "+System.getenv("DHIS2_HOME"));
    log.info("System property user.dir: "+System.getProperty("user.dir"));
    if(!SystemTray.isSupported()) {
      String message = "SystemTray not supported on this platform";
      JOptionPane.showMessageDialog((JFrame)null, message);
      System.exit(0);
    }
    
    // ok - we're good to go ...
    new TrayApp();
  }
}
