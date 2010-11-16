package org.hisp.dhis.web.api.service;

import java.util.List;
import java.util.Locale;

import org.hisp.dhis.web.api.model.ModelList;
import org.hisp.dhis.web.api.model.DataSet;

public interface IDataSetService {

	List<DataSet> getAllMobileDataSetsForLocale( String localeString );
	
	DataSet getDataSetForLocale( int dataSetId,Locale locale);
}
