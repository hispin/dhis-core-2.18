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


import java.awt.Component;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Vector;
import java.util.jar.JarFile;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import org.hisp.dhis.config.ConfigType.DatabaseConfiguration.ConnectionTypes.ConnectionType;
import org.hisp.dhis.config.ConfigType.DatabaseConfiguration.DatabaseConnections.Connection;

public class SettingsWindow extends JFrame
{

    private static final LiveMessagingService messageService = new LiveMessagingService();

    private int selectedLang;

    private Vector countryVect = new Vector();

    private JComboBox connTypesCombo;

    public SettingsWindow()
    {
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( Exception ex )
        {
            JOptionPane.showMessageDialog( null, ex.getMessage() );
        }
        Vector connTypesVect = new Vector( TrayApp.databaseConfig.getConnectionTypes().getConnectionType() );
        connTypesCombo = new JComboBox( connTypesVect );
        connTypesCombo.setRenderer( new ConnTypesComboRenderer() );
        initComponents();
        langCombo.setModel( new DefaultComboBoxModel( getSupportedLanguages() ) );
        langCombo.setSelectedIndex( selectedLang );
        setLocationRelativeTo( null );
    }

    class ConnTypesComboRenderer extends BasicComboBoxRenderer
    {

        @Override
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus )
        {
            super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            if ( value != null )
            {
                ConnectionType item = (ConnectionType) value;
                setText( item.getId() );
            }
            if ( index == -1 )
            {
                ConnectionType item = (ConnectionType) value;
                setText((value == null) ? "" : item.getId());
            }
            return this;
        }
    }

    class ConnTypesCellRenderer extends DefaultTableCellRenderer
    {
        @Override
        public void setValue( Object value )
        {
            ConnectionType item = (ConnectionType) value;
            setText((value == null) ? "" : item.getId());
        }
    }

    private String getJarfileName()
    {
        // Get the location of the jar file and the jar file name
        URL outputURL = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        String[] loc = outputURL.toString().split( "\\/" );
        String jarFilename;
        if ( outputURL.toString().contains( ".jar" ) )
        {
            jarFilename = TrayApp.getInstallDir() + "/" + loc[loc.length - 1];
        } else
        {
            String outputString = outputURL.toString();
            String[] parseString;
            int index1 = outputString.indexOf( ":" );
            int index2 = outputString.lastIndexOf( ":" );
            if ( index1 != index2 ) // Windows/DOS uses C: naming convention
            {
                parseString = outputString.split( "file:/" );
            } else
            {
                parseString = outputString.split( "file:" );
            }
            jarFilename = parseString[1];
        }
        return jarFilename;
    }

    private Vector getSupportedLanguages()
    {
        Vector supportedLanguages = new Vector();
        try
        {
            File file = new File( getJarfileName() );
            if ( file.exists() )
            {
                if ( file.getName().contains( ".jar" ) )
                {
                    JarFile jarFile = new JarFile( file );
                    Enumeration<JarEntry> entries = jarFile.entries();
                    int i = 0;
                    while ( entries.hasMoreElements() )
                    {
                        JarEntry entry = (JarEntry) entries.nextElement();
                        if ( entry.getName().startsWith( "messages" ) && entry.getName().endsWith( ".properties" ) )
                        {
                            String entryName = entry.getName();
                            String lang = entryName.substring( 18, entryName.length() - 11 );
                            String arr[] = lang.split( "\\_" );
                            if ( arr.length > 1 )
                            {
                                countryVect.add( lang );
                                if ( TrayApp.appConfig.getLocaleLanguage().equals( arr[0] ) )
                                {
                                    selectedLang = i;
                                }
                                supportedLanguages.add( arr[0] );
                            } else
                            {
                                if ( TrayApp.appConfig.getLocaleLanguage().equals( lang ) )
                                {
                                    selectedLang = i;
                                }
                                supportedLanguages.add( lang );
                            }
                            i++;
                        }
                    }
                } else
                {
                    File dir = new File( file, "messages" );
                    String[] list = dir.list( new FilenameFilter()
                    {

                        @Override
                        public boolean accept( File dir, String name )
                        {
                            String lowercase = name.toLowerCase();
                            if ( lowercase.startsWith( "messages" ) && lowercase.endsWith( ".properties" ) )
                            {
                                return true;
                            } else
                            {
                                return false;
                            }
                        }
                    } );
                    for ( int i = 0; i < list.length; i++ )
                    {
                        String lang = list[i].substring( 9, list[i].length() - 11 );
                        String arr[] = lang.split( "\\_" );
                        if ( arr.length > 1 )
                        {
                            countryVect.add( lang );
                            if ( TrayApp.appConfig.getLocaleLanguage().equals( arr[0] ) )
                            {
                                selectedLang = i;
                            }
                            supportedLanguages.add( arr[0] );
                        } else
                        {
                            if ( TrayApp.appConfig.getLocaleLanguage().equals( lang ) )
                            {
                                selectedLang = i;
                            }
                            supportedLanguages.add( lang );
                        }
                    }
                }
            }
        } catch ( IOException ex )
        {
            ex.printStackTrace();
        }
        return supportedLanguages;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        config = TrayApp.config;
        appConfigPanel = new javax.swing.JPanel();
        portLabel = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();
        hostLabel = new javax.swing.JLabel();
        hostField = new javax.swing.JTextField();
        browserPathLabel = new javax.swing.JLabel();
        browserPathField = new javax.swing.JTextField();
        browserPathButton = new javax.swing.JButton();
        langLabel = new javax.swing.JLabel();
        countryLabel = new javax.swing.JLabel();
        maxSizeLabel = new javax.swing.JLabel();
        maxSizeField = new javax.swing.JTextField();
        maxSizeDefaultLabel = new javax.swing.JLabel();
        unitLabel = new javax.swing.JLabel();
        langCombo = new javax.swing.JComboBox();
        countryCombo = new javax.swing.JComboBox();
        databaseConfigPanel = new javax.swing.JPanel();
        connTypePanel = new javax.swing.JPanel();
        connTypePane = new javax.swing.JScrollPane();
        connTypeTable = new javax.swing.JTable();
        connTypeAddButton = new javax.swing.JButton();
        connTypeDelButton = new javax.swing.JButton();
        connPanel = new javax.swing.JPanel();
        connPane = new javax.swing.JScrollPane();
        connTable = new javax.swing.JTable();
        connAddButton = new javax.swing.JButton();
        connDelButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(messageService.getString("settings.windowtitle"));
        setAlwaysOnTop(true);
        setResizable(false);

        appConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(messageService.getString("settings.appconfig")));

        portLabel.setText(messageService.getString("settings.port"));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.port}"), portField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"), "portBinding");
        bindingGroup.addBinding(binding);

        hostLabel.setText(messageService.getString("settings.host"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.host}"), hostField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"), "hostBinding");
        bindingGroup.addBinding(binding);

        browserPathLabel.setText(messageService.getString("settings.browserpath"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.preferredBrowser}"), browserPathField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"), "browserPathBinding");
        bindingGroup.addBinding(binding);

        browserPathButton.setText(messageService.getString("settings.browse"));
        browserPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browserPathButtonActionPerformed(evt);
            }
        });

        langLabel.setText(messageService.getString("settings.language"));

        countryLabel.setText(messageService.getString("settings.country"));

        maxSizeLabel.setText(messageService.getString("settings.maxformsize"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.maxFormContentSize}"), maxSizeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        maxSizeDefaultLabel.setText(messageService.getString("settings.restartinfo"));

        unitLabel.setText(messageService.getString("settings.bytes"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.localeLanguage}"), langCombo, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        langCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                langComboActionPerformed(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.localeCountry}"), countryCombo, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout appConfigPanelLayout = new javax.swing.GroupLayout(appConfigPanel);
        appConfigPanel.setLayout(appConfigPanelLayout);
        appConfigPanelLayout.setHorizontalGroup(
            appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addComponent(browserPathLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browserPathField, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(browserPathButton)
                        .addGap(8, 8, 8))
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, appConfigPanelLayout.createSequentialGroup()
                                .addComponent(langLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(langCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, appConfigPanelLayout.createSequentialGroup()
                                .addComponent(hostLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(countryLabel)
                            .addComponent(portLabel))
                        .addGap(23, 23, 23)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(countryCombo, 0, 0, Short.MAX_VALUE)
                            .addComponent(portField, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxSizeDefaultLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addGroup(appConfigPanelLayout.createSequentialGroup()
                                .addComponent(maxSizeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(unitLabel)))
                        .addContainerGap())))
        );
        appConfigPanelLayout.setVerticalGroup(
            appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appConfigPanelLayout.createSequentialGroup()
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxSizeLabel)
                    .addComponent(hostLabel)
                    .addComponent(portLabel)
                    .addComponent(unitLabel)
                    .addComponent(maxSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(maxSizeDefaultLabel))
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(langLabel)
                            .addComponent(countryLabel)
                            .addComponent(langCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(countryCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browserPathLabel)
                    .addComponent(browserPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browserPathButton))
                .addContainerGap())
        );

        databaseConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(messageService.getString("settings.dbconfig")));

        connTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(messageService.getString("settings.conntypes")));

        connTypeTable.setBackground(new java.awt.Color(212, 208, 200));
        connTypeTable.setEnabled(false);
        connTypeTable.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${databaseConfiguration.connectionTypes.connectionType}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, eLProperty, connTypeTable);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("Id");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${dialect}"));
        columnBinding.setColumnName("Dialect");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${driverClass}"));
        columnBinding.setColumnName("Driver Class");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        connTypePane.setViewportView(connTypeTable);
        connTypeTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        connTypeTable.getColumnModel().getColumn(0).setHeaderValue(messageService.getString("ID"));

        connTypeAddButton.setText(messageService.getString("settings.add"));
        connTypeAddButton.setEnabled(false);
        connTypeAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connTypeAddButtonActionPerformed(evt);
            }
        });

        connTypeDelButton.setText(messageService.getString("settings.remove"));
        connTypeDelButton.setEnabled(false);
        connTypeDelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connTypeDelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout connTypePanelLayout = new javax.swing.GroupLayout(connTypePanel);
        connTypePanel.setLayout(connTypePanelLayout);
        connTypePanelLayout.setHorizontalGroup(
            connTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connTypePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(connTypePane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connTypePanelLayout.createSequentialGroup()
                        .addComponent(connTypeAddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(connTypeDelButton)))
                .addContainerGap())
        );
        connTypePanelLayout.setVerticalGroup(
            connTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connTypePanelLayout.createSequentialGroup()
                .addComponent(connTypePane, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(connTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(connTypeAddButton)
                    .addComponent(connTypeDelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        connPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(messageService.getString("settings.connections")));

        connTable.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${databaseConfiguration.databaseConnections.connection}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, eLProperty, connTable);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("ID");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${type}"));
        columnBinding.setColumnName("Type");
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${URL}"));
        columnBinding.setColumnName("URL");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${userName}"));
        columnBinding.setColumnName("Username");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${password}"));
        columnBinding.setColumnName("Password");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        connPane.setViewportView(connTable);
        connTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        connTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(connTypesCombo));
        connTable.getColumnModel().getColumn(1).setCellRenderer(new ConnTypesCellRenderer());
        connTable.getColumnModel().getColumn(3).setPreferredWidth(60);

        connAddButton.setText(messageService.getString("settings.add"));
        connAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connAddButtonActionPerformed(evt);
            }
        });

        connDelButton.setText(messageService.getString("settings.remove"));
        connDelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connDelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout connPanelLayout = new javax.swing.GroupLayout(connPanel);
        connPanel.setLayout(connPanelLayout);
        connPanelLayout.setHorizontalGroup(
            connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connPanelLayout.createSequentialGroup()
                .addContainerGap(401, Short.MAX_VALUE)
                .addComponent(connAddButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connDelButton)
                .addContainerGap())
            .addGroup(connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(connPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(connPane, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        connPanelLayout.setVerticalGroup(
            connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connPanelLayout.createSequentialGroup()
                .addContainerGap(97, Short.MAX_VALUE)
                .addGroup(connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(connDelButton)
                    .addComponent(connAddButton))
                .addContainerGap())
            .addGroup(connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(connPanelLayout.createSequentialGroup()
                    .addComponent(connPane, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(37, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout databaseConfigPanelLayout = new javax.swing.GroupLayout(databaseConfigPanel);
        databaseConfigPanel.setLayout(databaseConfigPanelLayout);
        databaseConfigPanelLayout.setHorizontalGroup(
            databaseConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databaseConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databaseConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(connPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(connTypePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        databaseConfigPanelLayout.setVerticalGroup(
            databaseConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseConfigPanelLayout.createSequentialGroup()
                .addComponent(connTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        saveButton.setText(messageService.getString("settings.saveclose"));
        saveButton.setToolTipText(messageService.getString("settings.saveclose"));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(databaseConfigPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appConfigPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(databaseConfigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveButtonActionPerformed
    {//GEN-HEADEREND:event_saveButtonActionPerformed
        TrayApp.getInstance().writeConfigToFile();
        TrayApp.getInstance().updateDatabaseMenus();
        this.dispose();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void browserPathButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_browserPathButtonActionPerformed
    {//GEN-HEADEREND:event_browserPathButtonActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
        int returnVal = fc.showOpenDialog( this );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            File file = fc.getSelectedFile();
            browserPathField.setText( file.getAbsolutePath() );
        }
    }//GEN-LAST:event_browserPathButtonActionPerformed

    private void connTypeAddButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_connTypeAddButtonActionPerformed
    {//GEN-HEADEREND:event_connTypeAddButtonActionPerformed
        TrayApp.databaseConfig.getConnectionTypes().getConnectionType().add( new ConnectionType() );
        bindingGroup.unbind();
        bindingGroup.bind();
    }//GEN-LAST:event_connTypeAddButtonActionPerformed

    private void connAddButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_connAddButtonActionPerformed
    {//GEN-HEADEREND:event_connAddButtonActionPerformed
        TrayApp.databaseConfig.getDatabaseConnections().getConnection().add( new Connection() );
        bindingGroup.unbind();
        bindingGroup.bind();
    }//GEN-LAST:event_connAddButtonActionPerformed

    private void connTypeDelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_connTypeDelButtonActionPerformed
    {//GEN-HEADEREND:event_connTypeDelButtonActionPerformed
        if ( connTypeTable.getSelectedRow() != -1 )
        {
            TrayApp.databaseConfig.getConnectionTypes().getConnectionType().remove( connTypeTable.getSelectedRow() );
        }
        bindingGroup.unbind();
        bindingGroup.bind();
    }//GEN-LAST:event_connTypeDelButtonActionPerformed

    private void connDelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_connDelButtonActionPerformed
    {//GEN-HEADEREND:event_connDelButtonActionPerformed
        if ( connTable.getSelectedRow() != -1 )
        {
            TrayApp.databaseConfig.getDatabaseConnections().getConnection().remove( connTable.getSelectedRow() );
        }
        bindingGroup.unbind();
        bindingGroup.bind();
    }//GEN-LAST:event_connDelButtonActionPerformed

    private void langComboActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_langComboActionPerformed
    {//GEN-HEADEREND:event_langComboActionPerformed
        Vector vect = new Vector();
        for ( int i = 0; i < countryVect.size(); i++ )
        {
            String item = countryVect.get( i ).toString();
            if ( item.split( "\\_" )[0].equals( langCombo.getSelectedItem().toString() ) )
            {
                vect.add( item.split( "\\_" )[1] );
            }
        }
        if ( vect.isEmpty() )
        {
            vect.add( "" );
        }
        countryCombo.setModel( new DefaultComboBoxModel( vect ) );
    }//GEN-LAST:event_langComboActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appConfigPanel;
    private javax.swing.JButton browserPathButton;
    private javax.swing.JTextField browserPathField;
    private javax.swing.JLabel browserPathLabel;
    private org.hisp.dhis.config.ConfigType config;
    private javax.swing.JButton connAddButton;
    private javax.swing.JButton connDelButton;
    private javax.swing.JScrollPane connPane;
    private javax.swing.JPanel connPanel;
    private javax.swing.JTable connTable;
    private javax.swing.JButton connTypeAddButton;
    private javax.swing.JButton connTypeDelButton;
    private javax.swing.JScrollPane connTypePane;
    private javax.swing.JPanel connTypePanel;
    private javax.swing.JTable connTypeTable;
    private javax.swing.JComboBox countryCombo;
    private javax.swing.JLabel countryLabel;
    private javax.swing.JPanel databaseConfigPanel;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JComboBox langCombo;
    private javax.swing.JLabel langLabel;
    private javax.swing.JLabel maxSizeDefaultLabel;
    private javax.swing.JTextField maxSizeField;
    private javax.swing.JLabel maxSizeLabel;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel unitLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
