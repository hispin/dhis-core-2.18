package org.hisp.dhis.reports.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class GetReportsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private String periodType;

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    private String ouId;

    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }

    private String reportTypeName;

    public void setReportTypeName( String reportTypeName )
    {
        this.reportTypeName = reportTypeName;
    }

    /*
     * private String autogenrep;
     * 
     * public void setAutogenrep( String autogenrep ) { this.autogenrep =
     * autogenrep; }
     */
    private List<Report_in> reportList;

    public List<Report_in> getReportList()
    {
        return reportList;
    }

    private String ouName;

    public String getOuName()
    {
        return ouName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // raFolderName = reportService.getRAFolderName();
        if ( ouId != null )
        {
            try
            {
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouId ) );

                // int ouLevel =
                // organisationUnitService.getLevelOfOrganisationUnit( orgUnit
                // );

                // if(autogenrep.equalsIgnoreCase( "1") )
                // ouLevel++;

                // orgUnitLevel = ""+ouLevel;
                ouName = orgUnit.getShortName();

                PeriodType periodTypeObj = periodService.getPeriodTypeByName( periodType );

                reportList = new ArrayList<Report_in>( reportService.getReportsByPeriodSourceAndReportType(
                    periodTypeObj, orgUnit, reportTypeName ) );
                System.out.println("Org Unit : "+ ouName + " Report List : " + reportList.size() );
                // getSelectedReportList( reportListFileName );
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while getting Reports List : " + e.getMessage() );
            }
        }

        return SUCCESS;
    }

    /*
     * public void getSelectedReportList( String reportListFileName ) { String
     * fileName = reportListFileName; String path = System.getProperty(
     * "user.home" ) + File.separator + "dhis" + File.separator + raFolderName +
     * File.separator + fileName; try { String newpath = System.getenv(
     * "DHIS2_HOME" ); if ( newpath != null ) { path = newpath + File.separator
     * + raFolderName + File.separator + fileName; } } catch (
     * NullPointerException npe ) { // do nothing, but we might be using this
     * somewhere without // USER_HOME set, which will throw a NPE }
     * 
     * String reportId = ""; String reportName = ""; String reportType = "";
     * String reportLevel = ""; String reportModel = ""; String reportFileName =
     * ""; int count = 0;
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
     * if ( reportType.equals( periodType ) && reportLevel.equals( orgUnitLevel
     * ) ) { Report reportObj = new Report(reportId, reportName, reportType,
     * reportModel, reportFileName, reportLevel); reportList.add( count,
     * reportObj ); count++; System.out.println( reportName + " : " + reportId
     * ); } } }// end of for loop with s var }// try block end catch (
     * SAXParseException err ) { System.out.println( "** Parsing error" +
     * ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
     * System.out.println( " " + err.getMessage() ); } catch ( SAXException e )
     * { Exception x = e.getException(); ((x == null) ? e :
     * x).printStackTrace(); } catch ( Throwable t ) { t.printStackTrace(); }
     * 
     * }// getReportList end
     */

}
