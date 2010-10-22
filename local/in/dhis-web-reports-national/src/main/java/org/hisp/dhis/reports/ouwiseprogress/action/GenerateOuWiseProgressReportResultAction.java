
package org.hisp.dhis.reports.ouwiseprogress.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
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
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitShortNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.system.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;


public class GenerateOuWiseProgressReportResultAction
    implements Action
{

    private static final String NULL_REPLACEMENT = "0";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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

    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
/*
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
*/
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
/*    
    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
*/
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    /*
     * private String contentType;
     * 
     * public String getContentType() { return contentType; }
     */

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    /*
     * private int bufferSize;
     * 
     * public int getBufferSize() { return bufferSize; }
     */

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    private OrganisationUnit selectedOrgUnit;

    public OrganisationUnit getSelectedOrgUnit()
    {
        return selectedOrgUnit;
    }

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private List<String> dataValueList;

    public List<String> getDataValueList()
    {
        return dataValueList;
    }

    private List<String> services;

    public List<String> getServices()
    {
        return services;
    }

    private List<String> slNos;

    public List<String> getSlNos()
    {
        return slNos;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private SimpleDateFormat monthFormat;

    public SimpleDateFormat getMonthFormat()
    {
        return monthFormat;
    }

    private SimpleDateFormat simpleMonthFormat;

    public SimpleDateFormat getSimpleMonthFormat()
    {
        return simpleMonthFormat;
    }

    private SimpleDateFormat yearFormat;

    public SimpleDateFormat getYearFormat()
    {
        return yearFormat;
    }

    private SimpleDateFormat simpleYearFormat;

    public SimpleDateFormat getSimpleYearFormat()
    {
        return simpleYearFormat;
    }

    private List<String> deCodeType;

    private List<String> serviceType;

    private String reportFileNameTB;
/*
    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }
*/
    private String reportModelTB;
/*
    public void setReportModelTB( String reportModelTB )
    {
        this.reportModelTB = reportModelTB;
    }
*/
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
/*
    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }
*/
    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }
/*
    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

    private String aggCB;

    public void setAggCB( String aggCB )
    {
        this.aggCB = aggCB;
    }
*/
    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private Date sDate;

    private Date eDate;

    private PeriodType periodType;

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    private String raFolderName;

    private List<OrganisationUnit> childOrgUnits;

    public List<OrganisationUnit> getChildOrgUnits()
    {
        return childOrgUnits;
    }

    Integer startMonth;

    Integer endMonth;

    private Map<String, String> months;

    public Map<String, String> getMonths()
    {
        return months;
    }

    private Map<String, Integer> monthOrder;

    public Map<String, Integer> getMonthOrder()
    {
        return monthOrder;
    }

    int deFlag1;

    int deFlag2;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        statementManager.initialise();
        deFlag2 = 0;
        deFlag1 = 0;
        // Initialization
        raFolderName = reportService.getRAFolderName();

        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        //String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleYearFormat = new SimpleDateFormat( "yy" );
        //deCodesXMLFileName = reportList + "DECodes.xml";
        int tempNum = 0;

        startMonth = 0;

        endMonth = 0;
        
        // Getting Report Details       
        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        System.out.println( reportModelTB + " : " + reportFileNameTB + " : " + deCodesXMLFileName + " : " + ouIDTB );

      //  String parentUnit = "";

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new OrganisationUnitShortNameComparator() );
            orgUnitList.add( selectedOrgUnit );

        }
        // System.out.println(orgUnitList.size());

        // Period Info
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );

        // System.out.println(sDate.getTime() + " "+ eDate.getTime());
        List<String> deCodesList = getDECodes( deCodesXMLFileName );

        Iterator<OrganisationUnit> it = orgUnitList.iterator();

        int rowCounter = 0;
        int orgUnitCount = 0;
        // System.out.println("deCodesList size = " + deCodesList.size());

        OrganisationUnit currentOrgUnit = new OrganisationUnit();

        while ( it.hasNext() )

        {
            currentOrgUnit = (OrganisationUnit) it.next();
            // System.out.println(orgUnitCount);
            Iterator<String> it1 = deCodesList.iterator();
            int count1 = 0;
            while ( it1.hasNext() )
            {

                String deCodeString = (String) it1.next();
                // System.out.println(deCodeString);
                // System.out.println(deCodeType.size());
               // String deType = (String) deCodeType.get( count1 );
                String sType = (String) serviceType.get( count1 );
                // System.out.println(deType + " " + sType);
               // int count = 0;
               // double sum = 0.0;
                //int flag1 = 0;
                String tempStr = "";
                int tempColNo = 0;
                int tempRowNo = rowList.get( count1 );
                if ( tempRowNo > 6 )
                {
                    tempColNo = colList.get( count1 ) + orgUnitCount;
                }
                else
                {
                    tempColNo = colList.get( count1 );
                }
                int sheetNo = sheetList.get( count1 );

                // System.out.println("tempRowNo = " + tempRowNo +
                // " tempColNo = " + tempColNo);

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                    deFlag2 = 0;
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-FROM" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                    deFlag2 = 0;
                }

                else if ( deCodeString.equalsIgnoreCase( "MONTH-TO" ) )
                {
                    tempStr = simpleDateFormat.format( eDate );
                    deFlag2 = 0;
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                    deFlag2 = 0;
                }
                else if ( tempRowNo == 7 && tempColNo > 3 )
                {
                    tempStr = currentOrgUnit.getName();
                    deFlag2 = 0;
                }
                else
                {
                    rowCounter += 1;

                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        tempStr = getResultDataValue( deCodeString, sDate, eDate, currentOrgUnit );
                        if ( deFlag2 == 1 )
                        {
                            try
                            {
                                tempNum = Integer.parseInt( tempStr );
                            }
                            catch ( Exception ex )
                            {
                                tempNum = 0;
                            }

                        }
                    }

                }

                if ( tempStr == null || tempStr.equals( " " ) )
                {

                    WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                    WritableCellFormat wCellformat = new WritableCellFormat();

                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );

                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );

                }
                else
                {

                    if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                        {

                        }

                        else if ( deCodeString.equalsIgnoreCase( "MONTH-FROM" )
                            || deCodeString.equalsIgnoreCase( "MONTH-TO" ) )
                        {

                        }

                        WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                        WritableCellFormat wCellformat;

                       // WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );
                        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );

                        int lastCol = 3 + (orgUnitList.size());
                        // System.out.println(lastCol);
                        if ( tempColNo == lastCol )
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
                            if ( deFlag2 == 1 )
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, tempNum, wCellformat ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }

                        catch ( Exception e )
                        {
                            System.out.println( "Cannot write to Excel" );
                        }
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

        System.out.println( "Report Generation End Time is : \t" + new Date() );

        outputReportFile.deleteOnExit();

        statementManager.destroy();

        return SUCCESS;
    }

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
        // System.out.println("path = "+path);

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
        // System.out.println("serviceType "+serviceType.size() + " deCodeType "
        // +deCodeType.size() +" sheetList "+sheetList.size() +
        // " rowList "+rowList.size() + " colList " + colList.size());
        return deCodes;
    }// getDECodes end

    /*
     * Returns the PeriodType Object for selected DataElement, If no PeriodType
     * is found then by default returns Monthly Period type
     */
    public PeriodType getDataElementPeriodType( DataElement de )
    {
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Iterator<DataSet> it = dataSetList.iterator();
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

    /**
     * Converts an expression on the form<br>
     * [34] + [23], where the numbers are IDs of DataElements, to the form<br>
     * 200 + 450, where the numbers are the values of the DataValues registered
     * for the Period and source.
     * 
     * @return The generated expression
     */
    private String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {
        try
        {

            // System.out.println( "expression : " + formula + " ***** " +
            // String.valueOf( startDate ) + " **** "
            // + String.valueOf( endDate ) );

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
                        deFlag2 = 1;
                    }
                    else
                    {
                        replaceString = String.valueOf( aggregatedValue );

                        deFlag2 = 1;
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
                        replaceString = "";

                    if ( replaceString == null )
                        replaceString = "";

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
                    if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                        resultValue = "" + (int) d;

                    // if ( resultValue.equalsIgnoreCase( "0" ) )
                    // {
                    // resultValue = "";
                    // }
                }

            }
            else
            {
                resultValue = buffer.toString();

            }

            if ( resultValue.equalsIgnoreCase( "" ) )
                resultValue = " ";

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

}
