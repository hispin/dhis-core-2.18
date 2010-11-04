package org.hisp.dhis.web.api.service;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hisp.dhis.dataelement.comparator.DataElementSortOrderComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.api.model.DataElement;
import org.hisp.dhis.web.api.model.DataSet;
import org.hisp.dhis.web.api.model.Section;
import org.hisp.dhis.web.api.utils.LocaleUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultDataSetService implements IDataSetService {
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private DataElementSortOrderComparator dataElementComparator = new DataElementSortOrderComparator();
	
	@Autowired
	private org.hisp.dhis.dataset.DataSetService dataSetService;
	
	@Autowired
	private org.hisp.dhis.i18n.I18nService i18nService;	
	
	@Autowired
	private org.hisp.dhis.order.manager.DataElementOrderManager dataElementOrderManager ;	
	
	@Autowired
    private CurrentUserService currentUserService;
	
	
	// -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------	
	
	
	public void setDataElementOrderManager(
			org.hisp.dhis.order.manager.DataElementOrderManager dataElementOrderManager) {
		this.dataElementOrderManager = dataElementOrderManager;
	}

	public List<DataSet> getAllMobileDataSetsForLocale(String localeString) {
		Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();
        OrganisationUnit unit = null;
        
        if( units.size() > 0 )
        {
        	unit = units.iterator().next();       	
        }
        else
        {
        	return null;
        }
        
        
		
		List<DataSet> datasets = new ArrayList<DataSet>();
		Locale locale = LocaleUtil.getLocale(localeString);			
//		AbstractModelList abstractModelList = new AbstractModelList();
//
//		List<AbstractModel> abstractModels = new ArrayList<AbstractModel>();
//
		for (org.hisp.dhis.dataset.DataSet dataSet : dataSetService.getDataSetsForMobile(unit)) 
		{			
			if( dataSet.getPeriodType().getName().equals( "Daily") ||
					dataSet.getPeriodType().getName().equals( "Weekly") ||
					dataSet.getPeriodType().getName().equals( "Monthly") ||
					dataSet.getPeriodType().getName().equals( "Yearly") || 
					dataSet.getPeriodType().getName().equals("Quarterly") )
			{
				datasets.add(getDataSetForLocale(dataSet.getId(),locale));
			}
		}
//		
//		abstractModelList.setAbstractModels(abstractModels);
		
		return datasets;
	}

	public DataSet getDataSetForLocale(int dataSetId, Locale locale) {
		org.hisp.dhis.dataset.DataSet dataSet = dataSetService.getDataSet( dataSetId );
		dataSet = i18n( i18nService, locale, dataSet );
		Set<org.hisp.dhis.dataset.Section> sections = dataSet.getSections();
		
//		Collection<org.hisp.dhis.dataelement.DataElement> dataElements = dataSet.getDataElements();

		//Mobile
		DataSet ds = new DataSet();
		
		ds.setId( dataSet.getId() );			
		ds.setName( dataSet.getName() );
		ds.setPeriodType( dataSet.getPeriodType().getName() );
		
		//Mobile
		List<Section> sectionList = new ArrayList<Section>(); 
		ds.setSections(sectionList);
		
			if(sections.size() == 0 || sections == null){
//				Collection<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>();
				List<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>(dataSet.getDataElements());
									
				Collections.sort(dataElements, dataElementComparator);
				
				//Fake Section to store Data Elements
				Section section = new Section();
				
				sectionList.add(section);
				section.setId(0);
				section.setName("");
				
				List<DataElement> dataElementList = new ArrayList<DataElement>();
				section.setDes(dataElementList);
				
				for( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
				{
					dataElement = i18n( i18nService, locale, dataElement );
					
					DataElement de = new DataElement();
					de.setId( dataElement.getId() );
					de.setName( dataElement.getName() );
					de.setType( dataElement.getType() );
					dataElementList.add( de );
				}
			}else{
				for(org.hisp.dhis.dataset.Section each : sections){
				List<org.hisp.dhis.dataelement.DataElement> dataElements = each.getDataElements();
				
				Section section = new Section();
				section.setId(each.getId());
				section.setName(each.getName());
				//Mobile
				List<DataElement> dataElementList = new ArrayList<DataElement>();
				section.setDes(dataElementList);
				
				
				for( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
				{
					dataElement = i18n( i18nService, locale, dataElement );
					
					DataElement de = new DataElement();
					de.setId( dataElement.getId() );
					de.setName( dataElement.getName() );
					de.setType( dataElement.getType() );
					dataElementList.add( de );
				}
				sectionList.add(section);
			}
		}		
		
		return ds;
	}	
	
}
