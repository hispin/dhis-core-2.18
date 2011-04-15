package org.hisp.dhis.reports.csreview.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager; 
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType; 
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.user.CurrentUserService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.opensymphony.xwork2.Action;

public class GenerateCSReviewReportResultAction
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";

    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }
   
    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
    
/*    
    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
 */   

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

 

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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private SimpleDateFormat yearFormat;

    public SimpleDateFormat getYearFormat()
    {
        return yearFormat;
    }

    private List<OrganisationUnit> orgUnitList;

    private List<String> deCodeType;

    private List<String> serviceType;

    private String raFolderName;

    private String reportModelTB;

    private String reportFileNameTB;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private Date sDate;

    private Date eDate;

    // private Date sDateTemp;

    // private Date eDateTemp;
    private Integer monthCount;

    int isAggregated = 0;

    int deFlag2;

    int deFlag1;
    
    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {

        // Intialisation

        statementManager.initialise();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        orgUnitList = new ArrayList<OrganisationUnit>();
        String deCodesXMLFileName = "";
        raFolderName = reportService.getRAFolderName();

        String tempStr = "";

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        yearFormat = new SimpleDateFormat( "yyyy" );

        // Month count
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );

        String tempFromDate = simpleDateFormat.format( sDate );
        String tempToDate = simpleDateFormat.format( eDate );

        //System.out.println( "Starting:" + tempFromDate + "Ending: " + tempToDate );

        String startTargetYear = yearFormat.format( sDate );

       // System.out.println( "StartTargetYear:" + startTargetYear );

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        tempStartDate.setTime( sDate );
        tempEndDate.setTime( eDate );

        int endYear = tempEndDate.get( Calendar.YEAR );
        int startYear = tempStartDate.get( Calendar.YEAR );
        int endMonth = tempEndDate.get( Calendar.MONTH );
        int startMonth = tempStartDate.get( Calendar.MONTH );

        monthCount = ((endYear - startYear) * 12) - startMonth + endMonth + 1;
       // System.out.println( "MonthCount : " + monthCount );

        tempStr = monthCount.toString();

        // Getting Report Details
        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();

        System.out.println( reportModelTB + " : " + reportFileNameTB + " : " + deCodesXMLFileName );

        System.out.println( "Report Generation Start Time is : \t" + new Date() );

        // Getting Orgunit Details
        List<OrganisationUnit> curUserRootOrgUnitList = new ArrayList<OrganisationUnit>( currentUserService
            .getCurrentUser().getOrganisationUnits() );
        if ( curUserRootOrgUnitList != null && curUserRootOrgUnitList.size() != 0 )
        {
            for ( OrganisationUnit orgUnit : curUserRootOrgUnitList )
            {
                List<OrganisationUnit> childOrgList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( childOrgList, new OrganisationUnitNameComparator() );
                orgUnitList.addAll( childOrgList );
                // Collections.sort( orgUnitList, new
                // OrganisationUnitNameComparator() );
                orgUnitList.add( orgUnit );

            }
        }

        System.out.println( "OrgUnitSize:" + orgUnitList.size() );
        // Getting DeCodes
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        // Getting Exel Template
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );
        // WritableSheet sheet0 = outputReportWorkbook.createSheet(
        // selReportObj.getName(), 0 );
        // int rowStart = 0;
        // int colStart = 0;
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( true );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );

        WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
        sheet0.addCell( new Label( 1, 1, selReportObj.getName(), getCellFormat2() ) );
        sheet0.addCell( new Label( 3, 4, startTargetYear, getCellFormat2() ) );
        sheet0.addCell( new Label( 5, 3, "Achievement for : " + tempFromDate + " - " + tempToDate, getCellFormat2() ) );

        // Period selPeriod = periodService.getPeriod( sDate, eDate, new
        // YearlyPeriodType() );
        /*
         * if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) ) {
         * OrganisationUnit orgUnit =
         * organisationUnitService.getOrganisationUnit( ouIDTB ); orgUnitList =
         * new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
         * Collections.sort( orgUnitList, new OrganisationUnitNameComparator()
         * ); orgUnitList.add( orgUnit );
         * 
         * parentUnit = orgUnit.getName(); }
         */

        int rowCount = 1;
        int rowIncr = 0;
        // Getting DataValues
        for ( OrganisationUnit curOrgUnit : orgUnitList )
        {
            int count1 = 0;
            for ( String deCode : deCodesList )
            {
                int tempRowNo = rowList.get( count1 ) + rowIncr;
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 );

                // String deType = (String) deCodeType.get( count1 );
                String sType = (String) serviceType.get( count1 );

                /*
                 * Calendar tempStartDate1 = Calendar.getInstance(); Calendar
                 * tempEndDate1 = Calendar.getInstance();
                 * 
                 * tempStartDate1.setTime( sDate ); tempEndDate1.setTime( eDate
                 * );
                 */
                /*
                 * List<Calendar> calendarList = new ArrayList<Calendar>(
                 * getStartingEndingPeriods( deType ) ); if ( calendarList ==
                 * null || calendarList.isEmpty() ) { tempStartDate1.setTime(
                 * selPeriod.getStartDate() ); tempEndDate1.setTime(
                 * selPeriod.getEndDate() ); return SUCCESS; } else {
                 * tempStartDate1 = calendarList.get( 0 ); tempEndDate1 =
                 * calendarList.get( 1 ); }
                 */
                if ( deCode.equalsIgnoreCase( "[0.0]" ) )
                {
                    tempStr = " ";
                }
                else if ( deCode.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = curOrgUnit.getName();
                }
                else if ( deCode.equalsIgnoreCase( "SLNo" ) )
                {
                    if ( rowCount == orgUnitList.size() )
                    {
                        tempStr = " ";
                    }
                    else
                    {
                        tempStr = "" + rowCount;
                    }
                }
                else if ( sType.equalsIgnoreCase( "formula" ) )
                {
                    tempStr = deCode.replace( "?", "" + (tempRowNo + 1) );
                    tempStr = tempStr.replace( "MONTHCOUNT", "" + monthCount );
                    // float f = Float.parseFloat(tempStr);
                    // int tempStrRound = Math.round(f);
                }
                else
                {
                    if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                    {
                        tempStr = reportService.getIndividualResultDataValue(deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit, reportModelTB );
                    } 
                    else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                    {
                        tempStr = getResultDataValue( deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit );
                        //tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                    }
                    else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                    {
                        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( tempStartDate.getTime(), tempEndDate.getTime() ) );
                        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                        tempStr = reportService.getResultDataValueFromAggregateTable( deCode, periodIds, curOrgUnit, reportModelTB );
                    }
                    //tempStr = getResultDataValue( deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit );
                }
                System.out.println( "DECode : " + deCode + "   TempStr : " + tempStr );
               // System.out.println( "TempStr: " + tempStr );
                sheet0 = outputReportWorkbook.getSheet( sheetNo );
                // sheet0.addCell( new Label( colStart, rowStart,
                // selReportObj.getName(), getCellFormat1() ) );

                /*
                 * WritableCellFormat wCellformat = new WritableCellFormat();
                 * wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                 * wCellformat.setWrap( true ); wCellformat.setAlignment(
                 * Alignment.CENTRE );
                 */
                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "formula" ) )
                    {

                        sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        // float f = Float.parseFloat(tempStr);
                        // int tempStrRound = Math.round(f);
                        // System.out.println("Round value :" +tempStrRound);
                    }
                    else
                    {
                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                    }
                }
                // System.out.println("Temp Data value : " + tempStr );
                count1++;
            }
           // System.out.println( "Name og OrgUnit :" + curOrgUnit.getName() );
            rowCount++;
            rowIncr++;
        }

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        //fileName = reportFileNameTB.replace( ".xls", "" );
        fileName = reportFileNameTB;
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( "Report Generation End Time is : \t" + new Date() );

        outputReportFile.deleteOnExit();
        statementManager.destroy();
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive Methods
    // -------------------------------------------------------------------------

    // for EXL file

    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                // System.out.println( "There is no DECodes related XML file in
                // the user home" );
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();

                deCodes.add( ((Node) textDECodeList.item( 0 )).getNodeValue().trim() );
                serviceType.add( deCodeElement.getAttribute( "stype" ) );
                deCodeType.add( deCodeElement.getAttribute( "type" ) );
                sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );

            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return deCodes;
    }// getDECodes end

    // Function for get data Value

    private String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {
        isAggregated = 0;

        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                    .length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                {
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        startDate, endDate, organisationUnit );
                    if ( aggregatedValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                        deFlag2 = 0;

                    }
                    else
                    {
                        replaceString = String.valueOf( aggregatedValue );

                        deFlag2 = 1;

                        isAggregated = 1;
                    }

                }
                else
                {
                    deFlag1 = 1;
                    deFlag2 = 0;
                    PeriodType dePeriodType = getDataElementPeriodType( dataElement );
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType(
                        dePeriodType, startDate, endDate ) );
                    Period tempPeriod = new Period();
                    if ( periodList == null || periodList.isEmpty() )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                    {
                        // Works for both text and boolean data types

                        replaceString = dataValue.getValue();
                    }
                    else
                    {
                        replaceString = "";
                    }

                    if ( replaceString == null )
                    {
                        replaceString = "";
                    }
                }
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );

            if ( deFlag1 == 0 )
            {

                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                    resultValue = "";
                }
                if ( d == -1 )
                {
                    d = 0.0;
                    resultValue = "";
                }
                else
                {

                    // This is to display financial data as it is like 2.1476838
                    resultValue = "" + d;

                    // These lines are to display financial data that do not
                    // have decimals
                    d = d * 10;

                    if ( d % 10 == 0 )
                    {
                        resultValue = "" + (int) d / 10;
                    }

                    d = d / 10;

                    // These line are to display non financial data that do not
                    // require decimals
                    /*
                     * if ( !( reportModelTB.equalsIgnoreCase(
                     * "STATIC-FINANCIAL" ) ) ) { resultValue = "" + (int) d; }
                     */

                    // if ( resultValue.equalsIgnoreCase( "0" ) )
                    // {
                    // resultValue = "";
                    // }
                }

            }
            else
            {
                deFlag2 = 0;
                resultValue = buffer.toString();
            }

            if ( isAggregated == 0 )
            {
                resultValue = " ";
            }

            if ( resultValue.equalsIgnoreCase( "" ) )
            {
                resultValue = " ";
            }

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }// end data valu function

    /*
     * Returns the PeriodType Object for selected DataElement, If no PeriodType
     * is found then by default returns Monthly Period type
     */
    public PeriodType getDataElementPeriodType( DataElement de )
    {
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Iterator<DataSet> it = dataSetList.iterator();
        // Iterator it = dataSetList.iterator();
        while ( it.hasNext() )
        {
            DataSet ds = (DataSet) it.next();
            List<DataElement> dataElementList = new ArrayList<DataElement>( ds.getDataElements() );
            if ( dataElementList.contains( de ) )
            {
                return ds.getPeriodType();
            }
        }

        return null;

    } // getDataElementPeriodType end

    // getStartEndDateFunction
    /*
     * public List<Calendar> getStartingEndingPeriods( String deType ) {
     * 
     * List<Calendar> calendarList = new ArrayList<Calendar>();
     * 
     * Calendar tempStartDate1 = Calendar.getInstance(); Calendar tempEndDate1 =
     * Calendar.getInstance();
     * 
     * //tempStartDate1.setTime( selPeriod.getStartDate() ); //
     * tempEndDate1.setTime( selPeriod.getEndDate() );
     * 
     * 
     * calendarList.add( tempStartDate1 ); calendarList.add( tempEndDate1 );
     * 
     * 
     * 
     * return calendarList; }
     */

    // Excel sheet format function
    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_50 );
        wCellformat.setWrap( false );
        return wCellformat;
    } // end getCellFormat1() function

    // Excel sheet format function
    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_50 );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        // wCellformat.
        wCellformat.setWrap( true );
        return wCellformat;
    } // end getCellFormat1() function

}
