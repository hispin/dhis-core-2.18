package org.hisp.dhis.dimension;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DimensionServiceTest
    extends DhisSpringTest
{
    private DimensionService dimensionService;
    
    private DataElementGroupSet dataElementGroupSetA;
    private DataElementGroupSet dataElementGroupSetB;
    private DataElementGroupSet dataElementGroupSetC;
    
    private DataElement dataElementA;
    private DataElement dataElementB;
    private DataElement dataElementC;
    private DataElement dataElementD;
    
    @Override
    public void setUpTest()
    {
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        dimensionService = (DimensionService) getBean( DimensionService.ID );
        
        dataElementGroupSetA = new DataElementGroupSet( "DataElementGroupSetA" );
        dataElementGroupSetB = new DataElementGroupSet( "DataElementGroupSetB" );
        dataElementGroupSetC = new DataElementGroupSet( "DataElementGroupSetC" );
        
        dataElementService.addDataElementGroupSet( dataElementGroupSetA );
        dataElementService.addDataElementGroupSet( dataElementGroupSetB );
        dataElementService.addDataElementGroupSet( dataElementGroupSetC );
        
        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        dataElementC = createDataElement( 'C' );
        dataElementD = createDataElement( 'D' );
        
        dataElementA.getGroupSets().add( dataElementGroupSetA );
        dataElementA.getGroupSets().add( dataElementGroupSetB );

        dataElementB.getGroupSets().add( dataElementGroupSetB );
        dataElementB.getGroupSets().add( dataElementGroupSetC );

        dataElementC.getGroupSets().add( dataElementGroupSetA );
        dataElementC.getGroupSets().add( dataElementGroupSetB );
        
        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );
    }
    
    @Test
    public void getDataElementDimensionSets()
    {
        Collection<DimensionSet> dimensionSets = dimensionService.getDataElementDimensionSets();
        
        assertEquals( 3, dimensionSets.size() ); // Including default category combo
        assertTrue( dimensionSets.contains( new BasicDimensionSet( dataElementGroupSetA, dataElementGroupSetB ) ) );
        assertTrue( dimensionSets.contains( new BasicDimensionSet( dataElementGroupSetB, dataElementGroupSetC ) ) );
    }
    
    @Test
    public void getDataElements()
    {
        Collection<DataElement> dataElements = new HashSet<DataElement>();
        
        Collection<DimensionSet> dimensionSets = dimensionService.getDataElementDimensionSets();
        
        for ( DimensionSet dimensionSet : dimensionSets )
        {
            dataElements.addAll( dimensionService.getDataElements( dimensionSet ) );
        }
        
        assertEquals( 3, dataElements.size() );
        assertTrue( dataElements.contains( dataElementA ) );
        assertTrue( dataElements.contains( dataElementB ) );
        assertTrue( dataElements.contains( dataElementC ) );
    }
}
