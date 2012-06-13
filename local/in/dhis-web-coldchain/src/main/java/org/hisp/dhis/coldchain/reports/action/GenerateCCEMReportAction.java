package org.hisp.dhis.coldchain.reports.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.write.Label;

import org.hisp.dhis.coldchain.reports.CCEMReport;
import org.hisp.dhis.coldchain.reports.CCEMReportDesign;
import org.hisp.dhis.coldchain.reports.CCEMReportManager;
import org.hisp.dhis.coldchain.reports.CCEMReportOutput;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class GenerateCCEMReportAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CCEMReportManager ccemReportManager;
    
    public void setCcemReportManager( CCEMReportManager ccemReportManager )
    {
        this.ccemReportManager = ccemReportManager;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
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
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String selReportId;
    
    public void setSelReportId( String selReportId )
    {
        this.selReportId = selReportId;
    }

    private List<Integer> selOrgUnitList;

    public void setSelOrgUnitList( List<Integer> selOrgUnitList )
    {
        this.selOrgUnitList = selOrgUnitList;
    }

    private List<Integer> orgunitGroupList;
    
    public void setOrgunitGroupList( List<Integer> orgunitGroupList )
    {
        this.orgunitGroupList = orgunitGroupList;
    }

    private CCEMReport ccemReport;
    
    public CCEMReport getCcemReport()
    {
        return ccemReport;
    }

    private CCEMReportOutput ccemReportOutput;
    
    public CCEMReportOutput getCcemReportOutput()
    {
        return ccemReportOutput;
    }

    private String periodRadio;
    
    public void setPeriodRadio( String periodRadio )
    {
        this.periodRadio = periodRadio;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        String orgUnitIdsByComma = ccemReportManager.getOrgunitIdsByComma( selOrgUnitList, orgunitGroupList );
        
        ccemReport = ccemReportManager.getCCEMReportByReportId( selReportId );
        
        Map<String, String> ccemSettingsMap = new HashMap<String, String>( ccemReportManager.getCCEMSettings() );
        
        List<CCEMReportDesign> reportDesignList = new ArrayList<CCEMReportDesign>( ccemReportManager.getCCEMReportDesign( ccemReport.getXmlTemplateName() ) );
        
        if( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Map<String, Integer> catalogTypeAttributeValueMap = new HashMap<String, Integer>( ccemReportManager.getCatalogTypeAttributeValue( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId ) );            
        
            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableData = new ArrayList<List<String>>();
            List<String> oneTableRowData = new ArrayList<String>();
            for( String catalogTypeAttributeValueKey : catalogTypeAttributeValueMap.keySet() )
            {
                tableHeadings.add( catalogTypeAttributeValueKey );
                oneTableRowData.add( ""+catalogTypeAttributeValueMap.get( catalogTypeAttributeValueKey ) );                
            }
            
            tableData.add( oneTableRowData );
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );            
        }
        else if( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP ) )
        {

            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            Map<String, Integer> catalogTypeAttributeValueMap = new HashMap<String, Integer>( ccemReportManager.getCatalogTypeAttributeValue( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId ) );            

            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableData = new ArrayList<List<String>>();
            
            
            List<Map<String, Integer>> outPutMap = new ArrayList<Map<String, Integer>>();
            
            tableHeadings.add( "Model Name" );
            tableHeadings.add( "Total #" );
            
            int i = 0;
            for( CCEMReportDesign ccemReportDesign1 :  reportDesignList )
            {
                i++;
                if( i == 1 ) continue;
                
                
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                if( ccemCellContent1.split( ":" )[3].equalsIgnoreCase( "UNKNOWN" ))
                {
                    tableHeadings.add( "Unknown" );
                    tableHeadings.add( "%" );
                }
                else if( ccemCellContent1.split( ":" )[4].equalsIgnoreCase( "MORE" ) )
                {
                    inventoryTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    catalogTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    Integer inventoryTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                    Integer ageStart = Integer.parseInt( ccemCellContent1.split( ":" )[3] );
                    Integer ageEnd = -1;

                    tableHeadings.add( ">"+(ageStart-1)+" Yrs" );
                    tableHeadings.add( "%" );
                    
                    Map<String, Integer> catalogTypeAttributeValueMap1 = new HashMap<String, Integer>( ccemReportManager.getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId, inventoryTypeAttributeId, ageStart, ageEnd ) );
                    outPutMap.add( catalogTypeAttributeValueMap1 );
                }
                else
                {
                    inventoryTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    catalogTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    Integer inventoryTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                    Integer ageStart = Integer.parseInt( ccemCellContent1.split( ":" )[3] );
                    Integer ageEnd = Integer.parseInt( ccemCellContent1.split( ":" )[4] );
                    
                    tableHeadings.add( ageStart+"-"+ageEnd+" Yrs" );
                    tableHeadings.add( "%" );
                    
                    Map<String, Integer> catalogTypeAttributeValueMap1 = new HashMap<String, Integer>( ccemReportManager.getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId, catalogTypeAttributeId, inventoryTypeAttributeId, ageStart, ageEnd ) );
                    outPutMap.add( catalogTypeAttributeValueMap1 );
                }
            }
            
            Map<Integer, Integer> grandTotal = new HashMap<Integer, Integer>();
            Integer temp = 0;
            for( String modelName : catalogTypeAttributeValueMap.keySet() )
            {
                List<String> oneTableRowData = new ArrayList<String>();
                oneTableRowData.add( modelName );
                
                Integer modelNameTotalCount = catalogTypeAttributeValueMap.get( modelName );
                
                if( modelNameTotalCount == null )
                    modelNameTotalCount = 0;
                
                oneTableRowData.add( ""+modelNameTotalCount );
                
                Integer temp1 = grandTotal.get( 0 );
                if( temp1 == null )
                {
                    grandTotal.put( 0, modelNameTotalCount );
                }
                else
                {
                    grandTotal.put( 0, temp1+modelNameTotalCount );
                }
                
                Integer unknownCount = 0;
                int rowNo = 1;
                for( Map<String, Integer> tempMap : outPutMap )
                {
                    temp = tempMap.get( modelName );
                    if( temp == null )
                        temp = 0;
                    
                    oneTableRowData.add( ""+temp );
                    try
                    {
                        double tempD =  ( (double) temp/modelNameTotalCount)*100.0;
                        tempD = Math.round( tempD * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                        oneTableRowData.add( ""+tempD  );
                    }
                    catch( Exception e )
                    {
                        oneTableRowData.add( ""+0 );
                    }
                    
                    temp1 = grandTotal.get( rowNo );
                    if( temp1 == null )
                    {
                        grandTotal.put( rowNo, temp );
                    }
                    else
                    {
                        grandTotal.put( rowNo, temp1+temp );
                    }
                    
                    unknownCount += temp;
                    rowNo++;
                }
                
                oneTableRowData.add( ""+(modelNameTotalCount-unknownCount) );

                try
                {
                    double tempD =  ((modelNameTotalCount-unknownCount)/ (double) modelNameTotalCount)*100.0;
                    
                    tempD = Math.round( tempD * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                    
                    oneTableRowData.add( ""+tempD  );
                }
                catch( Exception e )
                {
                    oneTableRowData.add( ""+0 );
                }
                
                tableData.add( oneTableRowData );
            }
            
            List<String> oneTableRowData = new ArrayList<String>();
            
            oneTableRowData.add( "Total" );
            Integer totalCount = grandTotal.get( 0 );
            if( totalCount == null )
                totalCount = 0;
            oneTableRowData.add( ""+totalCount );
            
            Integer grandTotalOfUnknown = 0; 
            for( i = 1; i < grandTotal.size(); i++ )
            {
                temp = grandTotal.get( i );
                
                if( temp == null )
                {
                    temp = 0;
                }
                oneTableRowData.add( ""+temp );
                try
                {
                    double tempD =  ((double)temp/totalCount)*100.0;
                    tempD = Math.round( tempD * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                    oneTableRowData.add( ""+ tempD );
                }
                catch( Exception e )
                {
                    oneTableRowData.add( ""+0 );
                }

                grandTotalOfUnknown+= temp;
            }
            
            oneTableRowData.add( ""+(totalCount-grandTotalOfUnknown) );
            try
            {
                double tempD =  ((double)(totalCount-grandTotalOfUnknown)/totalCount)*100.0;
                tempD = Math.round( tempD * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                oneTableRowData.add( ""+ tempD );
            }
            catch( Exception e )
            {
                oneTableRowData.add( ""+0 );
            }
            
            tableData.add( oneTableRowData );
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );  
        }
        else if( ccemReport.getReportType().equals( CCEMReport.ORGUNITGROUP_DATAVALUE ) )
        {
            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List<List<String>> tableData = new ArrayList<List<String>>();
            
            List<String> oneSubHeadingRow = new ArrayList<String>();
            
            Integer periodId = 0;
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date );
            String periodStartDate = "";
            
            if( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
            {
                periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
            }
            else if( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
            {
               periodStartDate = (calendar.get( Calendar.YEAR )-1) + "-01-01";
            }
            
            periodId = ccemReportManager.getPeriodId( periodStartDate, ccemReport.getPeriodRequire() );
            
            if( periodId == 0 )
            {
                ccemReportOutput.setReportHeading( "No Period Exists" );
                return SUCCESS;
            }
            
            tableHeadings.add( "Facility Type" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "Total Facilities" );
            oneSubHeadingRow.add( " " );
            
            String dataElementIdsByComma = "-1";
            String optComboIdsByComma = "-1";
            List<String> dataElementOptions = new ArrayList<String>();
            
            for( CCEMReportDesign ccemReportDesign1 :  reportDesignList )
            {                
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                
                dataElementIdsByComma += "," + dataElementId;
                optComboIdsByComma += "," + optComboId;
                
                tableHeadings.add( ccemReportDesign1.getDisplayheading() );
                
                List<String> distinctDataElementValues = new ArrayList<String>( ccemReportManager.getDistinctDataElementValue( dataElementId, optComboId, periodId ) );
                
                for( int i = 0; i < distinctDataElementValues.size(); i++ )
                {
                    if( i != 0 )
                    {
                        tableHeadings.add( " " );
                    }
                    oneSubHeadingRow.add( distinctDataElementValues.get( i ).split( ":" )[2] );
                    dataElementOptions.add( distinctDataElementValues.get( i ) );
                }                
            }
            
            tableSubHeadings.add( oneSubHeadingRow );
            
            for( Integer orgUnitGroupId : orgunitGroupList )
            {
                List<Integer> orgUnitIds = ccemReportManager.getOrgunitIds( selOrgUnitList, orgUnitGroupId );

                if( orgUnitIds ==  null || orgUnitIds.size() <= 0 )
                {
                    
                }
                else
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
    
                    List<String> oneTableDataRow = new ArrayList<String>();
                    String orgUnitIdsBycomma = getCommaDelimitedString( orgUnitIds );
                    
                    oneTableDataRow.add( orgUnitGroup.getName() );
                    
                    oneTableDataRow.add( ""+orgUnitIds.size() );
                    
                    Map<String, Integer> dataValueCountMap = new HashMap<String, Integer>( ccemReportManager.getDataValueCountforDataElements( dataElementIdsByComma, optComboIdsByComma, periodId, orgUnitIdsBycomma ) );
                    for( String dataElementOption : dataElementOptions )
                    {
                        Integer temp = dataValueCountMap.get( dataElementOption );
                        if( temp == null )
                        {
                            temp = 0;
                        }
                        oneTableDataRow.add( ""+temp );
                    }
                    
                    tableData.add( oneTableDataRow );
                }
            }
            
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );
            ccemReportOutput.setTableSubHeadings( tableSubHeadings );
        }
        else if( ccemReport.getReportType().equals( CCEMReport.ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE ) )
        {
            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List<List<String>> tableData = new ArrayList<List<String>>();
            
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date );
            String periodStartDate = "";
            String periodEndDate = "";
            String periodIdsByComma = "";
            List<Period> periodList = null;
            PeriodType periodType = periodService.getPeriodTypeByName( ccemReport.getPeriodRequire() );
            Date sDate = null;
            Date eDate = null;
            
            int monthDays[] = {31,28,31,30,31,30,31,31,30,31,30,31};
            
            tableHeadings.add( "OrgUnit Hierarchy" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "OrgUnit" );
            oneSubHeadingRow.add( " " );
            
            if( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
            {
                periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
                periodEndDate = calendar.get( Calendar.YEAR ) + "-12-31";
                sDate = format.parseDate( periodStartDate );
                eDate = format.parseDate( periodEndDate );
            }
            else if( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
            {
               periodStartDate = (calendar.get( Calendar.YEAR )-1) + "-01-01";
               periodEndDate = (calendar.get( Calendar.YEAR )-1) + "-12-31";
               sDate = format.parseDate( periodStartDate );
               eDate = format.parseDate( periodEndDate );
            }
            else if( periodRadio.equalsIgnoreCase( CCEMReport.LAST_6_MONTHS ) )
            {
                calendar.add( Calendar.MONTH, -1 );
                calendar.set( Calendar.DATE, monthDays[calendar.get( Calendar.MONTH )] );
                eDate = calendar.getTime();
                
                calendar.add( Calendar.MONTH, -5 );
                calendar.set( Calendar.DATE, 1 );
                sDate = calendar.getTime();
            }
            else if( periodRadio.equalsIgnoreCase( CCEMReport.LAST_3_MONTHS ) )
            {
                calendar.add( Calendar.MONTH, -1 );
                calendar.set( Calendar.DATE, monthDays[calendar.get( Calendar.MONTH )] );
                eDate = calendar.getTime();
                
                calendar.add( Calendar.MONTH, -2 );
                calendar.set( Calendar.DATE, 1 );
                sDate = calendar.getTime();
            }
            
            periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, sDate, eDate ) );
            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
            periodIdsByComma = getCommaDelimitedString( periodIds );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yy" );
            System.out.println(simpleDateFormat.format( sDate) +" : "+ simpleDateFormat.format( eDate ) );
            
            String dataElementIdsByComma = "-1";
            String optComboIdsByComma = "-1";
            
            for( CCEMReportDesign ccemReportDesign1 :  reportDesignList )
            {
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                
                dataElementIdsByComma += "," + dataElementId;
                optComboIdsByComma += "," + optComboId;
                
                tableHeadings.add( ccemReportDesign1.getDisplayheading() );
                int i = 0;
                for( Period period : periodList )
                {
                    oneSubHeadingRow.add( simpleDateFormat.format( period.getStartDate() ) );
                    if( i != 0 ) 
                        tableHeadings.add( " " );
                    i++;
                }
            }
            
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();
            
            for( Integer orgUnitGroupId : orgunitGroupList )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
                orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
            }
            
            for( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }
            
            orgUnitList.retainAll( orgUnitGroupMembers );
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            
            Map<String, Integer> equipmentDataValueMap = new HashMap<String, Integer>( ccemReportManager.getFacilityWiseEquipmentRoutineData( orgUnitIdsByComma, periodIdsByComma, dataElementIdsByComma, optComboIdsByComma ) );
            
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                List<String> oneTableDataRow = new ArrayList<String>();
                String orgUnitBranch = "";
                if( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }
                
                oneTableDataRow.add( orgUnitBranch );
                oneTableDataRow.add( orgUnit.getName() );
                
                for( CCEMReportDesign ccemReportDesign1 :  reportDesignList )
                {                
                    String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                    Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    
                    for( Period period : periodList )
                    {
                        Integer temp = equipmentDataValueMap.get( orgUnit.getId()+":"+dataElementId+":"+period.getId() );
                        if( temp == null )
                        {
                            oneTableDataRow.add( " " );
                        }
                        else
                        {
                            oneTableDataRow.add( ""+temp );
                        }
                    }
                }
                
                tableData.add( oneTableDataRow );
            }
            
            tableSubHeadings.add( oneSubHeadingRow );
            ccemReportOutput.setTableData( tableData );
            ccemReportOutput.setTableHeadings( tableHeadings );
            ccemReportOutput.setTableSubHeadings( tableSubHeadings );
        }
        
        return SUCCESS;
    }
    
    private String getOrgunitBranch( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " -> " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
}
