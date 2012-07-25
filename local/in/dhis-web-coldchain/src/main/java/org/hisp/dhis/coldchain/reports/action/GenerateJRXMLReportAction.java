/**
 * 
 */
package org.hisp.dhis.coldchain.reports.action;

import static org.hisp.dhis.reporttable.ReportTable.SPACE;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.reports.CCEMReport;
import org.hisp.dhis.coldchain.reports.CCEMReportDesign;
import org.hisp.dhis.coldchain.reports.CCEMReportManager;
import org.hisp.dhis.coldchain.reports.CCEMReportOutput;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import com.lowagie.text.pdf.codec.Base64.InputStream;
import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * 
 * @version GenerateJRXMLReportAction.java Jun 26, 2012 12:12:17 PM
 */
public class GenerateJRXMLReportAction
    implements Action
{
    private static final String DEFAULT_TYPE = "html";

    protected JasperPrint jasperPrint;

    protected JasperReport jr;

    protected Map param = new HashMap();

    protected DynamicReport dr;
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private CCEMReportManager ccemReportManager;

    public void setCcemReportManager( CCEMReportManager ccemReportManager )
    {
        this.ccemReportManager = ccemReportManager;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private String pe;

    public void setPe( String pe )
    {
        this.pe = pe;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    private Map<String, Object> params = new HashMap<String, Object>();

    public Map<String, Object> getParams()
    {
        return params;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------
    private InputStream fileStream;

    public void setFileStream( InputStream arg )
    {
        fileStream = arg;
    }

    public InputStream getFileStream()
    {
        return fileStream;
    }

    private String ReportPath;

    public String getReportPath()
    {
        return ReportPath;
    }

    public void setReportPath( String reportPath )
    {
        ReportPath = reportPath;
    }

    @Override
    public String execute()
        throws Exception
    {
        Connection con = jdbcTemplate.getDataSource().getConnection();
        String fileName = null;
        String path = System.getenv("DHIS2_HOME") + File.separator + "ireports" + File.separator;
        
        
        HashMap<String, Object> hash = new HashMap<String, Object>();

        String orgUnitIdsByComma = ccemReportManager.getOrgunitIdsByComma( selOrgUnitList, orgunitGroupList );
        ccemReport = ccemReportManager.getCCEMReportByReportId( reportList );
        Map<String, String> ccemSettingsMap = new HashMap<String, String>( ccemReportManager.getCCEMSettings() );
        List<CCEMReportDesign> reportDesignList = new ArrayList<CCEMReportDesign>( ccemReportManager
            .getCCEMReportDesign( ccemReport.getXmlTemplateName() ) );
        
        
        String oName = null;
        String oUnitGrpName = null;
        oUnitGrpName = organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( 0 ) ).getName()
            + "";
        for ( int i = 1; i <= orgunitGroupList.size() - 1; i++ )
        {
            oUnitGrpName += ","
                + organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( i ) ).getName();
            System.out.println( "Group is: "
                + organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( i ) ) );
        }
        oName = organisationUnitService.getOrganisationUnit( selOrgUnitList.get( 0 ) ).getName() + "";
        for ( int j = 1; j <= selOrgUnitList.size() - 1; j++ )
        {
            oName += "," + organisationUnitService.getOrganisationUnit( selOrgUnitList.get( j ) ).getName();
            System.out.println( "Group is: "
                + organisationUnitService.getOrganisationUnit( selOrgUnitList.get( j ) ) );
        }
        hash.put( "orgunitGroup", oUnitGrpName );
        hash.put( "selOrgUnit", oName );
        hash.put( "orgUnitIdsByComma", orgUnitIdsByComma );
        HttpServletResponse response = ServletActionContext.getResponse();

        ccemReport = ccemReportManager.getCCEMReportByReportId( reportList );
        Date date = pe != null ? DateUtils.getMediumDate( pe ) : new Date();

        hash.put( "reportName", ccemReport.getReportName() );
        hash.put( "date", date );
        if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );           
            hash.put( "inventoryTypeId", inventoryTypeId );
            hash.put( "catalogTypeAttributeId", catalogTypeAttributeId );            
            fileName = "Refrigerators_freezer_models_by_agegroup.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
        jasperPrint = JasperFillManager.fillReport( jasperReport, hash, con );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP.trim() ) )

        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer inventoryTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer catalogTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );            
            hash.put( "inventoryTypeId", inventoryTypeId );
            hash.put( "catalogTypeAttributeId", catalogTypeAttributeId );            
            int i = 0;
            Integer inventoryTypeAttributeId=3;
            for( CCEMReportDesign ccemReportDesign1 :  reportDesignList )
            {
                i++;
                if( i == 1 ) continue;
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                if( ccemCellContent1.split( ":" )[3].equalsIgnoreCase( "UNKNOWN" ))
                {
                   
                }
                else if( ccemCellContent1.split( ":" )[4].equalsIgnoreCase( "MORE" ) )
                {
                    inventoryTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    catalogTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    inventoryTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );                    
                }
                else
                {
                    inventoryTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    catalogTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    inventoryTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );                                       
                }
            }             
            Map<String, Integer> catalogTypeAttributeValueMap1 = new HashMap<String, Integer>(
                ccemReportManager.getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId,
                catalogTypeAttributeId, inventoryTypeAttributeId, 0, 2 ) );
            
            Map<String, Integer> catalogTypeAttributeValueMap2 = new HashMap<String, Integer>(
                ccemReportManager.getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId,
                    catalogTypeAttributeId, inventoryTypeAttributeId, 3, 5 ) );

            Map<String, Integer> catalogTypeAttributeValueMap3 = new HashMap<String, Integer>(
                ccemReportManager.getCatalogTypeAttributeValueByAge( orgUnitIdsByComma, inventoryTypeId,
                    catalogTypeAttributeId, inventoryTypeAttributeId, 6, 10 ) );

            Map<String, Integer> catalogTypeAttributeValueMap4 = new HashMap<String, Integer>( ccemReportManager.getCatalogTypeAttributeValueByAge( orgUnitIdsByComma,inventoryTypeId, catalogTypeAttributeId, 3, 11, -1 ) );
            
            hash.put( "Value_0_2", catalogTypeAttributeValueMap1 );
            hash.put( "Value_3_5", catalogTypeAttributeValueMap2 ); 
            hash.put( "Value_6_10", catalogTypeAttributeValueMap3 );
            hash.put( "Value_11_MORE", catalogTypeAttributeValueMap4 );            
            
            fileName = "CATALOGTYPE ATTRIBUTE VALUE AGE GROUP.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, con );
        }
        else if( ccemReport.getReportType().equals( CCEMReport.ORGUNITGROUP_DATAVALUE ) )
        {  
            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List tableData = new ArrayList();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List<String> content= new ArrayList<String>();
            
            FastReportBuilder frb = new FastReportBuilder();            
            Integer periodId = 0;
            Date date1 = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date1 );
            String periodStartDate = "";            
            Map<String, Integer> subHeadingNumber= new HashMap<String, Integer>();
            if( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
            {
                periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
            }
            else if( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
            {
               periodStartDate = (calendar.get( Calendar.YEAR )-1) + "-01-01";
            }
            
            periodId = ccemReportManager.getPeriodId( periodStartDate, ccemReport.getPeriodRequire() );            
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
                int number=0;
                for( int i = 0; i < distinctDataElementValues.size(); i++ )
                {                    
                    if( i != 0 )
                    {
                        tableHeadings.add( " " );                                                
                    }
                    oneSubHeadingRow.add( distinctDataElementValues.get( i ).split( ":" )[2] );
                    dataElementOptions.add( distinctDataElementValues.get( i ) );
                    number++;
                }
                subHeadingNumber.put( ccemReportDesign1.getDisplayheading(), number );
            }
            
            tableSubHeadings.add( oneSubHeadingRow );            
            int count=0;
            
            for(int i=0;i<=tableHeadings.size()-1;i++)
            {                
                if(tableHeadings.get( i )==" " )
                {                    
                }
                else
                {
                    if(i==0 || i==1)
                    {
                        frb.addColumn(tableHeadings.get( i ), tableHeadings.get( i ), String.class.getName(), 100,true);
                        count++;
                        
                    }
                    else
                    {                        
                    }
                }
            }
           for(int j=0;j<=tableSubHeadings.size()-1;j++)
            { 
                for(int k=0; k<=tableSubHeadings.get( j ).size()-1;k++)
                {
                    if(tableSubHeadings.get( j ).get( k )==" ")
                    {                                    
                    }
                    else
                    {   
                        frb.addColumn(tableSubHeadings.get( j ).get( k ),
                            tableSubHeadings.get( j ).get( k ), String.class.getName(), 50, true);   
                        content.add( tableSubHeadings.get( j ).get( k ) );                        
                            count++;
                    }
                }             
            }            
            frb.setPrintColumnNames(true);
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage(1, count).setUseFullPageWidth(true); 
            
            int start=2; 
            for(int i=2;i<=tableHeadings.size()-1;i++)
            {  
                if(tableHeadings.get( i )== " ")
                {
                    
                }
            else
            {  
                frb.setColspan(start, subHeadingNumber.get( tableHeadings.get( i ) ), tableHeadings.get( i ));  
                start=start + subHeadingNumber.get( tableHeadings.get( i ) );
            }
            }
            frb.setTemplateFile( path+"ORGUNITGROUP_DATAVALUE.jrxml" );
            for( Integer orgUnitGroupId : orgunitGroupList )
            {
                Map numberOfData=new HashMap();
                List<Integer> orgUnitIds = ccemReportManager.getOrgunitIds( selOrgUnitList, orgUnitGroupId );
                if( orgUnitIds ==  null || orgUnitIds.size() <= 0 )
                {  
                    
                }
               else
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
    
                    String orgUnitIdsBycomma = getCommaDelimitedString( orgUnitIds );                                        
                    numberOfData.put( "Facility Type", orgUnitGroup.getName() );                    
                    numberOfData.put( "Total Facilities", ""+orgUnitIds.size() );                   
                    Map<String, Integer> dataValueCountMap2 = new HashMap<String, Integer>( ccemReportManager.getDataValueCountforDataElements( dataElementIdsByComma, optComboIdsByComma, periodId, orgUnitIdsBycomma ) );
                    for(int i=0;i<=dataElementOptions.size()-1;i++)
                    {                      
                       Integer temp = dataValueCountMap2.get( dataElementOptions.get(i) );
                            if( temp == null )
                            {
                              numberOfData.put( content.get( i ), "0" );
                            }
                            else
                            {
                              numberOfData.put( content.get( i ), temp+"" );
                            }                                               
                    } 
                    tableData.add( numberOfData );
                }               
            }
            
            JRDataSource ds = new JRMapCollectionDataSource(tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getDefaultDetailStyle().setBackgroundColor( Color.BLUE );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder(Border.THIN());
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign(HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder(Border.THIN()); 
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign(HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );            
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds ); 
        }
        
        else if( ccemReport.getReportType().equals( CCEMReport.ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE ) )
        {
            ccemReportOutput = new CCEMReportOutput();
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List tableData = new ArrayList();
            List<String> content= new ArrayList<String>();
            Date date2 = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date2 );
            String periodStartDate = "";
            String periodEndDate = "";
            String periodIdsByComma = "";
            List<Period> periodList = null;
            PeriodType periodType = periodService.getPeriodTypeByName( ccemReport.getPeriodRequire() );
            Date sDate = null;
            Date eDate = null;
            Map<String, Integer> subHeadingNumber= new HashMap<String, Integer>();
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
                subHeadingNumber.put( ccemReportDesign1.getDisplayheading(), i );
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
            
            FastReportBuilder frb = new FastReportBuilder();
            tableSubHeadings.add( oneSubHeadingRow );
           
            int count=0;
            
            for(int i=0;i<=tableHeadings.size()-1;i++)
            {                
                if(tableHeadings.get( i )==" " )
                {                    
                }
                else
                {
                    if(i==0 || i==1)
                    {
                        content.add(tableHeadings.get( i ));
                        frb.addColumn(tableHeadings.get( i ), tableHeadings.get( i ), String.class.getName(), 50,true);
                        count++;
                        
                    }
                    else
                    {                        
                    }
                }
            }
           for(int j=0;j<=tableSubHeadings.size()-1;j++)
            { 
               int increment=0;
               
                for(int k=0; k<=tableSubHeadings.get( j ).size()-1;k++)
                {
                   
                    if(tableSubHeadings.get( j ).get( k )==" ")
                    {                                    
                    }
                    else
                    { 
                        if(tableSubHeadings.get( j ).get( k ).contains( tableSubHeadings.get( j ).get( k ) ))
                        {
                            increment++;
                        }
                        
                        frb.addColumn(tableSubHeadings.get( j ).get( k ),
                            tableSubHeadings.get( j ).get( k )+ " "+ increment , String.class.getName(), 50, true);   
                        content.add( tableSubHeadings.get( j ).get( k )+ " "+increment );                        
                            count++;
                    }
                }             
            }            
            frb.setPrintColumnNames(true);
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage(1, count).setUseFullPageWidth(true); 
            
            int start=2; 
            for(int i=2;i<=tableHeadings.size()-1;i++)
            {  
                if(tableHeadings.get( i )== " ")
                {
                    
                }
            else
            {  
                frb.setColspan(start, subHeadingNumber.get( tableHeadings.get( i ) ), tableHeadings.get( i ));  
                start=start + subHeadingNumber.get( tableHeadings.get( i ) );
            }
            }
            
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData=new HashMap<String, String>();
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
                
                numberOfData.put( content.get( 0 ), orgUnitBranch );
                numberOfData.put( content.get( 1 ), orgUnit.getName() );
                int i=2;
                for( CCEMReportDesign ccemReportDesign1 :  reportDesignList )
                {                
                    String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                    Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    
                    for(Period period : periodList)
                    { 
                        Integer temp = equipmentDataValueMap.get( orgUnit.getId()+":"+dataElementId+":"+period.getId() );
                        if( temp == null )
                        {                            
                            numberOfData.put(content.get( i ), " " );                            
                        }
                        else
                        {                       
                            numberOfData.put(content.get( i ) , temp+"" );
                        }
                        i++; 
                    }  
                    
                }                
                tableData.add( numberOfData );
            }

            frb.setTemplateFile( path+"ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE.jrxml" );
            JRDataSource ds = new JRMapCollectionDataSource(tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getDefaultDetailStyle().setBackgroundColor( Color.BLUE );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder(Border.THIN());
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign(HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder(Border.THIN()); 
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign(HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );            
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        
        ServletOutputStream ouputStream = response.getOutputStream();
        JRExporter exporter = null;
        if ( "pdf".equalsIgnoreCase( type ) )
        {            
            response.setContentType( "application/pdf" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.pdf\"" );

            exporter = new JRPdfExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "rtf".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/rtf" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.rtf\"" );

            exporter = new JRRtfExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "html".equalsIgnoreCase( type ) )
        {
            exporter = new JRHtmlExporter();
            exporter.setParameter( JRHtmlExporterParameter.OUTPUT_STREAM, false );
            exporter.setParameter( JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, new Boolean( false ) );

            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "xls".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/xls" );
            response.setHeader( "Content-Disposition", "attachment; fileName=\"file.xls\"" );

            exporter = new JRXlsExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "csv".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/csv" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.csv\"" );

            exporter = new JRCsvExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        con.close();
        try
        {
            exporter.exportReport();
        }
        catch ( JRException e )
        {
            throw new ServletException( e );
        }
        finally
        {
            if ( ouputStream != null )
            {
                try
                {
                    ouputStream.close();
                }
                catch ( IOException ex )
                {
                    System.out.println( "exception thrown" );
                }
            }
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
