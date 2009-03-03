/*
 * Copyright (c) 2004-2008, University of Oslo
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
package org.hisp.dhis.integration.rims.api;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Leif Arne Storset
 * @version $Id$
 */
public class RIMS_Mapping_DataElementTest
{
    @Test
    public void testGetDhisDataElementIdSucceed()
    {
        RIMS_Mapping_DataElement fixture = new RIMS_Mapping_DataElement();
        fixture.setDhisExpression( "[123.45]" );
        assertEquals( 123, fixture.getDhisDataElementId() );
    }

    @Test
    public void testGetDhisDataElementIdFail()
    {
        RIMS_Mapping_DataElement fixture = new RIMS_Mapping_DataElement();
        try
        {
            fixture.setDhisExpression( "123" );
            fixture.getDhisDataElementId();
        }
        catch ( Exception e )
        {
            return;
        }
        fail();
    }

    @Test
    public void testGetDhisOptionComboSucceed()
    {
        RIMS_Mapping_DataElement fixture = new RIMS_Mapping_DataElement();
        fixture.setDhisExpression( "[123.45]" );
        assertEquals( 45, fixture.getDhisOptionCombo() );
    }

    @Test
    public void testGetDhisOptionComboFail()
    {
        RIMS_Mapping_DataElement fixture = new RIMS_Mapping_DataElement();
        try
        {
            fixture.setDhisExpression( "123" );
            fixture.getDhisOptionCombo();
        }
        catch ( Exception e )
        {
            return;
        }
        fail();
    }
}
