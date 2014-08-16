package org.hisp.dhis.schema;

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

import org.hisp.dhis.node.annotation.NodeSimple;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

class SimpleFieldsOnly
{
    @NodeSimple( isAttribute = false )
    private String simpleProperty;

    @NodeSimple( value = "renamedProperty", isAttribute = true )
    private String simplePropertyRenamed;

    @NodeSimple( isPersisted = false )
    private String notPersistedProperty;

    @NodeSimple( isReadable = true, isWritable = false )
    private String readOnly;

    @NodeSimple( isReadable = false, isWritable = true )
    private boolean writeOnly;

    @NodeSimple( namespace = "http://ns.example.org" )
    private String propertyWithNamespace;

    public void setWriteOnly( boolean writeOnly )
    {

    }

    public boolean isWriteOnly()
    {
        return writeOnly;
    }
}

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class FieldSimpleNodePropertyIntrospectorServiceTest
{
    private Map<String, Property> propertyMap;

    @Before
    public void setup()
    {
        propertyMap = new NodePropertyIntrospectorService().scanClass( SimpleFieldsOnly.class );
    }

    @Test
    public void testContainsKey() throws NoSuchFieldException
    {
        assertTrue( propertyMap.containsKey( "simpleProperty" ) );
        assertFalse( propertyMap.containsKey( "simplePropertyRenamed" ) );
        assertTrue( propertyMap.containsKey( "renamedProperty" ) );
        assertTrue( propertyMap.containsKey( "readOnly" ) );
        assertTrue( propertyMap.containsKey( "writeOnly" ) );
    }

    @Test
    public void testAttribute()
    {
        assertFalse( propertyMap.get( "simpleProperty" ).isAttribute() );
        assertTrue( propertyMap.get( "renamedProperty" ).isAttribute() );
    }

    @Test
    public void testPersisted()
    {
        assertTrue( propertyMap.get( "simpleProperty" ).isPersisted() );
        assertFalse( propertyMap.get( "notPersistedProperty" ).isPersisted() );
    }

    @Test
    public void testReadWrite()
    {
        assertTrue( propertyMap.get( "readOnly" ).isReadable() );
        assertFalse( propertyMap.get( "readOnly" ).isWritable() );

        assertFalse( propertyMap.get( "writeOnly" ).isReadable() );
        assertTrue( propertyMap.get( "writeOnly" ).isWritable() );
    }

    @Test
    public void testFieldName()
    {
        assertEquals( "simpleProperty", propertyMap.get( "simpleProperty" ).getFieldName() );
        assertEquals( "simplePropertyRenamed", propertyMap.get( "renamedProperty" ).getFieldName() );
    }

    @Test
    public void testNamespace()
    {
        assertEquals( "http://ns.example.org", propertyMap.get( "propertyWithNamespace" ).getNamespace() );
    }

    @Test
    public void testGetter()
    {
        assertNull( propertyMap.get( "simpleProperty" ).getGetterMethod() );
        assertNotNull( propertyMap.get( "writeOnly" ).getGetterMethod() );
    }

    @Test
    public void testSetter()
    {
        assertNull( propertyMap.get( "simpleProperty" ).getSetterMethod() );
        assertNotNull( propertyMap.get( "writeOnly" ).getGetterMethod() );
    }
}
