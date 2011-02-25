package org.hisp.dhis.importexport.dxf2.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class DataElement extends Link
{

    @XmlAttribute
    private String type;

    @XmlElementWrapper(name="categoryOptionCombos")
    @XmlElement(name="categoryOptionCombo")
    private List<CategoryOptionCombo> categoryOptionCombos;
    
    
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public List<CategoryOptionCombo> getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( List<CategoryOptionCombo> categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }

}
