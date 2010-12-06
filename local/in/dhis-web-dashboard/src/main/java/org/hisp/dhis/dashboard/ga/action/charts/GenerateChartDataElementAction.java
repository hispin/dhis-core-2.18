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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amplecode.quick.StatementManager;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dashboard.util.DataElementChartResult;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateChartDataElementAction.java Oct 25, 2010 12:20:22 PM
 */
public class GenerateChartDataElementAction implements Action
{
   
    private final String PERIODWISE = "period";

    private final String CHILDREN = "children";

    private final String SELECTED = "random";
    
    private final String OPTIONCOMBO = "optioncombo";
    
   // private final String ORGUNITGROUP = "orgUnitSelectedRadio";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
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
    
    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
/*    
    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }
*/ 
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
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
    // --------------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------------
    
    private HttpSession session;

    public HttpSession getSession()
    {
        return session;
    }
    
    private List<DataElementCategoryOptionCombo> selectedOptionComboList;
    
    private List<Object> selectedServiceList;

    public List<Object> getSelectedServiceList()
    {
        return selectedServiceList;
    }
    
    private List<OrganisationUnit> selOUList;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
/*    
    private List<String> selectedDataElements;
    
    public void setSelectedDataElements( List<String> selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }
*/
    private List<String> selectedDataElements;
    

    public void setSelectedDataElements( List<String> selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }

    private String deSelection;
    
    public void setDeSelection( String deSelection )
    {
        this.deSelection = deSelection;
    }
    
/*
    private List<String> ougGroupSetCB;
    
    public List<String> getOugGroupSetCB()
    {
        return ougGroupSetCB;
    }

    public void setOugGroupSetCB( List<String> ougGroupSetCB )
    {
        this.ougGroupSetCB = ougGroupSetCB;
    }
*/
     
    private String ougGroupSetCB;
    
    public void setOugGroupSetCB( String ougGroupSetCB )
    {
        this.ougGroupSetCB = ougGroupSetCB;
    }
    
    public String getOugGroupSetCB()
    {
        return ougGroupSetCB;
    }
   
    private List<String> orgUnitGroupList;
    

    public List<String> getOrgUnitGroupList()
    {
        return orgUnitGroupList;
    }

    public void setOrgUnitGroupList( List<String> orgUnitGroupList )
    {
        this.orgUnitGroupList = orgUnitGroupList;
    }

    private String aggDataCB;
    
