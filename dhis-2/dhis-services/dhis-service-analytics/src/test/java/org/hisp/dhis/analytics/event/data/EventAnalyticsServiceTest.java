package org.hisp.dhis.analytics.event.data;

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

import static org.hisp.dhis.system.util.CollectionUtils.asSet;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class EventAnalyticsServiceTest
    extends DhisSpringTest
{
    private Program prA;

    private OrganisationUnit ouA;
    private OrganisationUnit ouB;
    
    private DataElement deA;
    private DataElement deB;
    
    private TrackedEntityAttribute atA;
    private TrackedEntityAttribute atB;

    @Autowired
    private EventAnalyticsService analyticsService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private TrackedEntityAttributeService attributeService;

    @Override
    public void setUpTest()
    {
        ouA = createOrganisationUnit( 'A' );
        ouB = createOrganisationUnit( 'B' );

        organisationUnitService.addOrganisationUnit( ouA );
        organisationUnitService.addOrganisationUnit( ouB );
        
        deA = createDataElement( 'A' );
        deB = createDataElement( 'B' );

        dataElementService.addDataElement( deA );
        dataElementService.addDataElement( deB );
        
        atA = createTrackedEntityAttribute( 'A' );
        atB = createTrackedEntityAttribute( 'B' );
        
        attributeService.addTrackedEntityAttribute( atA );
        attributeService.addTrackedEntityAttribute( atB );

        prA = createProgram( 'A', null, asSet( atA, atB ), asSet( ouA, ouB ) );
        programService.addProgram( prA );        
    }

    @Test
    public void testGetFromUrlA()
    {
        Set<String> dimensionParams = new HashSet<String>();
        dimensionParams.add( "ou:" + ouA.getUid() + ";" + ouB.getId() );
        dimensionParams.add( atA.getUid() + ":LE:5" );
        
        Set<String> filterParams = new HashSet<String>();
        filterParams.add( "pe:201401;201402" );
        
        EventQueryParams params = analyticsService.getFromUrl( prA.getUid(), null, 
            null, null, dimensionParams, filterParams, false, false, null, null, false, null );
        
        assertEquals( prA, params.getProgram() );
        assertEquals( 1, params.getOrganisationUnits().size() );
        assertEquals( 2, params.getFilterPeriods().size() );
    }
}
