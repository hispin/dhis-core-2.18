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

public class AttributeOptionStoreTest
    extends DhisSpringTest
{
    private AttributeOptionStore attributeOptionStore;

    private AttributeOption option1;

    private AttributeOption option2;

    @Override
    protected void setUpTest()
    {
        attributeOptionStore = (AttributeOptionStore) getBean( "org.hisp.dhis.attribute.AttributeOptionStore" );

        Attribute attribute1 = new Attribute();
        attribute1.setName( "attribute_simple1" );
        attribute1.setValueType( "string" );

        Attribute attribute2 = new Attribute();
        attribute2.setName( "attribute_simple2" );
        attribute2.setValueType( "string" );

        option1 = new AttributeOption( "option 1" );
        option1.getAttributes().add( attribute1 );
        option1.getAttributes().add( attribute2 );

        option2 = new AttributeOption( "option 2" );
        option2.getAttributes().add( attribute2 );

        attributeOptionStore.save( option1 );
        attributeOptionStore.save( option2 );
    }

    @Test
    public void testGetAttributes()
    {
        AttributeOption ao1 = attributeOptionStore.get( option1.getId() );
        AttributeOption ao2 = attributeOptionStore.get( option2.getId() );

        assertNotNull( ao1 );
        assertNotNull( ao2 );

        assertEquals( 2, ao1.getAttributes().size() );
        assertEquals( 1, ao2.getAttributes().size() );
    }
}
