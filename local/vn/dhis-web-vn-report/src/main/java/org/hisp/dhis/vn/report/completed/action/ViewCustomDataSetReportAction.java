package org.hisp.dhis.vn.report.completed.action;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataEntryForm;
import org.hisp.dhis.dataset.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

public class ViewCustomDataSetReportAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private DataEntryFormService dataEntryFormService;

    private PeriodService periodService;

    private DataSetService dataSetService;

    private DataValueService dataValueService;

    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private Integer dataSetId;

    private Integer periodId;

    private Integer organisationUnitId;

    private String customDataEntryFormCode;

    private DataSet dataSet;

    private Period period;

    private OrganisationUnit organisationUnit;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public Period getPeriod()
    {
        return period;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public String execute()
        throws Exception
    {
        period = periodService.getPeriod( periodId.intValue() );

        dataSet = dataSetService.getDataSet( dataSetId.intValue() );

        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId.intValue() );  

        Map<String, String> dataValues = new HashMap<String, String>();
        
        //System.out.println(period.getId() + ":" + dataSet.getId() + ":" + organisationUnit.getId());

        for ( DataElement dataElement : dataSet.getDataElements() )
        {
            //System.out.println("DataElement:" + dataElement.getId());
            DataElementCategoryCombo catCombo = dataElement.getCategoryCombo();
            for ( DataElementCategoryOptionCombo optionCombo : catCombo.getOptionCombos() )
            {
                if ( dataElement.getType().equals( DataElement.TYPE_INT ) )
                {
                    DataValue value = dataValueService
                        .getDataValue( organisationUnit, dataElement, period, optionCombo );
                    if(value!=null){
                        dataValues.put( dataElement.getId() + ":" + optionCombo.getId(), value.getValue() ); 
                    }                   

                }

            }
        }

        // -----------------------------------------------------------------
        // Get the custom data entry form if any
        // -----------------------------------------------------------------

        DataEntryForm dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );

        customDataEntryFormCode = CustomDataSetReportGenerator.prepareReportContent( dataEntryForm.getHtmlCode(),
            dataValues );

        return SUCCESS;
    }

}
