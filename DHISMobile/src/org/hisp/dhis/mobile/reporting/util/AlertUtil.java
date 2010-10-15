package org.hisp.dhis.mobile.reporting.util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public class AlertUtil {

	public static Alert getErrorAlert(String title, String msg) {
		Alert alert = new Alert(title);
		alert.setString(msg);
		alert.setType(AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		return alert;
	}

	public static Alert getInfoAlert(String title, String msg) {
		Alert alert = new Alert(title);
		alert.setString(msg);
		alert.setType(AlertType.INFO);
		alert.setTimeout(Alert.FOREVER);
		return alert;
	}

	public static Alert getConfirmAlert(String title, String msg,
			AlertConfirmListener listener, MIDlet midlet,
			Displayable currentScreen, Displayable nextScreen) {

		Alert alert = new Alert(title, msg, null, AlertType.CONFIRMATION);
		alert.addCommand(new Command("YES", Command.OK, 0));
		alert.addCommand(new Command("NO", Command.CANCEL, 0));
		listener.setMidlet(midlet);
		listener.setCurrentScrren(currentScreen);
		listener.setNextScreen(nextScreen);
		alert.setCommandListener(listener);
		return alert;
	}

}
