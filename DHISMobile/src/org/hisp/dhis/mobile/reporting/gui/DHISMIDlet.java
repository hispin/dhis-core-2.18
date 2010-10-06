package org.hisp.dhis.mobile.reporting.gui;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;
import org.hisp.dhis.mobile.reporting.connection.ConnectionManager;
import org.hisp.dhis.mobile.reporting.db.ActivityRecordStore;
import org.hisp.dhis.mobile.reporting.db.ModelRecordStore;
import org.hisp.dhis.mobile.reporting.db.ProgramStageRecordStore;
import org.hisp.dhis.mobile.reporting.db.SettingsRecordStore;
import org.hisp.dhis.mobile.reporting.model.AbstractModel;
import org.hisp.dhis.mobile.reporting.model.Activity;
import org.hisp.dhis.mobile.reporting.model.ActivityPlan;
import org.hisp.dhis.mobile.reporting.model.ActivityValue;
import org.hisp.dhis.mobile.reporting.model.DataElement;
import org.hisp.dhis.mobile.reporting.model.DataSet;
import org.hisp.dhis.mobile.reporting.model.DataSetValue;
import org.hisp.dhis.mobile.reporting.model.DataValue;
import org.hisp.dhis.mobile.reporting.model.Period;
import org.hisp.dhis.mobile.reporting.model.Program;
import org.hisp.dhis.mobile.reporting.model.ProgramStage;
import org.hisp.dhis.mobile.reporting.util.AlertUtil;
import org.hisp.dhis.mobile.reporting.util.ReinitConfirmListener;

/**
 * @author abyotag_adm
 */
public class DHISMIDlet extends MIDlet implements CommandListener {

	private boolean midletPaused = false;
	private boolean login = false;

	Vector dataSetsVector = new Vector();
	Vector programsVector = new Vector();
	Vector activitiesVector = new Vector();
	
	private DataSet selectedDataSet;
	private Activity selectedActivity;
	private ProgramStage selectedProgramStage;
	
	private Hashtable dataElements = new Hashtable();

	
	private String selectedPeriod;
	private DateField dailyPeriodDateField;
	private TextField url;
	private TextField dhisUserPass;
	private TextField locale;
	private TextField dhisUserName;
	private TextField userName;
	private TextField password;
	private TextField serverURL;
	private TextField pinTextField;
	private ChoiceGroup periodChoice;
	private Alert successAlert;
	private Alert errorAlert;
	private Form activityEntryForm;
	private Form dataEntryForm;
	private Form loginForm;
	private Form pinForm;
	private Form settingsForm;
	private Form periodForm;
	private Form waitForm;		
	private Form activityDetailForm;
	private List activityPlanList;	
	private List dataSetDisplayList;
	private List servicesList;
	private List maintenanceList;
	private List deleteList;
	private List deleteProgList;
	private List mainMenuList;
	private List dsDnldList;
	private List prDnldList;
	private Command exitCommand;
	private Command mnuListDnldCmd;
	private Command mnuListExtCmd;
	private Command frmDnldCmd;
	private Command dsDnldListBakCmd;
	private Command prDnldListBakCmd;
	private Command actvPlnLstBakCmd;
	private Command activityDetailCmd;
	private Command activityDetailOkCmd;
	private Command stngsOkCmd;
	private Command setngsBakCmd;
	private Command setngsSaveCmd;	
	private Command deFrmBakCmd;
	private Command deFrmSndCmd;
	private Command deFrmSavCmd;	
	private Command activityFrmBakCmd;
	private Command activityFrmSndCmd;
	private Command activityFrmSavCmd;	
	private Command screenCommand;
	private Command backCommand;
	private Command okCommand;
	private Command lgnFrmExtCmd;
	private Command lgnFrmLgnCmd;
	private Command dsLstBakCmd;
	private Command periodBakCmd;
	private Command mntnceBakCmd;
	private Command srvcsBakCmd;
	private Command deleteBakCmd;
	private Command periodNxtCmd;
	private Command dsDeleteCmd;
	private Command selectDailyPeriodCmd;
	private Command pinFormNextCmd;
	private Command pinFormReinitCmd;
	private Image logo;

	/**
	 * The DHISMIDlet constructor.
	 */
	public DHISMIDlet() {
	}

	/**
	 * Initilizes the application. It is called only once when the MIDlet is
	 * started. The method is called before the <code>startMIDlet</code> method.
	 */
	private void initialize() {
	}

	/**
	 * Performs an action assigned to the Mobile Device - MIDlet Started point.
	 */
	public void startMIDlet() {
		new SplashScreen(getLogo(), getDisplay(), (Displayable) getLoginForm(), (Displayable) getPinForm());
	}

	/**
	 * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
	 */
	public void resumeMIDlet() {
	}

	/**
	 * Switches a current displayable in a display. The <code>display</code>
	 * instance is taken from <code>getDisplay</code> method. This method is
	 * used by all actions in the design for switching displayable.
	 * 
	 * @param alert
	 *            the Alert which is temporarily set to the display; if
	 *            <code>null</code>, then <code>nextDisplayable</code> is set
	 *            immediately
	 * @param nextDisplayable
	 *            the Displayable to be set
	 */
	public void switchDisplayable(Alert alert, Displayable nextDisplayable) {
		Display display = getDisplay();
		if (alert == null) {
			display.setCurrent(nextDisplayable);
		} else {
			display.setCurrent(alert, nextDisplayable);
		}
	}

