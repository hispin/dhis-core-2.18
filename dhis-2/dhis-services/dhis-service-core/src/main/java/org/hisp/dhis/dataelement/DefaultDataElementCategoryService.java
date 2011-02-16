package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.system.util.UUIdUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultDataElementCategoryService
    implements DataElementCategoryService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<DataElementCategory> dataElementCategoryStore;

    public void setDataElementCategoryStore(
        GenericIdentifiableObjectStore<DataElementCategory> dataElementCategoryStore )
    {
        this.dataElementCategoryStore = dataElementCategoryStore;
    }

    private GenericIdentifiableObjectStore<DataElementCategoryOption> dataElementCategoryOptionStore;

    public void setDataElementCategoryOptionStore(
        GenericIdentifiableObjectStore<DataElementCategoryOption> dataElementCategoryOptionStore )
    {
        this.dataElementCategoryOptionStore = dataElementCategoryOptionStore;
    }

    private GenericIdentifiableObjectStore<DataElementCategoryCombo> dataElementCategoryComboStore;

    public void setDataElementCategoryComboStore(
        GenericIdentifiableObjectStore<DataElementCategoryCombo> dataElementCategoryComboStore )
    {
        this.dataElementCategoryComboStore = dataElementCategoryComboStore;
    }

    private GenericIdentifiableObjectStore<DataElementCategoryOptionCombo> dataElementCategoryOptionComboStore;

    public void setDataElementCategoryOptionComboStore(
        GenericIdentifiableObjectStore<DataElementCategoryOptionCombo> dataElementCategoryOptionComboStore )
    {
        this.dataElementCategoryOptionComboStore = dataElementCategoryOptionComboStore;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Category
    // -------------------------------------------------------------------------

    public int addDataElementCategory( DataElementCategory dataElementCategory )
    {
        if ( dataElementCategory != null && dataElementCategory.getUuid() == null )
        {
            dataElementCategory.setUuid( UUIdUtils.getUUId() );
        }

        return dataElementCategoryStore.save( dataElementCategory );
    }

    public void updateDataElementCategory( DataElementCategory dataElementCategory )
    {
        dataElementCategoryStore.update( dataElementCategory );
    }

    public void deleteDataElementCategory( DataElementCategory dataElementCategory )
    {
        dataElementCategoryStore.delete( dataElementCategory );
    }

    public Collection<DataElementCategory> getAllDataElementCategories()
    {
        return dataElementCategoryStore.getAll();
    }

    public DataElementCategory getDataElementCategory( int id )
    {
        return dataElementCategoryStore.get( id );
    }

    public Collection<DataElementCategory> getDataElementCategories( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategory> categories = getAllDataElementCategories();

        return identifiers == null ? categories : FilterUtils.filter( categories, new Filter<DataElementCategory>()
        {
            public boolean retain( DataElementCategory object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public DataElementCategory getDataElementCategoryByName( String name )
    {
        return dataElementCategoryStore.getByName( name );
    }

    // -------------------------------------------------------------------------
    // CategoryOption
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        if ( dataElementCategoryOption != null && dataElementCategoryOption.getUuid() == null )
        {
            dataElementCategoryOption.setUuid( UUIdUtils.getUUId() );
        }

        return dataElementCategoryOptionStore.save( dataElementCategoryOption );
    }

    public void updateDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        dataElementCategoryOptionStore.update( dataElementCategoryOption );
    }

    public void deleteDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        dataElementCategoryOptionStore.delete( dataElementCategoryOption );
    }

    public DataElementCategoryOption getDataElementCategoryOption( int id )
    {
        return dataElementCategoryOptionStore.get( id );
    }

    public DataElementCategoryOption getDataElementCategoryOptionByName( String name )
    {
        return dataElementCategoryOptionStore.getByName( name );
    }

    public Collection<DataElementCategoryOption> getDataElementCategoryOptions( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryOption> categoryOptions = getAllDataElementCategoryOptions();

        return identifiers == null ? categoryOptions : FilterUtils.filter( categoryOptions,
            new Filter<DataElementCategoryOption>()
            {
                public boolean retain( DataElementCategoryOption object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public Collection<DataElementCategoryOption> getAllDataElementCategoryOptions()
    {
        return dataElementCategoryOptionStore.getAll();
    }

    // -------------------------------------------------------------------------
    // CategoryCombo
    // -------------------------------------------------------------------------

    public int addDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        return dataElementCategoryComboStore.save( dataElementCategoryCombo );
    }

    public void updateDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        dataElementCategoryComboStore.save( dataElementCategoryCombo );
    }

    public void deleteDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        dataElementCategoryComboStore.delete( dataElementCategoryCombo );
    }

    public Collection<DataElementCategoryCombo> getAllDataElementCategoryCombos()
    {
        return dataElementCategoryComboStore.getAll();
    }

    public DataElementCategoryCombo getDataElementCategoryCombo( int id )
    {
        return dataElementCategoryComboStore.get( id );
    }
    
    public Collection<DataElementCategoryCombo> getDataElementCategoryCombos( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryCombo> categoryCombo = getAllDataElementCategoryCombos();

        return identifiers == null ? categoryCombo : FilterUtils.filter( categoryCombo,
            new Filter<DataElementCategoryCombo>()
            {
                public boolean retain( DataElementCategoryCombo object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public DataElementCategoryCombo getDataElementCategoryComboByName( String name )
    {
        return dataElementCategoryComboStore.getByName( name );
    }

    // -------------------------------------------------------------------------
    // CategoryOptionCombo
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        if ( dataElementCategoryOptionCombo != null && dataElementCategoryOptionCombo.getUuid() == null )
        {
            dataElementCategoryOptionCombo.setUuid( UUIdUtils.getUUId() );
        }

        return dataElementCategoryOptionComboStore.save( dataElementCategoryOptionCombo );
    }

    public void updateDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        dataElementCategoryOptionComboStore.update( dataElementCategoryOptionCombo );
    }

    public void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        dataElementCategoryOptionComboStore.delete( dataElementCategoryOptionCombo );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( int id )
    {
        return dataElementCategoryOptionComboStore.get( id );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( String uuid ) {
        return dataElementCategoryOptionComboStore.getByUuid( uuid );
    }


    
    public Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos(
        final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryOptionCombo> categoryOptionCombos = getAllDataElementCategoryOptionCombos();

        return identifiers == null ? categoryOptionCombos : FilterUtils.filter( categoryOptionCombos,
            new Filter<DataElementCategoryOptionCombo>()
            {
                public boolean retain( DataElementCategoryOptionCombo object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo(
        Collection<DataElementCategoryOption> categoryOptions )
    {
        for ( DataElementCategoryOptionCombo categoryOptionCombo : getAllDataElementCategoryOptionCombos() )
        {
            if ( CollectionUtils.isEqualCollection( categoryOptions, categoryOptionCombo.getCategoryOptions() ) )
            {
                return categoryOptionCombo;
            }
        }

        return null;
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo(
        DataElementCategoryOptionCombo categoryOptionCombo )
    {
        for ( DataElementCategoryOptionCombo dcoc : getAllDataElementCategoryOptionCombos() )
        {
            // -----------------------------------------------------------------
            // Hibernate puts proxies on associations and makes the native
            // equals methods unusable
            // -----------------------------------------------------------------

            if ( dcoc.equalsOnName( categoryOptionCombo ) )
            {
                return dcoc;
            }
        }

        return null;
    }

    public Collection<DataElementCategoryOptionCombo> getAllDataElementCategoryOptionCombos()
    {
        return dataElementCategoryOptionComboStore.getAll();
    }

    public void generateDefaultDimension()
    {
        // ---------------------------------------------------------------------
        // Add default DataElementCategoryOption
        // ---------------------------------------------------------------------

        DataElementCategoryOption categoryOption = new DataElementCategoryOption(
            DataElementCategoryOption.DEFAULT_NAME );

        addDataElementCategoryOption( categoryOption );

        // ---------------------------------------------------------------------
        // Add default DataElementCategory containing default
        // DataElementCategoryOption
        // ---------------------------------------------------------------------

        DataElementCategory category = new DataElementCategory( DataElementCategory.DEFAULT_NAME );

        List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();
        categoryOptions.add( categoryOption );
        category.setCategoryOptions( categoryOptions );

        addDataElementCategory( category );

        // ---------------------------------------------------------------------
        // Add default DataElementCategoryCombo made of the default
        // DataElementCategory
        // ---------------------------------------------------------------------

        DataElementCategoryCombo categoryCombo = new DataElementCategoryCombo(
            DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        List<DataElementCategory> categories = new ArrayList<DataElementCategory>();
        categories.add( category );
        categoryCombo.setCategories( categories );

        addDataElementCategoryCombo( categoryCombo );

        // ---------------------------------------------------------------------
        // Add default DataElementCategoryOptionCombo
        // ---------------------------------------------------------------------

        DataElementCategoryOptionCombo categoryOptionCombo = new DataElementCategoryOptionCombo();

        categoryOptionCombo.setCategoryCombo( categoryCombo );
        categoryOptionCombo.setCategoryOptions( new ArrayList<DataElementCategoryOption>( categoryOptions ) );

        addDataElementCategoryOptionCombo( categoryOptionCombo );

        Set<DataElementCategoryOptionCombo> categoryOptionCombos = new HashSet<DataElementCategoryOptionCombo>();
        categoryOptionCombos.add( categoryOptionCombo );
        categoryCombo.setOptionCombos( categoryOptionCombos );

        updateDataElementCategoryCombo( categoryCombo );
    }

    public DataElementCategoryOptionCombo getDefaultDataElementCategoryOptionCombo()
    {
        DataElementCategoryCombo categoryCombo = getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        return categoryCombo.getOptionCombos().iterator().next();
    }

    public Collection<DataElementOperand> populateOperands( Collection<DataElementOperand> operands )
    {
        for ( DataElementOperand operand : operands )
        {
            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            DataElementCategoryOptionCombo categoryOptionCombo = getDataElementCategoryOptionCombo( operand.getOptionComboId() );
            
            operand.updateProperties( dataElement, categoryOptionCombo );
        }
        
        return operands;
    }
    
    public Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements, boolean includeTotals )
    {
        Collection<DataElementOperand> operands = new ArrayList<DataElementOperand>();

        for ( DataElement dataElement : dataElements )
        {
            if ( !dataElement.getCategoryCombo().isDefault() && includeTotals )
            {
                DataElementOperand operand = new DataElementOperand();
                operand.updateProperties( dataElement );
                
                operands.add( operand );
            }
            
            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getOptionCombos() )
            {
                DataElementOperand operand = new DataElementOperand();
                operand.updateProperties( dataElement, categoryOptionCombo );

                operands.add( operand );
            }
        }

        return operands;
    }
    
    public Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements )
    {
        return getOperands( dataElements, false );
    }

    public Collection<DataElementOperand> getOperandsLikeName( String name )
    {
        Collection<DataElement> dataElements = dataElementService.getDataElementsLikeName( name );
        
        return getOperands( dataElements );
    }
    
    public Collection<DataElementOperand> getFullOperands( Collection<DataElement> dataElements )
    {
        Collection<DataElementOperand> operands = new ArrayList<DataElementOperand>();

        for ( DataElement dataElement : dataElements )
        {
            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getOptionCombos() )
            {
                DataElementOperand operand = new DataElementOperand( dataElement, categoryOptionCombo );
                operand.updateProperties( dataElement, categoryOptionCombo );

                operands.add( operand );
            }
        }

        return operands;
    }

    public void generateOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        int totalOptionCombos = 1;

        for ( DataElementCategory category : categoryCombo.getCategories() )
        {
            totalOptionCombos = totalOptionCombos * category.getCategoryOptions().size();
        }

        /*
         * Iterate through the collection of optionsMap every time picking one
         * option from each collection. Because we have put enough number of
         * options in each collection, better to remove the picked options so
         * that we don't get confused how many times to pick an option - pick an
         * option only once!
         */
        Map<Integer, Collection<DataElementCategoryOption>> optionsMap = prepareOptionsForCombination( categoryCombo );

        Set<DataElementCategoryOptionCombo> optionCombos = new HashSet<DataElementCategoryOptionCombo>(
            totalOptionCombos );

        for ( int i = 0; i < totalOptionCombos; i++ )
        {
            List<DataElementCategoryOption> options = new ArrayList<DataElementCategoryOption>( categoryCombo
                .getCategories().size() );

            /*
             * We are going to iterate the list of categories a number of times.
             * better to create a copy and iterate through the copy. we can stop
             * iterating when we have create the required option combinations.
             */
            Collection<DataElementCategory> copyOfCategories = categoryCombo.getCategories();

            Iterator<DataElementCategory> categoryIterator = copyOfCategories.iterator();

            while ( categoryIterator.hasNext() )
            {
                DataElementCategory cat = categoryIterator.next();

                /*
                 * From each category pick one option
                 */
                Iterator<DataElementCategoryOption> optionIterator = optionsMap.get( cat.getId() ).iterator();

                DataElementCategoryOption option = optionIterator.next();

                options.add( option );

                /*
                 * Once we used the option, better to remove it. because we have
                 * enough number of options
                 */
                optionIterator.remove();
            }

            DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

            optionCombo.setCategoryCombo( categoryCombo );

            optionCombo.setCategoryOptions( options );

            addDataElementCategoryOptionCombo( optionCombo );

            optionCombos.add( optionCombo );
        }

        categoryCombo.setOptionCombos( optionCombos );

        updateDataElementCategoryCombo( categoryCombo );
    }

    public Collection<DataElementCategoryOptionCombo> sortOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        Collection<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
            categoryCombo.getOptionCombos() );

        int totalColumns = optionCombos.size();

        Map<Integer, Collection<DataElementCategoryOption>> orderedOptions = prepareOptionsForCombination( categoryCombo );

        Collection<DataElementCategoryOptionCombo> orderdCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

        for ( int i = 0; i < totalColumns; i++ )
        {
            Collection<DataElementCategoryOption> options = new ArrayList<DataElementCategoryOption>( categoryCombo
                .getCategories().size() );

            Collection<DataElementCategory> copyOforderedCategories = categoryCombo.getCategories();

            Iterator<DataElementCategory> categoryIterator = copyOforderedCategories.iterator();

            while ( categoryIterator.hasNext() )
            {
                DataElementCategory category = categoryIterator.next();
                Iterator<DataElementCategoryOption> optionIterator = orderedOptions.get( category.getId() ).iterator();
                DataElementCategoryOption option = optionIterator.next();
                options.add( option );
                optionIterator.remove();
            }

            for ( DataElementCategoryOptionCombo optionCombo : optionCombos )
            {
                if ( optionCombo.getCategoryOptions().containsAll( options ) )
                {
                    orderdCategoryOptionCombos.add( optionCombo );
                    break;
                }
            }
        }

        return orderdCategoryOptionCombos;
    }

    private Map<Integer, Collection<DataElementCategoryOption>> prepareOptionsForCombination(
        DataElementCategoryCombo categoryCombo )
    {
        // Get categories for a given category

        List<DataElementCategory> categories = new ArrayList<DataElementCategory>( categoryCombo.getCategories() );

        /*
         * Get the total number of option combinations that will come into
         * existence when combining categories having their own options
         * 
         * Eg. Category SEX with Options MALE and FEMALE Category AGE with
         * Options <5years, 5-15years, >15years When combining these two
         * categories we are going to have a total of 6 option combinations
         * MALE_<5years,MALE_5-15years,MALE_>15years
         * FEMALE_<5years,FEMALE_5-15years,FEMALE_>15years
         * 
         * 6 OptionCombinations = 2(from SEX) * 3(from AGE)
         * 
         * generalizing this we can have total option combinations by
         * multiplying the number of options in each category
         */
        int totalOptionCombos = 1;

        for ( DataElementCategory category : categories )
        {
            totalOptionCombos = totalOptionCombos * category.getCategoryOptions().size();
        }

        // ---------------------------------------------------------------------
        // Determine the number of times each category is going to repeat
        // ---------------------------------------------------------------------

        /*
         * Example again Category IPD_OPD, Options I and D Category Sex, Options
         * F and M Category Age, Options 0-5,5-10,11+
         * 
         * Category combination is IPD_OPD+Sex+Age
         * 
         * Option combinations ... with the following arrangement in Data Entry
         * 
         * I | I | I | I | I | I | O | O | O | O | O | O F | F | F | F | F | F |
         * M | M | M | M | M | M
         * 0-5|5-10|11+|0-5|5-10|11+1|0-5|5-10|11+|0-5|5-10|11+1
         * 
         * If we rearrange our categories like IPD_OPD+Age+Sex - then we will
         * have the same option combinations, but with different arrangement.
         * 
         * I | I | I | I | I | I | O | O | O | O | O | O 0-5| 0-5|5-10|5-10|
         * 11+| 11+|0-5| 0-5|5-10|5-10| 11+|11+ F | M | F | M | F | M |F | M | F
         * | M | F | M
         * 
         * If we assume that we will draw a data entry table header, the top a
         * category is in the order list, then the more ColSpan its options are
         * going to have
         */
        int categoryColSpan = totalOptionCombos;

        Map<Integer, Integer> categoryRepeat = new HashMap<Integer, Integer>();

        for ( DataElementCategory category : categories )
        {
            categoryColSpan = categoryColSpan / category.getCategoryOptions().size();

            categoryRepeat.put( category.getId(), categoryColSpan );
        }

        /*
         * If we see the above example, any option from AGE appear only twice
         * while an option from SEX appears three times....generalizing this we
         * can say set the following formula let
         * 
         * appearance = appearance of any option from a given category
         * category_options = number of options from the category containing the
         * option and option_combinations = total number of option combinations
         * 
         * appearance = option_combinaitions/category_options
         * 
         * each option becoming part of the option combinations for 'appearance'
         * number of times, then totally a category will be represented in the
         * option combinations option_combinaitions number of times.
         * 
         * Then we can prepare list of categories containing collection of its
         * options where each option is repeated 'appearance' times. By doing
         * this, we can iterate through these categories every time removing an
         * option from the category but putting it in the option combinations.
         */
        Map<Integer, Collection<DataElementCategoryOption>> optionsMap = new HashMap<Integer, Collection<DataElementCategoryOption>>();

        /*
         * For each category create a collection of options by repeating each of
         * its options 'appearance' number of times. The size of the collection
         * should be equal to total number of options combinations.
         */
        for ( DataElementCategory cat : categories )
        {
            Collection<DataElementCategoryOption> requiredOptions = new ArrayList<DataElementCategoryOption>();
            Collection<DataElementCategoryOption> options = cat.getCategoryOptions();

            int count = 0;

            while ( count < totalOptionCombos )
            {
                for ( DataElementCategoryOption option : options )
                {
                    for ( int i = 0; i < categoryRepeat.get( cat.getId() ); i++ )
                    {
                        requiredOptions.add( option );

                        count++;
                    }
                }
            }

            optionsMap.put( cat.getId(), requiredOptions );
        }

        return optionsMap;
    }

    public int getDataElementCategoryCount()
    {
        return dataElementCategoryStore.getCount();
    }

    public int getDataElementCategoryCountByName( String name )
    {
        return dataElementCategoryStore.getCountByName( name );
    }

    public Collection<DataElementCategory> getDataElementCategorysBetween( int first, int max )
    {
        return dataElementCategoryStore.getBetween( first, max );
    }

    public Collection<DataElementCategory> getDataElementCategorysBetweenByName( String name, int first, int max )
    {
        return dataElementCategoryStore.getBetweenByName( name, first, max );
    }

    public int getDataElementCategoryComboCount()
    {
        return dataElementCategoryComboStore.getCount();
    }

    public int getDataElementCategoryComboCountByName( String name )
    {
        return dataElementCategoryComboStore.getCountByName( name );
    }
    
    public Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetween( int first, int max )
    {
        return dataElementCategoryComboStore.getBetween( first, max );
    }

    public Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetweenByName( String name, int first,
        int max )
    {
        return dataElementCategoryComboStore.getBetweenByName( name, first, max );
    }

}
