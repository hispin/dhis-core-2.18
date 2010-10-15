package org.hisp.dhis.mobile.reporting.db;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * @author Tran Ng Minh Luan
 * 
 */
public class ProgramStageRecordStore {
	public static final String PROGRAMSTAGE_DB = "PROGRAMSTAGE";

	private String dbName;

	public ProgramStageRecordStore() {
		this.dbName = ModelRecordStore.PROGRAM_STAGE_DB;
	}

	public void deleteProgStageOfProgId(int progId) throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		ProgramStageRecordFilter rf = new ProgramStageRecordFilter(
				ProgramStageRecordFilter.filterByProgramId);
		rf.setProgId(progId);
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(rf, null, false);
			while (re.hasNextElement()) {
				rs.deleteRecord(re.nextRecordId());
				System.out.println("one progstage deleted");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			if (re != null)
				re.destroy();
			if (rs != null)
				rs.closeRecordStore();
			rf = null;
		}
	}
}
