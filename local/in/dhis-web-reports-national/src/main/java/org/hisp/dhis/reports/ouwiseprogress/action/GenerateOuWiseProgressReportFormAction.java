package org.hisp.dhis.reports.ouwiseprogress.action;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class GenerateOuWiseProgressReportFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
/*
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
*/
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private String periodTypeName;
    
    public String getPeriodTypeName()
    {
        return periodTypeName;
    }

    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }  
 /*   
    private List<Report_in> reportList;

    public List<Report_in> getReportList()
    {
        return reportList;
    }
*/
 //   private String raFolderName;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
       
        
        
        // raFolderName = reportService.getRAFolderName();
       // System.out.println( "raFolderName = " + raFolderName );
       // reportList = new ArrayList<Report>();
        
//      reportList = new ArrayList<Report_in>( reportService.getReportsByReportType( ReportType.RT_ORGUNITWISEPROGRESS ) );
        reportTypeName = ReportType.RT_ORGUNITWISEPROGRESS;
        
        periodTypeName = MonthlyPeriodType.NAME;
        
//        System.out.println( "PeriodTypeName: "+ periodTypeName);
        
//        reportList = new ArrayList<Report_in>();

        // getSelectedReportList();

        return SUCCESS;
    }
    /*
     * public void getSelectedReportList() { String fileName =
     * "ouWiseProgressReportsList.xml";
     * System.out.println("fileName = "+fileName); String path =
     * System.getProperty( "user.home" ) + File.separator + "dhis" +
     * File.separator + raFolderName + File.separator + fileName;
     * System.out.println("path = "+path); try { String newpath = System.getenv(
     * "DHIS2_HOME" ); if ( newpath != null ) { path = newpath + File.separator
     * + raFolderName + File.separator + fileName;
     * System.out.println("path = "+path); } } catch ( NullPointerException npe
     * ) { // do nothing, but we might be using this somewhere without //
     * DHIS2_HOME set, which will throw a NPE }
     * 
     * String reportId = ""; String reportName = ""; String reportType = "";
     * String reportLevel = ""; String reportModel = ""; String reportFileName =
     * "";
     * 
     * try { DocumentBuilderFactory docBuilderFactory =
     * DocumentBuilderFactory.newInstance(); DocumentBuilder docBuilder =
     * docBuilderFactory.newDocumentBuilder(); Document doc = docBuilder.parse(
     * new File( path ) ); if ( doc == null ) { System.out.println(
     * "XML File Not Found at user home" ); return; }
     * 
     * NodeList listOfReports = doc.getElementsByTagName( "report" ); int
     * totalReports = listOfReports.getLength(); for ( int s = 0; s <
     * totalReports; s++ ) { Node reportNode = listOfReports.item( s ); if (
     * reportNode.getNodeType() == Node.ELEMENT_NODE ) { Element reportElement =
     * (Element) reportNode; reportId = reportElement.getAttribute( "id" );
     * 
     * NodeList reportNameList = reportElement.getElementsByTagName( "name" );
     * Element reportNameElement = (Element) reportNameList.item( 0 ); NodeList
     * textreportNameList = reportNameElement.getChildNodes(); reportName =
     * ((Node) textreportNameList.item( 0 )).getNodeValue().trim();
     * 
     * NodeList reportTypeList = reportElement.getElementsByTagName( "type" );
     * Element reportTypeElement = (Element) reportTypeList.item( 0 ); NodeList
     * textreportTypeList = reportTypeElement.getChildNodes(); reportType =
     * ((Node) textreportTypeList.item( 0 )).getNodeValue().trim();
     * 
     * NodeList reportModelList = reportElement.getElementsByTagName( "model" );
     * Element reportModelElement = (Element) reportModelList.item( 0 );
     * NodeList textreportModelList = reportModelElement.getChildNodes();
     * reportModel = ((Node) textreportModelList.item( 0
     * )).getNodeValue().trim();
     * 
     * NodeList reportFileNameList = reportElement.getElementsByTagName(
     * "filename" ); Element reportFileNameElement = (Element)
     * reportFileNameList.item( 0 ); NodeList textreportFileNameList =
     * reportFileNameElement.getChildNodes(); reportFileName = ((Node)
     * textreportFileNameList.item( 0 )).getNodeValue().trim();
     * 
     * NodeList reportLevelList = reportElement.getElementsByTagName( "level" );
     * Element reportLevelElement = (Element) reportLevelList.item( 0 );
     * NodeList textreportLevelList = reportLevelElement.getChildNodes();
     * reportLevel = ((Node) textreportLevelList.item( 0
     * )).getNodeValue().trim();
     * 
     * Report reportObj = new Report(reportId, reportName, reportType,
     * reportModel, reportFileName, reportLevel); reportList.add( reportObj ); }
     * }// end of for loop with s var }// try block end catch (
     * SAXParseException err ) { System.out.println( "** Parsing error" +
     * ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
     * System.out.println( " " + err.getMessage() ); } catch ( SAXException e )
     * { Exception x = e.getException(); ((x == null) ? e :
     * x).printStackTrace(); } catch ( Throwable t ) { t.printStackTrace(); }
     * System.out.println(reportList.size());
     * 
     * }// getReportList end
     */
}
