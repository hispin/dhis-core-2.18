/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.patient.action.patientimport;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version ImportPatientAction.java Nov 12, 2010 12:45:57 PM
 */
public class ImportPatientAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeOptionService patientAttributeOptionService;

    private PatientAttributeValueService patientAttributeValueService;

    private ProgramService programService;

    private ProgramStageService programStageService;

    private ProgramInstanceService programInstanceService;

    private ProgramStageInstanceService programStageInstanceService;

    private DataElementService dataElementService;

    private PatientDataValueService patientDataValueService;

    private SystemSettingManager systemSettingManager;

    private OrganisationUnitService organisationUnitService;

    private I18nFormat format;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private HashMap<Integer, Patient> errPatients = new HashMap<Integer, Patient>();

    private HashMap<Integer, String> errMessage = new HashMap<Integer, String>();

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private File upload;

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    private File output;

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Private Parameters
    // -------------------------------------------------------------------------

    private XMLItem xmlStartRow;

    private XMLItem xmlEndRow;

    private XMLItem xmlOrgunit;

    private XMLItem xmlProgram;

    private char ageType;

    Collection<XMLItem> itemProperty = new HashSet<XMLItem>();

    Collection<XMLItem> itemAttribute = new HashSet<XMLItem>();

    Collection<XMLItem> itemEnrollProgram = new HashSet<XMLItem>();

    Collection<XMLItem> itemProgramStage = new HashSet<XMLItem>();

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    public HashMap<Integer, Patient> getErrPatients()
    {
        return errPatients;
    }

    public HashMap<Integer, String> getErrMessage()
    {
        return errMessage;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public File getOutput()
    {
        return output;
    }

    public void setOutput( File output )
    {
        this.output = output;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( upload == null )
        {
            message = i18n.getString( "upload_file_null" );

            return ERROR;
        }

        // ---------------------------------------------------------------------
        // Get template-file
        // ---------------------------------------------------------------------

        String path = System.getenv( "DHIS2_HOME" );
        fileName += (Math.random() * 1000) + ".xls";

        if ( path != null )
        {
            path += File.separator + "patienttemp" + File.separator + fileName;
        }
        else
        {
            path = System.getenv( "user.home" ) + File.separator + "patienttemp" + File.separator + fileName;
        }

        output = new File( path );
        StreamUtils.write( upload, output );

        // ---------------------------------------------------------------------
        // Do import
        // ---------------------------------------------------------------------

        if ( output == null )
        {
            message = i18n.getString( "upload_file_null" );

            return ERROR;
        }

        try
        {
            // -----------------------------------------------------------------
            // Get excel-items from XML-template-file
            // -----------------------------------------------------------------

            readXMLTemplateFile();

            if ( message != null && message.length() > 0 )
            {
                return ERROR;
            }

            // -----------------------------------------------------------------
            // Get file which need to import information
            // -----------------------------------------------------------------

            FileInputStream inputStream = new FileInputStream( output );

            HSSFWorkbook wb = new HSSFWorkbook( inputStream );

            // -----------------------------------------------------------------
            // Get common-information from XML-template-file
            // -----------------------------------------------------------------

            // Get Mandatory attributes
            Collection<PatientAttribute> attributes = patientAttributeService.getPatientAttributesByMandatory( true );

            // Get Mandatory identifiers
            Collection<PatientIdentifierType> identifiers = patientIdentifierTypeService
                .getPatientIdentifierTypes( true );

            // Get organisation-unit
            HSSFSheet sheet = wb.getSheetAt( xmlOrgunit.getSheet() );
            String value = readValue( xmlOrgunit.getRow(), xmlOrgunit.getColumn(), sheet );
            OrganisationUnit orgunit = organisationUnitService.getOrganisationUnit( Integer.parseInt( value ) );

            // Get program
            sheet = wb.getSheetAt( xmlProgram.getSheet() );
            value = readValue( xmlProgram.getRow(), xmlProgram.getColumn(), sheet );
            Program program = programService.getProgram( Integer.parseInt( value ) );

            // Get startRow
            sheet = wb.getSheetAt( xmlStartRow.getSheet() );
            value = readValue( xmlStartRow.getRow(), xmlStartRow.getColumn(), sheet );
            int startRow = Integer.parseInt( value ) - 1;

            // get endRow
            sheet = wb.getSheetAt( xmlEndRow.getSheet() );
            value = readValue( xmlEndRow.getRow(), xmlEndRow.getColumn(), sheet );
            int endRow = Integer.parseInt( value ) - 1;

            // -----------------------------------------------------------------
            // Import information
            // -----------------------------------------------------------------

            for ( int row = startRow; row <= endRow; row++ )
            {
                // -------------------------------------------------------------
                // Get patient and patient-attribute-values from Excel file
                // -------------------------------------------------------------

                Patient patient = getPatient( row, orgunit, wb );

                Collection<PatientAttributeValue> attributeValues = getAttributes( patient, row, wb );

                // -------------------------------------------------------------
                // Validate data of patient is right, continue to get
                // information into excel file
                // -------------------------------------------------------------

                ProgramInstance programInstance = null;
                List<ProgramStageInstance> programStageInstances = null;
                Collection<PatientDataValue> patientDataValues = null;

                if ( validatePatient( patient, identifiers, row )
                    && validatePatientAttribute( patient, attributes, attributeValues, row ) )
                {
                    try
                    {
                        // -------------------------------------------------------------
                        // Get Information of EnrollProgram
                        // -------------------------------------------------------------

                        // Get programInstance for patient
                        programInstance = getProgramInstance( patient, program, row, wb );

                        // Get programStageInstance for patient
                        programStageInstances = getProgramStageInstances( programInstance, row, wb );

                    }
                    catch ( Exception ex )
                    {
                        errPatients.put( row, patient );
                        errMessage.put( row, i18n.getString( "get_data_error" ) );
                        ex.printStackTrace();
                        continue;
                    }

                    // save new Patient
                    patientService.savePatient( patient );
                    // save patient-attribute-values
                    for ( PatientAttributeValue attributeValue : attributeValues )
                    {
                        patientAttributeValueService.savePatientAttributeValue( attributeValue );
                    }

                    programInstanceService.addProgramInstance( programInstance );

                    patientService.updatePatient( patient );

                    for ( ProgramStageInstance programStageInstance : programStageInstances )
                    {
                        programStageInstanceService.addProgramStageInstance( programStageInstance );
                    }

                    // -------------------------------------------------------------
                    // Get PatientDataValue
                    // -------------------------------------------------------------

                    patientDataValues = getPatientDataValue( orgunit, programInstance, programStageInstances, row, wb );

                    for ( PatientDataValue patientDataValue : patientDataValues )
                    {
                        patientDataValueService.savePatientDataValue( patientDataValue );
                    }
                }
            }

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private Patient getPatient( int row, OrganisationUnit orgunit, HSSFWorkbook wb )
        throws Exception
    {
        Patient patient = new Patient();
        patient.setFirstName( "" );
        patient.setMiddleName( "" );
        patient.setLastName( "" );
        patient.setOrganisationUnit( orgunit );
        patient.setRegistrationDate( new Date() );
        patient.setUnderAge( false );

        // ---------------------------------------------------------------------
        // Create Patient
        // ---------------------------------------------------------------------

        for ( XMLItem xmlItem : itemProperty )
        {
            HSSFSheet sheet = wb.getSheetAt( xmlItem.getSheet() );

            String value = readValue( row, xmlItem.getColumn(), sheet );

            // -----------------------------------------------------------------
            // Import identifier values
            // -----------------------------------------------------------------

            if ( xmlItem.isType( XMLItem.IDENTIFIER_TYPE ) )
            {
                int objectId = Integer.parseInt( xmlItem.getValue() );
                PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( objectId );

                PatientIdentifier identifier = new PatientIdentifier();
                identifier.setIdentifierType( identifierType );
                identifier.setPatient( patient );
                identifier.setIdentifier( value.trim() );
                patient.getIdentifiers().add( identifier );
            }

            // -----------------------------------------------------------------
            // Import property values
            // -----------------------------------------------------------------

            else if ( xmlItem.isType( XMLItem.PROPERTY_TYPE ) )
            {
                if ( !value.isEmpty() )
                {
                    if ( xmlItem.getValue().equalsIgnoreCase( XMLItem.BIRTH_DATE_FROM_VALUE ) )
                    {
                        patient.setBirthDateFromAge( Integer.parseInt( value ), ageType );
                    }
                    else
                    {
                        setObject( patient, xmlItem.getValue(), value );
                    }
                }
            }
        }

//        // -------------------------------------------------------------
//        // Generate system id with this format :
//        // (BirthDate)(Gender)(XXXXXX)(checkdigit)
//        // PatientIdentifierType will be null
//        // -------------------------------------------------------------
//
//        String systemIdentifier = PatientIdentifierGenerator.getNewIdentifier( patient.getBirthDate(), patient
//            .getGender() );
//
//        PatientIdentifier systemGenerateIdentifier = patientIdentifierService.get( null, systemIdentifier );
//        while ( systemGenerateIdentifier != null )
//        {
//            systemIdentifier = PatientIdentifierGenerator
//                .getNewIdentifier( patient.getBirthDate(), patient.getGender() );
//            systemGenerateIdentifier = patientIdentifierService.get( null, systemIdentifier );
//        }
//
//        systemGenerateIdentifier = new PatientIdentifier();
//        systemGenerateIdentifier.setIdentifier( systemIdentifier );
//        systemGenerateIdentifier.setPatient( patient );
//
//        patient.getIdentifiers().add( systemGenerateIdentifier );

        return patient;
    }

    private Collection<PatientAttributeValue> getAttributes( Patient patient, int row, HSSFWorkbook wb )
    {
        // ---------------------------------------------------------------------
        // Import Information of attributes of patient
        // ---------------------------------------------------------------------

        Collection<PatientAttributeValue> attributeValues = new HashSet<PatientAttributeValue>();

        Set<PatientAttribute> attributes = new HashSet<PatientAttribute>();

        for ( XMLItem xmlItem : itemAttribute )
        {
            HSSFSheet sheet = wb.getSheetAt( xmlItem.getSheet() );

            // -----------------------------------------------------------------
            // Get value into Excel-file
            // -----------------------------------------------------------------

            String value = readValue( row, xmlItem.getColumn(), sheet );

            // -----------------------------------------------------------------
            // Import attribute value
            // -----------------------------------------------------------------

            int objectId = Integer.parseInt( xmlItem.getValue() );
            PatientAttribute attribute = patientAttributeService.getPatientAttribute( objectId );

            attributes.add( attribute );
            PatientAttributeValue attributeValue = new PatientAttributeValue();
            attributeValue.setPatient( patient );
            attributeValue.setPatientAttribute( attribute );

            // Attribute is combo-type
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
            {
                // value is the id of the option
                PatientAttributeOption option = patientAttributeOptionService.get( Integer
                    .parseInt( value.split( ":" )[1] ) );

                if ( option != null )
                {
                    attributeValue.setPatientAttributeOption( option );
                    attributeValue.setValue( option.getName() );
                }
            }
            else
            {
                attributeValue.setValue( value.trim() );
            }

            attributeValues.add( attributeValue );
        }

        patient.setAttributes( attributes );

        return attributeValues;
    }

    private ProgramInstance getProgramInstance( Patient patient, Program program, int row, HSSFWorkbook wb )
        throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        ProgramInstance programInstance = new ProgramInstance();

        for ( XMLItem xmlItem : itemEnrollProgram )
        {
            HSSFSheet sheet = wb.getSheetAt( xmlItem.getSheet() );
            String value = readValue( row, xmlItem.getColumn(), sheet );

            Date date = format.parseDate( value );

            ProgramInstance.class.getMethod( "set" + StringUtils.capitalize( xmlItem.getValue() ), Date.class ).invoke(
                programInstance, date );

        }

        // -------------------------------------------------------------
        // Enroll program
        // -------------------------------------------------------------

        programInstance.setProgram( program );
        programInstance.setPatient( patient );
        programInstance.setCompleted( false );

        patient.getPrograms().add( program );

        return programInstance;
    }

    private List<ProgramStageInstance> getProgramStageInstances( ProgramInstance programInstance, int row,
        HSSFWorkbook wb )
    {

        List<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

        Program program = programInstance.getProgram();

        for ( ProgramStage programStage : program.getProgramStages() )
        {
            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setStageInProgram( programStage.getStageInProgram() );

            Date dueDate = DateUtils.getDateAfterAddition( programInstance.getDateOfIncident(), programStage
                .getMinDaysFromStart() );

            programStageInstance.setDueDate( dueDate );

            programStageInstances.add( programStageInstance );
        }

        return programStageInstances;
    }

    private Collection<PatientDataValue> getPatientDataValue( OrganisationUnit orgunit,
        ProgramInstance programInstance, List<ProgramStageInstance> stageInstances, int row, HSSFWorkbook wb )
    {
        Collection<PatientDataValue> dataValues = new HashSet<PatientDataValue>();

        for ( XMLItem xmlItem : itemProgramStage )
        {
            HSSFSheet sheet = wb.getSheetAt( xmlItem.getSheet() );
            String value = readValue( row, xmlItem.getColumn(), sheet );

            // -----------------------------------------------------------------
            // Create PatientDataValue
            // -----------------------------------------------------------------

            String[] infor = xmlItem.getValue().split( "\\." );

            ProgramStage stage = programStageService.getProgramStage( Integer.parseInt( infor[0] ) );

            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( infor[1] ) );

            int optionComboId = Integer.parseInt( infor[2] );
            DataElementCategoryOptionCombo optionCombo = null;

            if ( optionComboId == 0 )
            {
                String[] temp = value.trim().split( ":" );
                if ( temp.length == 2 )
                {
                    optionComboId = Integer.parseInt( temp[1] );
                }
                else
                {
                    value = (value.equalsIgnoreCase( "yes" )) ? "true" : "false";
                    optionComboId = dataElement.getCategoryCombo().getOptionCombos().iterator().next().getId();
                }
            }

            Set<DataElementCategoryOptionCombo> options = dataElement.getCategoryCombo().getOptionCombos();
            if ( options != null && options.size() > 0 )
            {
                Iterator<DataElementCategoryOptionCombo> i = options.iterator();
                while ( i.hasNext() )
                {
                    DataElementCategoryOptionCombo tmpOption = i.next();
                    if ( tmpOption.getId() == optionComboId )
                    {
                        optionCombo = tmpOption;
                    }
                }
            }

            if ( stage != null && dataElement != null && optionCombo != null )
            {
                ProgramStageInstance stageInstance = programStageInstanceService.getProgramStageInstance(
                    programInstance, stage );

                PatientDataValue dataValue = new PatientDataValue();

                dataValue.setDataElement( dataElement );
                dataValue.setOptionCombo( optionCombo );
                dataValue.setProgramStageInstance( stageInstance );
                dataValue.setOrganisationUnit( orgunit );
                dataValue.setTimestamp( new Date() );
                dataValue.setValue( value );

                dataValues.add( dataValue );
            }
        }

        return dataValues;
    }

    // -------------------------------------------------------------------------
    // Validate data from Excel file
    // -------------------------------------------------------------------------

    private boolean validatePatient( Patient patient, Collection<PatientIdentifierType> identifiers, int row )
    {
        // ---------------------------------------------------------------------
        // Validation information of the patient
        // ---------------------------------------------------------------------

        if ( patient.getFirstName() == null && patient.getMiddleName() == null && patient.getLastName() == null )
        {
            errPatients.put( row, patient );
            errMessage.put( row, i18n.getString( "patient_name_is_null" ) );
            return false;
        }

        if ( patient.getBirthDate() == null )
        {
            errPatients.put( row, patient );
            errMessage.put( row, i18n.getString( "birthday_is_null" ) );
            return false;
        }

        // ---------------------------------------------------------------------
        // Check duplicated patient
        // ---------------------------------------------------------------------

        Collection<Patient> patients = patientService.getPatient( patient.getFirstName(), patient.getMiddleName(),
            patient.getLastName(), patient.getBirthDate(), patient.getGender() );

        if ( patients != null && patients.size() > 0 )
        {
            errPatients.put( row, patient );
            errMessage.put( row, i18n.getString( "duplicate" ) );
            return false;
        }

        // ---------------------------------------------------------------------
        // Validation identifiers of the patient
        // ---------------------------------------------------------------------

        Collection<PatientIdentifierType> patientIdentifierTypes = new HashSet<PatientIdentifierType>();

        for ( PatientIdentifier patientIdentifier : patient.getIdentifiers() )
        {
            patientIdentifierTypes.add( patientIdentifier.getIdentifierType() );
        }

        if ( !patientIdentifierTypes.containsAll( identifiers ) )
        {
            errPatients.put( row, patient );
            errMessage.put( row, i18n.getString( "not_enough_mandatory_identifier" ) );
            return false;
        }

        return true;
    }

    private boolean validatePatientAttribute( Patient patient, Collection<PatientAttribute> attributes,
        Collection<PatientAttributeValue> attributeValues, int row )
    {
        Collection<PatientAttribute> patientAttributes = new HashSet<PatientAttribute>();

        for ( PatientAttributeValue attributeValue : attributeValues )
        {
            patientAttributes.add( attributeValue.getPatientAttribute() );
        }

        if ( !patientAttributes.containsAll( attributes ) )
        {
            errPatients.put( row, patient );
            errMessage.put( row, i18n.getString( "not_enough_madatory_attribute" ) );
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Read template file
    // -------------------------------------------------------------------------

    private void readXMLTemplateFile()
    {
        // ---------------------------------------------------------------------
        // Get template-file
        // ---------------------------------------------------------------------

        String fileName = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_PATIENT_EXCEL_TEMPLATE_FILE_NAME );

        if ( fileName == null )
        {
            message = i18n.getString( "configuration_xml_file_null" );

            return;
        }

        String path = System.getenv( "DHIS2_HOME" );

        if ( path != null )
        {
            path += File.separator + fileName;
        }
        else
        {
            path = System.getenv( "user.home" ) + File.separator + "patienttemp" + File.separator + fileName;
        }
        
        // ---------------------------------------------------------------------
        // Get contents into template-file
        // ---------------------------------------------------------------------

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );

            if ( doc == null )
            {
                message = i18n.getString( "there_is_no_defination_xml_file_in_user_home" );

                return;
            }

            NodeList cellNode = doc.getElementsByTagName( "cell" );

            for ( int i = 0; i < cellNode.getLength(); i++ )
            {
                Element cellElement = (Element) cellNode.item( i );

                XMLItem item = new XMLItem();

                item.setValue( cellElement.getAttribute( "value" ) );

                item.setType( cellElement.getAttribute( "type" ) );

                item.setColumn( Integer.parseInt( cellElement.getAttribute( "colno" ) ) - 1 );

                item.setRow( Integer.parseInt( cellElement.getAttribute( "rowno" ) ) - 1 );

                item.setSheet( Integer.parseInt( cellElement.getAttribute( "sheetno" ) ) - 1 );

                if ( item.isType( XMLItem.ORGUNIT_TYPE ) )
                {
                    xmlOrgunit = item;
                }
                else if ( item.isType( XMLItem.START_ROW_TYPE ) )
                {
                    xmlStartRow = item;
                }
                else if ( item.isType( XMLItem.END_ROW_TYPE ) )
                {
                    xmlEndRow = item;
                }
                else if ( item.isType( XMLItem.PROGRAM_TYPE ) )
                {
                    xmlProgram = item;
                }
                else if ( item.isType( XMLItem.AGE_TYPE ) )
                {
                    ageType = cellElement.getAttribute( "value" ).charAt( 0 );
                }
                else if ( item.isType( XMLItem.PROGRAM_ATTRIBUTE_TYPE ) )
                {
                    itemEnrollProgram.add( item );
                }
                else if ( item.isType( XMLItem.PROGRAM_STAGE_TYPE ) )
                {
                    itemProgramStage.add( item );
                }
                else if ( item.isType( XMLItem.ATTRIBUTE_TYPE ) )
                {
                    itemAttribute.add( item );
                }
                else
                {
                    itemProperty.add( item );
                }
            }
        }
        catch ( Exception t )
        {
            t.printStackTrace();
        }
    }
    
    private String readValue( int row, int column, Sheet sheet )
    {
        Cell cell = sheet.getRow( row ).getCell( column );

        String value = "";

        if ( cell != null )
        {
            switch ( cell.getCellType() )
            {
            case Cell.CELL_TYPE_NUMERIC:

                if ( HSSFDateUtil.isCellDateFormatted( cell ) )
                {
                    value = format.formatDate( cell.getDateCellValue() );
                }
                else
                {
                    value = String.valueOf( Math.round( cell.getNumericCellValue() ) );
                }
                break;

            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf( cell.getBooleanCellValue() );
                break;

            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;

            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().toString();
                break;
            }
        }

        return value;

    }

    private void setObject( Patient patient, String property, String value )
        throws Exception
    {
        Type type = Patient.class.getMethod( "get" + StringUtils.capitalize( property ) ).getReturnType();

        // Get value
        if ( type == Integer.class || type == Integer.TYPE )
        {
            Integer object = Integer.valueOf( value );
            Patient.class.getMethod( "set" + StringUtils.capitalize( property ), Integer.class ).invoke( patient,
                object );
        }
        else if ( type.equals( Boolean.class ) || type == Boolean.TYPE )
        {
            Boolean object = Boolean.valueOf( value );
            Patient.class.getMethod( "set" + StringUtils.capitalize( property ), Boolean.class ).invoke( patient,
                object );
        }
        else if ( type.equals( Date.class ) )
        {
            Date object = format.parseDate( value.trim() );
            Patient.class.getMethod( "set" + StringUtils.capitalize( property ), Date.class ).invoke( patient, object );
        }
        else if ( type.equals( Character.class ) || type == Character.TYPE )
        {
            Character object = Character.valueOf( value.charAt( 0 ) );
            Patient.class.getMethod( "set" + StringUtils.capitalize( property ), Character.class ).invoke( patient,
                object );
        }
        else
        {
            Patient.class.getMethod( "set" + StringUtils.capitalize( property ), String.class ).invoke( patient, value );
        }

    }
}
