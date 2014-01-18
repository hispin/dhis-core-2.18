package org.hisp.dhis.pbf.quality.dataentry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;
import org.hisp.dhis.pbf.api.QualityMaxValue;
import org.hisp.dhis.pbf.api.QualityMaxValueService;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;

import com.opensymphony.xwork2.Action;

public class LoadQualityScoreDetailsAction
    implements Action
{

	private final static String QUALITY_MAX_DATAELEMENT = "QUALITY_MAX_DATAELEMENT";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QualityMaxValueService qualityMaxValueService;
    
    public void setQualityMaxValueService(
			QualityMaxValueService qualityMaxValueService) {
		this.qualityMaxValueService = qualityMaxValueService;
	}

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private LookupService lookupService;
    
    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private DataValueService dataValueService;
    
    public void setDataValueService(DataValueService dataValueService)
    {
		this.dataValueService = dataValueService;
	}
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
   
	private String orgUnitId;
    
    public void setOrgUnitId(String orgUnitId) {
		this.orgUnitId = orgUnitId;
	}

	private String dataSetId;
    
    public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}
    
	private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
    
	List<DataElement> dataElements = new ArrayList<DataElement>();
    
	public List<DataElement> getDataElements() {
		return dataElements;
	}	

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );;
    
    public SimpleDateFormat getSimpleDateFormat() {
		return simpleDateFormat;
	}
    
    private Map<Integer,QualityMaxValue> qualityMaxValueMap = new  HashMap<Integer,QualityMaxValue>();
    
    public Map<Integer, QualityMaxValue> getQualityMaxValueMap() {
		return qualityMaxValueMap;
	}
    
    private Map<Integer,DataValue> dataValueMap = new HashMap<Integer, DataValue>();
    
    public Map<Integer, DataValue> getDataValueMap() {
		return dataValueMap;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute() throws Exception
    {
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
		Period period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
		
		Constant qualityMaxDataElement = constantService.getConstantByName( QUALITY_MAX_DATAELEMENT );
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        DataSet dataSet = dataSetService.getDataSet(Integer.parseInt(dataSetId));
        
        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        List<DataElement> dataElementList = new ArrayList<DataElement>(dataSet.getDataElements());
        for( DataElement de : dataElementList )
        {
        	Set<AttributeValue> attrValueSet = new HashSet<AttributeValue>( de.getAttributeValues() );
            for ( AttributeValue attValue : attrValueSet )
            {
            	if(attValue.getAttribute().getId() == qualityMaxDataElement.getValue())
            	{
            		dataElements.add(de);
            	}
            }
        }
        for(DataElement dataElement : dataElements)
        {
        	List<QualityMaxValue> qualityMaxValues = new ArrayList<QualityMaxValue>();
        	
        	qualityMaxValues = new ArrayList<QualityMaxValue>(qualityMaxValueService.getQuanlityMaxValues(organisationUnit, dataElement)) ;
        	DataValue dataValue = dataValueService.getDataValue(dataElement, period, organisationUnit, optionCombo);
        	for (QualityMaxValue qualityMaxValue : qualityMaxValues) 
        	{
        		
        		if(qualityMaxValue.getStartDate().getTime() <= period.getStartDate().getTime() && period.getEndDate().getTime() <= qualityMaxValue.getEndDate().getTime()  )
            	{
            		qualityMaxValueMap.put(dataElement.getId(), qualityMaxValue);
            		if(dataValue != null)
            		{
            			dataValueMap.put(dataElement.getId(), dataValue );
            		}
            		
            		System.out.println("In Quality Data Value");
            		break;
            	}
			}        	
        }
        Collections.sort(dataElements);
        return SUCCESS;
    }
}