package org.hisp.dhis.dashboard.ga.action.charts;

import com.opensymphony.xwork2.Action;
import java.io.BufferedInputStream;
import jxl.write.Label;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.caseaggregation.CaseAggregationMapping;
import org.hisp.dhis.caseaggregation.CaseAggregationMappingService;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;

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

    private CaseAggregationMappingService caseAggregationMappingService;

    public void setCaseAggregationMappingService( CaseAggregationMappingService caseAggregationMappingService )
    {
        this.caseAggregationMappingService = caseAggregationMappingService;
    }

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

    private Period startDate;

    private OrganisationUnit selectedOrgUnit;

    private DataElement de;

    private DataElementCategoryOptionCombo coc;

    private CaseAggregationMapping caseAggMapping;

    private String reportFileNameTB;

    public String execute()
        throws Exception
    {

        values = selectedValues.split( ":" );
        int orgunit = Integer.parseInt( values[0] );
        int periodid = Integer.parseInt( values[3] );
        int deid = Integer.parseInt( values[1] );
        int cocid = Integer.parseInt( values[2] );

        startDate = periodService.getPeriod( periodid );
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgunit );
        de = dataElementService.getDataElement( deid );
        coc = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( cocid );
        System.out.println( "orgunit is " + orgunit + " de is " + deid + " coc is " + cocid + " period is " + periodid );

        statementManager.initialise();
        raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();

        reportFileNameTB = "DrillDownToCaseBased.xls";
        // Initialization
        inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template"
            + File.separator + reportFileNameTB;

        outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output"
            + File.separator + UUID.randomUUID().toString() + ".xls";

        System.out.println( " inputTemplatePath " + inputTemplatePath );

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

        caseAggMapping = caseAggregationMappingService.getCaseAggregationMappingByOptionCombo( de, coc );

        List<PatientDataValue> patientDataValues = caseAggregationMappingService.getCaseAggregatePatientDataValue(
            selectedOrgUnit, startDate, caseAggMapping );
        List<String> allValues = new ArrayList<String>();

        List<ProgramInstance> pInstances = new ArrayList<ProgramInstance>();
        List<Date> executionDates = new ArrayList<Date>();
        List<DataElement> des = new ArrayList<DataElement>();

        Map<ProgramInstance, Date> pSInstancesMap = new HashMap<ProgramInstance, Date>();

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            allValues.add( patientDataValue.getValue() );
            if ( !des.contains( patientDataValue.getDataElement() ) )
                des.add( patientDataValue.getDataElement() );
            ProgramStageInstance psi = patientDataValue.getProgramStageInstance();
            pSInstancesMap.put( psi.getProgramInstance(), psi.getExecutionDate() );
            pInstances.add( psi.getProgramInstance() );
            executionDates.add( psi.getExecutionDate() );
            System.out.println( "value = " + patientDataValue.getValue() + " psiid " + psi.getId()
                + " exceution date is  " + psi.getExecutionDate() );
        }

        WritableSheet sheet0 = outputReportWorkbook.getSheet( 0 );
        WritableCellFormat wCellformat1 = new WritableCellFormat();
        wCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat1.setAlignment( Alignment.CENTRE );
        wCellformat1.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat1.setBackground( Colour.GRAY_50 );
        // adding column names
        // selected orgunit
        sheet0.addCell( new Label( 1, 1, "Selected orgunit", wCellformat1 ) );

        // village
        sheet0.addCell( new Label( 2, 1, "Village", wCellformat1 ) );

        //patient Identifier
        sheet0.addCell( new Label( 3, 1, "Patient Identifier", wCellformat1 ) );
        
        // patient name
        sheet0.addCell( new Label( 4, 1, "Patient Name", wCellformat1 ) );

        // Address
        sheet0.addCell( new Label( 5, 1, "Address", wCellformat1 ) );

        // Sex
        sheet0.addCell( new Label( 6, 1, "Gender", wCellformat1 ) );

        // Age
        sheet0.addCell( new Label( 7, 1, "Age", wCellformat1 ) );
        

        int count1 = 0;
        System.out.println( "des size " + des.size() );
        for ( DataElement de : des )
        {
            // DEName
            sheet0.addCell( new Label( 8 + count1, 1, "" + de.getName(), wCellformat1 ) );

            // Execution date
            sheet0.addCell( new Label( 9 + count1, 1, "Execution Date", wCellformat1 ) );

            count1++;
        }

        PatientAttribute villageAttribute = patientAttributeService.getPatientAttributeByName( "Gram Panchayat or Village" );
        PatientAttribute addressAttribute = patientAttributeService.getPatientAttributeByName( "Address" );

        int rowCount = 0;
        for ( int i = 0; i < pInstances.size(); i++ )
        {

            rowCount = i;
            ProgramInstance pi = pInstances.get( i );
            String value = allValues.get( i );
            Date executionDate = executionDates.get( i );
            Patient patient = pi.getPatient();

            // selected orgunit
            sheet0.addCell( new Label( 1, 2 + rowCount, selectedOrgUnit.getName(), wCellformat ) );

            // village
            PatientAttributeValue villageAttributeValue = patientAttributeValueService.getPatientAttributeValue(
                patient, villageAttribute );
            if ( villageAttributeValue != null )
            {
                sheet0.addCell( new Label( 2, 2 + rowCount, villageAttributeValue.getValue(), wCellformat ) );
            }
            else
            {
                sheet0.addCell( new Label( 2, 2 + rowCount, " ", wCellformat ) );
            }

            //patinet Identifier
            sheet0.addCell( new Label( 3, 2 + rowCount, patient.getIdentifiers().iterator().next().getIdentifier(), wCellformat ) );
            
            // patient name
            sheet0.addCell( new Label( 4, 2 + rowCount, patient.getFirstName(), wCellformat ) );

            // Address
            PatientAttributeValue addressAttributeValue = patientAttributeValueService.getPatientAttributeValue(
                patient, addressAttribute );
            if ( addressAttributeValue != null )
            {
                sheet0.addCell( new Label( 5, 2 + rowCount, addressAttributeValue.getValue(), wCellformat ) );
            }
            else
            {
                sheet0.addCell( new Label( 5, 2 + rowCount, " ", wCellformat ) );
            }
            
            // Sex
            sheet0.addCell( new Label( 6, 2 + rowCount, patient.getGender(), wCellformat ) );

            // age
            sheet0.addCell( new Label( 7, 2 + rowCount, patient.getAge(), wCellformat ) );

            for ( int count = 0; count < des.size(); count++ )
            {
                // DE Value
                sheet0.addCell( new Label( 8 + count, 2 + rowCount, "" + value, wCellformat ) );

                // Execution date
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                String eDate = simpleDateFormat.format( executionDate );
                sheet0.addCell( new Label( 9 + count, 2 + rowCount, "" + eDate, wCellformat ) );

                count++;
            }
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        // System.out.println("fileName = " + fileName);
        fileName += "_" + selectedOrgUnit.getShortName() + ".xls";
        // System.out.println("fileName = " + fileName + " outputReportPath = "
        // + outputReportPath);
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
        // Cell formatting

    }
}
