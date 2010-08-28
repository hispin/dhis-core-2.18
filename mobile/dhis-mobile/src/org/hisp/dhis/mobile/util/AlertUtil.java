package org.hisp.dhis.mobile.util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public class AlertUtil{
	
	public static Alert getErrorAlert(String title, String msg){
		Alert alert = new Alert(title);
		alert.setString(msg);
		alert.setType(AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		return alert;
	}
	
	public static Alert getInfoAlert(String title, String msg){
		Alert alert = new Alert(title);
		alert.setString(msg);
		alert.setType(AlertType.INFO);
		alert.setTimeout(Alert.FOREVER);
		return alert;
	}
	
	public static Alert getConfirmAlert(MIDlet midlet, Displayable currentScreen, Displayable nextScreen){
	    
	    Alert alert = new Alert( "Confirmation", "Are you sure ?", null, AlertType.CONFIRMATION );
	    alert.addCommand( new Command("YES",Command.OK,0) );
	    alert.addCommand( new Command("NO",Command.CANCEL,0) );
	    alert.setCommandListener( new AlertConfirmListener(midlet,currentScreen, nextScreen) );
	    return alert;
	}

    
}
