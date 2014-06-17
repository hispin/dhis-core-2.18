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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 */

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hisp.dhis.common.IdentifiableObject;
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
public class DefaultFieldFilterService implements FieldFilterService
{
    static final ImmutableMap<String, List<String>> FIELD_PRESETS = ImmutableMap.<String, List<String>>builder()
        .put( "all", Lists.newArrayList( "*" ) )
        .put( "identifiable", Lists.newArrayList( "id", "name", "code", "created", "lastUpdated", "href" ) )
        .put( "nameable", Lists.newArrayList( "id", "name", "shortName", "description", "code", "created", "lastUpdated", "href" ) )
        .build();

    @Autowired
    private ParserService parserService;

    @Autowired
    private SchemaService schemaService;

    @Override
    public <T extends IdentifiableObject> CollectionNode filter( Class<?> klass, List<T> objects, List<String> fieldList )
    {
        if ( objects == null )
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

        CollectionNode collectionNode = new CollectionNode( rootSchema.getCollectionName() );
        collectionNode.setNamespace( rootSchema.getNamespace() );

        for ( Object object : objects )
        {
            collectionNode.addChild( buildComplexNode( fieldMap, klass, object ) );
        }

        return collectionNode;
    }

    @SuppressWarnings( "unchecked" )
    private ComplexNode buildComplexNode( Map<String, Map> fieldMap, Class<?> klass, Object object )
    {
        Schema schema = schemaService.getDynamicSchema( klass );

        ComplexNode complexNode = new ComplexNode( schema.getName() );
        complexNode.setNamespace( schema.getNamespace() );

        if ( object == null )
        {
            return complexNode;
        }

        updateFields( fieldMap, schema.getKlass() );

        for ( String fieldKey : fieldMap.keySet() )
        {
            if ( !schema.getPropertyMap().containsKey( fieldKey ) )
            {
                continue;
            }

            Property property = schema.getPropertyMap().get( fieldKey );

            Object returnValue = ReflectionUtils.invokeMethod( object, property.getGetterMethod() );
            Schema propertySchema = schemaService.getDynamicSchema( property.getKlass() );

            Map fieldValue = fieldMap.get( fieldKey );

            if ( property.isCollection() )
            {
                updateFields( fieldValue, property.getItemKlass() );
            }
            else
            {
                updateFields( fieldValue, property.getKlass() );
            }

            if ( fieldValue.isEmpty() )
            {
                List<String> fields = FIELD_PRESETS.get( "identifiable" );

                if ( property.isCollection() )
                {
                    Collection<?> collection = (Collection<?>) returnValue;

                    CollectionNode collectionNode = complexNode.addChild( new CollectionNode( property.getCollectionName() ) );
                    collectionNode.setNamespace( property.getNamespace() );

                    if ( property.isIdentifiableObject() )
                    {
                        for ( Object collectionObject : collection )
                        {
                            collectionNode.addChild( getProperties( property, collectionObject, fields ) );
                        }
                    }
                    else if ( !property.isSimple() )
                    {
                        Map<String, Map> map = getFullFieldMap( schemaService.getDynamicSchema( property.getItemKlass() ) );

                        for ( Object collectionObject : collection )
                        {
                            ComplexNode node = buildComplexNode( map, property.getItemKlass(), collectionObject );

                            if ( !node.getChildren().isEmpty() )
                            {
                                collectionNode.addChild( node );
                            }
                        }
                    }
                    else
                    {
                        for ( Object collectionObject : collection )
                        {
                            collectionNode.addChild( new SimpleNode( property.getName(), collectionObject ) );
                        }
                    }
                }
                else if ( property.isIdentifiableObject() )
                {
                    complexNode.addChild( getProperties( property, returnValue, fields ) );
                }
                else
                {
                    if ( propertySchema.getProperties().isEmpty() )
                    {
                        SimpleNode simpleNode = new SimpleNode( fieldKey, returnValue );
                        simpleNode.setAttribute( property.isAttribute() );
                        simpleNode.setNamespace( property.getNamespace() );

                        complexNode.addChild( simpleNode );
                    }
                    else
                    {
                        complexNode.addChild( buildComplexNode( getFullFieldMap( propertySchema ), property.getKlass(),
                            returnValue ) );
                    }
                }
            }
            else
            {
                if ( property.isCollection() )
                {
                    CollectionNode collectionNode = complexNode.addChild( new CollectionNode( property.getCollectionName() ) );
                    collectionNode.setNamespace( property.getNamespace() );

                    for ( Object collectionObject : (Collection<?>) returnValue )
                    {
                        ComplexNode node = buildComplexNode( fieldValue, property.getItemKlass(), collectionObject );

                        if ( !node.getChildren().isEmpty() )
                        {
                            collectionNode.addChild( node );
                        }
                    }
                }
                else
                {
                    ComplexNode node = buildComplexNode( fieldValue, property.getKlass(), returnValue );

                    if ( !node.getChildren().isEmpty() )
                    {
                        complexNode.addChild( node );
                    }
                }
            }
        }

        return complexNode;
    }

    private void updateFields( Map<String, Map> fieldMap, Class<?> klass )
    {
        // we need two run this (at least) two times, since some of the presets might contain other presets
        _updateFields( fieldMap, klass, true );
        _updateFields( fieldMap, klass, false );
    }

    private void _updateFields( Map<String, Map> fieldMap, Class<?> klass, boolean expandOnly )
    {
        Schema schema = schemaService.getDynamicSchema( klass );
        List<String> cleanupFields = Lists.newArrayList();

        for ( String fieldKey : Sets.newHashSet( fieldMap.keySet() ) )
        {
            if ( "*".equals( fieldKey ) )
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
                List<String> fields = FIELD_PRESETS.get( fieldKey.substring( 1 ) );

                if ( fields == null )
                {
                    continue;
                }

                for ( String field : fields )
                {
                    if ( !fieldMap.containsKey( field ) )
                    {
                        fieldMap.put( field, Maps.newHashMap() );
                    }
                }

                cleanupFields.add( fieldKey );
            }
            else if ( fieldKey.startsWith( "!" ) && !expandOnly )
            {
                cleanupFields.add( fieldKey );
            }
        }

        for ( String field : cleanupFields )
        {
            fieldMap.remove( field );

            if ( !expandOnly )
            {
                fieldMap.remove( field.substring( 1 ) );
            }
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

    private ComplexNode getProperties( Property currentProperty, Object object, List<String> fields )
    {
        if ( object == null )
        {
            return null;
        }

        ComplexNode complexNode = new ComplexNode( currentProperty.getName() );
        complexNode.setNamespace( currentProperty.getNamespace() );

        Schema schema;

        if ( currentProperty.isCollection() )
        {
            schema = schemaService.getDynamicSchema( currentProperty.getItemKlass() );

        }
        else
        {
            schema = schemaService.getDynamicSchema( currentProperty.getKlass() );
        }

        for ( String field : fields )
        {
            Property property = schema.getPropertyMap().get( field );

            if ( property == null )
            {
                continue;
            }

            Object returnValue = ReflectionUtils.invokeMethod( object, property.getGetterMethod() );

            SimpleNode simpleNode = new SimpleNode( field, returnValue );
            simpleNode.setAttribute( property.isAttribute() );
            simpleNode.setNamespace( property.getNamespace() );

            complexNode.addChild( simpleNode );
        }

        return complexNode;
    }
}