    public void setAggDataCB( String aggDataCB )
    {
        this.aggDataCB = aggDataCB;
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
    
    private  List<String> periodNames;
    
    private List<Date> selStartPeriodList;

    private List<Date> selEndPeriodList;
    
    private DataElementChartResult dataElementChartResult;
    
    public DataElementChartResult getDataElementChartResult()
    {
        return dataElementChartResult;
    }
    
    private OrganisationUnit selectedOrgUnit;
    
    private OrganisationUnitGroup selOrgUnitGroup;
    
    private List<OrganisationUnit> selOUGroupMemberList = new ArrayList<OrganisationUnit>();
    //public List<OrganisationUnit> orgUnit;
    
//    public List<DataElementCategoryOptionCombo>  decoc ;

/*    
    String chartTitle ;

    public String getChartTitle()
    {
        return chartTitle;
    }

    String xAxis_Title;

    public String getXAxis_Title()
    {
        return xAxis_Title;
    }

    String yAxis_Title;

    public String getYAxis_Title()
    {
        return yAxis_Title;
    }
*/    
    
    List<String> yseriesList;

    public List<String> getYseriesList()
    {
        return yseriesList;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    



    @SuppressWarnings( "unchecked" )
    public String execute()  throws Exception
    {
        statementManager.initialise();
        selectedOptionComboList = new ArrayList<DataElementCategoryOptionCombo>();
        
        selOUList = new ArrayList<OrganisationUnit>();
        System.out.println( "selected orgUnit  size : " + orgUnitListCB.size() );
        
        System.out.println( "selected Year  size : " + yearLB.size());
        
      //  System.out.println( "selected Period  size : " + periodLB.size());
        
        System.out.println( "selected dataelements : " + selectedDataElements);
        
        System.out.println( "selected dataelements size : " + selectedDataElements.size());
        
        // int flag = 0;
        //  selOUList = new ArrayList<OrganisationUnit>();
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();
        
        yseriesList = new ArrayList<String>();
      //  DataElement dElement = new DataElement();
        
       // DataElementCategoryOptionCombo decoc1 = new DataElementCategoryOptionCombo();
       // int countForServiceList = 0;
        
      //  ouChildCountMap = new HashMap<OrganisationUnit, Integer>();

       // String monthOrder[] = { "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03" };
       // int monthDays[] = { 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31 };

        String monthOrder[] = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        
        /* Period Info */

        String startD = "";
        String endD = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        periodNames = new ArrayList<String>();
        
        // for weekly period
        if ( periodTypeLB.equalsIgnoreCase( WeeklyPeriodType.NAME ) )
        {
           // System.out.println( " Inside  weekly" );
            for ( String periodStr : periodLB )
            {
                String  startWeekDate = periodStr.split( "To" )[0] ; //for start week
                String  endWeekDate = periodStr.split( "To" )[1] ; //for end week
                
                startD = startWeekDate.trim();
                endD = endWeekDate.trim();
                
                selStartPeriodList.add( format.parseDate( startD ) );
                selEndPeriodList.add( format.parseDate( endD ) );
                
                periodNames.add( periodStr );
                //System.out.println( startD + " : " + endD );
            }
        }
        else
        {
            for ( String year : yearLB )
            {
                //int selYear = Integer.parseInt( year.split( "-" )[0] );
                int selYear = Integer.parseInt( year );
            
                if ( periodTypeLB.equalsIgnoreCase( YearlyPeriodType.NAME ) )
                {
                    startD = "" + selYear + "-01-01";
                    endD = "" + selYear  + "-12-31";
                    
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
            
                   // periodNames.add( "" + selYear + "-" + (selYear + 1) );
                    
                    periodNames.add( "" + selYear );
                   // System.out.println( "Start Date : " + startD + " , End Date : " + endD );
                    continue;
                    
                }
               
                for ( String periodStr : periodLB )
                {
                   // int period = Integer.parseInt( periodStr );
            
                    if ( periodTypeLB.equalsIgnoreCase( MonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
                        
                        startD = "" + selYear + "-" + monthOrder[period] + "-01";
                        endD = "" + selYear  + "-" + monthOrder[period] + "-" + monthDays[period];
                       
                        //check for leapYear
                        if ( ((( selYear ) % 400 == 0 ) || ((( selYear) % 100 != 0 && ( selYear ) % 4 == 0))) && period == 1 )
                        {
                            endD = "" + selYear  + "-" + monthOrder[period] + "-" + ( monthDays[period] + 1 );
                        } 
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                        periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                       // System.out.println( "Start Date : " + startD + " , End Date : " + endD );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( QuarterlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-03-31";
                            periodNames.add( selYear + "-Q1" );
                        }
                        else if ( period == 1 )
                        {
                            startD = "" + selYear + "-04-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-Q2" );
                        }
                        else if ( period == 2 )
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-09-30";
                            periodNames.add( selYear + "-Q3" );
                        }
                        else
                        {
                            startD = "" + selYear + "-10-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( (selYear) + "-Q4" );
                        }
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                       // System.out.println( "Start Date : " + startD + " , End Date : " + endD );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( SixMonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-HY1" );
                        }
                        else
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( selYear + "-HY2" );
                        }
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                    }
                    
                    else if ( periodTypeLB.equalsIgnoreCase( DailyPeriodType.NAME ) )
                    {
                       String  month = periodStr.split( "-" )[0] ;
                       String  date = periodStr.split( "-" )[1] ;
                       
                       startD = selYear + "-" + periodStr;
                       endD = selYear + "-" + periodStr ;
                       
                       if ( selYear  % 4 != 0 && month.trim().equalsIgnoreCase( "02" )  && date.trim().equalsIgnoreCase( "29" ) )
                        {
                            startD = selYear + "-" + month + "-" + date;
                            endD = selYear + "-" + month + "-" + date;
                            continue;
                        }
                        
                       if ( (( selYear % 400 == 0) || (( selYear % 100 != 0 && selYear % 4 == 0))) && month.trim().equalsIgnoreCase( "02" )  && date.trim().equalsIgnoreCase( "29" ) ); 
                        {
                            startD = selYear + "-" + month + "-" + date;
                            endD = selYear  + "-" + month + "-" + date;
                        }
                       
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                        
                        periodNames.add( startD );
                       // System.out.println( startD + " : " + endD );
                    }
                    System.out.println( startD + " : " + endD );
                }
   
            }
      }    
        
        // DataElement Information        
        
        List<DataElement> dataElementList = new ArrayList<DataElement>();
        
        if ( deSelection == null )
        {
            System.out.println( "deOptionValue is null" );
            return null;
        }
        else
        {
            //System.out.println( "deOptionValue : " + deSelection );
        }
        
        System.out.println("\n\n\n ===== \n deSelection: " + deSelection);
        
       
        
        if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
        { 
           // System.out.println("\n\n\n ===== \n deSelection: OK ");
            Iterator deIterator = selectedDataElements.iterator();
            
          //  dElement = (DataElement) deIterator.next();
            
            while ( deIterator.hasNext() )
            {
                String serviceId = (String) deIterator.next();
                String partsOfServiceId[] = serviceId.split( ":" );
                int dataElementId = Integer.parseInt( partsOfServiceId[0] );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                // selectedServiceList.add( dataElement );
                dataElementList.add( dataElement );
                int optionComboId = Integer.parseInt( partsOfServiceId[1] );
                DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDataElementCategoryOptionCombo( optionComboId );
                selectedOptionComboList.add( decoc );
              //  chartTitle += dataElement.getName() + " : " + dataElementCategoryService.getDataElementCategoryOptionCombo( decoc ).getName() + ", ";
              /*  
                decoc1 = selectedOptionComboList.get( countForServiceList );
                yseriesList.add( dElement.getName() + " : " + dataElementCategoryService.getDataElementCategoryOptionCombo( decoc1 ).getName() );
                System.out.println( "Data Elenent name is  : " + dElement.getName() + " categoryOptionCombo is :  " + dataElementCategoryService.getDataElementCategoryOptionCombo( decoc1 ).getName() );
            */
            }
        }
        else
        {
            Iterator deIterator = selectedDataElements.iterator();
            while ( deIterator.hasNext() )
            {
                int serviceID = Integer.parseInt( (String) deIterator.next() );
                DataElement dataElement = dataElementService.getDataElement( serviceID );
                // selectedServiceList.add( dataElement );
                dataElementList.add( dataElement );
               // chartTitle += dataElement.getName() + ", ";
             /*   yseriesList.add( dElement.getName() );
                System.out.println( "Data Elenent name is  : " + dElement.getName() );*/
            }
            
        }
       
        //Collections.sort( dataElementList, dataElementComparator );
        selectedServiceList = new ArrayList<Object>( dataElementList );
        
        // OrgUnit Information              
        
        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( orgUnit );
        }

        Calendar now = Calendar.getInstance();//for mili seconds
        
        // calling individual Function 
        if ( categoryLB.equalsIgnoreCase( PERIODWISE )&& ougGroupSetCB == null )
        {
            System.out.println( "Inside PeriodWise Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );
           
            
            dataElementChartResult = generateChartDataPeriodWise( selStartPeriodList,selEndPeriodList,periodNames,dataElementList,selectedOptionComboList,selOUList.iterator().next() );
           /* 
            ActionContext ctx = ActionContext.getContext();
            HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

            session = req.getSession();
            /*
            #foreach( )
                $dataElementChartResult.data[$count1][$count2]
            #end
            *//*
            Integer i = dataElementChartResult.getCategories().length;
            System.out.println( "\n Categories Length is " + i );
            
            Integer j = dataElementChartResult.getSeries().length;
            System.out.println( "\n Series Length is " + j );
            
            session.setAttribute( "data1", dataElementChartResult.getData() );
            session.setAttribute( "series1", dataElementChartResult.getSeries() );
            session.setAttribute( "categories1", dataElementChartResult.getCategories() );
            session.setAttribute( "chartTitle", dataElementChartResult.getChartTitle() );
            session.setAttribute( "xAxisTitle", dataElementChartResult.getXAxis_Title() );
            session.setAttribute( "yAxisTitle", dataElementChartResult.getYAxis_Title() );
           */ 

        }
        else if ( categoryLB.equalsIgnoreCase( CHILDREN ) && ougGroupSetCB == null )
        {
            System.out.println( "Inside Child Wise Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );
            
            selectedOrgUnit = new OrganisationUnit();
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            
            List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
            childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren());
            
            
            dataElementChartResult = generateChartDataWithChildrenWise( selStartPeriodList,selEndPeriodList,periodNames,dataElementList,selectedOptionComboList,childOrgUnitList );
  
        }
        else if ( categoryLB.equalsIgnoreCase( SELECTED ) && ougGroupSetCB == null )
        {
            
            System.out.println( "Inside Selected OrgUnit Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );
            
            dataElementChartResult = generateChartDataSelectedOrgUnitWise( selStartPeriodList,selEndPeriodList,periodNames,dataElementList,selectedOptionComboList,selOUList );
          
        }
        
        else if ( categoryLB.equalsIgnoreCase( PERIODWISE ) && ougGroupSetCB != null )
        {
            System.out.println( "Inside Period dWise With OrgGroup Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() + "Mili seconds is : " + now.getTimeInMillis() );
          
            //finding the common list of selected org unit and selected Group
            
            //  selOUList = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
           
            System.out.println( "oug Group Set is  = " + orgUnitGroupList );
            
            selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupList.get( 0 ) ) );
           // String selOrgGroupname = selOrgUnitGroup.getName();
            selOUGroupMemberList = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );
            
           // System.out.println( "Report Generation Start Time is : \t" + new Date() + "Mili seconds is : " + now.getTimeInMillis() );
           
           // System.out.println( "Size of Group member is before retain : " + selOUGroupMemberList.size() );
            
            selOUGroupMemberList.retainAll( orgUnitChildList );
            
           // orgUnitChildList.retainAll( selOUGroupMemberList );
           /* 
            System.out.println( "Size of Group member is after retain : " + selOUGroupMemberList.size() );
            
            System.out.println( "Report Generation End Time is : \t" + new Date()+ "Mili seconds is : " + now.getTimeInMillis() );
            
            System.out.println( "OruUnit: " + selectedOrgUnit.getName() + "Group Name is : " + selOrgUnitGroup.getName() + "Size of Group member after retain is : " + selOUGroupMemberList.size() );
            
            System.out.println( "Inside PeriodWise With OrgGroup Chart Data" );
            */
          
           // dataElementChartResult = generateChartDataOrgGroupPeriodWise(selStartPeriodList,selEndPeriodList,periodNames,dataElementList,selectedOptionComboList,selectedOrgUnit,selOrgUnitGroup,selOUGroupMemberList);
            dataElementChartResult = generateChartDataOrgGroupPeriodWise(selStartPeriodList,selEndPeriodList,periodNames,dataElementList,selectedOptionComboList,selOUGroupMemberList);
 
        }
        
        else if ( categoryLB.equalsIgnoreCase( CHILDREN ) && ougGroupSetCB != null )
        {
            System.out.println( "Inside ChildWise With OrgGroup Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );
            
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
            
            int groupCount = 0;
           // System.out.println("\n\n ++++++++++++++++++++++ \n orgUnitGroup : " + orgUnitGroupList );
            for ( String orgUnitGroupId : orgUnitGroupList )
            {
                OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupId ) );
               
                List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );
                
                selOUGroupMemberList.addAll( selectedOUGroupMemberList );

               // System.out.println( "Total Size of " + groupCount + " : " + selOUGroupMemberList.size()  );

                groupCount++;
    
            }
            
          //  System.out.println( "Total Size of " + selOUGroupMemberList.size()  );

           // System.out.println( "Report Generation Start Time is : \t" + new Date() + "Mili seconds is : " + now.getTimeInMillis() );
            
           // System.out.println( "Size of Group member is before retain : " + selOUGroupMemberList.size() + " ,Size of Child Member is :" + orgUnitChildList.size() );
            
            selOUGroupMemberList.retainAll( orgUnitChildList );
            
           // System.out.println( "Report Generation End Time is : \t" + new Date()+ "Mili seconds is : " + now.getTimeInMillis() );
            
           // System.out.println( "OruUnit: " + selectedOrgUnit.getName() + " Group Size is : " + orgUnitGroupList.size() + " ,Size of Group member after retain is : " + selOUGroupMemberList.size() );
            // generateChartDataOrgGroupChildWise();
            
            // calling sane function in case of SelectedOrgUnit Chart Data
            dataElementChartResult = generateChartDataSelectedOrgUnitWise( selStartPeriodList,selEndPeriodList,periodNames,dataElementList,selectedOptionComboList,selOUGroupMemberList );
            

        }
        
        else if ( categoryLB.equalsIgnoreCase( SELECTED ) && ougGroupSetCB != null )
        {
            System.out.println( "Inside Selected With OrgGroup Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );
            
            Map<OrganisationUnitGroup,List<OrganisationUnit>> orgUnitGroupMap = new HashMap<OrganisationUnitGroup,List<OrganisationUnit>>();
           
            System.out.println( "Inside SelectedOrgUnit With OrgGroup Chart Data" );
           
           selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
           List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
           
           for ( String orgUnitGroupId : orgUnitGroupList )
           {
               OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupId ) );
               List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );
               
               selectedOUGroupMemberList.retainAll( orgUnitChildList );
               
               orgUnitGroupMap.put( selOrgUnitGroup,  selectedOUGroupMemberList);
               //selOUGroupMemberList.addAll( selectedOUGroupMemberList );
           }
           
          // System.out.println( "selOrgUnitGroup Size is : " + orgUnitGroupMap.keySet().size()  );
          
           dataElementChartResult = generateChartDataSelectedOrgUnitGroupWise( selStartPeriodList,selEndPeriodList,periodNames,dataElementList,selectedOptionComboList,orgUnitGroupMap );

        }
       
        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

        session = req.getSession();
        
        session.setAttribute( "data1", dataElementChartResult.getData() );
        session.setAttribute( "series1", dataElementChartResult.getSeries() );
        session.setAttribute( "categories1", dataElementChartResult.getCategories() );
        session.setAttribute( "chartTitle", dataElementChartResult.getChartTitle() );
        session.setAttribute( "xAxisTitle", dataElementChartResult.getXAxis_Title() );
        session.setAttribute( "yAxisTitle", dataElementChartResult.getYAxis_Title() );
        
        System.out.println( "Chart Generation End Time is : \t" + new Date() );
        statementManager.destroy();
        return SUCCESS;
    }
    // execute end
    
    // Supporting Methods
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise start
    // -------------------------------------------------------------------------
    
   
    public DataElementChartResult generateChartDataPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,List<DataElement> dataElementList,List<DataElementCategoryOptionCombo> decocList,OrganisationUnit orgUnit ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[selStartPeriodList.size()];
       Double[][] data = new Double[dataElementList.size()][selStartPeriodList.size()];
       String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
       String xAxis_Title = "Time Line";
       String yAxis_Title = "Value";
    
      // System.out.println("\n\n +++ \n decoc : " + decocList);
       
       int serviceCount = 0;     
     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
          /*
           if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) && dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
           {
               System.out.println( " inside value type : - INT and Number Type : - INT"  );
               System.out.println( " DataElementName : "+ dataElement.getName() );
           }
           if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) && !dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
           {
               System.out.println( " inside value type : - INT and Number Type : - other than INT"  );
               System.out.println( " DataElementName : "+ dataElement.getName() );
           }
           */
           // if( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
          // {
             // System.out.println( " DataElement VALUE_TYPE_INT  is : " + DataElement.VALUE_TYPE_INT );
          // }
           //else
          // {
              // System.out.println( " DataElementName : "+ dataElement.getName() +" , DataElement Value Type  is : " + dataElement.getType() + ", DataElement Number Type  is : " + dataElement.getNumberType() );
          // }
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           
          // if( dataElement.isMultiDimensional() )               
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               
               yseriesList.add( dataElement.getName() );
           }
           
           int periodCount = 0;
           for( Date startDate : selStartPeriodList )
           {
               Date endDate = selEndPeriodList.get( periodCount );
                              
               categories[periodCount] = periodNames.get( periodCount );
               
               Double aggDataValue = 0.0;
               if( aggDataCB != null )
               {
                   aggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, orgUnit );
                   //System.out.println( "Agg data value before is  : " + aggDataValue );
                   if(aggDataValue == null ) aggDataValue = 0.0;
                   //System.out.println( "Agg data value after zero assign is  : " + aggDataValue );
               }
               else
               {
                   PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
                   
                   for( Period period : periods )
                   {
                       DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc );
                       
                       try
                       {
                           aggDataValue += Double.parseDouble( dataValue.getValue() );
                       }
                       catch( Exception e )
                       {
                           
                       }
                   }
               }
               
               data[serviceCount][periodCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               periodCount++;
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       return dataElementChartResult;
    }
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise end
    // -------------------------------------------------------------------------
    
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise start
    // -------------------------------------------------------------------------
        
    public DataElementChartResult generateChartDataWithChildrenWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,List<DataElement> dataElementList,List<DataElementCategoryOptionCombo> decocList,List<OrganisationUnit> childOrgUnitList ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[childOrgUnitList.size()];
       Double[][] data = new Double[dataElementList.size()][childOrgUnitList.size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
       
      // String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
       String xAxis_Title = "Facilities";
       String yAxis_Title = "Value";
    
      // System.out.println("\n\n +++ \n decoc : " + decocList);
       
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           
          // if( dataElement.isMultiDimensional() )               
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               
               yseriesList.add( dataElement.getName() );
           }
           
           int childCount = 0;
           for( OrganisationUnit orgChild : childOrgUnitList )
           {
               categories[childCount] = orgChild.getName();
               
               Double aggDataValue = 0.0;

               int periodCount = 0;
               for( Date startDate : selStartPeriodList )
               {
                   Date endDate = selEndPeriodList.get( periodCount );
                   PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
                   
                   for( Period period : periods )
                   {
                       if( aggDataCB != null )
                       {
                           Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, period.getStartDate(), period.getEndDate(), orgChild );
                          // System.out.println( "Agg data value before is  : " + aggDataValue );
                          
                           if(tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                          // System.out.println( "Agg data value after zero assign is  : " + aggDataValue );
                       }
                       else
                       {
                           DataValue dataValue = dataValueService.getDataValue( orgChild, dataElement, period, decoc );
                           
                           try
                           {
                               aggDataValue += Double.parseDouble( dataValue.getValue() );
                           }
                           catch( Exception e )
                           {
                               
                           }
                       }
                   }
                   periodCount++;
               }
 
               data[serviceCount][childCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               childCount++;
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       return dataElementChartResult;
    }
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise end
    // -------------------------------------------------------------------------
    
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Selected Wise start
    // -------------------------------------------------------------------------
        
    public DataElementChartResult generateChartDataSelectedOrgUnitWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,List<DataElement> dataElementList,List<DataElementCategoryOptionCombo> decocList,List<OrganisationUnit> selOUList ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[selOUList.size()];
       Double[][] data = new Double[dataElementList.size()][selOUList.size()];
       String chartTitle = "OrganisationUnit : -----" ;
       
      // String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
       String xAxis_Title = "Facilities";
       String yAxis_Title = "Value";
    
      // System.out.println("\n\n +++ \n decoc : " + decocList);
       
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           
          // if( dataElement.isMultiDimensional() )               
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               
               yseriesList.add( dataElement.getName() );
           }
           
           int orgUnitCount = 0;
           for( OrganisationUnit orgunit : selOUList )
           {             
               categories[orgUnitCount] = orgunit.getName();
               
               Double aggDataValue = 0.0;

               int periodCount = 0;
               for( Date startDate : selStartPeriodList )
               {
                   Date endDate = selEndPeriodList.get( periodCount );
                   PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
                   
                   for( Period period : periods )
                   {
                       if( aggDataCB != null )
                       {
                           Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, period.getStartDate(), period.getEndDate(), orgunit );
                           //System.out.println( "Agg data value before is  : " + aggDataValue );
                          
                           if(tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                          // System.out.println( "Agg data value after zero assign is  : " + aggDataValue );
                       }
                       else
                       {
                           DataValue dataValue = dataValueService.getDataValue( orgunit, dataElement, period, decoc );
                           
                           try
                           {
                               aggDataValue += Double.parseDouble( dataValue.getValue() );
                           }
                           catch( Exception e )
                           {
                               
                           }
                       }
                   }
                   periodCount++;
               }
 
               data[serviceCount][orgUnitCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][orgUnitCount] = Math.round( data[serviceCount][orgUnitCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][orgUnitCount] = Math.round( data[serviceCount][orgUnitCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               orgUnitCount++;
             
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       return dataElementChartResult;
    }
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Selected Wise end 
    // -------------------------------------------------------------------------
        
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise start
    // -------------------------------------------------------------------------
       
    public DataElementChartResult generateChartDataOrgGroupPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,List<DataElement> dataElementList,List<DataElementCategoryOptionCombo> decocList,List<OrganisationUnit> selOUGroupMemberList ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[selStartPeriodList.size()];
       Double[][] data = new Double[dataElementList.size()][selStartPeriodList.size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName()+ "(" + selOrgUnitGroup.getName() +  ")";
       String xAxis_Title = "Time Line";
       String yAxis_Title = "Value";
       
       
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           
          // if( dataElement.isMultiDimensional() )               
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               
               yseriesList.add( dataElement.getName() );
           }
               
               Double aggDataValue = 0.0;
              
               int periodCount = 0;
               for( Date startDate : selStartPeriodList )
               {
                   Date endDate = selEndPeriodList.get( periodCount );
                   categories[periodCount] = periodNames.get( periodCount );
                   
                   PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
  
                   for( Period period : periods )
                   {
                       int orgGroupCount = 0;
                       
                       for( OrganisationUnit orgUnit : selOUGroupMemberList )
                       {
                           
                           if( aggDataCB != null )
                           {
                               Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, period.getStartDate(), period.getEndDate(), orgUnit );
                              // System.out.println( "Agg data value before is  : " + aggDataValue );
                              
                               if(tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                              // System.out.println( "Agg data value after zero assign is  : " + aggDataValue );
                           }
                       else
                       {
                           DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc );
                           
                           try
                           {
                               aggDataValue += Double.parseDouble( dataValue.getValue() );
                           }
                           catch( Exception e )
                           {
                               
                           }
                       }
                          orgGroupCount++;
                   }
                   
               }
   
               data[serviceCount][periodCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               periodCount++;    
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       
      return dataElementChartResult;
    
   }
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise end 
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Selected orgUnit Wise start
    // -------------------------------------------------------------------------
       
    public DataElementChartResult generateChartDataSelectedOrgUnitGroupWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,List<DataElement> dataElementList,List<DataElementCategoryOptionCombo> decocList,Map<OrganisationUnitGroup,List<OrganisationUnit>> orgUnitGroupMap ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[orgUnitGroupMap.keySet().size()];
       Double[][] data = new Double[dataElementList.size()][orgUnitGroupMap.keySet().size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName() +" - ";
       String xAxis_Title = "Organisation Unit Group";
       String yAxis_Title = "Value";
       
       
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           
          // if( dataElement.isMultiDimensional() )               
           {
               decoc = decocList.get( serviceCount );
                   
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
               yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
               
               yseriesList.add( dataElement.getName() );
           }
           
                       int orgGroupCount = 0;
                      // int orgGroupCount1 = 0;
                      // Double aggDataValue = 0.0;
                       
                       for( OrganisationUnitGroup orgUnitGroup : orgUnitGroupMap.keySet() )
                       {
                          // Double aggDataValue = 0.0;
                           Double aggDataValue = 0.0;
                           
                           categories[orgGroupCount] = orgUnitGroup.getName();
                           
                           if( serviceCount == 0 )
                           {
                               chartTitle += orgUnitGroup.getName() + ",";
                           }
                           Collection<OrganisationUnit> orgUnitGroupMembers = orgUnitGroupMap.get( orgUnitGroup );
                           if( orgUnitGroupMembers == null || orgUnitGroupMembers.size() == 0 )
                           {
                               data[serviceCount][orgGroupCount] = aggDataValue;
                               orgGroupCount++;
                               continue;
                           }
                           for( OrganisationUnit orgUnit : orgUnitGroupMembers )
                           {
                               
                               int periodCount = 0;
                               for( Date startDate : selStartPeriodList )
                               {
                                   Date endDate = selEndPeriodList.get( periodCount );
                                 
                                   PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                   Collection<Period> periods = periodService.getPeriodsBetweenDates( periodType, startDate, endDate );
                                                                  
                                      for( Period period : periods )
                                      {
                                           if( aggDataCB != null )
                                           {
                                               Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, period.getStartDate(), period.getEndDate(), orgUnit );
                                             //  System.out.println( "Agg data value before is  : " + aggDataValue );
                                              
                                               if(tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                                              // System.out.println( "Agg data value after zero assign is  : " + aggDataValue );
                                           }
                                           else
                                           {
                                               DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc );
                                               
                                               try
                                               {
                                                   aggDataValue += Double.parseDouble( dataValue.getValue() );
                                               }
                                               catch( Exception e )
                                               {
                                                   
                                               }
                                           }
                                      }
                          periodCount++;  
                 }  
               }
               data[serviceCount][orgGroupCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][orgGroupCount] = Math.round( data[serviceCount][orgGroupCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][orgGroupCount] = Math.round( data[serviceCount][orgGroupCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
              
               orgGroupCount++;
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       
      return dataElementChartResult;
    
   }
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Selected orgUnit Wise end
    // -------------------------------------------------------------------------        

}
