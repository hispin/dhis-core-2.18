package org.hisp.dhis.web.api.service;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


import org.hisp.dhis.web.api.model.AbstractModel;
import org.hisp.dhis.web.api.model.AbstractModelList;
import org.hisp.dhis.web.api.model.DataElement;
import org.hisp.dhis.web.api.model.DataSet;
import org.hisp.dhis.web.api.utils.LocaleUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultDataSetService implements IDataSetService {
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
	private org.hisp.dhis.dataset.DataSetService dataSetService;
	
	@Autowired
	private org.hisp.dhis.i18n.I18nService i18nService;	
	
	// -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------	
	
	public AbstractModelList getAllMobileDataSetsForLocale(String localeString) {
		
		Locale locale = LocaleUtil.getLocale(localeString);		
		
		AbstractModelList abstractModelList = new AbstractModelList();

		List<AbstractModel> abstractModels = new ArrayList<AbstractModel>();

		for (org.hisp.dhis.dataset.DataSet dataSet : dataSetService.getDataSetsForMobile()) 
		{			
			if( dataSet.getPeriodType().getName().equals( "Daily") ||
					dataSet.getPeriodType().getName().equals( "Weekly") ||
					dataSet.getPeriodType().getName().equals( "Monthly") ||
					dataSet.getPeriodType().getName().equals( "Yearly") )
			{
				
				dataSet = i18n( i18nService, locale, dataSet );		

				AbstractModel abstractModel = new AbstractModel();

				abstractModel.setId( dataSet.getId());				
				abstractModel.setName(dataSet.getName());			

				abstractModels.add(abstractModel);					
			}								
		}
		
		abstractModelList.setAbstractModels(abstractModels);
		
		return abstractModelList;
	}
	
	public DataSet getDataSetForLocale(int dataSetId, String localeString) {
		
		Locale locale = LocaleUtil.getLocale(localeString);			
		
		org.hisp.dhis.dataset.DataSet dataSet = dataSetService.getDataSet( dataSetId );
		
		dataSet = i18n( i18nService, locale, dataSet );
		
		Collection<org.hisp.dhis.dataelement.DataElement> dataElements = dataSet.getDataElements();
		
		DataSet ds = new DataSet();
		List<DataElement> des = new ArrayList<DataElement>();
		ds.setId( dataSet.getId() );			
		
		ds.setName( dataSet.getName() );
		ds.setPeriodType( dataSet.getPeriodType().getName() );		
		
		for( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
		{
			dataElement = i18n( i18nService, locale, dataElement );
			
			DataElement de = new DataElement();
			de.setId( dataElement.getId() );
			de.setName( dataElement.getName() );
			de.setType( dataElement.getType() );
			des.add( de );
		}
		
		ds.setDataElements(des);		
		
		return ds;
	}	
	
}
