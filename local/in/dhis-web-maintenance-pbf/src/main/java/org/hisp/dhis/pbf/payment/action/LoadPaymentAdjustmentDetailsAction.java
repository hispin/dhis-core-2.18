package org.hisp.dhis.pbf.payment.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadPaymentAdjustmentDetailsAction
    implements Action
{
	private final static String PAYMENT_ADJUSTMENT_AMOUNT_DE = "PAYMENT_ADJUSTMENT_AMOUNT_DE";
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
    private DataElementService dataElementService;

	public void setDataElementService(DataElementService dataElementService) {
		this.dataElementService = dataElementService;
	}

	private ConstantService constantService;

	public void setConstantService(ConstantService constantService) {
		this.constantService = constantService;
	}
	
	@Autowired
	private PeriodService periodService;
	
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
    
    private String amountAvailable = "";
    
    public String getAmountAvailable() {
		return amountAvailable;
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
        
        Constant paymentAmount = constantService.getConstantByName(PAYMENT_ADJUSTMENT_AMOUNT_DE);
		
		String amountDEId = paymentAmount.getValue()+"";
        
        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        for(DataElement de : dataElements)
        {
        	int quantityValue = 0;
        	int tariffValue = 0;
        	List<OrganisationUnit> orgList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId()));
        	for(OrganisationUnit ou : orgList )
        	{
        		List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates(period.getStartDate(), period.getEndDate()));
	        	for(Period prd : periodList)
	        	{
	        		DataValue dataValue = dataValueService.getDataValue(de, prd, ou, optionCombo );
		        	if(dataValue != null)
		        	{
		        		quantityValue = quantityValue + Integer.parseInt(dataValue.getValue());	        		
		        	}	        	
		        	TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue( ou , de, dataSet, prd.getStartDate(), prd.getEndDate());
		        	
		        	if(tariffDataValue != null)
		        	{
		        		tariffValue = tariffValue + Integer.parseInt(tariffDataValue.getValue()+"");
		        	}
	        	}
        	}
        	quantityValidatedMap.put(de.getUid(), quantityValue+"" );
        	tariffDataValueMap.put(de.getUid(), tariffValue+"");
        }
        Collections.sort(dataElements);
        DataElement dataElement = dataElementService.getDataElement((int)paymentAmount.getValue());
        DataValue dataValue = dataValueService.getDataValue(dataElement, period, organisationUnit, optionCombo);
        
        if(dataValue != null)
        {
        	amountAvailable = dataValue.getValue();        	
        }
        return SUCCESS;
    }
}