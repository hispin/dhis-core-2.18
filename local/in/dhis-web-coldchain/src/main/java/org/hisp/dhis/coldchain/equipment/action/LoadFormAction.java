package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

public class LoadFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

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

    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<DataElementCategoryCombo, List<DataElement>> orderedDataElements = new HashMap<DataElementCategoryCombo, List<DataElement>>();

    public Map<DataElementCategoryCombo, List<DataElement>> getOrderedDataElements()
    {
        return orderedDataElements;
    }
    
    private List<DataElementCategoryCombo> orderedCategoryCombos = new ArrayList<DataElementCategoryCombo>();

    public List<DataElementCategoryCombo> getOrderedCategoryCombos()
    {
        return orderedCategoryCombos;
    }
    
    
    private Map<Integer, Integer> numberOfTotalColumns = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getNumberOfTotalColumns()
    {
        return numberOfTotalColumns;
    }
    
    private Map<Integer, Collection<DataElementCategoryOptionCombo>> orderdCategoryOptionCombos = new HashMap<Integer, Collection<DataElementCategoryOptionCombo>>();

    public Map<Integer, Collection<DataElementCategoryOptionCombo>> getOrderdCategoryOptionCombos()
    {
        return orderdCategoryOptionCombos;
    }
    
    private Map<Integer, Collection<DataElementCategory>> orderedCategories = new HashMap<Integer, Collection<DataElementCategory>>();

    public Map<Integer, Collection<DataElementCategory>> getOrderedCategories()
    {
        return orderedCategories;
    }
    
    private Map<Integer, Map<Integer, Collection<DataElementCategoryOption>>> orderedOptionsMap = new HashMap<Integer, Map<Integer, Collection<DataElementCategoryOption>>>();

    public Map<Integer, Map<Integer, Collection<DataElementCategoryOption>>> getOrderedOptionsMap()
    {
        return orderedOptionsMap;
    }
    
    private Map<Integer, Map<Integer, Collection<Integer>>> catColRepeat = new HashMap<Integer, Map<Integer, Collection<Integer>>>();

    public Map<Integer, Map<Integer, Collection<Integer>>> getCatColRepeat()
    {
        return catColRepeat;
    }
    
    private String customDataEntryFormCode;

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }

    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId, true, false, false );

        List<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );

        if ( dataElements.isEmpty() )
        {
            return INPUT;
        }

        Collections.sort( dataElements, dataElementComparator );

        orderedDataElements = dataElementService.getGroupedDataElementsByCategoryCombo( dataElements );

        orderedCategoryCombos = dataElementService.getDataElementCategoryCombos( dataElements );
        
        
        for ( DataElementCategoryCombo categoryCombo : orderedCategoryCombos )
        {
            List<DataElementCategoryOptionCombo> optionCombos = categoryCombo.getSortedOptionCombos();

            orderdCategoryOptionCombos.put( categoryCombo.getId(), optionCombos );

            // -----------------------------------------------------------------
            // Perform ordering of categories and their options so that they
            // could be displayed as in the paper form. Note that the total
            // number of entry cells to be generated are the multiple of options
            // from each category.
            // -----------------------------------------------------------------

            numberOfTotalColumns.put( categoryCombo.getId(), optionCombos.size() );

            orderedCategories.put( categoryCombo.getId(), categoryCombo.getCategories() );

            Map<Integer, Collection<DataElementCategoryOption>> optionsMap = new HashMap<Integer, Collection<DataElementCategoryOption>>();

            for ( DataElementCategory dec : categoryCombo.getCategories() )
            {
                optionsMap.put( dec.getId(), dec.getCategoryOptions() );
            }

            orderedOptionsMap.put( categoryCombo.getId(), optionsMap );

            // -----------------------------------------------------------------
            // Calculating the number of times each category should be repeated
            // -----------------------------------------------------------------

            Map<Integer, Integer> catRepeat = new HashMap<Integer, Integer>();

            Map<Integer, Collection<Integer>> colRepeat = new HashMap<Integer, Collection<Integer>>();

            int catColSpan = optionCombos.size();

            for ( DataElementCategory cat : categoryCombo.getCategories() )
            {
                int categoryOptionSize = cat.getCategoryOptions().size();

                if ( catColSpan > 0 && categoryOptionSize > 0 )
                {
                    catColSpan = catColSpan / categoryOptionSize;
                    int total = optionCombos.size() / (catColSpan * categoryOptionSize);
                    Collection<Integer> cols = new ArrayList<Integer>( total );

                    for ( int i = 0; i < total; i++ )
                    {
                        cols.add( i );
                    }

                    colRepeat.put( cat.getId(), cols );

                    catRepeat.put( cat.getId(), catColSpan );
                }
            }

            catColRepeat.put( categoryCombo.getId(), colRepeat );
        }

        // ---------------------------------------------------------------------
        // Get data entry form
        // ---------------------------------------------------------------------

        String displayMode = dataSet.getDataSetType();
        
        if ( displayMode.equals( DataSet.TYPE_SECTION ) )
        {
            //getSectionForm( dataElements, dataSet );
        }
        else
        {
            getOtherDataEntryForm( dataElements, dataSet );
        }
        
        
        System.out.println( displayMode );
        
        return displayMode;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    /*
    private void getSectionForm( Collection<DataElement> dataElements, DataSet dataSet )
    {
        sections = new ArrayList<Section>( dataSet.getSections() );

        Collections.sort( sections, new SectionOrderComparator() );

        for ( Section section : sections )
        {
            DataElementCategoryCombo sectionCategoryCombo = section.getCategoryCombo();

            if ( sectionCategoryCombo != null )
            {
                orderedCategoryCombos.add( sectionCategoryCombo );

                sectionCombos.put( section.getId(), sectionCategoryCombo.getId() );
            }

            for ( DataElementOperand operand : section.getGreyedFields() )
            {
                greyedFields.put( operand.getDataElement().getId() + ":" +
                    operand.getCategoryOptionCombo().getId(), true );
            }
        }
    }
    */
    private void getOtherDataEntryForm( List<DataElement> dataElements, DataSet dataSet )
    {
        DataEntryForm dataEntryForm = dataSet.getDataEntryForm();

        if ( dataEntryForm != null )
        {
            customDataEntryFormCode = dataEntryFormService.prepareDataEntryFormForEntry( dataEntryForm.getHtmlCode(),
                i18n, dataSet );
        }

        List<DataElement> des = new ArrayList<DataElement>();

        for ( DataElementCategoryCombo categoryCombo : orderedCategoryCombos )
        {
            des = (List<DataElement>) orderedDataElements.get( categoryCombo );

            Collections.sort( des, IdentifiableObjectNameComparator.INSTANCE );

            orderedDataElements.put( categoryCombo, des );
        }
    }
    
}
        
