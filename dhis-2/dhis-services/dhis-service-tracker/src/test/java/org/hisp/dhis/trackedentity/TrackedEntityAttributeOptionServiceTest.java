package org.hisp.dhis.trackedentity;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ TrackedEntityAttributeOptionServiceTest.java Nov 5, 2013 4:25:29 PM $
 */
public class TrackedEntityAttributeOptionServiceTest
    extends DhisSpringTest
{
    @Autowired
    private TrackedEntityAttributeService attributeService;

    private TrackedEntityAttributeOption attributeOptionA;

    private TrackedEntityAttributeOption attributeOptionB;

    private TrackedEntityAttributeOption attributeOptionC;

    private TrackedEntityAttribute attributeA;

    private TrackedEntityAttribute attributeB;

    @Override
    public void setUpTest()
    {
        attributeA = createTrackedEntityAttribute( 'A' );
        attributeB = createTrackedEntityAttribute( 'B' );

        attributeService.addTrackedEntityAttribute( attributeA );
        attributeService.addTrackedEntityAttribute( attributeB );

        attributeOptionA = createTrackedEntityAttributeOption( 'A', attributeA );
        attributeOptionB = createTrackedEntityAttributeOption( 'B', attributeA );
        attributeOptionC = createTrackedEntityAttributeOption( 'C', attributeB );
    }

    @Test
    public void testAddTrackedEntityAttributeOption()
    {
        int idA = attributeService.addTrackedEntityAttributeOption( attributeOptionA );
        int idB = attributeService.addTrackedEntityAttributeOption( attributeOptionB );

        assertNotNull( attributeService.getTrackedEntityAttributeOption( idA ) );
        assertNotNull( attributeService.getTrackedEntityAttributeOption( idB ) );
    }

    @Test
    public void testDeleteTrackedEntityAttributeGroup()
    {
        int idA = attributeService.addTrackedEntityAttributeOption( attributeOptionA );
        int idB = attributeService.addTrackedEntityAttributeOption( attributeOptionB );

        assertNotNull( attributeService.getTrackedEntityAttributeOption( idA ) );
        assertNotNull( attributeService.getTrackedEntityAttributeOption( idB ) );

        attributeService.deleteTrackedEntityAttributeOption( attributeOptionA );

        assertNull( attributeService.getTrackedEntityAttributeOption( idA ) );
        assertNotNull( attributeService.getTrackedEntityAttributeOption( idB ) );

        attributeService.deleteTrackedEntityAttributeOption( attributeOptionB );

        assertNull( attributeService.getTrackedEntityAttributeOption( idA ) );
        assertNull( attributeService.getTrackedEntityAttributeOption( idB ) );
    }

    @Test
    public void testUpdateTrackedEntityAttributeOption()
    {
        int idA = attributeService.addTrackedEntityAttributeOption( attributeOptionA );

        assertNotNull( attributeService.getTrackedEntityAttributeOption( idA ) );

        attributeOptionA.setName( "B" );
        attributeService.updateTrackedEntityAttributeOption( attributeOptionA );

        assertEquals( "B", attributeService.getTrackedEntityAttributeOption( idA ).getName() );
    }

    @Test
    public void testGetTrackedEntityAttributeGroupById()
    {
        int idA = attributeService.addTrackedEntityAttributeOption( attributeOptionA );
        int idB = attributeService.addTrackedEntityAttributeOption( attributeOptionB );

        assertEquals( attributeOptionA, attributeService.getTrackedEntityAttributeOption( idA ) );
        assertEquals( attributeOptionB, attributeService.getTrackedEntityAttributeOption( idB ) );
    }

    @Test
    public void testGetTrackedEntityAttributeGroupByName()
    {
        int idA = attributeService.addTrackedEntityAttributeOption( attributeOptionA );

        assertNotNull( attributeService.getTrackedEntityAttributeOption( idA ) );

        assertEquals( attributeOptionA, attributeService.getTrackedEntityAttributeOption( attributeA, "AttributeOptionA" ) );
    }

    @Test
    public void testGetTrackedEntityAttributeOptionByAttribute()
    {
        attributeService.addTrackedEntityAttributeOption( attributeOptionA );
        attributeService.addTrackedEntityAttributeOption( attributeOptionB );
        attributeService.addTrackedEntityAttributeOption( attributeOptionC );

        assertTrue( equals( attributeService.getTrackedEntityAttributeOption( attributeA ), attributeOptionA, attributeOptionB ) );
        assertTrue( equals( attributeService.getTrackedEntityAttributeOption( attributeB ), attributeOptionC ) );
    }

}