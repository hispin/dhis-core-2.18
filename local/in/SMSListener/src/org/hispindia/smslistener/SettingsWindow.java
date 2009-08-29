package org.hispindia.smslistener;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class SettingsWindow extends javax.swing.JFrame {

    private SMSListener app = new SMSListener();

    public SettingsWindow() {
        initComponents();
        portNumField.setDocument(new IntDocument());
        portNumField.setText(Integer.toString(app.getPort()));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveCmd = new javax.swing.JButton();
        portNumLabel = new javax.swing.JLabel();
        portNumField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SMS Listener Settings");
        setLocationByPlatform(true);
        setResizable(false);

        saveCmd.setText("Save & Close");
        saveCmd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCmdActionPerformed(evt);
            }
        });

        portNumLabel.setText("Port Number (without COM):");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(portNumLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portNumField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(saveCmd, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portNumLabel)
                    .addComponent(portNumField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 227, Short.MAX_VALUE)
                .addComponent(saveCmd)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveCmdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCmdActionPerformed
        app.setPort(Integer.parseInt(portNumField.getText()));
        this.dispose();
    }//GEN-LAST:event_saveCmdActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SettingsWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField portNumField;
    private javax.swing.JLabel portNumLabel;
    private javax.swing.JButton saveCmd;
    // End of variables declaration//GEN-END:variables
}

class IntDocument extends PlainDocument {

    @Override
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        Toolkit tk = Toolkit.getDefaultToolkit();
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                tk.beep();
                return;
            } else if (getLength() >= 3) {
                tk.beep();
                return;
            } else {
                super.insertString(offset, str, a);
            }
        }
    }
}
