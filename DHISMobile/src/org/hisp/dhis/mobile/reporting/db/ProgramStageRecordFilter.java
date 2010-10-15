package org.hisp.dhis.mobile.reporting.db;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.rms.RecordFilter;

/**
 * @author Tran Ng Minh Luan
 * 
 */
public class ProgramStageRecordFilter implements RecordFilter {
	public static final String filterByProgramId = "filterByProgramId";

	private int progId;

	private String filter;

	public ProgramStageRecordFilter(String filter) {
		this.filter = filter;
	}

	public int getProgId() {
		return progId;
	}

	public void setProgId(int progId) {
		this.progId = progId;
	}

	public boolean matches(byte[] candidate) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
				candidate));

		try {
			// skip ID and Name
			dis.readInt();
			dis.readUTF();
			// end
			if (this.filter.equals(filterByProgramId)) {
				if (dis.readInt() == this.progId) {
					return true;
				} else {
					return false;
				}
			} else {
				System.out.println("lack of filter");
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

}
