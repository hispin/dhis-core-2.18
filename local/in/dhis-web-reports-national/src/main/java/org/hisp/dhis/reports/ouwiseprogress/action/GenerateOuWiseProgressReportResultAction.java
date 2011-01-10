
package org.hisp.dhis.reports.ouwiseprogress.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitShortNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;

import com.opensymphony.xwork2.Action;

public class GenerateOuWiseProgressReportResultAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private String aggRadio;
    
    public void setAggRadio( String aggRadio )
    {
        this.aggRadio = aggRadio;
    }

    private OrganisationUnit selectedOrgUnit;

    private List<OrganisationUnit> orgUnitList;

    private SimpleDateFormat simpleDateFormat;

    private String reportFileNameTB;

    private String reportModelTB;

    private Date sDate;

    private Date eDate;

    private String raFolderName;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    @Override
    public String execute()
        throws Exception
    {
        statementManager.initialise();

        // Initialization
        raFolderName = reportService.getRAFolderName();
        simpleDateFormat = new SimpleDateFormat( "MMM-yy" );
        SimpleDateFormat dayFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        // Getting Report Details       
        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitShortNameComparator() );
            orgUnitList.add( selectedOrgUnit );
        }

        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );

        // Period Info
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );
        
        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( sDate, eDate ) );
        
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
        
        //String periodids = getCommaDelimitedString( getIdentifiers(Period.class, periodList ));

        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        int orgUnitCount = 0;

        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

            int count1 = 0;
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while ( reportDesignIterator.hasNext() )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    tempStr = currentOrgUnit.getName();
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = selectedOrgUnit.getParent().getName();
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = selectedOrgUnit.getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "DATE-FROM" ) )
                {
                    tempStr = dayFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "DATE-TO" ) )
                {
                    tempStr = dayFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-FROM" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-TO" ) )
                {
                    tempStr = simpleDateFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if( aggRadio.equalsIgnoreCase( "generateaggdata" ) )
                        {
                            tempStr = reportService.getResultDataValue( deCodeString, sDate, eDate, currentOrgUnit, reportModelTB );
                        }
                        else
                        {
                            tempStr = reportService.getResultDataValueFromAggregateTable( deCodeString, periodIds, currentOrgUnit, reportModelTB );
                        }
                    }
                }

                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) 
                        || deCodeString.equalsIgnoreCase( "MONTH-FROM" ) || deCodeString.equalsIgnoreCase( "MONTH-TO" ) 
                        || deCodeString.equalsIgnoreCase( "DATE-FROM" ) || deCodeString.equalsIgnoreCase( "DATE-TO" ) )
                    {
                    }
                    else
                    {
                        tempColNo += orgUnitCount;
                    }

                    WritableCellFormat wCellformat;

                    if( orgUnitCount == orgUnitList.size()-1 )
                    {
                        wCellformat = new WritableCellFormat( arialBold );
                    }
                    else
                    {
                        wCellformat = new WritableCellFormat();
                    }
                    
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setAlignment( Alignment.CENTRE );
                    wCellformat.setWrap( true );

                    try
                    {
                        sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                    }
                    catch( Exception e )
                    {
                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                    }
                }
                
                count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }
}
