package org.hispindia.mobile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.MessageConnection;
import org.netbeans.microedition.lcdui.SplashScreen;

public class ANMApp extends MIDlet implements CommandListener {

    private boolean midletPaused = false;
    private boolean editingLastReport = false;
    private boolean firstRun = false;
    private RecordStore lastMsgStore = null;
    private boolean savedMsg = false;
    private ImageItem imgItem = new ImageItem(null, null, ImageItem.LAYOUT_TOP, null);
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private java.util.Hashtable __previousDisplayables = new java.util.Hashtable();
    private Form ancPage;
    private TextField pregNum;
    private TextField jsyNum;
    private TextField firstTrimesterNum;
    private TextField tt1Num;
    private TextField threeAncNum;
    private TextField ifaTabletNum;
    private TextField boosterNum;
    private TextField anaemicAncNum;
    private TextField hypertensionNum;
    private Form deliveriesPage;
    private TextField nonSbaHomeNum;
    private TextField sbaHomeNum;
    private StringItem homeLabel;
    private TextField jsyHomeNum;
    private TextField newbornsHomeNum;
    private TextField earlyDischargeNum;
    private TextField jsyFacilityMother;
    private StringItem deliveryLabel;
    private TextField facilityNum;
    private TextField jsyFacilityAsha;
    private TextField jsyFacilityAnm;
    private Form pregOutPage;
    private TextField liveFemaleNum;
    private TextField liveMaleNum;
    private StringItem NewbornWeigthLabel;
    private TextField weightedTotalNum;
    private TextField abortionsNum;
    private TextField stillNum;
    private TextField pnc48Num;
    private StringItem postNatalLabel;
    private TextField breastFedNum;
    private TextField underWeightNum;
    private TextField pnc14daysNum;
    private Form familyPage;
    private TextField iudInsFacilityNum;
    private TextField iudRemFacilityNum;
    private TextField ocpDistNum;
    private TextField condomsNum;
    private TextField failMaleSterNum;
    private TextField failFemSterNum;
    private TextField weeklyPillsNum;
    private TextField emerContraNum;
    private TextField compliMaleSterNum;
    private TextField compliFemSterNum;
    private TextField deathFemSterNum;
    private TextField deathMaleSterNum;
    private Form childImmPage1;
    private TextField dpt2Num;
    private TextField dpt1Num;
    private TextField opv0Num;
    private TextField dpt3Num;
    private StringItem childImm1Label;
    private TextField bcgNum;
    private TextField measlesVacNum;
    private TextField fullImmMaleNum;
    private TextField hepB2Num;
    private TextField hepB3Num;
    private TextField opv3Num;
    private TextField hepB1Num;
    private TextField opv1Num;
    private TextField opv2Num;
    private TextField fullImmFemNum;
    private Form childImmPage2;
    private TextField dt5Num;
    private TextField fullImm2FemNum;
    private TextField fullImm2MaleNum;
    private TextField mmrNum;
    private TextField opvBNum;
    private TextField dptBNum;
    private TextField tt16Num;
    private TextField tt10Num;
    private Form childImmPage3;
    private TextField aefiOthersNum;
    private TextField sessionPlannedNum;
    private StringItem stringItem;
    private TextField sessionsAshaNum;
    private TextField sessionHeldNum;
    private TextField aefiDeathsNum;
    private TextField aefiAbscessNum;
    private Form childImmPage4;
    private StringItem childDiseaseLabel;
    private TextField caseMeaslesNum;
    private TextField caseDiarrhNum;
    private TextField caseMalariaNum;
    private TextField vitA1Num;
    private TextField vitA5Num;
    private TextField vitA9Num;
    private Form healthFacilityPage;
    private TextField vhndNum;
    private TextField anaemicOpdNum;
    private TextField hbTestsNum;
    private TextField opdNum;
    private Form sendPage;
    private StringItem sendMsgLabel;
    private Form monthPage;
    private ChoiceGroup monthChoice;
    private ChoiceGroup reportingChoice;
    private TextField dateField;
    private SplashScreen splashScreen;
    private Form loadPage;
    private StringItem questionLabel;
    private ChoiceGroup lastChoice;
    private ImageItem questionImage;
    private Form settingsPage;
    private TextField phone3Num;
    private TextField phone1Num;
    private TextField phone2Num;
    private Alert exitAlert;
    private Command ancCmd;
    private Command deliveriesCmd;
    private Command deliveriesBackCmd;
    private Command pregOutBackCmd;
    private Command pregOutCmd;
    private Command familyBackCmd;
    private Command familyCmd;
    private Command childImm1BackCmd;
    private Command childImm1Cmd;
    private Command childImm2BackCmd;
    private Command childImm2Cmd;
    private Command childImm4Cmd;
    private Command childImm3Cmd;
    private Command childImm3BackCmd;
    private Command healthFacilityCmd;
    private Command healthFacilityBackCmd;
    private Command childImm4BackCmd;
    private Command sendBackCmd;
    private Command sendCmd;
    private Command monthExitCmd;
    private Command ancBackCmd;
    private Command monthCmd;
    private Command sendExitCmd;
    private Command loadExitCmd;
    private Command loadCmd;
    private Command sendSettingsCmd;
    private Command monthSettingsCmd;
    private Command settingsBackCmd;
    private Command settingsCmd;
    private Command exitAlertBackCmd;
    private Command exitAlertCmd;
    private Image nrhmlogo;
    private Image question;
    private Font font;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The ANMApp constructor.
     */
    public ANMApp() {
        try {
            lastMsgStore = RecordStore.openRecordStore("lastMsgStore", true);
            if (lastMsgStore.getNumRecords() == 0) {
                firstRun = true;
            } else {
                firstRun = false;
            }

            if (firstRun) {
                for (int i = 0; i < 90; i++) {
                    try {
                        lastMsgStore.addRecord("".getBytes(), 0, "".getBytes().length);
                    } catch (RecordStoreException rsex) {
                        rsex.printStackTrace();
                    }
                }
            } else {
                if (lastMsgStore.getRecord(4) != null) {
                    String checkSaved = new String(lastMsgStore.getRecord(4));
                    if (checkSaved.equals("true")) {
                        savedMsg = true;
                    }
                }
            }
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    /**
     * Switches a display to previous displayable of the current displayable.
     * The <code>display</code> instance is obtain from the <code>getDisplay</code> method.
     */
    private void switchToPreviousDisplayable() {
        Displayable __currentDisplayable = getDisplay().getCurrent();
        if (__currentDisplayable != null) {
            Displayable __nextDisplayable = (Displayable) __previousDisplayables.get(__currentDisplayable);
            if (__nextDisplayable != null) {
                switchDisplayable(null, __nextDisplayable);
            }
        }
    }
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
        switchDisplayable(null, getSplashScreen());//GEN-LINE:|3-startMIDlet|1|3-postAction
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
        Displayable __currentDisplayable = display.getCurrent();
        if (__currentDisplayable != null  &&  nextDisplayable != null) {
            __previousDisplayables.put(nextDisplayable, __currentDisplayable);
        }
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
// write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
    //</editor-fold>//GEN-END:|5-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ancPage ">//GEN-BEGIN:|14-getter|0|14-preInit
    /**
     * Returns an initiliazed instance of ancPage component.
     * @return the initialized component instance
     */
    public Form getAncPage() {
        if (ancPage == null) {//GEN-END:|14-getter|0|14-preInit
            // write pre-init user code here
            ancPage = new Form("ANC Form", new Item[] { getPregNum(), getFirstTrimesterNum(), getJsyNum(), getThreeAncNum(), getTt1Num(), getBoosterNum(), getIfaTabletNum(), getHypertensionNum(), getAnaemicAncNum() });//GEN-BEGIN:|14-getter|1|14-postInit
            ancPage.addCommand(getAncCmd());
            ancPage.addCommand(getAncBackCmd());
            ancPage.setCommandListener(this);//GEN-END:|14-getter|1|14-postInit
            // write post-init user code here
        }//GEN-BEGIN:|14-getter|2|
        return ancPage;
    }
    //</editor-fold>//GEN-END:|14-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: pregNum ">//GEN-BEGIN:|16-getter|0|16-preInit
    /**
     * Returns an initiliazed instance of pregNum component.
     * @return the initialized component instance
     */
    public TextField getPregNum() {
        if (pregNum == null) {//GEN-END:|16-getter|0|16-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(11) != null) {
                        str = new String(lastMsgStore.getRecord(11));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            pregNum = new TextField("Total No. of Pregnant Women", str, 32, TextField.NUMERIC);//GEN-BEGIN:|16-getter|1|16-postInit
            pregNum.setLayout(ImageItem.LAYOUT_DEFAULT | ImageItem.LAYOUT_NEWLINE_BEFORE | ImageItem.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_SHRINK | Item.LAYOUT_2);//GEN-END:|16-getter|1|16-postInit
            // write post-init user code here
        }//GEN-BEGIN:|16-getter|2|
        return pregNum;
    }
    //</editor-fold>//GEN-END:|16-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: firstTrimesterNum ">//GEN-BEGIN:|18-getter|0|18-preInit
    /**
     * Returns an initiliazed instance of firstTrimesterNum component.
     * @return the initialized component instance
     */
    public TextField getFirstTrimesterNum() {
        if (firstTrimesterNum == null) {//GEN-END:|18-getter|0|18-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(12) != null) {
                        str = new String(lastMsgStore.getRecord(12));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            firstTrimesterNum = new TextField("No. Registered Within 1st Trimester", str, 32, TextField.NUMERIC);//GEN-LINE:|18-getter|1|18-postInit
            // write post-init user code here
        }//GEN-BEGIN:|18-getter|2|
        return firstTrimesterNum;
    }
    //</editor-fold>//GEN-END:|18-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: jsyNum ">//GEN-BEGIN:|19-getter|0|19-preInit
    /**
     * Returns an initiliazed instance of jsyNum component.
     * @return the initialized component instance
     */
    public TextField getJsyNum() {
        if (jsyNum == null) {//GEN-END:|19-getter|0|19-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(13) != null) {
                        str = new String(lastMsgStore.getRecord(13));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            jsyNum = new TextField("No. of New Women Registered Under JSY", str, 32, TextField.NUMERIC);//GEN-LINE:|19-getter|1|19-postInit
            // write post-init user code here
        }//GEN-BEGIN:|19-getter|2|
        return jsyNum;
    }
    //</editor-fold>//GEN-END:|19-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: threeAncNum ">//GEN-BEGIN:|20-getter|0|20-preInit
    /**
     * Returns an initiliazed instance of threeAncNum component.
     * @return the initialized component instance
     */
    public TextField getThreeAncNum() {
        if (threeAncNum == null) {//GEN-END:|20-getter|0|20-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(14) != null) {
                        str = new String(lastMsgStore.getRecord(14));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            threeAncNum = new TextField("No. of Women Received 3 ANC Checkup", str, 32, TextField.NUMERIC);//GEN-LINE:|20-getter|1|20-postInit
            // write post-init user code here
        }//GEN-BEGIN:|20-getter|2|
        return threeAncNum;
    }
    //</editor-fold>//GEN-END:|20-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: tt1Num ">//GEN-BEGIN:|21-getter|0|21-preInit
    /**
     * Returns an initiliazed instance of tt1Num component.
     * @return the initialized component instance
     */
    public TextField getTt1Num() {
        if (tt1Num == null) {//GEN-END:|21-getter|0|21-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(15) != null) {
                        str = new String(lastMsgStore.getRecord(15));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            tt1Num = new TextField("No. of Women Given TT1", str, 32, TextField.NUMERIC);//GEN-LINE:|21-getter|1|21-postInit
            // write post-init user code here
        }//GEN-BEGIN:|21-getter|2|
        return tt1Num;
    }
    //</editor-fold>//GEN-END:|21-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: boosterNum ">//GEN-BEGIN:|22-getter|0|22-preInit
    /**
     * Returns an initiliazed instance of boosterNum component.
     * @return the initialized component instance
     */
    public TextField getBoosterNum() {
        if (boosterNum == null) {//GEN-END:|22-getter|0|22-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(16) != null) {
                        str = new String(lastMsgStore.getRecord(16));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            boosterNum = new TextField("No. of Women Given TT2 / Booster", str, 32, TextField.NUMERIC);//GEN-LINE:|22-getter|1|22-postInit
            // write post-init user code here
        }//GEN-BEGIN:|22-getter|2|
        return boosterNum;
    }
    //</editor-fold>//GEN-END:|22-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ifaTabletNum ">//GEN-BEGIN:|23-getter|0|23-preInit
    /**
     * Returns an initiliazed instance of ifaTabletNum component.
     * @return the initialized component instance
     */
    public TextField getIfaTabletNum() {
        if (ifaTabletNum == null) {//GEN-END:|23-getter|0|23-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(17) != null) {
                        str = new String(lastMsgStore.getRecord(17));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            ifaTabletNum = new TextField("No. of Women Given 100 IFA Tablets", str, 32, TextField.NUMERIC);//GEN-LINE:|23-getter|1|23-postInit
            // write post-init user code here
        }//GEN-BEGIN:|23-getter|2|
        return ifaTabletNum;
    }
    //</editor-fold>//GEN-END:|23-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: hypertensionNum ">//GEN-BEGIN:|24-getter|0|24-preInit
    /**
     * Returns an initiliazed instance of hypertensionNum component.
     * @return the initialized component instance
     */
    public TextField getHypertensionNum() {
        if (hypertensionNum == null) {//GEN-END:|24-getter|0|24-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(18) != null) {
                        str = new String(lastMsgStore.getRecord(18));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            hypertensionNum = new TextField("New Pregnant Women with Hypertension", str, 32, TextField.NUMERIC);//GEN-LINE:|24-getter|1|24-postInit
            // write post-init user code here
        }//GEN-BEGIN:|24-getter|2|
        return hypertensionNum;
    }
    //</editor-fold>//GEN-END:|24-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: anaemicAncNum ">//GEN-BEGIN:|25-getter|0|25-preInit
    /**
     * Returns an initiliazed instance of anaemicAncNum component.
     * @return the initialized component instance
     */
    public TextField getAnaemicAncNum() {
        if (anaemicAncNum == null) {//GEN-END:|25-getter|0|25-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(19) != null) {
                        str = new String(lastMsgStore.getRecord(19));
                    }
                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            anaemicAncNum = new TextField("No. of Pregnant Women Having HB < 11", str, 32, TextField.NUMERIC);//GEN-LINE:|25-getter|1|25-postInit
            // write post-init user code here
        }//GEN-BEGIN:|25-getter|2|
        return anaemicAncNum;
    }
    //</editor-fold>//GEN-END:|25-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == ancPage) {//GEN-BEGIN:|7-commandAction|1|185-preAction
            if (command == ancBackCmd) {//GEN-END:|7-commandAction|1|185-preAction
                // write pre-action user code here
                switchDisplayable(null, getMonthPage());//GEN-LINE:|7-commandAction|2|185-postAction
                // write post-action user code here
            } else if (command == ancCmd) {//GEN-LINE:|7-commandAction|3|28-preAction
                // write pre-action user code here
                switchDisplayable(null, getDeliveriesPage());//GEN-LINE:|7-commandAction|4|28-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|5|109-preAction
        } else if (displayable == childImmPage1) {
            if (command == childImm1BackCmd) {//GEN-END:|7-commandAction|5|109-preAction
                // write pre-action user code here
                switchDisplayable(null, getFamilyPage());//GEN-LINE:|7-commandAction|6|109-postAction
                // write post-action user code here
            } else if (command == childImm1Cmd) {//GEN-LINE:|7-commandAction|7|107-preAction
                // write pre-action user code here
                switchDisplayable(null, getChildImmPage2());//GEN-LINE:|7-commandAction|8|107-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|9|125-preAction
        } else if (displayable == childImmPage2) {
            if (command == childImm2BackCmd) {//GEN-END:|7-commandAction|9|125-preAction
                // write pre-action user code here
                switchDisplayable(null, getChildImmPage1());//GEN-LINE:|7-commandAction|10|125-postAction
                // write post-action user code here
            } else if (command == childImm2Cmd) {//GEN-LINE:|7-commandAction|11|123-preAction
                // write pre-action user code here
                switchDisplayable(null, getChildImmPage3());//GEN-LINE:|7-commandAction|12|123-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|13|131-preAction
        } else if (displayable == childImmPage3) {
            if (command == childImm3BackCmd) {//GEN-END:|7-commandAction|13|131-preAction
                // write pre-action user code here
                switchDisplayable(null, getChildImmPage2());//GEN-LINE:|7-commandAction|14|131-postAction
                // write post-action user code here
            } else if (command == childImm3Cmd) {//GEN-LINE:|7-commandAction|15|129-preAction
                // write pre-action user code here
                switchDisplayable(null, getChildImmPage4());//GEN-LINE:|7-commandAction|16|129-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|17|146-preAction
        } else if (displayable == childImmPage4) {
            if (command == childImm4BackCmd) {//GEN-END:|7-commandAction|17|146-preAction
                // write pre-action user code here
                switchDisplayable(null, getChildImmPage3());//GEN-LINE:|7-commandAction|18|146-postAction
                // write post-action user code here
            } else if (command == childImm4Cmd) {//GEN-LINE:|7-commandAction|19|144-preAction
                // write pre-action user code here
                switchDisplayable(null, getHealthFacilityPage());//GEN-LINE:|7-commandAction|20|144-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|21|47-preAction
        } else if (displayable == deliveriesPage) {
            if (command == deliveriesBackCmd) {//GEN-END:|7-commandAction|21|47-preAction
                // write pre-action user code here
                switchDisplayable(null, getAncPage());//GEN-LINE:|7-commandAction|22|47-postAction
                // write post-action user code here
            } else if (command == deliveriesCmd) {//GEN-LINE:|7-commandAction|23|43-preAction
                // write pre-action user code here
                switchDisplayable(null, getPregOutPage());//GEN-LINE:|7-commandAction|24|43-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|25|263-preAction
        } else if (displayable == exitAlert) {
            if (command == exitAlertBackCmd) {//GEN-END:|7-commandAction|25|263-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendPage());//GEN-LINE:|7-commandAction|26|263-postAction
                // write post-action user code here
            } else if (command == exitAlertCmd) {//GEN-LINE:|7-commandAction|27|261-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|28|261-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|29|86-preAction
        } else if (displayable == familyPage) {
            if (command == familyBackCmd) {//GEN-END:|7-commandAction|29|86-preAction
                // write pre-action user code here
                switchDisplayable(null, getPregOutPage());//GEN-LINE:|7-commandAction|30|86-postAction
                // write post-action user code here
            } else if (command == familyCmd) {//GEN-LINE:|7-commandAction|31|84-preAction
                // write pre-action user code here
                switchDisplayable(null, getChildImmPage1());//GEN-LINE:|7-commandAction|32|84-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|33|160-preAction
        } else if (displayable == healthFacilityPage) {
            if (command == healthFacilityBackCmd) {//GEN-END:|7-commandAction|33|160-preAction
                // write pre-action user code here
                switchDisplayable(null, getChildImmPage4());//GEN-LINE:|7-commandAction|34|160-postAction
                // write post-action user code here
            } else if (command == healthFacilityCmd) {//GEN-LINE:|7-commandAction|35|158-preAction
                // write pre-action user code here
                getEmptyFields();
                switchDisplayable(null, getSendPage());//GEN-LINE:|7-commandAction|36|158-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|37|226-preAction
        } else if (displayable == loadPage) {
            if (command == loadCmd) {//GEN-END:|7-commandAction|37|226-preAction
                int lastSelected = lastChoice.getSelectedIndex();
                if (lastSelected == 1) {
                    editingLastReport = true;
                } else {
                    editingLastReport = false;
                }
                switchDisplayable(null, getMonthPage());//GEN-LINE:|7-commandAction|38|226-postAction
                // write post-action user code here
            } else if (command == loadExitCmd) {//GEN-LINE:|7-commandAction|39|228-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|40|228-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|41|180-preAction
        } else if (displayable == monthPage) {
            if (command == monthCmd) {//GEN-END:|7-commandAction|41|180-preAction
                // write pre-action user code here
                switchDisplayable(null, getAncPage());//GEN-LINE:|7-commandAction|42|180-postAction
                // write post-action user code here
            } else if (command == monthExitCmd) {//GEN-LINE:|7-commandAction|43|188-preAction

                exitMIDlet();//GEN-LINE:|7-commandAction|44|188-postAction
                // write post-action user code here
            } else if (command == monthSettingsCmd) {//GEN-LINE:|7-commandAction|45|252-preAction
                // write pre-action user code here
                switchDisplayable(null, getSettingsPage());//GEN-LINE:|7-commandAction|46|252-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|47|64-preAction
        } else if (displayable == pregOutPage) {
            if (command == pregOutBackCmd) {//GEN-END:|7-commandAction|47|64-preAction
                // write pre-action user code here
                switchDisplayable(null, getDeliveriesPage());//GEN-LINE:|7-commandAction|48|64-postAction
                // write post-action user code here
            } else if (command == pregOutCmd) {//GEN-LINE:|7-commandAction|49|62-preAction
                // write pre-action user code here
                switchDisplayable(null, getFamilyPage());//GEN-LINE:|7-commandAction|50|62-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|51|171-preAction
        } else if (displayable == sendPage) {
            if (command == sendBackCmd) {//GEN-END:|7-commandAction|51|171-preAction
                // write pre-action user code here
                switchDisplayable(null, getHealthFacilityPage());//GEN-LINE:|7-commandAction|52|171-postAction
                // write post-action user code here
            } else if (command == sendCmd) {//GEN-LINE:|7-commandAction|53|169-preAction

                sendMsgLabel.setText("Sending SMS...");
                final String monthData = String.valueOf(monthChoice.getSelectedIndex() + 1);
                String ancFormData = pregNum.getString() + "|" + firstTrimesterNum.getString() + "|" + jsyNum.getString() + "|" + threeAncNum.getString() + "|" + tt1Num.getString() + "|" + boosterNum.getString() + "|" + ifaTabletNum.getString() + "|" + hypertensionNum.getString() + "|" + anaemicAncNum.getString();
                String deliveriesFormData = sbaHomeNum.getString() + "|" + nonSbaHomeNum.getString() + "|" + newbornsHomeNum.getString() + "|" + jsyHomeNum.getString() + "|" + facilityNum.getString() + "|" + earlyDischargeNum.getString() + "|" + jsyFacilityMother.getString() + "|" + jsyFacilityAsha.getString() + "|" + jsyFacilityAnm.getString();
                String pregOutFormData = liveMaleNum.getString() + "|" + liveFemaleNum.getString() + "|" + stillNum.getString() + "|" + abortionsNum.getString() + "|" + weightedTotalNum.getString() + "|" + underWeightNum.getString() + "|" + breastFedNum.getString() + "|" + pnc48Num.getString() + "|" + pnc14daysNum.getString();
                String familyPlanFormData = iudInsFacilityNum.getString() + "|" + iudRemFacilityNum.getString() + "|" + ocpDistNum.getString() + "|" + condomsNum.getString() + "|" + weeklyPillsNum.getString() + "|" + emerContraNum.getString() + "|" + compliMaleSterNum.getString() + "|" + compliFemSterNum.getString() + "|" + failMaleSterNum.getString() + "|" + failFemSterNum.getString() + "|" + deathMaleSterNum.getString() + "|" + deathFemSterNum.getString();
                String childImm1FormData = bcgNum.getString() + "|" + dpt1Num.getString() + "|" + dpt2Num.getString() + "|" + dpt3Num.getString() + "|" + opv0Num.getString() + "|" + opv1Num.getString() + "|" + opv2Num.getString() + "|" + opv3Num.getString() + "|" + hepB1Num.getString() + "|" + hepB2Num.getString() + "|" + hepB3Num.getString() + "|" + measlesVacNum.getString() + "|" + fullImmMaleNum.getString() + "|" + fullImmFemNum.getString();
                String childImm2FormData = dptBNum.getString() + "|" + opvBNum.getString() + "|" + mmrNum.getString() + "|" + fullImm2MaleNum.getString() + "|" + fullImm2FemNum.getString() + "|" + dt5Num.getString() + "|" + tt10Num.getString() + "|" + tt16Num.getString();
                String childImm3FormData = aefiAbscessNum.getString() + "|" + aefiDeathsNum.getString() + "|" + aefiOthersNum.getString() + "|" + sessionPlannedNum.getString() + "|" + sessionHeldNum.getString() + "|" + sessionsAshaNum.getString();
                String childImm4FormData = vitA1Num.getString() + "|" + vitA5Num.getString() + "|" + vitA9Num.getString() + "|" + caseMeaslesNum.getString() + "|" + caseDiarrhNum.getString() + "|" + caseMalariaNum.getString();
                String healthFacilityFormData = vhndNum.getString() + "|" + opdNum.getString() + "|" + hbTestsNum.getString() + "|" + anaemicOpdNum.getString() + "|";

                final String fullData = monthData + "$" + ancFormData + "|" + deliveriesFormData + "|" + pregOutFormData + "|" + familyPlanFormData + "|" + childImm1FormData + "|" + childImm2FormData + "|" + childImm3FormData + "|" + childImm4FormData + "|" +
                        healthFacilityFormData;

                //<editor-fold defaultstate="collapsed" desc=" Thread to Save Records to RMS ">
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            lastMsgStore.setRecord(10, monthData.getBytes(), 0, monthData.length());

                            //<editor-fold defaultstate="collapsed" desc=" ancForm to update RMS ">
                            lastMsgStore.setRecord(11, pregNum.getString().getBytes(), 0, pregNum.getString().length());
                            lastMsgStore.setRecord(12, firstTrimesterNum.getString().getBytes(), 0, firstTrimesterNum.getString().length());
                            lastMsgStore.setRecord(13, jsyNum.getString().getBytes(), 0, jsyNum.getString().length());
                            lastMsgStore.setRecord(14, threeAncNum.getString().getBytes(), 0, threeAncNum.getString().length());
                            lastMsgStore.setRecord(15, tt1Num.getString().getBytes(), 0, tt1Num.getString().length());
                            lastMsgStore.setRecord(16, boosterNum.getString().getBytes(), 0, boosterNum.getString().length());
                            lastMsgStore.setRecord(17, ifaTabletNum.getString().getBytes(), 0, ifaTabletNum.getString().length());
                            lastMsgStore.setRecord(18, hypertensionNum.getString().getBytes(), 0, hypertensionNum.getString().length());
                            lastMsgStore.setRecord(19, anaemicAncNum.getString().getBytes(), 0, anaemicAncNum.getString().length());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc=" deliveriesForm to update RMS ">
                            lastMsgStore.setRecord(20, sbaHomeNum.getString().getBytes(), 0, sbaHomeNum.getString().length());
                            lastMsgStore.setRecord(21, nonSbaHomeNum.getString().getBytes(), 0, nonSbaHomeNum.getString().length());
                            lastMsgStore.setRecord(22, newbornsHomeNum.getString().getBytes(), 0, newbornsHomeNum.getString().length());
                            lastMsgStore.setRecord(23, jsyHomeNum.getString().getBytes(), 0, jsyHomeNum.getString().length());
                            lastMsgStore.setRecord(24, facilityNum.getString().getBytes(), 0, facilityNum.getString().length());
                            lastMsgStore.setRecord(25, earlyDischargeNum.getString().getBytes(), 0, earlyDischargeNum.getString().length());
                            lastMsgStore.setRecord(26, jsyFacilityMother.getString().getBytes(), 0, jsyFacilityMother.getString().length());
                            lastMsgStore.setRecord(27, jsyFacilityAsha.getString().getBytes(), 0, jsyFacilityAsha.getString().length());
                            lastMsgStore.setRecord(28, jsyFacilityAnm.getString().getBytes(), 0, jsyFacilityAnm.getString().length());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc=" pregOutForm to update RMS ">
                            lastMsgStore.setRecord(29, liveMaleNum.getString().getBytes(), 0, liveMaleNum.getString().length());
                            lastMsgStore.setRecord(30, liveFemaleNum.getString().getBytes(), 0, liveFemaleNum.getString().length());
                            lastMsgStore.setRecord(31, stillNum.getString().getBytes(), 0, stillNum.getString().length());
                            lastMsgStore.setRecord(32, abortionsNum.getString().getBytes(), 0, abortionsNum.getString().length());
                            lastMsgStore.setRecord(33, weightedTotalNum.getString().getBytes(), 0, weightedTotalNum.getString().length());
                            lastMsgStore.setRecord(34, underWeightNum.getString().getBytes(), 0, underWeightNum.getString().length());
                            lastMsgStore.setRecord(35, breastFedNum.getString().getBytes(), 0, breastFedNum.getString().length());
                            lastMsgStore.setRecord(36, pnc48Num.getString().getBytes(), 0, pnc48Num.getString().length());
                            lastMsgStore.setRecord(37, pnc14daysNum.getString().getBytes(), 0, pnc14daysNum.getString().length());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc=" familyPlanForm to update RMS ">
                            lastMsgStore.setRecord(38, iudInsFacilityNum.getString().getBytes(), 0, iudInsFacilityNum.getString().length());
                            lastMsgStore.setRecord(39, iudRemFacilityNum.getString().getBytes(), 0, iudRemFacilityNum.getString().length());
                            lastMsgStore.setRecord(40, ocpDistNum.getString().getBytes(), 0, ocpDistNum.getString().length());
                            lastMsgStore.setRecord(41, condomsNum.getString().getBytes(), 0, condomsNum.getString().length());
                            lastMsgStore.setRecord(42, weeklyPillsNum.getString().getBytes(), 0, weeklyPillsNum.getString().length());
                            lastMsgStore.setRecord(43, emerContraNum.getString().getBytes(), 0, emerContraNum.getString().length());
                            lastMsgStore.setRecord(44, compliMaleSterNum.getString().getBytes(), 0, compliMaleSterNum.getString().length());
                            lastMsgStore.setRecord(45, compliFemSterNum.getString().getBytes(), 0, compliFemSterNum.getString().length());
                            lastMsgStore.setRecord(46, failMaleSterNum.getString().getBytes(), 0, failMaleSterNum.getString().length());
                            lastMsgStore.setRecord(47, failFemSterNum.getString().getBytes(), 0, failFemSterNum.getString().length());
                            lastMsgStore.setRecord(48, deathMaleSterNum.getString().getBytes(), 0, deathMaleSterNum.getString().length());
                            lastMsgStore.setRecord(49, deathFemSterNum.getString().getBytes(), 0, deathFemSterNum.getString().length());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc=" childImm1Form to update RMS ">
                            lastMsgStore.setRecord(50, bcgNum.getString().getBytes(), 0, bcgNum.getString().length());
                            lastMsgStore.setRecord(51, dpt1Num.getString().getBytes(), 0, dpt1Num.getString().length());
                            lastMsgStore.setRecord(52, dpt2Num.getString().getBytes(), 0, dpt2Num.getString().length());
                            lastMsgStore.setRecord(53, dpt3Num.getString().getBytes(), 0, dpt3Num.getString().length());
                            lastMsgStore.setRecord(54, opv0Num.getString().getBytes(), 0, opv0Num.getString().length());
                            lastMsgStore.setRecord(55, opv1Num.getString().getBytes(), 0, opv1Num.getString().length());
                            lastMsgStore.setRecord(56, opv2Num.getString().getBytes(), 0, opv2Num.getString().length());
                            lastMsgStore.setRecord(57, opv3Num.getString().getBytes(), 0, opv3Num.getString().length());
                            lastMsgStore.setRecord(58, hepB1Num.getString().getBytes(), 0, hepB1Num.getString().length());
                            lastMsgStore.setRecord(59, hepB2Num.getString().getBytes(), 0, hepB2Num.getString().length());
                            lastMsgStore.setRecord(60, hepB3Num.getString().getBytes(), 0, hepB3Num.getString().length());
                            lastMsgStore.setRecord(61, measlesVacNum.getString().getBytes(), 0, measlesVacNum.getString().length());
                            lastMsgStore.setRecord(62, fullImmMaleNum.getString().getBytes(), 0, fullImmMaleNum.getString().length());
                            lastMsgStore.setRecord(63, fullImmFemNum.getString().getBytes(), 0, fullImmFemNum.getString().length());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc=" childImm2Form to update RMS ">
                            lastMsgStore.setRecord(64, dptBNum.getString().getBytes(), 0, dptBNum.getString().length());
                            lastMsgStore.setRecord(65, opvBNum.getString().getBytes(), 0, opvBNum.getString().length());
                            lastMsgStore.setRecord(66, mmrNum.getString().getBytes(), 0, mmrNum.getString().length());
                            lastMsgStore.setRecord(67, fullImm2MaleNum.getString().getBytes(), 0, fullImm2MaleNum.getString().length());
                            lastMsgStore.setRecord(68, fullImm2FemNum.getString().getBytes(), 0, fullImm2FemNum.getString().length());
                            lastMsgStore.setRecord(69, dt5Num.getString().getBytes(), 0, dt5Num.getString().length());
                            lastMsgStore.setRecord(70, tt10Num.getString().getBytes(), 0, tt10Num.getString().length());
                            lastMsgStore.setRecord(71, tt16Num.getString().getBytes(), 0, tt16Num.getString().length());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc=" childImm3Form to update RMS ">
                            lastMsgStore.setRecord(72, aefiAbscessNum.getString().getBytes(), 0, aefiAbscessNum.getString().length());
                            lastMsgStore.setRecord(73, aefiDeathsNum.getString().getBytes(), 0, aefiDeathsNum.getString().length());
                            lastMsgStore.setRecord(74, aefiOthersNum.getString().getBytes(), 0, aefiOthersNum.getString().length());
                            lastMsgStore.setRecord(75, sessionPlannedNum.getString().getBytes(), 0, sessionPlannedNum.getString().length());
                            lastMsgStore.setRecord(76, sessionHeldNum.getString().getBytes(), 0, sessionHeldNum.getString().length());
                            lastMsgStore.setRecord(77, sessionsAshaNum.getString().getBytes(), 0, sessionsAshaNum.getString().length());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc=" childImm4Form to update RMS ">
                            lastMsgStore.setRecord(78, vitA1Num.getString().getBytes(), 0, vitA1Num.getString().length());
                            lastMsgStore.setRecord(79, vitA5Num.getString().getBytes(), 0, vitA5Num.getString().length());
                            lastMsgStore.setRecord(80, vitA9Num.getString().getBytes(), 0, vitA9Num.getString().length());
                            lastMsgStore.setRecord(81, caseMeaslesNum.getString().getBytes(), 0, caseMeaslesNum.getString().length());
                            lastMsgStore.setRecord(82, caseDiarrhNum.getString().getBytes(), 0, caseDiarrhNum.getString().length());
                            lastMsgStore.setRecord(83, caseMalariaNum.getString().getBytes(), 0, caseMalariaNum.getString().length());
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc=" healthfacilityForm to update RMS ">
                            lastMsgStore.setRecord(84, vhndNum.getString().getBytes(), 0, vhndNum.getString().length());
                            lastMsgStore.setRecord(85, opdNum.getString().getBytes(), 0, opdNum.getString().length());
                            lastMsgStore.setRecord(86, hbTestsNum.getString().getBytes(), 0, hbTestsNum.getString().length());
                            lastMsgStore.setRecord(87, anaemicOpdNum.getString().getBytes(), 0, anaemicOpdNum.getString().length());
                            //</editor-fold>

                            lastMsgStore.setRecord(4, "true".getBytes(), 0, "true".getBytes().length);
                            savedMsg = true;

                        } catch (RecordStoreException ex) {
                            ex.printStackTrace();
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        }
                    }
                }).start();
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc=" Thread to Send SMS ">
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            int j = 0;
                            for (int i = 0; i < 3; i++) {
                                if (lastMsgStore.getRecord(i + 1) != null) {
                                    j++;
                                    MessageConnection smsConn = (MessageConnection) Connector.open("sms://+91" + new String(lastMsgStore.getRecord(i + 1)));
                                    BinaryMessage sms = (BinaryMessage) smsConn.newMessage(MessageConnection.BINARY_MESSAGE);
                                    byte[] compressedData = Compressor.compress(fullData.getBytes("UTF-8"));
                                    sms.setPayloadData(compressedData);
                                    smsConn.send(sms);
                                    smsConn.close();
                                }
                            }
                            if (j == 0) {
                                sendMsgLabel.setText("No Number to Send SMS... Please Go to Settings & Enter a Phone Number");
                            } else {
                                sendMsgLabel.setText("Message Saved & Sent Successfully. You Can Go Back & Edit or Exit the Application");
                            }
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        } catch (RecordStoreException rsex) {
                            rsex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (SecurityException ex) {
                            ex.printStackTrace();
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
                //</editor-fold>

//GEN-LINE:|7-commandAction|54|169-postAction
                // write post-action user code here
            } else if (command == sendExitCmd) {//GEN-LINE:|7-commandAction|55|207-preAction
                // write pre-action user code here
                switchDisplayable(getExitAlert(), getSendPage());//GEN-LINE:|7-commandAction|56|207-postAction
                // write post-action user code here
            } else if (command == sendSettingsCmd) {//GEN-LINE:|7-commandAction|57|255-preAction
                // write pre-action user code here
                switchDisplayable(null, getSettingsPage());//GEN-LINE:|7-commandAction|58|255-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|59|246-preAction
        } else if (displayable == settingsPage) {
            if (command == settingsBackCmd) {//GEN-END:|7-commandAction|59|246-preAction
                // write pre-action user code here
                switchToPreviousDisplayable();//GEN-LINE:|7-commandAction|60|246-postAction
                // write post-action user code here
            } else if (command == settingsCmd) {//GEN-LINE:|7-commandAction|61|243-preAction
                try {
                    String phone1 = phone1Num.getString();
                    String phone2 = phone2Num.getString();
                    String phone3 = phone3Num.getString();

                    lastMsgStore.setRecord(1, phone1.getBytes(), 0, phone1.length());
                    lastMsgStore.setRecord(2, phone2.getBytes(), 0, phone2.length());
                    lastMsgStore.setRecord(3, phone3.getBytes(), 0, phone3.length());

                    if (sendMsgLabel != null) {
                        sendMsgLabel.setText("Saved Settings... Press \"Send SMS\" to send report");
                    }
                } catch (RecordStoreException ex) {
                    ex.printStackTrace();
                }
                switchToPreviousDisplayable();//GEN-LINE:|7-commandAction|62|243-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|63|203-preAction
        } else if (displayable == splashScreen) {
            if (command == SplashScreen.DISMISS_COMMAND) {//GEN-END:|7-commandAction|63|203-preAction
                if (savedMsg == false) {
                    switchDisplayable(null, getMonthPage());
                } else {
                    switchDisplayable(null, getLoadPage());//GEN-LINE:|7-commandAction|64|203-postAction
                }
            }//GEN-BEGIN:|7-commandAction|65|7-postCommandAction
        }//GEN-END:|7-commandAction|65|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|66|
    //</editor-fold>//GEN-END:|7-commandAction|66|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deliveriesPage ">//GEN-BEGIN:|26-getter|0|26-preInit
    /**
     * Returns an initiliazed instance of deliveriesPage component.
     * @return the initialized component instance
     */
    public Form getDeliveriesPage() {
        if (deliveriesPage == null) {//GEN-END:|26-getter|0|26-preInit
            // write pre-init user code here
            deliveriesPage = new Form("Deliveries Form", new Item[] { getHomeLabel(), getSbaHomeNum(), getNonSbaHomeNum(), getNewbornsHomeNum(), getJsyHomeNum(), getDeliveryLabel(), getFacilityNum(), getEarlyDischargeNum(), getJsyFacilityMother(), getJsyFacilityAsha(), getJsyFacilityAnm() });//GEN-BEGIN:|26-getter|1|26-postInit
            deliveriesPage.addCommand(getDeliveriesCmd());
            deliveriesPage.addCommand(getDeliveriesBackCmd());
            deliveriesPage.setCommandListener(this);//GEN-END:|26-getter|1|26-postInit
            // write post-init user code here
        }//GEN-BEGIN:|26-getter|2|
        return deliveriesPage;
    }
    //</editor-fold>//GEN-END:|26-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sbaHomeNum ">//GEN-BEGIN:|30-getter|0|30-preInit
    /**
     * Returns an initiliazed instance of sbaHomeNum component.
     * @return the initialized component instance
     */
    public TextField getSbaHomeNum() {
        if (sbaHomeNum == null) {//GEN-END:|30-getter|0|30-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(20) != null) {
                        str = new String(lastMsgStore.getRecord(20));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            sbaHomeNum = new TextField("Attended by SBA Trained", str, 32, TextField.NUMERIC);//GEN-LINE:|30-getter|1|30-postInit
            // write post-init user code here
        }//GEN-BEGIN:|30-getter|2|
        return sbaHomeNum;
    }
    //</editor-fold>//GEN-END:|30-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: nonSbaHomeNum ">//GEN-BEGIN:|31-getter|0|31-preInit
    /**
     * Returns an initiliazed instance of nonSbaHomeNum component.
     * @return the initialized component instance
     */
    public TextField getNonSbaHomeNum() {
        if (nonSbaHomeNum == null) {//GEN-END:|31-getter|0|31-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(21) != null) {
                        str = new String(lastMsgStore.getRecord(21));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            nonSbaHomeNum = new TextField("Attended by Non-SBA", str, 32, TextField.NUMERIC);//GEN-LINE:|31-getter|1|31-postInit
            // write post-init user code here
        }//GEN-BEGIN:|31-getter|2|
        return nonSbaHomeNum;
    }
    //</editor-fold>//GEN-END:|31-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: newbornsHomeNum ">//GEN-BEGIN:|33-getter|0|33-preInit
    /**
     * Returns an initiliazed instance of newbornsHomeNum component.
     * @return the initialized component instance
     */
    public TextField getNewbornsHomeNum() {
        if (newbornsHomeNum == null) {//GEN-END:|33-getter|0|33-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(22) != null) {
                        str = new String(lastMsgStore.getRecord(22));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            newbornsHomeNum = new TextField("Newborns Visited Within 24hrs", str, 32, TextField.NUMERIC);//GEN-LINE:|33-getter|1|33-postInit
            // write post-init user code here
        }//GEN-BEGIN:|33-getter|2|
        return newbornsHomeNum;
    }
    //</editor-fold>//GEN-END:|33-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: homeLabel ">//GEN-BEGIN:|34-getter|0|34-preInit
    /**
     * Returns an initiliazed instance of homeLabel component.
     * @return the initialized component instance
     */
    public StringItem getHomeLabel() {
        if (homeLabel == null) {//GEN-END:|34-getter|0|34-preInit
            // write pre-init user code here
            homeLabel = new StringItem("", "HOME DELIVERIES", Item.PLAIN);//GEN-BEGIN:|34-getter|1|34-postInit
            homeLabel.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_2);
            homeLabel.setFont(getFont());//GEN-END:|34-getter|1|34-postInit
            // write post-init user code here
        }//GEN-BEGIN:|34-getter|2|
        return homeLabel;
    }
    //</editor-fold>//GEN-END:|34-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: jsyHomeNum ">//GEN-BEGIN:|35-getter|0|35-preInit
    /**
     * Returns an initiliazed instance of jsyHomeNum component.
     * @return the initialized component instance
     */
    public TextField getJsyHomeNum() {
        if (jsyHomeNum == null) {//GEN-END:|35-getter|0|35-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(23) != null) {
                        str = new String(lastMsgStore.getRecord(23));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            jsyHomeNum = new TextField("No. of Mothers Paid JSY Incentive", str, 32, TextField.NUMERIC);//GEN-LINE:|35-getter|1|35-postInit
            // write post-init user code here
        }//GEN-BEGIN:|35-getter|2|
        return jsyHomeNum;
    }
    //</editor-fold>//GEN-END:|35-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deliveryLabel ">//GEN-BEGIN:|36-getter|0|36-preInit
    /**
     * Returns an initiliazed instance of deliveryLabel component.
     * @return the initialized component instance
     */
    public StringItem getDeliveryLabel() {
        if (deliveryLabel == null) {//GEN-END:|36-getter|0|36-preInit
            // write pre-init user code here
            deliveryLabel = new StringItem("", "DELIVERIES AT FACILITY");//GEN-BEGIN:|36-getter|1|36-postInit
            deliveryLabel.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_2);
            deliveryLabel.setFont(getFont());//GEN-END:|36-getter|1|36-postInit
            // write post-init user code here
        }//GEN-BEGIN:|36-getter|2|
        return deliveryLabel;
    }
    //</editor-fold>//GEN-END:|36-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ancCmd ">//GEN-BEGIN:|27-getter|0|27-preInit
    /**
     * Returns an initiliazed instance of ancCmd component.
     * @return the initialized component instance
     */
    public Command getAncCmd() {
        if (ancCmd == null) {//GEN-END:|27-getter|0|27-preInit
            // write pre-init user code here
            ancCmd = new Command("Next", Command.OK, 0);//GEN-LINE:|27-getter|1|27-postInit
            // write post-init user code here
        }//GEN-BEGIN:|27-getter|2|
        return ancCmd;
    }
    //</editor-fold>//GEN-END:|27-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: facilityNum ">//GEN-BEGIN:|37-getter|0|37-preInit
    /**
     * Returns an initiliazed instance of facilityNum component.
     * @return the initialized component instance
     */
    public TextField getFacilityNum() {
        if (facilityNum == null) {//GEN-END:|37-getter|0|37-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(24) != null) {
                        str = new String(lastMsgStore.getRecord(24));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            facilityNum = new TextField("No. of Deliveries at Facility", str, 32, TextField.NUMERIC);//GEN-LINE:|37-getter|1|37-postInit
            // write post-init user code here
        }//GEN-BEGIN:|37-getter|2|
        return facilityNum;
    }
    //</editor-fold>//GEN-END:|37-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: earlyDischargeNum ">//GEN-BEGIN:|38-getter|0|38-preInit
    /**
     * Returns an initiliazed instance of earlyDischargeNum component.
     * @return the initialized component instance
     */
    public TextField getEarlyDischargeNum() {
        if (earlyDischargeNum == null) {//GEN-END:|38-getter|0|38-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(25) != null) {
                        str = new String(lastMsgStore.getRecord(25));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            earlyDischargeNum = new TextField("No. Discharged < 48hrs of Delivery", str, 32, TextField.NUMERIC);//GEN-LINE:|38-getter|1|38-postInit
            // write post-init user code here
        }//GEN-BEGIN:|38-getter|2|
        return earlyDischargeNum;
    }
    //</editor-fold>//GEN-END:|38-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: jsyFacilityMother ">//GEN-BEGIN:|39-getter|0|39-preInit
    /**
     * Returns an initiliazed instance of jsyFacilityMother component.
     * @return the initialized component instance
     */
    public TextField getJsyFacilityMother() {
        if (jsyFacilityMother == null) {//GEN-END:|39-getter|0|39-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(26) != null) {
                        str = new String(lastMsgStore.getRecord(26));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            jsyFacilityMother = new TextField("No.of Mothers Paid JSY Incentive", str, 32, TextField.NUMERIC);//GEN-LINE:|39-getter|1|39-postInit
            // write post-init user code here
        }//GEN-BEGIN:|39-getter|2|
        return jsyFacilityMother;
    }
    //</editor-fold>//GEN-END:|39-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: jsyFacilityAsha ">//GEN-BEGIN:|40-getter|0|40-preInit
    /**
     * Returns an initiliazed instance of jsyFacilityAsha component.
     * @return the initialized component instance
     */
    public TextField getJsyFacilityAsha() {
        if (jsyFacilityAsha == null) {//GEN-END:|40-getter|0|40-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(27) != null) {
                        str = new String(lastMsgStore.getRecord(27));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            jsyFacilityAsha = new TextField("No.of ASHAs Paid JSY Incentive", str, 32, TextField.NUMERIC);//GEN-LINE:|40-getter|1|40-postInit
            // write post-init user code here
        }//GEN-BEGIN:|40-getter|2|
        return jsyFacilityAsha;
    }
    //</editor-fold>//GEN-END:|40-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: jsyFacilityAnm ">//GEN-BEGIN:|41-getter|0|41-preInit
    /**
     * Returns an initiliazed instance of jsyFacilityAnm component.
     * @return the initialized component instance
     */
    public TextField getJsyFacilityAnm() {
        if (jsyFacilityAnm == null) {//GEN-END:|41-getter|0|41-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(28) != null) {
                        str = new String(lastMsgStore.getRecord(28));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            jsyFacilityAnm = new TextField("No.of ANM / AWW Paid JSY Incentive", str, 32, TextField.NUMERIC);//GEN-LINE:|41-getter|1|41-postInit
            // write post-init user code here
        }//GEN-BEGIN:|41-getter|2|
        return jsyFacilityAnm;
    }
    //</editor-fold>//GEN-END:|41-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: pregOutPage ">//GEN-BEGIN:|44-getter|0|44-preInit
    /**
     * Returns an initiliazed instance of pregOutPage component.
     * @return the initialized component instance
     */
    public Form getPregOutPage() {
        if (pregOutPage == null) {//GEN-END:|44-getter|0|44-preInit
            // write pre-init user code here
            pregOutPage = new Form("Pregnancy Outcome Form", new Item[] { getLiveMaleNum(), getLiveFemaleNum(), getStillNum(), getAbortionsNum(), getNewbornWeigthLabel(), getWeightedTotalNum(), getUnderWeightNum(), getBreastFedNum(), getPostNatalLabel(), getPnc48Num(), getPnc14daysNum() });//GEN-BEGIN:|44-getter|1|44-postInit
            pregOutPage.addCommand(getPregOutCmd());
            pregOutPage.addCommand(getPregOutBackCmd());
            pregOutPage.setCommandListener(this);//GEN-END:|44-getter|1|44-postInit
            // write post-init user code here
        }//GEN-BEGIN:|44-getter|2|
        return pregOutPage;
    }
    //</editor-fold>//GEN-END:|44-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: liveMaleNum ">//GEN-BEGIN:|49-getter|0|49-preInit
    /**
     * Returns an initiliazed instance of liveMaleNum component.
     * @return the initialized component instance
     */
    public TextField getLiveMaleNum() {
        if (liveMaleNum == null) {//GEN-END:|49-getter|0|49-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(29) != null) {
                        str = new String(lastMsgStore.getRecord(29));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            liveMaleNum = new TextField("Live Births: (Male)", str, 32, TextField.NUMERIC);//GEN-LINE:|49-getter|1|49-postInit
            // write post-init user code here
        }//GEN-BEGIN:|49-getter|2|
        return liveMaleNum;
    }
    //</editor-fold>//GEN-END:|49-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: liveFemaleNum ">//GEN-BEGIN:|50-getter|0|50-preInit
    /**
     * Returns an initiliazed instance of liveFemaleNum component.
     * @return the initialized component instance
     */
    public TextField getLiveFemaleNum() {
        if (liveFemaleNum == null) {//GEN-END:|50-getter|0|50-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(30) != null) {
                        str = new String(lastMsgStore.getRecord(30));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            liveFemaleNum = new TextField("Live Births: (Female)", str, 32, TextField.NUMERIC);//GEN-LINE:|50-getter|1|50-postInit
            // write post-init user code here
        }//GEN-BEGIN:|50-getter|2|
        return liveFemaleNum;
    }
    //</editor-fold>//GEN-END:|50-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stillNum ">//GEN-BEGIN:|52-getter|0|52-preInit
    /**
     * Returns an initiliazed instance of stillNum component.
     * @return the initialized component instance
     */
    public TextField getStillNum() {
        if (stillNum == null) {//GEN-END:|52-getter|0|52-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(31) != null) {
                        str = new String(lastMsgStore.getRecord(31));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            stillNum = new TextField("No. of Still Births", str, 32, TextField.NUMERIC);//GEN-LINE:|52-getter|1|52-postInit
            // write post-init user code here
        }//GEN-BEGIN:|52-getter|2|
        return stillNum;
    }
    //</editor-fold>//GEN-END:|52-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: abortionsNum ">//GEN-BEGIN:|53-getter|0|53-preInit
    /**
     * Returns an initiliazed instance of abortionsNum component.
     * @return the initialized component instance
     */
    public TextField getAbortionsNum() {
        if (abortionsNum == null) {//GEN-END:|53-getter|0|53-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(32) != null) {
                        str = new String(lastMsgStore.getRecord(32));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            abortionsNum = new TextField("Abortions (spontaneous / induced)", str, 32, TextField.NUMERIC);//GEN-LINE:|53-getter|1|53-postInit
            // write post-init user code here
        }//GEN-BEGIN:|53-getter|2|
        return abortionsNum;
    }
    //</editor-fold>//GEN-END:|53-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: weightedTotalNum ">//GEN-BEGIN:|54-getter|0|54-preInit
    /**
     * Returns an initiliazed instance of weightedTotalNum component.
     * @return the initialized component instance
     */
    public TextField getWeightedTotalNum() {
        if (weightedTotalNum == null) {//GEN-END:|54-getter|0|54-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(33) != null) {
                        str = new String(lastMsgStore.getRecord(33));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            weightedTotalNum = new TextField("No. of Newborns Weighted at Birth", str, 32, TextField.NUMERIC);//GEN-LINE:|54-getter|1|54-postInit
            // write post-init user code here
        }//GEN-BEGIN:|54-getter|2|
        return weightedTotalNum;
    }
    //</editor-fold>//GEN-END:|54-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: NewbornWeigthLabel ">//GEN-BEGIN:|55-getter|0|55-preInit
    /**
     * Returns an initiliazed instance of NewbornWeigthLabel component.
     * @return the initialized component instance
     */
    public StringItem getNewbornWeigthLabel() {
        if (NewbornWeigthLabel == null) {//GEN-END:|55-getter|0|55-preInit
            // write pre-init user code here
            NewbornWeigthLabel = new StringItem("", "DETAILS OF NEWBORNS WEIGHED");//GEN-BEGIN:|55-getter|1|55-postInit
            NewbornWeigthLabel.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_2);
            NewbornWeigthLabel.setFont(getFont());//GEN-END:|55-getter|1|55-postInit
            // write post-init user code here
        }//GEN-BEGIN:|55-getter|2|
        return NewbornWeigthLabel;
    }
    //</editor-fold>//GEN-END:|55-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: underWeightNum ">//GEN-BEGIN:|56-getter|0|56-preInit
    /**
     * Returns an initiliazed instance of underWeightNum component.
     * @return the initialized component instance
     */
    public TextField getUnderWeightNum() {
        if (underWeightNum == null) {//GEN-END:|56-getter|0|56-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(34) != null) {
                        str = new String(lastMsgStore.getRecord(34));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            underWeightNum = new TextField("No. of Newborns < 2.5 kgs", str, 32, TextField.NUMERIC);//GEN-LINE:|56-getter|1|56-postInit
            // write post-init user code here
        }//GEN-BEGIN:|56-getter|2|
        return underWeightNum;
    }
    //</editor-fold>//GEN-END:|56-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: breastFedNum ">//GEN-BEGIN:|57-getter|0|57-preInit
    /**
     * Returns an initiliazed instance of breastFedNum component.
     * @return the initialized component instance
     */
    public TextField getBreastFedNum() {
        if (breastFedNum == null) {//GEN-END:|57-getter|0|57-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(35) != null) {
                        str = new String(lastMsgStore.getRecord(35));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            breastFedNum = new TextField("No. of Newborns Breastfed Within 1 hr", str, 32, TextField.NUMERIC);//GEN-LINE:|57-getter|1|57-postInit
            // write post-init user code here
        }//GEN-BEGIN:|57-getter|2|
        return breastFedNum;
    }
    //</editor-fold>//GEN-END:|57-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deliveriesCmd ">//GEN-BEGIN:|42-getter|0|42-preInit
    /**
     * Returns an initiliazed instance of deliveriesCmd component.
     * @return the initialized component instance
     */
    public Command getDeliveriesCmd() {
        if (deliveriesCmd == null) {//GEN-END:|42-getter|0|42-preInit
            // write pre-init user code here
            deliveriesCmd = new Command("Next", Command.OK, 0);//GEN-LINE:|42-getter|1|42-postInit
            // write post-init user code here
        }//GEN-BEGIN:|42-getter|2|
        return deliveriesCmd;
    }
    //</editor-fold>//GEN-END:|42-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deliveriesBackCmd ">//GEN-BEGIN:|46-getter|0|46-preInit
    /**
     * Returns an initiliazed instance of deliveriesBackCmd component.
     * @return the initialized component instance
     */
    public Command getDeliveriesBackCmd() {
        if (deliveriesBackCmd == null) {//GEN-END:|46-getter|0|46-preInit
            // write pre-init user code here
            deliveriesBackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|46-getter|1|46-postInit
            // write post-init user code here
        }//GEN-BEGIN:|46-getter|2|
        return deliveriesBackCmd;
    }
    //</editor-fold>//GEN-END:|46-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: postNatalLabel ">//GEN-BEGIN:|58-getter|0|58-preInit
    /**
     * Returns an initiliazed instance of postNatalLabel component.
     * @return the initialized component instance
     */
    public StringItem getPostNatalLabel() {
        if (postNatalLabel == null) {//GEN-END:|58-getter|0|58-preInit
            // write pre-init user code here
            postNatalLabel = new StringItem("", "POST-NATAL CARE");//GEN-BEGIN:|58-getter|1|58-postInit
            postNatalLabel.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_2);
            postNatalLabel.setFont(getFont());//GEN-END:|58-getter|1|58-postInit
            // write post-init user code here
        }//GEN-BEGIN:|58-getter|2|
        return postNatalLabel;
    }
    //</editor-fold>//GEN-END:|58-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: pnc48Num ">//GEN-BEGIN:|59-getter|0|59-preInit
    /**
     * Returns an initiliazed instance of pnc48Num component.
     * @return the initialized component instance
     */
    public TextField getPnc48Num() {
        if (pnc48Num == null) {//GEN-END:|59-getter|0|59-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(36) != null) {
                        str = new String(lastMsgStore.getRecord(36));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            pnc48Num = new TextField("Women Receiving PNC Within 48 hrs", str, 32, TextField.NUMERIC);//GEN-LINE:|59-getter|1|59-postInit
            // write post-init user code here
        }//GEN-BEGIN:|59-getter|2|
        return pnc48Num;
    }
    //</editor-fold>//GEN-END:|59-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: pnc14daysNum ">//GEN-BEGIN:|60-getter|0|60-preInit
    /**
     * Returns an initiliazed instance of pnc14daysNum component.
     * @return the initialized component instance
     */
    public TextField getPnc14daysNum() {
        if (pnc14daysNum == null) {//GEN-END:|60-getter|0|60-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(37) != null) {
                        str = new String(lastMsgStore.getRecord(37));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            pnc14daysNum = new TextField("Women Receiving PNC (48 hrs - 14 days)", str, 32, TextField.NUMERIC);//GEN-LINE:|60-getter|1|60-postInit
            // write post-init user code here
        }//GEN-BEGIN:|60-getter|2|
        return pnc14daysNum;
    }
    //</editor-fold>//GEN-END:|60-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: familyPage ">//GEN-BEGIN:|66-getter|0|66-preInit
    /**
     * Returns an initiliazed instance of familyPage component.
     * @return the initialized component instance
     */
    public Form getFamilyPage() {
        if (familyPage == null) {//GEN-END:|66-getter|0|66-preInit
            // write pre-init user code here
            familyPage = new Form("Family Planning Form", new Item[] { getIudInsFacilityNum(), getIudRemFacilityNum(), getOcpDistNum(), getCondomsNum(), getWeeklyPillsNum(), getEmerContraNum(), getCompliMaleSterNum(), getCompliFemSterNum(), getFailMaleSterNum(), getFailFemSterNum(), getDeathMaleSterNum(), getDeathFemSterNum() });//GEN-BEGIN:|66-getter|1|66-postInit
            familyPage.addCommand(getFamilyCmd());
            familyPage.addCommand(getFamilyBackCmd());
            familyPage.setCommandListener(this);//GEN-END:|66-getter|1|66-postInit
            // write post-init user code here
        }//GEN-BEGIN:|66-getter|2|
        return familyPage;
    }
    //</editor-fold>//GEN-END:|66-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: iudInsFacilityNum ">//GEN-BEGIN:|68-getter|0|68-preInit
    /**
     * Returns an initiliazed instance of iudInsFacilityNum component.
     * @return the initialized component instance
     */
    public TextField getIudInsFacilityNum() {
        if (iudInsFacilityNum == null) {//GEN-END:|68-getter|0|68-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(38) != null) {
                        str = new String(lastMsgStore.getRecord(38));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            iudInsFacilityNum = new TextField("New IUD Insertions at Facility", str, 32, TextField.NUMERIC);//GEN-LINE:|68-getter|1|68-postInit
            // write post-init user code here
        }//GEN-BEGIN:|68-getter|2|
        return iudInsFacilityNum;
    }
    //</editor-fold>//GEN-END:|68-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: iudRemFacilityNum ">//GEN-BEGIN:|69-getter|0|69-preInit
    /**
     * Returns an initiliazed instance of iudRemFacilityNum component.
     * @return the initialized component instance
     */
    public TextField getIudRemFacilityNum() {
        if (iudRemFacilityNum == null) {//GEN-END:|69-getter|0|69-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(39) != null) {
                        str = new String(lastMsgStore.getRecord(39));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            iudRemFacilityNum = new TextField("IUD Removals at Facility", str, 32, TextField.NUMERIC);//GEN-LINE:|69-getter|1|69-postInit
            // write post-init user code here
        }//GEN-BEGIN:|69-getter|2|
        return iudRemFacilityNum;
    }
    //</editor-fold>//GEN-END:|69-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ocpDistNum ">//GEN-BEGIN:|70-getter|0|70-preInit
    /**
     * Returns an initiliazed instance of ocpDistNum component.
     * @return the initialized component instance
     */
    public TextField getOcpDistNum() {
        if (ocpDistNum == null) {//GEN-END:|70-getter|0|70-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(40) != null) {
                        str = new String(lastMsgStore.getRecord(40));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            ocpDistNum = new TextField("Oral Pill Cycles Distributed", str, 32, TextField.NUMERIC);//GEN-LINE:|70-getter|1|70-postInit
            // write post-init user code here
        }//GEN-BEGIN:|70-getter|2|
        return ocpDistNum;
    }
    //</editor-fold>//GEN-END:|70-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: condomsNum ">//GEN-BEGIN:|71-getter|0|71-preInit
    /**
     * Returns an initiliazed instance of condomsNum component.
     * @return the initialized component instance
     */
    public TextField getCondomsNum() {
        if (condomsNum == null) {//GEN-END:|71-getter|0|71-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(41) != null) {
                        str = new String(lastMsgStore.getRecord(41));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            condomsNum = new TextField("Condoms Distributed", str, 32, TextField.NUMERIC);//GEN-LINE:|71-getter|1|71-postInit
            // write post-init user code here
        }//GEN-BEGIN:|71-getter|2|
        return condomsNum;
    }
    //</editor-fold>//GEN-END:|71-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: weeklyPillsNum ">//GEN-BEGIN:|72-getter|0|72-preInit
    /**
     * Returns an initiliazed instance of weeklyPillsNum component.
     * @return the initialized component instance
     */
    public TextField getWeeklyPillsNum() {
        if (weeklyPillsNum == null) {//GEN-END:|72-getter|0|72-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(42) != null) {
                        str = new String(lastMsgStore.getRecord(42));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            weeklyPillsNum = new TextField("Centchroman (Weekly) Pills Distributed", str, 32, TextField.NUMERIC);//GEN-LINE:|72-getter|1|72-postInit
            // write post-init user code here
        }//GEN-BEGIN:|72-getter|2|
        return weeklyPillsNum;
    }
    //</editor-fold>//GEN-END:|72-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: pregOutCmd ">//GEN-BEGIN:|61-getter|0|61-preInit
    /**
     * Returns an initiliazed instance of pregOutCmd component.
     * @return the initialized component instance
     */
    public Command getPregOutCmd() {
        if (pregOutCmd == null) {//GEN-END:|61-getter|0|61-preInit
            // write pre-init user code here
            pregOutCmd = new Command("Next", Command.OK, 0);//GEN-LINE:|61-getter|1|61-postInit
            // write post-init user code here
        }//GEN-BEGIN:|61-getter|2|
        return pregOutCmd;
    }
    //</editor-fold>//GEN-END:|61-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: pregOutBackCmd ">//GEN-BEGIN:|63-getter|0|63-preInit
    /**
     * Returns an initiliazed instance of pregOutBackCmd component.
     * @return the initialized component instance
     */
    public Command getPregOutBackCmd() {
        if (pregOutBackCmd == null) {//GEN-END:|63-getter|0|63-preInit
            // write pre-init user code here
            pregOutBackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|63-getter|1|63-postInit
            // write post-init user code here
        }//GEN-BEGIN:|63-getter|2|
        return pregOutBackCmd;
    }
    //</editor-fold>//GEN-END:|63-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: emerContraNum ">//GEN-BEGIN:|73-getter|0|73-preInit
    /**
     * Returns an initiliazed instance of emerContraNum component.
     * @return the initialized component instance
     */
    public TextField getEmerContraNum() {
        if (emerContraNum == null) {//GEN-END:|73-getter|0|73-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(43) != null) {
                        str = new String(lastMsgStore.getRecord(43));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            emerContraNum = new TextField("Emergency Contraceptive Pills Distributed", str, 32, TextField.NUMERIC);//GEN-LINE:|73-getter|1|73-postInit
            // write post-init user code here
        }//GEN-BEGIN:|73-getter|2|
        return emerContraNum;
    }
    //</editor-fold>//GEN-END:|73-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: compliMaleSterNum ">//GEN-BEGIN:|74-getter|0|74-preInit
    /**
     * Returns an initiliazed instance of compliMaleSterNum component.
     * @return the initialized component instance
     */
    public TextField getCompliMaleSterNum() {
        if (compliMaleSterNum == null) {//GEN-END:|74-getter|0|74-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(44) != null) {
                        str = new String(lastMsgStore.getRecord(44));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            compliMaleSterNum = new TextField("Complications After Male Sterilization", str, 32, TextField.NUMERIC);//GEN-LINE:|74-getter|1|74-postInit
            // write post-init user code here
        }//GEN-BEGIN:|74-getter|2|
        return compliMaleSterNum;
    }
    //</editor-fold>//GEN-END:|74-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: compliFemSterNum ">//GEN-BEGIN:|75-getter|0|75-preInit
    /**
     * Returns an initiliazed instance of compliFemSterNum component.
     * @return the initialized component instance
     */
    public TextField getCompliFemSterNum() {
        if (compliFemSterNum == null) {//GEN-END:|75-getter|0|75-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(45) != null) {
                        str = new String(lastMsgStore.getRecord(45));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            compliFemSterNum = new TextField("Complications After Female Sterilization", str, 32, TextField.NUMERIC);//GEN-LINE:|75-getter|1|75-postInit
            // write post-init user code here
        }//GEN-BEGIN:|75-getter|2|
        return compliFemSterNum;
    }
    //</editor-fold>//GEN-END:|75-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: failMaleSterNum ">//GEN-BEGIN:|77-getter|0|77-preInit
    /**
     * Returns an initiliazed instance of failMaleSterNum component.
     * @return the initialized component instance
     */
    public TextField getFailMaleSterNum() {
        if (failMaleSterNum == null) {//GEN-END:|77-getter|0|77-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(46) != null) {
                        str = new String(lastMsgStore.getRecord(46));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            failMaleSterNum = new TextField("Failures After Male Sterilization", str, 32, TextField.NUMERIC);//GEN-LINE:|77-getter|1|77-postInit
            // write post-init user code here
        }//GEN-BEGIN:|77-getter|2|
        return failMaleSterNum;
    }
    //</editor-fold>//GEN-END:|77-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: failFemSterNum ">//GEN-BEGIN:|78-getter|0|78-preInit
    /**
     * Returns an initiliazed instance of failFemSterNum component.
     * @return the initialized component instance
     */
    public TextField getFailFemSterNum() {
        if (failFemSterNum == null) {//GEN-END:|78-getter|0|78-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(47) != null) {
                        str = new String(lastMsgStore.getRecord(47));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            failFemSterNum = new TextField("Failures After Female Sterilization", str, 32, TextField.NUMERIC);//GEN-LINE:|78-getter|1|78-postInit
            // write post-init user code here
        }//GEN-BEGIN:|78-getter|2|
        return failFemSterNum;
    }
    //</editor-fold>//GEN-END:|78-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deathMaleSterNum ">//GEN-BEGIN:|80-getter|0|80-preInit
    /**
     * Returns an initiliazed instance of deathMaleSterNum component.
     * @return the initialized component instance
     */
    public TextField getDeathMaleSterNum() {
        if (deathMaleSterNum == null) {//GEN-END:|80-getter|0|80-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(48) != null) {
                        str = new String(lastMsgStore.getRecord(48));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            deathMaleSterNum = new TextField("Death After Male Sterlization", str, 32, TextField.NUMERIC);//GEN-LINE:|80-getter|1|80-postInit
            // write post-init user code here
        }//GEN-BEGIN:|80-getter|2|
        return deathMaleSterNum;
    }
    //</editor-fold>//GEN-END:|80-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deathFemSterNum ">//GEN-BEGIN:|81-getter|0|81-preInit
    /**
     * Returns an initiliazed instance of deathFemSterNum component.
     * @return the initialized component instance
     */
    public TextField getDeathFemSterNum() {
        if (deathFemSterNum == null) {//GEN-END:|81-getter|0|81-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(49) != null) {
                        str = new String(lastMsgStore.getRecord(49));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            deathFemSterNum = new TextField("Death After Female Sterlization", str, 32, TextField.NUMERIC);//GEN-LINE:|81-getter|1|81-postInit
            // write post-init user code here
        }//GEN-BEGIN:|81-getter|2|
        return deathFemSterNum;
    }
    //</editor-fold>//GEN-END:|81-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImmPage1 ">//GEN-BEGIN:|88-getter|0|88-preInit
    /**
     * Returns an initiliazed instance of childImmPage1 component.
     * @return the initialized component instance
     */
    public Form getChildImmPage1() {
        if (childImmPage1 == null) {//GEN-END:|88-getter|0|88-preInit
            // write pre-init user code here
            childImmPage1 = new Form("Child Immunization Form (0-11 months)", new Item[] { getChildImm1Label(), getBcgNum(), getDpt1Num(), getDpt2Num(), getDpt3Num(), getOpv0Num(), getOpv1Num(), getOpv2Num(), getOpv3Num(), getHepB1Num(), getHepB2Num(), getHepB3Num(), getMeaslesVacNum(), getFullImmMaleNum(), getFullImmFemNum() });//GEN-BEGIN:|88-getter|1|88-postInit
            childImmPage1.addCommand(getChildImm1Cmd());
            childImmPage1.addCommand(getChildImm1BackCmd());
            childImmPage1.setCommandListener(this);//GEN-END:|88-getter|1|88-postInit
            // write post-init user code here
        }//GEN-BEGIN:|88-getter|2|
        return childImmPage1;
    }
    //</editor-fold>//GEN-END:|88-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: familyCmd ">//GEN-BEGIN:|83-getter|0|83-preInit
    /**
     * Returns an initiliazed instance of familyCmd component.
     * @return the initialized component instance
     */
    public Command getFamilyCmd() {
        if (familyCmd == null) {//GEN-END:|83-getter|0|83-preInit
            // write pre-init user code here
            familyCmd = new Command("Next", Command.OK, 0);//GEN-LINE:|83-getter|1|83-postInit
            // write post-init user code here
        }//GEN-BEGIN:|83-getter|2|
        return familyCmd;
    }
    //</editor-fold>//GEN-END:|83-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: familyBackCmd ">//GEN-BEGIN:|85-getter|0|85-preInit
    /**
     * Returns an initiliazed instance of familyBackCmd component.
     * @return the initialized component instance
     */
    public Command getFamilyBackCmd() {
        if (familyBackCmd == null) {//GEN-END:|85-getter|0|85-preInit
            // write pre-init user code here
            familyBackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|85-getter|1|85-postInit
            // write post-init user code here
        }//GEN-BEGIN:|85-getter|2|
        return familyBackCmd;
    }
    //</editor-fold>//GEN-END:|85-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm1Label ">//GEN-BEGIN:|89-getter|0|89-preInit
    /**
     * Returns an initiliazed instance of childImm1Label component.
     * @return the initialized component instance
     */
    public StringItem getChildImm1Label() {
        if (childImm1Label == null) {//GEN-END:|89-getter|0|89-preInit
            // write pre-init user code here
            childImm1Label = new StringItem("", "NO. OF INFANTS 0-11 MONTHS WHO RECEIVED THE FOLLOWING", Item.PLAIN);//GEN-BEGIN:|89-getter|1|89-postInit
            childImm1Label.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_SHRINK | Item.LAYOUT_2);
            childImm1Label.setFont(getFont());//GEN-END:|89-getter|1|89-postInit
            // write post-init user code here
        }//GEN-BEGIN:|89-getter|2|
        return childImm1Label;
    }
    //</editor-fold>//GEN-END:|89-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: bcgNum ">//GEN-BEGIN:|91-getter|0|91-preInit
    /**
     * Returns an initiliazed instance of bcgNum component.
     * @return the initialized component instance
     */
    public TextField getBcgNum() {
        if (bcgNum == null) {//GEN-END:|91-getter|0|91-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(50) != null) {
                        str = new String(lastMsgStore.getRecord(50));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            bcgNum = new TextField("BCG", str, 32, TextField.NUMERIC);//GEN-LINE:|91-getter|1|91-postInit
            // write post-init user code here
        }//GEN-BEGIN:|91-getter|2|
        return bcgNum;
    }
    //</editor-fold>//GEN-END:|91-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dpt1Num ">//GEN-BEGIN:|92-getter|0|92-preInit
    /**
     * Returns an initiliazed instance of dpt1Num component.
     * @return the initialized component instance
     */
    public TextField getDpt1Num() {
        if (dpt1Num == null) {//GEN-END:|92-getter|0|92-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(51) != null) {
                        str = new String(lastMsgStore.getRecord(51));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            dpt1Num = new TextField("DPT 1", str, 32, TextField.NUMERIC);//GEN-LINE:|92-getter|1|92-postInit
            // write post-init user code here
        }//GEN-BEGIN:|92-getter|2|
        return dpt1Num;
    }
    //</editor-fold>//GEN-END:|92-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dpt2Num ">//GEN-BEGIN:|93-getter|0|93-preInit
    /**
     * Returns an initiliazed instance of dpt2Num component.
     * @return the initialized component instance
     */
    public TextField getDpt2Num() {
        if (dpt2Num == null) {//GEN-END:|93-getter|0|93-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(52) != null) {
                        str = new String(lastMsgStore.getRecord(52));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            dpt2Num = new TextField("DPT 2", str, 32, TextField.NUMERIC);//GEN-LINE:|93-getter|1|93-postInit
            // write post-init user code here
        }//GEN-BEGIN:|93-getter|2|
        return dpt2Num;
    }
    //</editor-fold>//GEN-END:|93-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dpt3Num ">//GEN-BEGIN:|94-getter|0|94-preInit
    /**
     * Returns an initiliazed instance of dpt3Num component.
     * @return the initialized component instance
     */
    public TextField getDpt3Num() {
        if (dpt3Num == null) {//GEN-END:|94-getter|0|94-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(53) != null) {
                        str = new String(lastMsgStore.getRecord(53));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            dpt3Num = new TextField("DPT 3", str, 32, TextField.NUMERIC);//GEN-LINE:|94-getter|1|94-postInit
            // write post-init user code here
        }//GEN-BEGIN:|94-getter|2|
        return dpt3Num;
    }
    //</editor-fold>//GEN-END:|94-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: opv0Num ">//GEN-BEGIN:|95-getter|0|95-preInit
    /**
     * Returns an initiliazed instance of opv0Num component.
     * @return the initialized component instance
     */
    public TextField getOpv0Num() {
        if (opv0Num == null) {//GEN-END:|95-getter|0|95-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(54) != null) {
                        str = new String(lastMsgStore.getRecord(54));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            opv0Num = new TextField("OPV 0 (Birth dose)", str, 32, TextField.NUMERIC);//GEN-LINE:|95-getter|1|95-postInit
            // write post-init user code here
        }//GEN-BEGIN:|95-getter|2|
        return opv0Num;
    }
    //</editor-fold>//GEN-END:|95-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: opv1Num ">//GEN-BEGIN:|96-getter|0|96-preInit
    /**
     * Returns an initiliazed instance of opv1Num component.
     * @return the initialized component instance
     */
    public TextField getOpv1Num() {
        if (opv1Num == null) {//GEN-END:|96-getter|0|96-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(55) != null) {
                        str = new String(lastMsgStore.getRecord(55));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            opv1Num = new TextField("OPV 1", str, 32, TextField.NUMERIC);//GEN-LINE:|96-getter|1|96-postInit
            // write post-init user code here
        }//GEN-BEGIN:|96-getter|2|
        return opv1Num;
    }
    //</editor-fold>//GEN-END:|96-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: opv2Num ">//GEN-BEGIN:|97-getter|0|97-preInit
    /**
     * Returns an initiliazed instance of opv2Num component.
     * @return the initialized component instance
     */
    public TextField getOpv2Num() {
        if (opv2Num == null) {//GEN-END:|97-getter|0|97-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(56) != null) {
                        str = new String(lastMsgStore.getRecord(56));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            opv2Num = new TextField("OPV 2", str, 32, TextField.NUMERIC);//GEN-LINE:|97-getter|1|97-postInit
            // write post-init user code here
        }//GEN-BEGIN:|97-getter|2|
        return opv2Num;
    }
    //</editor-fold>//GEN-END:|97-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: opv3Num ">//GEN-BEGIN:|98-getter|0|98-preInit
    /**
     * Returns an initiliazed instance of opv3Num component.
     * @return the initialized component instance
     */
    public TextField getOpv3Num() {
        if (opv3Num == null) {//GEN-END:|98-getter|0|98-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(57) != null) {
                        str = new String(lastMsgStore.getRecord(57));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            opv3Num = new TextField("OPV 3", str, 32, TextField.NUMERIC);//GEN-LINE:|98-getter|1|98-postInit
            // write post-init user code here
        }//GEN-BEGIN:|98-getter|2|
        return opv3Num;
    }
    //</editor-fold>//GEN-END:|98-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: hepB1Num ">//GEN-BEGIN:|99-getter|0|99-preInit
    /**
     * Returns an initiliazed instance of hepB1Num component.
     * @return the initialized component instance
     */
    public TextField getHepB1Num() {
        if (hepB1Num == null) {//GEN-END:|99-getter|0|99-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(58) != null) {
                        str = new String(lastMsgStore.getRecord(58));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            hepB1Num = new TextField("Hepatitis - B1", str, 32, TextField.NUMERIC);//GEN-LINE:|99-getter|1|99-postInit
            // write post-init user code here
        }//GEN-BEGIN:|99-getter|2|
        return hepB1Num;
    }
    //</editor-fold>//GEN-END:|99-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: hepB2Num ">//GEN-BEGIN:|100-getter|0|100-preInit
    /**
     * Returns an initiliazed instance of hepB2Num component.
     * @return the initialized component instance
     */
    public TextField getHepB2Num() {
        if (hepB2Num == null) {//GEN-END:|100-getter|0|100-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(59) != null) {
                        str = new String(lastMsgStore.getRecord(59));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            hepB2Num = new TextField("Hepatitis - B2", str, 32, TextField.NUMERIC);//GEN-LINE:|100-getter|1|100-postInit
            // write post-init user code here
        }//GEN-BEGIN:|100-getter|2|
        return hepB2Num;
    }
    //</editor-fold>//GEN-END:|100-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: hepB3Num ">//GEN-BEGIN:|101-getter|0|101-preInit
    /**
     * Returns an initiliazed instance of hepB3Num component.
     * @return the initialized component instance
     */
    public TextField getHepB3Num() {
        if (hepB3Num == null) {//GEN-END:|101-getter|0|101-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(60) != null) {
                        str = new String(lastMsgStore.getRecord(60));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            hepB3Num = new TextField("Hepatitis - B3", str, 32, TextField.NUMERIC);//GEN-LINE:|101-getter|1|101-postInit
            // write post-init user code here
        }//GEN-BEGIN:|101-getter|2|
        return hepB3Num;
    }
    //</editor-fold>//GEN-END:|101-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: measlesVacNum ">//GEN-BEGIN:|102-getter|0|102-preInit
    /**
     * Returns an initiliazed instance of measlesVacNum component.
     * @return the initialized component instance
     */
    public TextField getMeaslesVacNum() {
        if (measlesVacNum == null) {//GEN-END:|102-getter|0|102-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(61) != null) {
                        str = new String(lastMsgStore.getRecord(61));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            measlesVacNum = new TextField("Measles", str, 32, TextField.NUMERIC);//GEN-LINE:|102-getter|1|102-postInit
            // write post-init user code here
        }//GEN-BEGIN:|102-getter|2|
        return measlesVacNum;
    }
    //</editor-fold>//GEN-END:|102-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: fullImmMaleNum ">//GEN-BEGIN:|103-getter|0|103-preInit
    /**
     * Returns an initiliazed instance of fullImmMaleNum component.
     * @return the initialized component instance
     */
    public TextField getFullImmMaleNum() {
        if (fullImmMaleNum == null) {//GEN-END:|103-getter|0|103-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(62) != null) {
                        str = new String(lastMsgStore.getRecord(62));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            fullImmMaleNum = new TextField("Males (9-11 months) Fully Immunized (BCG+DPT123+OPV123+Measles)", str, 32, TextField.NUMERIC);//GEN-BEGIN:|103-getter|1|103-postInit
            fullImmMaleNum.setLayout(ImageItem.LAYOUT_DEFAULT | Item.LAYOUT_2);//GEN-END:|103-getter|1|103-postInit
            // write post-init user code here
        }//GEN-BEGIN:|103-getter|2|
        return fullImmMaleNum;
    }
    //</editor-fold>//GEN-END:|103-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: fullImmFemNum ">//GEN-BEGIN:|104-getter|0|104-preInit
    /**
     * Returns an initiliazed instance of fullImmFemNum component.
     * @return the initialized component instance
     */
    public TextField getFullImmFemNum() {
        if (fullImmFemNum == null) {//GEN-END:|104-getter|0|104-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(63) != null) {
                        str = new String(lastMsgStore.getRecord(63));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }
            }
            fullImmFemNum = new TextField("Females (9-11 months) Fully Immunized (BCG+DPT123+OPV123+Measles)", str, 32, TextField.NUMERIC);//GEN-BEGIN:|104-getter|1|104-postInit
            fullImmFemNum.setLayout(ImageItem.LAYOUT_DEFAULT | Item.LAYOUT_2);//GEN-END:|104-getter|1|104-postInit
            // write post-init user code here
        }//GEN-BEGIN:|104-getter|2|
        return fullImmFemNum;
    }
    //</editor-fold>//GEN-END:|104-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImmPage2 ">//GEN-BEGIN:|110-getter|0|110-preInit
    /**
     * Returns an initiliazed instance of childImmPage2 component.
     * @return the initialized component instance
     */
    public Form getChildImmPage2() {
        if (childImmPage2 == null) {//GEN-END:|110-getter|0|110-preInit
            // write pre-init user code here
            childImmPage2 = new Form("Child Immunization (over 16 months)", new Item[] { getDptBNum(), getOpvBNum(), getMmrNum(), getFullImm2MaleNum(), getFullImm2FemNum(), getDt5Num(), getTt10Num(), getTt16Num() });//GEN-BEGIN:|110-getter|1|110-postInit
            childImmPage2.addCommand(getChildImm2Cmd());
            childImmPage2.addCommand(getChildImm2BackCmd());
            childImmPage2.setCommandListener(this);//GEN-END:|110-getter|1|110-postInit
            // write post-init user code here
        }//GEN-BEGIN:|110-getter|2|
        return childImmPage2;
    }
    //</editor-fold>//GEN-END:|110-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm1Cmd ">//GEN-BEGIN:|106-getter|0|106-preInit
    /**
     * Returns an initiliazed instance of childImm1Cmd component.
     * @return the initialized component instance
     */
    public Command getChildImm1Cmd() {
        if (childImm1Cmd == null) {//GEN-END:|106-getter|0|106-preInit
            // write pre-init user code here
            childImm1Cmd = new Command("Next", Command.OK, 0);//GEN-LINE:|106-getter|1|106-postInit
            // write post-init user code here
        }//GEN-BEGIN:|106-getter|2|
        return childImm1Cmd;
    }
    //</editor-fold>//GEN-END:|106-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm1BackCmd ">//GEN-BEGIN:|108-getter|0|108-preInit
    /**
     * Returns an initiliazed instance of childImm1BackCmd component.
     * @return the initialized component instance
     */
    public Command getChildImm1BackCmd() {
        if (childImm1BackCmd == null) {//GEN-END:|108-getter|0|108-preInit
            // write pre-init user code here
            childImm1BackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|108-getter|1|108-postInit
            // write post-init user code here
        }//GEN-BEGIN:|108-getter|2|
        return childImm1BackCmd;
    }
    //</editor-fold>//GEN-END:|108-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dptBNum ">//GEN-BEGIN:|112-getter|0|112-preInit
    /**
     * Returns an initiliazed instance of dptBNum component.
     * @return the initialized component instance
     */
    public TextField getDptBNum() {
        if (dptBNum == null) {//GEN-END:|112-getter|0|112-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(64) != null) {
                        str = new String(lastMsgStore.getRecord(64));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            dptBNum = new TextField("DPT Booster", str, 32, TextField.NUMERIC);//GEN-LINE:|112-getter|1|112-postInit
            // write post-init user code here
        }//GEN-BEGIN:|112-getter|2|
        return dptBNum;
    }
    //</editor-fold>//GEN-END:|112-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: opvBNum ">//GEN-BEGIN:|113-getter|0|113-preInit
    /**
     * Returns an initiliazed instance of opvBNum component.
     * @return the initialized component instance
     */
    public TextField getOpvBNum() {
        if (opvBNum == null) {//GEN-END:|113-getter|0|113-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(65) != null) {
                        str = new String(lastMsgStore.getRecord(65));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            opvBNum = new TextField("OPV Booster", str, 32, TextField.NUMERIC);//GEN-LINE:|113-getter|1|113-postInit
            // write post-init user code here
        }//GEN-BEGIN:|113-getter|2|
        return opvBNum;
    }
    //</editor-fold>//GEN-END:|113-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: mmrNum ">//GEN-BEGIN:|114-getter|0|114-preInit
    /**
     * Returns an initiliazed instance of mmrNum component.
     * @return the initialized component instance
     */
    public TextField getMmrNum() {
        if (mmrNum == null) {//GEN-END:|114-getter|0|114-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(66) != null) {
                        str = new String(lastMsgStore.getRecord(66));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            mmrNum = new TextField("Measles, Mumps, Rubella (MMR)", str, 32, TextField.NUMERIC);//GEN-LINE:|114-getter|1|114-postInit
            // write post-init user code here
        }//GEN-BEGIN:|114-getter|2|
        return mmrNum;
    }
    //</editor-fold>//GEN-END:|114-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: fullImm2MaleNum ">//GEN-BEGIN:|116-getter|0|116-preInit
    /**
     * Returns an initiliazed instance of fullImm2MaleNum component.
     * @return the initialized component instance
     */
    public TextField getFullImm2MaleNum() {
        if (fullImm2MaleNum == null) {//GEN-END:|116-getter|0|116-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(67) != null) {
                        str = new String(lastMsgStore.getRecord(67));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            fullImm2MaleNum = new TextField("Males (12-23 months) Fully Immunized (BCG+DPT123+OPV123+Measles)", str, 32, TextField.NUMERIC);//GEN-LINE:|116-getter|1|116-postInit
            // write post-init user code here
        }//GEN-BEGIN:|116-getter|2|
        return fullImm2MaleNum;
    }
    //</editor-fold>//GEN-END:|116-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: fullImm2FemNum ">//GEN-BEGIN:|117-getter|0|117-preInit
    /**
     * Returns an initiliazed instance of fullImm2FemNum component.
     * @return the initialized component instance
     */
    public TextField getFullImm2FemNum() {
        if (fullImm2FemNum == null) {//GEN-END:|117-getter|0|117-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(68) != null) {
                        str = new String(lastMsgStore.getRecord(68));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            fullImm2FemNum = new TextField("Females (12-23 months) Fully Immunized (BCG+DPT123+OPV123+Measles)", str, 32, TextField.NUMERIC);//GEN-LINE:|117-getter|1|117-postInit
            // write post-init user code here
        }//GEN-BEGIN:|117-getter|2|
        return fullImm2FemNum;
    }
    //</editor-fold>//GEN-END:|117-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dt5Num ">//GEN-BEGIN:|119-getter|0|119-preInit
    /**
     * Returns an initiliazed instance of dt5Num component.
     * @return the initialized component instance
     */
    public TextField getDt5Num() {
        if (dt5Num == null) {//GEN-END:|119-getter|0|119-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(69) != null) {
                        str = new String(lastMsgStore.getRecord(69));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            dt5Num = new TextField("Children > 5 yrs Given DT5", str, 32, TextField.NUMERIC);//GEN-LINE:|119-getter|1|119-postInit
            // write post-init user code here
        }//GEN-BEGIN:|119-getter|2|
        return dt5Num;
    }
    //</editor-fold>//GEN-END:|119-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: tt10Num ">//GEN-BEGIN:|120-getter|0|120-preInit
    /**
     * Returns an initiliazed instance of tt10Num component.
     * @return the initialized component instance
     */
    public TextField getTt10Num() {
        if (tt10Num == null) {//GEN-END:|120-getter|0|120-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(70) != null) {
                        str = new String(lastMsgStore.getRecord(70));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            tt10Num = new TextField("Children > 10 yrs Given TT10", str, 32, TextField.NUMERIC);//GEN-LINE:|120-getter|1|120-postInit
            // write post-init user code here
        }//GEN-BEGIN:|120-getter|2|
        return tt10Num;
    }
    //</editor-fold>//GEN-END:|120-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: tt16Num ">//GEN-BEGIN:|121-getter|0|121-preInit
    /**
     * Returns an initiliazed instance of tt16Num component.
     * @return the initialized component instance
     */
    public TextField getTt16Num() {
        if (tt16Num == null) {//GEN-END:|121-getter|0|121-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(71) != null) {
                        str = new String(lastMsgStore.getRecord(71));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            tt16Num = new TextField("Children > 16 yrs Given TT16", str, 32, TextField.NUMERIC);//GEN-LINE:|121-getter|1|121-postInit
            // write post-init user code here
        }//GEN-BEGIN:|121-getter|2|
        return tt16Num;
    }
    //</editor-fold>//GEN-END:|121-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImmPage3 ">//GEN-BEGIN:|127-getter|0|127-preInit
    /**
     * Returns an initiliazed instance of childImmPage3 component.
     * @return the initialized component instance
     */
    public Form getChildImmPage3() {
        if (childImmPage3 == null) {//GEN-END:|127-getter|0|127-preInit
            // write pre-init user code here
            childImmPage3 = new Form("Adverse Event Following Immunization", new Item[] { getAefiAbscessNum(), getAefiDeathsNum(), getAefiOthersNum(), getStringItem(), getSessionPlannedNum(), getSessionHeldNum(), getSessionsAshaNum() });//GEN-BEGIN:|127-getter|1|127-postInit
            childImmPage3.addCommand(getChildImm3Cmd());
            childImmPage3.addCommand(getChildImm3BackCmd());
            childImmPage3.setCommandListener(this);//GEN-END:|127-getter|1|127-postInit
            // write post-init user code here
        }//GEN-BEGIN:|127-getter|2|
        return childImmPage3;
    }
    //</editor-fold>//GEN-END:|127-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: aefiAbscessNum ">//GEN-BEGIN:|134-getter|0|134-preInit
    /**
     * Returns an initiliazed instance of aefiAbscessNum component.
     * @return the initialized component instance
     */
    public TextField getAefiAbscessNum() {
        if (aefiAbscessNum == null) {//GEN-END:|134-getter|0|134-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(72) != null) {
                        str = new String(lastMsgStore.getRecord(72));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            aefiAbscessNum = new TextField("Abscess", str, 32, TextField.NUMERIC);//GEN-LINE:|134-getter|1|134-postInit
            // write post-init user code here
        }//GEN-BEGIN:|134-getter|2|
        return aefiAbscessNum;
    }
    //</editor-fold>//GEN-END:|134-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: aefiDeathsNum ">//GEN-BEGIN:|135-getter|0|135-preInit
    /**
     * Returns an initiliazed instance of aefiDeathsNum component.
     * @return the initialized component instance
     */
    public TextField getAefiDeathsNum() {
        if (aefiDeathsNum == null) {//GEN-END:|135-getter|0|135-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(73) != null) {
                        str = new String(lastMsgStore.getRecord(73));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            aefiDeathsNum = new TextField("Deaths", str, 32, TextField.NUMERIC);//GEN-LINE:|135-getter|1|135-postInit
            // write post-init user code here
        }//GEN-BEGIN:|135-getter|2|
        return aefiDeathsNum;
    }
    //</editor-fold>//GEN-END:|135-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: aefiOthersNum ">//GEN-BEGIN:|136-getter|0|136-preInit
    /**
     * Returns an initiliazed instance of aefiOthersNum component.
     * @return the initialized component instance
     */
    public TextField getAefiOthersNum() {
        if (aefiOthersNum == null) {//GEN-END:|136-getter|0|136-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(74) != null) {
                        str = new String(lastMsgStore.getRecord(74));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            aefiOthersNum = new TextField("Others", str, 32, TextField.NUMERIC);//GEN-LINE:|136-getter|1|136-postInit
            // write post-init user code here
        }//GEN-BEGIN:|136-getter|2|
        return aefiOthersNum;
    }
    //</editor-fold>//GEN-END:|136-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem ">//GEN-BEGIN:|138-getter|0|138-preInit
    /**
     * Returns an initiliazed instance of stringItem component.
     * @return the initialized component instance
     */
    public StringItem getStringItem() {
        if (stringItem == null) {//GEN-END:|138-getter|0|138-preInit
            // write pre-init user code here
            stringItem = new StringItem("", "IMMUNIZATION SESSIONS");//GEN-BEGIN:|138-getter|1|138-postInit
            stringItem.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_2);
            stringItem.setFont(getFont());//GEN-END:|138-getter|1|138-postInit
            // write post-init user code here
        }//GEN-BEGIN:|138-getter|2|
        return stringItem;
    }
    //</editor-fold>//GEN-END:|138-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sessionPlannedNum ">//GEN-BEGIN:|139-getter|0|139-preInit
    /**
     * Returns an initiliazed instance of sessionPlannedNum component.
     * @return the initialized component instance
     */
    public TextField getSessionPlannedNum() {
        if (sessionPlannedNum == null) {//GEN-END:|139-getter|0|139-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(75) != null) {
                        str = new String(lastMsgStore.getRecord(75));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            sessionPlannedNum = new TextField("Sessions Planned", str, 32, TextField.NUMERIC);//GEN-LINE:|139-getter|1|139-postInit
            // write post-init user code here
        }//GEN-BEGIN:|139-getter|2|
        return sessionPlannedNum;
    }
    //</editor-fold>//GEN-END:|139-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sessionHeldNum ">//GEN-BEGIN:|140-getter|0|140-preInit
    /**
     * Returns an initiliazed instance of sessionHeldNum component.
     * @return the initialized component instance
     */
    public TextField getSessionHeldNum() {
        if (sessionHeldNum == null) {//GEN-END:|140-getter|0|140-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(76) != null) {
                        str = new String(lastMsgStore.getRecord(76));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            sessionHeldNum = new TextField("Sessions Held", str, 32, TextField.NUMERIC);//GEN-LINE:|140-getter|1|140-postInit
            // write post-init user code here
        }//GEN-BEGIN:|140-getter|2|
        return sessionHeldNum;
    }
    //</editor-fold>//GEN-END:|140-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sessionsAshaNum ">//GEN-BEGIN:|141-getter|0|141-preInit
    /**
     * Returns an initiliazed instance of sessionsAshaNum component.
     * @return the initialized component instance
     */
    public TextField getSessionsAshaNum() {
        if (sessionsAshaNum == null) {//GEN-END:|141-getter|0|141-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(77) != null) {
                        str = new String(lastMsgStore.getRecord(77));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            sessionsAshaNum = new TextField("Sessions Where ASHA Present", str, 32, TextField.NUMERIC);//GEN-LINE:|141-getter|1|141-postInit
            // write post-init user code here
        }//GEN-BEGIN:|141-getter|2|
        return sessionsAshaNum;
    }
    //</editor-fold>//GEN-END:|141-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm2Cmd ">//GEN-BEGIN:|122-getter|0|122-preInit
    /**
     * Returns an initiliazed instance of childImm2Cmd component.
     * @return the initialized component instance
     */
    public Command getChildImm2Cmd() {
        if (childImm2Cmd == null) {//GEN-END:|122-getter|0|122-preInit
            // write pre-init user code here
            childImm2Cmd = new Command("Next", Command.OK, 0);//GEN-LINE:|122-getter|1|122-postInit
            // write post-init user code here
        }//GEN-BEGIN:|122-getter|2|
        return childImm2Cmd;
    }
    //</editor-fold>//GEN-END:|122-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm2BackCmd ">//GEN-BEGIN:|124-getter|0|124-preInit
    /**
     * Returns an initiliazed instance of childImm2BackCmd component.
     * @return the initialized component instance
     */
    public Command getChildImm2BackCmd() {
        if (childImm2BackCmd == null) {//GEN-END:|124-getter|0|124-preInit
            // write pre-init user code here
            childImm2BackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|124-getter|1|124-postInit
            // write post-init user code here
        }//GEN-BEGIN:|124-getter|2|
        return childImm2BackCmd;
    }
    //</editor-fold>//GEN-END:|124-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm3Cmd ">//GEN-BEGIN:|128-getter|0|128-preInit
    /**
     * Returns an initiliazed instance of childImm3Cmd component.
     * @return the initialized component instance
     */
    public Command getChildImm3Cmd() {
        if (childImm3Cmd == null) {//GEN-END:|128-getter|0|128-preInit
            // write pre-init user code here
            childImm3Cmd = new Command("Next", Command.OK, 0);//GEN-LINE:|128-getter|1|128-postInit
            // write post-init user code here
        }//GEN-BEGIN:|128-getter|2|
        return childImm3Cmd;
    }
    //</editor-fold>//GEN-END:|128-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm3BackCmd ">//GEN-BEGIN:|130-getter|0|130-preInit
    /**
     * Returns an initiliazed instance of childImm3BackCmd component.
     * @return the initialized component instance
     */
    public Command getChildImm3BackCmd() {
        if (childImm3BackCmd == null) {//GEN-END:|130-getter|0|130-preInit
            // write pre-init user code here
            childImm3BackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|130-getter|1|130-postInit
            // write post-init user code here
        }//GEN-BEGIN:|130-getter|2|
        return childImm3BackCmd;
    }
    //</editor-fold>//GEN-END:|130-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImmPage4 ">//GEN-BEGIN:|142-getter|0|142-preInit
    /**
     * Returns an initiliazed instance of childImmPage4 component.
     * @return the initialized component instance
     */
    public Form getChildImmPage4() {
        if (childImmPage4 == null) {//GEN-END:|142-getter|0|142-preInit
            // write pre-init user code here
            childImmPage4 = new Form("Vitamin A Doses (9 months - 5 yrs)", new Item[] { getVitA1Num(), getVitA5Num(), getVitA9Num(), getChildDiseaseLabel(), getCaseMeaslesNum(), getCaseDiarrhNum(), getCaseMalariaNum() });//GEN-BEGIN:|142-getter|1|142-postInit
            childImmPage4.addCommand(getChildImm4Cmd());
            childImmPage4.addCommand(getChildImm4BackCmd());
            childImmPage4.setCommandListener(this);//GEN-END:|142-getter|1|142-postInit
            // write post-init user code here
        }//GEN-BEGIN:|142-getter|2|
        return childImmPage4;
    }
    //</editor-fold>//GEN-END:|142-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: vitA1Num ">//GEN-BEGIN:|149-getter|0|149-preInit
    /**
     * Returns an initiliazed instance of vitA1Num component.
     * @return the initialized component instance
     */
    public TextField getVitA1Num() {
        if (vitA1Num == null) {//GEN-END:|149-getter|0|149-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(78) != null) {
                        str = new String(lastMsgStore.getRecord(78));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            vitA1Num = new TextField("Dose 1", str, 32, TextField.NUMERIC);//GEN-LINE:|149-getter|1|149-postInit
            // write post-init user code here
        }//GEN-BEGIN:|149-getter|2|
        return vitA1Num;
    }
    //</editor-fold>//GEN-END:|149-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: vitA5Num ">//GEN-BEGIN:|150-getter|0|150-preInit
    /**
     * Returns an initiliazed instance of vitA5Num component.
     * @return the initialized component instance
     */
    public TextField getVitA5Num() {
        if (vitA5Num == null) {//GEN-END:|150-getter|0|150-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(79) != null) {
                        str = new String(lastMsgStore.getRecord(79));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            vitA5Num = new TextField("Dose 5", str, 32, TextField.NUMERIC);//GEN-LINE:|150-getter|1|150-postInit
            // write post-init user code here
        }//GEN-BEGIN:|150-getter|2|
        return vitA5Num;
    }
    //</editor-fold>//GEN-END:|150-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: vitA9Num ">//GEN-BEGIN:|151-getter|0|151-preInit
    /**
     * Returns an initiliazed instance of vitA9Num component.
     * @return the initialized component instance
     */
    public TextField getVitA9Num() {
        if (vitA9Num == null) {//GEN-END:|151-getter|0|151-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(80) != null) {
                        str = new String(lastMsgStore.getRecord(80));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            vitA9Num = new TextField("Dose 9", str, 32, TextField.NUMERIC);//GEN-LINE:|151-getter|1|151-postInit
            // write post-init user code here
        }//GEN-BEGIN:|151-getter|2|
        return vitA9Num;
    }
    //</editor-fold>//GEN-END:|151-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childDiseaseLabel ">//GEN-BEGIN:|152-getter|0|152-preInit
    /**
     * Returns an initiliazed instance of childDiseaseLabel component.
     * @return the initialized component instance
     */
    public StringItem getChildDiseaseLabel() {
        if (childDiseaseLabel == null) {//GEN-END:|152-getter|0|152-preInit
            // write pre-init user code here
            childDiseaseLabel = new StringItem("", "CASES OF CHILDHOOD DISEASES (0-5yrs)");//GEN-BEGIN:|152-getter|1|152-postInit
            childDiseaseLabel.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_2);
            childDiseaseLabel.setFont(getFont());//GEN-END:|152-getter|1|152-postInit
            // write post-init user code here
        }//GEN-BEGIN:|152-getter|2|
        return childDiseaseLabel;
    }
    //</editor-fold>//GEN-END:|152-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: caseMeaslesNum ">//GEN-BEGIN:|153-getter|0|153-preInit
    /**
     * Returns an initiliazed instance of caseMeaslesNum component.
     * @return the initialized component instance
     */
    public TextField getCaseMeaslesNum() {
        if (caseMeaslesNum == null) {//GEN-END:|153-getter|0|153-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(81) != null) {
                        str = new String(lastMsgStore.getRecord(81));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            caseMeaslesNum = new TextField("Measles Cases", str, 32, TextField.NUMERIC);//GEN-LINE:|153-getter|1|153-postInit
            // write post-init user code here
        }//GEN-BEGIN:|153-getter|2|
        return caseMeaslesNum;
    }
    //</editor-fold>//GEN-END:|153-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: caseDiarrhNum ">//GEN-BEGIN:|154-getter|0|154-preInit
    /**
     * Returns an initiliazed instance of caseDiarrhNum component.
     * @return the initialized component instance
     */
    public TextField getCaseDiarrhNum() {
        if (caseDiarrhNum == null) {//GEN-END:|154-getter|0|154-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(82) != null) {
                        str = new String(lastMsgStore.getRecord(82));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            caseDiarrhNum = new TextField("Diarrhoea & Dehydration", str, 32, TextField.NUMERIC);//GEN-LINE:|154-getter|1|154-postInit
            // write post-init user code here
        }//GEN-BEGIN:|154-getter|2|
        return caseDiarrhNum;
    }
    //</editor-fold>//GEN-END:|154-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: caseMalariaNum ">//GEN-BEGIN:|155-getter|0|155-preInit
    /**
     * Returns an initiliazed instance of caseMalariaNum component.
     * @return the initialized component instance
     */
    public TextField getCaseMalariaNum() {
        if (caseMalariaNum == null) {//GEN-END:|155-getter|0|155-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(83) != null) {
                        str = new String(lastMsgStore.getRecord(83));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            caseMalariaNum = new TextField("Malaria", str, 32, TextField.NUMERIC);//GEN-LINE:|155-getter|1|155-postInit
            // write post-init user code here
        }//GEN-BEGIN:|155-getter|2|
        return caseMalariaNum;
    }
    //</editor-fold>//GEN-END:|155-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm4Cmd ">//GEN-BEGIN:|143-getter|0|143-preInit
    /**
     * Returns an initiliazed instance of childImm4Cmd component.
     * @return the initialized component instance
     */
    public Command getChildImm4Cmd() {
        if (childImm4Cmd == null) {//GEN-END:|143-getter|0|143-preInit
            // write pre-init user code here
            childImm4Cmd = new Command("Next", Command.OK, 0);//GEN-LINE:|143-getter|1|143-postInit
            // write post-init user code here
        }//GEN-BEGIN:|143-getter|2|
        return childImm4Cmd;
    }
    //</editor-fold>//GEN-END:|143-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: childImm4BackCmd ">//GEN-BEGIN:|145-getter|0|145-preInit
    /**
     * Returns an initiliazed instance of childImm4BackCmd component.
     * @return the initialized component instance
     */
    public Command getChildImm4BackCmd() {
        if (childImm4BackCmd == null) {//GEN-END:|145-getter|0|145-preInit
            // write pre-init user code here
            childImm4BackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|145-getter|1|145-postInit
            // write post-init user code here
        }//GEN-BEGIN:|145-getter|2|
        return childImm4BackCmd;
    }
    //</editor-fold>//GEN-END:|145-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: healthFacilityPage ">//GEN-BEGIN:|156-getter|0|156-preInit
    /**
     * Returns an initiliazed instance of healthFacilityPage component.
     * @return the initialized component instance
     */
    public Form getHealthFacilityPage() {
        if (healthFacilityPage == null) {//GEN-END:|156-getter|0|156-preInit
            // write pre-init user code here
            healthFacilityPage = new Form("Health Facility Services", new Item[] { getVhndNum(), getOpdNum(), getHbTestsNum(), getAnaemicOpdNum() });//GEN-BEGIN:|156-getter|1|156-postInit
            healthFacilityPage.addCommand(getHealthFacilityCmd());
            healthFacilityPage.addCommand(getHealthFacilityBackCmd());
            healthFacilityPage.setCommandListener(this);//GEN-END:|156-getter|1|156-postInit
            // write post-init user code here
        }//GEN-BEGIN:|156-getter|2|
        return healthFacilityPage;
    }
    //</editor-fold>//GEN-END:|156-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: healthFacilityCmd ">//GEN-BEGIN:|157-getter|0|157-preInit
    /**
     * Returns an initiliazed instance of healthFacilityCmd component.
     * @return the initialized component instance
     */
    public Command getHealthFacilityCmd() {
        if (healthFacilityCmd == null) {//GEN-END:|157-getter|0|157-preInit
            // write pre-init user code here
            healthFacilityCmd = new Command("Next", Command.OK, 0);//GEN-LINE:|157-getter|1|157-postInit
            // write post-init user code here
        }//GEN-BEGIN:|157-getter|2|
        return healthFacilityCmd;
    }
    //</editor-fold>//GEN-END:|157-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: healthFacilityBackCmd ">//GEN-BEGIN:|159-getter|0|159-preInit
    /**
     * Returns an initiliazed instance of healthFacilityBackCmd component.
     * @return the initialized component instance
     */
    public Command getHealthFacilityBackCmd() {
        if (healthFacilityBackCmd == null) {//GEN-END:|159-getter|0|159-preInit
            // write pre-init user code here
            healthFacilityBackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|159-getter|1|159-postInit
            // write post-init user code here
        }//GEN-BEGIN:|159-getter|2|
        return healthFacilityBackCmd;
    }
    //</editor-fold>//GEN-END:|159-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: vhndNum ">//GEN-BEGIN:|163-getter|0|163-preInit
    /**
     * Returns an initiliazed instance of vhndNum component.
     * @return the initialized component instance
     */
    public TextField getVhndNum() {
        if (vhndNum == null) {//GEN-END:|163-getter|0|163-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(84) != null) {
                        str = new String(lastMsgStore.getRecord(84));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            vhndNum = new TextField("Anganwadi Centres Reported to Have Conducted VHNDs", str, 32, TextField.NUMERIC);//GEN-LINE:|163-getter|1|163-postInit
            // write post-init user code here
        }//GEN-BEGIN:|163-getter|2|
        return vhndNum;
    }
    //</editor-fold>//GEN-END:|163-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: opdNum ">//GEN-BEGIN:|164-getter|0|164-preInit
    /**
     * Returns an initiliazed instance of opdNum component.
     * @return the initialized component instance
     */
    public TextField getOpdNum() {
        if (opdNum == null) {//GEN-END:|164-getter|0|164-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(85) != null) {
                        str = new String(lastMsgStore.getRecord(85));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            opdNum = new TextField("OPD Attendence (All)", str, 32, TextField.NUMERIC);//GEN-LINE:|164-getter|1|164-postInit
            // write post-init user code here
        }//GEN-BEGIN:|164-getter|2|
        return opdNum;
    }
    //</editor-fold>//GEN-END:|164-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: hbTestsNum ">//GEN-BEGIN:|165-getter|0|165-preInit
    /**
     * Returns an initiliazed instance of hbTestsNum component.
     * @return the initialized component instance
     */
    public TextField getHbTestsNum() {
        if (hbTestsNum == null) {//GEN-END:|165-getter|0|165-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(86) != null) {
                        str = new String(lastMsgStore.getRecord(86));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            hbTestsNum = new TextField("No. of Hb Tests Conducted", str, 32, TextField.NUMERIC);//GEN-LINE:|165-getter|1|165-postInit
            // write post-init user code here
        }//GEN-BEGIN:|165-getter|2|
        return hbTestsNum;
    }
    //</editor-fold>//GEN-END:|165-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: anaemicOpdNum ">//GEN-BEGIN:|166-getter|0|166-preInit
    /**
     * Returns an initiliazed instance of anaemicOpdNum component.
     * @return the initialized component instance
     */
    public TextField getAnaemicOpdNum() {
        if (anaemicOpdNum == null) {//GEN-END:|166-getter|0|166-preInit
            String str = "";
            if (editingLastReport) {
                try {
                    if (lastMsgStore.getRecord(87) != null) {
                        str = new String(lastMsgStore.getRecord(87));
                    }

                } catch (RecordStoreException rsex) {
                    rsex.printStackTrace();
                }

            }
            anaemicOpdNum = new TextField("No. of Hb < 7mg", str, 32, TextField.NUMERIC);//GEN-LINE:|166-getter|1|166-postInit
            // write post-init user code here
        }//GEN-BEGIN:|166-getter|2|
        return anaemicOpdNum;
    }
    //</editor-fold>//GEN-END:|166-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendPage ">//GEN-BEGIN:|167-getter|0|167-preInit
    /**
     * Returns an initiliazed instance of sendPage component.
     * @return the initialized component instance
     */
    public Form getSendPage() {
        if (sendPage == null) {//GEN-END:|167-getter|0|167-preInit
            // write pre-init user code here
            sendPage = new Form("Data Collection Complete", new Item[] { getSendMsgLabel() });//GEN-BEGIN:|167-getter|1|167-postInit
            sendPage.addCommand(getSendCmd());
            sendPage.addCommand(getSendBackCmd());
            sendPage.addCommand(getSendExitCmd());
            sendPage.addCommand(getSendSettingsCmd());
            sendPage.setCommandListener(this);//GEN-END:|167-getter|1|167-postInit

            if (sendPage.size() > 1) {
                sendPage.delete(sendPage.size() - 1);
            }

            sendPage.append(imgItem);

        }//GEN-BEGIN:|167-getter|2|
        return sendPage;
    }
    //</editor-fold>//GEN-END:|167-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendCmd ">//GEN-BEGIN:|168-getter|0|168-preInit
    /**
     * Returns an initiliazed instance of sendCmd component.
     * @return the initialized component instance
     */
    public Command getSendCmd() {
        if (sendCmd == null) {//GEN-END:|168-getter|0|168-preInit
            // write pre-init user code here
            sendCmd = new Command("Send SMS", Command.OK, 0);//GEN-LINE:|168-getter|1|168-postInit
            // write post-init user code here
        }//GEN-BEGIN:|168-getter|2|
        return sendCmd;
    }
    //</editor-fold>//GEN-END:|168-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendBackCmd ">//GEN-BEGIN:|170-getter|0|170-preInit
    /**
     * Returns an initiliazed instance of sendBackCmd component.
     * @return the initialized component instance
     */
    public Command getSendBackCmd() {
        if (sendBackCmd == null) {//GEN-END:|170-getter|0|170-preInit
            // write pre-init user code here
            sendBackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|170-getter|1|170-postInit
            // write post-init user code here
        }//GEN-BEGIN:|170-getter|2|
        return sendBackCmd;
    }
    //</editor-fold>//GEN-END:|170-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: monthPage ">//GEN-BEGIN:|178-getter|0|178-preInit
    /**
     * Returns an initiliazed instance of monthPage component.
     * @return the initialized component instance
     */
    public Form getMonthPage() {
        if (monthPage == null) {//GEN-END:|178-getter|0|178-preInit
            // write pre-init user code here
            monthPage = new Form("Monthly Report", new Item[] { getReportingChoice(), getMonthChoice(), getDateField() });//GEN-BEGIN:|178-getter|1|178-postInit
            monthPage.addCommand(getMonthCmd());
            monthPage.addCommand(getMonthExitCmd());
            monthPage.addCommand(getMonthSettingsCmd());
            monthPage.setCommandListener(this);//GEN-END:|178-getter|1|178-postInit
            monthPage.delete(2);
            monthPage.setItemStateListener(new ItemStateListener() {

                public void itemStateChanged(Item item) {
                    if (item == reportingChoice) {
                        if (reportingChoice.getSelectedIndex() == 0) {
                            monthPage.delete(1);
                            monthPage.append(monthChoice);
                        } else {
                            monthPage.delete(1);
                            monthPage.append(dateField);
                        }
                    }
                }
            });
        }//GEN-BEGIN:|178-getter|2|
        return monthPage;
    }
    //</editor-fold>//GEN-END:|178-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: monthChoice ">//GEN-BEGIN:|189-getter|0|189-preInit
    /**
     * Returns an initiliazed instance of monthChoice component.
     * @return the initialized component instance
     */
    public ChoiceGroup getMonthChoice() {
        if (monthChoice == null) {//GEN-END:|189-getter|0|189-preInit
            // write pre-init user code here
            monthChoice = new ChoiceGroup("Reporting Month", Choice.POPUP);//GEN-BEGIN:|189-getter|1|189-postInit
            monthChoice.append("Jan 2009", null);
            monthChoice.append("Feb 2009", null);
            monthChoice.append("Mar 2009", null);
            monthChoice.append("Apr 2009", null);
            monthChoice.append("May 2009", null);
            monthChoice.append("Jun 2009", null);
            monthChoice.append("Jul 2009", null);
            monthChoice.append("Aug 2009", null);
            monthChoice.append("Sep 2009", null);
            monthChoice.append("Oct 2009", null);
            monthChoice.append("Nov 2009", null);
            monthChoice.append("Dec 2009", null);
            monthChoice.append("Jan 2008", null);
            monthChoice.append("Feb 2008", null);
            monthChoice.append("Mar 2008", null);
            monthChoice.append("Apr 2008", null);
            monthChoice.append("May 2008", null);
            monthChoice.append("Jun 2008", null);
            monthChoice.append("Jul 2008", null);
            monthChoice.append("Aug 2008", null);
            monthChoice.append("Sep 2008", null);
            monthChoice.append("Oct 2008", null);
            monthChoice.append("Nov 2008", null);
            monthChoice.append("Dec 2008", null);
            monthChoice.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_TOP | Item.LAYOUT_BOTTOM | Item.LAYOUT_VCENTER | Item.LAYOUT_2);
            monthChoice.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            monthChoice.setSelectedFlags(new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false });//GEN-END:|189-getter|1|189-postInit
            try {
                if (editingLastReport) {
                    if (lastMsgStore.getRecord(10) != null) {
                        monthChoice.setSelectedIndex(Integer.parseInt(new String(lastMsgStore.getRecord(10))) - 1, true);
                    }

                }
            } catch (RecordStoreException rsex) {
                rsex.printStackTrace();
            }
        }//GEN-BEGIN:|189-getter|2|
        return monthChoice;
    }
    //</editor-fold>//GEN-END:|189-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: monthCmd ">//GEN-BEGIN:|179-getter|0|179-preInit
    /**
     * Returns an initiliazed instance of monthCmd component.
     * @return the initialized component instance
     */
    public Command getMonthCmd() {
        if (monthCmd == null) {//GEN-END:|179-getter|0|179-preInit
            // write pre-init user code here
            monthCmd = new Command("Ok", Command.OK, 0);//GEN-LINE:|179-getter|1|179-postInit
            // write post-init user code here
        }//GEN-BEGIN:|179-getter|2|
        return monthCmd;
    }
    //</editor-fold>//GEN-END:|179-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ancBackCmd ">//GEN-BEGIN:|184-getter|0|184-preInit
    /**
     * Returns an initiliazed instance of ancBackCmd component.
     * @return the initialized component instance
     */
    public Command getAncBackCmd() {
        if (ancBackCmd == null) {//GEN-END:|184-getter|0|184-preInit
            // write pre-init user code here
            ancBackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|184-getter|1|184-postInit
            // write post-init user code here
        }//GEN-BEGIN:|184-getter|2|
        return ancBackCmd;
    }
    //</editor-fold>//GEN-END:|184-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: monthExitCmd ">//GEN-BEGIN:|187-getter|0|187-preInit
    /**
     * Returns an initiliazed instance of monthExitCmd component.
     * @return the initialized component instance
     */
    public Command getMonthExitCmd() {
        if (monthExitCmd == null) {//GEN-END:|187-getter|0|187-preInit
            // write pre-init user code here
            monthExitCmd = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|187-getter|1|187-postInit
            // write post-init user code here
        }//GEN-BEGIN:|187-getter|2|
        return monthExitCmd;
    }
    //</editor-fold>//GEN-END:|187-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: splashScreen ">//GEN-BEGIN:|202-getter|0|202-preInit
    /**
     * Returns an initiliazed instance of splashScreen component.
     * @return the initialized component instance
     */
    public SplashScreen getSplashScreen() {
        if (splashScreen == null) {//GEN-END:|202-getter|0|202-preInit
            // write pre-init user code here
            splashScreen = new SplashScreen(getDisplay());//GEN-BEGIN:|202-getter|1|202-postInit
            splashScreen.setTitle("splashScreen");
            splashScreen.setCommandListener(this);
            splashScreen.setFullScreenMode(true);
            splashScreen.setImage(getNrhmlogo());
            splashScreen.setText("Mobile SCDRT");
            splashScreen.setTimeout(3000);//GEN-END:|202-getter|1|202-postInit
            // write post-init user code here
        }//GEN-BEGIN:|202-getter|2|
        return splashScreen;
    }
    //</editor-fold>//GEN-END:|202-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: nrhmlogo ">//GEN-BEGIN:|205-getter|0|205-preInit
    /**
     * Returns an initiliazed instance of nrhmlogo component.
     * @return the initialized component instance
     */
    public Image getNrhmlogo() {
        if (nrhmlogo == null) {//GEN-END:|205-getter|0|205-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|205-getter|1|205-@java.io.IOException
                nrhmlogo = Image.createImage("/org/hispindia/mobile/images/nrhm-logo.png");
            } catch (java.io.IOException e) {//GEN-END:|205-getter|1|205-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|205-getter|2|205-postInit
// write post-init user code here
        }//GEN-BEGIN:|205-getter|3|
        return nrhmlogo;
    }
    //</editor-fold>//GEN-END:|205-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendExitCmd ">//GEN-BEGIN:|206-getter|0|206-preInit
    /**
     * Returns an initiliazed instance of sendExitCmd component.
     * @return the initialized component instance
     */
    public Command getSendExitCmd() {
        if (sendExitCmd == null) {//GEN-END:|206-getter|0|206-preInit
            // write pre-init user code here
            sendExitCmd = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|206-getter|1|206-postInit
            // write post-init user code here
        }//GEN-BEGIN:|206-getter|2|
        return sendExitCmd;
    }
    //</editor-fold>//GEN-END:|206-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendMsgLabel ">//GEN-BEGIN:|210-getter|0|210-preInit
    /**
     * Returns an initiliazed instance of sendMsgLabel component.
     * @return the initialized component instance
     */
    public StringItem getSendMsgLabel() {
        if (sendMsgLabel == null) {//GEN-END:|210-getter|0|210-preInit
            // write pre-init user code here
            sendMsgLabel = new StringItem("Info:", "All Data Collection Complete. Press \"Send SMS\" to send information to server", Item.PLAIN);//GEN-BEGIN:|210-getter|1|210-postInit
            sendMsgLabel.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_TOP | Item.LAYOUT_BOTTOM | Item.LAYOUT_VCENTER | ImageItem.LAYOUT_NEWLINE_BEFORE | ImageItem.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_SHRINK | Item.LAYOUT_VSHRINK | Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND | Item.LAYOUT_2);
            sendMsgLabel.setFont(getFont());//GEN-END:|210-getter|1|210-postInit
            // write post-init user code here
        }//GEN-BEGIN:|210-getter|2|
        return sendMsgLabel;
    }
    //</editor-fold>//GEN-END:|210-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: font ">//GEN-BEGIN:|211-getter|0|211-preInit
    /**
     * Returns an initiliazed instance of font component.
     * @return the initialized component instance
     */
    public Font getFont() {
        if (font == null) {//GEN-END:|211-getter|0|211-preInit
            // write pre-init user code here
            font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);//GEN-LINE:|211-getter|1|211-postInit
            // write post-init user code here
        }//GEN-BEGIN:|211-getter|2|
        return font;
    }
    //</editor-fold>//GEN-END:|211-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: question ">//GEN-BEGIN:|213-getter|0|213-preInit
    /**
     * Returns an initiliazed instance of question component.
     * @return the initialized component instance
     */
    public Image getQuestion() {
        if (question == null) {//GEN-END:|213-getter|0|213-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|213-getter|1|213-@java.io.IOException
                question = Image.createImage("/org/hispindia/mobile/images/question.png");
            } catch (java.io.IOException e) {//GEN-END:|213-getter|1|213-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|213-getter|2|213-postInit
// write post-init user code here
        }//GEN-BEGIN:|213-getter|3|
        return question;
    }
    //</editor-fold>//GEN-END:|213-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadPage ">//GEN-BEGIN:|219-getter|0|219-preInit
    /**
     * Returns an initiliazed instance of loadPage component.
     * @return the initialized component instance
     */
    public Form getLoadPage() {
        if (loadPage == null) {//GEN-END:|219-getter|0|219-preInit
            // write pre-init user code here
            loadPage = new Form("Last Report", new Item[] { getQuestionImage(), getQuestionLabel(), getLastChoice() });//GEN-BEGIN:|219-getter|1|219-postInit
            loadPage.addCommand(getLoadCmd());
            loadPage.addCommand(getLoadExitCmd());
            loadPage.setCommandListener(this);//GEN-END:|219-getter|1|219-postInit
            // write post-init user code here
        }//GEN-BEGIN:|219-getter|2|
        return loadPage;
    }
    //</editor-fold>//GEN-END:|219-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: questionLabel ">//GEN-BEGIN:|221-getter|0|221-preInit
    /**
     * Returns an initiliazed instance of questionLabel component.
     * @return the initialized component instance
     */
    public StringItem getQuestionLabel() {
        if (questionLabel == null) {//GEN-END:|221-getter|0|221-preInit
            // write pre-init user code here
            questionLabel = new StringItem("", "Do You Want To Edit Your Last Submitted Report?");//GEN-LINE:|221-getter|1|221-postInit
            // write post-init user code here
        }//GEN-BEGIN:|221-getter|2|
        return questionLabel;
    }
    //</editor-fold>//GEN-END:|221-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: lastChoice ">//GEN-BEGIN:|222-getter|0|222-preInit
    /**
     * Returns an initiliazed instance of lastChoice component.
     * @return the initialized component instance
     */
    public ChoiceGroup getLastChoice() {
        if (lastChoice == null) {//GEN-END:|222-getter|0|222-preInit
            // write pre-init user code here
            lastChoice = new ChoiceGroup("Select", Choice.POPUP);//GEN-BEGIN:|222-getter|1|222-postInit
            lastChoice.append("No", null);
            lastChoice.append("Yes", null);
            lastChoice.setSelectedFlags(new boolean[] { false, false });//GEN-END:|222-getter|1|222-postInit
            // write post-init user code here
        }//GEN-BEGIN:|222-getter|2|
        return lastChoice;
    }
    //</editor-fold>//GEN-END:|222-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadCmd ">//GEN-BEGIN:|225-getter|0|225-preInit
    /**
     * Returns an initiliazed instance of loadCmd component.
     * @return the initialized component instance
     */
    public Command getLoadCmd() {
        if (loadCmd == null) {//GEN-END:|225-getter|0|225-preInit
            // write pre-init user code here
            loadCmd = new Command("Ok", Command.OK, 0);//GEN-LINE:|225-getter|1|225-postInit
            // write post-init user code here
        }//GEN-BEGIN:|225-getter|2|
        return loadCmd;
    }
    //</editor-fold>//GEN-END:|225-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadExitCmd ">//GEN-BEGIN:|227-getter|0|227-preInit
    /**
     * Returns an initiliazed instance of loadExitCmd component.
     * @return the initialized component instance
     */
    public Command getLoadExitCmd() {
        if (loadExitCmd == null) {//GEN-END:|227-getter|0|227-preInit
            // write pre-init user code here
            loadExitCmd = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|227-getter|1|227-postInit
            // write post-init user code here
        }//GEN-BEGIN:|227-getter|2|
        return loadExitCmd;
    }
    //</editor-fold>//GEN-END:|227-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: settingsPage ">//GEN-BEGIN:|233-getter|0|233-preInit
    /**
     * Returns an initiliazed instance of settingsPage component.
     * @return the initialized component instance
     */
    public Form getSettingsPage() {
        if (settingsPage == null) {//GEN-END:|233-getter|0|233-preInit
            // write pre-init user code here
            settingsPage = new Form("Settings", new Item[] { getPhone1Num(), getPhone2Num(), getPhone3Num() });//GEN-BEGIN:|233-getter|1|233-postInit
            settingsPage.addCommand(getSettingsCmd());
            settingsPage.addCommand(getSettingsBackCmd());
            settingsPage.setCommandListener(this);//GEN-END:|233-getter|1|233-postInit
            // write post-init user code here
        }//GEN-BEGIN:|233-getter|2|
        return settingsPage;
    }
    //</editor-fold>//GEN-END:|233-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: reportingChoice ">//GEN-BEGIN:|235-getter|0|235-preInit
    /**
     * Returns an initiliazed instance of reportingChoice component.
     * @return the initialized component instance
     */
    public ChoiceGroup getReportingChoice() {
        if (reportingChoice == null) {//GEN-END:|235-getter|0|235-preInit
            // write pre-init user code here
            reportingChoice = new ChoiceGroup("Reporting Freq:", Choice.POPUP);//GEN-BEGIN:|235-getter|1|235-postInit
            reportingChoice.append("Monthly", null);
            reportingChoice.append("Weekly", null);
            reportingChoice.append("Daily", null);
            reportingChoice.setSelectedFlags(new boolean[] { false, false, false });//GEN-END:|235-getter|1|235-postInit

        }//GEN-BEGIN:|235-getter|2|
        return reportingChoice;
    }
    //</editor-fold>//GEN-END:|235-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: questionImage ">//GEN-BEGIN:|241-getter|0|241-preInit
    /**
     * Returns an initiliazed instance of questionImage component.
     * @return the initialized component instance
     */
    public ImageItem getQuestionImage() {
        if (questionImage == null) {//GEN-END:|241-getter|0|241-preInit
            // write pre-init user code here
            questionImage = new ImageItem("", getQuestion(), ImageItem.LAYOUT_CENTER | Item.LAYOUT_2, "");//GEN-LINE:|241-getter|1|241-postInit
            // write post-init user code here
        }//GEN-BEGIN:|241-getter|2|
        return questionImage;
    }
    //</editor-fold>//GEN-END:|241-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: settingsCmd ">//GEN-BEGIN:|242-getter|0|242-preInit
    /**
     * Returns an initiliazed instance of settingsCmd component.
     * @return the initialized component instance
     */
    public Command getSettingsCmd() {
        if (settingsCmd == null) {//GEN-END:|242-getter|0|242-preInit
            // write pre-init user code here
            settingsCmd = new Command("Save", Command.OK, 0);//GEN-LINE:|242-getter|1|242-postInit
            // write post-init user code here
        }//GEN-BEGIN:|242-getter|2|
        return settingsCmd;
    }
    //</editor-fold>//GEN-END:|242-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: settingsBackCmd ">//GEN-BEGIN:|245-getter|0|245-preInit
    /**
     * Returns an initiliazed instance of settingsBackCmd component.
     * @return the initialized component instance
     */
    public Command getSettingsBackCmd() {
        if (settingsBackCmd == null) {//GEN-END:|245-getter|0|245-preInit
            // write pre-init user code here
            settingsBackCmd = new Command("Back", Command.BACK, 0);//GEN-LINE:|245-getter|1|245-postInit
            // write post-init user code here
        }//GEN-BEGIN:|245-getter|2|
        return settingsBackCmd;
    }
    //</editor-fold>//GEN-END:|245-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: phone1Num ">//GEN-BEGIN:|248-getter|0|248-preInit
    /**
     * Returns an initiliazed instance of phone1Num component.
     * @return the initialized component instance
     */
    public TextField getPhone1Num() {
        if (phone1Num == null) {//GEN-END:|248-getter|0|248-preInit
            String str = "";
            try {
                if (lastMsgStore.getRecord(1) != null) {
                    str = new String(lastMsgStore.getRecord(1));
                }

            } catch (RecordStoreException rsex) {
                rsex.printStackTrace();
            }
            phone1Num = new TextField("Enter 10-digit Phone #1:", str, 10, TextField.PHONENUMBER);//GEN-LINE:|248-getter|1|248-postInit
            // write post-init user code here
        }//GEN-BEGIN:|248-getter|2|
        return phone1Num;
    }
    //</editor-fold>//GEN-END:|248-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: phone2Num ">//GEN-BEGIN:|249-getter|0|249-preInit
    /**
     * Returns an initiliazed instance of phone2Num component.
     * @return the initialized component instance
     */
    public TextField getPhone2Num() {
        if (phone2Num == null) {//GEN-END:|249-getter|0|249-preInit
            String str = "";
            try {
                if (lastMsgStore.getRecord(2) != null) {
                    str = new String(lastMsgStore.getRecord(2));
                }

            } catch (RecordStoreException rsex) {
                rsex.printStackTrace();
            }
            phone2Num = new TextField("Enter 10-digit Phone #2:", str, 10, TextField.PHONENUMBER);//GEN-LINE:|249-getter|1|249-postInit
            // write post-init user code here
        }//GEN-BEGIN:|249-getter|2|
        return phone2Num;
    }
    //</editor-fold>//GEN-END:|249-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: phone3Num ">//GEN-BEGIN:|250-getter|0|250-preInit
    /**
     * Returns an initiliazed instance of phone3Num component.
     * @return the initialized component instance
     */
    public TextField getPhone3Num() {
        if (phone3Num == null) {//GEN-END:|250-getter|0|250-preInit
            String str = "";
            try {
                if (lastMsgStore.getRecord(3) != null) {
                    str = new String(lastMsgStore.getRecord(3));
                }

            } catch (RecordStoreException rsex) {
                rsex.printStackTrace();
            }
            phone3Num = new TextField("Enter 10-digit Phone #3:", str, 10, TextField.PHONENUMBER);//GEN-LINE:|250-getter|1|250-postInit
            // write post-init user code here
        }//GEN-BEGIN:|250-getter|2|
        return phone3Num;
    }
    //</editor-fold>//GEN-END:|250-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: monthSettingsCmd ">//GEN-BEGIN:|251-getter|0|251-preInit
    /**
     * Returns an initiliazed instance of monthSettingsCmd component.
     * @return the initialized component instance
     */
    public Command getMonthSettingsCmd() {
        if (monthSettingsCmd == null) {//GEN-END:|251-getter|0|251-preInit
            // write pre-init user code here
            monthSettingsCmd = new Command("Settings", Command.OK, 0);//GEN-LINE:|251-getter|1|251-postInit
            // write post-init user code here
        }//GEN-BEGIN:|251-getter|2|
        return monthSettingsCmd;
    }
    //</editor-fold>//GEN-END:|251-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendSettingsCmd ">//GEN-BEGIN:|254-getter|0|254-preInit
    /**
     * Returns an initiliazed instance of sendSettingsCmd component.
     * @return the initialized component instance
     */
    public Command getSendSettingsCmd() {
        if (sendSettingsCmd == null) {//GEN-END:|254-getter|0|254-preInit
            // write pre-init user code here
            sendSettingsCmd = new Command("Settings", Command.OK, 0);//GEN-LINE:|254-getter|1|254-postInit
            // write post-init user code here
        }//GEN-BEGIN:|254-getter|2|
        return sendSettingsCmd;
    }
    //</editor-fold>//GEN-END:|254-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dateField ">//GEN-BEGIN:|258-getter|0|258-preInit
    /**
     * Returns an initiliazed instance of dateField component.
     * @return the initialized component instance
     */
    public TextField getDateField() {
        if (dateField == null) {//GEN-END:|258-getter|0|258-preInit
            // write pre-init user code here
            dateField = new TextField("Enter Date (yyyy-mm-dd):", null, 32, TextField.ANY);//GEN-LINE:|258-getter|1|258-postInit
            // write post-init user code here
        }//GEN-BEGIN:|258-getter|2|
        return dateField;
    }
    //</editor-fold>//GEN-END:|258-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitAlert ">//GEN-BEGIN:|259-getter|0|259-preInit
    /**
     * Returns an initiliazed instance of exitAlert component.
     * @return the initialized component instance
     */
    public Alert getExitAlert() {
        if (exitAlert == null) {//GEN-END:|259-getter|0|259-preInit
            // write pre-init user code here
            exitAlert = new Alert("Exit Warning", "Are you sure you want to exit?\n\nAny unsent data will be lost!", null, AlertType.CONFIRMATION);//GEN-BEGIN:|259-getter|1|259-postInit
            exitAlert.addCommand(getExitAlertCmd());
            exitAlert.addCommand(getExitAlertBackCmd());
            exitAlert.setCommandListener(this);
            exitAlert.setTimeout(Alert.FOREVER);//GEN-END:|259-getter|1|259-postInit
            // write post-init user code here
        }//GEN-BEGIN:|259-getter|2|
        return exitAlert;
    }
    //</editor-fold>//GEN-END:|259-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitAlertCmd ">//GEN-BEGIN:|260-getter|0|260-preInit
    /**
     * Returns an initiliazed instance of exitAlertCmd component.
     * @return the initialized component instance
     */
    public Command getExitAlertCmd() {
        if (exitAlertCmd == null) {//GEN-END:|260-getter|0|260-preInit
            // write pre-init user code here
            exitAlertCmd = new Command("Yes", Command.EXIT, 0);//GEN-LINE:|260-getter|1|260-postInit
            // write post-init user code here
        }//GEN-BEGIN:|260-getter|2|
        return exitAlertCmd;
    }
    //</editor-fold>//GEN-END:|260-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitAlertBackCmd ">//GEN-BEGIN:|262-getter|0|262-preInit
    /**
     * Returns an initiliazed instance of exitAlertBackCmd component.
     * @return the initialized component instance
     */
    public Command getExitAlertBackCmd() {
        if (exitAlertBackCmd == null) {//GEN-END:|262-getter|0|262-preInit
            // write pre-init user code here
            exitAlertBackCmd = new Command("No", Command.BACK, 0);//GEN-LINE:|262-getter|1|262-postInit
            // write post-init user code here
        }//GEN-BEGIN:|262-getter|2|
        return exitAlertBackCmd;
    }
    //</editor-fold>//GEN-END:|262-getter|2|

    private void getEmptyFields() {
        String ancFormData = pregNum.getString() + "|" + firstTrimesterNum.getString() + "|" + jsyNum.getString() + "|" + threeAncNum.getString() + "|" + tt1Num.getString() + "|" + boosterNum.getString() + "|" + ifaTabletNum.getString() + "|" + hypertensionNum.getString() + "|" + anaemicAncNum.getString();
        String deliveriesFormData = sbaHomeNum.getString() + "|" + nonSbaHomeNum.getString() + "|" + newbornsHomeNum.getString() + "|" + jsyHomeNum.getString() + "|" + facilityNum.getString() + "|" + earlyDischargeNum.getString() + "|" + jsyFacilityMother.getString() + "|" + jsyFacilityAsha.getString() + "|" + jsyFacilityAnm.getString();
        String pregOutFormData = liveMaleNum.getString() + "|" + liveFemaleNum.getString() + "|" + stillNum.getString() + "|" + abortionsNum.getString() + "|" + weightedTotalNum.getString() + "|" + underWeightNum.getString() + "|" + breastFedNum.getString() + "|" + pnc48Num.getString() + "|" + pnc14daysNum.getString();
        String familyPlanFormData = iudInsFacilityNum.getString() + "|" + iudRemFacilityNum.getString() + "|" + ocpDistNum.getString() + "|" + condomsNum.getString() + "|" + weeklyPillsNum.getString() + "|" + emerContraNum.getString() + "|" + compliMaleSterNum.getString() + "|" + compliFemSterNum.getString() + "|" + failMaleSterNum.getString() + "|" + failFemSterNum.getString() + "|" + deathMaleSterNum.getString() + "|" + deathFemSterNum.getString();
        String childImm1FormData = bcgNum.getString() + "|" + dpt1Num.getString() + "|" + dpt2Num.getString() + "|" + dpt3Num.getString() + "|" + opv0Num.getString() + "|" + opv1Num.getString() + "|" + opv2Num.getString() + "|" + opv3Num.getString() + "|" + hepB1Num.getString() + "|" + hepB2Num.getString() + "|" + hepB3Num.getString() + "|" + measlesVacNum.getString() + "|" + fullImmMaleNum.getString() + "|" + fullImmFemNum.getString();
        String childImm2FormData = dptBNum.getString() + "|" + opvBNum.getString() + "|" + mmrNum.getString() + "|" + fullImm2MaleNum.getString() + "|" + fullImm2FemNum.getString() + "|" + dt5Num.getString() + "|" + tt10Num.getString() + "|" + tt16Num.getString();
        String childImm3FormData = aefiAbscessNum.getString() + "|" + aefiDeathsNum.getString() + "|" + aefiOthersNum.getString() + "|" + sessionPlannedNum.getString() + "|" + sessionHeldNum.getString() + "|" + sessionsAshaNum.getString();
        String childImm4FormData = vitA1Num.getString() + "|" + vitA5Num.getString() + "|" + vitA9Num.getString() + "|" + caseMeaslesNum.getString() + "|" + caseDiarrhNum.getString() + "|" + caseMalariaNum.getString();
        String healthFacilityFormData = vhndNum.getString() + "|" + opdNum.getString() + "|" + hbTestsNum.getString() + "|" + anaemicOpdNum.getString() + "|";

        String fullStr = ancFormData + "|" + deliveriesFormData + "|" + pregOutFormData + "|" + familyPlanFormData + "|" + childImm1FormData + "|" + childImm2FormData + "|" + childImm3FormData + "|" + childImm4FormData + "|" + healthFacilityFormData;

        String[] allContent = split(fullStr);
        int j = 0;
        for (int i = 0; i < 77; i++) {
            if (allContent[i].equals("")) {
                j++;
            }
        }
        try {
            if (j != 0) {
                Image missingImg = Image.createImage("/org/hispindia/mobile/images/exclamation.png");
                imgItem.setImage(missingImg);
                imgItem.setLabel(j + " fields are not filled. Please verify...");
                imgItem.setLayout(ImageItem.LAYOUT_DEFAULT);
            } else {
                Image completeImg = Image.createImage("/org/hispindia/mobile/images/success.png");
                imgItem.setImage(completeImg);
                imgItem.setLabel("All fields are filled");
                imgItem.setLayout(ImageItem.LAYOUT_DEFAULT);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String[] split(String original) {
        Vector nodes = new Vector();
        String separator = "|";

        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create a split string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }
        }
        return result;
    }

    /**
     * Returns a display instance.
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
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
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
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
    }
}