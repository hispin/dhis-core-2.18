package org.hisp.dhis.mobile.reporting.db;

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

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * 
 * @author abyotag_adm
 */
public class SettingsRecordStore {

	public static final String SETTINGS_DB = "SETTINGS";

	private String dbName;

	private Hashtable hashtable;

	public SettingsRecordStore(String dbName) throws RecordStoreException {
		this.dbName = dbName;
		hashtable = new Hashtable();
		load();
	}

	public String get(String setting) {

		String value = "";
		if (hashtable.containsKey(setting))
			value = (String) hashtable.get(setting);

		return value;
	}

	public void put(String setting, String value) {
		if (value == null)
			value = "";
		hashtable.put(setting, value);
	}

	private void load() throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(null, null, false);
			while (re.hasNextElement()) {
				String settingRecord = new String(re.nextRecord());

				int index = settingRecord.indexOf('|');

				String setting = settingRecord.substring(0, index);
				String value = settingRecord.substring(index + 1);
				put(setting, value);
			}
		} finally {
			if (re != null)
				re.destroy();
			if (rs != null)
				rs.closeRecordStore();
		}
	}

	public void save() throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(null, null, false);

			// clean and save
			while (re.hasNextElement()) {
				rs.deleteRecord(re.nextRecordId());
			}

			Enumeration keys = hashtable.keys();
			while (keys.hasMoreElements()) {
				String setting = (String) keys.nextElement();
				String value = get(setting);
				String settingValue = setting + "|" + value;

				byte[] raw = settingValue.getBytes();
				rs.addRecord(raw, 0, raw.length);
			}
		} finally {
			if (re != null)
				re.destroy();
			if (rs != null)
				rs.closeRecordStore();
		}
	}
}