	/**
	 * Called by a system to indicated that a command has been invoked on a
	 * particular displayable.
	 * 
	 * @param command
	 *            the Command that was invoked
	 * @param displayable
	 *            the Displayable where the command was invoked
	 */
	public void commandAction(Command command, Displayable displayable) 
	{
		if (displayable == dataEntryForm) 
		{
			if (command == deFrmBakCmd) 
			{
				switchDisplayable(null, getDataSetDisplayList());
			} 
			else if (command == deFrmSndCmd) 
			{
				sendData();
			}
			else if (command == deFrmSavCmd) 
			{
				saveData();
			}			
		}
		if (displayable == activityEntryForm) 
		{
			if (command == activityFrmBakCmd) 
			{
				switchDisplayable(null, getActivityPlanList());
			} 
			else if (command == activityFrmSndCmd) 
			{
				sendActivity();
			}
			else if (command == activityFrmSavCmd) 
			{
				saveActivity();
			}			
		} 
		else if (displayable == dataSetDisplayList) 
		{
			if (command == List.SELECT_COMMAND) 
			{
				dataSetDisplayListAction();
			} else if (command == dsLstBakCmd) 
			{
				switchDisplayable(null, getServicesList());
			}
		} 
		else if (displayable == deleteList) 
		{
			if (command == List.SELECT_COMMAND) 
			{
				deleteListAction();
			} 
			else if (command == deleteBakCmd) 
			{
				switchDisplayable(null, getMaintenanceList());
			} 
			else if (command == dsDeleteCmd) 
			{
				AbstractModel model = (AbstractModel) dataSetsVector.elementAt(((List) getDeleteList()).getSelectedIndex());
				deleteDataSet(model);
				switchDisplayable(null, getMaintenanceList());
			}
		}
		else if(displayable == deleteProgList)
		{
		    if(command == List.SELECT_COMMAND){
		        AbstractModel model = (AbstractModel) programsVector.elementAt(((List) getDeleteProgList()).getSelectedIndex());
		        deleteProgramsAndProgramStages(model);
		    }else if(command == deleteBakCmd){
		        switchDisplayable( null, getMaintenanceList() );
		    }
		}
		else if (displayable == dsDnldList) 
		{
			if (command == List.SELECT_COMMAND) 
			{
				dsDnldListAction();
			} 
			else if (command == dsDnldListBakCmd) 
			{
				switchDisplayable(null, getMaintenanceList());
			}
		} 
		else if (displayable == prDnldList) 
		{
			if (command == List.SELECT_COMMAND) 
			{
				prDnldListAction();
			} 
			else if (command == prDnldListBakCmd) 
			{
				switchDisplayable(null, getMaintenanceList());
			}
		}
		else if (displayable == loginForm) 
		{
			if (command == lgnFrmExtCmd) 
			{
				exitMIDlet();
			} else if (command == lgnFrmLgnCmd) 
			{
				switchDisplayable(null, getWaitForm());
				login();
//				switchDisplayable(null, getMainMenuList());
			}
		} 
		else if (displayable == mainMenuList) 
		{
			if (command == List.SELECT_COMMAND) 
			{
				mainMenuListAction();
			} 
			else if (command == mnuListExtCmd) 
			{
				exitMIDlet();
			}		
		}
		else if (displayable == servicesList) 
		{
			if (command == List.SELECT_COMMAND) 
			{
				servicesListAction();
			} 
			else if (command == srvcsBakCmd) 
			{
				switchDisplayable(null, getMainMenuList());
			} 
		} 		
		else if (displayable == activityPlanList) 
		{
			if (command == List.SELECT_COMMAND) 
			{
				activityListAction();
			} 
			else if (command == actvPlnLstBakCmd) 
			{
				switchDisplayable(null, getServicesList());
			}
			else if (command == activityDetailCmd) 
			{				
				activityDetailsAction();
				switchDisplayable(null, getActivityDetailForm());
			}
		} 
		else if (displayable == activityDetailForm) 
		{
			if (command == activityDetailOkCmd) 
			{
				switchDisplayable(null, getActivityPlanList());
			}			
		}
		else if (displayable == maintenanceList) 
		{
			if (command == List.SELECT_COMMAND) 
			{
				maintenanceListAction();
			} 
			else if (command == mntnceBakCmd) 
			{
				switchDisplayable(null, getMainMenuList());
			}
		} 
		else if (displayable == periodForm) 
		{
			if (command == periodBakCmd) 
			{
				switchDisplayable(null, getDataSetDisplayList());
			} 
			else if (command == selectDailyPeriodCmd) 
			{
				getDisplay().setCurrent(new CalendarCanvas(this));
			} 
			else if (command == periodNxtCmd) 
			{
				switchDisplayable(null, getDataEntryForm());

				if (selectedDataSet.getPeriodType().equals("Daily")) 
				{
					selectedPeriod = Period.formatDailyPeriod(dailyPeriodDateField.getDate());
				} 
				else if (selectedDataSet.getPeriodType().equals("Weekly")) 
				{
					selectedPeriod = Period.formatWeeklyPeriod(getPeriodChoice().getString(getPeriodChoice().getSelectedIndex()));
				} 
				else if (selectedDataSet.getPeriodType().equals("Monthly")) 
				{
					selectedPeriod = Period.formatMonthlyPeriod(getPeriodChoice().getString(getPeriodChoice().getSelectedIndex()));
				} 
				else 
				{
					selectedPeriod = getPeriodChoice().getString(getPeriodChoice().getSelectedIndex());
				}

				displayDataEntry(selectedDataSet, getDataEntryForm());
			}
		} 
		else if (displayable == settingsForm) 
		{
			if (command == setngsBakCmd) 
			{
				switchDisplayable(null, getMainMenuList());
			} 
			else if (command == setngsSaveCmd) 
			{
				saveSettings();
				switchDisplayable(null, getMainMenuList());
			} 
		} else if (displayable == pinForm) {
			if (command == pinFormNextCmd) {
				checkPIN();
			} else if (command == exitCommand){
				exitMIDlet();
			} else if (command == pinFormReinitCmd) {
				ReinitConfirmListener listener = new ReinitConfirmListener();
				listener.setCurrentScrren(getPinForm());
				listener.setNextScreen(getLoginForm());
				this.getDisplay().setCurrent(AlertUtil.getConfirmAlert("Reinisialize", "All of the data will be lost. Are you sure you want to reinit", listener, this, getPinForm(), getLoginForm()));
			}
		}
	}
		
	private void checkPIN() {
		SettingsRecordStore settingRs = null;
        try
        {
            settingRs = new SettingsRecordStore( SettingsRecordStore.SETTINGS_DB );
            if ( settingRs.get( "pin" ).equals(""))
            {
                if (!getPinTextField().getString().equals("")){
                	settingRs.put("pin", getPinTextField().getString().trim());
                	settingRs.save();
                	switchDisplayable(null, getMainMenuList());
                } else {
                	switchDisplayable(AlertUtil.getInfoAlert("Error", "PIN cannot be empty"), getPinForm());
                }
            } else {
            	if (settingRs.get( "pin" ).equals(getPinTextField().getString())){
            		switchDisplayable(null, getMainMenuList());
            	} else {
            		switchDisplayable(AlertUtil.getInfoAlert("Error", "Ivalid PIN"), getPinForm());
            	}
            }
            settingRs = null;
        }
        catch ( RecordStoreException e )
        {
            e.printStackTrace();
        }
	}

	/**
	 * Returns an initiliazed instance of exitCommand component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getExitCommand() {
		if (exitCommand == null) {
			exitCommand = new Command("Exit", Command.EXIT, 0);
		}
		return exitCommand;
	}

	/**
	 * Returns an initiliazed instance of mainMenuList component.
	 * 
	 * @return the initialized component instance
	 */
	public List getMainMenuList() {
		if (mainMenuList == null) {
			mainMenuList = new List("Main Menu", Choice.IMPLICIT);
			mainMenuList.append("Services", null);
			mainMenuList.append("Maintenance", null);
			mainMenuList.append("Settings", null);
			mainMenuList.addCommand(getMnuListExtCmd());
			mainMenuList.setCommandListener(this);
			mainMenuList.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
			mainMenuList
					.setSelectedFlags(new boolean[] { false, false, false });
		}
		return mainMenuList;
	}

	/**
	 * Performs an action assigned to the selected list element in the
	 * mainMenuList component.
	 */
	public void mainMenuListAction() {
		String __selectedString = getMainMenuList().getString(
				getMainMenuList().getSelectedIndex());
		if (__selectedString != null) {
			if (__selectedString.equals("Services")) {
				switchDisplayable(null, getServicesList());				
			} else if (__selectedString.equals("Maintenance")) {
				switchDisplayable(null, getMaintenanceList());
			} else if (__selectedString.equals("Settings")) {
				loadSettings();
				switchDisplayable(null, getSettingsForm());
			}
		}
	}

	/**
	 * Returns an initiliazed instance of mnuListExtCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getMnuListExtCmd() {
		if (mnuListExtCmd == null) {
			mnuListExtCmd = new Command("Exit", Command.EXIT, 0);
		}
		return mnuListExtCmd;
	}

	/**
	 * Returns an initiliazed instance of mnuListDnldCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getMnuListDnldCmd() {
		if (mnuListDnldCmd == null) {
			mnuListDnldCmd = new Command("Download", Command.SCREEN, 0);
		}
		return mnuListDnldCmd;
	}
	
	/**
	 * Returns an initiliazed instance of dsDnldList component.
	 * 
	 * @return the initialized component instance
	 */
	public List getPrDnldList() {
		if (prDnldList == null) {
			prDnldList = new List("Select program to download", Choice.IMPLICIT);
			prDnldList.addCommand(getPrDnldListBakCmd());
			prDnldList.setCommandListener(this);
			prDnldList.setSelectedFlags(new boolean[] {});
		}
		return prDnldList;
	}	
	
