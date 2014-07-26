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
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.common.AnalyticsType;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DimensionalObjectUtils;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.eventchart.EventChart;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeDimension;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityDataElementDimension;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class EventAnalyticsServiceTest
    extends DhisSpringTest
{
    private Program prA;
    private ProgramStage psA;

    private Period peA;
    private Period peB;
    
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
    private ProgramStageService programStageService;
    
    @Autowired
    private ProgramStageDataElementService programStageDataElementService;
    
    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private TrackedEntityAttributeService attributeService;
    
    @Override
    public void setUpTest()
    {
        peA = PeriodType.getPeriodFromIsoString( "201401" );
        peB = PeriodType.getPeriodFromIsoString( "201402" );
        
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

                
        prA = createProgram( 'A', new ArrayList<ProgramStage>(), asSet( atA, atB ), asSet( ouA, ouB ) );
        programService.addProgram( prA );
        
        psA = createProgramStage( 'A', asSet( deA, deB ) );
        prA.getProgramStages().add( psA );
        
        programStageService.saveProgramStage( psA );
        
        programStageDataElementService.addProgramStageDataElement( new ProgramStageDataElement( psA, deA, false ) );
        programStageDataElementService.addProgramStageDataElement( new ProgramStageDataElement( psA, deB, false ) );
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
        assertEquals( 1, params.getItems().size() );
        assertEquals( 2, params.getFilterPeriods().size() );
    }
    
    @Test
    public void testGetFromAnalyticalObjectA()
    {
        EventChart chart = new EventChart();
        chart.setProgram( prA );
        
        chart.getColumnDimensions().add( atA.getUid() );
        chart.getRowDimensions().add( DimensionalObject.ORGUNIT_DIM_ID );
        chart.getFilterDimensions().add( DimensionalObject.PERIOD_DIM_ID );
        
        chart.getAttributeDimensions().add( new TrackedEntityAttributeDimension( atA, "LE:5" ) );
        chart.getPeriods().add( peA );
        chart.getPeriods().add( peB );
        chart.getOrganisationUnits().add( ouA );
        chart.getOrganisationUnits().add( ouB );
        
        EventQueryParams params = analyticsService.getFromAnalyticalObject( chart, null );
        
        assertNotNull( params );
        assertEquals( 1, params.getItems().size() );
        assertEquals( 2, params.getOrganisationUnits().size() );
        assertEquals( 2, params.getFilterPeriods().size() );
    }
    
    @Test
    public void testGetFromAnalyticalObjectB()
    {
        EventChart chart = new EventChart();
        chart.setProgram( prA );
        
        chart.getColumnDimensions().add( atA.getUid() );
        chart.getColumnDimensions().add( deA.getUid() );
        chart.getRowDimensions().add( DimensionalObject.PERIOD_DIM_ID );
        chart.getFilterDimensions().add( DimensionalObject.ORGUNIT_DIM_ID );
        
        chart.getAttributeDimensions().add( new TrackedEntityAttributeDimension( atA, "LE:5" ) );
        chart.getDataElementDimensions().add( new TrackedEntityDataElementDimension( deA, "GE:100" ) );
        chart.getPeriods().add( peA );
        chart.getPeriods().add( peB );
        chart.getOrganisationUnits().add( ouA );
        chart.getOrganisationUnits().add( ouB );
        
        EventQueryParams params = analyticsService.getFromAnalyticalObject( chart, null );
        
        assertNotNull( params );
        assertEquals( 2, params.getItems().size() );
        assertEquals( 2, params.getPeriods().size() );
        assertEquals( 2, params.getFilterOrganisationUnits().size() );
    }

    @Test
    public void testSetItemsForDimensionFilters()
    {
        TrackedEntityAttribute tea = new TrackedEntityAttribute();

        TrackedEntityAttributeDimension tead = new TrackedEntityAttributeDimension( tea, "EQ:2" );
        
        EventChart chart = new EventChart();
        chart.getColumnDimensions().add( tea.getUid() );
        chart.getAttributeDimensions().add( tead );
        
        Grid grid = new ListGrid();
        grid.addHeader( new GridHeader( tea.getUid(), tea.getName() ) );
        grid.addRow().addValue( "1" );
        grid.addRow().addValue( "2" );
        grid.addRow().addValue( "3" );
        
        chart.populateAnalyticalProperties();
        
        DimensionalObject dim = chart.getColumns().get( 0 );
        
        DimensionalObjectUtils.setDimensionItemsForFilters( dim, grid );
        
        assertNotNull( dim );
        assertEquals( tea.getDimension(), dim.getDimension() );
        assertEquals( DimensionType.TRACKED_ENTITY_ATTRIBUTE, dim.getDimensionType() );
        assertEquals( AnalyticsType.EVENT, dim.getAnalyticsType() );
        assertEquals( tead.getFilter(), dim.getFilter() );
        
        List<NameableObject> items = dim.getItems();
        assertEquals( 3, items.size() );
        assertNotNull( items.get( 0 ).getUid() );
        assertNotNull( items.get( 0 ).getName() );
        assertNotNull( items.get( 0 ).getCode() );
        assertNotNull( items.get( 0 ).getShortName() );
    }
}
