package org.hisp.dhis.web.api.service;

import org.hisp.dhis.web.api.model.AbstractModelList;
import org.hisp.dhis.web.api.model.DataSet;

public interface IDataSetService {

	AbstractModelList getAllMobileDataSetsForLocale( String localeString );
	
	DataSet getDataSetForLocale( int dataSetId, String localeString );
}
