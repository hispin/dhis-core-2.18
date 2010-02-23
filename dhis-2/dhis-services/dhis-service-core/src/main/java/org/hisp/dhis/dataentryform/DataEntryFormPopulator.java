package org.hisp.dhis.dataentryform;

import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.OptionsCategoriesDefaultSortOrderPopulator;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;

public class DataEntryFormPopulator extends AbstractStartupRoutine {
	private static final Log log = LogFactory
			.getLog(OptionsCategoriesDefaultSortOrderPopulator.class);
	private StatementManager statementManager;

	public void setStatementManager(StatementManager statementManager) {
		this.statementManager = statementManager;
	}


	public void execute() throws Exception {
		
		log.info("Update association between DataEntryForm table  and DataEntryFormAssociation table");
		//      For mysql
		//executeSql("alter table dataentryform drop foreign key fk_dataentryform_datasetid;");
		
		// 		For postgres
		executeSql("alter table dataentryform drop constraint fk_dataentryform_datasetid;");

		executeSql("insert into dataentryformassociation  select 'dataset', datasetid, dataentryformid from dataentryform ; ");
		executeSql("alter table dataentryform drop column datasetid;");
		
		log.info("Finished Update association between DataEntryForm table  and DataEntryFormAssociation table");

	}

	private int executeSql(String sql) {
		try {
			return statementManager.getHolder().getStatement().executeUpdate(sql);
		} catch (Exception ex) {
			log.debug(ex);
			return -1;
		}
	}

}
