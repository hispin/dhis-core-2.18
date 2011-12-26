package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataelement.comparator.DataElementCategoryComboSizeComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kristian Nordal
 * @version $Id: DefaultDataElementService.java 5243 2008-05-25 10:18:58Z
 *          larshelg $
 */
@Transactional
public class DefaultDataElementService
    implements DataElementService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementStore dataElementStore;

    public void setDataElementStore( DataElementStore dataElementStore )
    {
        this.dataElementStore = dataElementStore;
    }

    private GenericIdentifiableObjectStore<DataElementGroup> dataElementGroupStore;

    public void setDataElementGroupStore( GenericIdentifiableObjectStore<DataElementGroup> dataElementGroupStore )
    {
        this.dataElementGroupStore = dataElementGroupStore;
    }

    private GenericIdentifiableObjectStore<DataElementGroupSet> dataElementGroupSetStore;

    public void setDataElementGroupSetStore( GenericIdentifiableObjectStore<DataElementGroupSet> dataElementGroupSetStore )
    {
        this.dataElementGroupSetStore = dataElementGroupSetStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // DataElement
    // -------------------------------------------------------------------------

    public int addDataElement( DataElement dataElement )
    {
        int id = dataElementStore.save( dataElement );

        i18nService.addObject( dataElement );

        return id;
    }

    public void updateDataElement( DataElement dataElement )
    {
        dataElementStore.update( dataElement );

        i18nService.verify( dataElement );
    }

    public void deleteDataElement( DataElement dataElement )
        throws HierarchyViolationException
    {
        i18nService.removeObject( dataElement );

        dataElementStore.delete( dataElement );
    }

    public DataElement getDataElement( int id )
    {
        return i18n( i18nService, dataElementStore.get( id ) );
    }

    public DataElement getDataElement( String uid )
    {
        return i18n( i18nService, dataElementStore.getByUid( uid ) );
    }

    public DataElement getDataElementByCode( String code )
    {
        return i18n( i18nService, dataElementStore.getByCode( code ) );
    }

    public Collection<DataElement> getAllDataElements()
    {
        return i18n( i18nService, dataElementStore.getAll() );
    }

    public Collection<DataElement> getDataElements( final Collection<Integer> identifiers )
    {
        Collection<DataElement> dataElements = getAllDataElements();

        return identifiers == null ? dataElements : FilterUtils.filter( dataElements, new Filter<DataElement>()
        {
            public boolean retain( DataElement dataElement )
            {
                return identifiers.contains( dataElement.getId() );
            }
        } );
    }

    public void setZeroIsSignificantForDataElements( Collection<Integer> dataElementIds )
    {
        if ( dataElementIds != null )
        {
            dataElementStore.setZeroIsSignificantForDataElements( dataElementIds );
        }
    }

    public Collection<DataElement> getDataElementsByZeroIsSignificant( boolean zeroIsSignificant )
    {
        return dataElementStore.getDataElementsByZeroIsSignificant( zeroIsSignificant );
    }

    public Collection<DataElement> getDataElementsByZeroIsSignificantAndGroup( boolean zeroIsSignificant,
        DataElementGroup dataElementGroup )
    {
        Collection<DataElement> dataElements = new HashSet<DataElement>();

        for ( DataElement element : dataElementGroup.getMembers() )
        {
            if ( element.isZeroIsSignificant() )
            {
                dataElements.add( element );
            }
        }

        return dataElements;
    }

    public Collection<DataElement> getAggregateableDataElements()
    {
        return i18n( i18nService, dataElementStore.getAggregateableDataElements() );
    }

    public Collection<DataElement> getAllActiveDataElements()
    {
        return i18n( i18nService, dataElementStore.getAllActiveDataElements() );
    }

    public DataElement getDataElementByName( String name )
    {
        return i18n( i18nService, dataElementStore.getByName( name ) );
    }

    public Collection<DataElement> searchDataElementsByName( String key )
    {
        return i18n( i18nService, dataElementStore.searchDataElementsByName( key ) );
    }

    public DataElement getDataElementByAlternativeName( String alternativeName )
    {
        return i18n( i18nService, dataElementStore.getByAlternativeName( alternativeName ) );
    }

    public DataElement getDataElementByShortName( String shortName )
    {
        return i18n( i18nService, dataElementStore.getByShortName( shortName ) );
    }

    public Collection<DataElement> getDataElementsByAggregationOperator( String aggregationOperator )
    {
        return i18n( i18nService, dataElementStore.getDataElementsByAggregationOperator( aggregationOperator ) );
    }

    public Collection<DataElement> getDataElementsByType( String type )
    {
        return i18n( i18nService, dataElementStore.getDataElementsByType( type ) );
    }

    public Collection<DataElement> getDataElementsByPeriodType( final PeriodType periodType )
    {
        Collection<DataElement> dataElements = getAllDataElements();

        return FilterUtils.filter( dataElements, new Filter<DataElement>()
        {
            public boolean retain( DataElement dataElement )
            {
                return dataElement.getPeriodType() != null && dataElement.getPeriodType().equals( periodType );
            }
        } );
    }

    public Collection<DataElement> getDataElementsByDomainType( String domainType )
    {
        return i18n( i18nService, dataElementStore.getDataElementsByDomainType( domainType ) );
    }

    public Collection<DataElement> getDataElementByCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        return i18n( i18nService, dataElementStore.getDataElementByCategoryCombo( categoryCombo ) );
    }

    public Map<DataElementCategoryCombo, List<DataElement>> getGroupedDataElementsByCategoryCombo(
        List<DataElement> dataElements )
    {
        Map<DataElementCategoryCombo, List<DataElement>> mappedDataElements = new HashMap<DataElementCategoryCombo, List<DataElement>>();

        for ( DataElement dataElement : dataElements )
        {
            if ( mappedDataElements.containsKey( dataElement.getCategoryCombo() ) )
            {
                mappedDataElements.get( dataElement.getCategoryCombo() ).add( dataElement );
            }
            else
            {
                List<DataElement> des = new ArrayList<DataElement>();
                des.add( dataElement );

                mappedDataElements.put( dataElement.getCategoryCombo(), des );
            }
        }

        return mappedDataElements;
    }

    public List<DataElementCategoryCombo> getDataElementCategoryCombos( List<DataElement> dataElements )
    {
        Set<DataElementCategoryCombo> setCategoryCombos = new HashSet<DataElementCategoryCombo>();

        for ( DataElement dataElement : dataElements )
        {
            setCategoryCombos.add( dataElement.getCategoryCombo() );
        }

        List<DataElementCategoryCombo> listCategoryCombos = new ArrayList<DataElementCategoryCombo>( setCategoryCombos );

        Collections.sort( listCategoryCombos, new DataElementCategoryComboSizeComparator() );

        return listCategoryCombos;
    }

    public Collection<DataElement> getDataElementsWithGroupSets()
    {
        return i18n( i18nService, dataElementStore.getDataElementsWithGroupSets() );
    }

    public Collection<DataElement> getDataElementsWithoutGroups()
    {
        return i18n( i18nService, dataElementStore.getDataElementsWithoutGroups() );
    }

    public Collection<DataElement> getDataElementsWithoutDataSets()
    {
        return i18n( i18nService, dataElementStore.getDataElementsWithoutDataSets() );
    }

    public Collection<DataElement> getDataElementsWithDataSets()
    {
        return i18n( i18nService, dataElementStore.getDataElementsWithDataSets() );
    }

    public boolean dataElementExists( int id )
    {
        return dataElementStore.dataElementExists( id );
    }

    public boolean dataElementCategoryOptionComboExists( int id )
    {
        return dataElementStore.dataElementCategoryOptionComboExists( id );
    }

    public Collection<DataElement> getDataElementsLikeName( String name )
    {
        return i18n( i18nService, dataElementStore.getLikeName( name ) );
    }

    public Collection<DataElement> getDataElementsBetween( int first, int max )
    {
        return i18n( i18nService, dataElementStore.getBetween( first, max ) );
    }

    public Collection<DataElement> getDataElementsBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, dataElementStore.getBetweenByName( name, first, max ) );
    }

    public int getDataElementCount()
    {
        return dataElementStore.getCount();
    }

    public int getDataElementCountByName( String name )
    {
        return dataElementStore.getCountByName( name );
    }

    public Collection<DataElement> getDataElementsByDataSets( Collection<DataSet> dataSets )
    {
        return i18n( i18nService, dataElementStore.getDataElementsByDataSets( dataSets ) );
    }

    public Map<Integer, Set<Integer>> getDataElementCategoryOptionCombos()
    {
        return dataElementStore.getDataElementCategoryOptionCombos();
    }
    
    // -------------------------------------------------------------------------
    // DataElementGroup
    // -------------------------------------------------------------------------

    public int addDataElementGroup( DataElementGroup dataElementGroup )
    {
        int id = dataElementGroupStore.save( dataElementGroup );

        i18nService.addObject( dataElementGroup );

        return id;
    }

    public void updateDataElementGroup( DataElementGroup dataElementGroup )
    {
        dataElementGroupStore.update( dataElementGroup );

        i18nService.verify( dataElementGroup );
    }

    public void deleteDataElementGroup( DataElementGroup dataElementGroup )
    {
        i18nService.removeObject( dataElementGroup );

        dataElementGroupStore.delete( dataElementGroup );
    }

    public DataElementGroup getDataElementGroup( int id )
    {
        return i18n( i18nService, dataElementGroupStore.get( id ) );
    }

    public Collection<DataElementGroup> getDataElementGroups( final Collection<Integer> identifiers )
    {
        Collection<DataElementGroup> groups = getAllDataElementGroups();

        return identifiers == null ? groups : FilterUtils.filter( groups, new Filter<DataElementGroup>()
        {
            public boolean retain( DataElementGroup object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public DataElementGroup getDataElementGroup( String uid )
    {
        return i18n( i18nService, dataElementGroupStore.getByUid( uid ) );
    }

    public Collection<DataElementGroup> getAllDataElementGroups()
    {
        return i18n( i18nService, dataElementGroupStore.getAll() );
    }

    public DataElementGroup getDataElementGroupByName( String name )
    {
        return i18n( i18nService, dataElementGroupStore.getByName( name ) );
    }

    public Collection<DataElementGroup> getGroupsContainingDataElement( DataElement dataElement )
    {
        Collection<DataElementGroup> groups = getAllDataElementGroups();

        Iterator<DataElementGroup> iterator = groups.iterator();

        while ( iterator.hasNext() )
        {
            if ( !iterator.next().getMembers().contains( dataElement ) )
            {
                iterator.remove();
            }
        }

        return groups;
    }

    public Collection<DataElement> getDataElementsByGroupId( int groupId )
    {
        return i18n( i18nService, dataElementGroupStore.get( groupId ).getMembers() );
    }

    public Collection<DataElementGroup> getDataElementGroupsBetween( int first, int max )
    {
        return dataElementGroupStore.getBetween( first, max );
    }

    public Collection<DataElementGroup> getDataElementGroupsBetweenByName( String name, int first, int max )
    {
        return dataElementGroupStore.getBetweenByName( name, first, max );
    }

    public int getDataElementGroupCount()
    {
        return dataElementGroupStore.getCount();
    }

    public int getDataElementGroupCountByName( String name )
    {
        return dataElementGroupStore.getCountByName( name );
    }

    // -------------------------------------------------------------------------
    // DataElementGroupSet
    // -------------------------------------------------------------------------

    public int addDataElementGroupSet( DataElementGroupSet groupSet )
    {
        int id = dataElementGroupSetStore.save( groupSet );

        i18nService.addObject( groupSet );

        return id;
    }

    public void updateDataElementGroupSet( DataElementGroupSet groupSet )
    {
        dataElementGroupSetStore.update( groupSet );

        i18nService.verify( groupSet );
    }

    public void deleteDataElementGroupSet( DataElementGroupSet groupSet )
    {
        i18nService.removeObject( groupSet );

        dataElementGroupSetStore.delete( groupSet );
    }

    public DataElementGroupSet getDataElementGroupSet( int id )
    {
        return i18n( i18nService, dataElementGroupSetStore.get( id ) );
    }

    public DataElementGroupSet getDataElementGroupSet( String uid )
    {
        return i18n( i18nService, dataElementGroupSetStore.getByUid( uid ) );
    }

    public DataElementGroupSet getDataElementGroupSetByName( String name )
    {
        return i18n( i18nService, dataElementGroupSetStore.getByName( name ) );
    }

    @Override
    public Collection<DataElementGroupSet> getCompulsoryDataElementGroupSets()
    {
        Collection<DataElementGroupSet> groupSets = new ArrayList<DataElementGroupSet>();

        for ( DataElementGroupSet groupSet : getAllDataElementGroupSets() )
        {
            if ( groupSet.isCompulsory() )
            {
                groupSets.add( groupSet );
            }
        }

        return groupSets;
    }

    @Override
    public Collection<DataElementGroupSet> getCompulsoryDataElementGroupSetsWithMembers()
    {
        return FilterUtils.filter( getAllDataElementGroupSets(), new Filter<DataElementGroupSet>()
        {
            public boolean retain( DataElementGroupSet object )
            {
                return object.isCompulsory() && object.hasDataElementGroups();
            }
        } );
    }

    @Override
    public Collection<DataElementGroupSet> getCompulsoryDataElementGroupSetsNotAssignedTo( DataElement dataElement )
    {
        Collection<DataElementGroupSet> groupSets = new ArrayList<DataElementGroupSet>();

        for ( DataElementGroupSet groupSet : getCompulsoryDataElementGroupSets() )
        {
            if ( !groupSet.isMemberOfDataElementGroups( dataElement ) && groupSet.hasDataElementGroups() )
            {
                groupSets.add( groupSet );
            }
        }

        return groupSets;
    }

    public Collection<DataElementGroupSet> getAllDataElementGroupSets()
    {
        return i18n( i18nService, dataElementGroupSetStore.getAll() );
    }

    public Collection<DataElementGroupSet> getDataElementGroupSets( final Collection<Integer> identifiers )
    {
        Collection<DataElementGroupSet> groupSets = getAllDataElementGroupSets();

        return identifiers == null ? groupSets : FilterUtils.filter( groupSets, new Filter<DataElementGroupSet>()
        {
            public boolean retain( DataElementGroupSet object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public int getDataElementGroupSetCount()
    {
        return dataElementGroupSetStore.getCount();
    }

    public int getDataElementGroupSetCountByName( String name )
    {
        return dataElementGroupSetStore.getCountByName( name );
    }

    public Collection<DataElementGroupSet> getDataElementGroupSetsBetween( int first, int max )
    {
        return dataElementGroupSetStore.getBetween( first, max );
    }

    public Collection<DataElementGroupSet> getDataElementGroupSetsBetweenByName( String name, int first, int max )
    {
        return dataElementGroupSetStore.getBetweenByName( name, first, max );
    }

    // -------------------------------------------------------------------------
    // DataElementOperand
    // -------------------------------------------------------------------------

    public Collection<DataElementOperand> getAllGeneratedOperands()
    {
        return dataElementStore.getAllGeneratedOperands();
    }

    public Collection<DataElementOperand> getAllGeneratedOperands( Collection<DataElement> dataElements )
    {
        return dataElementStore.getAllGeneratedOperands( dataElements );
    }

}
