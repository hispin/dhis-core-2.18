package org.hispindia.smslistener;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.smslib.AGateway.Protocols;
import org.smslib.GatewayException;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Message.MessageTypes;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

public class SMSListener {

    private Service service;
    private InboundNotification inboundNotification;

    public SMSListener() {
        showTrayIcon();

        service = new Service();
        System.out.println("#######Service Created Successfully");
        System.out.println("Listening on port: COM" + SettingsWindow.portNumber);
        inboundNotification = new InboundNotification();
        SerialModemGateway gateway = new SerialModemGateway("modem.com" + SettingsWindow.portNumber, "COM" + SettingsWindow.portNumber, 115200, "Generic USB", "generic-usb-modem");
        System.out.println("#######Gateway Created Successfully");
        gateway.setProtocol(Protocols.PDU);
        gateway.setInbound(true);
        gateway.setOutbound(false);
        service.setInboundNotification(inboundNotification);
        service.addGateway(gateway);
        System.out.println("#######Gateway Added to Service");
    }

    public class InboundNotification implements IInboundMessageNotification {

        public void process(String gatewayId, MessageTypes msgType, InboundMessage msg) {
            if (msgType == MessageTypes.INBOUND) {
                System.out.println(">>> New Inbound message detected from Gateway: " + gatewayId);
            } else if (msgType == MessageTypes.STATUSREPORT) {
                System.out.println(">>> New Inbound Status Report message detected from Gateway: " + gatewayId);
            }
            processMessage(msg);
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
            final MenuItem exitItem = new MenuItem("Exit DHIS SMS Listener");
            exitItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    System.out.println("#######Exit Item pressed");
                    System.exit(0);
                }
            });
            final MenuItem serviceItem = new MenuItem("Start SMS Listening");
            serviceItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    if (serviceItem.getLabel().equals("Start SMS Listening")) {
                        System.out.println("#######Start Service Pressed");
                        startSMSListener();
                        serviceItem.setLabel("Stop SMS Listening");
                    } else {
                        System.out.println("#######Stop Service Pressed");
                        stopSMSListener();
                        serviceItem.setLabel("Start SMS Listening");
                    }
                }
            });
            final MenuItem settingsItem = new MenuItem("Settings");
            settingsItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    new SettingsWindow().setVisible(true);
                }
            });

            menu.add(serviceItem);
            menu.add(settingsItem);
            menu.add(exitItem);

            Image img = new ImageIcon(getClass().getResource("phone.png")).getImage();
            TrayIcon trayIcon = new TrayIcon(img, "DHIS SMS Listener");
            trayIcon.setPopupMenu(menu);
            try {
                tray.add(trayIcon);
            } catch (AWTException ex) {
                Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "System Tray Error",
                    "Your system does not support tray/notification area. Exiting...",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Start Service ">
    private void startSMSListener() {
        try {
            System.out.println("Starting Service on Com:"+SettingsWindow.portNumber);
            service.startService();
        } catch (SMSLibException ex) {
            Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SMSListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Stop Service ">
    private void stopSMSListener() {
        try {
            System.out.println("Stopping Service on Com:"+SettingsWindow.portNumber);
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
        //System.out.println("PDU HEX String:" + message.getPduUserData());
        //System.out.println("PDU HEX String#:" + message.getPduUserData().length());
        InboundMessage textMsg = (InboundMessage) message;
        String compressedText = textMsg.getText();
        System.out.println("Compressed text: " + compressedText);

        //byte[] compressedData = compressedText.getBytes("UTF-8");
        //String unCompressedText = new String(Compressor.decompress(compressedData), "UTF-8");
        System.out.println("Uncompressed text: " + compressedText);
        XMLCreator xmlcreator = new XMLCreator();
        xmlcreator.writeXML(message.getOriginator(), new Timestamp(message.getDate().getTime()).toString(), compressedText);
    }

    public static void main(String args[]) {
        SMSListener obj = new SMSListener();
    }
}