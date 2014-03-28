package org.hisp.dhis.pbf.payment.action;

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
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.LookupService;
import org.hisp.dhis.pbf.api.QualityMaxValue;
import org.hisp.dhis.pbf.api.QualityMaxValueService;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class LoadPaymentAdjustmentDetailsAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    private DataValueService dataValueService;
    
    public void setDataValueService(DataValueService dataValueService) {
		this.dataValueService = dataValueService;
	}
    
    private TariffDataValueService tariffDataValueService;

    public void setTariffDataValueService( TariffDataValueService tariffDataValueService )
    {
        this.tariffDataValueService = tariffDataValueService;
    }


    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String orgUnitId;

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String periodIso;

    public void setPeriodIso(String periodIso) {
		this.periodIso = periodIso;
	}

	List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    } 
    
    private Map<String,String> quantityValidatedMap = new HashMap<String, String>();
    
    public Map<String, String> getQuantityValidatedMap() {
		return quantityValidatedMap;
	}

    private Map<String,String> tariffDataValueMap = new HashMap<String, String>();
    
    public Map<String, String> getTariffDataValueMap() {
		return tariffDataValueMap;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(orgUnitId);
        
        DataSet dataSet = dataSetService.getDataSet(Integer.parseInt(dataSetId));
        
        Period period = PeriodType.getPeriodFromIsoString( periodIso );
        
        dataElements.addAll(dataSet.getDataElements());
        
        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        for(DataElement de : dataElements)
        {
        	DataValue dataValue = dataValueService.getDataValue(de, period, organisationUnit, optionCombo );
        	if(dataValue != null)
        	{
        		quantityValidatedMap.put(de.getUid(), dataValue.getValue());
        	}
        	else
        	{
        		quantityValidatedMap.put(de.getUid(), "");
        	} 
        	TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue(organisationUnit, de, dataSet, period.getStartDate(), period.getEndDate());
        	
        	if(tariffDataValue != null)
        	{
        		tariffDataValueMap.put(de.getUid(), tariffDataValue.getValue()+"");
        	}
        	else
        	{
        		tariffDataValueMap.put(de.getUid(), "");
        	}
        }
        
        return SUCCESS;
    }
}