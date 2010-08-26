package org.hisp.dhis.mobile.util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

public class AlertUtil {
	
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
}