	/**
	 * Performs an action assigned to the selected list element in the
	 * prDnldList component.
	 */
	public void prDnldListAction() {
		AbstractModel model = (AbstractModel) programsVector
				.elementAt(((List) getPrDnldList()).getSelectedIndex());

		getWaitForm().deleteAll();		
		getWaitForm().setTitle("Downloading program");
		getWaitForm().append("Please wait........");
		
		switchDisplayable(null, getWaitForm());
		
		downloadProgram(model.getId());

	}


	/**
	 * Returns an initiliazed instance of dsDnldList component.
	 * 
	 * @return the initialized component instance
	 */
	public List getDsDnldList() {
		if (dsDnldList == null) {
			dsDnldList = new List("Select dataset to download", Choice.IMPLICIT);
			dsDnldList.addCommand(getDsDnldListBakCmd());
			dsDnldList.setCommandListener(this);
			dsDnldList.setSelectedFlags(new boolean[] {});
		}
		return dsDnldList;
	}

	/**
	 * Performs an action assigned to the selected list element in the
	 * dsDnldList component.
	 */
	public void dsDnldListAction() {
		AbstractModel model = (AbstractModel) dataSetsVector
				.elementAt(((List) getDsDnldList()).getSelectedIndex());

		getWaitForm().deleteAll();
		
		getWaitForm().setTitle("Downloading dataset");
		getWaitForm().append("Please wait........");
		
		switchDisplayable(null, getWaitForm());
		
		downloadDataSet(model.getId());

	}
	
	/**
	 * Returns an initiliazed instance of prDnldListBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getPrDnldListBakCmd() {
		if (prDnldListBakCmd == null) {
			prDnldListBakCmd = new Command("Back", Command.BACK, 0);
		}
		return prDnldListBakCmd;
	}

	/**
	 * Returns an initiliazed instance of dsDnldListBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getDsDnldListBakCmd() {
		if (dsDnldListBakCmd == null) {
			dsDnldListBakCmd = new Command("Back", Command.BACK, 0);
		}
		return dsDnldListBakCmd;
	}

	/**
	 * Returns an initiliazed instance of frmDnldCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getFrmDnldCmd() {
		if (frmDnldCmd == null) {
			frmDnldCmd = new Command("Download", Command.SCREEN, 0);
		}
		return frmDnldCmd;
	}

	/**
	 * Returns an initiliazed instance of actvPlnLstBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getActvPlnLstBakCmd() {
		if (actvPlnLstBakCmd == null) {
			actvPlnLstBakCmd = new Command("Back", Command.BACK, 0);
		}
		return actvPlnLstBakCmd;
	}

	/**
	 * Returns an initiliazed instance of settingsForm component.
	 * 
	 * @return the initialized component instance
	 */
	public Form getSettingsForm() {
		if (settingsForm == null) {
			settingsForm = new Form("Configurable Parameters",
					new Item[] { getUrl(), getDhisUserName(),
							getDhisUserPass(), getLocale() });
			settingsForm.addCommand(getSetngsSaveCmd());
			settingsForm.addCommand(getSetngsBakCmd());
			settingsForm.setCommandListener(this);
		}
		return settingsForm;
	}

	/**
	 * Returns an initiliazed instance of stngsOkCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getStngsOkCmd() {
		if (stngsOkCmd == null) {
			stngsOkCmd = new Command("Save", Command.OK, 0);
		}
		return stngsOkCmd;
	}

	/**
	 * Returns an initiliazed instance of setngsBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getSetngsBakCmd() {
		if (setngsBakCmd == null) {
			setngsBakCmd = new Command("Back", Command.BACK, 0);
		}
		return setngsBakCmd;
	}

	/**
	 * Returns an initiliazed instance of setngsSaveCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getSetngsSaveCmd() {
		if (setngsSaveCmd == null) {
			setngsSaveCmd = new Command("Save", Command.SCREEN, 0);
		}
		return setngsSaveCmd;
	}

	/**
	 * Returns an initiliazed instance of url component.
	 * 
	 * @return the initialized component instance
	 */
	public TextField getUrl() {
		if (url == null) {
			url = new TextField("Server Location", "http://localhost:8080/",
					64, TextField.URL);
		}
		return url;
	}

	/**
	 * Returns an initiliazed instance of dhisUserPass component.
	 * 
	 * @return the initialized component instance
	 */
	public TextField getDhisUserPass() {
		if (dhisUserPass == null) {
			dhisUserPass = new TextField("DHIS User Password", "", 32,
					TextField.ANY | TextField.PASSWORD);
		}
		return dhisUserPass;
	}

	/**
	 * Returns an initiliazed instance of dataEntryForm component.
	 * 
	 * @return the initialized component instance
	 */
	public Form getDataEntryForm() {
		if (dataEntryForm == null) {
			dataEntryForm = new Form("form", new Item[] {});
			dataEntryForm.addCommand(getDeFrmSndCmd());
			dataEntryForm.addCommand(getDeFrmSavCmd());
			dataEntryForm.addCommand(getDeFrmBakCmd());
			dataEntryForm.setCommandListener(this);
		}
		return dataEntryForm;
	}
	
	/**
	 * Returns an initiliazed instance of activityEntryForm component.
	 * 
	 * @return the initialized component instance
	 */
	public Form getActivityEntryForm() {
		if (activityEntryForm == null) {
			activityEntryForm = new Form("form", new Item[] {});
			activityEntryForm.addCommand(getActivityFrmSndCmd());
			activityEntryForm.addCommand(getActivityFrmSavCmd());
			activityEntryForm.addCommand(getActivityFrmBakCmd());
			activityEntryForm.setCommandListener(this);
		}
		return activityEntryForm;
	}
	
