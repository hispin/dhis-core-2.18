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
import static org.junit.Assert.assertTrue;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeOption;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeOptionStore;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ TrackedEntityAttributeOptionStoreTest.java Nov 5, 2013 4:43:12 PM $
 */
public class TrackedEntityAttributeOptionStoreTest
    extends DhisSpringTest
{
    @Autowired
    private TrackedEntityAttributeOptionStore attributeOptionStore;

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

        attributeService.saveTrackedEntityAttribute( attributeA );
        attributeService.saveTrackedEntityAttribute( attributeB );

        attributeOptionA = createTrackedEntityAttributeOption( 'A', attributeA );
        attributeOptionB = createTrackedEntityAttributeOption( 'B', attributeA );
        attributeOptionC = createTrackedEntityAttributeOption( 'C', attributeB );
    }

    @Test
    public void testGetTrackedEntityAttributeGroupByName()
    {
        int idA = attributeOptionStore.save( attributeOptionA );

        assertNotNull( attributeOptionStore.get( idA ) );

        assertEquals( attributeOptionA, attributeOptionStore.get( attributeA, "AttributeOptionA" ) );
    }

    @Test
    public void testGetTrackedEntityAttributeOptionByAttribute()
    {
        attributeOptionStore.save( attributeOptionA );
        attributeOptionStore.save( attributeOptionB );
        attributeOptionStore.save( attributeOptionC );

        assertTrue( equals( attributeOptionStore.get( attributeA ), attributeOptionA, attributeOptionB ) );
        assertTrue( equals( attributeOptionStore.get( attributeB ), attributeOptionC ) );
    }

}