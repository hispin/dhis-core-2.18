package org.hisp.dhis.reports.portal.action;

//@23-06-2010 - Date from and to is solved.
//@23-06-2010 - for doses checking if dose is given before enddate then show it
//@23-06-2010 - formating done as per eclipse
//@23-06-2010 - for tt dates checking if execution date is before end date

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.reports.util.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;
import jxl.write.Label;
import jxl.write.WritableCell;
import org.dom4j.io.SAXReader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientdatavalue.PatientDataValue;

public class PortalReportsResultAction implements Action
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

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
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

    private ProgramStageDataElementService programStageDataElementService;

    public void setProgramStageDataElementService(
        ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
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

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
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

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Patient, Set<ProgramStageInstance>> visitsByPatients = new HashMap<Patient, Set<ProgramStageInstance>>();

    public Map<Patient, Set<ProgramStageInstance>> getVisitsByPatients()
    {
        return visitsByPatients;
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

    private String reportFileNameTB;

    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }

    private String reportModelTB;

    public void setReportModelTB( String reportModelTB )
    {
        this.reportModelTB = reportModelTB;
    }

    private String reportLevelTB;

    public void setReportLevelTB( String reportLevelTB )
    {
        this.reportLevelTB = reportLevelTB;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int periodList;

    public int getPeriodList()
    {
        return periodList;
    }

    public void setPeriodList( int periodList )
    {
        this.periodList = periodList;
    }

    private List<String> serviceType;

    private List<String> deCodeType;

    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private List<Integer> progList;

    private String raFolderName;

    private String inputTemplatePath;

    private String outputReportPath;

    private String deCodesXMLFileName;

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

    private int rowCount;

    private Date sDate;

    private Date eDate;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        //con = (new DBConnection()).openConnection();
        statementManager.initialise();
        raFolderName = reportService.getRAFolderName();

        // Initialization
        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        deCodesXMLFileName = reportList + "DECodes.xml";
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        progList = new ArrayList<Integer>();
        //System.out.println( "startDate = " + startDate + " endDate = " + endDate );
        Calendar c = Calendar.getInstance();
        c.setTime( format.parseDate( startDate ) );
        c.add( Calendar.DATE, -1 );  // number of days to add
        startDate = format.formatDate( c.getTime() );  // dt is now the new date
        c.setTime( format.parseDate( endDate ) );
        c.add( Calendar.DATE, 1 );  // number of days to add
        endDate = format.formatDate( c.getTime() );  // dt is now the new date
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        System.out.println( " reportList " + reportList );

        generatPortalReport();
        statementManager.destroy();

        return SUCCESS;
    }

    public void generatPortalReport() throws Exception
    {
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        // //System.out.println( "outputReportWorkbook = "+outputReportWorkbook);
        // Cell formatting
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        WritableCellFormat deWCellformat = new WritableCellFormat();
        deWCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        deWCellformat.setAlignment( Alignment.CENTRE );
        deWCellformat.setVerticalAlignment( VerticalAlignment.JUSTIFY );
        deWCellformat.setWrap( true );

        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        //System.out.println("______________ " + ouIDTB);
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        //Collection<PatientIdentifier> patientIdentifiers = patientIdentifierService.getPatientIdentifiersByOrgUnit( selectedOrgUnit );

        Collection<Patient> patientListByOrgUnit = new ArrayList<Patient>();
        patientListByOrgUnit.addAll( patientService.getPatientsByOrgUnit( selectedOrgUnit ) );

        // Getting Programs
        rowCount = 0;
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        String[] programNames = reportLevelTB.split( "," );

        String tempStr = "";
        String dataelementWithStage = "";
        String programStages = "";
        Collection<ProgramStage> programStagesList = new ArrayList<ProgramStage>();
        if ( programNames.length != 0 )
        {
            for ( int pn = 0; pn < programNames.length; pn++ )
            {
                Program curProgram = programService.getProgram( Integer.parseInt( programNames[pn] ) );
                if ( curProgram != null )
                {
                    System.out.println( "curProgram = " + curProgram.getName() );
                    programStagesList = curProgram.getProgramStages();
                    int count1 = 0;


                    Map<String, String> childPhoneNo = new HashMap<String, String>();
                    childPhoneNo.put( "Others", "Immediate Relations" );
                    childPhoneNo.put( "Neighbor", "Neighbor" );
                    childPhoneNo.put( "Mother", "Parents" );
                    childPhoneNo.put( "Father", "Parents" );
                    childPhoneNo.put( "Husband", "Immediate Relations" );

                    Map<String, String> dhisPortalMap = new HashMap<String, String>();

                    //phone no of whom
                    dhisPortalMap.put( "Others", "Others" );
                    dhisPortalMap.put( "Neighbor", "Neighbor" );
                    dhisPortalMap.put( "Mother", "Relative" );
                    dhisPortalMap.put( "Father", "Relative" );
                    dhisPortalMap.put( "Husband", "Relative" );
                    dhisPortalMap.put( "Self", "Self" );

                    //putting jsy beneficiary / rti/sti / complication / pnc checkup / breast feeded in map
                    dhisPortalMap.put( "true", "Yes" );
                    dhisPortalMap.put( "false", "No" );

                    //putting linked facility / place of delivery
                    dhisPortalMap.put( "(Sub Centre)", "Sub-center" );
                    dhisPortalMap.put( "(PHC)", "PHC" );
                    dhisPortalMap.put( "(CHC)", "CH" );
                    dhisPortalMap.put( "(SDH)", "SDH" );
                    dhisPortalMap.put( "(DH)", "DH" );
                    //anemia
                    dhisPortalMap.put( "(Normal)", "Normal" );
                    dhisPortalMap.put( "(Moderate <11)", "(Moderate<11" );
                    dhisPortalMap.put( "(Severe <7)", "Severe<7" );
                    //anc Complication
                    dhisPortalMap.put( "(ANC None)", "None" );
                    dhisPortalMap.put( "(Hypertensive)", "Hypertensive" );
                    dhisPortalMap.put( "(Diabetics)", "Diabetics" );
                    dhisPortalMap.put( "(APH)", "APH" );
                    dhisPortalMap.put( "(Malaria)", "Malaria" );
                    //place of delivery home type
                    dhisPortalMap.put( "(Home non SBA)", "Non SBA" );
                    dhisPortalMap.put( "(Home SBA)", "SBA" );
                    //place of delivery public
                    dhisPortalMap.put( "(Sub Centre)", "Sub Centre" );
                    dhisPortalMap.put( "(PHC.)", "PHC" );
                    dhisPortalMap.put( "(CHC.)", "CH" );
                    dhisPortalMap.put( "(SDH.)", "SDH" );
                    dhisPortalMap.put( "(DH.)", "DH" );
                    //place of delivery private
                    dhisPortalMap.put( "(Private)", "Private" );
                    List<String> podHomeList = new ArrayList<String>();
                    podHomeList.add( "Non SBA" );
                    podHomeList.add( "SBA" );

                    List<String> podPublicList = new ArrayList<String>();
                    podPublicList.add( "Sub Centre" );
                    podPublicList.add( "PHC" );
                    podPublicList.add( "CH" );
                    podPublicList.add( "SDH" );
                    podPublicList.add( "DH" );

                    List<String> podPrivateList = new ArrayList<String>();
                    podPrivateList.add( "Private" );
                    //delivery type

                    dhisPortalMap.put( "(Normal.)", "Normal" );
                    dhisPortalMap.put( "(C Section)", "CS" );
                    dhisPortalMap.put( "(Instrumental)", "Instrumental" );
                    //abortion
                    dhisPortalMap.put( "(MTP < 12 Weeks)", "MTP<12" );
                    dhisPortalMap.put( "(MTP > 12 Weeks)", "MTP>12" );
                    dhisPortalMap.put( "(Spontaneous)", "Spontaneous" );//not thr in excel sheet
                    dhisPortalMap.put( "(None)", "None" );
                    //pnc visit
                    dhisPortalMap.put( "(with in 7 days)", "Within 7 days" );
                    dhisPortalMap.put( "(With in 48 hrs)", "Within 48 hours" );
                    //pnc complications
                    dhisPortalMap.put( "(None.)", "None" );
                    dhisPortalMap.put( "(Sepsis)", "Sepsis" );
                    dhisPortalMap.put( "(PPH)", "PPH" );
                    dhisPortalMap.put( "(Death)", "PPH" );
                    dhisPortalMap.put( "(Others.)", "Others" );
                    //pp contrapception
                    dhisPortalMap.put( "(Other method)", "None" );
                    dhisPortalMap.put( "(Sterilisation)", "Sterilisation" );
                    dhisPortalMap.put( "(IUD)", "IUD" );
                    dhisPortalMap.put( "(Injectibles)", "Injectibles" );
                    //child health
                    //blood group
                    dhisPortalMap.put( "A+", "A+" );
                    dhisPortalMap.put( "A-", "A-" );
                    dhisPortalMap.put( "AB+", "AB+" );
                    dhisPortalMap.put( "AB-", "AB-" );
                    dhisPortalMap.put( "B+", "B+" );
                    dhisPortalMap.put( "B-", "B-" );
                    dhisPortalMap.put( "O+", "O+" );
                    dhisPortalMap.put( "O-", "O-" );
                    //gender
                    dhisPortalMap.put( "M", "Male" );
                    dhisPortalMap.put( "F", "Female" );

                    Map<String, String> mFNameMap = new HashMap<String, String>();
                    mFNameMap.put( "Father", "Father's" );
                    mFNameMap.put( "Mother", "Mother's" );

                    // <editor-fold defaultstate="collapsed" desc="Get all Maps">
                  /*  File allMaps = new File( //System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
                    + File.separator + "\\DhisPortalMap.xml");
                    if (allMaps.exists()) {
                    SAXReader reader = new SAXReader();
                    org.dom4j.Document document = reader.read(allMaps.toURI().toURL());
                    org.dom4j.Element root = document.getRootElement();
                    org.dom4j.XPath xpathSelector = new Dom4jXPath("//patientIDs/available");
                    List list = xpathSelector.selectNodes(document);
                    if (list.size() > 0) {
                    if (list.size() < 50) {
                    registerPatientModel.setMessage("Very Few IDs (" + list.size() + ") Left. Please Upload New IDs");
                    }
                    registerPatientModel.setIdentifierString(((Element) list.get(0)).getText());
                    Element usedNode = (Element) list.get(0);
                    String usedIdentifier = usedNode.getText();
                    root.remove(usedNode);
                    root.addElement("locked").addText(usedIdentifier);
                    FileWriter fw = new FileWriter(availableIDs);
                    OutputFormat format = OutputFormat.createPrettyPrint();
                    XMLWriter writer = new XMLWriter(fw, format);
                    writer.write(document);
                    writer.close();
                    } else {
                    registerPatientModel.setMessage("No Identifiers Left. Please Upload New IDs");
                    }
                    } else {
                    registerPatientModel.setMessage("AvailableIDs File Not Found. Please Upload Generated File from Options Page");
                    }*/
                    // </editor-fold>

                    //Checking whether there is stages or not.
                    if ( programStagesList == null || programStagesList.isEmpty() )
                    {
                        //System.out.println( "No prgram stages" );
                    }

                    Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();

                    programInstances = programInstanceService.getProgramInstances( curProgram, false );
                    if ( programInstances == null || programInstances.isEmpty() )
                    {
                        //System.out.println( "No prgram Instances" );
                    }

                    List<Patient> patientList = new ArrayList<Patient>();
                    Map<Patient, ProgramInstance> patientPIList = new HashMap<Patient, ProgramInstance>();
                    Map<ProgramInstance, Collection<ProgramStageInstance>> PIPSIList = new HashMap<ProgramInstance, Collection<ProgramStageInstance>>();
                    Map<ProgramInstance, Collection<ProgramStageInstance>> PIAllPSIList = new HashMap<ProgramInstance, Collection<ProgramStageInstance>>();
                    for ( ProgramInstance programInstance : programInstances )
                    {
                        Patient patient = programInstance.getPatient();
                        //taking patient present in selected orgunit
                        if ( !patientListByOrgUnit.contains( patient ) )
                        {
                            continue;
                        }
                        Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();
                        Collection<ProgramStageInstance> allProgramStageInstances = new ArrayList<ProgramStageInstance>();
                        Iterator itr1 = programStagesList.iterator();
                        while ( itr1.hasNext() )
                        {
                            ProgramStage PSName = (ProgramStage) itr1.next();
                            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programInstance, PSName );
                            if ( programStageInstance != null )
                            {
                                //putting all stageinstances in one list
                                allProgramStageInstances.add( programStageInstance );
                                //taking programstageinstace wich are between startdate and enddate
                                if ( programStageInstance.getDueDate().after( sDate ) && programStageInstance.getDueDate().before( eDate ) )
                                {
                                    programStageInstances.add( programStageInstance );
                                    //System.out.println( "programInstance = " + programInstance.getId() + " programStageInstances = " + programStageInstance.getId() );
                                    //System.out.println("programStageInstances size = "+programStageInstances.size());
                                }
                            }
                        }
                        if ( programInstance != null && programStageInstances.size() != 0 )
                        {
                            //putting pi and psi together
                            //System.out.println( "putting in pipsilist = " + programInstance.getId() + " " + programStageInstances.size() );
                            PIPSIList.put( programInstance, programStageInstances );
                            PIAllPSIList.put( programInstance, allProgramStageInstances );
                            //putting patient and pi together
                            patientPIList.put( patient, programInstance );
                            patientList.add( patient );

                        }

                    }
                    int sheetNo = 0;
                    rowCount = 0;
                    //running patient loop
                    for ( Patient patient : patientList )
                    {
                        ProgramInstance programInstance = patientPIList.get( patient );
                        String cAPhoneNumberName = "";
                        //System.out.println( "______________________________ patient = " + patient.getFullName() );
                        //System.out.println( "__________________________________________________________" );

                        count1 = 0;
                        int rowNo = rowList.get( 1 ) + rowCount;
                        for ( String deCodeString : deCodesList )
                        {
                            int tempColNo = colList.get( count1 );
                            sheetNo = sheetList.get( count1 );
                            //rowNo = rowNo+rowCount;
//                            ///System.out.println(progList.get( count1 ) + " : " +curProgram.getId());
                            tempStr = "";
                            String sType = (String) serviceType.get( count1 );
                            if ( progList.get( count1 ) == curProgram.getId() )
                            {
                                //System.out.println( progList.get( count1 ) + " : " + curProgram.getId() + " deCodeString = " + deCodeString );
                                if ( sType.equalsIgnoreCase( "srno" ) )
                                {
                                    int tempNum = 1 + rowCount;
                                    tempStr = String.valueOf( tempNum );
                                    //System.out.println( "srno = " + tempNum + " " + tempStr );
                                }
                                if ( !deCodeString.equalsIgnoreCase( "NA" ) )
                                {
                                    //System.out.println("deCodeString = "+deCodeString + "sType = "+sType);
                                    //_______________________identifiertype_______________________
                                    if ( sType.equalsIgnoreCase( "identifiertype" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        //_______________________Id. no._______________________
                                        PatientIdentifierType patientIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( deCodeInt );
                                        if ( patientIdentifierType != null )
                                        {
                                            PatientIdentifier patientIdentifier = patientIdentifierService.getPatientIdentifier( patientIdentifierType, patient );
                                            if ( patientIdentifier != null )
                                            {
                                                tempStr = patientIdentifier.getIdentifier();
                                            } else
                                            {
                                                tempStr = " ";
                                            }
                                        }
                                    } else if ( sType.equalsIgnoreCase( "caseAttribute" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );

                                        PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                                        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                                        if ( patientAttributeValue != null )
                                        {
                                            tempStr = patientAttributeValue.getValue();
                                            if ( dhisPortalMap.containsKey( tempStr ) )
                                            {
                                                tempStr = dhisPortalMap.get( tempStr );
                                            }
                                        } else
                                        {
                                            tempStr = " ";
                                        }
                                        //System.out.println( "patientAttribute = " + patientAttribute.getName() + " value = " + tempStr );
                                    } else if ( sType.equalsIgnoreCase( "dataelementstage" ) || sType.equalsIgnoreCase( "dataelementstagePODPublic" ) || sType.equalsIgnoreCase( "dataelementstagePODPrivate" ) || sType.equalsIgnoreCase( "dataelementstagePODHome" ) )
                                    {
                                        //_______________________dataelementstage_______________________
                                        dataelementWithStage = deCodeString;
                                        String[] deAndPs = dataelementWithStage.split( "\\." );
                                        int psId = Integer.parseInt( deAndPs[0] );
                                        int deId = Integer.parseInt( deAndPs[1] );

                                        DataElement d1e = dataElementService.getDataElement( deId );
                                        //System.out.println( "dataelement = " + d1e.getName() );
                                        ProgramStageInstance pStageInstance = programStageInstanceService.getProgramStageInstance( programInstance, programStageService.getProgramStage( psId ) );

                                        if ( PIPSIList.get( programInstance ).contains( pStageInstance ) || programInstance.isCompleted() == false )
                                        {
                                            if ( pStageInstance.getExecutionDate() != null && pStageInstance.getExecutionDate().before( eDate ) )
                                            {
                                                PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( pStageInstance, d1e, selectedOrgUnit );

                                                if ( patientDataValue1 == null )
                                                {
                                                    tempStr = " ";
                                                } else if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_STRING ) && d1e.isMultiDimensional() )
                                                {
                                                    DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( patientDataValue1.getValue() ) );
                                                    String decocName = dataElementCategoryOptionCombo.getName();
                                                    if ( dhisPortalMap.containsKey( decocName ) )
                                                    {
                                                        decocName = dhisPortalMap.get( decocName );
                                                        if ( sType.equalsIgnoreCase( "dataelementstagePODPublic" ) )
                                                        {
                                                            if ( podPublicList.contains( decocName ) )
                                                            {
                                                                tempStr = decocName;
                                                            }
                                                        } else if ( sType.equalsIgnoreCase( "dataelementstagePODPrivate" ) )
                                                        {
                                                            if ( podPrivateList.contains( decocName ) )
                                                            {
                                                                tempStr = decocName;
                                                            }
                                                        } else if ( sType.equalsIgnoreCase( "dataelementstagePODHome" ) )
                                                        {
                                                            if ( podHomeList.contains( decocName ) )
                                                            {
                                                                tempStr = decocName;
                                                            }
                                                        } else
                                                        {
                                                            tempStr = decocName;
                                                        }
                                                    }
                                                } else
                                                {
                                                    tempStr = patientDataValue1.getValue();
                                                    //System.out.println( "PatientDataValue = " + tempStr);
                                                    if ( dhisPortalMap.containsKey( tempStr ) )
                                                    {
                                                        tempStr = dhisPortalMap.get( tempStr );
                                                        //System.out.println( "PatientDataValue = " + tempStr);
                                                    }
                                                }

                                            }
                                        } else
                                        {
                                            tempStr = " ";
                                        }
                                    } else if ( sType.equalsIgnoreCase( "dataelement" ) )
                                    {
                                        if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                                        {
                                            tempStr = selectedOrgUnit.getName();
                                        } else
                                        {
                                            int deCodeInt = Integer.parseInt( deCodeString );
                                            //_______________________all programdes_______________________
                                            DataElement d1e = dataElementService.getDataElement( deCodeInt );
                                            //System.out.println( "dataelement = "+d1e.getName() );
                                            Collection<ProgramStageInstance> programStageInstances = PIAllPSIList.get( programInstance );
                                            Iterator<ProgramStageInstance> itrPSI = programStageInstances.iterator();
                                            while ( itrPSI.hasNext() )
                                            {
                                                ProgramStageInstance programStageInstance = itrPSI.next();
                                                //System.out.println( "programStageInstance " + programStageInstance.getId() + " programinstance = " + programInstance.getId() + " de = "+d1e.getId() );
                                                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, d1e, selectedOrgUnit );
                                                if ( patientDataValue != null )
                                                {
                                                        //System.out.println( "value = " + patientDataValue.getValue() + "programStageInstance " + programStageInstance.getId());
                                                        if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_STRING ) && d1e.isMultiDimensional() )
                                                        {

                                                            DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( Integer.parseInt( patientDataValue.getValue() ) );
                                                            tempStr = dataElementCategoryOptionCombo.getName();
                                                            if ( dhisPortalMap.containsKey( tempStr ) )
                                                            {
                                                                tempStr = dhisPortalMap.get( tempStr );
                                                                //System.out.println("tempStr = "+tempStr);
                                                            }
                                                        } else if ( d1e.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_DATE ) )
                                                        {
                                                            String str = patientDataValue.getValue();
                                                            if ( str != null )
                                                            {
                                                                System.out.println( "Date type value = " + str + " programStageInstance " + programStageInstance.getExecutionDate() );
                                                                SimpleDateFormat simpleLmpDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                                                                Date doseDate = simpleLmpDateFormat.parse( str );
                                                                if ( doseDate.before( eDate ) )
                                                                {
                                                                    tempStr = simpleLmpDateFormat.format( doseDate );
                                                                } else
                                                                {
                                                                    tempStr = "";
                                                                }
                                                            } else
                                                            {
                                                                tempStr = "";
                                                            }
                                                        } else
                                                        {

                                                            tempStr = patientDataValue.getValue();
                                                            //System.out.println("tempStr = "+tempStr);
                                                            if ( dhisPortalMap.containsKey( tempStr ) )
                                                            {
                                                                tempStr = dhisPortalMap.get( tempStr );
                                                            }

                                                        }
                                                } else
                                                {
                                                    continue;
                                                }
                                                //System.out.println( "tempStr = " + tempStr );

                                            }
                                        }
                                        //
                                    } else if ( sType.equalsIgnoreCase( "caseProperty" ) )
                                    {
                                        //_______________________patient name_______________________
                                        if ( deCodeString.equalsIgnoreCase( "Name" ) )
                                        {
                                            tempStr = patient.getFullName();
                                        } else if ( deCodeString.equalsIgnoreCase( "DOB" ) )
                                        {
                                            //_______________________DateOfBirth_______________________
                                            Date patientDate = patient.getBirthDate();
                                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
                                            tempStr = simpleDateFormat1.format( patientDate );
                                            //System.out.println("DOB = "+tempStr);
                                        } else if ( deCodeString.equalsIgnoreCase( "LMP" ) )
                                        {
                                            //_______________________LMP_______________________
                                            Date lmpDate = programInstance.getDateOfIncident();
                                            SimpleDateFormat simpleLmpDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                                            tempStr = simpleLmpDateFormat.format( lmpDate );
                                        } else if ( deCodeString.equalsIgnoreCase( "PNCCheck" ) )
                                        {
                                            //_______________________pnc checkup_______________________
                                            ProgramStage ps = programStageService.getProgramStage( 7 );
                                            if ( curProgram.getProgramStages().contains( ps ) )
                                            {
                                                ProgramStageInstance psi = programStageInstanceService.getProgramStageInstance( programInstance, ps );
                                                if ( psi.getExecutionDate() != null )
                                                {
                                                    tempStr = "Yes";
                                                } else
                                                {
                                                    tempStr = "No";
                                                }
                                            }
                                        } else if ( deCodeString.equalsIgnoreCase( "BloodGroup" ) )
                                        {
                                            //_______________________Blood group_______________________
                                            String bloodGroup = patient.getBloodGroup();
                                            if ( !bloodGroup.trim().equalsIgnoreCase( "" ) )
                                            {
                                                if ( dhisPortalMap.containsKey( bloodGroup ) )
                                                {
                                                    tempStr = dhisPortalMap.get( bloodGroup );
                                                    //System.out.println("tempStr = "+tempStr);
                                                }
                                            } else
                                            {
                                                tempStr = "N.A";
                                            }
                                        } else if ( deCodeString.equalsIgnoreCase( "MotherId" ) )
                                        {
                                            Patient representative = patient.getRepresentative();
                                            if ( representative != null )
                                            {
                                                String gender = representative.getGender();
                                                if ( gender.equalsIgnoreCase( "F" ) )
                                                {
                                                    tempStr = patientIdentifierService.getPatientIdentifier( representative ).getIdentifier();
                                                }
                                                //System.out.println("Gender = "+gender + " temStr = "+tempStr);
                                            }
                                        }
                                    } else if ( sType.equalsIgnoreCase( "dataelementIFA" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        //_______________________all programdes_______________________
                                        DataElement d1e = dataElementService.getDataElement( deCodeInt );
                                        Collection<ProgramStageInstance> programStageInstances = PIPSIList.get( programInstance );
                                        Iterator<ProgramStageInstance> itrPSI = programStageInstances.iterator();
                                        int ifaCount = 0;

                                        while ( itrPSI.hasNext() )
                                        {
                                            ProgramStageInstance programStageInstance = itrPSI.next();
                                            //System.out.println( "programStageInstance = "+programStageInstance.getId() + " de = "+d1e.getId() );
                                            PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue( programStageInstance, d1e, selectedOrgUnit );
                                            if ( patientDataValue1 != null )
                                            {

                                                tempStr = patientDataValue1.getValue();
                                            } else
                                            {
                                                tempStr = "";
                                            }
                                        }
                                    }//end of if ifa
                                    else if ( sType.equalsIgnoreCase( "caseAttributePN" ) || sType.equalsIgnoreCase( "caseAttributeHusband" ) || sType.equalsIgnoreCase( "caseAttributeMFName" ) )
                                    {
                                        int deCodeInt = Integer.parseInt( deCodeString );
                                        //System.out.println( "deCodeInt = " + deCodeInt );
                                        PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( deCodeInt );
                                        //System.out.println( "patientAttribute = " + patientAttribute.getName() );
                                        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                                        String name = "";
                                        if ( patientAttributeValue != null )
                                        {
                                            if ( sType.equalsIgnoreCase( "caseAttributePN" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                //System.out.println( "name = " + name );
                                                if ( curProgram.getId() == 1 && dhisPortalMap.containsKey( name ) )
                                                {
                                                    cAPhoneNumberName = name;
                                                    tempStr = dhisPortalMap.get( name );
                                                }
                                                if ( curProgram.getId() == 2 && childPhoneNo.containsKey( name ) )
                                                {
                                                    cAPhoneNumberName = name;
                                                    tempStr = childPhoneNo.get( name );
                                                }
                                                //System.out.println( "name = " + name + " cAPhoneNumberName = " + cAPhoneNumberName );
                                            }
                                            if ( sType.equalsIgnoreCase( "caseAttributeHusband" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                if ( cAPhoneNumberName.equals( "Husband" ) )
                                                {
                                                    tempStr = name;
                                                }
                                            }
                                            if ( sType.equalsIgnoreCase( "caseAttributeMFName" ) )
                                            {
                                                name = patientAttributeValue.getValue();
                                                if ( cAPhoneNumberName.equals( "Mother" ) || cAPhoneNumberName.equals( "Father" ) )
                                                {
                                                    tempStr = mFNameMap.get( cAPhoneNumberName );
                                                }
                                            }
                                        } else
                                        {
                                            tempStr = " ";
                                        }
                                    }
                                }// end of if for excluding NA and program stages
                                sheet = outputReportWorkbook.getSheet( sheetNo );
                                WritableCell cell = sheet.getWritableCell( tempColNo, rowNo );

                                sheet.addCell( new Label( tempColNo, rowNo, tempStr, wCellformat ) );
                                //System.out.println( "tempColNo = " + tempColNo + " rowNo = " + rowNo + " value = " + tempStr );

                            }// end of checking program no is same or not
                            count1++;
                        }//end of decodelist for loop
                        rowCount++;
                        rowNo++;
                    }//end of patient for loop

                }//end of curprogram if loop
            }//end of for loop for programs
        }//end of programs if

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );

        fileName += "_" + selectedOrgUnit.getShortName() + ".xls";
        //System.out.println( "fileName = " + fileName + " outputReportPath = " + outputReportPath );

        File outputReportFile = new File( outputReportPath );

        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }
    /*
     * Returns a list which contains the DataElementCodes
     */

    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getenv( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + File.separator + raFolderName + File.separator + fileName;
            }

        } catch ( NullPointerException npe )
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
                //System.out.println( "There is no DECodes related XML file in the user home" );
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s <
                totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                deCodes.add( ( (Node) textDECodeList.item( 0 ) ).getNodeValue().trim() );
                serviceType.add( deCodeElement.getAttribute( "stype" ) );
                deCodeType.add( deCodeElement.getAttribute( "type" ) );
                sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );
                progList.add( new Integer( deCodeElement.getAttribute( "progno" ) ) );

                //System.out.println(deCodes.get( s )+" : "+rowList.get( s ));
            }// end of for loop with s var

        }// try block end
        catch ( SAXParseException err )
        {
            //System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.get//SystemId() );
            //System.out.println( " " + err.getMessage() );
        } catch ( SAXException e )
        {
            Exception x = e.getException();
            ( ( x == null ) ? e : x ).printStackTrace();
        } catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return deCodes;
    }// getDECodes end

    public Period getPreviousPeriod(
        Date sDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        } else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }

        PeriodType periodType = getPeriodTypeObject( "monthly" );
        period =
            getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
            periodType );

        return period;
    }

    public Period getNextPeriod(
        Date sDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.DECEMBER )
        {
            tempDate.set( Calendar.MONTH, Calendar.JANUARY );
            tempDate.roll( Calendar.YEAR, +1 );
        } else
        {
            tempDate.roll( Calendar.MONTH, +1 );
        }

        PeriodType periodType = getPeriodTypeObject( "monthly" );
        period =
            getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ),
            periodType );

        return period;
    }

    public Period getPeriodByMonth(
        int month, int year, PeriodType periodType )
    {
        int monthDays[] =
        {
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            } else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }

        } else
        {
            if ( periodType.getName().equals( "Yearly" ) )
            {
                cal.set( year, Calendar.DECEMBER, 31 );
            }

        }
        Date lastDay = new Date( cal.getTimeInMillis() );
        //System.out.println( lastDay.toString() );
        Period newPeriod = new Period();
        newPeriod =
            periodService.getPeriod( firstDay, lastDay, periodType );
        return newPeriod;
    }

    public PeriodType getPeriodTypeObject(
        String periodTypeName )
    {
        Collection periodTypes = periodService.getAllPeriodTypes();
        PeriodType periodType = null;
        Iterator iter = periodTypes.iterator();
        while ( iter.hasNext() )
        {
            PeriodType tempPeriodType = (PeriodType) iter.next();
            if ( tempPeriodType.getName().toLowerCase().trim().equals( periodTypeName ) )
            {
                periodType = tempPeriodType;

                break;

            }












        }
        if ( periodType == null )
        {
            //System.out.println( "No Such PeriodType" );
            return null;
        }

        return periodType;
    }

    public List<Calendar> getStartingEndingPeriods( String deType, Date sDate, Date eDate )
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod =
            getPreviousPeriod( sDate );

        if ( deType.equalsIgnoreCase( "cpmcy" ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }

            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( previousPeriod.getEndDate() );
        } else
        {
            if ( deType.equalsIgnoreCase( "ccmcy" ) )
            {
                tempStartDate.setTime( sDate );
                if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
                {
                    tempStartDate.roll( Calendar.YEAR, -1 );
                }

                tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
                tempEndDate.setTime( eDate );
                // //System.out.println("We are in PT if block");
            } else
            {
                if ( deType.equalsIgnoreCase( "ccmpy" ) )
                {
                    tempStartDate.setTime( sDate );
                    tempEndDate.setTime( eDate );

                    tempStartDate.roll( Calendar.YEAR, -1 );
                    tempEndDate.roll( Calendar.YEAR, -1 );
                } else
                {
                    if ( deType.equalsIgnoreCase( "cmpy" ) )
                    {
                        tempStartDate.setTime( sDate );
                        tempEndDate.setTime( eDate );

                        tempStartDate.roll( Calendar.YEAR, -1 );
                        tempEndDate.roll( Calendar.YEAR, -1 );
                    } else
                    {
                        tempStartDate.setTime( sDate );
                        tempEndDate.setTime( eDate );
                    }

                }
            }
        }

        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }
}
