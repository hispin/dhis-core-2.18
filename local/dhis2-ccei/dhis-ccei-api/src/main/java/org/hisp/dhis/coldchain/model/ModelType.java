package org.hisp.dhis.coldchain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataentryform.DataEntryForm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "modelType", namespace = DxfNamespaces.DXF_2_0)
public class ModelType extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static String PREFIX_MODEL_TYPE = "Vaccines";
    
    //private int id;
    
    //private String name;
    
    //private String description;
    
    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
	
    private DataEntryForm dataEntryForm;
    
    private String modelTypeImage;
    
    
    /**
     * The ModelTypeAttributeGroup associated with the ModelType.
     */
    private Set<ModelTypeAttributeGroup> modelTypeAttributeGroups = new HashSet<ModelTypeAttributeGroup>();
    

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public ModelType()
    {

    }
    public ModelType( String name )
    {
        this.name = name;
    }
    
    public ModelType( String name, String description )
    {
        this.name = name;
        this.description = description;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
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

        if ( !(o instanceof ModelType) )
        {
            return false;
        }

        final ModelType other = (ModelType) o;

        return name.equals( other.getName() );
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
    
    public String getName()
    {
        return name;
    }
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription( String description )
    {
        this.description = description;
    }

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }
    
    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }

    @JsonProperty( value = "modelTypeAttribute" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class } )
    @JacksonXmlElementWrapper( localName = "modelTypeAttributes", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "modelTypeAttribute", namespace = DxfNamespaces.DXF_2_0)
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    
    public void setModelTypeAttributes( List<ModelTypeAttribute> modelTypeAttributes )
    {
        this.modelTypeAttributes = modelTypeAttributes;
    }
    
    public String getModelTypeImage()
    {
        return modelTypeImage;
    }
    
    public void setModelTypeImage( String modelTypeImage )
    {
        this.modelTypeImage = modelTypeImage;
    }

    @JsonProperty( value = "modelTypeAttributeGroup" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class } )
    @JacksonXmlElementWrapper( localName = "modelTypeAttributeGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "modelTypeAttributeGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<ModelTypeAttributeGroup> getModelTypeAttributeGroups()
    {
        return modelTypeAttributeGroups;
    }
    
    public void setModelTypeAttributeGroups( Set<ModelTypeAttributeGroup> modelTypeAttributeGroups )
    {
        this.modelTypeAttributeGroups = modelTypeAttributeGroups;
    }
    
    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if(other.getClass().isInstance( this ))
        {
            ModelType modelType = (ModelType) other;          
            id = modelType.getId();                      
            name = modelType.getName() == null ? name : modelType.getName();            
            description = modelType.getDescription() == null ? description : modelType.getDescription();            
            modelTypeAttributes.clear();
            modelTypeAttributes.addAll(modelType.getModelTypeAttributes());             
            modelTypeAttributeGroups.clear();
            modelTypeAttributeGroups.addAll(modelType.getModelTypeAttributeGroups());          
        }
    }    
}
