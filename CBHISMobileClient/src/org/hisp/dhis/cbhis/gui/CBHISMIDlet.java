/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.cbhis.gui;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;
import org.hisp.dhis.cbhis.connection.DownloadManager;
import org.hisp.dhis.cbhis.db.ModelRecordStore;
import org.hisp.dhis.cbhis.db.SettingsRectordStore;
import org.hisp.dhis.cbhis.model.AbstractModel;
import org.hisp.dhis.cbhis.model.DataElement;
import org.hisp.dhis.cbhis.model.ProgramStageForm;

/**
 * @author abyotag_adm
 */
public class CBHISMIDlet extends MIDlet implements CommandListener {

    private boolean midletPaused = false;
    private boolean login = false;
    Vector programStagesVector = new Vector();
    private DownloadManager downloadManager;
    private Hashtable formElements = new Hashtable();
    private ProgramStageForm programStageForm;

    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private List mainMenuList;
    private List frmDnldList;
    private Form settingsForm;
    private TextField url;
    private TextField adminPass;
    private Form dataEntryForm;
    private Form form;
    private Form loginForm;
    private TextField userName;
    private TextField password;
    private Command exitCommand;
    private Command mnuListDnldCmd;
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
    private Command backCommand;
    private Command okCommand;
    private Command lgnFrmExtCmd;
    private Command lgnFrmLgnCmd;
    private Image logo;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The CBHISMIDlet constructor.
     */
    public CBHISMIDlet() {
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    //</editor-fold>//GEN-END:|methods|0|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
        // write pre-initialize user code here
//GEN-LINE:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
//GEN-LINE:|3-startMIDlet|1|3-postAction
        new SplashScreen(getLogo(),getDisplay(), (Displayable)getLoginForm());
        //Here need to call my splahshscreen
        // write post-action user code here
    }//GEN-BEGIN:|3-startMIDlet|2|
    //</editor-fold>//GEN-END:|3-startMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
    //</editor-fold>//GEN-END:|4-resumeMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
    //</editor-fold>//GEN-END:|5-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == dataEntryForm) {//GEN-BEGIN:|7-commandAction|1|94-preAction
            if (command == deFrmBakCmd) {//GEN-END:|7-commandAction|1|94-preAction
                // write pre-action user code here
                switchDisplayable(null, getMainMenuList());//GEN-LINE:|7-commandAction|2|94-postAction
                // write post-action user code here
            } else if (command == deFrmSavCmd) {//GEN-LINE:|7-commandAction|3|96-preAction
                // write pre-action user code here

                // need to send the recorded data
                sendRecordedData();

//GEN-LINE:|7-commandAction|4|96-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|5|100-preAction
        } else if (displayable == form) {
            if (command == backCommand) {//GEN-END:|7-commandAction|5|100-preAction
                // write pre-action user code here
                switchDisplayable(null, getMainMenuList());//GEN-LINE:|7-commandAction|6|100-postAction
                // write post-action user code here
            } else if (command == screenCommand) {//GEN-LINE:|7-commandAction|7|102-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|8|102-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|9|54-preAction
        } else if (displayable == frmDnldList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|9|54-preAction
                // write pre-action user code here
                frmDnldListAction();//GEN-LINE:|7-commandAction|10|54-postAction
                // write post-action user code here
            } else if (command == frmDnldListBakCmd) {//GEN-LINE:|7-commandAction|11|61-preAction
                // write pre-action user code here
                switchDisplayable(null, getMainMenuList());//GEN-LINE:|7-commandAction|12|61-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|13|109-preAction
        } else if (displayable == loginForm) {
            if (command == lgnFrmExtCmd) {//GEN-END:|7-commandAction|13|109-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|14|109-postAction
                // write post-action user code here
            } else if (command == lgnFrmLgnCmd) {//GEN-LINE:|7-commandAction|15|113-preAction
                // write pre-action user code here

                login();

                switchDisplayable(null, getMainMenuList());//GEN-LINE:|7-commandAction|16|113-postAction
                // write post-action user code here

            }//GEN-BEGIN:|7-commandAction|17|39-preAction
        } else if (displayable == mainMenuList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|17|39-preAction
                // write pre-action user code here
                mainMenuListAction();//GEN-LINE:|7-commandAction|18|39-postAction
                // write post-action user code here
            } else if (command == mnuListExtCmd) {//GEN-LINE:|7-commandAction|19|49-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|20|49-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|21|79-preAction
        } else if (displayable == settingsForm) {
            if (command == setngsBakCmd) {//GEN-END:|7-commandAction|21|79-preAction
                // write pre-action user code here
                switchDisplayable(null, getMainMenuList());//GEN-LINE:|7-commandAction|22|79-postAction
                // write post-action user code here
            } else if (command == setngsSaveCmd) {//GEN-LINE:|7-commandAction|23|83-preAction
                // write pre-action user code here

                // should try to save global parameters.......
                saveSettings();
                switchDisplayable(null, getMainMenuList());//GEN-LINE:|7-commandAction|24|83-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|25|7-postCommandAction
        }//GEN-END:|7-commandAction|25|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|26|
    //</editor-fold>//GEN-END:|7-commandAction|26|
    

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand ">//GEN-BEGIN:|28-getter|0|28-preInit
    /**
     * Returns an initiliazed instance of exitCommand component.
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {//GEN-END:|28-getter|0|28-preInit
            // write pre-init user code here
            exitCommand = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|28-getter|1|28-postInit
            // write post-init user code here
        }//GEN-BEGIN:|28-getter|2|
        return exitCommand;
    }
    //</editor-fold>//GEN-END:|28-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: mainMenuList ">//GEN-BEGIN:|37-getter|0|37-preInit
    /**
     * Returns an initiliazed instance of mainMenuList component.
     * @return the initialized component instance
     */
    public List getMainMenuList() {
        if (mainMenuList == null) {//GEN-END:|37-getter|0|37-preInit
            // write pre-init user code here
            mainMenuList = new List("Menu", Choice.IMPLICIT);//GEN-BEGIN:|37-getter|1|37-postInit
            mainMenuList.append("Download Form", null);
            mainMenuList.append("Download Activity Plan", null);
            mainMenuList.append("Record Data", null);
            mainMenuList.append("Settings", null);
            mainMenuList.addCommand(getMnuListExtCmd());
            mainMenuList.setCommandListener(this);
            mainMenuList.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            mainMenuList.setSelectedFlags(new boolean[] { false, false, false, false });//GEN-END:|37-getter|1|37-postInit
            // write post-init user code here
        }//GEN-BEGIN:|37-getter|2|
        return mainMenuList;
    }
    //</editor-fold>//GEN-END:|37-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: mainMenuListAction ">//GEN-BEGIN:|37-action|0|37-preAction
    /**
     * Performs an action assigned to the selected list element in the mainMenuList component.
     */
    public void mainMenuListAction() {//GEN-END:|37-action|0|37-preAction
        // enter pre-action user code here
        String __selectedString = getMainMenuList().getString(getMainMenuList().getSelectedIndex());//GEN-BEGIN:|37-action|1|41-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("Download Form")) {//GEN-END:|37-action|1|41-preAction
                // write pre-action user code here

                browseForms();

                Form waitForm = new Form("Making connection");
                waitForm.append("Please wait........");
                switchDisplayable(null, waitForm);

//GEN-LINE:|37-action|2|41-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Download Activity Plan")) {//GEN-LINE:|37-action|3|42-preAction
                // write pre-action user code here
                System.out.println("I will download activity plans from here");
//GEN-LINE:|37-action|4|42-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Record Data")) {//GEN-LINE:|37-action|5|65-preAction
                // write pre-action user code here
                switchDisplayable(null, getForm());//GEN-LINE:|37-action|6|65-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Settings")) {//GEN-LINE:|37-action|7|47-preAction
                // write pre-action user code here
                loadSettings();
                switchDisplayable(null, getSettingsForm());//GEN-LINE:|37-action|8|47-postAction
                // write post-action user code here
            }//GEN-BEGIN:|37-action|9|37-postAction
        }//GEN-END:|37-action|9|37-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|37-action|10|
    //</editor-fold>//GEN-END:|37-action|10|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: mnuListExtCmd ">//GEN-BEGIN:|48-getter|0|48-preInit
    /**
     * Returns an initiliazed instance of mnuListExtCmd component.
     * @return the initialized component instance
     */
    public Command getMnuListExtCmd() {
        if (mnuListExtCmd == null) {//GEN-END:|48-getter|0|48-preInit
            // write pre-init user code here
            mnuListExtCmd = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|48-getter|1|48-postInit
            // write post-init user code here
        }//GEN-BEGIN:|48-getter|2|
        return mnuListExtCmd;
    }
    //</editor-fold>//GEN-END:|48-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: mnuListDnldCmd ">//GEN-BEGIN:|50-getter|0|50-preInit
    /**
     * Returns an initiliazed instance of mnuListDnldCmd component.
     * @return the initialized component instance
     */
    public Command getMnuListDnldCmd() {
        if (mnuListDnldCmd == null) {//GEN-END:|50-getter|0|50-preInit
            // write pre-init user code here
            mnuListDnldCmd = new Command("Download", Command.SCREEN, 0);//GEN-LINE:|50-getter|1|50-postInit
            // write post-init user code here
        }//GEN-BEGIN:|50-getter|2|
        return mnuListDnldCmd;
    }
    //</editor-fold>//GEN-END:|50-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmDnldList ">//GEN-BEGIN:|53-getter|0|53-preInit
    /**
     * Returns an initiliazed instance of frmDnldList component.
     * @return the initialized component instance
     */
    public List getFrmDnldList() {
        if (frmDnldList == null) {//GEN-END:|53-getter|0|53-preInit
            // write pre-init user code here
            frmDnldList = new List("Select form to download", Choice.IMPLICIT);//GEN-BEGIN:|53-getter|1|53-postInit
            frmDnldList.addCommand(getFrmDnldListBakCmd());
            frmDnldList.setCommandListener(this);
            frmDnldList.setSelectedFlags(new boolean[] {  });//GEN-END:|53-getter|1|53-postInit
            // write post-init user code here
        }//GEN-BEGIN:|53-getter|2|
        return frmDnldList;
    }
    //</editor-fold>//GEN-END:|53-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: frmDnldListAction ">//GEN-BEGIN:|53-action|0|53-preAction
    /**
     * Performs an action assigned to the selected list element in the frmDnldList component.
     */
    public void frmDnldListAction() {//GEN-END:|53-action|0|53-preAction        

        AbstractModel programStage = (AbstractModel)programStagesVector.elementAt( ((List)getFrmDnldList()).getSelectedIndex() );
        downloadForm( programStage.getId() );

    }//GEN-BEGIN:|53-action|2|
    //</editor-fold>//GEN-END:|53-action|2|



    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmDnldListBakCmd ">//GEN-BEGIN:|60-getter|0|60-preInit
    /**
     * Returns an initiliazed instance of frmDnldListBakCmd component.
     * @return the initialized component instance
     */
    public Command getFrmDnldListBakCmd() {
        if (frmDnldListBakCmd == null) {//GEN-END:|60-getter|0|60-preInit
            // write pre-init user code here
            frmDnldListBakCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|60-getter|1|60-postInit
            // write post-init user code here
        }//GEN-BEGIN:|60-getter|2|
        return frmDnldListBakCmd;
    }
    //</editor-fold>//GEN-END:|60-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmDnldCmd ">//GEN-BEGIN:|63-getter|0|63-preInit
    /**
     * Returns an initiliazed instance of frmDnldCmd component.
     * @return the initialized component instance
     */
    public Command getFrmDnldCmd() {
        if (frmDnldCmd == null) {//GEN-END:|63-getter|0|63-preInit
            // write pre-init user code here
            frmDnldCmd = new Command("Download", Command.SCREEN, 0);//GEN-LINE:|63-getter|1|63-postInit
            // write post-init user code here
        }//GEN-BEGIN:|63-getter|2|
        return frmDnldCmd;
    }
    //</editor-fold>//GEN-END:|63-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: actvyPlnListBakCmd ">//GEN-BEGIN:|71-getter|0|71-preInit
    /**
     * Returns an initiliazed instance of actvyPlnListBakCmd component.
     * @return the initialized component instance
     */
    public Command getActvyPlnListBakCmd() {
        if (actvyPlnListBakCmd == null) {//GEN-END:|71-getter|0|71-preInit
            // write pre-init user code here
            actvyPlnListBakCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|71-getter|1|71-postInit
            // write post-init user code here
        }//GEN-BEGIN:|71-getter|2|
        return actvyPlnListBakCmd;
    }
    //</editor-fold>//GEN-END:|71-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: settingsForm ">//GEN-BEGIN:|74-getter|0|74-preInit
    /**
     * Returns an initiliazed instance of settingsForm component.
     * @return the initialized component instance
     */
    public Form getSettingsForm() {
        if (settingsForm == null) {//GEN-END:|74-getter|0|74-preInit
            // write pre-init user code here
            settingsForm = new Form("Configurable Parameters", new Item[] { getUrl(), getAdminPass() });//GEN-BEGIN:|74-getter|1|74-postInit
            settingsForm.addCommand(getSetngsBakCmd());
            settingsForm.addCommand(getSetngsSaveCmd());
            settingsForm.setCommandListener(this);//GEN-END:|74-getter|1|74-postInit
            // write post-init user code here
        }//GEN-BEGIN:|74-getter|2|
        return settingsForm;
    }
    //</editor-fold>//GEN-END:|74-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stngsOkCmd ">//GEN-BEGIN:|76-getter|0|76-preInit
    /**
     * Returns an initiliazed instance of stngsOkCmd component.
     * @return the initialized component instance
     */
    public Command getStngsOkCmd() {
        if (stngsOkCmd == null) {//GEN-END:|76-getter|0|76-preInit
            // write pre-init user code here
            stngsOkCmd = new Command("Save", Command.OK, 0);//GEN-LINE:|76-getter|1|76-postInit
            // write post-init user code here
        }//GEN-BEGIN:|76-getter|2|
        return stngsOkCmd;
    }
    //</editor-fold>//GEN-END:|76-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: setngsBakCmd ">//GEN-BEGIN:|78-getter|0|78-preInit
    /**
     * Returns an initiliazed instance of setngsBakCmd component.
     * @return the initialized component instance
     */
    public Command getSetngsBakCmd() {
        if (setngsBakCmd == null) {//GEN-END:|78-getter|0|78-preInit
            // write pre-init user code here
            setngsBakCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|78-getter|1|78-postInit
            // write post-init user code here
        }//GEN-BEGIN:|78-getter|2|
        return setngsBakCmd;
    }
    //</editor-fold>//GEN-END:|78-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: setngsSaveCmd ">//GEN-BEGIN:|82-getter|0|82-preInit
    /**
     * Returns an initiliazed instance of setngsSaveCmd component.
     * @return the initialized component instance
     */
    public Command getSetngsSaveCmd() {
        if (setngsSaveCmd == null) {//GEN-END:|82-getter|0|82-preInit
            // write pre-init user code here
            setngsSaveCmd = new Command("Save", Command.SCREEN, 0);//GEN-LINE:|82-getter|1|82-postInit
            // write post-init user code here
        }//GEN-BEGIN:|82-getter|2|
        return setngsSaveCmd;
    }
    //</editor-fold>//GEN-END:|82-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: url ">//GEN-BEGIN:|85-getter|0|85-preInit
    /**
     * Returns an initiliazed instance of url component.
     * @return the initialized component instance
     */
    public TextField getUrl() {
        if (url == null) {//GEN-END:|85-getter|0|85-preInit
            // write pre-init user code here
            url = new TextField("Server Location", "http://localhost:8080/", 64, TextField.URL);//GEN-LINE:|85-getter|1|85-postInit
            // write post-init user code here
        }//GEN-BEGIN:|85-getter|2|
        return url;
    }
    //</editor-fold>//GEN-END:|85-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: adminPass ">//GEN-BEGIN:|86-getter|0|86-preInit
    /**
     * Returns an initiliazed instance of adminPass component.
     * @return the initialized component instance
     */
    public TextField getAdminPass() {
        if (adminPass == null) {//GEN-END:|86-getter|0|86-preInit
            // write pre-init user code here
            adminPass = new TextField("Admin Password", "", 32, TextField.ANY | TextField.PASSWORD);//GEN-LINE:|86-getter|1|86-postInit
            // write post-init user code here
        }//GEN-BEGIN:|86-getter|2|
        return adminPass;
    }
    //</editor-fold>//GEN-END:|86-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dataEntryForm ">//GEN-BEGIN:|92-getter|0|92-preInit
    /**
     * Returns an initiliazed instance of dataEntryForm component.
     * @return the initialized component instance
     */
    public Form getDataEntryForm() {
        if (dataEntryForm == null) {//GEN-END:|92-getter|0|92-preInit
            // write pre-init user code here
            dataEntryForm = new Form("form", new Item[] { });//GEN-BEGIN:|92-getter|1|92-postInit
            dataEntryForm.addCommand(getDeFrmBakCmd());
            dataEntryForm.addCommand(getDeFrmSavCmd());
            dataEntryForm.setCommandListener(this);//GEN-END:|92-getter|1|92-postInit
            // write post-init user code here
        }//GEN-BEGIN:|92-getter|2|
        return dataEntryForm;
    }
    //</editor-fold>//GEN-END:|92-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deFrmBakCmd ">//GEN-BEGIN:|93-getter|0|93-preInit
    /**
     * Returns an initiliazed instance of deFrmBakCmd component.
     * @return the initialized component instance
     */
    public Command getDeFrmBakCmd() {
        if (deFrmBakCmd == null) {//GEN-END:|93-getter|0|93-preInit
            // write pre-init user code here
            deFrmBakCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|93-getter|1|93-postInit
            // write post-init user code here
        }//GEN-BEGIN:|93-getter|2|
        return deFrmBakCmd;
    }
    //</editor-fold>//GEN-END:|93-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deFrmSavCmd ">//GEN-BEGIN:|95-getter|0|95-preInit
    /**
     * Returns an initiliazed instance of deFrmSavCmd component.
     * @return the initialized component instance
     */
    public Command getDeFrmSavCmd() {
        if (deFrmSavCmd == null) {//GEN-END:|95-getter|0|95-preInit
            // write pre-init user code here
            deFrmSavCmd = new Command("Save", Command.SCREEN, 0);//GEN-LINE:|95-getter|1|95-postInit
            // write post-init user code here
        }//GEN-BEGIN:|95-getter|2|
        return deFrmSavCmd;
    }
    //</editor-fold>//GEN-END:|95-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: form ">//GEN-BEGIN:|98-getter|0|98-preInit
    /**
     * Returns an initiliazed instance of form component.
     * @return the initialized component instance
     */
    public Form getForm() {
        if (form == null) {//GEN-END:|98-getter|0|98-preInit
            // write pre-init user code here
            form = new Form("form");//GEN-BEGIN:|98-getter|1|98-postInit
            form.addCommand(getBackCommand());
            form.addCommand(getScreenCommand());
            form.setCommandListener(this);//GEN-END:|98-getter|1|98-postInit
            // write post-init user code here

            ProgramStageForm frm = fetchForm(1);
            renderForm( frm, form );
        }//GEN-BEGIN:|98-getter|2|
        return form;
    }
    //</editor-fold>//GEN-END:|98-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand ">//GEN-BEGIN:|99-getter|0|99-preInit
    /**
     * Returns an initiliazed instance of backCommand component.
     * @return the initialized component instance
     */
    public Command getBackCommand() {
        if (backCommand == null) {//GEN-END:|99-getter|0|99-preInit
            // write pre-init user code here
            backCommand = new Command("Back", Command.BACK, 0);//GEN-LINE:|99-getter|1|99-postInit
            // write post-init user code here
        }//GEN-BEGIN:|99-getter|2|
        return backCommand;
    }
    //</editor-fold>//GEN-END:|99-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: screenCommand ">//GEN-BEGIN:|101-getter|0|101-preInit
    /**
     * Returns an initiliazed instance of screenCommand component.
     * @return the initialized component instance
     */
    public Command getScreenCommand() {
        if (screenCommand == null) {//GEN-END:|101-getter|0|101-preInit
            // write pre-init user code here
            screenCommand = new Command("Save", Command.SCREEN, 0);//GEN-LINE:|101-getter|1|101-postInit
            // write post-init user code here
        }//GEN-BEGIN:|101-getter|2|
        return screenCommand;
    }
    //</editor-fold>//GEN-END:|101-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loginForm ">//GEN-BEGIN:|105-getter|0|105-preInit
    /**
     * Returns an initiliazed instance of loginForm component.
     * @return the initialized component instance
     */
    public Form getLoginForm() {
        if (loginForm == null) {//GEN-END:|105-getter|0|105-preInit
            // write pre-init user code here
            loginForm = new Form("Please login", new Item[] { getUserName(), getPassword() });//GEN-BEGIN:|105-getter|1|105-postInit
            loginForm.addCommand(getLgnFrmExtCmd());
            loginForm.addCommand(getLgnFrmLgnCmd());
            loginForm.setCommandListener(this);//GEN-END:|105-getter|1|105-postInit
            // write post-init user code here
        }//GEN-BEGIN:|105-getter|2|
        return loginForm;
    }
    //</editor-fold>//GEN-END:|105-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: userName ">//GEN-BEGIN:|106-getter|0|106-preInit
    /**
     * Returns an initiliazed instance of userName component.
     * @return the initialized component instance
     */
    public TextField getUserName() {
        if (userName == null) {//GEN-END:|106-getter|0|106-preInit
            // write pre-init user code here
            userName = new TextField("Username", "", 32, TextField.ANY | TextField.SENSITIVE);//GEN-LINE:|106-getter|1|106-postInit
            // write post-init user code here
        }//GEN-BEGIN:|106-getter|2|
        return userName;
    }
    //</editor-fold>//GEN-END:|106-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: password ">//GEN-BEGIN:|107-getter|0|107-preInit
    /**
     * Returns an initiliazed instance of password component.
     * @return the initialized component instance
     */
    public TextField getPassword() {
        if (password == null) {//GEN-END:|107-getter|0|107-preInit
            // write pre-init user code here
            password = new TextField("Password", null, 32, TextField.ANY | TextField.PASSWORD);//GEN-LINE:|107-getter|1|107-postInit
            // write post-init user code here
        }//GEN-BEGIN:|107-getter|2|
        return password;
    }
    //</editor-fold>//GEN-END:|107-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: lgnFrmExtCmd ">//GEN-BEGIN:|108-getter|0|108-preInit
    /**
     * Returns an initiliazed instance of lgnFrmExtCmd component.
     * @return the initialized component instance
     */
    public Command getLgnFrmExtCmd() {
        if (lgnFrmExtCmd == null) {//GEN-END:|108-getter|0|108-preInit
            // write pre-init user code here
            lgnFrmExtCmd = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|108-getter|1|108-postInit
            // write post-init user code here
        }//GEN-BEGIN:|108-getter|2|
        return lgnFrmExtCmd;
    }
    //</editor-fold>//GEN-END:|108-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|110-getter|0|110-preInit
    /**
     * Returns an initiliazed instance of okCommand component.
     * @return the initialized component instance
     */
    public Command getOkCommand() {
        if (okCommand == null) {//GEN-END:|110-getter|0|110-preInit
            // write pre-init user code here
            okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|110-getter|1|110-postInit
            // write post-init user code here
        }//GEN-BEGIN:|110-getter|2|
        return okCommand;
    }
    //</editor-fold>//GEN-END:|110-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: lgnFrmLgnCmd ">//GEN-BEGIN:|112-getter|0|112-preInit
    /**
     * Returns an initiliazed instance of lgnFrmLgnCmd component.
     * @return the initialized component instance
     */
    public Command getLgnFrmLgnCmd() {
        if (lgnFrmLgnCmd == null) {//GEN-END:|112-getter|0|112-preInit
            // write pre-init user code here
            lgnFrmLgnCmd = new Command("Login", Command.SCREEN, 0);//GEN-LINE:|112-getter|1|112-postInit
            // write post-init user code here
        }//GEN-BEGIN:|112-getter|2|
        return lgnFrmLgnCmd;
    }
    //</editor-fold>//GEN-END:|112-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: logo ">//GEN-BEGIN:|116-getter|0|116-preInit
    /**
     * Returns an initiliazed instance of logo component.
     * @return the initialized component instance
     */
    public Image getLogo() {
        if (logo == null) {//GEN-END:|116-getter|0|116-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|116-getter|1|116-@java.io.IOException
                logo = Image.createImage("/org/hisp/dhis/cbhis/image/dhis2_logo.PNG");
            } catch (java.io.IOException e) {//GEN-END:|116-getter|1|116-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|116-getter|2|116-postInit
            // write post-init user code here
        }//GEN-BEGIN:|116-getter|3|
        return logo;
    }
    //</editor-fold>//GEN-END:|116-getter|3|



    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay () {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        switchDisplayable (null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet ();
        } else {
            initialize ();
            startMIDlet ();
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
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
    }

    private void login() {       

        if( getUserName().getString() != null && getPassword().getString() != null )
        {
            if( getUserName().getString().trim().length() != 0 && getPassword().getString().trim().length() != 0 )
            {
                login = true;                
            }
        }
        //Take action based on login value
        if (login) {            
            System.out.println("Login successfull");
            
        } else {
            System.out.println("Login failed...");
        }
        login = false;
    }

    private void saveSettings() {

        SettingsRectordStore settingsRecord;

        try{
            settingsRecord = new SettingsRectordStore("SETTINGS");            
            settingsRecord.put("url", url.getString());
            settingsRecord.put("adminPass", adminPass.getString());
            settingsRecord.save();
        } catch (RecordStoreException rse) {}
    }

    private void loadSettings()
    {
        SettingsRectordStore settingsRecord;

        try{
            settingsRecord = new SettingsRectordStore("SETTINGS");
            
            getUrl().setString( settingsRecord.get("url") );
            getAdminPass().setString( settingsRecord.get("adminPass") );
        } catch (RecordStoreException rse) {}
    }

    private void browseForms()
    {        
        loadSettings();
        downloadManager = new DownloadManager(this, getUrl().getString(), "admin", getAdminPass().getString(), DownloadManager.DOWNLOAD_FORMS );
        downloadManager.start();
    }
    
    public void displayFormsForDownload(Vector forms) 
    {
        programStagesVector = forms;
        
        if( forms == null )
        {
            getFrmDnldList().append("No forms available", null);
        }
        else
        {
            getFrmDnldList().deleteAll();
            for(int i=0; i<forms.size(); i++)
            {            
                AbstractModel programStage = (AbstractModel)forms.elementAt(i);
                getFrmDnldList().insert(i, programStage.getName(), null);
            }
        }
        
        switchDisplayable(null, getFrmDnldList());       
    }

    private void downloadForm( int formId )
    {
        loadSettings();
        downloadManager = new DownloadManager(this, getUrl().getString(), "admin", getAdminPass().getString(), DownloadManager.DOWNLOAD_FORM, formId );
        downloadManager.start();
    }

    public ProgramStageForm fetchForm( int formId )
    {        
        ModelRecordStore modelRecordStore = null;
        ProgramStageForm frm = null;

        try{
            modelRecordStore = new ModelRecordStore(ModelRecordStore.FORM_DB);
            byte rec[] = modelRecordStore.getRecord( formId );
            if ( rec != null )                
                frm = ProgramStageForm.recordToProgramStageForm( rec );                
        } catch (RecordStoreException rse) {}

        return frm;
    }

    public void saveForm( ProgramStageForm programStageForm )
    {
        ModelRecordStore modelRecordStore;

        try{
            modelRecordStore = new ModelRecordStore(ModelRecordStore.FORM_DB);
            modelRecordStore.AddRecord(ProgramStageForm.programStageFormToRecord(programStageForm));
        } catch (RecordStoreException rse) {}

        try{
            modelRecordStore = new ModelRecordStore(ModelRecordStore.DATAELEMENT_DB);
            modelRecordStore.AddDataElementRecords(programStageForm.getDataElements());
        } catch (RecordStoreException rse) {}

    }

    public void renderForm( ProgramStageForm prStgFrm, Form form )
    {      

        programStageForm = prStgFrm;

        if( prStgFrm == null )
        {            
            form.append("The requested form is not available");
        }
        else
        {
            form.deleteAll();

            form.setTitle( prStgFrm.getName() );
            Vector des = prStgFrm.getDataElements();
            
            for(int i=0; i<des.size(); i++)
            {
                DataElement de = (DataElement)des.elementAt(i);
                if( de.getType() == DataElement.TYPE_DATE )
                {
                   DateField dateField = new  DateField( de.getName(), DateField.DATE );
                   form.append( dateField );
                   formElements.put(de,dateField);
                }
                else if( de.getType() == DataElement.TYPE_INT)
                {
                    TextField intField = new TextField(de.getName(),"",32,TextField.NUMERIC);
                    form.append(intField);
                    formElements.put(de,intField);
                }
                else
                {
                    TextField txtField = new TextField(de.getName(),"",32,TextField.ANY);
                    form.append(txtField);
                    formElements.put(de,txtField);
                }
            }
        }

        switchDisplayable(null, form);
    }

    public void sendRecordedData()
    {
        System.out.println("The form is:  " + programStageForm.getName() + "  with an ID of:  " +  programStageForm.getId() );

        System.out.println(".... and the values:  ");

        Vector des = programStageForm.getDataElements();

         for(int i=0; i<des.size(); i++)
         {
             DataElement de = (DataElement)des.elementAt(i);
             if( de.getType() == DataElement.TYPE_DATE )
             {
                 DateField dateField = (DateField)formElements.get(de);
                 System.out.println( de.getName() + " or  " + de.getId()   + "   val   " + dateField.getDate());
             }
             else if( de.getType() == DataElement.TYPE_INT)
             {
                 TextField intField = (TextField)formElements.get(de);
                 System.out.println( de.getName() + " or  " + de.getId()   + "   val   " + intField.getString() );
             }
             else
             {
                 TextField txtField = (TextField)formElements.get(de);
                 System.out.println( de.getName() + " or  " + de.getId()   + "   val   " + txtField.getString() );
             }
         }
    }
}
