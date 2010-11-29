package org.hisp.dhis.dataset.action.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;

public class GetSectionOptionsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer categoryComboId;
    
    public void setCategoryComboId( Integer categoryComboId )
    {
        this.categoryComboId = categoryComboId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private DataSet dataSet;

    public DataSet getDataSet()
    {
        return dataSet;
    }

    private DataElementCategoryCombo categoryCombo;
    
    public DataElementCategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }
    
    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        dataSet = dataSetService.getDataSet( dataSetId );
        
        categoryCombo = categoryService.getDataElementCategoryCombo( categoryComboId );
        
        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() ); // Available data elements must be member of data set

        for ( Section section : dataSet.getSections() )
        {
            dataElements.removeAll( section.getDataElements() ); // Remove data elements used in other sections for data set
        }

        categoryCombo = categoryService.getDataElementCategoryCombo( categoryComboId.intValue() );

        Iterator<DataElement> dataElementIterator = dataElements.iterator();

        while ( dataElementIterator.hasNext() )
        {
            DataElement de = dataElementIterator.next();

            if ( !de.getCategoryCombo().getName().equalsIgnoreCase( categoryCombo.getName() ) )
            {
                dataElementIterator.remove(); // Remove data elements with different category combo
            }
        }

        Collections.sort( dataElements, dataElementComparator );

        displayPropertyHandler.handle( dataElements );

        return SUCCESS;
    }
}
