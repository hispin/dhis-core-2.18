package org.hisp.dhis.dxf2.filter;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.filter.ops.Op;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultFilterService implements FilterService
{
    @Autowired
    private ParserService parserService;

    @Autowired
    private SchemaService schemaService;

    @Override
    public <T extends IdentifiableObject> List<T> objectFilter( List<T> objects, List<String> filters )
    {
        if ( objects == null || objects.isEmpty() )
        {
            return Lists.newArrayList();
        }

        Filters parsed = parserService.parseObjectFilter( filters );

        List<T> list = Lists.newArrayList();

        for ( T object : objects )
        {
            if ( evaluateWithFilters( object, parsed ) )
            {
                list.add( object );
            }
        }

        return list;
    }

    @Override
    public <T extends IdentifiableObject> CollectionNode fieldFilter( Class<?> klass, List<T> objects,
        List<String> fieldList )
    {
        if ( objects.isEmpty() )
        {
            return null;
        }

        String fields = fieldList == null ? "" : Joiner.on( "," ).join( fieldList );

        Schema rootSchema = schemaService.getDynamicSchema( klass );

        Map<String, Map> fieldMap = Maps.newHashMap();
        Schema schema = schemaService.getDynamicSchema( objects.get( 0 ).getClass() );

        if ( fields == null )
        {
            for ( Property property : schema.getProperties() )
            {
                fieldMap.put( property.getName(), Maps.newHashMap() );
            }
        }
        else
        {
            fieldMap = parserService.parsePropertyFilter( fields );
        }

        CollectionNode collectionNode = new CollectionNode( rootSchema.getPlural() ); // replace with 'xml' collection name
        collectionNode.setNamespace( rootSchema.getNamespaceURI() );

        if ( objects.isEmpty() )
        {
            return collectionNode;
        }

        for ( Object object : objects )
        {
            collectionNode.addChild( buildObjectOutput( fieldMap, object ) );
        }

        return collectionNode;
    }

    @SuppressWarnings( "unchecked" )
    private ComplexNode buildObjectOutput( Map<String, Map> fieldMap, Object object )
    {
        if ( object == null )
        {
            return null;
        }

        Schema schema = schemaService.getDynamicSchema( object.getClass() );
        ComplexNode complexNode = new ComplexNode( schema.getName() );
        complexNode.setNamespace( schema.getNamespaceURI() );

        updateFields( fieldMap, object );

        for ( String fieldKey : fieldMap.keySet() )
        {
            if ( !schema.getPropertyMap().containsKey( fieldKey ) )
            {
                continue;
            }

            Property property = schema.getPropertyMap().get( fieldKey );
            Object returnValue = ReflectionUtils.invokeMethod( object, property.getGetterMethod() );
            Schema propertySchema = schemaService.getDynamicSchema( property.getKlass() );

            if ( returnValue == null )
            {
                continue;
            }

            Map fieldValue = fieldMap.get( fieldKey );

            if ( fieldValue.isEmpty() )
            {
                if ( property.isCollection() )
                {
                    if ( property.isIdentifiableObject() )
                    {
                        complexNode.addChild( getCollectionProperties( returnValue, FilterService.FIELD_PRESETS.get( "identifiable" ), property ) );
                    }
                    else
                    {
                        complexNode.addChild( getCollectionProperties( returnValue, Lists.newArrayList( propertySchema.getPropertyMap().keySet() ), property ) );
                    }
                }
                else if ( property.isIdentifiableObject() )
                {
                    complexNode.addChild( getProperties( returnValue, FilterService.FIELD_PRESETS.get( "identifiable" ) ) );
                }
                else
                {
                    if ( propertySchema.getProperties().isEmpty() )
                    {
                        SimpleNode simpleNode = new SimpleNode( fieldKey, returnValue );
                        simpleNode.setAttribute( property.isAttribute() );
                        simpleNode.setNamespace( property.getNamespaceURI() );

                        complexNode.addChild( simpleNode );
                    }
                    else
                    {
                        complexNode.addChild( buildObjectOutput( getFullFieldMap( propertySchema ), returnValue ) );
                    }
                }
            }
            else
            {
                if ( property.isCollection() )
                {
                    CollectionNode collectionNode = complexNode.addChild( new CollectionNode( property.getCollectionName() ) );
                    collectionNode.setNamespace( property.getNamespaceURI() );

                    for ( Object collectionObject : (Collection<?>) returnValue )
                    {
                        ComplexNode node = buildObjectOutput( fieldValue, collectionObject );

                        if ( !node.getChildren().isEmpty() )
                        {
                            collectionNode.addChild( node );
                        }
                    }
                }
                else
                {
                    ComplexNode node = buildObjectOutput( fieldValue, returnValue );

                    if ( !node.getChildren().isEmpty() )
                    {
                        complexNode.addChild( node );
                    }
                }
            }
        }

        return complexNode;
    }

    private void updateFields( Map<String, Map> fieldMap, Object object )
    {
        // we need two run this (at least) two times, since some of the presets might contain other presets
        _updateFields( fieldMap, object );
        _updateFields( fieldMap, object );
    }

    private void _updateFields( Map<String, Map> fieldMap, Object object )
    {
        Schema schema = schemaService.getDynamicSchema( object.getClass() );

        List<String> cleanupFields = Lists.newArrayList();

        for ( String fieldKey : Sets.newHashSet( fieldMap.keySet() ) )
        {
            if ( fieldKey.equals( "*" ) )
            {
                for ( String mapKey : schema.getPropertyMap().keySet() )
                {
                    if ( !fieldMap.containsKey( mapKey ) )
                    {
                        fieldMap.put( mapKey, Maps.newHashMap() );
                    }
                }

                cleanupFields.add( fieldKey );
            }
            else if ( fieldKey.startsWith( ":" ) )
            {
                List<String> fields = FilterService.FIELD_PRESETS.get( fieldKey.substring( 1 ) );

                for ( String field : fields )
                {
                    if ( !fieldMap.containsKey( field ) )
                    {
                        fieldMap.put( field, Maps.newHashMap() );
                    }
                }

                cleanupFields.add( fieldKey );
            }
            else if ( fieldKey.startsWith( "!" ) )
            {
                cleanupFields.add( fieldKey );
            }
        }

        for ( String ignore : cleanupFields )
        {
            fieldMap.remove( ignore );
            fieldMap.remove( ignore.substring( 1 ) );
        }
    }

    private Map<String, Map> getFullFieldMap( Schema schema )
    {
        Map<String, Map> map = Maps.newHashMap();

        for ( String mapKey : schema.getPropertyMap().keySet() )
        {
            map.put( mapKey, Maps.newHashMap() );
        }

        return map;
    }

    @SuppressWarnings( "unchecked" )
    private CollectionNode getCollectionProperties( Object object, List<String> fields, Property property )
    {
        if ( object == null )
        {
            return null;
        }

        if ( !Collection.class.isInstance( object ) )
        {
            return null;
        }

        CollectionNode collectionNode = new CollectionNode( property.getCollectionName() );
        collectionNode.setNamespace( property.getNamespaceURI() );

        Collection<?> collection = (Collection<?>) object;

        for ( Object collectionObject : collection )
        {
            collectionNode.addChild( getProperties( collectionObject, fields ) );
        }

        return collectionNode;
    }

    private ComplexNode getProperties( Object object, List<String> fields )
    {
        if ( object == null )
        {
            return null;
        }

        Schema schema = schemaService.getDynamicSchema( object.getClass() );

        ComplexNode complexNode = new ComplexNode( schema.getSingular() );
        complexNode.setNamespace( schema.getNamespaceURI() );

        for ( String field : fields )
        {
            Property property = schema.getPropertyMap().get( field );

            if ( property == null )
            {
                continue;
            }

            Object o = ReflectionUtils.invokeMethod( object, property.getGetterMethod() );

            if ( o != null )
            {
                SimpleNode simpleNode = new SimpleNode( field, o );
                simpleNode.setAttribute( property.isAttribute() );
                simpleNode.setNamespace( property.getNamespaceURI() );

                complexNode.addChild( simpleNode );
            }
        }

        return complexNode;
    }

    @SuppressWarnings( "unchecked" )
    private <T> boolean evaluateWithFilters( T object, Filters filters )
    {
        Schema schema = schemaService.getDynamicSchema( object.getClass() );

        for ( String field : filters.getFilters().keySet() )
        {
            if ( !schema.getPropertyMap().containsKey( field ) )
            {
                continue;
            }

            Property descriptor = schema.getPropertyMap().get( field );

            if ( descriptor == null )
            {
                continue;
            }

            Object value = ReflectionUtils.invokeMethod( object, descriptor.getGetterMethod() );

            Object filter = filters.getFilters().get( field );

            if ( FilterOps.class.isInstance( filter ) )
            {
                if ( evaluateFilterOps( value, (FilterOps) filter ) )
                {
                    return false;
                }
            }
            else
            {
                Map<String, Object> map = (Map<String, Object>) filters.getFilters().get( field );
                Filters f = new Filters();
                f.setFilters( map );

                if ( map.containsKey( "__self__" ) )
                {
                    if ( evaluateFilterOps( value, (FilterOps) map.get( "__self__" ) ) )
                    {
                        return false;
                    }

                    map.remove( "__self__" );
                }

                if ( !descriptor.isCollection() )
                {
                    if ( !evaluateWithFilters( value, f ) )
                    {
                        return false;
                    }
                }
                else
                {
                    Collection<?> objectCollection = (Collection<?>) value;

                    if ( objectCollection.isEmpty() )
                    {
                        return false;
                    }

                    boolean include = false;

                    for ( Object idObject : objectCollection )
                    {
                        if ( evaluateWithFilters( idObject, f ) )
                        {
                            include = true;
                        }
                    }

                    if ( !include )
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean evaluateFilterOps( Object value, FilterOps filterOps )
    {
        // filter through every operator treating multiple of same operator as OR
        for ( String operator : filterOps.getFilters().keySet() )
        {
            boolean include = false;

            List<Op> ops = filterOps.getFilters().get( operator );

            for ( Op op : ops )
            {
                switch ( op.evaluate( value ) )
                {
                    case INCLUDE:
                    {
                        include = true;
                    }
                }
            }

            if ( !include )
            {
                return true;
            }
        }

        return false;
    }
}
