/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.mobile.reporting.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * 
 * @author abyotag_adm
 */
public class Period {

	public static final int DAILY = 0;
	public static final int WEEKLY = 1;
	public static final int MONTHLY = 2;
	public static final int QUARTERLY = 3;
	public static final int SIX_MONTHLY = 4;
	public static final int YEARLY = 5;
	public static final int TWO_YEARLY = 6;

	public Period() {
	}

	public static Vector generateWeeklyPeriods() {
		Vector weeks = new Vector();
		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);

		int a, b, c, s, e, f, g, d, n;
		int week;

		if (month <= 2) {
			a = year - 1;
			b = a / 4 - a / 100 + a / 400;
			c = (a - 1) / 4 - (a - 1) / 100 + (a - 1) / 400;
			s = b - c;
			e = 0;
			f = day - 1 + 31 * (month - 1);
		} else {
			a = year;
			b = a / 4 - a / 100 + a / 400;
			c = (a - 1) / 4 - (a - 1) / 100 + (a - 1) / 400;
			s = b - c;
			e = s + 1;
			f = day + (153 * (month - 3) + 2) / 5 + 58 + s;
		}

		g = (a + b) % 7;
		d = (f + g - e) % 7;
		n = f + 3 - d;

		if (n < 0) {
			week = 53 - (g - s) / 5;
			year = year - 1;
		} else if (n > 364 + s) {
			week = 1;
			year = year + 1;
		} else {
			week = n / 7 + 1;
		}

		// Display only 12 previous periods including the current one
		week = week - 11;

		if (week <= 0) {
			week = week + 53;
			year = year - 1;
		}

		// formatting week "Week WW YYYY"
		for (int i = 0; i < 12; i++) {
			if (week < 10) {
				weeks.addElement("Week  0" + week + " " + year);
			} else {
				weeks.addElement("Week " + week + " " + year);
			}

			week = week + 1;

			if (week > 53) {
				week = 1;
				year = year + 1;
			}
		}

		return weeks;
	}

	public static Vector generateMonthlyPeriods() {
		Vector months = new Vector();
		Calendar cal = Calendar.getInstance();

		String[] monthNames = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
				"December" };

		// Display only 12 previous periods including the current one
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 11);

		if (cal.get(Calendar.MONTH) < 0) {
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 12);
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
		}

		for (int i = 0; i < 12; i++) {
			if (cal.get(Calendar.MONTH) > 11) {
				cal.set(Calendar.MONTH, 0);
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
			}
			months.addElement(monthNames[cal.get(Calendar.MONTH)] + " "
					+ cal.get(Calendar.YEAR));
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		}

		return months;
	}

	public static Vector generateYearlyPeriods() {
		Vector years = new Vector();
		Calendar cal = Calendar.getInstance();

		// Display only 12 previous periods including the current one
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 11);

		for (int i = 0; i < 12; i++) {
			years.addElement(Integer.toString(cal.get(Calendar.YEAR)));
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
		}

		return years;
	}

	public static String formatDailyPeriod(Date date) {
		StringBuffer formattedPeriod = new StringBuffer();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int yr = cal.get(Calendar.YEAR);
		int mnth = (cal.get(Calendar.MONTH) + 1) % 13;
		int d = cal.get(Calendar.DATE);

		formattedPeriod.append(yr);
		formattedPeriod.append("-");

		if (mnth < 10) {
			formattedPeriod.append(0);
			formattedPeriod.append(mnth);
		} else {
			formattedPeriod.append(mnth);
		}

		formattedPeriod.append("-");

		if (d < 10) {
			formattedPeriod.append(0);
			formattedPeriod.append(d);
		} else {
			formattedPeriod.append(d);
		}

		return formattedPeriod.toString();
	}

	public static String formatWeeklyPeriod(String week) {
		week = week.substring(5, week.length());
		StringBuffer formattedPeriod = new StringBuffer();

		String w = week.substring(0, week.lastIndexOf(' '));
		String y = week.substring(week.indexOf(" ") + 1, week.length());

		formattedPeriod.append(w);
		formattedPeriod.append("-");
		formattedPeriod.append(y);

		return formattedPeriod.toString();
	}

	public static String formatMonthlyPeriod(String month) {
		String[] monthNames = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
				"December" };

		StringBuffer formattedPeriod = new StringBuffer();

		String m = month.substring(0, month.indexOf(" "));
		String y = month.substring(month.indexOf(" ") + 1, month.length());

		int mnth = -1;
		for (int i = 0; i < monthNames.length; i++) {
			if (monthNames[i].equals(m)) {
				mnth = i;
				break;
			}
		}

		formattedPeriod.append(mnth);
		formattedPeriod.append("-");
		formattedPeriod.append(y);

		return formattedPeriod.toString();
	}
}
