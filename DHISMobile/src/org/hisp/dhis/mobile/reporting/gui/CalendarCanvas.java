package org.hisp.dhis.mobile.reporting.gui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

public class CalendarCanvas extends Canvas implements CommandListener {
	CalendarWidget calendar = null;
	private DHISMIDlet dhisMIDlet;
	Command selectDateCmd = null;

	public CalendarCanvas(DHISMIDlet dhisMIDlet) {
		this.dhisMIDlet = dhisMIDlet;
		calendar = new CalendarWidget(dhisMIDlet.getDailyPeriodDateField()
				.getDate());
		calendar.initialize();
		selectDateCmd = new Command("Select", Command.OK, 1);
		setCommandListener(this);
		addCommand(selectDateCmd);
	}

	protected void keyPressed(int key) {
		int keyCode = getGameAction(key);
		if (keyCode == FIRE) {
			Display.getDisplay(dhisMIDlet).setCurrent(
					dhisMIDlet.getPeriodForm());
		} else {
			calendar.keyPressed(keyCode);
			repaint();
		}
	}

	protected void paint(Graphics g) {
		g.setColor(0xffffff);
		g.fillRect(0, 0, getWidth(), getHeight());
		calendar.paint(g);
	}

	public void commandAction(Command c, Displayable d) {

		if (c == selectDateCmd) {
			if (c.getCommandType() == 4) {
				dhisMIDlet.getDailyPeriodDateField().setDate(
						calendar.getSelectedDate());
				Display.getDisplay(dhisMIDlet).setCurrent(
						dhisMIDlet.getPeriodForm());
			}
		}
	}
}
