package org.hispindia.smslistener;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.smslib.AGateway.Protocols;
import org.smslib.GatewayException;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundBinaryMessage;
import org.smslib.InboundMessage;
import org.smslib.Message.MessageTypes;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

public class SMSListener {

    private Service service;
    private InboundNotification inboundNotification;
    private int PORT = 8; //default port
    private File infFile = new File(System.getProperty("user.home") + "/.smslistener", "SMSListener.inf");
    SerialModemGateway gateway;

    public SMSListener() {
    }

    public class InboundNotification implements IInboundMessageNotification {

        public void process(String gatewayId, MessageTypes msgType, InboundMessage msg) {
            if (msgType == MessageTypes.INBOUND) {
                System.out.println(">>> New Inbound message detected from Gateway: " + gatewayId);
                processMessage(msg);
            } else if (msgType == MessageTypes.STATUSREPORT) {
                System.out.println(">>> New Inbound Status Report message detected from Gateway: " + gatewayId);
            }
            try {
                service.deleteMessage(msg);
            } catch (Exception e) {
                System.out.println("Oops!!! Something gone bad...");
                e.printStackTrace();
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc=" Show Tray Icon ">
    private void showTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            PopupMenu menu = new PopupMenu();

            //<editor-fold defaultstate="collapsed" desc=" Exit Item ">
            final MenuItem exitItem = new MenuItem("Exit DHIS SMS Listener");
            exitItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    System.exit(0);
                }
            });
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc=" Service Item ">
            final MenuItem serviceItem = new MenuItem("Start SMS Listening");
            serviceItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    if (serviceItem.getLabel().equals("Start SMS Listening")) {
                        startSMSListener();
                        serviceItem.setLabel("Stop SMS Listening");
                    } else {
                        stopSMSListener();
                        serviceItem.setLabel("Start SMS Listening");
                    }
                }
            });
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc=" Settings Item ">
            final MenuItem settingsItem = new MenuItem("Settings");
            settingsItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    new SettingsWindow().setVisible(true);
                }
            });
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc=" About Item ">
            final MenuItem aboutItem = new MenuItem("About");
            aboutItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    new AboutWindow().setVisible(true);
                }
            });
            //</editor-fold>

            menu.add(serviceItem);
            menu.add(settingsItem);
            menu.add(aboutItem);
            menu.add(exitItem);

            Image img = new ImageIcon(getClass().getResource("images/phone.png")).getImage();
            TrayIcon trayIcon = new TrayIcon(img, "DHIS SMS Listener");
            trayIcon.setPopupMenu(menu);
            try {
                tray.add(trayIcon);
            } catch (AWTException ex) {
                Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "System Tray Error",
                    "Your platform does not support tray/notification area. Exiting...",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Start Service ">
    private void startSMSListener() {
        service = new Service();
        inboundNotification = new InboundNotification();
        gateway = new SerialModemGateway("modem.com" + getPort(), "COM" + getPort(), 115200, "Generic USB", "generic-usb-modem");
        gateway.setProtocol(Protocols.PDU);
        gateway.setInbound(true);
        gateway.setOutbound(false);
        service.setInboundNotification(inboundNotification);
        try {
            service.addGateway(gateway);
            System.out.println("Starting Service on Com:" + getPort());
            service.startService();
        } catch (SMSLibException ex) {
            JOptionPane.showMessageDialog(null, "SMSLib Exception: " + ex.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "I/O Exception: " + ex.getMessage());
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(null, "Thread Exception: " + ex.getMessage());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Stop Service ">
    private void stopSMSListener() {
        try {
            System.out.println("Stopping Service on Com:" + getPort());
            gateway.stopGateway();
            service.stopService();
        } catch (TimeoutException ex) {
            Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GatewayException ex) {
            Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>

    private void processMessage(InboundMessage message) {

        try {
            InboundBinaryMessage binaryMsg = (InboundBinaryMessage) message;
            byte[] compressedData = binaryMsg.getDataBytes();
            String unCompressedText = new String(Compressor.decompress(compressedData), "UTF-8");
            XMLCreator xmlcreator = new XMLCreator();
            xmlcreator.writeXML(message.getOriginator(), new Timestamp(message.getDate().getTime()).toString(), unCompressedText);
        } catch (UnsupportedEncodingException uneex) {
            JOptionPane.showMessageDialog(null, "Message Decryption Error: " + uneex.getMessage());
            return;
        } catch (ClassCastException ccex) {
            return;
        } catch (ArithmeticException aex) {
            return;
        }
    }

    public void setPort(int portNumber) {
        try {
            Properties prop = new Properties();
            if (infFile.exists()) {
                FileOutputStream fos = new FileOutputStream(infFile);
                prop.setProperty("com.port", Integer.toString(portNumber));
                prop.store(fos, "COM Port Property");
                fos.close();
            } else {
                JOptionPane.showMessageDialog(null, "Properties File Not Found at: " + infFile.getAbsolutePath());
            }
        } catch (FileNotFoundException fnfex) {
            JOptionPane.showMessageDialog(null, "Properties File Exception: " + fnfex.getMessage());
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(null, "Properties I/O Exception: " + ioex.getMessage());
        } catch (NumberFormatException nfex) {
            JOptionPane.showMessageDialog(null, "Properties Port Number Exception: " + nfex.getMessage());
        } catch (SecurityException nfex) {
            JOptionPane.showMessageDialog(null, "Properties File Exception: " + nfex.getMessage());
        }
    }

    public int getPort() {
        try {
            Properties prop = new Properties();
            if (infFile.exists()) {
                FileInputStream fis = new FileInputStream(infFile);
                prop.load(fis);
                this.PORT = Integer.parseInt(prop.getProperty("com.port"));
                fis.close();
            } else {
                JOptionPane.showMessageDialog(null, "Properties File Not Found at: " + infFile.getAbsolutePath());
            }
        } catch (FileNotFoundException fnfex) {
            JOptionPane.showMessageDialog(null, "Properties File Exception: " + fnfex.getMessage());
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(null, "Properties I/O Exception: " + ioex.getMessage());
        } catch (NumberFormatException nfex) {
            JOptionPane.showMessageDialog(null, "Properties Port Number Exception: " + nfex.getMessage());
        } catch (SecurityException nfex) {
            JOptionPane.showMessageDialog(null, "Properties File Exception: " + nfex.getMessage());
        }
        return this.PORT;
    }

    public static void main(String args[]) {
        SMSListener app = new SMSListener();
        app.showTrayIcon();

        if (app.infFile.exists()) {
            app.getPort();
        } else {
            try {
                if (!app.infFile.getParentFile().exists()) {
                    app.infFile.getParentFile().mkdir();
                }
                app.infFile.createNewFile();
                app.setPort(app.PORT);
            } catch (IOException ioex) {
                JOptionPane.showMessageDialog(null, "Properties I/O Exception: " + ioex.getMessage());
                System.exit(1);
            }
        }
    }
}
