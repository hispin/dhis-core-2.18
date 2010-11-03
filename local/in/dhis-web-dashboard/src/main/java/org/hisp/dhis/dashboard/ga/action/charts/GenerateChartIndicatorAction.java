/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.dashboard.ga.action.charts;
    
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amplecode.quick.StatementManager;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dashboard.util.DashBoardService;
import org.hisp.dhis.dashboard.util.IndicatorChartResult;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateChartIdicatorAction.java Nov 3, 2010 12:41:31 PM
 */
public class GenerateChartIndicatorAction
implements Action
{

private final String PERIODWISE = "period";

private final String CHILDREN = "children";

private final String SELECTED = "random";

IndicatorChartResult  indicatorChartResult = new IndicatorChartResult() ;


// -------------------------------------------------------------------------
// Dependencies
// -------------------------------------------------------------------------

    
    private StatementManager statementManager;
    
    
    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }    

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private AggregationService aggregationService;
    
    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }
    
    private IndicatorService indicatorService;
    
    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------
    /*
    private Comparator<Indicator> indicatorComparator;
    
    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }
    */
    //--------------------------------------------------------------------------
    // Parameters
    //--------------------------------------------------------------------------
    private HttpSession session;
    
    public HttpSession getSession()
    {
        return session;
    }
    
    
    private List<Object> selectedServiceList;
    
    public List<Object> getSelectedServiceList()
    {
        return selectedServiceList;
    }
    
    private List<OrganisationUnit> selOUList;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private List<String> selectedIndicators;
    
    public void setSelectedIndicators( List<String> selectedIndicators )
    {
        this.selectedIndicators = selectedIndicators;
    }
    
    
    
    private String ougGroupSetCB;
    
    public void setOugGroupSetCB( String ougGroupSetCB )
    {
        this.ougGroupSetCB = ougGroupSetCB;
    }
    
    public String getOugGroupSetCB()
    {
        return ougGroupSetCB;
    }
    
    
    
    private List<String> orgUnitListCB;
    
    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }
    
    private String categoryLB;
    
    public String getCategoryLB()
    {
        return categoryLB;
    }
    
    public void setCategoryLB( String categoryLB )
    {
        this.categoryLB = categoryLB;
    }
    
    private String selectedButton;
    
    public String getSelectedButton()
    {
        return selectedButton;
    }
    
    public void setSelectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }
    
   private String aggDataCB;
    
    public void setAggDataCB( String aggDataCB )
    {
        this.aggDataCB = aggDataCB;
    }
    
    private String periodTypeLB;
    
    public void setPeriodTypeLB( String periodTypeLB )
    {
        this.periodTypeLB = periodTypeLB;
    }
    
    private List<String> yearLB;
    
    public void setYearLB( List<String> yearLB )
    {
        this.yearLB = yearLB;
    }
    
    private List<String> periodLB;
    
    public void setPeriodLB( List<String> periodLB )
    {
        this.periodLB = periodLB;
    }
    
    private List<String> periodNames;
    
    private List<Date> selStartPeriodList;
    
    private List<Date> selEndPeriodList;
    
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    @SuppressWarnings( "unchecked" )
    public String execute()
        throws Exception
    {
        
        statementManager.initialise();
    
        selOUList = new ArrayList<OrganisationUnit>();
        System.out.println( "selected orgUnit  size : " + orgUnitListCB.size() );
    
        System.out.println( "selected Year  size : " + yearLB.size() );
    
        System.out.println( "selected Period  size : " + periodLB.size() );
    
        System.out.println( "selected dataelements : " + selectedIndicators );
    
        System.out.println( "selected dataelements size : " + selectedIndicators.size() );
    
        // int flag = 0;
        // selOUList = new ArrayList<OrganisationUnit>();
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();
    
        // ouChildCountMap = new HashMap<OrganisationUnit, Integer>();
    
        String monthOrder[] = { "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03" };
        int monthDays[] = { 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31 };
    
        /* Period Info */
    
        String startD = "";
        String endD = "";
    
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
    
        periodNames = new ArrayList<String>();
    
        for ( String year : yearLB )
        {
            int selYear = Integer.parseInt( year.split( "-" )[0] );
    
            if ( periodTypeLB.equalsIgnoreCase( YearlyPeriodType.NAME ) )
            {
    
                startD = "" + selYear + "-04-01";
                endD = "" + (selYear + 1) + "-03-31";
    
                selStartPeriodList.add( format.parseDate( startD ) );
                selEndPeriodList.add( format.parseDate( endD ) );
    
                periodNames.add( "" + selYear + "-" + (selYear + 1) );
    
                continue;
    
            }
    
            for ( String periodStr : periodLB )
            {
                int period = Integer.parseInt( periodStr );
    
                if ( periodTypeLB.equalsIgnoreCase( MonthlyPeriodType.NAME ) )
                {
                    simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
    
                    if ( period >= 9 )
                    {
                        startD = "" + (selYear + 1) + "-" + monthOrder[period] + "-01";
                        endD = "" + (selYear + 1) + "-" + monthOrder[period] + "-" + monthDays[period];
    
                        if ( (selYear + 1) % 4 == 0 && period == 10 )
                        {
                            endD = "" + (selYear + 1) + "-" + monthOrder[period] + "-" + (monthDays[period] + 1);
                        }
                    }
                    else
                    {
                        startD = "" + selYear + "-" + monthOrder[period] + "-01";
                        endD = "" + selYear + "-" + monthOrder[period] + "-" + monthDays[period];
                    }
    
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                    periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                }
                else if ( periodTypeLB.equalsIgnoreCase( QuarterlyPeriodType.NAME ) )
                {
                    if ( period == 0 )
                    {
                        startD = "" + selYear + "-04-01";
                        endD = "" + selYear + "-06-30";
                        periodNames.add( selYear + "-Q1" );
                    }
                    else if ( period == 1 )
                    {
                        startD = "" + selYear + "-07-01";
                        endD = "" + selYear + "-09-30";
                        periodNames.add( selYear + "-Q2" );
                    }
                    else if ( period == 2 )
                    {
                        startD = "" + selYear + "-10-01";
                        endD = "" + selYear + "-12-31";
                        periodNames.add( selYear + "-Q3" );
                    }
                    else
                    {
                        startD = "" + (selYear + 1) + "-01-01";
                        endD = "" + (selYear + 1) + "-03-31";
                        periodNames.add( (selYear) + "-Q4" );
                    }
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                }
                else if ( periodTypeLB.equalsIgnoreCase( SixMonthlyPeriodType.NAME ) )
                {
                    if ( period == 0 )
                    {
                        startD = "" + selYear + "-04-01";
                        endD = "" + selYear + "-09-30";
                        periodNames.add( selYear + "-HY1" );
                    }
                    else
                    {
                        startD = "" + selYear + "-10-01";
                        endD = "" + (selYear + 1) + "-03-31";
                        periodNames.add( selYear + "-HY2" );
                    }
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                }
    
                System.out.println( startD + " : " + endD );
            }
    
        }
         
           List<Indicator> indicatorList = new ArrayList<Indicator>();
           Iterator deIterator = selectedIndicators.iterator();
            while ( deIterator.hasNext() )
            {
                String indicatorId = (String) deIterator.next();
             
                Indicator indicator = indicatorService.getIndicator( indicatorId );                    
                
                indicatorList.add( indicator );
    
            }
    
            selectedServiceList = new ArrayList<Object>( indicatorList );
    
        // OrgUnit Information
    
        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( orgUnit );
        }
    
        // calling individual Function
        if ( categoryLB.equalsIgnoreCase( PERIODWISE ) && ougGroupSetCB == null )
        {
           
            System.out.println( "Inside PeriodWise Chart Data" );
            System.out.println( "\n\nsize of OrgUnit List : " + selOUList.size()+ " , size of Indicator List : " + indicatorList.size() );
            
            indicatorChartResult = generateChartDataPeriodWise( selStartPeriodList,selEndPeriodList, periodNames, indicatorList, selOUList.iterator().next() );
    

    
        }
        else if ( categoryLB.equalsIgnoreCase( CHILDREN ) && ougGroupSetCB == null )
        {
            // System.out.println( "Report Generation Start Time is : \t" + new
            // Date() );
            System.out.println( "Inside ChildWise Chart Data" );
            // generateChartDataWithChildWise();
    
        }
        else if ( categoryLB.equalsIgnoreCase( SELECTED ) && ougGroupSetCB == null )
        {
            // System.out.println( "Report Generation Start Time is : \t" + new
            // Date() );
    
            System.out.println( "Inside SelectedOrgUnit Chart Data" );
            // generateChartDataSelectedOrgUnitWise();
    
        }
    
        else if ( categoryLB.equalsIgnoreCase( PERIODWISE ) && ougGroupSetCB != null )
        {
            // System.out.println( "Report Generation Start Time is : \t" + new
            // Date() );
            System.out.println( "Inside ChildWise With OrgGroup Chart Data" );
            // generateChartDataOrgGroupPeriodWise();
    
        }
    
        else if ( categoryLB.equalsIgnoreCase( CHILDREN ) && ougGroupSetCB != null )
        {
            // System.out.println( "Report Generation Start Time is : \t" + new
            // Date() );
            System.out.println( "Inside ChildWise With OrgGroup Chart Data" );
            // generateChartDataOrgGroupChildWise();
    
        }
    
        else if ( categoryLB.equalsIgnoreCase( SELECTED ) && ougGroupSetCB != null )
        {
            // System.out.println( "Report Generation Start Time is : \t" + new
            // Date() );
    
            System.out.println( "Inside SelectedOrgUnit With OrgGroup Chart Data" );
            // generateChartDataSelectedOrgUnitGroupWise();
    
        } 
        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

        session = req.getSession();
       

        session.setAttribute( "data1", indicatorChartResult.getData() );
        session.setAttribute( "series1", indicatorChartResult.getSeries()) ;
        session.setAttribute( "categories1", indicatorChartResult.getCategories()) ;
        session.setAttribute( "chartTitle", indicatorChartResult.getChartTitle()) ;
        session.setAttribute( "xAxisTitle", indicatorChartResult.getXAxis_Title()) ;
        session.setAttribute( "yAxisTitle", indicatorChartResult.getYAxis_Title()) ;
        
        statementManager.destroy();
        return SUCCESS;
 
    // execute end
    }
    
    
    public  IndicatorChartResult  generateChartDataPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList, List<String> periodNames, List<Indicator> indicatorList,OrganisationUnit orgUnit )
        throws Exception
      {
        
        System.out.println("inside function defination");
        IndicatorChartResult  indicatorChartResult ;
    
        String[] series = new String[indicatorList.size()];
        String[] categories = new String[selStartPeriodList.size()];
        Double[][] data = new Double[indicatorList.size()][selStartPeriodList.size()];
        String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
        String xAxis_Title = "Time Line";
        String yAxis_Title = "Value";
    
    
        int serviceCount = 0;
        for (  Indicator indicator: indicatorList)  
        {
            int periodCount = 0;
            for ( Date startDate : selStartPeriodList )
            {
                Date endDate = selEndPeriodList.get( periodCount );
    
                categories[periodCount] = periodNames.get( periodCount );
    
                Double aggIndicatorValue = 0.0;
                if ( aggDataCB != null )
                {
                   
                    aggIndicatorValue = aggregationService.getAggregatedIndicatorValue( indicator,  startDate, endDate, orgUnit );
                    if(aggIndicatorValue == null ) aggIndicatorValue = 0.0;
                    
                }
                else
                {
                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                    Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
    
                    for ( Period period : periods )
                    {
                       aggIndicatorValue = dashBoardService.getIndividualIndicatorValue(indicator, orgUnit,  period );
                       
                     }
                  }
                data[serviceCount][periodCount] = aggIndicatorValue;
                periodCount++;
            }
            serviceCount++;
    }
        indicatorChartResult = new IndicatorChartResult( series,categories,data,chartTitle,xAxis_Title,yAxis_Title);
        
        return indicatorChartResult;
     
}



}      