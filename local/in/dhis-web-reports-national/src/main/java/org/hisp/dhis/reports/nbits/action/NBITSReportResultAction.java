package org.hisp.dhis.reports.nbits.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.comparator.DataElementNameComparator;
import org.hisp.dhis.i18n.I18nFormat;
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
import org.hisp.dhis.patient.comparator.PatientAttributeComparator;
import org.hisp.dhis.patient.comparator.PatientIdentifierTypeComparator;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.comparator.ProgramStageNameComparator;
import org.hisp.dhis.reports.util.ReportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class NBITSReportResultAction implements Action
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

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
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

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
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

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
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

    private int programList;
    
    public void setProgramList( int programList )
    {
        this.programList = programList;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
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
    
    private Boolean includePeriod;

    public void setIncludePeriod( Boolean includePeriod )
    {
        this.includePeriod = includePeriod;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception 
    {
        statementManager.initialise();
        
        
        Program selProgram = programService.getProgram( programList );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ouIDTB )  );

        List<OrganisationUnit> programOrgUnits = new ArrayList<OrganisationUnit>( selProgram.getOrganisationUnits() );
        
        orgUnitList.retainAll( programOrgUnits );
        
        Date sDate = format.parseDate( startDate );
        
        Date eDate = format.parseDate( endDate );

        System.out.println("NBITS Report_" + orgUnitList.get( 0 ) + "_" + selProgram.getName() + "_StartTime: " + new Date() );
        
        generateReport( selProgram, orgUnitList, sDate, eDate );

        System.out.println("NBITS Report_" + orgUnitList.get( 0 ) + "_" + selProgram.getName() + "_EndTime: " + new Date() );

        statementManager.destroy();
        
        return SUCCESS;
    }
    
    public void generateReport( Program selProgram, List<OrganisationUnit> orgUnitList, Date sDate, Date eDate ) throws Exception
    {
        String raFolderName = reportService.getRAFolderName();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String query = "";
        int rowStart = 3;
        int colStart = 1;
        int rowCount = rowStart;
        int colCount = colStart;
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( selProgram.getName(), 0 );

        try
        {
            List<PatientIdentifierType> patientIdentifierTypes = new ArrayList<PatientIdentifierType>( patientIdentifierTypeService.getAllPatientIdentifierTypes() );
            Collections.sort( patientIdentifierTypes, new PatientIdentifierTypeComparator() );
            
            List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
            Collections.sort( patientAttributes, new PatientAttributeComparator() );
            
            List<ProgramStage> programStages = new ArrayList<ProgramStage>( selProgram.getProgramStages() );
            Collections.sort( programStages, new ProgramStageNameComparator() );
            
            Map<ProgramStage, List<DataElement>> programStageDataElementMap = new HashMap<ProgramStage, List<DataElement>>();
            for( ProgramStage programStage : programStages )
            {
                List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
                
                List<DataElement> dataElements =  new ArrayList<DataElement>();
                for( ProgramStageDataElement programStageDataElement : programStageDataElements )
                {
                    dataElements.add( programStageDataElement.getDataElement() );
                }
                
                Collections.sort( dataElements, new DataElementNameComparator() );
                programStageDataElementMap.put( programStage, dataElements );
            }
            
            // Printing Header Information
            sheet0.addCell( new Label( colCount, rowCount, "OrgUnit" ) );
            colCount++;
            for( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
            {
                sheet0.addCell( new Label( colCount, rowCount, patientIdentifierType.getName() ) );
                colCount++;
            }
            sheet0.addCell( new Label( colCount, rowCount, "Benificiary Name" ) );
            colCount++;
            sheet0.addCell( new Label( colCount, rowCount, "Gender" ) );
            colCount++;
            sheet0.addCell( new Label( colCount, rowCount, "Age" ) );
            colCount++;
            sheet0.addCell( new Label( colCount, rowCount, "Data of Birth" ) );
            colCount++;
            sheet0.addCell( new Label( colCount, rowCount, "Blood Group" ) );
            colCount++;
            for( PatientAttribute patientAttribute : patientAttributes )
            {
                sheet0.addCell( new Label( colCount, rowCount, patientAttribute.getName() ) );
                colCount++;
            }
            sheet0.addCell( new Label( colCount, rowCount, "Incident Date" ) );
            colCount++;
            sheet0.addCell( new Label( colCount, rowCount, "Enrollment Date" ) );
            colCount++;
            for( ProgramStage programStage : programStages )
            {
                for( DataElement dataElement : programStageDataElementMap.get( programStage ) )
                {
                    sheet0.addCell( new Label( colCount, rowCount, dataElement.getName() ) );
                    colCount++;
                }
                sheet0.addCell( new Label( colCount, rowCount, "Due Date" ) );
                colCount++;
                sheet0.addCell( new Label( colCount, rowCount, "Execution Date" ) );
                colCount++;
            }
            rowCount++;
            
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                query = "SELECT patient.patientid, programinstance.programinstanceid,programinstance.dateofincident,programinstance.enrollmentdate FROM programinstance INNER JOIN patient " +
                		" ON programinstance.patientid = patient.patientid " +
                		" WHERE patient.organisationunitid = "+ orgUnit.getId() +
                		" AND programinstance.programid = "+ selProgram.getId() +
                		" AND enddate IS NULL";

                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
                if ( sqlResultSet != null )
                {
                    sqlResultSet.beforeFirst();
                    while ( sqlResultSet.next() )
                    {
                        colCount = colStart;
                        sheet0.addCell( new Label( colCount, rowCount, orgUnit.getName() ) );
                        colCount++;

                        int patientId = sqlResultSet.getInt( 1 );
                        int programInstanceId = sqlResultSet.getInt( 2 );
                        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );
                        Date dateOfIncident = sqlResultSet.getDate( 3 );
                        Date dateOfEnrollment = sqlResultSet.getDate( 4 );
                        
                        Patient patient = patientService.getPatient( patientId );
                        
                        //Patient Identifier Details
                        for( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
                        {
                            PatientIdentifier patientIdentifier = patientIdentifierService.getPatientIdentifier( patientIdentifierType, patient );
                            if( patientIdentifier != null && patientIdentifier.getIdentifier() != null )
                            {
                                sheet0.addCell( new Label( colCount, rowCount, patientIdentifier.getIdentifier() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, rowCount, "-" ) );
                            }
                            colCount++;
                        }
                        
                        //Patient Properties
                        sheet0.addCell( new Label( colCount, rowCount, patient.getFullName() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, patient.getTextGender() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, patient.getAge() ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( patient.getBirthDate() ) ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, patient.getBloodGroup() ) );
                        colCount++;

                        //Patient Attribute Values
                        for( PatientAttribute patientAttribute : patientAttributes )
                        {
                            PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                            if( patientAttributeValue != null && patientAttributeValue.getValue() != null )
                            {
                                sheet0.addCell( new Label( colCount, rowCount, patientAttributeValue.getValue() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, rowCount, "-" ) );
                            }
                            colCount++;
                        }
                        
                        //Program Enrollment Details
                        sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( dateOfIncident ) ) );
                        colCount++;
                        sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( dateOfEnrollment ) ) );
                        colCount++;
                        
                        
                        //ProgramStage Values
                        for( ProgramStage programStage : programStages )
                        {
                            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programInstance, programStage );

                            for( DataElement dataElement : programStageDataElementMap.get( programStage ) )
                            {
                                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, dataElement, orgUnit );
                                
                                if( patientDataValue != null && patientDataValue.getValue() != null )
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, patientDataValue.getValue() ) );    
                                }
                                else
                                {
                                    sheet0.addCell( new Label( colCount, rowCount, "-" ) );
                                }
                                colCount++;
                            }
                            if( programStageInstance.getDueDate() != null )
                            {
                                sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( programStageInstance.getDueDate() ) ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, rowCount, " " ) );
                            }
                            colCount++;
                            
                            if( programStageInstance.getExecutionDate() != null )
                            {
                                sheet0.addCell( new Label( colCount, rowCount, simpleDateFormat.format( programStageInstance.getExecutionDate() ) ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, rowCount, " " ) );
                            }
                            colCount++;
                        }
                        
                        rowCount++;
                    }
                }

            }

        }
        catch( Exception e )
        {
            System.out.println( "Exception: "+e.getMessage() );
            e.printStackTrace();
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = selProgram.getName() + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();

    }
}
