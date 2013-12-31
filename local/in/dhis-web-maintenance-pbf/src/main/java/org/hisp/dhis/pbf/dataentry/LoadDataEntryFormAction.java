package org.hisp.dhis.pbf.dataentry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadDataEntryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------
/*
    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }
*/    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private String orgUnitId;
  
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    public Map<String, String> dataValueMap;
    
    public Map<String, String> getDataValueMap()
    {
        return dataValueMap;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    private Period period;
    
    public Period getPeriod()
    {
        return period;
    }
    
    private List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>();
    
    public List<DataElementCategoryOptionCombo> getOptionCombos()
    {
        return optionCombos;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

 
    public String execute()
    {
        
        dataValueMap = new HashMap<String, String>();
        
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
        period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        
        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        optionCombos = new ArrayList<DataElementCategoryOptionCombo>();
       
        for( DataElement dataElement : dataElements )
        {
            //DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
            
            DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            
            optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );
            
            for( DataElementCategoryOptionCombo decombo : optionCombos )
            {
                DataValue dataValue = new DataValue();
                
                dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, decombo );
                
                String value = "";
                
                if ( dataValue != null )
                {
                    value = dataValue.getValue();
                }
                
                String key = dataElement.getId()+ ":" +  decombo.getId();
                
                dataValueMap.put( key, value );
            }
            
        }
        
        /*
        for( DataElementCategoryOptionCombo decombo : optionCombos )
        {
            System.out.println(" decombo ---" + decombo.getId() +" -- " + decombo.getName() );
        }
        */
        
        
        return SUCCESS;
    }

}

