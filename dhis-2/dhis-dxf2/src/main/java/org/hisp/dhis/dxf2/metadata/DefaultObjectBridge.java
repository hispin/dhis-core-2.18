package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2012, University of Oslo
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mapping.MapLayer;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
@Transactional( readOnly = true )
public class DefaultObjectBridge
    implements ObjectBridge
{
    private static final Log log = LogFactory.getLog( DefaultObjectBridge.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private PeriodStore periodStore;

    //-------------------------------------------------------------------------------------------------------
    // Internal and Semi-Public maps
    //-------------------------------------------------------------------------------------------------------

    private Map<Class<?>, Collection<?>> masterMap;

    private Map<String, PeriodType> periodTypeMap;

    private Map<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>> uidMap;

    private Map<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>> codeMap;

    private Map<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>> nameMap;

    private Map<Class<? extends NameableObject>, Map<String, NameableObject>> shortNameMap;

    private static final List<Class<?>> registeredTypes = new ArrayList<Class<?>>();

    private boolean writeEnabled = true;

    //-------------------------------------------------------------------------------------------------------
    // Build maps
    //-------------------------------------------------------------------------------------------------------

    static
    {
        registeredTypes.add( PeriodType.class );
        registeredTypes.add( Document.class );
        registeredTypes.add( Constant.class );
        registeredTypes.add( Attribute.class );
        registeredTypes.add( Concept.class );
        registeredTypes.add( SqlView.class );
        registeredTypes.add( Chart.class );
        registeredTypes.add( Report.class );
        registeredTypes.add( ReportTable.class );
        registeredTypes.add( DataDictionary.class );

        // registeredTypes.add( User.class );
        // registeredTypes.add( UserGroup.class );
        // registeredTypes.add( UserAuthorityGroup.class );

        registeredTypes.add( OrganisationUnitLevel.class );
        registeredTypes.add( OrganisationUnit.class );
        registeredTypes.add( OrganisationUnitGroup.class );
        registeredTypes.add( OrganisationUnitGroupSet.class );

        registeredTypes.add( Indicator.class );
        registeredTypes.add( IndicatorType.class );
        registeredTypes.add( IndicatorGroup.class );
        registeredTypes.add( IndicatorGroupSet.class );

        registeredTypes.add( DataElement.class );
        registeredTypes.add( OptionSet.class );
        registeredTypes.add( DataElementGroup.class );
        registeredTypes.add( DataElementGroupSet.class );
        registeredTypes.add( DataElementCategory.class );
        registeredTypes.add( DataElementCategoryOption.class );
        registeredTypes.add( DataElementCategoryCombo.class );
        registeredTypes.add( DataElementCategoryOptionCombo.class );

        registeredTypes.add( ValidationRule.class );
        registeredTypes.add( ValidationRuleGroup.class );

        registeredTypes.add( DataSet.class );
        registeredTypes.add( Section.class );

        registeredTypes.add( MapView.class );
        registeredTypes.add( MapLayer.class );
        registeredTypes.add( MapLegend.class );
        registeredTypes.add( MapLegendSet.class );
    }

    @Override
    public void init()
    {
        log.info( "Started updating lookup maps at " + new Date() );

        masterMap = new HashMap<Class<?>, Collection<?>>();
        periodTypeMap = new HashMap<String, PeriodType>();
        uidMap = new HashMap<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>>();
        codeMap = new HashMap<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>>();
        nameMap = new HashMap<Class<? extends IdentifiableObject>, Map<String, IdentifiableObject>>();
        shortNameMap = new HashMap<Class<? extends NameableObject>, Map<String, NameableObject>>();

        for ( Class<?> type : registeredTypes )
        {
            populatePeriodTypeMap( type );
            populateIdentifiableObjectMap( type );
            populateIdentifiableObjectMap( type, IdentifiableObject.IdentifiableProperty.UID );
            populateIdentifiableObjectMap( type, IdentifiableObject.IdentifiableProperty.CODE );
            populateIdentifiableObjectMap( type, IdentifiableObject.IdentifiableProperty.NAME );
            populateNameableObjectMap( type, NameableObject.NameableProperty.SHORT_NAME );
        }

        log.info( "Finished updating lookup maps at " + new Date() );
    }

    @Override
    public void destroy()
    {
        masterMap = null;

        uidMap = null;
        codeMap = null;
        nameMap = null;
        shortNameMap = null;

        periodTypeMap = null;
    }

    //-------------------------------------------------------------------------------------------------------
    // Populate Helpers
    //-------------------------------------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private void populateIdentifiableObjectMap( Class<?> clazz )
    {
        Collection<IdentifiableObject> map = new ArrayList<IdentifiableObject>();

        if ( IdentifiableObject.class.isAssignableFrom( clazz ) )
        {
            map = manager.getAll( (Class<IdentifiableObject>) clazz );
        }

        if ( map != null )
        {
            masterMap.put( clazz, map );
        }
    }

    @SuppressWarnings( "unchecked" )
    private void populateIdentifiableObjectMap( Class<?> clazz, IdentifiableObject.IdentifiableProperty property )
    {
        Map<String, IdentifiableObject> map = new HashMap<String, IdentifiableObject>();

        if ( IdentifiableObject.class.isAssignableFrom( clazz ) )
        {
            map = (Map<String, IdentifiableObject>) manager.getIdMap( (Class<? extends IdentifiableObject>) clazz, property );
        }

        if ( map != null )
        {
            if ( property == IdentifiableObject.IdentifiableProperty.UID )
            {
                uidMap.put( (Class<? extends IdentifiableObject>) clazz, map );
            }
            else if ( property == IdentifiableObject.IdentifiableProperty.CODE )
            {
                codeMap.put( (Class<? extends IdentifiableObject>) clazz, map );
            }
            else if ( property == IdentifiableObject.IdentifiableProperty.NAME )
            {
                nameMap.put( (Class<? extends IdentifiableObject>) clazz, map );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private void populateNameableObjectMap( Class<?> clazz, NameableObject.NameableProperty property )
    {
        Map<String, NameableObject> map = null;

        if ( NameableObject.class.isAssignableFrom( clazz ) )
        {
            map = (Map<String, NameableObject>) manager.getIdMap( (Class<? extends NameableObject>) clazz, property );
        }

        if ( map != null )
        {
            if ( property == NameableObject.NameableProperty.SHORT_NAME )
            {
                shortNameMap.put( (Class<? extends NameableObject>) clazz, map );
            }
        }
    }

    private void populatePeriodTypeMap( Class<?> clazz )
    {
        Collection<Object> periodTypes = new ArrayList<Object>();

        if ( PeriodType.class.isAssignableFrom( clazz ) )
        {
            for ( PeriodType periodType : periodStore.getAllPeriodTypes() )
            {
                periodTypes.add( periodType );
                periodTypeMap.put( periodType.getName(), periodType );
            }
        }

        masterMap.put( clazz, periodTypes );
    }

    //-------------------------------------------------------------------------------------------------------
    // ObjectBridge Implementation
    //-------------------------------------------------------------------------------------------------------

    @Override
    @Transactional( readOnly = false )
    public void saveObject( Object object )
    {
        if ( _typeSupported( object.getClass() ) && IdentifiableObject.class.isInstance( object ) )
        {
            if ( writeEnabled )
            {
                manager.save( (IdentifiableObject) object );
            }
        }
        else
        {
            log.warn( "Trying to save unsupported type + " + object.getClass() + " with object " + object + " object discarded." );
        }
    }

    @Override
    @Transactional( readOnly = false )
    public void updateObject( Object object )
    {
        if ( _typeSupported( object.getClass() ) && IdentifiableObject.class.isInstance( object ) )
        {
            if ( writeEnabled )
            {
                manager.update( (IdentifiableObject) object );
            }

            _updateInternalMaps( object );
        }
        else
        {
            log.warn( "Trying to update unsupported type + " + object.getClass() + " with object " + object + " object discarded." );
        }
    }

    @Override
    public <T> T getObject( T object )
    {
        Collection<T> objects = _findMatches( object );

        if ( objects.size() == 1 )
        {
            return objects.iterator().next();
        }
        else if ( objects.size() > 1 )
        {
            log.debug( "Multiple objects found for " + object + ", object discarded, returning null." );
        }
        else
        {
            log.debug( "No object found for " + object + ", returning null." );
        }

        return null;
    }

    @Override
    public <T> Collection<T> getObjects( T object )
    {
        return _findMatches( object );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> Collection<T> getAllObjects( Class<T> clazz )
    {
        return (Collection<T>) masterMap.get( clazz );
    }

    public Map<Class<?>, Collection<?>> getMasterMap()
    {
        return masterMap;
    }

    public void setMasterMap( Map<Class<?>, Collection<?>> masterMap )
    {
        this.masterMap = masterMap;
    }

    @Override
    public void setWriteEnabled( boolean enabled )
    {
        this.writeEnabled = enabled;
    }

    @Override
    public boolean isWriteEnabled()
    {
        return writeEnabled;
    }

    //-------------------------------------------------------------------------------------------------------
    // Internal Methods
    //-------------------------------------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private <T> Collection<T> _findMatches( T object )
    {
        Collection<T> objects = new HashSet<T>();

        if ( PeriodType.class.isInstance( object ) )
        {
            PeriodType periodType = (PeriodType) object;
            periodType = periodTypeMap.get( periodType.getName() );

            if ( periodType != null )
            {
                objects.add( (T) periodType );
            }
        }

        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;

            if ( identifiableObject.getUid() != null )
            {
                IdentifiableObject match = getUidMatch( identifiableObject );

                if ( match != null )
                {
                    objects.add( (T) match );
                }
            }

            if ( identifiableObject.getCode() != null )
            {
                IdentifiableObject match = getCodeMatch( identifiableObject );

                if ( match != null )
                {
                    objects.add( (T) match );
                }
            }

            if ( identifiableObject.getName() != null )
            {
                IdentifiableObject match = getNameMatch( identifiableObject );

                if ( match != null )
                {
                    objects.add( (T) match );
                }
            }
        }

        if ( NameableObject.class.isInstance( object ) )
        {
            NameableObject nameableObject = (NameableObject) object;

            if ( nameableObject.getShortName() != null )
            {
                IdentifiableObject match = getShortNameMatch( nameableObject );

                if ( match != null )
                {
                    objects.add( (T) match );
                }
            }
        }

        return objects;
    }

    private <T> void _updateInternalMaps( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;

            if ( identifiableObject.getUid() != null )
            {
                Map<String, IdentifiableObject> map = uidMap.get( identifiableObject.getClass() );

                if ( map == null )
                {
                    // might be dynamically sub-classed by javassist or cglib, fetch superclass and try again
                    map = uidMap.get( identifiableObject.getClass().getSuperclass() );
                }

                map.put( identifiableObject.getUid(), identifiableObject );
            }

            if ( identifiableObject.getCode() != null )
            {
                Map<String, IdentifiableObject> map = codeMap.get( identifiableObject.getClass() );

                if ( map == null )
                {
                    // might be dynamically sub-classed by javassist or cglib, fetch superclass and try again
                    map = uidMap.get( identifiableObject.getClass().getSuperclass() );
                }

                map.put( identifiableObject.getCode(), identifiableObject );
            }

            if ( identifiableObject.getName() != null )
            {
                Map<String, IdentifiableObject> map = uidMap.get( identifiableObject.getClass() );

                if ( map == null )
                {
                    // might be dynamically sub-classed by javassist or cglib, fetch superclass and try again
                    map = uidMap.get( identifiableObject.getClass().getSuperclass() );
                }

                map.put( identifiableObject.getName(), identifiableObject );
            }
        }

        if ( NameableObject.class.isInstance( object ) )
        {
            NameableObject nameableObject = (NameableObject) object;

            if ( nameableObject.getShortName() != null )
            {
                Map<String, NameableObject> map = shortNameMap.get( nameableObject.getClass() );

                if ( map == null )
                {
                    // might be dynamically sub-classed by javassist or cglib, fetch superclass and try again
                    map = shortNameMap.get( nameableObject.getClass().getSuperclass() );
                }

                map.put( nameableObject.getShortName(), nameableObject );
            }
        }
    }

    private IdentifiableObject getUidMatch( IdentifiableObject identifiableObject )
    {
        Map<String, IdentifiableObject> map = uidMap.get( identifiableObject.getClass() );

        if ( map != null )
        {
            return map.get( identifiableObject.getUid() );
        }

        return null;
    }

    private IdentifiableObject getCodeMatch( IdentifiableObject identifiableObject )
    {
        Map<String, IdentifiableObject> map = codeMap.get( identifiableObject.getClass() );

        if ( map != null )
        {
            return map.get( identifiableObject.getCode() );
        }

        return null;
    }

    private IdentifiableObject getNameMatch( IdentifiableObject identifiableObject )
    {
        Map<String, IdentifiableObject> map = nameMap.get( identifiableObject.getClass() );

        if ( map != null )
        {
            return map.get( identifiableObject.getName() );
        }

        return null;
    }

    private NameableObject getShortNameMatch( NameableObject nameableObject )
    {
        Map<String, NameableObject> map = shortNameMap.get( nameableObject.getClass() );

        if ( map != null )
        {
            return map.get( nameableObject.getShortName() );
        }

        return null;
    }

    private boolean _typeSupported( Class<?> clazz )
    {
        for ( Class c : registeredTypes )
        {
            if ( c.isAssignableFrom( clazz ) )
            {
                return true;
            }
        }

        return false;
    }
}
