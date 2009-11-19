package org.hisp.dhis.patientdatavalue;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.ProgramStageInstance;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class PatientDataValue
    implements Serializable
{

    private DataElement dataElement;

    private DataElementCategoryOptionCombo optionCombo;

    private ProgramStageInstance programStageInstance;

    private OrganisationUnit organisationUnit;

    private Date timestamp;

    private String value;
    
    private boolean providedByAnotherFacility = false;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientDataValue()
    {
    }

    public PatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit )
    {
        this.programStageInstance = programStageInstance;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.organisationUnit = organisationUnit;
    }

    public PatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit, Date timeStamp )
    {
        this.programStageInstance = programStageInstance;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.organisationUnit = organisationUnit;
        this.timestamp = timeStamp;
    }

    public PatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit, Date timeStamp, String value )
    {
        this.programStageInstance = programStageInstance;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.organisationUnit = organisationUnit;
        this.timestamp = timeStamp;
        this.value = value;
    }
    
    public PatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit, Date timeStamp, String value, boolean providedByAnotherFacility )
    {
        this.programStageInstance = programStageInstance;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.organisationUnit = organisationUnit;
        this.timestamp = timeStamp;
        this.value = value;
        this.providedByAnotherFacility = providedByAnotherFacility;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + programStageInstance.hashCode();
        result = result * prime + dataElement.hashCode();        
        result = result * prime + organisationUnit.hashCode();

        return result;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof PatientDataValue) )
        {
            return false;
        }

        final PatientDataValue other = (PatientDataValue) o;

        return programStageInstance.equals( other.programStageInstance ) && dataElement.equals( other.dataElement )
            && organisationUnit.equals( other.organisationUnit );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public void setProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        this.programStageInstance = programStageInstance;
    }

    public ProgramStageInstance getProgramStageInstance()
    {
        return programStageInstance;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }

    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }   
    
    public void setProvidedByAnotherFacility( boolean providedByAnotherFacility )
    {
        this.providedByAnotherFacility = providedByAnotherFacility;
    }
    
    public boolean isProvidedByAnotherFacility()
    {
        return providedByAnotherFacility;
    }
}
