package org.hisp.dhis.dataanalyser.ga.action.charts;

// <editor-fold defaultstate="collapsed" desc="imports">
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

// </editor-fold>

/**
 * 
 * @author Administrator
 */
public class GenerateDrillDownResultAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    // <editor-fold defaultstate="collapsed" desc="dependencies">
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CaseAggregationConditionService caseAggregationConditionService;
    
    public void setCaseAggregationConditionService( CaseAggregationConditionService caseAggregationConditionService )
    {
        this.caseAggregationConditionService = caseAggregationConditionService;
    }

    /*
    private CaseAggregationMappingService caseAggregationMappingService;

    public void setCaseAggregationMappingService( CaseAggregationMappingService caseAggregationMappingService )
    {
        this.caseAggregationMappingService = caseAggregationMappingService;
    }
    */
    
    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="properties">
    public String selectedValues;

    public String getSelectedValues()
    {
        return selectedValues;
    }

    public void setSelectedValues( String selectedValues )
    {
        this.selectedValues = selectedValues;
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

    public String[] values;

    private String raFolderName;

    private String inputTemplatePath;

    private String outputReportPath;

   // private Period startDate;

    private OrganisationUnit selectedOrgUnit;

    private DataElement de;

    private DataElementCategoryOptionCombo coc;

    //private CaseAggregationMapping caseAggMapping;

    private List<String> serviceType;

    private List<String> deCodeType;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private String deCodesXMLFileName;

    private String reportFileNameTB;
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private Date tempStartDate;
    
    private Date tempEndDate;
    
    private PeriodType periodTypeName;
    
    private Collection<Period> periods;
    // </editor-fold>

    public String execute()
        throws Exception
    {

        values = selectedValues.split( ":" );
        int orgunit = Integer.parseInt( values[0] );
       // int periodid = Integer.parseInt( values[3] );
        int deid = Integer.parseInt( values[1] );
        int cocid = Integer.parseInt( values[2] );
        
        //06/12/2010
        String periodType = values[3];
        //Date  startDate = values[4];
       // String endDate = values[5];
        
        tempStartDate = format.parseDate( values[4] );
        tempEndDate   = format.parseDate( values[5] );
        
        
        periodTypeName = periodService.getPeriodTypeByName( periodType );
        periods = periodService.getPeriodsBetweenDates( periodTypeName, tempStartDate, tempEndDate );
        
        
       // startDate = periodService.getPeriod( periodid );
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgunit );
        de = dataElementService.getDataElement( deid );
        coc = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( cocid );
        
        System.out.println( "orgunit is " + orgunit + " de is " + deid + " coc is " + cocid + " periodType is " + periodType + " tempStartDate is " + tempStartDate + " tempEndDate is  " + tempEndDate );

        statementManager.initialise();
        raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        deCodesXMLFileName = "NBITS_DrillDownToCaseBasedDECodes.xml";
        // System.out.println( "reportList = " + reportList );
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        reportFileNameTB = "DrillDownToCaseBased.xls";

        // Initialization
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template"
            + File.separator + reportFileNameTB;

        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output"
            + File.separator + UUID.randomUUID().toString() + ".xls";

        // System.out.println( " inputTemplatePath " + inputTemplatePath );

        generatDrillDownReport();
        statementManager.destroy();
        return SUCCESS;
    }// end if loop

    public void generatDrillDownReport()
        throws Exception
    {
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );

        // Cell formatting
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        // System.out.println( "deCodesXMLFileName = " + deCodesXMLFileName );
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        // System.out.println( "deCodesList size = "+deCodesList.size() );
        // taking expression for selected de and decoc
        CaseAggregationCondition caseAggregationCondition = caseAggregationConditionService.getCaseAggregationCondition( de, coc );
        //caseAggMapping = caseAggregationMappingService.getCaseAggregationMappingByOptionCombo( de, coc );

        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        List<OrganisationUnit> orgUnitDataList = new ArrayList<OrganisationUnit>();
        orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
        Map<OrganisationUnit, Integer> ouAndLevel = new HashMap<OrganisationUnit, Integer>();

        List<Integer> levelsList = new ArrayList<Integer>();
        Map<OrganisationUnit, List<PatientDataValue>> ouPatientDataValueMap = new HashMap<OrganisationUnit, List<PatientDataValue>>();
        List<DataElement> des = new ArrayList<DataElement>();
        String tempStr = "";
        for ( OrganisationUnit ou : orgUnitList )
        {
            int level = organisationUnitService.getLevelOfOrganisationUnit( ou );
            ouAndLevel.put( ou, level );
            if ( !levelsList.contains( level ) )
            {
                levelsList.add( level );
            }

            List<PatientDataValue> patientDataValues = new ArrayList<PatientDataValue>();
            
            for( Period period : periods )
            {
                patientDataValues.addAll( caseAggregationConditionService.getPatientDataValues( caseAggregationCondition, ou, period ) );
            }
            
           // patientDataValues.addAll( caseAggregationConditionService.getPatientDataValues( caseAggregationCondition, ou, startDate ) );
           // patientDataValues = caseAggregationMappingService.getCaseAggregatePatientDataValue( ou, startDate,
           //     caseAggMapping );
            if ( patientDataValues != null )
            {
                ouPatientDataValueMap.put( ou, patientDataValues );
                orgUnitDataList.add( ou );
                for ( PatientDataValue patientDataValue : patientDataValues )
                {
                    if ( !des.contains( patientDataValue.getDataElement() ) )
                    {
                        des.add( patientDataValue.getDataElement() );
                    }
                }
            }
        }

        WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
        WritableCellFormat wCellformat1 = new WritableCellFormat();
        wCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat1.setAlignment( Alignment.CENTRE );
        wCellformat1.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat1.setWrap( true );
        wCellformat1.setBackground( Colour.GREY_40_PERCENT );

        // <editor-fold defaultstate="collapsed" desc="adding column names">
        int count1 = 0;
        for ( DataElement de : des )
        {
            // DEName
            sheet0.addCell( new Label( 7 + count1, 1, "" + de.getName(), wCellformat1 ) );

            count1++;
        }
        sheet0.addCell( new Label( 7 + count1, 1, "Execution Date", wCellformat1 ) );

        for ( int i = levelsList.size() - 1; i >= 0; i-- )
        {
            int level = levelsList.get( i );
            count1++;
            sheet0.addCell( new Label( 7 + count1, 1, organisationUnitService.getOrganisationUnitLevelByLevel( level )
                .getName(), wCellformat1 ) );
        }

        // </editor-fold>

        int rowNo = rowList.get( 0 );
        int srno = 0;
        // <editor-fold defaultstate="collapsed"
        // desc="For loop of orgUnitDataList">
        for ( OrganisationUnit ou : orgUnitDataList )
        {
            List<PatientDataValue> pdvList = ouPatientDataValueMap.get( ou );
            // System.out.println( "pdvList size = " + pdvList.size() + " ou " +
            // ou.getName() );

            // <editor-fold defaultstate="collapsed"
            // desc="For loop of orgUnitDataList">
            for ( PatientDataValue patientDataValue : pdvList )
            {

                ProgramStageInstance psi = patientDataValue.getProgramStageInstance();
                ProgramInstance pi = psi.getProgramInstance();
                String value = patientDataValue.getValue();
                Date executionDate = psi.getExecutionDate();
                Patient patient = pi.getPatient();
                int colNo = 0;
                int rowCount = 0;
                // <editor-fold defaultstate="collapsed"
                // desc="for loop for deCodesList">
                for ( String deCodeString : deCodesList )
                {
                    tempStr = "";
                    String sType = (String) serviceType.get( rowCount );
                    if ( !deCodeString.equalsIgnoreCase( "NA" ) )
                    {
                        // <editor-fold defaultstate="collapsed"
                        // desc="stype = caseProperty">
                        if ( sType.equalsIgnoreCase( "caseProperty" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "Name" ) )
                            {
                                tempStr = patient.getFullName();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "Age" ) )
                            {
                                tempStr = patient.getAge();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "Sex" ) )
                            {
                                if ( patient.getGender().equalsIgnoreCase( "M" ) )
                                {
                                    tempStr = "Male";
                                }
                                else if ( patient.getGender().equalsIgnoreCase( "F" ) )
                                {
                                    tempStr = "Female";
                                }
                                else
                                {
                                    tempStr = "";
                                }
                            }
                        } // </editor-fold>
                        // <editor-fold defaultstate="collapsed"
                        // desc="stype = caseAttribute">
                        else if ( sType.equalsIgnoreCase( "caseAttribute" ) )
                        {
                            int deCodeInt = Integer.parseInt( deCodeString );

                            PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                            PatientAttributeValue patientAttributeValue = patientAttributeValueService
                                .getPatientAttributeValue( patient, patientAttribute );
                            if ( patientAttributeValue != null )
                            {
                                tempStr = patientAttributeValue.getValue();
                            }
                            else
                            {
                                tempStr = " ";
                            }
                        } // </editor-fold>
                        // <editor-fold defaultstate="collapsed"
                        // desc="stype = identifiertype">
                        else if ( sType.equalsIgnoreCase( "identifiertype" ) )
                        {
                            int deCodeInt = Integer.parseInt( deCodeString );
                            // _______________________Id.
                            // no._______________________
                            PatientIdentifierType patientIdentifierType = patientIdentifierTypeService
                                .getPatientIdentifierType( deCodeInt );
                            if ( patientIdentifierType != null )
                            {
                                PatientIdentifier patientIdentifier = patientIdentifierService.getPatientIdentifier(
                                    patientIdentifierType, patient );
                                if ( patientIdentifier != null )
                                {
                                    tempStr = patientIdentifier.getIdentifier();
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        // </editor-fold>
                    }
                    else
                    {
                        // <editor-fold defaultstate="collapsed"
                        // desc="stype = srno">
                        if ( sType.equalsIgnoreCase( "srno" ) )
                        {
                            int tempNum = 1 + srno;
                            tempStr = String.valueOf( tempNum );
                        }
                        // </editor-fold>
                    }
                    // <editor-fold defaultstate="collapsed"
                    // desc="adding columns">
                    int tempColNo = colList.get( rowCount );
                    int sheetNo = sheetList.get( rowCount );
                    sheet0 = outputReportWorkbook.getSheet( sheetNo );
                   // WritableCell cell = sheet0.getWritableCell( tempColNo, rowNo );
                    // System.out.println(
                    // "_______________________ count = "+rowCount
                    // +"tempColNo = " + tempColNo + " rowNo = " + rowNo +
                    // " value = " + tempStr );
                    sheet0.addCell( new Label( tempColNo, rowNo, tempStr, wCellformat ) );
                    colNo = tempColNo;
                    // </editor-fold>

                    rowCount++;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed"
                // desc="adding des columns">
                int count = 0;
                for ( count = 0; count < des.size(); count++ )
                {
                    colNo++;
                    // DE Value
                    sheet0.addCell( new Label( colNo, rowNo, value, wCellformat ) );

                }
                colNo++;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed"
                // desc="adding executiondate">
                // Execution date
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                String eDate = simpleDateFormat.format( executionDate );
                sheet0.addCell( new Label( colNo, rowNo, "" + eDate, wCellformat ) );
                // </editor-fold>
                // <editor-fold defaultstate="collapsed"
                // desc="adding ou in report at the end column">
                OrganisationUnit ouname = ou;
                for ( int i = levelsList.size() - 1; i >= 0; i-- )
                {
                    colNo++;
                    int level = organisationUnitService.getLevelOfOrganisationUnit( ouname );
                    if ( levelsList.get( i ) == level )
                    {
                        sheet0.addCell( new Label( colNo, rowNo, ouname.getName(), wCellformat ) );
                        // System.out.println( colNo+" "+ rowNo+" "+
                        // ou.getName()+" "+ wCellformat );
                    }
                    ouname = ouname.getParent();
                }
                // </editor-fold>
                rowNo++;
                srno++;
            }
            // </editor-fold>
        }
        // </editor-fold>
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
        // Cell formatting

    }

    // <editor-fold defaultstate="collapsed" desc="getChildOrgUnitTree Method">
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="getDECodes method">

    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getenv( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + File.separator + raFolderName + File.separator + fileName;
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
                System.out.println( "There is no DECodes related XML file in the user home" );
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

                // System.out.println( deCodes.get( s )+" : "+deCodeType.get( s
                // ) );
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
    // </editor-fold>
}
