package org.hisp.dhis.reportexcel.action;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelCategory;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelNormal;
import org.hisp.dhis.reportexcel.ReportExcelOganiztionGroupListing;
import org.hisp.dhis.reportexcel.ReportExcelPeriodColumnListing;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.export.action.SelectionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RestoreReportExcelAction
    extends ActionSupport

{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private SelectionManager selectionManager;

    private ReportExcelService reportExcelService;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public String execute()
        throws Exception
    {
        String filepath = selectionManager.getUploadFilePath();

        if ( filepath == null )
        {
            message = i18n.getString( "upload_file_first" );
            return ERROR;
        }

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse( new File( selectionManager.getUploadFilePath() ) );
        Element root = doc.getDocumentElement();

        String reportName = root.getElementsByTagName( ReportExcel.XML_TAG.NAME ).item( 0 ).getFirstChild()
            .getNodeValue();

        ReportExcel reportExcel = reportExcelService.getReportExcel( reportName );

        if ( reportExcel != null )
        {
            message = i18n.getString( "report_ready_exist" );
            return ERROR;
        }

        String reportType = root.getElementsByTagName( ReportExcel.XML_TAG.EXCEL_REPORT_TYPE ).item( 0 )
            .getFirstChild().getNodeValue();

        if ( reportType.equalsIgnoreCase( ReportExcel.TYPE.NORMAL ) )
        {
            reportExcel = new ReportExcelNormal();
        }

        if ( reportType.equalsIgnoreCase( ReportExcel.TYPE.ORGANIZATION_GROUP_LISTING ) )
        {
            reportExcel = new ReportExcelOganiztionGroupListing();
        }

        if ( reportType.equalsIgnoreCase( ReportExcel.TYPE.CATEGORY ) )
        {
            reportExcel = new ReportExcelCategory();

        }

        if ( reportType.equalsIgnoreCase( ReportExcel.TYPE.PERIOD_COLUMN_LISTING ) )
        {
            reportExcel = new ReportExcelPeriodColumnListing();
        }

        String periodRow = root.getElementsByTagName( ReportExcel.XML_TAG.PERIOD_ROW ).item( 0 ).getFirstChild()
            .getNodeValue();
        String periodColumn = root.getElementsByTagName( ReportExcel.XML_TAG.PERIOD_COLUMN ).item( 0 ).getFirstChild()
            .getNodeValue();
        String orgRow = root.getElementsByTagName( ReportExcel.XML_TAG.ORGANISATIONUNIT_ROW ).item( 0 ).getFirstChild()
            .getNodeValue();
        String orgColumn = root.getElementsByTagName( ReportExcel.XML_TAG.ORGANISATIONUNIT_COLUMN ).item( 0 )
            .getFirstChild().getNodeValue();
        String group = root.getElementsByTagName( ReportExcel.XML_TAG.GROUP ).item( 0 ).getFirstChild().getNodeValue();
        String excelTemplateFile = root.getElementsByTagName( ReportExcel.XML_TAG.EXCEL_FILE ).item( 0 )
            .getFirstChild().getNodeValue();

        reportExcel.setName( reportName );

        reportExcel.setPeriodRow( Integer.parseInt( periodRow ) );
        reportExcel.setPeriodColumn( Integer.parseInt( periodColumn ) );
        reportExcel.setOrganisationRow( Integer.parseInt( orgRow ) );
        reportExcel.setOrganisationColumn( Integer.parseInt( orgColumn ) );
        reportExcel.setGroup( group );
        reportExcel.setExcelTemplateFile( excelTemplateFile );

        reportExcelService.addReportExcel( reportExcel );

        NodeList items = ((Element) root.getElementsByTagName( ReportExcelItem.XML_TAG.REPORT_ITEMS ).item( 0 ))
            .getElementsByTagName( ReportExcelItem.XML_TAG.REPORT_ITEM );
        for ( int i = 0; i < items.getLength(); i++ )
        {
            Element item = (Element) items.item( i );

            String name = item.getElementsByTagName( ReportExcelItem.XML_TAG.NAME ).item( 0 ).getFirstChild()
                .getNodeValue();
            String row = item.getElementsByTagName( ReportExcelItem.XML_TAG.ROW ).item( 0 ).getFirstChild()
                .getNodeValue();

            String column = item.getElementsByTagName( ReportExcelItem.XML_TAG.COLUMN ).item( 0 ).getFirstChild()
                .getNodeValue();

            String sheetNo = item.getElementsByTagName( ReportExcelItem.XML_TAG.SHEET_NO ).item( 0 ).getFirstChild()
                .getNodeValue();

            String expression = item.getElementsByTagName( ReportExcelItem.XML_TAG.EXPRESSION ).item( 0 )
                .getFirstChild().getNodeValue();

            String type = item.getElementsByTagName( ReportExcelItem.XML_TAG.TYPE ).item( 0 ).getFirstChild()
                .getNodeValue();

            String periodType = item.getElementsByTagName( ReportExcelItem.XML_TAG.PERIOD_TYPE ).item( 0 )
                .getFirstChild().getNodeValue();

            ReportExcelItem reportExcelItem = new ReportExcelItem();
            reportExcelItem.setName( name );
            reportExcelItem.setColumn( Integer.parseInt( column ) );
            reportExcelItem.setRow( Integer.parseInt( row ) );
            reportExcelItem.setExpression( expression );
            reportExcelItem.setItemType( type );
            reportExcelItem.setPeriodType( periodType );
            reportExcelItem.setSheetNo( Integer.parseInt( sheetNo ) );
            reportExcelItem.setReportExcel( reportExcel );

            reportExcelService.addReportExcelItem( reportExcelItem );

        }

        return SUCCESS;
    }
}