	/**
	 * Returns an initiliazed instance of activityDetailCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getActivityDetailCmd() {
		if (activityDetailCmd == null) {
			activityDetailCmd = new Command("Details", Command.SCREEN, 0);
		}
		return activityDetailCmd;
	}
	
	/**
	 * Returns an initiliazed instance of activityDetailOkCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getActivityDetailOkCmd() {
		if (activityDetailOkCmd == null) {
			activityDetailOkCmd = new Command("OK", Command.OK, 0);
		}
		return activityDetailOkCmd;
	}

	/**
	 * Returns an initiliazed instance of deFrmBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getDeFrmBakCmd() {
		if (deFrmBakCmd == null) {
			deFrmBakCmd = new Command("Back", Command.BACK, 0);
		}
		return deFrmBakCmd;
	}
	

	/**
	 * Returns an initiliazed instance of deFrmSndCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getDeFrmSndCmd() {
		if (deFrmSndCmd == null) {
			deFrmSndCmd = new Command("Send", Command.SCREEN, 0);
		}
		return deFrmSndCmd;
	}
	
	/**
	 * Returns an initiliazed instance of deFrmSavCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getDeFrmSavCmd() {
		if (deFrmSavCmd == null) {
			deFrmSavCmd = new Command("Save", Command.SCREEN, 1);
		}
		return deFrmSavCmd;
	}
	
	/**
	 * Returns an initiliazed instance of activityFrmBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getActivityFrmBakCmd() {
		if (activityFrmBakCmd == null) {
			activityFrmBakCmd = new Command("Back", Command.BACK, 0);
		}
		return activityFrmBakCmd;
	}
	

	/**
	 * Returns an initiliazed instance of activityFrmSndCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getActivityFrmSndCmd() {
		if (activityFrmSndCmd == null) {
			activityFrmSndCmd = new Command("Send", Command.SCREEN, 0);
		}
		return activityFrmSndCmd;
	}
	
	/**
	 * Returns an initiliazed instance of activityFrmSavCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getActivityFrmSavCmd() {
		if (activityFrmSavCmd == null) {
			activityFrmSavCmd = new Command("Save", Command.SCREEN, 1);
		}
		return activityFrmSavCmd;
	}

	/**
	 * Returns an initiliazed instance of backCommand component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getBackCommand() {
		if (backCommand == null) {
			backCommand = new Command("Back", Command.BACK, 0);
		}
		return backCommand;
	}

	/**
	 * Returns an initiliazed instance of screenCommand component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getScreenCommand() {
		if (screenCommand == null) {
			screenCommand = new Command("Save", Command.SCREEN, 0);
		}
		return screenCommand;
	}

	/**
	 * Returns an initiliazed instance of loginForm component.
	 * 
	 * @return the initialized component instance
	 */
	public Form getLoginForm() {
		if (loginForm == null) {
			loginForm = new Form("Please login", new Item[] { getUserName(),
					getPassword(), getServerUrl() });
			loginForm.addCommand(getLgnFrmExtCmd());
			loginForm.addCommand(getLgnFrmLgnCmd());
			loginForm.setCommandListener(this);
		}
		return loginForm;
	}
	
	public Form getPinForm() {
		if (pinForm == null) {
			pinForm = new Form("Enter a 4 digit PIN");
			pinForm.append(this.getPinTextField());
			pinForm.addCommand(this.getPinFormNextCmd());
			pinForm.addCommand(this.getPinFormReinitCmd());
			pinForm.addCommand(this.getExitCommand());
			pinForm.setCommandListener(this);
		} else if (pinForm != null) {
			getPinTextField().setString("");
		}
		return pinForm;
	}
	
	private TextField getPinTextField() {
		if (pinTextField == null) {
			pinTextField = new TextField("PIN", "", 4, TextField.NUMERIC
					| TextField.PASSWORD);

		}
		return pinTextField;
	}

	private Command getPinFormNextCmd() {
		if (pinFormNextCmd == null) {
			pinFormNextCmd = new Command("Next", Command.SCREEN, 0);
		}
		return pinFormNextCmd;
	}

	private Command getPinFormReinitCmd() {
		if (pinFormReinitCmd == null) {
			pinFormReinitCmd = new Command("ReInit", Command.SCREEN, 1);
		}
		return pinFormReinitCmd;
	}

	/**
	 * Returns an initiliazed instance of userName component.
	 * 
	 * @return the initialized component instance
	 */
	public TextField getUserName() {
		if (userName == null) {
			userName = new TextField("Username", "", 32, TextField.ANY
					| TextField.SENSITIVE);
		}
		return userName;
	}

	/**
	 * Returns an initiliazed instance of password component.
	 * 
	 * @return the initialized component instance
	 */
	public TextField getPassword() {
		if (password == null) {
			password = new TextField("Password", null, 32, TextField.ANY
					| TextField.PASSWORD);
		}
		return password;
	}
	
	public TextField getServerUrl() {
		if (serverURL == null) {
			serverURL = new TextField("Server Location",
					"http://localhost:8080/api/", 64, TextField.URL);
		}
		return serverURL;
	}

