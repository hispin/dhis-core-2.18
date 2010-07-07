package org.hisp.dhis.linelisting;

import java.io.Serializable;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

public class LineListDataElementMap implements Serializable
{
    /**
     * Linelist Element
     */
    private LineListElement linelistElement;

    /**
     * Linelist Option
     */
    private LineListOption linelistOption;
    
    
    /**
     * DataElement
     */
    private DataElement dataElement;

    
    /**
     * Option Combo for DataElement.
     */
    private DataElementCategoryOptionCombo dataElementOptionCombo;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LineListDataElementMap()
    {
        
    }
    
    public LineListDataElementMap( LineListElement linelistElement, LineListOption linelistOption, DataElement dataElement, DataElementCategoryOptionCombo dataElementOptionCombo )
    {
        this.linelistElement = linelistElement;
        this.dataElement = dataElement;
        this.dataElementOptionCombo = dataElementOptionCombo;
        this.linelistOption = linelistOption;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public LineListElement getLinelistElement()
    {
        return linelistElement;
    }

    public void setLinelistElement( LineListElement linelistElement )
    {
        this.linelistElement = linelistElement;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElementCategoryOptionCombo getDataElementOptionCombo()
    {
        return dataElementOptionCombo;
    }

    public void setDataElementOptionCombo( DataElementCategoryOptionCombo dataElementOptionCombo )
    {
        this.dataElementOptionCombo = dataElementOptionCombo;
    }

    public LineListOption getLinelistOption()
    {
        return linelistOption;
    }

    public void setLinelistOption( LineListOption linelistOption )
    {
        this.linelistOption = linelistOption;
    }
    
}
