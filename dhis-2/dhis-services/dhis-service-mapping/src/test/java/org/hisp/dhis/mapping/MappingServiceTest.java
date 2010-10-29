package org.hisp.dhis.mapping;

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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class MappingServiceTest
    extends DhisSpringTest
{
    private MappingService mappingService;

    private OrganisationUnit organisationUnit;

    private OrganisationUnitLevel organisationUnitLevel;

    private IndicatorGroup indicatorGroup;

    private IndicatorType indicatorType;

    private Indicator indicator;

    private DataElement dataElement;

    private PeriodType periodType;

    private Period period;

    private MapLegendSet mapLegendSet;

    private Map mapA;

    private Map mapB;

    private MapOrganisationUnitRelation mapOrganisationUnitRelationA;

    private MapOrganisationUnitRelation mapOrganisationUnitRelationB;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        mappingService = (MappingService) getBean( MappingService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitLevel = new OrganisationUnitLevel( 1, "Level" );

        organisationUnitService.addOrganisationUnit( organisationUnit );
        organisationUnitService.addOrganisationUnitLevel( organisationUnitLevel );

        mapA = createMap( 'A', organisationUnit, organisationUnitLevel );
        mapB = createMap( 'B', organisationUnit, organisationUnitLevel );

        mapOrganisationUnitRelationA = new MapOrganisationUnitRelation( mapA, organisationUnit, "Feature" );
        mapOrganisationUnitRelationB = new MapOrganisationUnitRelation( mapB, organisationUnit, "Feature" );

        indicatorGroup = createIndicatorGroup( 'A' );
        indicatorService.addIndicatorGroup( indicatorGroup );

        indicatorType = createIndicatorType( 'A' );
        indicatorService.addIndicatorType( indicatorType );

        indicator = createIndicator( 'A', indicatorType );
        indicatorService.addIndicator( indicator );

        dataElement = createDataElement( 'A' );
        dataElementService.addDataElement( dataElement );

        periodType = periodService.getPeriodTypeByName( MonthlyPeriodType.NAME );
        period = createPeriod( periodType, getDate( 2000, 1, 1 ), getDate( 2000, 2, 1 ) );
        periodService.addPeriod( period );

        mapLegendSet = createMapLegendSet( 'A', indicator );
        mappingService.addMapLegendSet( mapLegendSet );
    }

    // -------------------------------------------------------------------------
    // Map tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetMap()
    {
        int idA = mappingService.addMap( mapA );
        int idB = mappingService.addMap( mapB );

        assertEquals( mapA, mappingService.getMap( idA ) );
        assertEquals( mapB, mappingService.getMap( idB ) );
    }

    @Test
    public void testDeleteMap()
    {
        int idA = mappingService.addMap( mapA );
        int idB = mappingService.addMap( mapB );

        mappingService.addMapOrganisationUnitRelation( mapOrganisationUnitRelationA );
        mappingService.addMapOrganisationUnitRelation( mapOrganisationUnitRelationB );

        assertNotNull( mappingService.getMap( idA ) );
        assertNotNull( mappingService.getMap( idB ) );

        mappingService.deleteMap( mapA );

        assertNull( mappingService.getMap( idA ) );
        assertNotNull( mappingService.getMap( idB ) );

        mappingService.deleteMap( mapB );

        assertNull( mappingService.getMap( idA ) );
        assertNull( mappingService.getMap( idB ) );
    }

    // -------------------------------------------------------------------------
    // MapOrganisationUnitRelation tests
    // -------------------------------------------------------------------------

    @Test
    public void addGetMapOrganisationUnitRelation()
    {
        mappingService.addMap( mapA );
        mappingService.addMap( mapB );

        int idA = mappingService.addMapOrganisationUnitRelation( mapOrganisationUnitRelationA );
        int idB = mappingService.addMapOrganisationUnitRelation( mapOrganisationUnitRelationB );

        assertEquals( mappingService.getMapOrganisationUnitRelation( idA ), mapOrganisationUnitRelationA );
        assertEquals( mappingService.getMapOrganisationUnitRelation( idB ), mapOrganisationUnitRelationB );
    }

    @Test
    public void deleteMapOrganisationUnitRelation()
    {
        mappingService.addMap( mapA );
        mappingService.addMap( mapB );

        int idA = mappingService.addMapOrganisationUnitRelation( mapOrganisationUnitRelationA );
        int idB = mappingService.addMapOrganisationUnitRelation( mapOrganisationUnitRelationB );

        assertNotNull( mappingService.getMapOrganisationUnitRelation( idA ) );
        assertNotNull( mappingService.getMapOrganisationUnitRelation( idB ) );

        mappingService.deleteMapOrganisationUnitRelation( mapOrganisationUnitRelationA );

        assertNull( mappingService.getMapOrganisationUnitRelation( idA ) );
        assertNotNull( mappingService.getMapOrganisationUnitRelation( idB ) );

        mappingService.deleteMapOrganisationUnitRelation( mapOrganisationUnitRelationB );

        assertNull( mappingService.getMapOrganisationUnitRelation( idA ) );
        assertNull( mappingService.getMapOrganisationUnitRelation( idB ) );
    }

    // -------------------------------------------------------------------------
    // MapView tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetMapView()
    {
        mappingService.addMap( mapA );

        MapView mapView = new MapView( "MapViewA", MappingService.MAP_VALUE_TYPE_INDICATOR, indicatorGroup, indicator,
            new DataElementGroup(), new DataElement(), MappingService.MAP_DATE_TYPE_FIXED, periodType, period, "", "",
            MappingService.ORGANISATION_UNIT_SELECTION_TYPE_PARENT, MappingService.MAP_SOURCE_TYPE_SHAPEFILE,
            "sl_districts", MappingService.MAPLEGENDSET_TYPE_AUTOMATIC, 1, 1, "", "A", "B", mapLegendSet, "1", "1", 1 );

        int idA = mappingService.addMapView( mapView );

        assertEquals( mapView, mappingService.getMapView( idA ) );
        assertEquals( indicatorGroup, mappingService.getMapView( idA ).getIndicatorGroup() );
        assertEquals( indicator, mappingService.getMapView( idA ).getIndicator() );
        assertEquals( periodType, mappingService.getMapView( idA ).getPeriodType() );
        assertEquals( period, mappingService.getMapView( idA ).getPeriod() );
    }

    // -------------------------------------------------------------------------
    // MapValue tests
    // -------------------------------------------------------------------------

    @Test
    public void testMapValues()
    {
        mappingService.getDataElementMapValues( dataElement.getId(), period.getId(), organisationUnit.getId() );
        mappingService.getIndicatorMapValues( indicator.getId(), period.getId(), organisationUnit.getId() );
    }

}
