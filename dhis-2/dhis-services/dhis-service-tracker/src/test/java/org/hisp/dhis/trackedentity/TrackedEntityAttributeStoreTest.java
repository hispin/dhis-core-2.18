/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.trackedentity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroup;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroupService;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ TrackedEntityAttributeStoreTest.java Nov 5, 2013 04:21:02 PM $
 */
public class TrackedEntityAttributeStoreTest
    extends DhisSpringTest
{
    @Autowired
    private TrackedEntityAttributeService attributeService;

    @Autowired
    private TrackedEntityAttributeGroupService attributeGroupService;

    private TrackedEntityAttribute attributeA;

    private TrackedEntityAttribute attributeB;

    private TrackedEntityAttribute attributeC;

    private TrackedEntityAttributeGroup attributeGroup;

    @Override
    public void setUpTest()
    {
        attributeA = createTrackedEntityAttribute( 'A' );
        attributeB = createTrackedEntityAttribute( 'B' );
        attributeC = createTrackedEntityAttribute( 'C', TrackedEntityAttribute.TYPE_INT );

        List<TrackedEntityAttribute> attributesA = new ArrayList<TrackedEntityAttribute>();
        attributesA.add( attributeA );
        attributesA.add( attributeB );
        attributeGroup = createTrackedEntityAttributeGroup( 'A', attributesA );
    }

    @Test
    public void testSaveTrackedEntityAttribute()
    {
        int idA = attributeService.saveTrackedEntityAttribute( attributeA );
        int idB = attributeService.saveTrackedEntityAttribute( attributeB );

        assertNotNull( attributeService.getTrackedEntityAttribute( idA ) );
        assertNotNull( attributeService.getTrackedEntityAttribute( idB ) );
    }

    @Test
    public void testDeleteTrackedEntityAttribute()
    {
        int idA = attributeService.saveTrackedEntityAttribute( attributeA );
        int idB = attributeService.saveTrackedEntityAttribute( attributeB );

        assertNotNull( attributeService.getTrackedEntityAttribute( idA ) );
        assertNotNull( attributeService.getTrackedEntityAttribute( idB ) );

        attributeService.deleteTrackedEntityAttribute( attributeA );

        assertNull( attributeService.getTrackedEntityAttribute( idA ) );
        assertNotNull( attributeService.getTrackedEntityAttribute( idB ) );

        attributeService.deleteTrackedEntityAttribute( attributeB );

        assertNull( attributeService.getTrackedEntityAttribute( idA ) );
        assertNull( attributeService.getTrackedEntityAttribute( idB ) );
    }

    @Test
    public void testUpdateTrackedEntityAttribute()
    {
        int idA = attributeService.saveTrackedEntityAttribute( attributeA );

        assertNotNull( attributeService.getTrackedEntityAttribute( idA ) );

        attributeA.setName( "B" );
        attributeService.updateTrackedEntityAttribute( attributeA );

        assertEquals( "B", attributeService.getTrackedEntityAttribute( idA ).getName() );
    }

    @Test
    public void testGetTrackedEntityAttributeById()
    {
        int idA = attributeService.saveTrackedEntityAttribute( attributeA );
        int idB = attributeService.saveTrackedEntityAttribute( attributeB );

        assertEquals( attributeA, attributeService.getTrackedEntityAttribute( idA ) );
        assertEquals( attributeB, attributeService.getTrackedEntityAttribute( idB ) );
    }

    @Test
    public void testGetTrackedEntityAttributeByUid()
    {
        attributeA.setUid( "uid" );
        attributeService.saveTrackedEntityAttribute( attributeA );

        assertEquals( attributeA, attributeService.getTrackedEntityAttribute( "uid" ) );
    }

    @Test
    public void testGetTrackedEntityAttributeByName()
    {
        int idA = attributeService.saveTrackedEntityAttribute( attributeA );

        assertNotNull( attributeService.getTrackedEntityAttribute( idA ) );
        assertEquals( attributeA.getName(), attributeService.getTrackedEntityAttributeByName( "AttributeA" ).getName() );
    }

    @Test
    public void testGetAllTrackedEntityAttributes()
    {
        attributeService.saveTrackedEntityAttribute( attributeA );
        attributeService.saveTrackedEntityAttribute( attributeB );

        assertTrue( equals( attributeService.getAllTrackedEntityAttributes(), attributeA, attributeB ) );
    }

    @Test
    public void testGetTrackedEntityAttributesByValueType()
    {
        attributeService.saveTrackedEntityAttribute( attributeA );
        attributeService.saveTrackedEntityAttribute( attributeB );
        attributeService.saveTrackedEntityAttribute( attributeC );

        Collection<TrackedEntityAttribute> attributes = attributeService
            .getTrackedEntityAttributesByValueType( TrackedEntityAttribute.TYPE_STRING );
        assertEquals( 2, attributes.size() );
        assertTrue( attributes.contains( attributeA ) );
        assertTrue( attributes.contains( attributeB ) );

        attributes = attributeService.getTrackedEntityAttributesByValueType( TrackedEntityAttribute.TYPE_INT );
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );

    }

    @Test
    public void testGetTrackedEntityAttributesByMandatory()
    {
        attributeA.setMandatory( true );
        attributeB.setMandatory( true );
        attributeC.setMandatory( false );

        attributeService.saveTrackedEntityAttribute( attributeA );
        attributeService.saveTrackedEntityAttribute( attributeB );
        attributeService.saveTrackedEntityAttribute( attributeC );

        Collection<TrackedEntityAttribute> attributes = attributeService.getTrackedEntityAttributesByMandatory( true );
        assertEquals( 2, attributes.size() );
        assertTrue( attributes.contains( attributeA ) );
        assertTrue( attributes.contains( attributeB ) );
    }

    @Test
    public void testGetTrackedEntityAttributeByGroupBy()
    {
        attributeA.setGroupBy( true );
        attributeB.setGroupBy( false );
        attributeC.setGroupBy( false );

        attributeService.saveTrackedEntityAttribute( attributeA );
        attributeService.saveTrackedEntityAttribute( attributeB );
        attributeService.saveTrackedEntityAttribute( attributeC );

        assertEquals( attributeA, attributeService.getTrackedEntityAttributeByGroupBy() );
    }

    @Test
    public void testGetTrackedEntityAttributesWithoutGroup()
    {
        attributeService.saveTrackedEntityAttribute( attributeA );
        attributeService.saveTrackedEntityAttribute( attributeB );
        attributeService.saveTrackedEntityAttribute( attributeC );

        attributeGroupService.saveTrackedEntityAttributeGroup( attributeGroup );

        Collection<TrackedEntityAttribute> attributes = attributeService.getOptionalAttributesWithoutGroup();
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );
    }

    @Test
    public void testGetTrackedEntityAttributesByDisplayOnVisitSchedule()
    {
        attributeA.setDisplayOnVisitSchedule( true );
        attributeB.setDisplayOnVisitSchedule( true );
        attributeC.setDisplayOnVisitSchedule( false );

        attributeService.saveTrackedEntityAttribute( attributeA );
        attributeService.saveTrackedEntityAttribute( attributeB );
        attributeService.saveTrackedEntityAttribute( attributeC );

        Collection<TrackedEntityAttribute> attributes = attributeService.getTrackedEntityAttributesByDisplayOnVisitSchedule( true );
        assertEquals( 2, attributes.size() );
        assertTrue( attributes.contains( attributeA ) );
        assertTrue( attributes.contains( attributeB ) );

        attributes = attributeService.getTrackedEntityAttributesByDisplayOnVisitSchedule( false );
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );
    }

}