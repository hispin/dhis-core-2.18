package org.hisp.dhis.patient.action.caseaggregation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElementService;

import com.opensymphony.xwork2.Action;

public class CaseAggregationMapping implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private ProgramStageDataElementService programStageDataElementService;
    
    public void setProgramStageDataElementService(
        ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }
        
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<PatientAttribute> patientAttributeList;
    
    public List<PatientAttribute> getPatientAttributeList()
    {
        return patientAttributeList;
    }
    
    private List<DataElementGroup> deGroupList;
    
    public List<DataElementGroup> getDeGroupList()
    {
        return deGroupList;
    }

    private List<DataElement> dataElementList;
    
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }
    
    private List<String> optionComboNames;

    public List<String> getOptionComboNames()
    {
        return optionComboNames;
    }

    private List<String> optionComboIds;
    
    public List<String> getOptionComboIds()
    {
        return optionComboIds;
    }
    
    private List<Program> programList;
    
    public List<Program> getProgramList()
    {
        return programList;
    }
    
    private List<ProgramStage> programStageList;
    
    public List<ProgramStage> getProgramStageList()
    {
        return programStageList;
    }

    private List<DataElement> programStageDEList;
    
    public List<DataElement> getProgramStageDEList()
    {
        return programStageDEList;
    }

    private String intType;
    
    public String getIntType()
    {
        return intType;
    }

    private String stringType;

    public String getStringType()
    {
        return stringType;
    }

    private String dateType;

    public String getDateType()
    {
        return dateType;
    }

    private String boolType;

    public String getBoolType()
    {
        return boolType;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {    
        intType = DataElement.VALUE_TYPE_INT;
        
        stringType = DataElement.VALUE_TYPE_STRING;
        
        dateType = DataElement.VALUE_TYPE_DATE;
        
        boolType = DataElement.VALUE_TYPE_BOOL;
        
        optionComboNames = new ArrayList<String>();
        
        optionComboIds = new ArrayList<String>();
        
        patientAttributeList = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
        
        deGroupList = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        
        programList = new ArrayList<Program>( programService.getAllPrograms() );
        
        if( programList != null && !programList.isEmpty() )
        {
            programStageList = new ArrayList<ProgramStage>( programList.get( 0 ).getProgramStages() );
        }
        if( programStageList != null && !programStageList.isEmpty() )
        {
            programStageDEList = new ArrayList<DataElement>( programStageDataElementService.getListDataElement( programStageList.get( 0 ) )  );
        }
        
        if( deGroupList != null && !deGroupList.isEmpty() )
        {
            dataElementList = new ArrayList<DataElement>( deGroupList.get( 0 ).getMembers() );
            
            Iterator<DataElement> deIterator = dataElementList.iterator();
            
            while( deIterator.hasNext() )
            {
                DataElement de = deIterator.next();
                if( de.getDomainType() != null )
                    if( de.getDomainType().equalsIgnoreCase( DataElement.DOMAIN_TYPE_PATIENT ) )
                        deIterator.remove();
            }
        }
        else
        {
            dataElementList = new ArrayList<DataElement>( dataElementService.getAggregateableDataElements() );
        }
            
        if( dataElementList != null && !dataElementList.isEmpty() )
        {
            Iterator<DataElement> deIterator = dataElementList.iterator();
            
            while(deIterator.hasNext())
            {
                DataElement de = deIterator.next();
                
                DataElementCategoryCombo dataElementCategoryCombo = de.getCategoryCombo();
                                
                List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                    dataElementCategoryCombo.getOptionCombos() );
    
                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                
                while ( optionComboIterator.hasNext() )
                {
                    DataElementCategoryOptionCombo decoc = optionComboIterator.next();
                    
                    optionComboIds.add( de.getId()+":"+decoc.getId());
                    
                    optionComboNames.add( de.getName()+":"+decoc.getName() );                
                }   
            }
                        
        }
        
        return SUCCESS;
    }
    
}
