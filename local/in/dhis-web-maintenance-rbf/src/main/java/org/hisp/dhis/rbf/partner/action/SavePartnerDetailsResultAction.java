package org.hisp.dhis.rbf.partner.action;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.rbf.api.PBFDataValue;
import org.hisp.dhis.rbf.api.PBFDataValueService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SavePartnerDetailsResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SelectionTreeManager selectionTreeManager;
    
    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private PBFDataValueService pbfDataValueService;
    
    public void setPbfDataValueService(PBFDataValueService pbfDataValueService) 
    {
        this.pbfDataValueService = pbfDataValueService;
    }
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private OptionService optionService;
    
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    private Integer optionSetId;
    
    public void setOptionSetId( Integer optionSetId )
    {
        this.optionSetId = optionSetId;
    }
    
    private Integer dataElementId;
    
    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }
    
    private Integer periodId;
    
    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        Period period = periodService.getPeriod( periodId );
        
        Option option = optionService.getOption( optionSetId );
        
        /*
        System.out.println( " Option name -- " + option.getName() );
        System.out.println( " dataSet name -- " + dataSet.getName() );
        System.out.println( " dataElement name -- " + dataElement.getName() );
        System.out.println( " period name -- " + period.getName() );
        */
        
        Set<OrganisationUnit> selectedOrgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
        
        for ( OrganisationUnit organisationUnit : selectedOrgUnitList )
        {
            PBFDataValue pbfDataValue = pbfDataValueService.getPBFDataValue( organisationUnit, dataSet, period, dataElement );
            
            if ( pbfDataValue != null )
            {
                pbfDataValue.setOption( option );
                pbfDataValue.setTimestamp( new Date() );

                pbfDataValueService.updatePBFDataValue( pbfDataValue );
            }
            
            //System.out.println( " orgUnit name -- " + organisationUnit.getName() );
        }
        
        //System.out.println( " Size of orgUnitList First -- " + orgUnitList.size() );
        
        
        return SUCCESS;
    }
    
}
