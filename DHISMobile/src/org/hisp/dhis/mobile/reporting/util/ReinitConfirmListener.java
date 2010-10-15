package org.hisp.dhis.mobile.reporting.util;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import org.hisp.dhis.mobile.reporting.db.ModelRecordStore;
import org.hisp.dhis.mobile.reporting.db.SettingsRecordStore;
import org.hisp.dhis.mobile.reporting.db.ValueRecordStore;
import org.hisp.dhis.mobile.reporting.gui.DHISMIDlet;

public class ReinitConfirmListener extends AlertConfirmListener {
	public void commandAction(Command c, Displayable d) {
		if (c.getCommandType() == Command.OK) {
			ModelRecordStore.clear(ModelRecordStore.ACTIVITY_PLAN_DB);
			ModelRecordStore.clear(ModelRecordStore.ACTIVITY_DB);
			ModelRecordStore.clear(ModelRecordStore.DATASET_DB);
			ModelRecordStore.clear(ModelRecordStore.PROGRAM_DB);
			ModelRecordStore.clear(ModelRecordStore.PROGRAM_STAGE_DB);
			ModelRecordStore.clear(SettingsRecordStore.SETTINGS_DB);
			ModelRecordStore.clear(ValueRecordStore.VALUE_DB);
			((DHISMIDlet) this.midlet).switchDisplayable(null, nextScreen);
		} else if (c.getCommandType() == Command.CANCEL) {
			((DHISMIDlet) this.midlet).switchDisplayable(null, currentScrren);
		}
	}
}
