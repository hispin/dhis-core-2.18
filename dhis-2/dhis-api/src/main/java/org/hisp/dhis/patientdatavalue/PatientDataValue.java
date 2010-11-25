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
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit, Date timeStamp, String value,
        boolean providedByAnotherFacility )
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
        result = prime * result + ((dataElement == null) ? 0 : dataElement.hashCode());
        result = prime * result + ((optionCombo == null) ? 0 : optionCombo.hashCode());
        result = prime * result + ((organisationUnit == null) ? 0 : organisationUnit.hashCode());
        result = prime * result + ((programStageInstance == null) ? 0 : programStageInstance.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        PatientDataValue other = (PatientDataValue) obj;
        if ( dataElement == null )
        {
            if ( other.dataElement != null )
                return false;
        }
        else if ( !dataElement.equals( other.dataElement ) )
            return false;
        if ( optionCombo == null )
        {
            if ( other.optionCombo != null )
                return false;
        }
        else if ( !optionCombo.equals( other.optionCombo ) )
            return false;
        if ( organisationUnit == null )
        {
            if ( other.organisationUnit != null )
                return false;
        }
        else if ( !organisationUnit.equals( other.organisationUnit ) )
            return false;
        if ( programStageInstance == null )
        {
            if ( other.programStageInstance != null )
                return false;
        }
        else if ( !programStageInstance.equals( other.programStageInstance ) )
            return false;
        return true;
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
