/*
 * Copyright (c) 2004-2010, University of Oslo All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the HISP project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.attribute;

import org.junit.Test;
import static org.junit.Assert.*;

import org.hisp.dhis.DhisSpringTest;

/**
 * @author mortenoh
 */
public class AttributeOptionServiceTest
    extends DhisSpringTest
{
    private AttributeService attributeService;

    @Override
    protected void setUpTest()
    {
        attributeService = (AttributeService) getBean( "org.hisp.dhis.attribute.AttributeService" );
    }

    @Test
    public void testAddAttributeOption()
    {
        AttributeOption attributeOption1 = new AttributeOption( "option 1" );
        AttributeOption attributeOption2 = new AttributeOption( "option 2" );

        attributeService.addAttributeOption( attributeOption1 );
        attributeService.addAttributeOption( attributeOption2 );

        attributeOption1 = attributeService.getAttributeOption( attributeOption1.getId() );
        attributeOption2 = attributeService.getAttributeOption( attributeOption2.getId() );

        assertNotNull( attributeOption1 );
        assertNotNull( attributeOption2 );
    }

    @Test
    public void testUpdateAttributeOption()
    {
        AttributeOption attributeOption1 = new AttributeOption( "option 1" );
        AttributeOption attributeOption2 = new AttributeOption( "option 2" );

        attributeService.addAttributeOption( attributeOption1 );
        attributeService.addAttributeOption( attributeOption2 );

        attributeOption1.setValue( "updated option 1" );
        attributeOption2.setValue( "updated option 2" );

        attributeService.updateAttributeOption( attributeOption1 );
        attributeService.updateAttributeOption( attributeOption2 );

        attributeOption1 = attributeService.getAttributeOption( attributeOption1.getId() );
        attributeOption2 = attributeService.getAttributeOption( attributeOption2.getId() );

        assertNotNull( attributeOption1 );
        assertNotNull( attributeOption2 );

        assertEquals( "updated option 1", attributeOption1.getValue() );
        assertEquals( "updated option 2", attributeOption2.getValue() );
    }

    @Test
    public void testDeleteAttributeOption()
    {
        AttributeOption attributeOption1 = new AttributeOption( "option 1" );
        AttributeOption attributeOption2 = new AttributeOption( "option 2" );

        attributeService.addAttributeOption( attributeOption1 );
        attributeService.addAttributeOption( attributeOption2 );

        int attributeOptionId1 = attributeOption1.getId();
        int attributeOptionId2 = attributeOption2.getId();

        attributeService.deleteAttributeOption( attributeOption1 );
        attributeService.deleteAttributeOption( attributeOption2 );

        assertNull( attributeService.getAttributeOption( attributeOptionId1 ) );
        assertNull( attributeService.getAttributeOption( attributeOptionId2 ) );
    }

    @Test
    public void testGetAttributeOption()
    {
        AttributeOption attributeOption1 = new AttributeOption( "option 1" );
        AttributeOption attributeOption2 = new AttributeOption( "option 2" );

        attributeService.addAttributeOption( attributeOption1 );
        attributeService.addAttributeOption( attributeOption2 );

        attributeOption1 = attributeService.getAttributeOption( attributeOption1.getId() );
        attributeOption2 = attributeService.getAttributeOption( attributeOption2.getId() );

        assertNotNull( attributeOption1 );
        assertNotNull( attributeOption2 );
    }

    @Test
    public void testGetAllAttributeOptions()
    {
        AttributeOption attributeOption1 = new AttributeOption( "option 1" );
        AttributeOption attributeOption2 = new AttributeOption( "option 2" );

        attributeService.addAttributeOption( attributeOption1 );
        attributeService.addAttributeOption( attributeOption2 );

        assertEquals( 2, attributeService.getAllAttributeOptions().size() );
    }
}
