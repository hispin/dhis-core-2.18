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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
    private static final List<String> IDENTIFIABLE_PROPERTIES =
        Lists.newArrayList( "id", "name", "code", "created", "lastUpdated" );

    @Autowired
    private ParserService parserService;

    @Autowired
    private SchemaService schemaService;

    @Override
    public <T extends IdentifiableObject> List<T> filterObjects( List<T> objects, List<String> filters )
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
    public <T extends IdentifiableObject> CollectionNode filterProperties( Class<?> klass, List<T> objects,
        String include, String exclude )
    {
        Schema rootSchema = schemaService.getDynamicSchema( klass );
        CollectionNode collectionNode = new CollectionNode( rootSchema.getPlural() ); // replace with 'xml' collection name

        if ( objects.isEmpty() )
        {
            return collectionNode;
        }

        Map<String, Map> fieldMap = Maps.newHashMap();
        Schema schema = schemaService.getDynamicSchema( objects.get( 0 ).getClass() );

        if ( include == null && exclude == null )
        {
            for ( Property property : schema.getProperties() )
            {
                fieldMap.put( property.getName(), Maps.newHashMap() );
            }
        }
        else if ( include != null )
        {
            fieldMap = parserService.parsePropertyFilter( include );
        }
        else
        {
            Map<String, Map> excludeMap = parserService.parsePropertyFilter( exclude );

            for ( Property property : schema.getProperties() )
            {
                if ( !excludeMap.containsKey( property.getName() ) )
                {
                    fieldMap.put( property.getName(), Maps.newHashMap() );
                }
            }
        }

        for ( Object object : objects )
        {
            collectionNode.addNode( buildObjectOutput( fieldMap, object ) );
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
        ComplexNode complexNode = new ComplexNode( schema.getSingular() );

        for ( String fieldKey : fieldMap.keySet() )
        {
            if ( !schema.getPropertyMap().containsKey( fieldKey ) )
            {
                continue;
            }

            Property property = schema.getPropertyMap().get( fieldKey );
            Object returnValue = ReflectionUtils.invokeMethod( object, property.getGetterMethod() );

            if ( returnValue == null )
            {
                continue;
            }

            Map fieldValue = fieldMap.get( fieldKey );

            if ( fieldValue.isEmpty() )
            {
                if ( !property.isIdentifiableObject() )
                {
                    complexNode.addNode( new SimpleNode( fieldKey, returnValue ) );
                }
                else if ( !property.isCollection() )
                {
                    complexNode.addNode( getIdentifiableObjectProperties( returnValue, IDENTIFIABLE_PROPERTIES ) );
                }
                else
                {
                    complexNode.addNode( getIdentifiableObjectCollectionProperties( returnValue, IDENTIFIABLE_PROPERTIES, fieldKey ) );
                }
            }
            else
            {
                if ( property.isCollection() )
                {
                    CollectionNode collectionNode = complexNode.addNode( new CollectionNode( property.getCollectionName() ) );

                    for ( Object collectionObject : (Collection<?>) returnValue )
                    {
                        ComplexNode node = buildObjectOutput( fieldValue, collectionObject );

                        if ( !node.getNodes().isEmpty() )
                        {
                            collectionNode.addNode( node );
                        }
                    }
                }
                else
                {
                    ComplexNode node = buildObjectOutput( fieldValue, returnValue );

                    if ( !node.getNodes().isEmpty() )
                    {
                        complexNode.addNode( node );
                    }
                }
            }
        }

        return complexNode;
    }

    @SuppressWarnings( "unchecked" )
    private CollectionNode getIdentifiableObjectCollectionProperties( Object object, List<String> fields, String collectionName )
    {
        if ( object == null )
        {
            return null;
        }

        if ( !Collection.class.isInstance( object ) )
        {
            return null;
        }

        CollectionNode collectionNode = new CollectionNode( collectionName );
        Collection<IdentifiableObject> identifiableObjects;

        try
        {
            identifiableObjects = (Collection<IdentifiableObject>) object;
        }
        catch ( ClassCastException ex )
        {
            ex.printStackTrace();
            return collectionNode;
        }

        for ( IdentifiableObject identifiableObject : identifiableObjects )
        {
            collectionNode.addNode( getIdentifiableObjectProperties( identifiableObject, fields ) );
        }

        return collectionNode;
    }

    private ComplexNode getIdentifiableObjectProperties( Object object, List<String> fields )
    {
        if ( object == null )
        {
            return null;
        }

        if ( !IdentifiableObject.class.isInstance( object ) )
        {
            return null;
        }

        Schema schema = schemaService.getDynamicSchema( object.getClass() );

        ComplexNode complexNode = new ComplexNode( schema.getSingular() );

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
                complexNode.addNode( new SimpleNode( field, o ) );
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
