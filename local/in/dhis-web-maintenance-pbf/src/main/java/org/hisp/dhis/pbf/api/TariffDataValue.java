package org.hisp.dhis.pbf.api;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

public class TariffDataValue implements Serializable
{

    private OrganisationUnit organisationUnit;
    
    private DataElement dataElement;
    
    private DataElementCategoryOptionCombo optionCombo;
    
    private OrganisationUnitGroup organisationUnitGroup;
    
    private Date startDate;
    
    private Date endDate;
    
    private Double value;
    
    private String storedBy;

    private Date timestamp;

    private String comment;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public TariffDataValue()
    {
        
    }
    
    public TariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, OrganisationUnitGroup organisationUnitGroup, Date startDate, Date endDate, Double value )
    {
        this.organisationUnit = organisationUnit;
        this.dataElement = dataElement;
        this.organisationUnitGroup = organisationUnitGroup;
        this.startDate = startDate;
        this.endDate = endDate;
        this.value = value;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

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

        if ( !(o instanceof TariffDataValue) )
        {
            return false;
        }

        final TariffDataValue other = (TariffDataValue) o;

        return dataElement.equals( other.getDataElement() ) && optionCombo.equals( other.getOptionCombo() )
            && organisationUnitGroup.equals( other.getOrganisationUnitGroup() ) && organisationUnit.equals( other.getOrganisationUnit() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + optionCombo.hashCode();
        result = result * prime + organisationUnitGroup.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + organisationUnit.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }

    public OrganisationUnitGroup getOrganisationUnitGroup()
    {
        return organisationUnitGroup;
    }

    public void setOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup )
    {
        this.organisationUnitGroup = organisationUnitGroup;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    public Double getValue()
    {
        return value;
    }

    public void setValue( Double value )
    {
        this.value = value;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

}