	/**
	 * Returns an initiliazed instance of lgnFrmExtCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getLgnFrmExtCmd() {
		if (lgnFrmExtCmd == null) {
			lgnFrmExtCmd = new Command("Exit", Command.EXIT, 0);
		}
		return lgnFrmExtCmd;
	}

	/**
	 * Returns an initiliazed instance of okCommand component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getOkCommand() {
		if (okCommand == null) {
			okCommand = new Command("Ok", Command.OK, 0);
		}
		return okCommand;
	}

	/**
	 * Returns an initiliazed instance of lgnFrmLgnCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getLgnFrmLgnCmd() {
		if (lgnFrmLgnCmd == null) {
			lgnFrmLgnCmd = new Command("Login", Command.SCREEN, 0);
		}
		return lgnFrmLgnCmd;
	}

	/**
	 * Returns an initiliazed instance of logo component.
	 * 
	 * @return the initialized component instance
	 */
	public Image getLogo() {
		if (logo == null) {
			try {
				logo = Image
						.createImage("/org/hisp/dhis/mobile/reporting/image/dhis2_logo.PNG");
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
		}
		return logo;
	}
	
	/**
	 * Returns an initiliazed instance of activityDetailForm component.
	 * 
	 * @return the initialized component instance
	 */
	public Form getActivityDetailForm() 
	{		
		if (activityDetailForm == null) {
			activityDetailForm = new Form("Details");
			activityDetailForm.addCommand(getActivityDetailOkCmd());
			activityDetailForm.setCommandListener(this);
		}
		return activityDetailForm;	
	}
	
	/**
	 * Returns an initiliazed instance of activityPlanList component.
	 * 
	 * @return the initialized component instance
	 */
	public List getActivityPlanList() {
		if (activityPlanList == null) {
			activityPlanList = new List("Select activity",Choice.IMPLICIT);
			activityPlanList.addCommand(getActvPlnLstBakCmd());			
			activityPlanList.setCommandListener(this);
		}
		return activityPlanList;
	}

	/**
	 * Returns an initiliazed instance of dataSetDisplayList component.
	 * 
	 * @return the initialized component instance
	 */
	public List getDataSetDisplayList() {
		if (dataSetDisplayList == null) {
			dataSetDisplayList = new List("Select dataSet for dataentry",
					Choice.IMPLICIT);
			dataSetDisplayList.addCommand(getDsLstBakCmd());
			dataSetDisplayList.setCommandListener(this);
		}
		return dataSetDisplayList;
	}

	/**
	 * Performs an action assigned to the selected list element in the
	 * dataSetDisplayList component.
	 */
	public void dataSetDisplayListAction() {

		AbstractModel model = (AbstractModel) dataSetsVector
				.elementAt(((List) getDataSetDisplayList()).getSelectedIndex());
		selectedDataSet = fetchDataSet(model.getId());

		switchDisplayable(null, getPeriodForm());
		getPeriodForm().removeCommand(getSelectDailyPeriodCmd());
		getPeriodForm().deleteAll();

		if (selectedDataSet.getPeriodType().equals("Daily")) {
			getDailyPeriodDateField();
			getPeriodForm().append(getDailyPeriodDateField());
			getPeriodForm().addCommand(getSelectDailyPeriodCmd());

		} else {
			getPeriodForm().append(getPeriodChoice());
			Vector periods = new Vector();

			if (selectedDataSet.getPeriodType().equals("Monthly"))
				periods = Period.generateMonthlyPeriods();
			else if (selectedDataSet.getPeriodType().equals("Yearly"))
				periods = Period.generateYearlyPeriods();
			else if (selectedDataSet.getPeriodType().equals("Weekly"))
				periods = Period.generateWeeklyPeriods();

			getPeriodChoice().deleteAll();
			for (int i = 0; i < periods.size(); i++) {
				String period = (String) periods.elementAt(i);
				getPeriodChoice().append(period, null);
			}
		}

	}

	/**
	 * Returns an initiliazed instance of dsLstBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getDsLstBakCmd() {
		if (dsLstBakCmd == null) {
			dsLstBakCmd = new Command("Back", Command.BACK, 0);
		}
		return dsLstBakCmd;
	}
	
	/**
	 * Returns an initiliazed instance of servicesList component.
	 * 
	 * @return the initialized component instance
	 */
	public List getServicesList() {
		if (servicesList == null) {
			servicesList = new List("Services", Choice.IMPLICIT);
			servicesList.append("DataSet Reporting", null);
			servicesList.append("Name-based Data Entry", null);
			servicesList.addCommand(getSrvcsBakCmd());
			servicesList.setCommandListener(this);
			servicesList.setSelectedFlags(new boolean[] { false, false });
		}
		return servicesList;
	}	
	
	/**
	 * Performs an action assigned to the selected list element in the
	 * servicesList component.
	 */
	public void servicesListAction() {
		String __selectedString = getServicesList().getString(getServicesList().getSelectedIndex());
		if (__selectedString != null) 
		{
			if (__selectedString.equals("DataSet Reporting")) 
			{				
				
				loadDataSets(getDataSetDisplayList());
				switchDisplayable(null, getDataSetDisplayList());

			} 
			else if (__selectedString.equals("Name-based Data Entry")) 
			{					
				
				getWaitForm().deleteAll();				
				getWaitForm().setTitle("Loading activities");
				getWaitForm().append("Please wait........");				
				switchDisplayable(null, getWaitForm());			
				
				ActivityRecordStore activityRecordStore = new ActivityRecordStore(this);
				Thread thread = new Thread( activityRecordStore );
				thread.start();						
			}
		}
	}
	
	/**
	 * Performs an action assigned to the selected list element in the
	 * servicesList component.
	 */
	public void activityListAction() {
		
		selectedActivity = (Activity) activitiesVector.elementAt(((List) getActivityPlanList()).getSelectedIndex());
		
		ModelRecordStore modelRecordStore = null;
		ProgramStage prStage = null;

		try {
			modelRecordStore = new ModelRecordStore(ModelRecordStore.PROGRAM_STAGE_DB);
			byte rec[] = modelRecordStore.getRecord(selectedActivity.getTask().getProgStageId());
			if (rec != null){				
				prStage = new ProgramStage();
				prStage.deSerialize(rec);
			}		
			
		} catch (RecordStoreException rse) {
			
		}catch (IOException e){}	
		
		displayActivity( selectedActivity, prStage, getActivityEntryForm());		
	}
	
	public void displayActivity(Activity activity, ProgramStage programStage, Form form) 
	{
		
		if( activity == null || programStage == null )
		{
		        form.deleteAll();
			form.append("The requested form is not available");
		}
		else
		{
			selectedActivity = activity;
			selectedProgramStage = programStage;
			
			form.deleteAll();
			
			form.setTitle(programStage.getName());
			Vector des = programStage.getDataElements();

			for (int i = 0; i < des.size(); i++) 
			{
				DataElement de = (DataElement) des.elementAt(i);
				if (de.getType().equals("date")) {
					DateField dateField = new DateField(de.getName(),
							DateField.DATE);
					form.append(dateField);
					dataElements.put(de, dateField);
				} else if (de.getType().equals("int")) {
					TextField intField = new TextField(de.getName(), "", 32,
							TextField.NUMERIC);
					form.append(intField);
					dataElements.put(de, intField);
				} else {
					TextField txtField = new TextField(de.getName(), "", 32,
							TextField.ANY);
					form.append(txtField);
					dataElements.put(de, txtField);
				}
			}
		}
		switchDisplayable(null, form);
	}
	
	/**
	 * Performs an action assigned to the selected list element in the
	 * activityPlanList component.
	 */
	public void activityDetailsAction() 
	{		
		getActivityDetailForm().deleteAll();
		
		selectedActivity = (Activity) activitiesVector.elementAt(((List) getActivityPlanList()).getSelectedIndex());
		
		ModelRecordStore modelRecordStore = null;
		ProgramStage prStage = null;

		try {
			modelRecordStore = new ModelRecordStore(ModelRecordStore.PROGRAM_STAGE_DB);
			byte rec[] = modelRecordStore.getRecord(selectedActivity.getTask().getProgStageId());
			if (rec != null){				
				prStage = new ProgramStage();
				prStage.deSerialize(rec);
				getActivityDetailForm().setTitle("Details");
	                        System.out.println(prStage);
	                        getActivityDetailForm().append( "Service: " + prStage.getName() 
	                                        + "\nName:  " + selectedActivity.getBeneficiary().getFullName() 
	                                        + "\nDue date: " + Period.formatDailyPeriod(selectedActivity.getDueDate()));
			}			
			else{
			    getActivityDetailForm().deleteAll();
			    getActivityDetailForm().append( "There is no details" );
			    
			}
			
		} catch (RecordStoreException rse) {
			
		}catch (IOException e){}
		
		
		switchDisplayable(null, getActivityDetailForm());		
	}
	
	/**
	 * Returns an initiliazed instance of maintenanceLst component.
	 * 
	 * @return the initialized component instance
	 */
	public List getMaintenanceList() {
		if (maintenanceList == null) {
			maintenanceList = new List("Maintenance", Choice.IMPLICIT);
			maintenanceList.append("Download DataSet", null);
			maintenanceList.append("Delete DataSet", null);
			maintenanceList.append("Download Program", null);
			maintenanceList.append("Delete Program", null);
			maintenanceList.append("Download Activity Plan", null);
			maintenanceList.append("Delete Activity Plan", null);
			maintenanceList.addCommand(getMntnceBakCmd());
			maintenanceList.setCommandListener(this);
			//maintenanceList.setSelectedFlags(new boolean[] { false, false });
		}
		return maintenanceList;
	}	

	/**
	 * Performs an action assigned to the selected list element in the
	 * maintenanceList component.
	 */
	public void maintenanceListAction() {
		String __selectedString = getMaintenanceList().getString(
				getMaintenanceList().getSelectedIndex());
		if (__selectedString != null) {
			if (__selectedString.equals("Download DataSet")) 
			{				
				getWaitForm().deleteAll();
				getWaitForm().setTitle("Making connection");
				getWaitForm().append("Please wait........");
				switchDisplayable(null, getWaitForm());				
				browseDataSets();
			} 
			else if (__selectedString.equals("Delete DataSet")) 
			{
				loadDataSets(getDeleteList());
				switchDisplayable(null, getDeleteList());
			}
			else if(__selectedString.equals("Download Program"))
			{
				getWaitForm().deleteAll();
				getWaitForm().setTitle("Making connection");
				getWaitForm().append("Please wait........");
				switchDisplayable(null, getWaitForm());				
				browsePrograms();
			}
			else if (__selectedString.equals("Delete Program")) 
			{
				populatePrograms(getDeleteProgList());
				switchDisplayable(null, getDeleteProgList());
			}
			else if(__selectedString.equals("Download Activity Plan"))
			{
				getWaitForm().deleteAll();
				getWaitForm().setTitle("Downloading activityplan");
				getWaitForm().append("Please wait........");
				switchDisplayable(null, getWaitForm());
				
				downloadActivityPlan();
			}
			else if (__selectedString.equals("Delete Activity Plan")) 
			{
    			        getWaitForm().deleteAll();
                                getWaitForm().setTitle("Deleting Activity Plan");
                                getWaitForm().append("Please wait........");
                                switchDisplayable(null, getWaitForm());
                                
                                deleteActivityPlan();
			}			
		}
	}

	/**
	 * Returns an initiliazed instance of mntnceBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getMntnceBakCmd() {
		if (mntnceBakCmd == null) {
			mntnceBakCmd = new Command("Back", Command.BACK, 0);
		}
		return mntnceBakCmd;
	}
	
	/**
	 * Returns an initiliazed instance of srvcsBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getSrvcsBakCmd() {
		if (srvcsBakCmd == null) {
			srvcsBakCmd = new Command("Back", Command.BACK, 0);
		}
		return srvcsBakCmd;
	}
	

	/**
	 * Returns an initiliazed instance of dhisUserName component.
	 * 
	 * @return the initialized component instance
	 */
	public TextField getDhisUserName() {
		if (dhisUserName == null) {
			dhisUserName = new TextField("DHIS UserName", null, 32,
					TextField.ANY);
		}
		return dhisUserName;
	}

	/**
	 * Returns an initiliazed instance of locale component.
	 * 
	 * @return the initialized component instance
	 */
	public TextField getLocale() {
		if (locale == null) {
			locale = new TextField("Language Locale", null, 32, TextField.ANY);
		}
		return locale;
	}

	/**
	 * Returns an initiliazed instance of periodForm component.
	 * 
	 * @return the initialized component instance
	 */
	public Form getPeriodForm() {
		if (periodForm == null) {
			periodForm = new Form("Select Period", new Item[] { getPeriodChoice() });
			periodForm.addCommand(getPeriodNxtCmd());
			periodForm.addCommand(getPeriodBakCmd());
			periodForm.setCommandListener(this);
		}
		return periodForm;
	}

	/**
	 * Returns an initiliazed instance of periodBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getPeriodBakCmd() {
		if (periodBakCmd == null) {
			periodBakCmd = new Command("Back", Command.BACK, 0);
		}
		return periodBakCmd;
	}

	/**
	 * Returns an initiliazed instance of periodNxtCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getPeriodNxtCmd() {
		if (periodNxtCmd == null) {
			periodNxtCmd = new Command("Next", Command.SCREEN, 0);
		}
		return periodNxtCmd;
	}

	/**
	 * Returns an initiliazed instance of periodChoice component.
	 * 
	 * @return the initialized component instance
	 */
	public ChoiceGroup getPeriodChoice() {
		if (periodChoice == null) {
			periodChoice = new ChoiceGroup("Period", Choice.POPUP);
			periodChoice.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
		}
		return periodChoice;
	}

	/**
	 * Returns an initiliazed instance of waitForm component.
	 * 
	 * @return the initialized component instance
	 */
	public Form getWaitForm() {
		if (waitForm == null) {
			waitForm = new Form("Please wait....");
		}
		return waitForm;
	}

	/**
	 * Returns an initiliazed instance of successAlert component.
	 * 
	 * @return the initialized component instance
	 */
	public Alert getSuccessAlert() {
		if (successAlert == null) {
			successAlert = new Alert("alert", null, null,
					AlertType.CONFIRMATION);
			successAlert.setTimeout(Alert.FOREVER);
		}
		return successAlert;
	}

	/**
	 * Returns an initiliazed instance of errorAlert component.
	 * 
	 * @return the initialized component instance
	 */
	public Alert getErrorAlert() {
		if (errorAlert == null) {
			errorAlert = new Alert("alert", null, null, AlertType.ERROR);
			errorAlert.setTimeout(Alert.FOREVER);
		}
		return errorAlert;
	}
	
	/**
         * Returns an initiliazed instance of deleteProgList component.
         * 
         * @return the initialized component instance
         */
        public List getDeleteProgList() {
                if (deleteProgList == null) {
                    deleteProgList = new List("Please select", Choice.IMPLICIT);
                    deleteProgList.addCommand(getDeleteBakCmd());                       
                    deleteProgList.setCommandListener(this);
                }               
                return deleteProgList;
        }
	
	
	/**
	 * Returns an initiliazed instance of deleteList component.
	 * 
	 * @return the initialized component instance
	 */
	public List getDeleteList() {
		if (deleteList == null) {
			deleteList = new List("Please select", Choice.IMPLICIT);
			deleteList.addCommand(getDeleteBakCmd());			
			deleteList.setCommandListener(this);
		}		
		if( deleteList.size() > 0 )
		{
			deleteList.addCommand(getDsDeleteCmd());
		}
		else
		{
			deleteList.removeCommand(getDsDeleteCmd());
		}
		return deleteList;
	}

	/**
	 * Performs an action assigned to the selected list element in the
	 * deleteList component.
	 */
	public void deleteListAction() {
	    
	}

	/**
	 * Returns an initiliazed instance of deleteBakCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getDeleteBakCmd() {
		if (deleteBakCmd == null) {
			deleteBakCmd = new Command("Back", Command.BACK, 0);
		}
		return deleteBakCmd;
	}

	/**
	 * Returns an initiliazed instance of dsDeleteCmd component.
	 * 
	 * @return the initialized component instance
	 */
	public Command getDsDeleteCmd() {
		if (dsDeleteCmd == null) {
			dsDeleteCmd = new Command("Delete", Command.SCREEN, 0);
		}
		return dsDeleteCmd;
	}

	/**
	 * Returns a display instance.
	 * 
	 * @return the display instance.
	 */
	public Display getDisplay() {
		return Display.getDisplay(this);
	}

	/**
	 * Exits MIDlet.
	 */
	public void exitMIDlet() {
		switchDisplayable(null, null);
		destroyApp(true);
		notifyDestroyed();
	}

	/**
	 * Called when MIDlet is started. Checks whether the MIDlet have been
	 * already started and initialize/starts or resumes the MIDlet.
	 */
	public void startApp() {
		if (midletPaused) {
			resumeMIDlet();
		} else {
			initialize();
			startMIDlet();
		}
		midletPaused = false;
	}

	/**
	 * Called when MIDlet is paused.
	 */
	public void pauseApp() {
		midletPaused = true;
	}

	/**
	 * Called to signal the MIDlet to terminate.
	 * 
	 * @param unconditional
	 *            if true, then the MIDlet has to be unconditionally terminated
	 *            and all resources has to be released.
	 */
	public void destroyApp(boolean unconditional) {
	}

	private void login() {
		if (getUserName().getString() != null
				&& getPassword().getString() != null) {
			if (getUserName().getString().trim().length() != 0
					&& getPassword().getString().trim().length() != 0) {
				ConnectionManager connectionManager = new ConnectionManager(
						this, getServerUrl().getString(), getUserName()
								.getString(), getPassword().getString(),
						getLocale().getString(), ConnectionManager.AUTHENTICATE);
				connectionManager.start();
			}
		}
	}

	private void saveSettings() {

		SettingsRecordStore settingsRecord;

		try {

			settingsRecord = new SettingsRecordStore(SettingsRecordStore.SETTINGS_DB);
			settingsRecord.put("url", getUrl().getString());
			settingsRecord.put("username", getDhisUserName().getString());
			settingsRecord.put("password", getDhisUserPass().getString());
			settingsRecord.put("locale", getLocale().getString());
			settingsRecord.save();

		} catch (RecordStoreException rse) {
		}
	}
	
	public void loadActivityPlan(Vector activities) 
	{		
		activitiesVector.removeAllElements();	
		activitiesVector = activities;	
		
		getActivityPlanList().deleteAll();
		
		if( activitiesVector.size() > 0 )
			getActivityPlanList().addCommand(getActivityDetailCmd());				
		else
			getActivityPlanList().removeCommand(getActivityDetailCmd());		

		for (int i = 0; i < activities.size(); i++) {
			Activity activity = (Activity) activities.elementAt(i);
			getActivityPlanList().insert(i, activity.getBeneficiary().getFullName(), null);			
		}	
		
		switchDisplayable(null, getActivityPlanList());
	}
	
	private void populatePrograms(List list) 
	{
	    programsVector.removeAllElements();
	    ModelRecordStore modelRecordStore = null;
	    Vector programs = null;
            try {
                modelRecordStore = new ModelRecordStore(ModelRecordStore.PROGRAM_DB);
                programs = modelRecordStore.getAllRecord();
            } catch (RecordStoreException rse) {
                rse.printStackTrace();
            }
            
            list.deleteAll();
            if(programs!=null){
                for (int i = 0; i < programs.size(); i++) {
                    AbstractModel prog = (AbstractModel) programs.elementAt(i);
                    list.insert(i, prog.getName(), null);
                    programsVector.addElement(prog);
                }
            }
            
            switchDisplayable( null, getDeleteProgList() );
	}

	private void loadDataSets(List list) 
	{
		dataSetsVector.removeAllElements();
		ModelRecordStore modelRecordStore = null;
		Vector dataSets = null;
		try {
			modelRecordStore = new ModelRecordStore(ModelRecordStore.DATASET_DB);
			dataSets = modelRecordStore.getAllRecord();
		} catch (RecordStoreException rse) {
		}

		list.deleteAll();

		for (int i = 0; i < dataSets.size(); i++) {
			AbstractModel ds = (AbstractModel) dataSets.elementAt(i);
			list.insert(i, ds.getName(), null);
			dataSetsVector.addElement(ds);
		}		
	}

	private void loadSettings() {
		SettingsRecordStore settingsRecord;

		try {
			settingsRecord = new SettingsRecordStore(
					SettingsRecordStore.SETTINGS_DB);
			
			String localeSetting = "";
			localeSetting = settingsRecord.get("locale");
			
			if (localeSetting.trim().length() == 0) {
				localeSetting = System.getProperty("microedition.locale");
				if (localeSetting == null) {
					localeSetting = "en-US";
				}
			}

			getUrl().setString(settingsRecord.get("url"));
			getDhisUserName().setString(settingsRecord.get("username"));
			getDhisUserPass().setString(settingsRecord.get("password"));
			getLocale().setString(localeSetting);

		} catch (RecordStoreException rse) {
		}
	}

	private void browseDataSets() {
		loadSettings();
		ConnectionManager connectionManager = new ConnectionManager(this, getUrl().getString(),
				getDhisUserName().getString(), getDhisUserPass().getString(),
				getLocale().getString(), ConnectionManager.BROWSE_DATASETS);
		connectionManager.start();
	}
	
	private void browsePrograms() {
		loadSettings();
		ConnectionManager connectionManager = new ConnectionManager(this, getUrl().getString(),
				getDhisUserName().getString(), getDhisUserPass().getString(),
				getLocale().getString(), ConnectionManager.BROWSE_PROGRAMS);
		connectionManager.start();
	}
	
	private void deleteActivityPlan()
	{
	    new Thread(new Runnable()
            {
                
	        public void run() {
	            ActivityRecordStore activityRs = new ActivityRecordStore();
	            activityRs.clear();
	            switchDisplayable( null, getMaintenanceList() );
                }
            }
	        
	    ).start();    
	    
	        
	}
	
	private void downloadActivityPlan() 
	{		
		loadSettings();
		ConnectionManager connectionManager = new ConnectionManager(this, getUrl().getString(),
				getDhisUserName().getString(), getDhisUserPass().getString(),
				getLocale().getString(), ConnectionManager.DOWNLOAD_ACTIVITYPLAN);
		connectionManager.start();
	}
	
	public void displayProgramsForDownload(Vector programs) {
		programsVector = programs;
		if (programs == null) {
			getDsDnldList().append("No Programs available", null);
		} else {
			getPrDnldList().deleteAll();
			for (int i = 0; i < programs.size(); i++) {
				AbstractModel pr = (AbstractModel) programs.elementAt(i);
				getPrDnldList().insert(i, pr.getName(), null);
			}
		}

		switchDisplayable(null, getPrDnldList());
	}
	
	private void downloadProgram(int programId) {
		loadSettings();
		ConnectionManager connectionManager = new ConnectionManager(this, getUrl().getString(),
				getDhisUserName().getString(), getDhisUserPass().getString(),
				getLocale().getString(), ConnectionManager.DOWNLOAD_PROGRAM,
				programId);
		connectionManager.start();
	}
	

	public void displayDataSetsForDownload(Vector dataSets) {
		dataSetsVector = dataSets;

		if (dataSets == null) {
		        getDsDnldList().deleteAll();
			getDsDnldList().append("No Datasets available", null);
		} else {
			getDsDnldList().deleteAll();
			for (int i = 0; i < dataSets.size(); i++) {
				AbstractModel ds = (AbstractModel) dataSets.elementAt(i);
				getDsDnldList().insert(i, ds.getName(), null);
			}
		}

		switchDisplayable(null, getDsDnldList());
	}

	private void downloadDataSet(int dataSetId) {
		loadSettings();
		ConnectionManager connectionManager = new ConnectionManager(this, getUrl().getString(),
				getDhisUserName().getString(), getDhisUserPass().getString(),
				getLocale().getString(), ConnectionManager.DOWNLOAD_DATASET,
				dataSetId);
		connectionManager.start();
	}

	public DataSet fetchDataSet(int dataSetId) {
		ModelRecordStore modelRecordStore = null;
		DataSet ds = null;

		try {
			modelRecordStore = new ModelRecordStore(ModelRecordStore.DATASET_DB);
			byte rec[] = modelRecordStore.getRecord(dataSetId);
			if (rec != null)
				ds = new DataSet();
			try {
				ds.deSerialize(rec);				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (RecordStoreException rse) {}

		return ds;
	}

	public void saveDataSet(DataSet dataSet) {	
		
		if (dataSet != null) {
			ModelRecordStore modelRecordStore;

			try {

				modelRecordStore = new ModelRecordStore(ModelRecordStore.DATASET_DB);
				try {
					modelRecordStore.AddRecord(dataSet.serialize());
				} catch (IOException ex) {
					// ex.printStackTrace();
					getErrorAlert().setTitle("Download Status");
					getErrorAlert().setString("FAILURE");
					switchDisplayable(getErrorAlert(), getDsDnldList());
				}
				
				getSuccessAlert().setTitle("Download Status");
				getSuccessAlert().setString("SUCCESS");
				switchDisplayable(getSuccessAlert(), getDsDnldList());
				
			} catch (RecordStoreException rse) {}
		} 
		else 
		{			
			getErrorAlert().setTitle("Download Status");
			getErrorAlert().setString("FAILURE");
			switchDisplayable(getErrorAlert(), getDsDnldList());
		}

	}
	
	public void saveProgram(Program program) {
		
		if (program != null) 
		{
			ModelRecordStore programRecordStore;
			ModelRecordStore programStageRecordStore;

			try {
				programRecordStore = new ModelRecordStore(ModelRecordStore.PROGRAM_DB);
				programStageRecordStore = new ModelRecordStore(ModelRecordStore.PROGRAM_STAGE_DB);
				
				try {					
					AbstractModel model = new AbstractModel();					
					model.setId( program.getId() );
					model.setName( program.getName() );
					
					programRecordStore.AddRecord(model.serialize());
					
					Vector prStgs = program.getProgramStages();
					for(int i=0; i<prStgs.size(); i++)
					{
						ProgramStage prStg = (ProgramStage) prStgs.elementAt(i);
						prStg.setProgramId( model.getId() );
						programStageRecordStore.AddRecord( prStg.serialize() );	
					}					
				} catch (IOException ex) {
					// ex.printStackTrace();
					getErrorAlert().setTitle("Download Status");
					getErrorAlert().setString("FAILURE");
					switchDisplayable(getErrorAlert(), getDsDnldList());
				}
				getSuccessAlert().setTitle("Download Status");
				getSuccessAlert().setString("SUCCESS");
				switchDisplayable(getSuccessAlert(), getPrDnldList());

			} catch (RecordStoreException rse) {}
		} 
		else 
		{			
			getErrorAlert().setTitle("Download Status");
			getErrorAlert().setString("FAILURE");
			switchDisplayable(getErrorAlert(), getPrDnldList());
		}	
	}
	
	public void saveActivityPlan(ActivityPlan activityPlan) 
	{		
		if (activityPlan != null) {
			
			ActivityRecordStore activityRecordStore = new ActivityRecordStore();				
			activityRecordStore.setActivityVector(activityPlan.getActivities());			
			
			activityRecordStore.save();
			
			getSuccessAlert().setTitle("Download Status");
			getSuccessAlert().setString("SUCCESS");
			switchDisplayable(getSuccessAlert(), getMaintenanceList());			
			
		} 
		else 
		{			
			getErrorAlert().setTitle("Download Status");
			getErrorAlert().setString("FAILURE");
			switchDisplayable(getErrorAlert(), getMaintenanceList());
		}	
	}
	
	public void displayDataEntry(DataSet ds, Form form) {
		
		selectedDataSet = ds;

		if (ds == null) {
			form.append("The requested dataset is not available");
		} else {
			form.deleteAll();

			form.setTitle(ds.getName());
			Vector des = ds.getDataElements();

			for (int i = 0; i < des.size(); i++) {
				DataElement de = (DataElement) des.elementAt(i);
				if (de.getType().equals("date")) {
					DateField dateField = new DateField(de.getName(),
							DateField.DATE);
					form.append(dateField);
					dataElements.put(de, dateField);
				} else if (de.getType().equals("int")) {
					TextField intField = new TextField(de.getName(), "", 32,
							TextField.NUMERIC);
					form.append(intField);
					dataElements.put(de, intField);
				} else {
					TextField txtField = new TextField(de.getName(), "", 32,
							TextField.ANY);
					form.append(txtField);
					dataElements.put(de, txtField);
				}
			}
		}

		switchDisplayable(null, form);
	}

	private DataSetValue generateDataSetValue() {
		Vector des = selectedDataSet.getDataElements();

		DataSetValue dsValue = new DataSetValue();
		dsValue.setId(selectedDataSet.getId());
		dsValue.setName(selectedDataSet.getName());
		dsValue.setpName(selectedPeriod);

		DataElement de;
		String val;
		for (int i = 0; i < des.size(); i++) {
			de = (DataElement) des.elementAt(i);

			if (de.getType().equals("date")) {
				DateField dateField = (DateField) dataElements.get(de);
				val = dateField.getDate().toString();
			} else if (de.getType().equals("int")) {
				TextField intField = (TextField) dataElements.get(de);
				val = intField.getString();

			} else {
				TextField txtField = (TextField) dataElements.get(de);
				val = txtField.getString();
			}

			DataValue dv = new DataValue();
			dv.setId(de.getId());
			dv.setVal(val);

			dsValue.getDataValues().addElement(dv);

		}

		return dsValue;
	}
	
	public void sendData() {
		DataSetValue dsValue = generateDataSetValue();

		loadSettings();
		ConnectionManager connectionManager = new ConnectionManager(this, getUrl().getString(),
				getDhisUserName().getString(), getDhisUserPass().getString(),
				getLocale().getString(),
				ConnectionManager.UPLOAD_DATASET_VALUES, dsValue);
		connectionManager.start();
	}
	
	public void saveData() 
	{
		DataSetValue dsValue = generateDataSetValue();		
		System.out.println("Dataset to save:  " + dsValue.getId() + " " + dsValue.getName() + " " + dsValue.getpName() );		
	}
	
	public void sendActivity() 
	{		
		ActivityValue activityValue = generateActivityValue();

		loadSettings();
		ConnectionManager connectionManager = new ConnectionManager(this, getUrl().getString(),
				getDhisUserName().getString(), getDhisUserPass().getString(),
				getLocale().getString(),
				ConnectionManager.UPLOAD_ACTIVITY_VALUES, activityValue);
		connectionManager.start();
		
	}
	
	public void saveActivity() 
	{
		System.out.println("I will try to save the filled activity from here...");		
	}
	
	private ActivityValue generateActivityValue() {
		Vector des = selectedProgramStage.getDataElements();

		ActivityValue activityValue = new ActivityValue();
		activityValue.setProgramInstanceId(selectedActivity.getTask().getProgStageInstId());		

		DataElement de;
		String val;
		for (int i = 0; i < des.size(); i++) {
			de = (DataElement) des.elementAt(i);

			if (de.getType().equals("date")) {
				DateField dateField = (DateField) dataElements.get(de);
				val = Period.formatDailyPeriod( dateField.getDate());
			} else if (de.getType().equals("int")) {
				TextField intField = (TextField) dataElements.get(de);
				val = intField.getString();

			} else {
				TextField txtField = (TextField) dataElements.get(de);
				val = txtField.getString();
			}

			DataValue dv = new DataValue();
			dv.setId(de.getId());
			dv.setVal(val);

			activityValue.getDataValues().addElement(dv);
		}

		return activityValue;
	}

	private void deleteDataSet(AbstractModel model) {
		ModelRecordStore modelRecordStore = null;
		try {
			modelRecordStore = new ModelRecordStore(ModelRecordStore.DATASET_DB);
			modelRecordStore.deleteRecord(model);
		} catch (RecordStoreException rse) {
		}
	}
	
	private void deleteProgramsAndProgramStages(AbstractModel model){
	       ModelRecordStore progRecord = null;
	       ProgramStageRecordStore progStageRecord = null;
               try {
                       progRecord = new ModelRecordStore(ModelRecordStore.PROGRAM_DB);
                       progStageRecord = new ProgramStageRecordStore();
                       //Delete ProgramStages have programId = model.getId();
                       progStageRecord.deleteProgStageOfProgId( model.getId() );
                       //Delete Program has id = model.getId();
                       progRecord.deleteRecord(model);
                       //should refresh deleteProgList here and show it again
                       switchDisplayable( null, getMaintenanceList() );
               } catch (RecordStoreException rse) {
               }    
	}

	public DateField getDailyPeriodDateField() {
		if (dailyPeriodDateField == null) {
			dailyPeriodDateField = new DateField("Select Date:", DateField.DATE);
			dailyPeriodDateField.setDate(new java.util.Date(System
					.currentTimeMillis()));
		}
		return dailyPeriodDateField;
	}

	public Command getSelectDailyPeriodCmd() {
		if (selectDailyPeriodCmd == null) {
			selectDailyPeriodCmd = new Command("Select Date", Command.OK, 1);
		}
		return selectDailyPeriodCmd;
	}
	
	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}
}
