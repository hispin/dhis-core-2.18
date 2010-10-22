package org.hisp.dhis.caseentry.action.caseaggregation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.caseaggregation.CaseAggregationMapping;
import org.hisp.dhis.caseaggregation.CaseAggregationMappingService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitShortNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class CaseAggregationResultAction
    implements Action
{

    Log log = LogFactory.getLog( getClass() );
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private CaseAggregationMappingService caseAggregationService;

    public void setCaseAggregationService( CaseAggregationMappingService caseAggregationService )
    {
        this.caseAggregationService = caseAggregationService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // ---------------------------------------------------------------
    // Input & Output Parameters
    // ---------------------------------------------------------------

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private String orgUnitListCB;

    public void setOrgUnitListCB( String orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private String selectedDataSets;

    public void setSelectedDataSets( String selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    private String resultMessage;

    public String getResultMessage()
    {
        return resultMessage;
    }

    private DataSet selDataSet;

    private OrganisationUnit selOrgUnit;

    private List<OrganisationUnit> orgUnitList;

    private List<DataElement> dataElementList;

    private List<Period> periodList;

    private String storedBy;

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute()
        throws Exception
    {
        
//        if ( true )
//        {
//            int resultValue1 = caseAggregationService.getCaseAggregateValue( null, null,
//                null );
//            
//            
//            return SUCCESS;
//        }
        
        storedBy = currentUserService.getCurrentUsername() + "_CAE";

        resultMessage = "";

        // DataSet and DataElement
        selDataSet = dataSetService.getDataSet( Integer.parseInt( selectedDataSets ) );
        dataElementList = new ArrayList<DataElement>( selDataSet.getDataElements() );

        // Orgunnit list
        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB ) );

        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            orgUnitList = getChildOrgUnitTree( selOrgUnit );
        }
        else if ( facilityLB.equals( "immChildren" ) )
        {
            orgUnitList.add( selOrgUnit );
            List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( selOrgUnit.getChildren() );
            Collections.sort( organisationUnits, new OrganisationUnitShortNameComparator() );
            orgUnitList.addAll( organisationUnits );
        }
        else
        {
            orgUnitList.add( selOrgUnit );
        }

        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        PeriodType dataSetPeriodType = selDataSet.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( dataSetPeriodType,
            startPeriod.getStartDate(), endPeriod.getEndDate() ) );

        // Orgunit Iteration for Aggregation
        
        for ( OrganisationUnit orgUnit : orgUnitList )
        {

            for ( DataElement dElement : dataElementList )
            {
                List<DataElementCategoryOptionCombo> deCOCList = new ArrayList<DataElementCategoryOptionCombo>(
                    dElement.getCategoryCombo().getOptionCombos() );
                
                for ( DataElementCategoryOptionCombo optionCombo : deCOCList )
                {
                    CaseAggregationMapping caseAggregationMapping = caseAggregationService
                        .getCaseAggregationMappingByOptionCombo( dElement, optionCombo );
System.out.println("\n\n\n ++++++++++++++ \n caseAggregationMapping : " + caseAggregationMapping);
                    if ( caseAggregationMapping == null || caseAggregationMapping.getExpression() == null
                        || caseAggregationMapping.getExpression().trim().equals( "" ) )
                        break;
                    
                    for ( Period period : periodList )
                    {
                        int resultValue = caseAggregationService.getCaseAggregateValue( orgUnit, period,
                            caseAggregationMapping );
                        
                        if ( resultValue != 0 )
                        {
                            String tempStr = "" + orgUnit.getName() + "_" + dElement.getName() + "_"
                                + period.getStartDate() + "_";
                            DataValue dataValue = dataValueService
                                .getDataValue( orgUnit, dElement, period, optionCombo );
                            if ( dataValue == null )
                            {
                                dataValue = new DataValue( dElement, period, orgUnit, "" + resultValue, storedBy,
                                    new Date(), null, optionCombo );

                                dataValueService.addDataValue( dataValue );
                                tempStr += resultValue + "Added.";
                            }
                            else
                            {
                                dataValue.setValue( "" + resultValue );
                                dataValue.setTimestamp( new Date() );
                                dataValue.setStoredBy( storedBy );

                                dataValueService.updateDataValue( dataValue );
                                tempStr += resultValue + "Updated.";
                            }

                            System.out.println( tempStr );
                            resultMessage += "<br>" + tempStr;
                        }

                    }// PeriodList end

                }// OptionComboList end
            }// DataElementList end

        }// Orgunit for loop end

        return SUCCESS;
    }

    // Returns the OrgUnitTree for which Root is the orgUnit
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end

}
