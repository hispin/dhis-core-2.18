package org.hisp.dhis.coldchain.equipment;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "equipment", namespace = DxfNamespaces.DXF_2_0)
public class Equipment extends BaseNameableObject implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    //private int id;
    
    private EquipmentType equipmentType;
    
    private OrganisationUnit organisationUnit;
    
    private Model model;
        
    private boolean working = false;
    
    private Date registrationDate;
    
    private String equipmentTrackingID;
    
    // Current working status
    private String cws;
    
    private Set<EquipmentStatus> equipmentStatusUpdates;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public Equipment()
    {
        
    }
    
    public Equipment( EquipmentType equipmentType, OrganisationUnit organisationUnit )
    {
        this.equipmentType = equipmentType;
        this.organisationUnit = organisationUnit;
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

        if ( !(o instanceof Equipment) )
        {
            return false;
        }

        final Equipment other = (Equipment) o;

        return equipmentType.equals( other.getEquipmentType() ) && organisationUnit.equals( other.getOrganisationUnit() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + equipmentType.hashCode();
        result = result * prime + organisationUnit.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }

    public void setEquipmentType( EquipmentType equipmentType )
    {
        this.equipmentType = equipmentType;
    }

    @JsonProperty( value = "organisationUnit" )
    @JsonSerialize( contentAs = BaseNameableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0 )	
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isWorking()
    {
        return working;
    }

    public void setWorking( boolean working )
    {
        this.working = working;
    }

    public Set<EquipmentStatus> getEquipmentStatusUpdates()
    {
        return equipmentStatusUpdates;
    }

    public void setEquipmentStatusUpdates( Set<EquipmentStatus> equipmentStatusUpdates )
    {
        this.equipmentStatusUpdates = equipmentStatusUpdates;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Model getModel()
    {
        return model;
    }

    public void setModel( Model model )
    {
        this.model = model;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)    
    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate( Date registrationDate )
    {
        this.registrationDate = registrationDate;
    }
    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)    
    public String getEquipmentTrackingID()
    {
        return equipmentTrackingID;
    }

    public void setEquipmentTrackingID( String equipmentTrackingID )
    {
        this.equipmentTrackingID = equipmentTrackingID;
    }
    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public String getCws()
    {
        return cws;
    }

    public void setCws( String cws )
    {
        this.cws = cws;
    }
    
    
    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if( other.getClass().isInstance( this ) )
        {
            Equipment equipment = (Equipment) other;          
            String et = equipmentType.getName();
            String mn = model.getName();
            id = equipment.getId(); 
            name = ( et == null && mn == null ) ? name : et + ":" + mn;           
            equipmentType = equipment.getEquipmentType() == null ? equipmentType : equipment.getEquipmentType();           
            organisationUnit = equipment.getOrganisationUnit() == null ? organisationUnit : equipment.getOrganisationUnit();            
            model = equipment.getModel() == null ? model : equipment.getModel();            
            working = equipment.isWorking();          
            registrationDate = equipment.getRegistrationDate() == null ? registrationDate : equipment.getRegistrationDate();
            equipmentTrackingID = equipment.getEquipmentTrackingID() == null ? equipmentTrackingID : equipment.getEquipmentTrackingID();
            cws = equipment.getCws() == null ? cws : equipment.getCws();
        }
    }    
}
