package org.hisp.dhis.mobile.reporting.util;

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
