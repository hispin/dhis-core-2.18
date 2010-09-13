package org.hisp.dhis.mobile.ui;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.DataValueUploadManager;
import org.hisp.dhis.mobile.connection.DownloadManager;
import org.hisp.dhis.mobile.db.SettingsRectordStore;
import org.hisp.dhis.mobile.db.Storage;
import org.hisp.dhis.mobile.model.AbstractModel;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.DataElement;
import org.hisp.dhis.mobile.model.DataValue;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.ProgramStageForm;
import org.hisp.dhis.mobile.model.User;
import org.hisp.dhis.mobile.util.AlertUtil;
import org.hisp.dhis.mobile.util.DnlActivitiesConfirmAlertListener;
import org.hisp.dhis.mobile.util.ReinitConfirmListener;

public class DHISMIDlet
    extends MIDlet
    implements CommandListener
{
    private String serverUrl = "http://localhost:8080/api/";

    private boolean midletPaused = false;

    private User user;

    private DownloadManager downloadManager;

    private Hashtable formElements = new Hashtable();

    private Vector programStagesVector = new Vector();

    private Vector activitiesVector = new Vector();

    private OrgUnit orgUnit;

    private ProgramStageForm programStageForm;

    private Activity selectedActivity;

    private Hashtable dataValueTable = new Hashtable();

    private List mainMenuList;

    private List formDownloadList;

    private List activityList;

    private Form settingsForm;

    private TextField url;

    private Form dataEntryForm;

    private Form form;

    private Form loginForm;

    private Form pinForm;

    private Form waitForm;

    private List downloadedFormsList;

    private TextField userName;

    private TextField password;

    private Command exitCommand;

    private Command mnuListDnldCmd;

    private Command mnuListSettingCmd;

    private Command mnuListExtCmd;

    private Command frmDnldCmd;

    private Command frmDnldListBakCmd;

    private Command actvyPlnListBakCmd;

    private Command stngsOkCmd;

    private Command setngsBakCmd;

    private Command setngsSaveCmd;

    private Command deFrmBakCmd;

    private Command deFrmSavCmd;

    private Command screenCommand;

    private Command saveCommand;

    private Command backCommand;

    private Command okCommand;

    private Command lgnFrmExtCmd;

    private Command lgnFrmLgnCmd;

    // private Command orgUnitBackCmd;

    private Command pinFormNextCmd;

    private Command pinFormReinitCmd;

    private Command pinFormExitCmd;

    // add one more back command for the downloaded forms list
    private Command downloadedBckCmd;

    private Image logo;

    private TextField pinTextField;

    private TextField urlInSetting;

    /**
     * The DHISMIDlet constructor.
     */
    public DHISMIDlet()
    {
    }

    /**
     * Initilizes the application. It is called only once when the MIDlet is
     * started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize()
    {
    }

    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet()
    {
        getPinForm().addCommand( this.getPinFormExitCmd() );

        new SplashScreen( getLogo(), getDisplay(), (Displayable) getLoginForm(), (Displayable) getPinForm() );

    }

    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet()
    {
    }

    /**
     * Switches a current displayable in a display. The <code>display</code>
     * instance is taken from <code>getDisplay</code> method. This method is
     * used by all actions in the design for switching displayable.
     * 
     * @param alert the Alert which is temporarily set to the display; if
     *        <code>null</code>, then <code>nextDisplayable</code> is set
     *        immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable( Alert alert, Displayable nextDisplayable )
    {

        Display display = getDisplay();

        if ( alert == null )
        {
            display.setCurrent( nextDisplayable );
        }
        else
        {
            display.setCurrent( alert, nextDisplayable );
        }
    }

    /**
     * Called by a system to indicated that a command has been invoked on a
     * particular displayable.
     * 
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction( Command command, Displayable displayable )
    {

        if ( displayable == dataEntryForm )
        {
            if ( command == deFrmBakCmd )
            {
                switchDisplayable( null, getMainMenuList() );
            }
            else if ( command == deFrmSavCmd )
            {
                // need to send the recorded data
                sendRecordedData();
            }
        }
        else if ( displayable == form )
        {
            if ( command == backCommand )
            {
                switchDisplayable( null, getActivitiesList() );
            }
            else if ( command == screenCommand )
            {
                sendRecordedData();
            }
            else if ( command == saveCommand )
            {
                saveDataValues();
            }
        }
        else if ( displayable == formDownloadList )
        {
            if ( command == List.SELECT_COMMAND )
            {
                frmDnldListAction();
            }
            else if ( command == frmDnldListBakCmd )
            {
                switchDisplayable( null, getMainMenuList() );
            }
        }
        else if ( displayable == loginForm )
        {
            if ( command == lgnFrmExtCmd )
            {
                exitMIDlet();
            }
            else if ( command == lgnFrmLgnCmd )
            {
                login();
            }
        }
        else if ( displayable == mainMenuList )
        {
            if ( command == List.SELECT_COMMAND )
            {
                mainMenuListAction();
            }
            else if ( command == getMnuListSettingCmd() )
            {
                switchDisplayable( null, getSettingsForm() );
            }
            else if ( command == getMnuListDnldCmd() )
            {
                this.getDisplay().setCurrent(
                    AlertUtil.getConfirmAlert( "Warning",
                        "All data which are not sent to the server will be clear, do you want to continue ?",
                        new DnlActivitiesConfirmAlertListener(), this, getMainMenuList(),
                        getWaitForm( "Redownloading Activities", "Downloading.....Please wait" ) ) );
            }
            else if ( command == mnuListExtCmd )
            {
                exitMIDlet();
            }
        }
        else if ( displayable == settingsForm )
        {
            if ( command == setngsBakCmd )
            {
                switchDisplayable( null, getMainMenuList() );
            }
            else if ( command == stngsOkCmd )
            {
                saveSettings();
                switchDisplayable( null, getMainMenuList() );
            }
        }
        else if ( displayable == activityList )
        {
            if ( command == actvyPlnListBakCmd )
            {
                switchDisplayable( null, getMainMenuList() );
            }
            else if ( command == List.SELECT_COMMAND )
            {
                this.displaySelectedActivity();
            }
        }
        else if ( displayable == downloadedFormsList )
        {
            if ( command == List.SELECT_COMMAND )
            {
                this.downloadedFormsSelectAction();
            }
            else if ( command == downloadedBckCmd )
            {
                this.switchDisplayable( null, mainMenuList );
            }
        }
        else if ( displayable == pinForm )
        {
            SettingsRectordStore settingRs = null;
            if ( command == pinFormNextCmd )
            {
                // check empty pin textfield
                if ( !getPinTextField().getString().equals( "" ) )
                {
                    // case 1: Later Startup, User Information has not been
                    // loaded

                    try
                    {
                        settingRs = new SettingsRectordStore( "SETTINGS" );
                        if ( this.user == null )
                        {
                            if ( getPinTextField().getString().equals( settingRs.get( "pin" ) ) )
                            {
                                // Load User Information
                                this.user = Storage.loadUser();
                                // Load OrgUnit
                                this.orgUnit = Storage.loadOrgUnit();
                                // Load Activities
                                switchDisplayable( null,
                                    this.getWaitForm( "Load Activities", "Loading.....please wait" ) );
                                this.loadSettings();
                                this.loadForms();
                                this.loadActivities();

                            }
                            else
                            {
                                this.getPinTextField().setString( "" );
                                switchDisplayable(
                                    AlertUtil.getInfoAlert( "Error", "Wrong PIN number, please input again" ),
                                    getPinForm() );
                            }
                            // case 2: First Startup, User Information
                            // initialized
                            // from input
                        }
                        else
                        {
                            // Save URL
                            System.out.println( "save url:" + getUrl().getString() );
                            settingRs.put( "url", getUrl().getString() );
                            // Save PIN
                            settingRs.put( "pin", this.getPinTextField().getString() );
                            settingRs.save();
                            // Save User Information
                            Storage.saveUser( this.user );
                            // Clear, Download and Save activities
                            switchDisplayable( null,
                                this.getWaitForm( "Download Activities", "Downloading.....please wait" ) );
                            downloadForms();
                            downloadActivities();
                            // Download and Save Forms

                        }
                        settingRs = null;
                    }
                    catch ( RecordStoreException e )
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    switchDisplayable( AlertUtil.getInfoAlert( "Error", "You must input the 4-digit PIN code" ),
                        getPinForm() );
                }
            }
            else if ( command == pinFormReinitCmd )
            {
                this.getDisplay().setCurrent(
                    AlertUtil.getConfirmAlert( "Confirmation", "Are you sure ?", new ReinitConfirmListener(), this,
                        getPinForm(), getLoginForm() ) );
            }
            else if ( command == pinFormExitCmd )
            {
                exitMIDlet();
            }
        }
    }

    private void saveDataValues()
    {
        try
        {
            this.saveDataValueToRMS();
            this.switchDisplayable( AlertUtil.getInfoAlert( "Report", "DataValues successfully saved." ),
                this.getActivitiesList() );
        }
        catch ( RecordStoreException e )
        {
            this.switchDisplayable( AlertUtil.getInfoAlert( "Report", "Fail to save datavalues" ),
                this.getActivitiesList() );
        }
    }

    private void saveDataValueToRMS()
        throws RecordStoreException
    {
        Vector des = programStageForm.getDataElements();
        for ( int i = 0; i < des.size(); i++ )
        {
            DataElement de = (DataElement) des.elementAt( i );
            if ( dataValueTable.get( String.valueOf( de.getId() ) ) != null )
            {
                this.updateDataValue( de );
            }
            else
            {
                this.storeNewDataValue( de );
            }
        }
        loadDataValues( selectedActivity );
    }

    private void storeNewDataValue( DataElement de )
        throws RecordStoreException
    {
        if ( de.getType() == DataElement.TYPE_DATE )
        {
            DateField dateField = (DateField) formElements.get( de );
            if ( dateField.getDate() != null )
            {
                Storage.storeDataValue( getDataValue( selectedActivity.getTask().getProgStageInstId(), de.getId(),
                    String.valueOf( dateField.getDate().getTime() ) ) );
                System.out.println( "Store new: " + de.getName() );
            }
        }
        else
        {
            TextField txtField = (TextField) formElements.get( de );
            if ( !txtField.getString().equalsIgnoreCase( "" ) )
            {
                Storage.storeDataValue( getDataValue( selectedActivity.getTask().getProgStageInstId(), de.getId(),
                    txtField.getString() ) );
                System.out.println( "Store new: " + de.getName() );
            }
        }

    }

    private void updateDataValue( DataElement de )
        throws RecordStoreException
    {

        if ( de.getType() == DataElement.TYPE_DATE )
        {
            DateField dateField = (DateField) formElements.get( de );
            if ( dateField.getDate() != null )
            {
                Storage.updateDataValue(
                    selectedActivity,
                    getDataValue( selectedActivity.getTask().getProgStageInstId(), de.getId(),
                        String.valueOf( dateField.getDate().getTime() ) ) );
                System.out.println( "Updating: " + de.getName() );
            }
        }
        else
        {
            TextField txtField = (TextField) formElements.get( de );
            if ( !txtField.getString().equalsIgnoreCase( "" ) )
            {
                Storage.updateDataValue( selectedActivity,
                    getDataValue( selectedActivity.getTask().getProgStageInstId(), de.getId(), txtField.getString() ) );
                System.out.println( "Updating: " + de.getName() );
            }
            else
            {
                Storage.deleteDataValue( selectedActivity,
                    getDataValue( selectedActivity.getTask().getProgStageInstId(), de.getId(), txtField.getString() ) );
                System.out.println( "Deleting: " + de.getName() );
            }
        }

    }

    private void displaySelectedActivity()
    {
        selectedActivity = (Activity) activitiesVector.elementAt( getActivitiesList().getSelectedIndex() );
        ProgramStageForm formOfActivity = Storage.fetchForm( selectedActivity.getTask().getProgStageId() );
        this.renderForm( formOfActivity, getForm() );
    }

    private DataValue getDataValue( int progStageId, int dataElementID, String value )
    {
        DataValue dataValue = new DataValue();
        dataValue.setProgramInstanceId( progStageId );
        dataValue.setDataElementId( dataElementID );
        dataValue.setValue( value );
        return dataValue;
    }

    /**
     * Returns an vector of activities loaded from RMS
     */

    // this Thread should moved to manager class like DownloadManager
    private void loadActivities()
    {
        new Thread()
        {
            public void run()
            {
                activitiesVector = Storage.loadActivities();
                switchDisplayable( null, getMainMenuList() );
            }
        }.start();
    }

    private void loadForms()
    {
        new Thread()
        {
            public void run()
            {
                programStagesVector = Storage.loadForms();
            }
        }.start();

    }

    private void downloadForms()
    {
        DownloadManager manager = new DownloadManager( this, orgUnit.getProgramFormsLink(), user,
            DownloadManager.DOWNLOAD_FORMS );
        manager.start();

    }

    /**
     * Returns an initiliazed instance of exitCommand component.
     * 
     * @return the initialized component instance
     */
    public Command getPinFormExitCmd()
    {
        if ( pinFormExitCmd == null )
        {
            pinFormExitCmd = new Command( "Exit", Command.EXIT, 0 );
        }
        return pinFormExitCmd;
    }

    /**
     * Returns an initiliazed instance of exitCommand component.
     * 
     * @return the initialized component instance
     */
    public Command getExitCommand()
    {
        if ( exitCommand == null )
        {
            exitCommand = new Command( "Exit", Command.EXIT, 0 );
        }
        return exitCommand;
    }

    /**
     * Method to initialize the downloaded forms screen.
     */
    public List getDownloadedFormsList()
    {
        if ( downloadedFormsList == null )
        {
            downloadedFormsList = new List( "Downloaded Forms List", Choice.IMPLICIT );
            downloadedFormsList.addCommand( this.getDownloadedFormListBckCmd() );
            downloadedFormsList.setCommandListener( this );
            downloadedFormsList.setSelectedFlags( new boolean[] {} );
        }
        return downloadedFormsList;
    }

    // Action when user select a downloaded form
    private void downloadedFormsSelectAction()
    {
        int index = ((List) getDownloadedFormsList()).getSelectedIndex();

        AbstractModel downloadedProgramStage = Storage.getForm( index );

        ProgramStageForm form = Storage.fetchForm( downloadedProgramStage.getId() );
        System.out.println( "Name: " + form.getName() );
        this.renderForm( form, this.getForm() );
    }

    // the "Back" command of the downloaded forms list
    private Command getDownloadedFormListBckCmd()
    {
        if ( downloadedBckCmd == null )
        {
            downloadedBckCmd = new Command( "Back", Command.BACK, 0 );
        }
        return downloadedBckCmd;
    }

    public void displayDownloadedForms( Vector downloadedForms )
    {
        if ( downloadedForms == null )
        {
            getDownloadedFormsList().append( "No form available", null );
        }
        else
        {
            getDownloadedFormsList().deleteAll();
            for ( int i = 0; i < downloadedForms.size(); i++ )
            {
                AbstractModel programStage = (AbstractModel) downloadedForms.elementAt( i );
                getDownloadedFormsList().insert( i, programStage.getName(), null );
            }
        }
        switchDisplayable( null, this.getDownloadedFormsList() );
    }

    public List getMainMenuList()
    {
        if ( mainMenuList == null )
        {
            mainMenuList = new List( "Menu", Choice.IMPLICIT );
            // mainMenuList.append( DOWNLOAD_FORM, null );
            mainMenuList.append( "Activity Plan", null );
            mainMenuList.append( "Send Finished Records", null );
            // mainMenuList.append( "Download Activity Plan", null );
            // mainMenuList.append( "Record Data", null );
            // mainMenuList.append( "Settings", null );

            mainMenuList.addCommand( getMnuListExtCmd() );
            mainMenuList.addCommand( getMnuListDnldCmd() );
            mainMenuList.addCommand( getMnuListSettingCmd() );

            mainMenuList.setCommandListener( this );

            mainMenuList.setFitPolicy( Choice.TEXT_WRAP_DEFAULT );
            mainMenuList.setSelectedFlags( new boolean[] { false, false, false, false } );
        }
        return mainMenuList;
    }

    /**
     * Performs an action assigned to the selected list element in the
     * mainMenuList component.
     */
    public void mainMenuListAction()
    {
        String __selectedString = getMainMenuList().getString( getMainMenuList().getSelectedIndex() );
        if ( __selectedString != null )
        {
            if ( __selectedString.equals( "Activity Plan" ) )
            {
                displayCurActivities();
            }
            else if ( __selectedString.equals( "Send Finished Records" ) )
            {

            }
            // else if ( __selectedString.equals( "Record Data" ) )
            // {
            // this.displayDownloadedForms( Storage.getAllForm() );
            // }
            // else if ( __selectedString.equals( "Settings" ) )
            // {
            // loadSettings();
            // switchDisplayable( null, getSettingsForm() );
            // }
        }
    }

    /**
     * Returns an initiliazed instance of mnuListSettingCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getMnuListSettingCmd()
    {
        if ( mnuListSettingCmd == null )
        {
            mnuListSettingCmd = new Command( "Settings", Command.ITEM, 1 );
        }
        return mnuListSettingCmd;
    }

    /**
     * Returns an initiliazed instance of mnuListDnldCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getMnuListDnldCmd()
    {
        if ( mnuListDnldCmd == null )
        {
            mnuListDnldCmd = new Command( "Download Activity", Command.ITEM, 1 );
        }
        return mnuListDnldCmd;
    }

    /**
     * Returns an initiliazed instance of mnuListExtCmd component.
     * 
     * @return the initialized component instance
     */

    public Command getMnuListExtCmd()
    {
        if ( mnuListExtCmd == null )
        {
            mnuListExtCmd = new Command( "Exit", Command.EXIT, 0 );
        }
        return mnuListExtCmd;
    }

    public List getFormDownloadList()
    {
        if ( formDownloadList == null )
        {
            formDownloadList = new List( "Select form to download", Choice.IMPLICIT );
            formDownloadList.addCommand( getFrmDnldListBakCmd() );
            formDownloadList.setCommandListener( this );
            formDownloadList.setSelectedFlags( new boolean[] {} );
        }
        return formDownloadList;
    }

    public void downloadActivities()
    {
        String urlDownloadActivities = this.orgUnit.getActivitiesLink();

        downloadManager = new DownloadManager( this, urlDownloadActivities, user, DownloadManager.DOWNLOAD_ACTIVITYPLAN );
        downloadManager.start();
    }

    /**
     * Performs an action assigned to the selected list element in the
     * frmDnldList component.
     */
    public void frmDnldListAction()
    {
        ProgramStageForm programStage = (ProgramStageForm) programStagesVector
            .elementAt( ((List) getFormDownloadList()).getSelectedIndex() );
        downloadForm( programStage.getId() );
    }

    public Command getFrmDnldListBakCmd()
    {
        if ( frmDnldListBakCmd == null )
        {
            frmDnldListBakCmd = new Command( "Back", Command.BACK, 0 );
        }
        return frmDnldListBakCmd;
    }

    /**
     * Returns an initiliazed instance of frmDnldCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getFrmDnldCmd()
    {
        if ( frmDnldCmd == null )
        {
            frmDnldCmd = new Command( "Download", Command.SCREEN, 0 );
        }
        return frmDnldCmd;
    }

    /**
     * Returns an initiliazed instance of actvyPlnListBakCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getActvyPlnListBakCmd()
    {
        if ( actvyPlnListBakCmd == null )
        {
            actvyPlnListBakCmd = new Command( "Back", Command.BACK, 0 );
        }
        return actvyPlnListBakCmd;
    }

    public Form getWaitForm( String title, String msg )
    {
        if ( waitForm == null )
        {
            waitForm = new Form( title );
            waitForm.append( msg );
            return waitForm;
        }
        else
        {
            waitForm.deleteAll();
            waitForm.setTitle( title );
            waitForm.append( msg );
            return waitForm;
        }

    }

    /**
     * Returns an initiliazed instance of settingsForm component.
     * 
     * @return the initialized component instance
     */
    public Form getSettingsForm()
    {
        if ( settingsForm == null )
        {
            // settingsForm = new Form( "Configurable Parameters", new Item[] {
            // settingsForm = new Form( "Configurable Parameters", new Item[] {
            // getUrl() } );
            settingsForm = new Form( "Configurable Parameters" );
            settingsForm.append( getUrlInSetting() );
            settingsForm.addCommand( getSetngsBakCmd() );
            settingsForm.addCommand( getStngsOkCmd() );
            settingsForm.setCommandListener( this );
        }
        return settingsForm;
    }

    /**
     * Returns an initiliazed instance of stngsOkCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getStngsOkCmd()
    {
        if ( stngsOkCmd == null )
        {
            stngsOkCmd = new Command( "Save", Command.OK, 0 );
        }
        return stngsOkCmd;
    }

    /**
     * Returns an initiliazed instance of setngsBakCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getSetngsBakCmd()
    {
        if ( setngsBakCmd == null )
        {
            setngsBakCmd = new Command( "Back", Command.BACK, 0 );
        }
        return setngsBakCmd;
    }

    /**
     * Returns an initiliazed instance of setngsSaveCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getSetngsSaveCmd()
    {
        if ( setngsSaveCmd == null )
        {
            setngsSaveCmd = new Command( "Save", Command.SCREEN, 0 );
        }
        return setngsSaveCmd;
    }

    /**
     * Returns an initiliazed instance of url component.
     * 
     * @return the initialized component instance
     */
    public TextField getUrl()
    {
        if ( url == null )
        {
            url = new TextField( "Server Location", serverUrl, 64, TextField.URL );
        }
        return url;
    }

    /**
     * Returns an initiliazed instance of url component in setting form.
     * 
     * @return the initialized component instance
     */
    private TextField getUrlInSetting()
    {
        String urlBase = "";
        try
        {
            SettingsRectordStore settingRs = new SettingsRectordStore( "SETTINGS" );
            urlBase = settingRs.get( "url" );
            System.out.println( "Base URL:" + urlBase );
            settingRs = null;
        }
        catch ( RecordStoreException e )
        {
            e.printStackTrace();
        }
        if ( urlInSetting == null )
        {
            urlInSetting = new TextField( "URL Server", urlBase, 64, TextField.URL );
        }
        else
        {
            urlInSetting.setString( urlBase );
        }
        return urlInSetting;
    }

    /**
     * Returns an initiliazed instance of dataEntryForm component.
     * 
     * @return the initialized component instance
     */
    public Form getDataEntryForm()
    {
        if ( dataEntryForm == null )
        {
            dataEntryForm = new Form( "form", new Item[] {} );
            dataEntryForm.addCommand( getDeFrmBakCmd() );
            dataEntryForm.addCommand( getDeFrmSavCmd() );
            dataEntryForm.setCommandListener( this );
        }
        return dataEntryForm;
    }

    /**
     * Returns an initiliazed instance of deFrmBakCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getDeFrmBakCmd()
    {
        if ( deFrmBakCmd == null )
        {
            deFrmBakCmd = new Command( "Back", Command.BACK, 0 );
        }
        return deFrmBakCmd;
    }

    /**
     * Returns an initiliazed instance of deFrmSavCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getDeFrmSavCmd()
    {
        if ( deFrmSavCmd == null )
        {
            deFrmSavCmd = new Command( "Save", Command.SCREEN, 0 );
        }
        return deFrmSavCmd;
    }

    /**
     * Returns an initiliazed instance of form component.
     * 
     * @return the initialized component instance
     */
    public Form getForm()
    {
        if ( form == null )
        {
            form = new Form( "Form" );
            form.addCommand( getBackCommand() );
            form.addCommand( getScreenCommand() );
            form.addCommand( getSaveCommand() );
            form.setCommandListener( this );
        }
        else
        {
            form.deleteAll();
        }
        return form;
    }

    private Command getSaveCommand()
    {
        if ( saveCommand == null )
        {
            saveCommand = new Command( "Save", Command.SCREEN, 0 );
        }
        return saveCommand;
    }

    public Form getPinForm()
    {
        if ( pinForm == null )
        {
            pinForm = new Form( "Enter a 4 digit PIN" );
            pinForm.append( this.getPinTextField() );
            pinForm.addCommand( this.getPinFormNextCmd() );
            pinForm.addCommand( this.getPinFormReinitCmd() );
            pinForm.setCommandListener( this );
        }
        else if ( pinForm != null )
        {
            getPinTextField().setString( "" );
        }
        return pinForm;
    }

    private Command getPinFormReinitCmd()
    {
        if ( pinFormReinitCmd == null )
        {
            pinFormReinitCmd = new Command( "ReInit", Command.SCREEN, 1 );
        }
        return pinFormReinitCmd;
    }

    private Command getPinFormNextCmd()
    {
        if ( pinFormNextCmd == null )
        {
            pinFormNextCmd = new Command( "Next", Command.SCREEN, 0 );
        }
        return pinFormNextCmd;
    }

    private TextField getPinTextField()
    {
        if ( pinTextField == null )
        {
            pinTextField = new TextField( "PIN", "", 4, TextField.NUMERIC | TextField.PASSWORD );

        }
        return pinTextField;
    }

    public void afterInit()
    {
        switchDisplayable( null, getMainMenuList() );
    }

    // Real downloaded forms select
    // public Form getForm( ProgramStageForm selectedForm )
    // {
    // if ( form == null )
    // {
    // form = new Form( "From" );
    // form.addCommand( getBackCommand() );
    // form.addCommand( getScreenCommand() );
    // form.addCommand( getSaveCommand() );
    // form.setCommandListener( this );
    // renderForm( selectedForm, form );
    // }
    // else
    // {
    // form.deleteAll();
    // renderForm( selectedForm, form );
    // }
    //
    // return form;
    // }

    /**
     * Returns an initiliazed instance of backCommand component.
     * 
     * @return the initialized component instance
     */
    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( "Back", Command.BACK, 0 );
        }
        return backCommand;
    }

    /**
     * Returns an initiliazed instance of screenCommand component.
     * 
     * @return the initialized component instance
     */
    public Command getScreenCommand()
    {
        if ( screenCommand == null )
        {
            screenCommand = new Command( "Send", Command.SCREEN, 0 );
        }
        return screenCommand;
    }

    public Form getLoginForm()
    {
        if ( loginForm == null )
        {
            loginForm = new Form( "Please login", new Item[] { getUserName(), getPassword(), getUrl() } );
            loginForm.addCommand( getLgnFrmExtCmd() );
            loginForm.addCommand( getLgnFrmLgnCmd() );
            loginForm.setCommandListener( this );
        }
        else
        {
            getPassword().setString( "" );
        }
        return loginForm;
    }

    public TextField getUserName()
    {
        if ( userName == null )
        {
            userName = new TextField( "Username", "", 32, TextField.ANY | TextField.SENSITIVE );
        }
        return userName;
    }

    public TextField getPassword()
    {
        if ( password == null )
        {
            password = new TextField( "Password", null, 32, TextField.ANY | TextField.PASSWORD );
        }
        return password;
    }

    /**
     * Returns an initiliazed instance of lgnFrmExtCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getLgnFrmExtCmd()
    {
        if ( lgnFrmExtCmd == null )
        {
            lgnFrmExtCmd = new Command( "Exit", Command.EXIT, 0 );
        }
        return lgnFrmExtCmd;
    }

    public Command getOkCommand()
    {
        if ( okCommand == null )
        {
            okCommand = new Command( "Ok", Command.OK, 0 );
        }
        return okCommand;
    }

    public Command getLgnFrmLgnCmd()
    {
        if ( lgnFrmLgnCmd == null )
        {
            lgnFrmLgnCmd = new Command( "Login", Command.SCREEN, 0 );
        }
        return lgnFrmLgnCmd;
    }

    public Image getLogo()
    {
        if ( logo == null )
        {
            try
            {
                logo = Image.createImage( "/logos/dhis2.png" );
            }
            catch ( java.io.IOException e )
            {
                e.printStackTrace();
            }
        }
        return logo;
    }

    public Display getDisplay()
    {
        return Display.getDisplay( this );
    }

    public void exitMIDlet()
    {
        switchDisplayable( null, null );
        destroyApp( true );
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started. Checks whether the MIDlet have been
     * already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp()
    {
        if ( midletPaused )
        {
            resumeMIDlet();
        }
        else
        {
            initialize();
            startMIDlet();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp()
    {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     * 
     * @param unconditional if true, then the MIDlet has to be unconditionally
     *        terminated and all resources has to be released.
     */
    public void destroyApp( boolean unconditional )
    {
    }

    private void login()
    {
        if ( getUserName().getString() != null && getPassword().getString() != null )
        {
            String username = getUserName().getString().trim();
            String password = getPassword().getString().trim();
            if ( username.length() != 0 && password.length() != 0 )
            {
                user = new User( username, password );
            }
        }
        if ( user != null )
        {
            DownloadManager downloadManager = new DownloadManager( this, getUrl().getString() + "user", user,
                DownloadManager.DOWNLOAD_ORGUNIT );
            switchDisplayable( null, getWaitForm( "Connecting", "Please wait..." ) );
            downloadManager.start();
        }
        else
        {
            this.error( "Username and password cannot be empty" );
        }
    }

    private void saveSettings()
    {
        SettingsRectordStore settingsRecord;

        try
        {
            settingsRecord = new SettingsRectordStore( "SETTINGS" );
            settingsRecord.put( "url", urlInSetting.getString() );
            getUrl().setString( urlInSetting.getString() );
            this.orgUnit.setActivitiesLink( urlInSetting.getString()
                + this.orgUnit.getActivitiesLink().substring( this.orgUnit.getActivitiesLink().indexOf( "orgUnits" ) ) );
            this.orgUnit.setProgramFormsLink( urlInSetting.getString()
                + this.orgUnit.getProgramFormsLink()
                    .substring( this.orgUnit.getProgramFormsLink().indexOf( "orgUnits" ) ) );

            this.saveOrgUnit( this.orgUnit );
            // settingsRecord.put( "adminPass", adminPass.getString() );
            settingsRecord.save();
        }
        catch ( RecordStoreException rse )
        {
        }
        settingsRecord = null;
    }

    private void loadSettings()
    {
        SettingsRectordStore settingsRecord;

        try
        {
            settingsRecord = new SettingsRectordStore( "SETTINGS" );

            getUrl().setString( settingsRecord.get( "url" ) );
            settingsRecord = null;
        }
        catch ( RecordStoreException rse )
        {
        }
    }

    // private void browseForms()
    // {
    // // loadSettings();
    // downloadManager = new DownloadManager( this, serverUrl + "forms", user,
    // DownloadManager.DOWNLOAD_FORMS );
    // downloadManager.start();
    // }

    public void displayFormsForDownload( Vector forms )
    {
        programStagesVector = forms;

        if ( forms == null )
        {
            getFormDownloadList().append( "No forms available", null );
        }
        else
        {
            getFormDownloadList().deleteAll();
            for ( int i = 0; i < forms.size(); i++ )
            {
                AbstractModel programStage = (AbstractModel) forms.elementAt( i );
                getFormDownloadList().insert( i, programStage.getName(), null );
            }
        }

        switchDisplayable( null, getFormDownloadList() );
    }

    private void downloadForm( int formId )
    {
        loadSettings();
        downloadManager = new DownloadManager( this, serverUrl + "forms/" + formId, user, DownloadManager.DOWNLOAD_FORM );
        downloadManager.start();
    }

    public void saveActivities( Vector activitiesVector )
    {
        this.activitiesVector = activitiesVector;
        Storage.storeActivities( activitiesVector );
    }

    public void displayCurActivities()
    {
        if ( activitiesVector == null || activitiesVector.size() == 0 )
        {
            getActivitiesList().deleteAll();
            getActivitiesList().append( "No Activity Available", null );
        }
        else
        {
            getActivitiesList().deleteAll();
            for ( int i = 0; i < activitiesVector.size(); i++ )
            {
                Activity activity = (Activity) activitiesVector.elementAt( i );
                getActivitiesList().append(
                    "Beneficiary: " + activity.getBeneficiary().getFullName() + "\n" + "Due Date: "
                        + activity.getDueDate().toString() + "\n", null );
                activity = null;
            }
        }
        switchDisplayable( null, activityList );
    }

    public List getActivitiesList()
    {
        if ( activityList == null )
        {
            activityList = new List( "Current Activities", Choice.IMPLICIT );
            activityList.addCommand( getActvyPlnListBakCmd() );
            activityList.setCommandListener( this );
        }
        return activityList;
    }

    public void saveForm( ProgramStageForm programStageForm )
    {
        Storage.storeForm( programStageForm );
    }

    public void renderForm( ProgramStageForm prStgFrm, Form form )
    {
        loadDataValues( selectedActivity );
        programStageForm = prStgFrm;

        if ( prStgFrm == null )
        {
            form.append( "The requested form is not available" );
        }
        else
        {
            form.deleteAll();
            form.setTitle( prStgFrm.getName() );
            Vector des = prStgFrm.getDataElements();

            for ( int i = 0; i < des.size(); i++ )
            {
                DataElement de = (DataElement) des.elementAt( i );
                if ( de.getType() == DataElement.TYPE_DATE )
                {
                    DateField dateField = new DateField( de.getName(), DateField.DATE );
                    if ( dataValueTable.get( String.valueOf( de.getId() ) ) != null )
                    {
                        Date date = new Date();
                        date.setTime( Long.parseLong( ((DataValue) dataValueTable.get( String.valueOf( de.getId() ) ))
                            .getValue() ) );
                        dateField.setDate( date );
                        System.out.println( "Date in db is: " + date.toString() );
                    }
                    form.append( dateField );
                    formElements.put( de, dateField );
                }
                else if ( de.getType() == DataElement.TYPE_INT )
                {
                    TextField intField = new TextField( de.getName(), "", 32, TextField.NUMERIC );
                    if ( dataValueTable.get( String.valueOf( de.getId() ) ) != null )
                    {
                        intField
                            .setString( ((DataValue) dataValueTable.get( String.valueOf( de.getId() ) )).getValue() );
                    }
                    form.append( intField );
                    formElements.put( de, intField );
                }
                else
                {
                    TextField txtField = new TextField( de.getName(), "", 32, TextField.ANY );
                    if ( dataValueTable.get( String.valueOf( de.getId() ) ) != null )
                    {
                        txtField
                            .setString( ((DataValue) dataValueTable.get( String.valueOf( de.getId() ) )).getValue() );
                    }
                    form.append( txtField );
                    formElements.put( de, txtField );
                }
            }
        }

        switchDisplayable( null, form );
    }

    private void loadDataValues( Activity activity )
    {
        dataValueTable = Storage.loadDataValues( selectedActivity );
    }

    public void saveOrgUnit( OrgUnit orgunit )
    {
        this.orgUnit = orgunit;
        Storage.saveOrgUnit( orgUnit );
    }

    public void sendRecordedData()
    {
        // Need more test
        try
        {
            this.saveDataValueToRMS();
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
        // If you are running Apache Tomcat, use the URL
        // http://localhost:8080/dhis-web-api/dhis-web-api/importDataValue.action
        // Otherwise, use http://localhost:8080/dhis-web-api/importDataValue.action for Jetty
        DataValueUploadManager uploadManager = new DataValueUploadManager( this, dataValueTable,
            "http://localhost:8080/dhis-web-api/importDataValue.action", orgUnit, user );
        this.switchDisplayable( null, this.getWaitForm( "Please wait", "Uploading..." ) );
        uploadManager.start();

    }

    public void error( String error )
    {
        switchDisplayable( AlertUtil.getErrorAlert( "Problem with server", error ), getLoginForm() );
    }

    public void loginNeeded()
    {
        switchDisplayable( AlertUtil.getInfoAlert( "Login failed", "Username/password was wrong" ), getLoginForm() );
    }

    public void saveForms( Vector downloadedProgramStagesVector )
    {
        this.programStagesVector = downloadedProgramStagesVector;
        Storage.storeForms( this.programStagesVector );
    }
}
