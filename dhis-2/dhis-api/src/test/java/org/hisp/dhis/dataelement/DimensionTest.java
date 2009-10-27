package org.hisp.dhis.dataelement;

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

import java.util.Map;

import org.hisp.dhis.dimension.Dimension;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Lars Helge Overland
 */
public class DimensionTest
{
    @Test
    public void testGroupSetDimensions()
    {
        OrganisationUnit source = new OrganisationUnit( "Bobs Clinic" );
        Period period = new MonthlyPeriodType().createPeriod();
        
        DataElement hivAids = new DataElement( "HivAids" );
        DataElement malaria = new DataElement( "Malaria" );
        DataElement diabetes = new DataElement( "Diabetes" );
        DataElement cancer = new DataElement( "Cancer" );
        
        DataElementGroup communicable = new DataElementGroup( "Communicable" );
        DataElementGroup nonCommunicable = new DataElementGroup( "NonCommunicable" );
        
        DataElementGroupSet diseaseType = new DataElementGroupSet( "DiseaseType" );
        
        DataElement diseaseByType = new DataElement( "DiseaseByType" ); // Uber data element
        
        communicable.getMembers().add( hivAids );
        communicable.getMembers().add( malaria );
        
        nonCommunicable.getMembers().add( diabetes );
        nonCommunicable.getMembers().add( cancer );
        
        diseaseType.getMembers().add( communicable );
        diseaseType.getMembers().add( nonCommunicable );
        
        diseaseByType.getGroupSets().add( diseaseType );
        
        DataValue dataValue = new DataValue( hivAids, period, source );
        
        Map<Dimension, DimensionOption> dimensions = dataValue.getDimensions( diseaseByType );
        
        assertEquals( 4, dimensions.size() );
        
        assertTrue( dimensions.keySet().contains( diseaseType ) );
        assertTrue( dimensions.keySet().contains( DataElement.DIMENSION ) );
        assertTrue( dimensions.keySet().contains( Period.DIMENSION ) );
        assertTrue( dimensions.keySet().contains( Source.DIMENSION ) );
        
        assertTrue( dimensions.values().contains( communicable ) );
        assertTrue( dimensions.values().contains( hivAids ) );
        assertTrue( dimensions.values().contains( period ) );
        assertTrue( dimensions.values().contains( source ) );

        dataValue = new DataValue( diabetes, period, source );
        
        dimensions = dataValue.getDimensions( diseaseByType );
        
        assertEquals( 4, dimensions.size() );
        
        assertTrue( dimensions.keySet().contains( diseaseType ) );
        assertTrue( dimensions.keySet().contains( DataElement.DIMENSION ) );
        assertTrue( dimensions.keySet().contains( Period.DIMENSION ) );
        assertTrue( dimensions.keySet().contains( Source.DIMENSION ) );
        
        assertTrue( dimensions.values().contains( nonCommunicable ) );
        assertTrue( dimensions.values().contains( diabetes ) );
        assertTrue( dimensions.values().contains( period ) );
        assertTrue( dimensions.values().contains( source ) );
    }
}
