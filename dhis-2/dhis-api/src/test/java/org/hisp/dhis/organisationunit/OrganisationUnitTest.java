package org.hisp.dhis.organisationunit;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class OrganisationUnitTest
{
    private List<String> dirtyCoordinatesCollection = new ArrayList<String>();
    private List<String> cleanCoordinatesCollection = new ArrayList<String>();
    
    private String coordinates = "[[[[11.11,22.22],[33.33,44.44],[55.55,66.66]]],[[[77.77,88.88],[99.99,11.11],[22.22,33.33]]],[[[44.44,55.55],[66.66,77.77],[88.88,99.99]]]]";
    
    @Before
    public void before()
    {
        dirtyCoordinatesCollection.add( "11.11,22.22  33.33,44.44    55.55,66.66" ); // Extra space between coords
        dirtyCoordinatesCollection.add( "77.77,88.88 99.99,11.11\n22.22,33.33" );  // Newline between coords
        dirtyCoordinatesCollection.add( "  44.44,55.55 66.66,77.77 88.88,99.99 " );  // Leading and trailing space
        
        cleanCoordinatesCollection.add( "11.11,22.22 33.33,44.44 55.55,66.66" ); // Testing on string since we control the output format
        cleanCoordinatesCollection.add( "77.77,88.88 99.99,11.11 22.22,33.33" );
        cleanCoordinatesCollection.add( "44.44,55.55 66.66,77.77 88.88,99.99" );
    }

    @Test
    public void testSetCoordinatesFromCollection()
    {
        OrganisationUnit unit = new OrganisationUnit();
        unit.setCoordinatesFromCollection( dirtyCoordinatesCollection );
        
        assertEquals( coordinates, unit.getCoordinates() );
    }
    
    @Test
    public void testGetCoordinatesAsCollection()
    {   
        OrganisationUnit unit = new OrganisationUnit();
        unit.setCoordinates( coordinates );
        
        Collection<String> actual = unit.getCoordinatesAsCollection();
        
        assertEquals( 3, actual.size() );
        assertTrue( actual.contains( cleanCoordinatesCollection.get( 0 ) ) );
        assertTrue( actual.contains( cleanCoordinatesCollection.get( 1 ) ) );
        assertTrue( actual.contains( cleanCoordinatesCollection.get( 2 ) ) );
    }
    
    @Test
    public void testGetCoordinatesAsMap()
    {
        OrganisationUnit unit = new OrganisationUnit();
        unit.setCoordinates( coordinates );
        
        Collection<String> actual = unit.getAllCoordinates();
        
        assertEquals( 9, actual.size() );        
    }
}
