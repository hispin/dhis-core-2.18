package org.hisp.dhis.reports.activeplan.action;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import java.util.HashSet;
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
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.reports.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

/**
 * @23-06-2010 - Date from and to is solved.
 * TODO: merging of cells for same village
 *
 */
public class ActivePlanReportsResultAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private RelationshipService relationshipService;

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

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

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
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

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
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

    private String raFolderName;

    // private DeCodesXML deCodeTotalList;
    // private int[] monthCount = { 0, 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    private Connection con = null;

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

    private Date sDate;

    private Date eDate;

    private int rowCount;

    // private String orgUnitInfo = "-1";
    // private String aggDataTableName;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {

        // con = (new DBConnection()).openConnection();

        statementManager.initialise();
        // raFolderName = "ra_uttarakhand";

        raFolderName = reportService.getRAFolderName();

        // Initialization
        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        deCodesXMLFileName = reportList + "DECodes.xml";
        // System.out.println("reportList = " + reportList);
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        System.out
            .println( "startDate = " + startDate + " endDate = " + endDate + " reportname  = " + reportFileNameTB );
        Calendar c = Calendar.getInstance();
        c.setTime( format.parseDate( startDate ) );
        c.add( Calendar.DATE, -1 ); // number of days to add
        startDate = format.formatDate( c.getTime() ); // dt is now the new date
        c.setTime( format.parseDate( endDate ) );
        c.add( Calendar.DATE, 1 ); // number of days to add
        endDate = format.formatDate( c.getTime() ); // dt is now the new date
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template"
            + File.separator + reportFileNameTB;
        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output"
            + File.separator + UUID.randomUUID().toString() + ".xls";

        generatFeedbackReport();
        statementManager.destroy();

        return SUCCESS;
    }

    public void generatFeedbackReport()
        throws Exception
    {
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook
            .createWorkbook( new File( outputReportPath ), templateWorkbook );
        // System.out.println( "outputReportWorkbook = "+outputReportWorkbook);
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

        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        // System.out.println("______________ " + reportLevelTB);
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );

        // Collection<PatientIdentifier> patientIdentifiers =
        // patientIdentifierService.getPatientIdentifiersByOrgUnit(
        // selectedOrgUnit );

        Collection<Patient> patientListByOrgUnit = new ArrayList<Patient>();
        patientListByOrgUnit.addAll( patientService.getPatients( selectedOrgUnit ) );

        // Getting Programs
        rowCount = 0;
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        Program curProgram = programService.getProgram( Integer.parseInt( reportLevelTB ) );
        // /System.out.println( "curProgram = " + curProgram.getName() );
        String tempStr = "";

        int villageAttrId = 0;

        if ( curProgram != null )
        {
            String psDeIds = "";
            WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
            System.out.println( "curProgram = " + curProgram.getName() );
            int count1 = 0;
            int tempRowNo = 0;
            int rowStart = 0;
            // System.out.println("deCodesList size = " + deCodesList.size());
            Collection<ProgramStage> programStagesList = new ArrayList<ProgramStage>();
            for ( String deCodeString : deCodesList )
            {
                String sType = (String) serviceType.get( count1 );//
                // System.out.println( sType );
                if ( sType.equalsIgnoreCase( "rowStart" ) )
                {
                    rowStart = Integer.parseInt( deCodeString );
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = selectedOrgUnit.getShortName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    OrganisationUnit orgUnitP = new OrganisationUnit();
                    orgUnitP = selectedOrgUnit.getParent();
                    if ( orgUnitP != null )
                    {
                        tempStr = orgUnitP.getName();
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    OrganisationUnit orgUnitP = new OrganisationUnit();
                    OrganisationUnit orgUnitPP = new OrganisationUnit();
                    orgUnitP = selectedOrgUnit.getParent();
                    if ( orgUnitP != null )
                    {
                        orgUnitPP = orgUnitP.getParent();
                        if ( orgUnitPP != null )
                        {
                            tempStr = orgUnitPP.getName();
                        }
                    }
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    // sheet0.addCell(new Label(tempColNo, tempRowNo,
                    // simpleDateFormat.format(startDate.getStartDate()),
                    // wCellformat));
                }
                else if ( sType.equalsIgnoreCase( "programStages" ) )
                {
                    String[] stages = deCodeString.split( "," );
                    for ( String stage : stages )
                    {
                        programStagesList.add( programStageService.getProgramStage( Integer.parseInt( stage ) ) );
                    }
                }
                else if ( sType.equalsIgnoreCase( "immunizationPS" ) )
                {
                    psDeIds = deCodeString;
                }
                else if ( sType.equalsIgnoreCase( "caseAttributeVillage" ) )
                {
                    villageAttrId = Integer.parseInt( deCodeString );
                }

                if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    // sheet0.addCell( new Label( tempColNo, tempRowNo,
                    // simpleDateFormat.format( sDate.getStartDate() ),
                    // wCellformat ) );
                }
                else
                {
                    if ( (deCodeString.equalsIgnoreCase( "FACILITY" ))
                        || (deCodeString.equalsIgnoreCase( "FACILITYP" ))
                        || (deCodeString.equalsIgnoreCase( "FACILITYPP" )) || (deCodeString.equalsIgnoreCase( "NA" )) )
                    {
                        tempRowNo = rowList.get( count1 );
                        int tempColNo = colList.get( count1 );
                        int sheetNo = sheetList.get( count1 );
                        sheet0 = outputReportWorkbook.getSheet( sheetNo );
                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );
                        // System.out.println("tempStr "+tempStr);
                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                    }
                }
                count1++;
            }

            Map<ProgramStage, Set<DataElement>> programStageDEs = new HashMap<ProgramStage, Set<DataElement>>();
            // Collection<ProgramStageInstance> programStageInstances = new
            // ArrayList<ProgramStageInstance>();
            if ( !psDeIds.equals( "" ) )
            {
                String[] Ids = psDeIds.split( "," );

                for ( int i = 0; i < Ids.length; i++ )
                {
                    // 9:80.81.82
                    // reading one program stage : its dataelements
                    String[] allIds = Ids[i].split( ":" );
                    // spliting with : so getting psid and all datalements ids
                    // System.out.println( "allIds = " + Ids[i] + " size = " +
                    // allIds.length );
                    String psid = "";
                    if ( allIds.length >= 1 )
                    {
                        psid = allIds[0];// according to ex 9
                    }
                    Set<DataElement> dataElements = new HashSet<DataElement>();
                    if ( allIds.length > 1 )
                    {
                        String deIds = allIds[1];// according to ex 80.81.82

                        String[] allDeIds = deIds.split( "\\." );// spliting des
                                                                 // with .
                        for ( int j = 0; j < allDeIds.length; j++ )
                        {
                            // putting des in list
                            dataElements.add( dataElementService.getDataElement( Integer.parseInt( allDeIds[j] ) ) );
                        }
                    }
                    ProgramStage programStage = programStageService.getProgramStage( Integer.parseInt( psid ) );
                    programStagesList.add( programStage );
                    // putting ps and des in one map
                    programStageDEs.put( programStage, dataElements );
                }
            }

            if ( programStagesList == null || programStagesList.isEmpty() )
            {
                System.out.println( "No prgram stages" );
            }

            Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();

            programInstances = programInstanceService.getProgramInstances( curProgram, false );

            if ( programInstances == null || programInstances.isEmpty() )
            {
                System.out.println( "No prgram Instances" );
            }

            List<Patient> patientList = new ArrayList<Patient>();
            Map<Patient, ProgramInstance> patientPIList = new HashMap<Patient, ProgramInstance>();
            Map<ProgramInstance, Collection<ProgramStageInstance>> PIPSIList = new HashMap<ProgramInstance, Collection<ProgramStageInstance>>();
            Map<Patient, Collection<ProgramStageInstance>> patientCompletedPSIList = new HashMap<Patient, Collection<ProgramStageInstance>>();
            Collection<Patient> sortedPatientList = new ArrayList<Patient>();
            for ( ProgramInstance programInstance : programInstances )
            {
                Patient patient = programInstance.getPatient();
                // taking patient present in selected orgunit
                if ( !patientListByOrgUnit.contains( patient ) )
                {
                    continue;
                }

                Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();
                Collection<ProgramStageInstance> completedProgramStageInstances = new ArrayList<ProgramStageInstance>();
                Iterator<ProgramStage> itr1 = programStagesList.iterator();
                while ( itr1.hasNext() )
                {
                    ProgramStage PSName = (ProgramStage) itr1.next();
                    ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance(
                        programInstance, PSName );
                    if ( programStageInstance != null )
                    {
                        if ( programStageInstance.getExecutionDate() != null )
                        {
                            completedProgramStageInstances.add( programStageInstance );
                            continue;
                        }
                        // System.out.println( "DueDate = " +
                        // programStageInstance.getDueDate() +
                        // patient.getFullName() );
                        if ( programStageInstance.getDueDate().after( sDate )
                            && programStageInstance.getDueDate().before( eDate ) )
                        {
                            // System.out.println( "DueDate = " +
                            // programStageInstance.getDueDate() + " " +
                            // programStageInstance.getExecutionDate() +
                            // " "+programStageInstance.getId());
                            programStageInstances.add( programStageInstance );
                        }
                    }
                }
                if ( programInstance != null && programStageInstances.size() != 0 )
                {
                    PIPSIList.put( programInstance, programStageInstances );
                    // System.out.println( "programStageInstances size = " +
                    // programStageInstances.size() + " programInstance = " +
                    // programInstance.getId() + " patient = " +
                    // patient.getFullName() );
                    // putting patient and pi together
                    patientPIList.put( patient, programInstance );
                    patientList.add( patient );
                    if ( completedProgramStageInstances != null && completedProgramStageInstances.size() != 0 )
                    {
                        patientCompletedPSIList.put( patient, completedProgramStageInstances );
                    }
                }
            }
            int count2 = 0;
            int mergeRowStart = 9 + rowCount;
            PatientAttribute villageAttribute = patientAttributeService.getPatientAttribute( villageAttrId );

            if ( villageAttribute != null && patientList != null && patientList.size() > 0 )
            {
                sortedPatientList = patientService.sortPatientsByAttribute( patientList, villageAttribute );
            }
            else
            {
                sortedPatientList = patientList;
            }

            for ( Patient patient : sortedPatientList )
            {
                ProgramInstance programInstance = patientPIList.get( patient );
                Collection<ProgramStageInstance> psiList = PIPSIList.get( programInstance );
                String cAPhoneNumberName = "";
                // System.out.println(
                // "______________________________ patient = " +
                // patient.getFullName() );
                // System.out.println(
                // "__________________________________________________________"
                // );

                boolean valuePresent = false;
                String deCollectedNames = "";
                String deNotCollectedNames = "";
                int ifaCount = 0;
                int rowNo = rowStart + rowCount;
                // System.out.println(
                // "______________________________________________________________"
                // +patient.getFullName() );
                for ( ProgramStageInstance programStageInstance : psiList )
                {
                    // System.out.println( "*************************"
                    // +patient.getFullName() );
                    count1 = 0;
                    for ( String deCodeString : deCodesList )
                    {

                        tempStr = "";
                        String sType = (String) serviceType.get( count1 );
                        if ( sType.equalsIgnoreCase( "dataelement" ) )
                        {
                            if ( !deCodeString.equals( "NA" ) )
                            {
                                DataElement d1e = dataElementService.getDataElement( Integer.parseInt( deCodeString ) );

                                PatientDataValue patientDataValue1 = patientDataValueService.getPatientDataValue(
                                    programStageInstance, d1e, selectedOrgUnit );
                                if ( patientDataValue1 == null )
                                {
                                    tempStr = " ";
                                }
                                else
                                {
                                    tempStr = patientDataValue1.getValue();
                                }
                            }

                        }
                        else if ( sType.equalsIgnoreCase( "caseProperty" ) )
                        {
                            // _______________________patient
                            // name_______________________
                            if ( deCodeString.equalsIgnoreCase( "Name" ) )
                            {
                                tempStr = patient.getFullName();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "DOB" ) )
                            {
                                // _______________________DateOfBirth_______________________
                                Date patientDate = patient.getBirthDate();
                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
                                tempStr = simpleDateFormat1.format( patientDate );

                            }
                            else if ( deCodeString.equalsIgnoreCase( "Sex" ) )
                            {
                                // ______________________Sex_______________________
                                tempStr = patient.getGender();
                            }
                            else if ( deCodeString.equalsIgnoreCase( "Age" ) )
                            {
                                // ______________________Sex_______________________
                                tempStr = patient.getAge();
                            }
                        }
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
                        }
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
                        else if ( sType.equalsIgnoreCase( "relationshipType" ) )
                        {
                            int deCodeInt = Integer.parseInt( deCodeString );
                            Patient representative = patient.getRepresentative();
                            if ( representative != null )
                            {
                                System.out.println( representative + " " + patient + " "
                                    + relationshipTypeService.getRelationshipType( deCodeInt ) );
                                Relationship parentRelationship = relationshipService.getRelationship( representative,
                                    patient, relationshipTypeService.getRelationshipType( deCodeInt ) );
                                if ( parentRelationship != null )
                                {
                                    tempStr = representative.getFullName();
                                }
                                // System.out.println("Gender = "+gender +
                                // " temStr = "+tempStr);
                            }

                        }// ended if of caseAttributeMFName
                        else if ( sType.equalsIgnoreCase( "HusbandPhoneNumber" )
                            || sType.equalsIgnoreCase( "HusbandName" ) )
                        {
                            int deCodeInt = Integer.parseInt( deCodeString );
                            tempStr = patient.getMiddleName() + " " + patient.getLastName();
                            if ( tempStr == null || tempStr.equals( "" ) )
                            {
                                PatientAttribute patientAttribute = patientAttributeService
                                    .getPatientAttribute( deCodeInt );
                                PatientAttributeValue patientAttributeValue = patientAttributeValueService
                                    .getPatientAttributeValue( patient, patientAttribute );
                                String husbandName = "";
                                if ( patientAttributeValue != null )
                                {
                                    if ( patientAttributeValue.getValue() != null )
                                    {
                                        husbandName = patientAttributeValue.getValue();
                                        cAPhoneNumberName = husbandName;
                                        // System.out.println(
                                        // " cAPhoneNumberName = " +
                                        // cAPhoneNumberName );
                                    }
                                    if ( sType.equalsIgnoreCase( "caseAttributeHusband" ) )
                                    {
                                        husbandName = patientAttributeValue.getValue();
                                        if ( cAPhoneNumberName.equals( "Husband" ) )
                                        {
                                            tempStr = husbandName;
                                        }
                                    }
                                }
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "dataelementDueDate" ) )
                        {
                            Date dueDate = programStageInstance.getDueDate();
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
                            tempStr = simpleDateFormat1.format( dueDate );
                        }
                        else if ( sType.equalsIgnoreCase( "srno" ) )
                        {
                            int tempNum = 1 + rowCount;
                            tempStr = String.valueOf( tempNum );
                            // System.out.println( "srno = " + tempNum + " " +
                            // tempStr );
                        }
                        else if ( sType.equalsIgnoreCase( "dataelementTT" )
                            || sType.equalsIgnoreCase( "dataelementTest" ) )
                        {
                            if ( !deCodeString.equals( "NA" ) )
                            {
                                tempStr = "";
                                if ( deCodeString.contains( "," ) )
                                {
                                    String[] des = deCodeString.split( "," );
                                    for ( String de : des )
                                    {
                                        DataElement d1e = dataElementService.getDataElement( Integer.parseInt( de ) );
                                        Collection<ProgramStageInstance> psisList = patientCompletedPSIList
                                            .get( patient );
                                        // System.out.println(
                                        // "psisList size = " + psisList.size()
                                        // );
                                        String dename = d1e.getShortName();
                                        if ( psisList != null && psisList.size() != 0 )
                                        {
                                            for ( ProgramStageInstance programStageInstanceName : psisList )
                                            {
                                                if ( !deCollectedNames.contains( dename ) )
                                                {
                                                    PatientDataValue patientDataValue1 = patientDataValueService
                                                        .getPatientDataValue( programStageInstanceName, d1e,
                                                            selectedOrgUnit );
                                                    if ( patientDataValue1 != null )
                                                    {
                                                        valuePresent = true;
                                                        deCollectedNames = deCollectedNames + dename;
                                                        // //System.out.println(
                                                        // "deCollectedNames = "
                                                        // + deCollectedNames );
                                                    }
                                                }
                                            }
                                        }

                                        if ( !deCollectedNames.contains( dename ) )
                                        {
                                            if ( !tempStr.trim().equals( "" ) )
                                            {
                                                tempStr = tempStr + " + " + dename;
                                            }
                                            else
                                            {
                                                tempStr = dename;
                                            }

                                        }
                                        // System.out.println( "tempStr = " +
                                        // tempStr );
                                    }
                                }
                                else
                                {
                                    DataElement d1e = dataElementService.getDataElement( Integer
                                        .parseInt( deCodeString ) );
                                    Collection<ProgramStageInstance> psisList = patientCompletedPSIList.get( patient );
                                    String dename = d1e.getShortName();
                                    if ( psisList.size() != 0 )
                                    {
                                        for ( ProgramStageInstance programStageInstanceName : psisList )
                                        {
                                            if ( !deCollectedNames.contains( dename ) )
                                            {
                                                PatientDataValue patientDataValue1 = patientDataValueService
                                                    .getPatientDataValue( programStageInstanceName, d1e,
                                                        selectedOrgUnit );
                                                if ( patientDataValue1 != null )
                                                {
                                                    valuePresent = true;
                                                    deCollectedNames = deCollectedNames + dename;
                                                }
                                            }
                                        }
                                    }

                                    if ( !deCollectedNames.contains( dename ) )
                                    {
                                        if ( !tempStr.trim().equals( "" ) )
                                        {
                                            tempStr = tempStr + " + " + dename;
                                        }
                                        else
                                        {
                                            tempStr = dename;
                                        }

                                    }
                                }
                            }
                            else
                            {
                                tempStr = "";
                            }
                        }
                        else if ( sType.equalsIgnoreCase( "dataelementVisit" ) )
                        {
                            tempStr = programStageInstance.getProgramStage().getName();
                        }
                        else if ( sType.equalsIgnoreCase( "dataelementIFA" ) )
                        {
                            DataElement d1e = dataElementService.getDataElement( Integer.parseInt( deCodeString ) );
                            Collection<ProgramStageInstance> psisList = patientCompletedPSIList.get( patient );
                            String dename = d1e.getShortName();
                            if ( psisList != null && psisList.size() != 0 )
                            {
                                for ( ProgramStageInstance programStageInstanceName : psisList )
                                {
                                    // System.out.println(
                                    // "programStage = "+programStageInstanceName
                                    // .getProgramStage() + " deCollectedNames "
                                    // +deCollectedNames );
                                    if ( !deCollectedNames.contains( dename ) )
                                    {
                                        PatientDataValue patientDataValue1 = patientDataValueService
                                            .getPatientDataValue( programStageInstanceName, d1e, selectedOrgUnit );
                                        if ( patientDataValue1 != null )
                                        {
                                            valuePresent = true;
                                            tempStr = patientDataValue1.getValue();
                                        }
                                        else
                                        {
                                            tempStr = "";
                                        }
                                    }
                                }
                            }

                        }
                        else if ( sType.equalsIgnoreCase( "immunizationPS" ) )
                        {
                            tempStr = "";
                            Set<DataElement> deList = programStageDEs.get( programStageInstance.getProgramStage() );
                            for ( DataElement de : deList )
                            {
                                String dename = de.getShortName();
                                if ( !tempStr.trim().equals( "" ) )
                                {
                                    tempStr = tempStr + " + " + dename;
                                }
                                else
                                {
                                    tempStr = dename;
                                }
                            }

                        }
                        else if ( sType.equalsIgnoreCase( "dataelementDueDate" ) )
                        {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                            tempStr = simpleDateFormat.format( programStageInstance.getDueDate() );
                        }
                        // programStageDEs
                        // int tempRowNo = 11 + rowCount;
                        if ( !sType.equalsIgnoreCase( "rowStart" ) && !sType.equalsIgnoreCase( "reportProperty" ) )
                        {
                            int tempColNo = colList.get( count1 );
                            int sheetNo = sheetList.get( count1 );
                            sheet0 = outputReportWorkbook.getSheet( sheetNo );
                            WritableCell cell = sheet0.getWritableCell( tempColNo, rowNo );

                            // /System.out.println( "tempColNo = " + tempColNo +
                            // " rowNo = " + rowNo + " value = " + tempStr );
                            sheet0.addCell( new Label( tempColNo, rowNo, tempStr, wCellformat ) );
                        }
                        count1++;
                    }// end of decodelist for loop
                    rowCount++;
                    rowNo++;
                    // System.out.println(
                    // "______________________________________________________________"
                    // );
                }

                count2++;
                int mergeRowEnd = 8 + rowCount;
                if ( count2 > 0 && mergeRowEnd > mergeRowStart )
                {
                    if ( reportList.equalsIgnoreCase( "NBITS_FeedBack_from_Block_to_SC_Immu" ) )
                    {
                        sheet0.mergeCells( 1, mergeRowStart, 1, mergeRowEnd );
                        sheet0.mergeCells( 2, mergeRowStart, 2, mergeRowEnd );
                        sheet0.mergeCells( 3, mergeRowStart, 3, mergeRowEnd );
                        sheet0.mergeCells( 4, mergeRowStart, 4, mergeRowEnd );
                        sheet0.mergeCells( 5, mergeRowStart, 5, mergeRowEnd );
                        sheet0.mergeCells( 6, mergeRowStart, 6, mergeRowEnd );
                        sheet0.mergeCells( 7, mergeRowStart, 7, mergeRowEnd );

                        // System.out.println(
                        // "______________________________________________________________"
                        // );
                        // System.out.println( "mergeRowStart = " +
                        // mergeRowStart + " merge row end = " + mergeRowEnd );
                    }
                    else
                    {
                        sheet0.mergeCells( 1, mergeRowStart, 1, mergeRowEnd );
                        sheet0.mergeCells( 2, mergeRowStart, 2, mergeRowEnd );
                        sheet0.mergeCells( 3, mergeRowStart, 3, mergeRowEnd );
                        sheet0.mergeCells( 4, mergeRowStart, 4, mergeRowEnd );
                        sheet0.mergeCells( 5, mergeRowStart, 5, mergeRowEnd );
                        sheet0.mergeCells( 6, mergeRowStart, 6, mergeRowEnd );
                        sheet0.mergeCells( 7, mergeRowStart, 7, mergeRowEnd );
                        // System.out.println(
                        // "______________________________________________________________"
                        // );
                        // System.out.println( "mergeRowStart = " +
                        // mergeRowStart + " merge row end = " + mergeRowEnd );
                    }
                }
                mergeRowStart = mergeRowEnd + 1;

            }// end of villagesort for loop

        }// end if loop

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + ".xls";
        // System.out.println( "fileName = " + fileName + " outputReportPath = "
        // + outputReportPath );

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

                // System.out.println(deCodes.get( s )+" : "+deCodeType.get( s
                // ));
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

    public Period getPreviousPeriod( Date sDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }

        PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), periodType );

        return period;
    }

    public Period getNextPeriod( Date sDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.DECEMBER )
        {
            tempDate.set( Calendar.MONTH, Calendar.JANUARY );
            tempDate.roll( Calendar.YEAR, +1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, +1 );
        }

        PeriodType periodType = PeriodType.getByNameIgnoreCase( "monthly" );
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), periodType );

        return period;
    }

    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }

        }
        else
        {
            if ( periodType.getName().equals( "Yearly" ) )
            {
                cal.set( year, Calendar.DECEMBER, 31 );
            }

        }
        Date lastDay = new Date( cal.getTimeInMillis() );

        return periodService.getPeriod( firstDay, lastDay, periodType );
    }

    public List<Calendar> getStartingEndingPeriods( String deType, Date sDate, Date eDate )
    {

        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod( sDate );

        if ( deType.equalsIgnoreCase( "cpmcy" ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }

            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( previousPeriod.getEndDate() );
        }
        else
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
                // System.out.println("We are in PT if block");
            }
            else
            {
                if ( deType.equalsIgnoreCase( "ccmpy" ) )
                {
                    tempStartDate.setTime( sDate );
                    tempEndDate.setTime( eDate );

                    tempStartDate.roll( Calendar.YEAR, -1 );
                    tempEndDate.roll( Calendar.YEAR, -1 );
                }
                else
                {
                    if ( deType.equalsIgnoreCase( "cmpy" ) )
                    {
                        tempStartDate.setTime( sDate );
                        tempEndDate.setTime( eDate );

                        tempStartDate.roll( Calendar.YEAR, -1 );
                        tempEndDate.roll( Calendar.YEAR, -1 );
                    }
                    else
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
