/*
 * Created on Dec 15, 2010 at 9:19:09 AM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.app.control;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.MessageConnection;

import de.enough.polish.app.App;
import de.enough.polish.app.view.MainMenuList;
import de.enough.polish.app.view.TextField;
import de.enough.polish.app.view.TheScreen;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.DateField;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemStateListener;
import de.enough.polish.ui.SimpleScreenHistory;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.splash2.ApplicationInitializer;
import de.enough.polish.ui.splash2.InitializerSplashScreen;
import de.enough.polish.util.Locale;

/**
 * <p>
 * Controls the UI of the mobile app
 * </p>
 * 
 * <p>
 * Copyright Enough Software 2010 - 2012
 * </p>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Controller
    implements ApplicationInitializer, CommandListener, ItemStateListener
{

    private java.util.Hashtable __previousDisplayables = new java.util.Hashtable();

    private final App midlet;

    private Display display;

    private boolean savedMsg = false;

    private boolean firstRun = false;

    private RecordStore lastMsgStore = null;

    private StringItem sendMsgLabel;

    private Image question;

    private TheScreen loadPage;

    private TheScreen sentPage;

    private StringItem questionLabel;

    private ChoiceGroup lastChoice;

    private DateField dateField;

    private boolean editingLastReport = false;

    private TextField individualtextfield1;

    private TextField individualtextfield2;

    private TextField individualtextfield3;

    private TextField individualtextfield4;

    private TextField individualtextfield5;

    private TextField individualtextfield6;

    private TextField individualtextfield7;

    private TextField individualtextfield8;

    private TextField individualtextfield9;

    private TextField individualtextfield10;

    private TextField individualtextfield11;

    private TextField individualtextfield12;

    private TextField individualtextfield13;

    private TextField individualtextfield14;

    private TextField individualtextfield15;

    private TextField individualtextfield16;

    private TextField individualtextfield17;

    private TextField individualtextfield18;

    private TextField individualtextfield19;

    private TextField individualtextfield20;

    private TextField individualtextfield21;

    private TextField individualtextfield22;

    private TextField individualtextfield23;

    private TextField individualtextfield24;

    private TextField individualtextfield25;

    private TextField individualtextfield26;

    // ******

    // ****

    private ChoiceGroup diagnosed;

    private ImageItem imgItem = new ImageItem( null, null, ImageItem.LAYOUT_TOP, null );

    private Command cmdExit = new Command( Locale.get( "cmd.exit" ), Command.EXIT, 10 );

    private Command cmdBack = new Command( Locale.get( "cmd.back" ), Command.BACK, 2 );

    private Command cmdSelect = new Command( Locale.get( "cmd.select" ), Command.OK, 3 );

    private Command cmdOK = new Command( Locale.get( "cmd.ok" ), Command.OK, 4 );

    private Command cmdSave = new Command( Locale.get( "cmd.save" ), Command.OK, 5 );

    private Command cmdNext = new Command( Locale.get( "cmd.next" ), Command.OK, 6 );

    private Command cmdSend = new Command( Locale.get( "cmd.send" ), Command.OK, 7 );

    // **********
    int formID = 0;

    // **********

    // *********
    private MainMenuList screenMainMenu;

    private TheScreen screen1;

    private TheScreen screen2;

    private TheScreen screen3;

    private TheScreen screen4;

    private TheScreen screen5;

    private TheScreen screen6;

    private TheScreen screen7;

    private TheScreen screen8;

    private TheScreen dataCollectionScreen;

    private TheScreen registrationScreen;

    private TheScreen settingScreen;

    private TheScreen dateFieldScreen;

    // *************

    // ****
    private TextField phone1;

    private TextField phone2;

    private TextField phone3;

    // ****

    // *****
    private TextField textfield1;

    private TextField textfield2;

    private TextField textfield3;

    private TextField textfield4;

    private TextField textfield5;

    private TextField textfield6;

    private TextField textfield7;

    private TextField textfield8;

    private TextField textfield9;

    private TextField textfield10;

    private TextField textfield11;

    private TextField textfield12;

    private TextField textfield13;

    private TextField textfield14;

    private TextField textfield15;

    private TextField textfield16;

    private TextField textfield17;

    private TextField textfield18;

    private TextField textfield19;

    private TextField textfield20;

    private TextField textfield21;

    private TextField textfield22;

    private TextField textfield23;

    private TextField textfield24;

    private TextField textfield25;

    private TextField textfield26;

    private TextField textfield27;

    private TextField textfield28;

    private TextField textfield29;

    private TextField textfield30;

    private TextField textfield31;

    private TextField textfield32;

    private TextField textfield33;

    private TextField textfield34;

    private TextField textfield35;

    // ****


    private SimpleScreenHistory screenHistory;

    /**
     * Creates a new controller.
     * 
     * @param midlet the main application
     */
    public Controller( App midlet )
    {
        this.midlet = midlet;
        this.display = Display.getDisplay( midlet );
        this.screenHistory = new SimpleScreenHistory( this.display );
    }

    /**
     * Lifecycle: starts the application for the first time.
     */
    public void appStart()
    {
        String splashUrl = "/lung_cancer_awareness.jpg";
        Image splashImage = null;
        try
        {
            splashImage = Image.createImage( splashUrl );
        }
        catch ( Exception e )
        {
            // #debug error
            // System.out.println( "Unable to load splash image " + splashUrl +
            // e );
        }
        int backgroundColor = 0xffffff;
        InitializerSplashScreen splash = new InitializerSplashScreen( splashImage, backgroundColor, this );
        this.display.setCurrent( splash );
    }

    /**
     * Lifecycle: pauses the application, e.g. when there is an incoming call.
     */
    public void appPause()
    {
        // TODO implement pauseApp, e.g. stop streaming
    }

    /**
     * Lifecycle: continues the application after it has been paused.
     */
    public void appContinue()
    {
        // TODO implement continueApp, e.g. start streaming again
    }

    /**
     * Initializes this application in a background thread that is called from
     * within the splash screen.
     */
    public void initApp()
    {
        long initStartTime = System.currentTimeMillis();

        try
        {
            lastMsgStore = RecordStore.openRecordStore( "lastMsgStore", true );
            if ( lastMsgStore.getNumRecords() == 0 )
            {
                firstRun = true;
            }
            else
            {
                firstRun = false;
            }

            if ( firstRun )
            {
                for ( int i = 0; i < 100; i++ )
                {
                    try
                    {
                        lastMsgStore.addRecord( "".getBytes(), 0, "".getBytes().length );
                    }
                    catch ( RecordStoreException rsex )
                    {
                        rsex.printStackTrace();
                    }
                }
            }
            else
            {
                if ( lastMsgStore.getRecord( 4 ) != null )
                {
                    String checkSaved = new String( lastMsgStore.getRecord( 4 ) );
                    if ( checkSaved.equals( "true" ) )
                    {
                        savedMsg = true;
                    }
                }
            }
        }
        catch ( RecordStoreException ex )
        {
            ex.printStackTrace();
        }

        // this.storage = new RmsStorage();

        // this.configuration = configurationLoad();
        // create main menu:
        this.screenMainMenu = createMainMenu();
        this.loadPage = getLoadPage();
        long currentTime = System.currentTimeMillis();
        long maxTime = 1500;
        if ( currentTime - initStartTime < maxTime )
        { // show the splash at least
          // for 1500 ms / 2
          // seconds:
            try
            {
                Thread.sleep( maxTime - currentTime + initStartTime );
            }
            catch ( InterruptedException e )
            {
                // ignore
            }
        }
        if ( true == this.firstRun )
            this.display.setCurrent( this.screenMainMenu );
        else
            this.display.setCurrent( this.loadPage );
    }

    /**
     * Init screen
     * 
     * @return
     */
    private MainMenuList createMainMenu()
    {
        MainMenuList list = new MainMenuList();
        list.setCommandListener( this );
        list.addCommand( this.cmdExit );
        // list.addEntry( Locale.get( "CRTISFacilityForm" ), "/icon.png" );
        list.addEntry( Locale.get( "CRTISIndividualForm" ), "/icon.png" );
        list.addEntry( Locale.get( "Settings" ), "/icon.png" );
        return list;
    }

    /**
     * 
     * Facility Forms
     * 
     * @return
     */

    private TheScreen createScreen1()
    {
        TheScreen screen = new TheScreen( Locale.get( "1.CRTISFacilityForm" ) );
        if ( null == this.textfield1 )
        {
            String value = getRecordValue( 11 );
            this.textfield1 = new TextField( Locale.get( "3.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield1 );
        }
        if ( null == this.textfield2 )
        {
            String value = getRecordValue( 12 );
            this.textfield2 = new TextField( Locale.get( "4.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield2 );
        }
        if ( null == this.textfield3 )
        {
            String value = getRecordValue( 13 );
            this.textfield3 = new TextField( Locale.get( "5.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield3 );
        }
        if ( null == this.textfield4 )
        {
            String value = getRecordValue( 14 );
            this.textfield4 = new TextField( Locale.get( "6.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield4 );
        }
        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    private TheScreen createScreen2()
    {
        TheScreen screen = new TheScreen( Locale.get( "7.CRTISFacilityForm" ) );
        if ( null == this.textfield5 )
        {
            String value = getRecordValue( 15 );
            this.textfield5 = new TextField( Locale.get( "8.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield5 );
        }
        if ( null == this.textfield6 )
        {
            String value = getRecordValue( 16 );
            this.textfield6 = new TextField( Locale.get( "9.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield6 );
        }
        if ( null == this.textfield7 )
        {
            String value = getRecordValue( 17 );
            this.textfield7 = new TextField( Locale.get( "10.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield7 );
        }
        if ( null == this.textfield8 )
        {
            String value = getRecordValue( 18 );
            this.textfield8 = new TextField( Locale.get( "11.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield8 );
        }
        if ( null == this.textfield9 )
        {
            String value = getRecordValue( 19 );
            this.textfield9 = new TextField( Locale.get( "12.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield9 );
        }
        if ( null == this.textfield10 )
        {
            String value = getRecordValue( 20 );
            this.textfield10 = new TextField( Locale.get( "13.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield10 );
        }
        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    private TheScreen createScreen3()
    {
        TheScreen screen = new TheScreen( Locale.get( "14.CRTISFacilityForm" ) );
        if ( null == this.textfield11 )
        {
            String value = getRecordValue( 21 );
            this.textfield11 = new TextField( Locale.get( "15.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield11 );
        }
        if ( null == this.textfield12 )
        {
            String value = getRecordValue( 22 );
            this.textfield12 = new TextField( Locale.get( "16.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield12 );
        }

        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    private TheScreen createScreen4()
    {
        TheScreen screen = new TheScreen( Locale.get( "17.CRTISFacilityForm" ) );
        if ( null == this.textfield13 )
        {
            String value = getRecordValue( 23 );
            this.textfield13 = new TextField( Locale.get( "18.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield13 );
        }
        if ( null == this.textfield14 )
        {
            String value = getRecordValue( 24 );
            this.textfield14 = new TextField( Locale.get( "19.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield14 );
        }

        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    private TheScreen createScreen5()
    {
        TheScreen screen = new TheScreen( Locale.get( "20.CRTISFacilityForm" ) );
        if ( null == this.textfield15 )
        {
            String value = getRecordValue( 25 );
            this.textfield15 = new TextField( Locale.get( "21.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield15 );
        }
        if ( null == this.textfield16 )
        {
            String value = getRecordValue( 26 );
            this.textfield16 = new TextField( Locale.get( "22.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield16 );
        }

        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    private TheScreen createScreen6()
    {
        TheScreen screen = new TheScreen( Locale.get( "23.CRTISFacilityForm" ) );
        if ( null == this.textfield17 )
        {
            String value = getRecordValue( 27 );
            this.textfield17 = new TextField( Locale.get( "24.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield17 );
        }
        if ( null == this.textfield18 )
        {
            String value = getRecordValue( 28 );
            this.textfield18 = new TextField( Locale.get( "25.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield18 );
        }
        if ( null == this.textfield19 )
        {
            String value = getRecordValue( 29 );
            this.textfield19 = new TextField( Locale.get( "26.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield19 );
        }
        if ( null == this.textfield20 )
        {
            String value = getRecordValue( 30 );
            this.textfield20 = new TextField( Locale.get( "27.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield20 );
        }
        if ( null == this.textfield21 )
        {
            String value = getRecordValue( 31 );
            this.textfield21 = new TextField( Locale.get( "28.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield21 );
        }

        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    private TheScreen createScreen7()
    {
        TheScreen screen = new TheScreen( Locale.get( "29.CRTISFacilityForm" ) );
        if ( null == this.textfield22 )
        {
            String value = getRecordValue( 32 );
            this.textfield22 = new TextField( Locale.get( "30.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield22 );
        }
        if ( null == this.textfield23 )
        {
            String value = getRecordValue( 33 );
            this.textfield23 = new TextField( Locale.get( "31.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield23 );
        }

        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    private TheScreen createScreen8()
    {
        TheScreen screen = new TheScreen( Locale.get( "32.CRTISFacilityForm" ) );
        if ( null == this.textfield24 )
        {
            String value = getRecordValue( 34 );
            this.textfield24 = new TextField( Locale.get( "33.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield24 );
        }
        if ( null == this.textfield25 )
        {
            String value = getRecordValue( 35 );
            this.textfield25 = new TextField( Locale.get( "34.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield25 );
        }
        if ( null == this.textfield26 )
        {
            String value = getRecordValue( 36 );
            this.textfield26 = new TextField( Locale.get( "35.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield26 );
        }
        if ( null == this.textfield27 )
        {
            String value = getRecordValue( 37 );
            this.textfield27 = new TextField( Locale.get( "36.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield27 );
        }
        if ( null == this.textfield28 )
        {
            String value = getRecordValue( 38 );
            this.textfield28 = new TextField( Locale.get( "37.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield28 );
        }

        if ( null == this.textfield29 )
        {
            String value = getRecordValue( 39 );
            this.textfield29 = new TextField( Locale.get( "38.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield29 );
        }
        if ( null == this.textfield30 )
        {
            String value = getRecordValue( 40 );
            this.textfield30 = new TextField( Locale.get( "39.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield30 );
        }
        if ( null == this.textfield31 )
        {
            String value = getRecordValue( 41 );
            this.textfield31 = new TextField( Locale.get( "40.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield31 );
        }
        if ( null == this.textfield32 )
        {
            String value = getRecordValue( 42 );
            this.textfield32 = new TextField( Locale.get( "41.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield32 );
        }
        if ( null == this.textfield33 )
        {
            String value = getRecordValue( 43 );
            this.textfield33 = new TextField( Locale.get( "42.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield33 );
        }

        if ( null == this.textfield34 )
        {
            String value = getRecordValue( 44 );
            this.textfield34 = new TextField( Locale.get( "43.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield34 );
        }
        if ( null == this.textfield35 )
        {
            String value = getRecordValue( 45 );
            this.textfield35 = new TextField( Locale.get( "44.CRTISFacilityForm" ), value, 4, TextField.NUMERIC );
            screen.addTextField( this.textfield35 );
        }

        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    /**
     * Individual Forms
     * 
     * @return
     */

    // private boolean loadChoiceGroupMultipleStore( ChoiceGroup choiceGroup,
    // int recordNumber )
    // {
    // if ( editingLastReport && !getRecordValue( recordNumber
    // ).equalsIgnoreCase( "" ) )
    // {
    // int Index[] = null;
    // String indexValue[] = null;
    //
    // indexValue = split( getRecordValue( 58 ), "," );
    // if ( indexValue.length > 1 )
    // Index = new int[indexValue.length - 1];
    // for ( int i = 0; i < indexValue.length - 1; i++ )
    // try
    // {
    // Index[i] = Integer.parseInt( indexValue[i] );
    // }
    // catch ( NumberFormatException nfe )
    // {
    // // do nothing
    // }
    //
    // for ( int i = 0; i < Index.length; i++ )
    // choiceGroup.setSelectedIndex( Index[i], true );
    // return true;
    // }
    // return false;
    // }

    private boolean saveChoiceGroupMultipleStore( ChoiceGroup choiceGroup, int recordNumber )
    {
        try
        {
            boolean get[] = new boolean[choiceGroup.size()];
            choiceGroup.getSelectedFlags( get );
            String str = "";
            for ( int i = 0; i < get.length; i++ )
            {
                if ( get[i] )
                {
                    str += i + ",";
                }
            }
            lastMsgStore.setRecord( recordNumber, str.getBytes(), 0, str.length() );
            return true;
        }
        catch ( RecordStoreException ex )
        {
            ex.printStackTrace();
        }
        catch ( NullPointerException npe )
        {
            npe.printStackTrace();
        }
        return false;
    }

    private boolean saveTextfield()
    {
        new Thread( new Runnable()
        {

            public void run()
            {
                try
                {

                    lastMsgStore.setRecord( 11, textfield1.getString().getBytes(), 0, textfield1.getString().length() );
                    lastMsgStore.setRecord( 12, textfield2.getString().getBytes(), 0, textfield2.getString().length() );
                    lastMsgStore.setRecord( 13, textfield3.getString().getBytes(), 0, textfield3.getString().length() );
                    lastMsgStore.setRecord( 14, textfield4.getString().getBytes(), 0, textfield4.getString().length() );
                    lastMsgStore.setRecord( 15, textfield5.getString().getBytes(), 0, textfield5.getString().length() );
                    lastMsgStore.setRecord( 16, textfield6.getString().getBytes(), 0, textfield6.getString().length() );
                    lastMsgStore.setRecord( 17, textfield7.getString().getBytes(), 0, textfield7.getString().length() );
                    lastMsgStore.setRecord( 18, textfield8.getString().getBytes(), 0, textfield8.getString().length() );
                    lastMsgStore.setRecord( 19, textfield9.getString().getBytes(), 0, textfield9.getString().length() );
                    lastMsgStore
                        .setRecord( 20, textfield10.getString().getBytes(), 0, textfield10.getString().length() );
                    lastMsgStore
                        .setRecord( 21, textfield11.getString().getBytes(), 0, textfield11.getString().length() );
                    lastMsgStore
                        .setRecord( 22, textfield12.getString().getBytes(), 0, textfield12.getString().length() );
                    lastMsgStore
                        .setRecord( 23, textfield13.getString().getBytes(), 0, textfield13.getString().length() );
                    lastMsgStore
                        .setRecord( 24, textfield14.getString().getBytes(), 0, textfield14.getString().length() );
                    lastMsgStore
                        .setRecord( 25, textfield15.getString().getBytes(), 0, textfield15.getString().length() );
                    lastMsgStore
                        .setRecord( 26, textfield16.getString().getBytes(), 0, textfield16.getString().length() );
                    lastMsgStore
                        .setRecord( 27, textfield17.getString().getBytes(), 0, textfield17.getString().length() );
                    lastMsgStore
                        .setRecord( 28, textfield18.getString().getBytes(), 0, textfield18.getString().length() );
                    lastMsgStore
                        .setRecord( 29, textfield19.getString().getBytes(), 0, textfield19.getString().length() );
                    lastMsgStore
                        .setRecord( 30, textfield20.getString().getBytes(), 0, textfield20.getString().length() );
                    lastMsgStore
                        .setRecord( 31, textfield21.getString().getBytes(), 0, textfield21.getString().length() );
                    lastMsgStore
                        .setRecord( 32, textfield22.getString().getBytes(), 0, textfield22.getString().length() );
                    lastMsgStore
                        .setRecord( 33, textfield23.getString().getBytes(), 0, textfield23.getString().length() );
                    lastMsgStore
                        .setRecord( 34, textfield24.getString().getBytes(), 0, textfield24.getString().length() );
                    lastMsgStore
                        .setRecord( 35, textfield25.getString().getBytes(), 0, textfield25.getString().length() );
                    lastMsgStore
                        .setRecord( 36, textfield26.getString().getBytes(), 0, textfield26.getString().length() );
                    lastMsgStore
                        .setRecord( 37, textfield27.getString().getBytes(), 0, textfield27.getString().length() );
                    lastMsgStore
                        .setRecord( 38, textfield28.getString().getBytes(), 0, textfield28.getString().length() );
                    lastMsgStore
                        .setRecord( 39, textfield29.getString().getBytes(), 0, textfield29.getString().length() );
                    lastMsgStore
                        .setRecord( 40, textfield30.getString().getBytes(), 0, textfield30.getString().length() );
                    lastMsgStore
                        .setRecord( 41, textfield31.getString().getBytes(), 0, textfield31.getString().length() );
                    lastMsgStore
                        .setRecord( 42, textfield32.getString().getBytes(), 0, textfield32.getString().length() );
                    lastMsgStore
                        .setRecord( 43, textfield33.getString().getBytes(), 0, textfield33.getString().length() );
                    lastMsgStore
                        .setRecord( 44, textfield34.getString().getBytes(), 0, textfield34.getString().length() );
                    lastMsgStore
                        .setRecord( 45, textfield35.getString().getBytes(), 0, textfield35.getString().length() );

                    lastMsgStore.setRecord( 4, "true".getBytes(), 0, "true".getBytes().length ); // record
                                                                                                 // for
                                                                                                 // edit
                                                                                                 // or
                                                                                                 // not
                                                                                                 // edit
                                                                                                 // option
                                                                                                 // (on
                                                                                                 // load
                                                                                                 // page)
                    savedMsg = true;
                }
                catch ( RecordStoreException ex )
                {
                    ex.printStackTrace();
                }
                catch ( NullPointerException npe )
                {
                    npe.printStackTrace();
                }
            }
        } ).start();

        return true;
    }

    private String getMyBaseDataAggregate()
    {
        String formId = "02";
        String myData = formId + "#" + textfield1.getString() + ":" + textfield2.getString() + ":"
            + textfield3.getString() + ":" + textfield4.getString() + ":" + textfield5.getString() + ":"
            + textfield6.getString() + ":" + textfield7.getString() + ":" + textfield8.getString() + ":"
            + textfield9.getString() + ":" + textfield10.getString() + ":" + textfield11.getString() + ":"
            + textfield12.getString() + ":" + textfield13.getString() + ":" + textfield14.getString() + ":"
            + textfield15.getString() + ":" + textfield16.getString() + ":" + textfield17.getString() + ":"
            + textfield18.getString() + ":" + textfield19.getString() + ":" + textfield20.getString() + ":"
            + textfield21.getString() + ":" + textfield22.getString() + ":" + textfield23.getString() + ":"
            + textfield24.getString() + ":" + textfield25.getString() + ":" + textfield26.getString() + ":"
            + textfield27.getString() + ":" + textfield28.getString() + ":" + textfield29.getString() + ":"
            + textfield30.getString() + ":" + textfield31.getString() + ":" + textfield32.getString() + ":"
            + textfield33.getString() + ":" + textfield34.getString() + ":" + textfield35.getString();

        // System.out.println(myData);
        return myData;
    }

    private TheScreen dataCollectScreen()
    {
        TheScreen screen = new TheScreen( "Data Collection" );
        screen.append( imgItem );
        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdSave );
        screen.addCommand( this.cmdSend );
        return screen;
    }

    private boolean savePatientInfo()
    {
        new Thread( new Runnable()
        {

            public void run()
            {
                try
                {

                    lastMsgStore.setRecord( 5, getRegistrationTime().getBytes(), 0, getRegistrationTime().length() );
                    lastMsgStore.setRecord( 46, individualtextfield1.getString().getBytes(), 0, individualtextfield1
                        .getString().length() );
                    lastMsgStore.setRecord( 47, individualtextfield2.getString().getBytes(), 0, individualtextfield2
                        .getString().length() );
                    lastMsgStore.setRecord( 48, individualtextfield3.getString().getBytes(), 0, individualtextfield3
                        .getString().length() );
                    lastMsgStore.setRecord( 49, individualtextfield4.getString().getBytes(), 0, individualtextfield4
                        .getString().length() );
                    lastMsgStore.setRecord( 50, individualtextfield5.getString().getBytes(), 0, individualtextfield5
                        .getString().length() );
                    lastMsgStore.setRecord( 51, individualtextfield6.getString().getBytes(), 0, individualtextfield6
                        .getString().length() );
                    lastMsgStore.setRecord( 52, individualtextfield7.getString().getBytes(), 0, individualtextfield7
                        .getString().length() );
                    lastMsgStore.setRecord( 53, individualtextfield8.getString().getBytes(), 0, individualtextfield8
                        .getString().length() );
                    lastMsgStore.setRecord( 54, individualtextfield9.getString().getBytes(), 0, individualtextfield9
                        .getString().length() );
                    lastMsgStore.setRecord( 55, individualtextfield10.getString().getBytes(), 0, individualtextfield10
                        .getString().length() );
                    // lastMsgStore.setRecord( 56,
                    // individualtextfield11.getString().getBytes(), 0,
                    // individualtextfield11
                    // .getString().length() );
                    lastMsgStore.setRecord( 56, ("" + diagnosed.getSelectedIndex()).getBytes(), 0,
                        ("" + diagnosed.getSelectedIndex()).length() );

                    lastMsgStore.setRecord( 57, individualtextfield12.getString().getBytes(), 0, individualtextfield12
                        .getString().length() );
                    lastMsgStore.setRecord( 58, individualtextfield13.getString().getBytes(), 0, individualtextfield13
                        .getString().length() );
                    lastMsgStore.setRecord( 59, individualtextfield14.getString().getBytes(), 0, individualtextfield14
                        .getString().length() );
                    lastMsgStore.setRecord( 60, individualtextfield15.getString().getBytes(), 0, individualtextfield15
                        .getString().length() );
                    lastMsgStore.setRecord( 61, individualtextfield16.getString().getBytes(), 0, individualtextfield16
                        .getString().length() );
                    lastMsgStore.setRecord( 62, individualtextfield17.getString().getBytes(), 0, individualtextfield17
                        .getString().length() );
                    lastMsgStore.setRecord( 63, individualtextfield18.getString().getBytes(), 0, individualtextfield18
                        .getString().length() );
                    lastMsgStore.setRecord( 64, individualtextfield19.getString().getBytes(), 0, individualtextfield19
                        .getString().length() );
                    lastMsgStore.setRecord( 65, individualtextfield20.getString().getBytes(), 0, individualtextfield20
                        .getString().length() );
                    lastMsgStore.setRecord( 66, individualtextfield21.getString().getBytes(), 0, individualtextfield21
                        .getString().length() );
                    lastMsgStore.setRecord( 67, individualtextfield22.getString().getBytes(), 0, individualtextfield22
                        .getString().length() );
                    lastMsgStore.setRecord( 68, individualtextfield23.getString().getBytes(), 0, individualtextfield23
                        .getString().length() );
                    lastMsgStore.setRecord( 69, individualtextfield24.getString().getBytes(), 0, individualtextfield24
                        .getString().length() );
                    lastMsgStore.setRecord( 70, individualtextfield25.getString().getBytes(), 0, individualtextfield25
                        .getString().length() );
                    lastMsgStore.setRecord( 71, individualtextfield26.getString().getBytes(), 0, individualtextfield26
                        .getString().length() );
                }
                catch ( RecordStoreException ex )
                {
                    ex.printStackTrace();
                }
                catch ( NullPointerException npe )
                {
                    npe.printStackTrace();
                }
            }
        } ).start();

        return true;

    }

    private TheScreen settingScreen()
    {
        TheScreen screen = new TheScreen( "Settings" );
        String value = getRecordValue( 1 );
        this.phone1 = new TextField( "Enter Server Number", value, 10, TextField.NUMERIC );
        screen.addTextField( this.phone1 );

        String value1 = getRecordValue( 2 );
        this.phone2 = new TextField( "Phone number 2", value1, 10, TextField.NUMERIC );
//        screen.addTextField( this.phone2 );

        String value2 = getRecordValue( 3 );
        this.phone3 = new TextField( "Phone number 3", value2, 10, TextField.NUMERIC );
//        screen.addTextField( this.phone3 );

        screen.setCommandListener( this );
        screen.addCommand( this.cmdSave );
        screen.addCommand( this.cmdBack );
        return screen;
    }

    private TheScreen createDateFieldScreen()
    {
        TheScreen screen = new TheScreen( Locale.get( "registerDateTitle" ) );
        if (editingLastReport)
        {
            if (null == this.dateField){
                // read from RMS
                String dateTime = getRecordValue( 5 );
                if (dateTime != ""){
                    String [] dateTimeArray = split( dateTime, "-" );
                    String dyStr = dateTimeArray[2];
                    String mtStr = dateTimeArray[1];
                    String yrStr = dateTimeArray[0];
                    Calendar cal = Calendar.getInstance();
                    int dy = Integer.parseInt(dyStr);
                    int mt = Integer.parseInt(mtStr);
                    int yr = Integer.parseInt(yrStr);
                    cal.set(Calendar.DATE, dy);
                    cal.set(Calendar.MONTH, mt - 1);
                    cal.set(Calendar.YEAR, yr);
                    
                    //set Time
                    Date myDate = new Date();
                    myDate = cal.getTime();
                    
                    //#style TextField
                    this.dateField = new DateField( Locale.get( "RegisterDate" ), DateField.DATE );
                    this.dateField.setDateFormatPattern( "dd/MM/yyyy" );
                    this.dateField.setDate( myDate );
                    screen.append (dateField);
                }
            }
        }
        if ( null == this.dateField )
        {
            this.dateField = screen.addDateField();
            this.dateField.setDateFormatPattern( "dd/MM/yyyy" );
        }

        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        screen.addCommand( this.cmdSelect );
        screen.addCommand( this.cmdNext );
        return screen;
    }

    private TheScreen createIndividualForm1()
    {
        TheScreen screen = new TheScreen( Locale.get( "1.CRTISIndividualForm" ) );

        // screen.addDateField();
        if ( null == this.individualtextfield1 )
        {
            String value = getRecordValue( 46 );
            this.individualtextfield1 = new TextField( Locale.get( "3.CRTISIndividualForm" ), value, 7,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield1 );
        }
        if ( null == this.individualtextfield2 )
        {
            String value = getRecordValue( 47 );
            this.individualtextfield2 = new TextField( Locale.get( "4.CRTISIndividualForm" ), value, 5,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield2 );
        }
        if ( null == this.individualtextfield3 )
        {
            String value = getRecordValue( 48 );
            this.individualtextfield3 = new TextField( Locale.get( "5.CRTISIndividualForm" ), value, 5,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield3 );
        }
        if ( null == this.individualtextfield4 )
        {
            String value = getRecordValue( 49 );
            this.individualtextfield4 = new TextField( Locale.get( "6.CRTISIndividualForm" ), value, 5,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield4 );
        }
        if ( null == this.individualtextfield5 )
        {
            String value = getRecordValue( 50 );
            this.individualtextfield5 = new TextField( Locale.get( "7.CRTISIndividualForm" ), value, 6,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield5 );
        }
        if ( null == this.individualtextfield6 )
        {
            String value = getRecordValue( 51 );
            this.individualtextfield6 = new TextField( Locale.get( "8.CRTISIndividualForm" ), value, 30, TextField.ANY );
            screen.addTextField( this.individualtextfield6 );
        }
        if ( null == this.individualtextfield7 )
        {
            String value = getRecordValue( 52 );
            this.individualtextfield7 = new TextField( Locale.get( "9.CRTISIndividualForm" ), value, 30, TextField.ANY );
            screen.addTextField( this.individualtextfield7 );
        }
        if ( null == this.individualtextfield8 )
        {
            String value = getRecordValue( 53 );
            this.individualtextfield8 = new TextField( Locale.get( "10.CRTISIndividualForm" ), value, 10,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield8 );
        }
        if ( null == this.individualtextfield9 )
        {
            String value = getRecordValue( 54 );
            this.individualtextfield9 = new TextField( Locale.get( "11.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield9 );
        }
        if ( null == this.individualtextfield10 )
        {
            String value = getRecordValue( 55 );
            this.individualtextfield10 = new TextField( Locale.get( "12.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield10 );
        }
        // if ( null == this.individualtextfield11 )
        // {
        // String value = getRecordValue( 56 );
        // this.individualtextfield11 = new TextField( Locale.get(
        // "13.CRTISIndividualForm" ), value, 2,
        // TextField.NUMERIC );
        // screen.addTextField( this.individualtextfield11 );
        // }
        if ( null == this.diagnosed )
        {
            String value = getRecordValue( 56 );
            int index = 0;
            try
            {
                index = Integer.parseInt( value );
            }
            catch ( NumberFormatException nfe )
            {
                // do nothing
            }
            String[] diagnosed = { Locale.get( "Yes" ), Locale.get( "No" ) };
            this.diagnosed = screen.addChoiceGroupSingle( Locale.get( "13.CRTISIndividualForm" ), diagnosed, null );
            this.diagnosed.setSelectedIndex( index, true );

        }

        if ( null == this.individualtextfield12 )
        {
            String value = getRecordValue( 57 );
            this.individualtextfield12 = new TextField( Locale.get( "14.CRTISIndividualForm" ), value, 2,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield12 );
        }
        if ( null == this.individualtextfield13 )
        {
            String value = getRecordValue( 58 );
            this.individualtextfield13 = new TextField( Locale.get( "15.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield13 );
        }
        if ( null == this.individualtextfield14 )
        {
            String value = getRecordValue( 59 );
            this.individualtextfield14 = new TextField( Locale.get( "16.CRTISIndividualForm" ), value, 2,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield14 );
        }
        if ( null == this.individualtextfield15 )
        {
            String value = getRecordValue( 60 );
            this.individualtextfield15 = new TextField( Locale.get( "17.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield15 );
        }
        if ( null == this.individualtextfield16 )
        {
            String value = getRecordValue( 61 );
            this.individualtextfield16 = new TextField( Locale.get( "18.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield16 );
        }
        if ( null == this.individualtextfield17 )
        {
            String value = getRecordValue( 62 );
            this.individualtextfield17 = new TextField( Locale.get( "19.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield17 );
        }
        if ( null == this.individualtextfield18 )
        {
            String value = getRecordValue( 63 );
            this.individualtextfield18 = new TextField( Locale.get( "20.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield18 );
        }
        if ( null == this.individualtextfield19 )
        {
            String value = getRecordValue( 64 );
            this.individualtextfield19 = new TextField( Locale.get( "21.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield19 );
        }
        if ( null == this.individualtextfield20 )
        {
            String value = getRecordValue( 65 );
            this.individualtextfield20 = new TextField( Locale.get( "22.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield20 );
        }
        if ( null == this.individualtextfield21 )
        {
            String value = getRecordValue( 66 );
            this.individualtextfield21 = new TextField( Locale.get( "23.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield21 );
        }
        if ( null == this.individualtextfield22 )
        {
            String value = getRecordValue( 67 );
            this.individualtextfield22 = new TextField( Locale.get( "24.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield22 );
        }
        if ( null == this.individualtextfield23 )
        {
            String value = getRecordValue( 68 );
            this.individualtextfield23 = new TextField( Locale.get( "25.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield23 );
        }
        if ( null == this.individualtextfield24 )
        {
            String value = getRecordValue( 69 );
            this.individualtextfield24 = new TextField( Locale.get( "26.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield24 );
        }
        if ( null == this.individualtextfield25 )
        {
            String value = getRecordValue( 70 );
            this.individualtextfield25 = new TextField( Locale.get( "27.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            screen.addTextField( this.individualtextfield25 );
        }
        if ( null == this.individualtextfield26 )
        {
            String value = getRecordValue( 71 );
            this.individualtextfield26 = new TextField( Locale.get( "28.CRTISIndividualForm" ), value, 1,
                TextField.NUMERIC );
            this.individualtextfield26.setEnableGameKey( false );
            screen.addTextField( this.individualtextfield26 );
        }
        if ( screen instanceof TheScreen )
        {
            ((TheScreen) screen).setItemStateListener( (de.enough.polish.ui.ItemStateListener) this );
        }

        this.disable_enable();
        screen.setCommandListener( this );
        screen.addCommand( this.cmdBack );
        // screen.addCommand( this.cmdSave );
        screen.addCommand( this.cmdNext );

        return screen;
    }

    public void commandAction( Command arg0, Item arg1 )
    {
        // TODO Auto-generated method stub

    }

    private String getRegistrationTime(){
     // Convert the date format
        Calendar cal = Calendar.getInstance();
        cal.setTime( dateField.getDate() );
        String date = cal.get( Calendar.YEAR ) + "-" + (cal.get( Calendar.MONTH ) + 1) + "-"
            + cal.get( Calendar.DAY_OF_MONTH );
//        System.out.println( date );
        return date;
    }
    
    private String getMyBaseDataRegistration()
    {
        String formID = "01";

        String date = getRegistrationTime();

        String myData = formID + "#" + date + "$" + collectRegistrationData();

        // System.out.println(myData);

        return myData;
    }

    private String collectRegistrationData()
    {
        String diagnosed = "Y";
        if ( 1 == this.diagnosed.getSelectedIndex() )
        {
            diagnosed = "N";
        }
        return this.individualtextfield1.getString() + ":" + this.individualtextfield2.getString() + ":"
            + this.individualtextfield3.getString() + ":" + this.individualtextfield4.getString() + ":"
            + this.individualtextfield5.getString() + ":" + this.individualtextfield6.getString() + ":"
            + this.individualtextfield7.getString() + ":" + this.individualtextfield8.getString() + ":"
            + this.individualtextfield9.getString() + ":" + this.individualtextfield10.getString() + ":" + diagnosed
            + ":" + this.individualtextfield12.getString() + ":" + this.individualtextfield13.getString() + ":"
            + this.individualtextfield14.getString() + ":" + this.individualtextfield15.getString() + ":"
            + this.individualtextfield16.getString() + ":" + this.individualtextfield17.getString() + ":"
            + this.individualtextfield18.getString() + ":" + this.individualtextfield19.getString() + ":"
            + this.individualtextfield20.getString() + ":" + this.individualtextfield21.getString() + ":"
            + this.individualtextfield22.getString() + ":" + this.individualtextfield23.getString() + ":"
            + this.individualtextfield24.getString() + ":" + this.individualtextfield25.getString() + ":"
            + this.individualtextfield26.getString();
    }

    private boolean saveTextPhoneNumber()
    {
        if (phone1.getString().length() < 10)
        {
            Alert myAlert = new Alert( "Warning", "Phone number has 10 digits!", null, AlertType.INFO );
            myAlert.setTimeout( 2000 );
            switchDisplayable( myAlert, settingScreen );
            return false;
        }
        else
        {
            new Thread( new Runnable()
            {
    
                public void run()
                {
                    try
                    {
                        // lastMsgStore.setRecord(8, msgVersion.getBytes(), 0,
                        // msgVersion.length());
                        // lastMsgStore.setRecord(9, intToByteArray(formID), 0,
                        // intToByteArray(formID).length);
                        // lastMsgStore.setRecord(10, monthStr.getBytes(), 0,
                        // monthStr.length()); // record for month
                        // lastMsgStore.setRecord(7, freqString.getBytes(), 0,
                        // freqString.length()); // record for frequency
                        lastMsgStore.setRecord( 1, phone1.getString().getBytes(), 0, phone1.getString().length() );
                        lastMsgStore.setRecord( 2, phone2.getString().getBytes(), 0, phone2.getString().length() );
                        lastMsgStore.setRecord( 3, phone3.getString().getBytes(), 0, phone3.getString().length() );
    
                    }
                    catch ( RecordStoreException ex )
                    {
                        ex.printStackTrace();
                    }
                    catch ( NullPointerException npe )
                    {
                        npe.printStackTrace();
                    }
                }
            } ).start();
        }
        return true;

    }

    /**
     * Init flow
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.enough.polish.ui.CommandListener#commandAction(de.enough.polish.ui
     * .Command, de.enough.polish.ui.Displayable)
     */
    public void commandAction( Command cmd, Displayable disp )
    {
        if ( cmd == this.cmdExit )
        {
            exit();
        }
        else if ( disp == loadPage )
        {
            if ( this.cmdOK == cmd )
            {
                int lastSelected = lastChoice.getSelectedIndex();
                if ( lastSelected == 0 )
                {
                    editingLastReport = true;
                }
                else
                {
                    editingLastReport = false;
                }
                //switchDisplayable( null, this.screenMainMenu );
                if ( null == this.dateFieldScreen )
                    this.dateFieldScreen = this.createDateFieldScreen();
                switchDisplayable( null, this.dateFieldScreen );
            }
        }
        else if ( disp == this.screenMainMenu )
        {
            if ( handleCommandMainMenu( cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.screen1 )
        {
            if ( null == this.screen2 )
            {
                this.screen2 = this.createScreen2();
                Alert myAlert = new Alert( "screen 1", "screen 1!", null, AlertType.INFO );
                myAlert.setTimeout( 2000 );
                switchDisplayable( myAlert, this.screen1 );
            }
            if ( handleCommandTheScreen( this.screenMainMenu, this.screen2, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.screen2 )
        {
            if ( null == this.screen3 )
                this.screen3 = this.createScreen3();
            if ( handleCommandTheScreen( this.screen1, this.screen3, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.screen3 )
        {
            if ( null == this.screen4 )
                this.screen4 = this.createScreen4();
            if ( handleCommandTheScreen( this.screen2, this.screen4, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.screen4 )
        {
            if ( null == this.screen5 )
                this.screen5 = this.createScreen5();
            if ( handleCommandTheScreen( this.screen3, this.screen5, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.screen5 )
        {
            if ( null == this.screen6 )
                this.screen6 = this.createScreen6();
            if ( handleCommandTheScreen( this.screen4, this.screen6, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.screen6 )
        {
            if ( null == this.screen7 )
                this.screen7 = this.createScreen7();
            if ( handleCommandTheScreen( this.screen5, this.screen7, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.screen7 )
        {
            if ( null == this.screen8 )
                this.screen8 = this.createScreen8();
            if ( handleCommandTheScreen( this.screen6, this.screen8, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.screen8 )
        {
            if ( null == this.dataCollectionScreen )
                this.dataCollectionScreen = this.dataCollectScreen();
            getEmptyFields();
            if ( handleCommandTheScreen( this.screen7, this.dataCollectionScreen, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.dataCollectionScreen )
        {
            if ( this.cmdSave == cmd )
            {
                savePatientInfo();
                Alert myAlert = new Alert( "Save success", "Your patient's information has been saved!", null,
                    AlertType.INFO );
                myAlert.setTimeout( 2000 );
                switchDisplayable( myAlert, this.dataCollectionScreen );
                return;
            }
            else if ( this.cmdBack == cmd )
            {
                handleCommandTheScreen( this.registrationScreen, this.dataCollectionScreen, cmd );
            }
            else
            {
                getSendMsgLabel();
                sentPage = getSentPage();
                sendDataViaSMS( getMyBaseDataRegistration() );
                savePatientInfo();
                this.screenHistory.show( sentPage );
            }
        }
        else if ( disp == this.registrationScreen )
        {
            // if ( null == this.dataCollectionScreen )
            this.dataCollectionScreen = this.dataCollectScreen();
            getEmptyFieldsRegistration();
            if ( handleCommandTheScreen( this.dateFieldScreen, this.dataCollectionScreen, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.settingScreen )
        {
            if ( this.cmdSave == cmd )
            {
                if (saveTextPhoneNumber()){
                    Alert myAlert = new Alert( "Save success", "saved!", null, AlertType.INFO );
                    myAlert.setTimeout( 2000 );
                    switchDisplayable( myAlert, this.screenMainMenu );
                }
                return;
            }
            if ( handleCommandTheScreen( this.screenMainMenu, null, cmd ) )
            {
                return;
            }
        }
        else if ( disp == this.sentPage )
        {
            if ( this.cmdOK == cmd )
            {
                this.screenHistory.show( this.screenMainMenu );
            }
        }
        else if ( disp == this.dateFieldScreen )
        {
            if ( null == this.registrationScreen )
                this.registrationScreen = this.createIndividualForm1();
            if ( handleCommandTheScreen( this.screenMainMenu, this.registrationScreen, cmd ) )
            {
                return;
            }
        }

    }

    /**
     * handle command
     */

    /**
     * Handles commands for the main menu
     * 
     * @param cmd the command of the main menu
     * @return true when a command was handled
     */
    private boolean handleCommandMainMenu( Command cmd )
    {
        if ( cmd == MainMenuList.SELECT_COMMAND )
        {
            int index = this.screenMainMenu.getSelectedIndex();
            switch ( index )
            {
            // case MAIN_ACTION_START:
            // if ( null == this.screen1 )
            // this.screen1 = createScreen1();
            // this.screenHistory.push( screenMainMenu );
            // this.screenHistory.show( this.screen1 );
            // break;
            // case MAIN_ACTION_REGISTRATIOIN:
            // if( null == this.dateFieldScreen)
            // this.dateFieldScreen = this.createDateFieldScreen();
            // this.screenHistory.push( screenMainMenu );
            // this.screenHistory.show( this.dateFieldScreen );
            // break;
            // case MAIN_ACTION_SETTINGS:
            // if ( null == this.settingScreen )
            // this.settingScreen = settingScreen();
            // this.screenHistory.push( screenMainMenu );
            // this.screenHistory.show( this.settingScreen );
            // break;
            // case MAIN_ACTION_EXIT:
            // exit();
            // return true;
            case 0:
                if ( null == this.dateFieldScreen )
                    this.dateFieldScreen = this.createDateFieldScreen();
                this.screenHistory.push( screenMainMenu );
                this.screenHistory.show( this.dateFieldScreen );
                break;
            case 1:
                if ( null == this.settingScreen )
                    this.settingScreen = settingScreen();
                this.screenHistory.push( screenMainMenu );
                this.screenHistory.show( this.settingScreen );
                break;
            }
        }
        else if ( cmd == this.cmdExit )
            this.exit();
        else
        {
        }
        return false;
    }

    private boolean handleCommandTheScreen( Displayable backScr, Displayable nextScr, Command cmd )
    {
        if ( cmd == this.cmdBack )
        {
            this.screenHistory.show( backScr );
            return true;
        }
        else if ( cmd == this.cmdNext )
        {
            this.screenHistory.show( nextScr );
            return true;
        }
        return false;
    }

    /**
     * Exits this app
     */
    private void exit()
    {
        this.midlet.exit();
    }

    public void switchDisplayable( Alert alert, Displayable nextDisplayable )
    {
        Display display = getDisplay();
        Displayable __currentDisplayable = display.getCurrent();
        if ( __currentDisplayable != null && nextDisplayable != null )
        {
            __previousDisplayables.put( nextDisplayable, __currentDisplayable );
        }
        if ( alert == null )
        {
            display.setCurrent( nextDisplayable );
        }
        else
        {
            display.setCurrent( alert, nextDisplayable );
        }
    }

    private String[] split( String original, String separator )
    {
        Vector nodes = new Vector();

        // Parse nodes into vector
        int index = original.indexOf( separator );
        while ( index >= 0 )
        {
            nodes.addElement( original.substring( 0, index ) );
            original = original.substring( index + separator.length() );
            index = original.indexOf( separator );
        }
        // Get the last node
        nodes.addElement( original );

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if ( nodes.size() > 0 )
        {
            for ( int loop = 0; loop < nodes.size(); loop++ )
            {
                result[loop] = (String) nodes.elementAt( loop );
            }
        }
        return result;
    }

    private void getEmptyFields()
    {

        String fullStr = "";
        int j = 0;

        fullStr = getMyBaseDataAggregate();
        String[] allContent = split( fullStr, ":" );

        for ( int i = 0; i < allContent.length; i++ )
        {
            if ( allContent[i].equals( "" ) )
            {
                j++;
            }
        }

        try
        {
            if ( j != 0 )
            {
                Image missingImg = Image.createImage( "/exclamation.png" );
                imgItem.setImage( missingImg );
                if ( j == 1 )
                {
                    imgItem.setLabel( j + " field is not filled. Please verify..." );
                }
                else
                {
                    imgItem.setLabel( j + " fields are not filled. Please verify..." );
                }
                imgItem.setLayout( ImageItem.LAYOUT_DEFAULT );
            }
            else
            {
                Image completeImg = Image.createImage( "/success.png" );
                imgItem.setImage( completeImg );
                imgItem.setLabel( "All fields are filled" );
                imgItem.setLayout( ImageItem.LAYOUT_DEFAULT );
            }
        }
        catch ( IOException ex )
        {
            ex.printStackTrace();
        }
    }

    private void getEmptyFieldsRegistration()
    {

        String fullStr = "";
        int j = 0;

        fullStr = collectRegistrationData();
        String[] allContent = split( fullStr, ":" );

        for ( int i = 0; i < allContent.length; i++ )
        {
            if ( 0 == this.diagnosed.getSelectedIndex() && 11 == i )
            {
                continue;
            }
            if ( 1 == this.diagnosed.getSelectedIndex()
                && (12 == i || 13 == i || 14 == i || 15 == i || 16 == i || 17 == i || 18 == i) )
            {
                continue;
            }
            if ( allContent[i].equals( "" ) )
            {
                j++;
            }
        }

        try
        {
            if ( j != 0 )
            {
                //this.dataCollectionScreen.removeCommand( this.cmdSend );
                Image missingImg = Image.createImage( "/exclamation.png" );
                imgItem.setImage( missingImg );
                if ( j == 1 )
                {
                    imgItem.setLabel( j + " field is not filled. Please verify..." );
                    //imgItem.setLabel("These many fields are not filled please verify" );
                    
                }
                else
                {
                    imgItem.setLabel( j + " fields are not filled. Please verify..." );
                    //imgItem.setLabel("These many fields are not filled please verify" );
                    
                }
                imgItem.setLayout( ImageItem.LAYOUT_DEFAULT );
            }
            else
            {
                Image completeImg = Image.createImage( "/success.png" );
                imgItem.setImage( completeImg );
                imgItem.setLabel( "All fields are filled" );
                imgItem.setLayout( ImageItem.LAYOUT_DEFAULT );
            }
        }
        catch ( IOException ex )
        {
            ex.printStackTrace();
        }
    }



    public StringItem getSendMsgLabel()
    {
        if ( sendMsgLabel == null )
        {
            sendMsgLabel = new StringItem( "Info:", "Processing...", Item.PLAIN );
            sendMsgLabel.setLayout( ImageItem.LAYOUT_CENTER );
        }
        return sendMsgLabel;
    }

    private void sendDataViaSMS( final String scForm1Data )
    {
        new Thread( new Runnable()
        {

            public void run()
            {
                try
                {
                    int j = 0;
                    for ( int i = 0; i < 3; i++ )
                    {
                        if ( lastMsgStore.getRecord( i + 1 ) != null )
                        {
                            j++;
                            MessageConnection smsConn = (MessageConnection) Connector.open( "sms://"
                                + new String( lastMsgStore.getRecord( i + 1 ) ) );

                            // send TextMessage
//                            TextMessage sms = (TextMessage) smsConn.newMessage( MessageConnection.TEXT_MESSAGE );
//                            sms.setPayloadText( scForm1Data );
//                            smsConn.send( sms );
//                            smsConn.close();

                            // send BinaryMessage
                             BinaryMessage sms = (BinaryMessage)
                             smsConn.newMessage(MessageConnection.BINARY_MESSAGE);
                             byte[] compressedData = null;
                             compressedData =
                             Compressor.compress(scForm1Data.getBytes("UTF-8"));
                             sms.setPayloadData(compressedData);
                             smsConn.send(sms);
                             smsConn.close();
                        }
                    }
                    if ( j == 0 )
                    {
                        sendMsgLabel
                            .setText( "No number to send SMS... Please go to settings and enter a phone number" );
                    }
                    else
                    {

                        sendMsgLabel
                            .setText( "Message Sent Successfully. You can go back and edit or exit the application now." );
                    }
                }
                catch ( UnsupportedEncodingException ex )
                {
                    ex.printStackTrace();
                }
                catch ( RecordStoreException rsex )
                {
                    rsex.printStackTrace();
                }
                catch ( IOException ex )
                {
                    ex.printStackTrace();
                }
                catch ( SecurityException ex )
                {
                    ex.printStackTrace();
                }
                catch ( NullPointerException npe )
                {
                    npe.printStackTrace();
                }
                catch ( IllegalArgumentException ex )
                {
                    ex.printStackTrace();
                }
            }
        } ).start();
    }

    public Display getDisplay()
    {
        return Display.getDisplay( midlet );
    }

    /*
     * Author : Thai Chuong This function use to get value from RecordStore
     * return String : the value of the record int recordNumber: the numberic of
     * the record in RecordStore
     */
    public String getRecordValue( int recordNumber )
    {
        String str = "";
        if ( editingLastReport )
        {
            try
            {
                byte[] myValue = lastMsgStore.getRecord( recordNumber );
                // fixed the problem that the textField was defined "numeric"
                // still accept "-" character
                if ( myValue != null )
                {
                    str = new String( myValue );
                }

            }
            catch ( RecordStoreException rsex )
            {
                rsex.printStackTrace();
            }
        }
        return str;
    }

    public static final byte[] intToByteArray( int value )
    {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
    }

    /**
     * Returns an initialized instance of question component.
     * 
     * @return the initialized component instance
     */
    public Image getQuestion()
    {
        if ( question == null )
        {
            try
            {
                question = Image.createImage( "/question.png" );
            }
            catch ( java.io.IOException e )
            {
                e.printStackTrace();
            }
        }
        return question;
    }

    /**
     * Returns an initialized instance of loadPage component.
     * 
     * @return the initialized component instance
     */
    public TheScreen getLoadPage()
    {
        if ( loadPage == null )
        {
            // loadPage = new Form("Edit Last Report", new
            // Item[]{getQuestionImage(), getQuestionLabel(), getLastChoice()});
            loadPage = new TheScreen( "Edit Last Report" );

            loadPage.addObject( getQuestion() );
            loadPage.addObject( getQuestionLabel() );
            loadPage.addObject( getLastChoice() );

            loadPage.addCommand( this.cmdOK );
            loadPage.addCommand( this.cmdExit );
            loadPage.setCommandListener( this );
        }
        return loadPage;
    }

    public TheScreen getSentPage()
    {
        if ( sentPage == null )
        {
            sentPage = new TheScreen( "Sent" );

            sentPage.addObject( sendMsgLabel );

            sentPage.addCommand( this.cmdOK );
            sentPage.addCommand( this.cmdExit );
            sentPage.setCommandListener( this );
        }
        if ( sentPage.size() > 1 )
        {
            sentPage.delete( sentPage.size() - 1 );
        }
        return sentPage;
    }

    /**
     * Returns an initialized instance of questionLabel component.
     * 
     * @return the initialized component instance
     */
    public StringItem getQuestionLabel()
    {
        if ( questionLabel == null )
        {
            questionLabel = new StringItem( "", "Do You Want To Edit Your Last Submitted Report?" );
        }
        return questionLabel;
    }

    /**
     * Returns an initialized instance of lastChoice component.
     * 
     * @return the initialized component instance
     */
    public ChoiceGroup getLastChoice()
    {
        if ( lastChoice == null )
        {
            lastChoice = new ChoiceGroup( Locale.get( "lastChoiceLabel" ), Choice.EXCLUSIVE );
            lastChoice.append( Locale.get( "lastChoiceYes" ), null );
            lastChoice.append( Locale.get( "lastChoiceNo" ), null );
            lastChoice.setFitPolicy( Choice.TEXT_WRAP_DEFAULT );
            lastChoice.setSelectedFlags( new boolean[] { false, false } );
        }
        return lastChoice;
    }

    public void itemStateChanged( Item item )
    {
        // TODO Auto-generated method stub
        if ( item == this.diagnosed )
        {
            this.disable_enable();
        }
        
        else if (item == this.individualtextfield18)
        {
            
            if(this.individualtextfield18.getText().equalsIgnoreCase( "2" ))
            {
                //#style DisabledTextField
                this.individualtextfield19.setStyle();
                this.individualtextfield19.setString( "" );
                this.individualtextfield19
                    .setConstraints( (this.individualtextfield19.getConstraints() & TextField.CONSTRAINT_MASK)
                        | TextField.UNEDITABLE );

            }
            else{
                //#style TextField
                this.individualtextfield19.setStyle();
                this.individualtextfield19
                    .setConstraints( (this.individualtextfield19.getConstraints() & TextField.CONSTRAINT_MASK) );
            }
        }
        
        else if (item == this.individualtextfield9)
        {
            String[] list = {"1","2"};
            if(valueValidation(this.individualtextfield9, list))
            {
                this.individualtextfield9.setText( "" );
            }
        }
        else if (item == this.individualtextfield10)
        {
            String[] list = {"1","2","3","4"};
            if(valueValidation(this.individualtextfield10, list))
            {
                this.individualtextfield10.setText( "" );
            }
        }
        else if (item == this.individualtextfield12)
        {
            String[] list = {"1","2","3","4","5","6","7","8","9","10","11","12"};
            if(valueValidation(this.individualtextfield12, list))
            {
                this.individualtextfield12.setText( "" );
            }
        }
        else if (item == this.individualtextfield13)
        {
            String[] list = {"1","2"};
            if(valueValidation(this.individualtextfield13, list))
            {
                this.individualtextfield13.setText( "" );
            }
        }
        else if (item == this.individualtextfield14)
        {
            String[] list = {"1","2","3","4","5","6","7","8","9","10","11","12"};
            if(valueValidation(this.individualtextfield14, list))
            {
                this.individualtextfield14.setText( "" );
            }
        }
        else if (item == this.individualtextfield15)
        {
            String[] list = {"1","2","3","4"};
            if(valueValidation(this.individualtextfield15, list))
            {
                this.individualtextfield15.setText( "" );
            }
        }
        else if (item == this.individualtextfield16)
        {
            String[] list = {"1","2","3","4"};
            if(valueValidation(this.individualtextfield16, list))
            {
                this.individualtextfield16.setText( "" );
            }
        }
        else if (item == this.individualtextfield17)
        {
            String[] list = {"1","2"};
            if(valueValidation(this.individualtextfield17, list))
            {
                this.individualtextfield17.setText( "" );
            }
        }
        else if (item == this.individualtextfield18)
        {
            String[] list = {"1","2"};
            if(valueValidation(this.individualtextfield18, list))
            {
                this.individualtextfield18.setText( "" );
            }
        }
        else if (item == this.individualtextfield19)
        {
            String[] list = {"1","2"};
            if(valueValidation(this.individualtextfield19, list))
            {
                this.individualtextfield19.setText( "" );
            }
        }
        else if (item == this.individualtextfield20)
        {
            String[] list = {"1","2","3","4","5","6"};
            if(valueValidation(this.individualtextfield20, list))
            {
                this.individualtextfield20.setText( "" );
            }
        }
        else if (item == this.individualtextfield21)
        {
            String[] list = {"1","2","3","4"};
            if(valueValidation(this.individualtextfield21, list))
            {
                this.individualtextfield21.setText( "" );
            }
        }
        else if (item == this.individualtextfield22)
        {
            String[] list = {"1","2"};
            if(valueValidation(this.individualtextfield22, list))
            {
                this.individualtextfield22.setText( "" );
            }
        }
        else if (item == this.individualtextfield23)
        {
            String[] list = {"1","2","3","4","5","6","7"};
            if(valueValidation(this.individualtextfield23, list))
            {
                this.individualtextfield23.setText( "" );
            }
        }
        else if (item == this.individualtextfield24)
        {
            String[] list = {"1","2","3","4","5","6"};
            if(valueValidation(this.individualtextfield24, list))
            {
                this.individualtextfield24.setText( "" );
            }
        }
        else if (item == this.individualtextfield25)
        {
            String[] list = {"1","2","3","4","5","6","7","8"};
            if(valueValidation(this.individualtextfield25, list))
            {
                this.individualtextfield25.setText( "" );
            }
        }
        else if (item == this.individualtextfield26)
        {
            String[] list = {"1","2","3","4","5","6"};
            if(valueValidation(this.individualtextfield26, list))
            {
                this.individualtextfield26.setText( "" );
            }
        }
    }
    
    private boolean valueValidation(TextField myTextField, String[] mylist){
        for (int i = 0; i < mylist.length; i++)
        {
            if (myTextField.getText().equalsIgnoreCase( mylist[i] ))
                return false;
        }
        return true;
        
    }
    
    private void disable_enable(){
        if ( 0 == this.diagnosed.getSelectedIndex() )
        {
            //#style DisabledTextField
            this.individualtextfield12.setStyle();
            this.individualtextfield12.setString( "" );
            this.individualtextfield12
                .setConstraints( (this.individualtextfield12.getConstraints() & TextField.CONSTRAINT_MASK)
                    | TextField.UNEDITABLE );

            //#style TextField
            this.individualtextfield13.setStyle();
            this.individualtextfield13
                .setConstraints( (this.individualtextfield13.getConstraints() & TextField.CONSTRAINT_MASK) );
          //#style TextField
            this.individualtextfield14.setStyle();
            this.individualtextfield14
                .setConstraints( (this.individualtextfield14.getConstraints() & TextField.CONSTRAINT_MASK) );
          //#style TextField
            this.individualtextfield15.setStyle();
            this.individualtextfield15
                .setConstraints( (this.individualtextfield15.getConstraints() & TextField.CONSTRAINT_MASK) );
          //#style TextField
            this.individualtextfield16.setStyle();
            this.individualtextfield16
                .setConstraints( (this.individualtextfield16.getConstraints() & TextField.CONSTRAINT_MASK) );
          //#style TextField
            this.individualtextfield17.setStyle();
            this.individualtextfield17
                .setConstraints( (this.individualtextfield17.getConstraints() & TextField.CONSTRAINT_MASK) );
          //#style TextField
            this.individualtextfield18.setStyle();
            this.individualtextfield18
                .setConstraints( (this.individualtextfield18.getConstraints() & TextField.CONSTRAINT_MASK) );
          //#style TextField
            this.individualtextfield19.setStyle();
            this.individualtextfield19
                .setConstraints( (this.individualtextfield19.getConstraints() & TextField.CONSTRAINT_MASK) );

        }
        if ( 1 == this.diagnosed.getSelectedIndex() )
        {
          //#style TextField
            this.individualtextfield12.setStyle();
            this.individualtextfield12
                .setConstraints( (this.individualtextfield12.getConstraints() & TextField.CONSTRAINT_MASK) );

            // disable textfield
            //#style DisabledTextField
            this.individualtextfield13.setStyle();
            this.individualtextfield13.setString( "" );
            this.individualtextfield13
                .setConstraints( (this.individualtextfield13.getConstraints() & TextField.CONSTRAINT_MASK)
                    | TextField.UNEDITABLE );
            //#style DisabledTextField
            this.individualtextfield14.setStyle();
            this.individualtextfield14.setString( "" );
            this.individualtextfield14
                .setConstraints( (this.individualtextfield14.getConstraints() & TextField.CONSTRAINT_MASK)
                    | TextField.UNEDITABLE );
            //#style DisabledTextField
            this.individualtextfield15.setStyle();
            this.individualtextfield15.setString( "" );
            this.individualtextfield15
                .setConstraints( (this.individualtextfield15.getConstraints() & TextField.CONSTRAINT_MASK)
                    | TextField.UNEDITABLE );
            //#style DisabledTextField
            this.individualtextfield16.setStyle();
            this.individualtextfield16.setString( "" );
            this.individualtextfield16
                .setConstraints( (this.individualtextfield16.getConstraints() & TextField.CONSTRAINT_MASK)
                    | TextField.UNEDITABLE );
            //#style DisabledTextField
            this.individualtextfield17.setStyle();
            this.individualtextfield17.setString( "" );
            this.individualtextfield17
                .setConstraints( (this.individualtextfield17.getConstraints() & TextField.CONSTRAINT_MASK)
                    | TextField.UNEDITABLE );
            //#style DisabledTextField
            this.individualtextfield18.setStyle();
            this.individualtextfield18.setString( "" );
            this.individualtextfield18
                .setConstraints( (this.individualtextfield18.getConstraints() & TextField.CONSTRAINT_MASK)
                    | TextField.UNEDITABLE );
            //#style DisabledTextField
            this.individualtextfield19.setStyle();
            this.individualtextfield19.setString( "" );
            this.individualtextfield19
                .setConstraints( (this.individualtextfield19.getConstraints() & TextField.CONSTRAINT_MASK)
                    | TextField.UNEDITABLE );
        }        
    }
}
