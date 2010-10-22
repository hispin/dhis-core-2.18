/**
 * 
 */
package org.hisp.dhis.reports.physical.action;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

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
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class GeneratePhysicalReportResultAction
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }
    
  /*  
    
    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
 */  
    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
    
    
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
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

    private String periodNameList;

    public void setPeriodNameList( String periodNameList )
    {
        this.periodNameList = periodNameList;
    }

    Period selPeriod;

    private List<OrganisationUnit> orgUnitList;

    private List<String> deCodeType;

    private List<String> serviceType;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private String reportModelTB;

    private String reportFileNameTB;

    private String raFolderName;

    int isAggregated = 0;

    String years[];

    int deFlag2;

    int deFlag1;

    public String execute()
        throws Exception
    {
        // Intialisation
        
        
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        orgUnitList = new ArrayList<OrganisationUnit>();
        String deCodesXMLFileName = "";

        System.out.println( "Report Generation Start Time is : \t" + new Date() );

        // Getting Report Details
        raFolderName = reportService.getRAFolderName();

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        deCodesXMLFileName = selReportObj.getXmlTemplateName();

        // Getting Orgunit Details
        List<OrganisationUnit> curUserRootOrgUnitList = new ArrayList<OrganisationUnit>( currentUserService
            .getCurrentUser().getOrganisationUnits() );

        if ( curUserRootOrgUnitList != null && curUserRootOrgUnitList.size() > 0 )
        {
            for ( OrganisationUnit orgUnit : curUserRootOrgUnitList )
            {
                List<OrganisationUnit> childList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( childList, new OrganisationUnitNameComparator() );
                orgUnitList.addAll( childList );
                orgUnitList.add( orgUnit );
            }
        }
        System.out.println( "Size of OrgUnit" + orgUnitList.size() );
        // Getting Period Info

        years = periodNameList.split( "-" );

        Date sDate = format.parseDate( years[0] + "-01-01" );

        Date eDate = format.parseDate( years[0] + "-12-31" );

        selPeriod = periodService.getPeriod( sDate, eDate, new YearlyPeriodType() );

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

        int rowCount = 1;
        // Getting DataValues
        for ( OrganisationUnit curOrgUnit : orgUnitList )
        {
            int count1 = 0;
            for ( String deCode : deCodesList )
            {
                String tempStr;
                String deType = (String) deCodeType.get( count1 );
                // String sType = (String) serviceType.get( count1 );

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( getStartingEndingPeriods( deType ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selPeriod.getStartDate() );
                    tempEndDate.setTime( selPeriod.getEndDate() );
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCode.equalsIgnoreCase( "[NA]" ) )
                {
                    tempStr = " ";
                }
                else if ( deCode.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = curOrgUnit.getName();
                }
                else if ( deCode.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + rowCount;
                }
                else
                {
                    tempStr = getResultDataValue( deCode, tempStartDate.getTime(), tempEndDate.getTime(), curOrgUnit );
                }

                System.out.println( "DECode : " + deCode + "   TempStr : " + tempStr );
                int tempRowNo = rowList.get( count1 ) + rowCount;
                int tempColNo = colList.get( count1 );
                int sheetNo = sheetList.get( count1 );
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );

                WritableCellFormat wCellformat = new WritableCellFormat();
                wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                wCellformat.setWrap( true );
                wCellformat.setAlignment( Alignment.CENTRE );

                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                }

                else
                {
                    sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                }

                count1++;
                System.out.println( tempStr );
            }
            System.out.println( "OrgUnitName=" + curOrgUnit.getName() );

            rowCount++;
        }

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( "Report Generation End Time is : \t" + new Date() );

        outputReportFile.deleteOnExit();

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
               // DataElementCategoryService optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );
                //DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );
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
                    if ( aggregatedValue == 0 )
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
                    if ( !(reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" )) )
                    {
                        resultValue = "" + (int) d;
                    }

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
    }

    public List<Calendar> getStartingEndingPeriods( String deType )
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        if ( deType.equalsIgnoreCase( "q1" ) )
        {
            tempStartDate.set( Integer.parseInt( years[0] ), Calendar.APRIL, 1 );
            tempEndDate.set( Integer.parseInt( years[0] ), Calendar.JUNE, 30 );
        }
        else if ( deType.equalsIgnoreCase( "q2" ) )
        {
            tempStartDate.set( Integer.parseInt( years[0] ), Calendar.JULY, 1 );
            tempEndDate.set( Integer.parseInt( years[0] ), Calendar.SEPTEMBER, 30 );
        }
        else if ( deType.equalsIgnoreCase( "q3" ) )
        {
            tempStartDate.set( Integer.parseInt( years[0] ), Calendar.OCTOBER, 1 );
            tempEndDate.set( Integer.parseInt( years[0] ), Calendar.DECEMBER, 31 );
        }
        else if ( deType.equalsIgnoreCase( "q4" ) )
        {
            tempStartDate.set( Integer.parseInt( years[1] ), Calendar.JANUARY, 1 );
            tempEndDate.set( Integer.parseInt( years[1] ), Calendar.MARCH, 31 );
        }
        else
        {
            tempStartDate.setTime( selPeriod.getStartDate() );
            tempEndDate.setTime( selPeriod.getEndDate() );
        }

        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }

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

    }

}
