package org.hisp.dhis.dashboard.nr.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork.Action;

import edu.emory.mathcs.backport.java.util.Collections;

public class GenerateNullReporterResultAction
    implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
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

    public DataSetService getDataSetService()
    {
        return dataSetService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private List<DataSet> dataSetList1;

    public List<DataSet> getDataSetList1()
    {
        return dataSetList1;
    }

    private List<Integer> results;

    public List<Integer> getResults()
    {
        return results;
    }

    private Map<DataSet, Collection<Period>> dataSetPeriods;

    public Map<DataSet, Collection<Period>> getDataSetPeriods()
    {
        return dataSetPeriods;
    }

    List<Period> selectedPeriodList;

    public List<Period> getSelectedPeriodList()
    {
        return selectedPeriodList;
    }

    List<String> levelNames;

    public List<String> getLevelNames()
    {
        return levelNames;
    }

    private int maxOULevel;

    public int getMaxOULevel()
    {
        return maxOULevel;
    }

    // ---------------------------------------------------------------
    // Input Parameters
    // ---------------------------------------------------------------

    private String ouIDTB;

    public void setOuIDTB( String ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    public int getSDateLB()
    {
        return sDateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    public int getEDateLB()
    {
        return eDateLB;
    }

    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private List<String> selectedDataElements;

    public void setSelectedDataElements( List<String> selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }

    public List<String> getSelectedDataElements()
    {
        return selectedDataElements;
    }

    private int minOULevel;

    public int getMinOULevel()
    {
        return minOULevel;
    }

    private Map<DataElement, PeriodType> dePeriodTypeMap;

    public Map<DataElement, PeriodType> getDePeriodTypeMap()
    {
        return dePeriodTypeMap;
    }

    private Map<OrganisationUnit, Map<Period, List<DataElement>>> nullReportResult;

    public Map<OrganisationUnit, Map<Period, List<DataElement>>> getNullReportResult()
    {
        return nullReportResult;
    }

    private Map<Period, List<DataElement>> periodDeListMap;

    public Map<Period, List<DataElement>> getPeriodDeListMap()
    {
        return periodDeListMap;
    }

    private int size;

    public int getSize()
    {
        return size;
    }

    List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<Period> periodsColl;

    public List<Period> getPeriodsColl()
    {
        return periodsColl;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute()
        throws Exception
    {
        // VelocityContext context = new VelocityContext();
        simpleDateFormat = new SimpleDateFormat( "MMM y" );

        nullReportResult = new HashMap<OrganisationUnit, Map<Period, List<DataElement>>>();

        // OrgUnit Related Info

        OrganisationUnit selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouIDTB ) );
        System.out.println( "OUIDTB : " + ouIDTB );
        orgUnitList = new ArrayList<OrganisationUnit>();

        if ( facilityLB.equalsIgnoreCase( "selected" ) )
        {
            orgUnitList.add( selectedOrgUnit );
        }
        else
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
        }

        Period startDate = periodService.getPeriod( sDateLB );
        Period endDate = periodService.getPeriod( eDateLB );

        // orgUnitList = new ArrayList<OrganisationUnit>();
        List<DataElement> deList = new ArrayList<DataElement>();

        dePeriodTypeMap = new HashMap<DataElement, PeriodType>();
        periodDeListMap = new HashMap<Period, List<DataElement>>();

        for ( String deid : selectedDataElements )
        {
            DataElement de1 = dataElementService.getDataElement( Integer.parseInt( deid ) );

            dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

            int flag = 0;
            for ( DataSet ds : dataSetList )
            {
                if ( ds.getDataElements().contains( de1 ) )
                {
                    dePeriodTypeMap.put( de1, ds.getPeriodType() );
                    flag = 1;
                    break;
                }
            }
            if ( flag == 0 )
            {
                dePeriodTypeMap.put( de1, new MonthlyPeriodType() );
            }

            deList.add( de1 );
        }

        periodsColl = new ArrayList<Period>( periodService.getIntersectingPeriods( startDate.getStartDate(), endDate
            .getEndDate() ) );
        size = periodsColl.size();
        Collections.sort( periodsColl, new PeriodTypeComparator() );

        for ( OrganisationUnit curOu : orgUnitList )
        {
            for ( Period p : periodsColl )
            {
                List<DataElement> resultDeList = new ArrayList<DataElement>();

                for ( DataElement de : deList )
                {

                    if ( (dePeriodTypeMap.get( de ).equals( p.getPeriodType() )) )
                    {
                        double aggValue = 0.0;
                        if ( facilityLB.equalsIgnoreCase( "selected" ) )
                        {
                            List<DataElementCategoryOptionCombo> decocList = new ArrayList<DataElementCategoryOptionCombo>(
                                de.getCategoryCombo().getOptionCombos() );
                            for ( DataElementCategoryOptionCombo decoc : decocList )
                            {
                                double tempVal = aggregationService.getAggregatedDataValue( de, decoc,
                                    p.getStartDate(), p.getEndDate(), selectedOrgUnit );
                                if ( tempVal > 0 )
                                    aggValue += tempVal;
                            }

                            if ( aggValue <= 0.0 )
                                resultDeList.add( de );
                        }
                        else
                        {
                            DataValue dataValue = dataValueService.getDataValue( curOu, de, p );

                            if ( dataValue == null )
                            {
                                resultDeList.add( de );
                            }

                        }
                    }
                }
                if ( resultDeList.size() != 0 )
                {
                    periodDeListMap.put( p, resultDeList );
                    nullReportResult.put( curOu, periodDeListMap );
                }
            }
        }

        return SUCCESS;
    }

}
