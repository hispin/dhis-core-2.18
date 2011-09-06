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
public class AttributeValueServiceTest
    extends DhisSpringTest
{
    private AttributeValueService attributeValueService;

    @Override
    protected void setUpTest()
    {
        attributeValueService = (AttributeValueService) getBean( "org.hisp.dhis.attribute.AttributeValueService" );
    }

    @Test
    public void testAddAttributeValue()
    {
        AttributeValue attributeValue1 = new AttributeValue( "value 1" );
        AttributeValue attributeValue2 = new AttributeValue( "value 2" );

        attributeValueService.addAttributeValue( attributeValue1 );
        attributeValueService.addAttributeValue( attributeValue2 );

        attributeValue1 = attributeValueService.getAttributeValue( attributeValue1.getId() );
        attributeValue2 = attributeValueService.getAttributeValue( attributeValue2.getId() );

        assertNotNull( attributeValue1 );
        assertNotNull( attributeValue2 );
    }

    @Test
    public void testUpdateAttributeValue()
    {
        AttributeValue attributeValue1 = new AttributeValue( "value 1" );
        AttributeValue attributeValue2 = new AttributeValue( "value 2" );

        attributeValueService.addAttributeValue( attributeValue1 );
        attributeValueService.addAttributeValue( attributeValue2 );

        attributeValue1.setValue( "updated value 1" );
        attributeValue2.setValue( "updated value 2" );

        attributeValueService.updateAttributeValue( attributeValue1 );
        attributeValueService.updateAttributeValue( attributeValue2 );

        attributeValue1 = attributeValueService.getAttributeValue( attributeValue1.getId() );
        attributeValue2 = attributeValueService.getAttributeValue( attributeValue2.getId() );

        assertNotNull( attributeValue1 );
        assertNotNull( attributeValue2 );

        assertEquals( "updated value 1", attributeValue1.getValue() );
        assertEquals( "updated value 2", attributeValue2.getValue() );
    }

    @Test
    public void testDeleteAttributeValue()
    {
        AttributeValue attributeValue1 = new AttributeValue( "value 1" );
        AttributeValue attributeValue2 = new AttributeValue( "value 2" );

        attributeValueService.addAttributeValue( attributeValue1 );
        attributeValueService.addAttributeValue( attributeValue2 );

        int attributeValueId1 = attributeValue1.getId();
        int attributeValueId2 = attributeValue2.getId();

        attributeValueService.deleteAttributeValue( attributeValue1 );
        attributeValueService.deleteAttributeValue( attributeValue2 );

        attributeValue1 = attributeValueService.getAttributeValue( attributeValueId1 );
        attributeValue2 = attributeValueService.getAttributeValue( attributeValueId2 );

        assertNull( attributeValue1 );
        assertNull( attributeValue2 );
    }

    @Test
    public void testGetAttributeValue()
    {
        AttributeValue attributeValue1 = new AttributeValue( "value 1" );
        AttributeValue attributeValue2 = new AttributeValue( "value 2" );

        attributeValueService.addAttributeValue( attributeValue1 );
        attributeValueService.addAttributeValue( attributeValue2 );

        attributeValue1 = attributeValueService.getAttributeValue( attributeValue1.getId() );
        attributeValue2 = attributeValueService.getAttributeValue( attributeValue2.getId() );

        assertNotNull( attributeValue1 );
        assertNotNull( attributeValue2 );
    }

    @Test
    public void testGetAllAttributeValues()
    {
        AttributeValue attributeValue1 = new AttributeValue( "value 1" );
        AttributeValue attributeValue2 = new AttributeValue( "value 2" );

        attributeValueService.addAttributeValue( attributeValue1 );
        attributeValueService.addAttributeValue( attributeValue2 );

        assertEquals( 2, attributeValueService.getAllAttributeValues().size() );
    }
}
