package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2007, University of Oslo
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


import org.hisp.dhis.common.GenericStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultDataElementCategoryOptionComboService
    implements DataElementCategoryOptionComboService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericStore<DataElementCategoryOptionCombo> dataElementCategoryOptionComboStore;

    public void setDataElementCategoryOptionComboStore(
        GenericStore<DataElementCategoryOptionCombo> dataElementCategoryOptionComboStore )
    {
        this.dataElementCategoryOptionComboStore = dataElementCategoryOptionComboStore;
    }

    private DataElementCategoryComboService dataElementCategoryComboService;

    public void setDataElementCategoryComboService( DataElementCategoryComboService dataElementCategoryComboService )
    {
        this.dataElementCategoryComboService = dataElementCategoryComboService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private DataElementCategoryOptionService dataElementCategoryOptionService;

    public void setDataElementCategoryOptionService( DataElementCategoryOptionService dataElementCategoryOptionService )
    {
        this.dataElementCategoryOptionService = dataElementCategoryOptionService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionCombo
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
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

    public Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos(
        Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllDataElementCategoryOptionCombos();
        }

        Collection<DataElementCategoryOptionCombo> categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

        for ( Integer id : identifiers )
        {
            categoryOptionCombos.add( getDataElementCategoryOptionCombo( id ) );
        }

        return categoryOptionCombos;
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

    public Collection<DataElementCategoryOptionCombo> sortDataElementCategoryOptionCombos(
        DataElementCategoryCombo catCombo )
    {
        Collection<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
            catCombo.getOptionCombos() );       

        // ---------------------------------------------------------------------
        // Determine the number of times each category is going to repeat
        // ---------------------------------------------------------------------       

        int categoryColSpan = optionCombos.size();

        Map<Integer, Integer> categoryRepeat = new HashMap<Integer, Integer>();

        for ( DataElementCategory category : catCombo.getCategories() )
        {
            categoryColSpan = categoryColSpan / category.getCategoryOptions().size();

            categoryRepeat.put( category.getId(), categoryColSpan );

        }

        Map<Integer, Collection<DataElementCategoryOption>> orderedOptions = new HashMap<Integer, Collection<DataElementCategoryOption>>();

        for ( DataElementCategory cat : catCombo.getCategories() )
        {
            int outerForLoopCount = optionCombos.size();
            int innerForLoopCount = categoryRepeat.get( cat.getId() );

            Collection<DataElementCategoryOption> requiredOptions = new ArrayList<DataElementCategoryOption>();
            Collection<DataElementCategoryOption> options = cat.getCategoryOptions();

            int x = 0;

            while ( x < outerForLoopCount )
            {
                for ( DataElementCategoryOption option : options )
                {
                    for ( int i = 0; i < innerForLoopCount; i++ )
                    {
                        requiredOptions.add( option );

                        x++;
                    }
                }
            }

            orderedOptions.put( cat.getId(), requiredOptions );
        }

        Collection<DataElementCategoryOptionCombo> orderdCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

        for ( int i = 0; i < optionCombos.size(); i++ )
        {
            Collection<DataElementCategoryOption> options = new ArrayList<DataElementCategoryOption>( catCombo
                .getCategories().size() );
            Collection<DataElementCategory> copyOforderedCategories = catCombo.getCategories();
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

    public void generateDefaultDimension()
    {
        // ---------------------------------------------------------------------
        // Add default DataElementCategoryOption
        // ---------------------------------------------------------------------

        DataElementCategoryOption categoryOption = new DataElementCategoryOption(
            DataElementCategoryOption.DEFAULT_NAME );

        dataElementCategoryOptionService.addDataElementCategoryOption( categoryOption );

        // ---------------------------------------------------------------------
        // Add default DataElementCategory containing default
        // DataElementCategoryOption
        // ---------------------------------------------------------------------

        DataElementCategory category = new DataElementCategory( DataElementCategory.DEFAULT_NAME );

        List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();
        categoryOptions.add( categoryOption );
        category.setCategoryOptions( categoryOptions );

        dataElementCategoryService.addDataElementCategory( category );

        // ---------------------------------------------------------------------
        // Add default DataElementCategoryCombo made of the default
        // DataElementCategory
        // ---------------------------------------------------------------------

        DataElementCategoryCombo categoryCombo = new DataElementCategoryCombo(
            DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        List<DataElementCategory> categories = new ArrayList<DataElementCategory>();
        categories.add( category );
        categoryCombo.setCategories( categories );

        dataElementCategoryComboService.addDataElementCategoryCombo( categoryCombo );

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

        dataElementCategoryComboService.updateDataElementCategoryCombo( categoryCombo );
    }

    public DataElementCategoryOptionCombo getDefaultDataElementCategoryOptionCombo()
    {
        DataElementCategoryCombo categoryCombo = dataElementCategoryComboService
            .getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        return categoryCombo.getOptionCombos().iterator().next();
    }

    public void generateOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        List<DataElementCategory> categories = new ArrayList<DataElementCategory>( categoryCombo.getCategories() );

        int totalOptionCombos = 1;

        for ( DataElementCategory category : categories )
        {
            totalOptionCombos = totalOptionCombos * category.getCategoryOptions().size();
        }

        int categoryOptionShare = totalOptionCombos;

        Map<Integer, Integer> categoryOptionAppearance = new HashMap<Integer, Integer>();

        for ( DataElementCategory cat : categories )
        {
            categoryOptionShare = categoryOptionShare / cat.getCategoryOptions().size();

            categoryOptionAppearance.put( cat.getId(), categoryOptionShare );
        }

        Map<Integer, Collection<DataElementCategoryOption>> optionsMap = new HashMap<Integer, Collection<DataElementCategoryOption>>();

        for ( DataElementCategory cat : categories )
        {
            int outerForLoopCount = totalOptionCombos;
            int innerForLoopCount = categoryOptionAppearance.get( cat.getId() );

            Collection<DataElementCategoryOption> requiredOptions = new ArrayList<DataElementCategoryOption>();
            List<DataElementCategoryOption> options = cat.getCategoryOptions();

            int x = 0;

            while ( x < outerForLoopCount )
            {
                for ( DataElementCategoryOption option : options )
                {
                    for ( int i = 0; i < innerForLoopCount; i++ )
                    {
                        requiredOptions.add( option );

                        x++;
                    }
                }
            }

            optionsMap.put( cat.getId(), requiredOptions );
        }

        Set<DataElementCategoryOptionCombo> optionCombos = new HashSet<DataElementCategoryOptionCombo>(
            totalOptionCombos );

        for ( int i = 0; i < totalOptionCombos; i++ )
        {
            List<DataElementCategoryOption> options = new ArrayList<DataElementCategoryOption>( categories.size() );

            Collection<DataElementCategory> copyOfCategories = categories;

            Iterator<DataElementCategory> categoryIterator = copyOfCategories.iterator();

            while ( categoryIterator.hasNext() )
            {
                DataElementCategory cat = categoryIterator.next();

                Iterator<DataElementCategoryOption> optionIterator = optionsMap.get( cat.getId() ).iterator();

                DataElementCategoryOption option = optionIterator.next();

                options.add( option );

                optionIterator.remove();
            }

            DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

            optionCombo.setCategoryCombo( categoryCombo );

            optionCombo.setCategoryOptions( options );

            addDataElementCategoryOptionCombo( optionCombo );

            optionCombos.add( optionCombo );
        }

        if ( categoryCombo.getOptionCombos().size() != optionCombos.size() )
        {
            categoryCombo.setOptionCombos( optionCombos );

            dataElementCategoryComboService.updateDataElementCategoryCombo( categoryCombo );
        }
    }    

    public Collection<Operand> getOperandsByIds( Collection<Integer> dataElementIdentifiers )
    {
        Collection<DataElement> dataElements = dataElementService.getDataElements( dataElementIdentifiers );

        return getOperands( dataElements );
    }

    public Collection<Operand> getOperands( Collection<DataElement> dataElements )
    {
        Collection<Operand> operands = new ArrayList<Operand>();

        for ( DataElement dataElement : dataElements )
        {
            Set<DataElementCategoryOptionCombo> categoryOptionCombos = dataElement.getCategoryCombo().getOptionCombos();

            if ( categoryOptionCombos.size() > 1 && !(dataElement instanceof CalculatedDataElement) )
            {
                for ( DataElementCategoryOptionCombo optionCombo : categoryOptionCombos )
                {
                    Operand operand = new Operand( dataElement.getId(), optionCombo.getId(), dataElement.getName()
                        + optionCombo.getName(), new ArrayList<Integer>( dataElement.getAggregationLevels() ) );

                    operands.add( operand );
                }
            }
            else
            {
                Operand operand = new Operand( dataElement.getId(), categoryOptionCombos.iterator().next().getId(),
                    dataElement.getName(), new ArrayList<Integer>( dataElement.getAggregationLevels() ) );

                operands.add( operand );
            }
        }

        return operands;
    }
}
