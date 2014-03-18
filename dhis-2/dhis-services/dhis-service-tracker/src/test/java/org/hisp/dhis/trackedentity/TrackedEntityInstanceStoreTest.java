package org.hisp.dhis.trackedentity;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.hisp.dhis.validation.ValidationCriteriaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class TrackedEntityInstanceStoreTest
    extends DhisSpringTest
{
    @Autowired
    private TrackedEntityInstanceStore entityInstanceStore;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private TrackedEntityAttributeService attributeService;

    @Autowired
    private TrackedEntityAttributeValueService attributeValueService;

    @Autowired
    private ValidationCriteriaService validationCriteriaService;

    private TrackedEntityInstance entityInstanceA1;

    private TrackedEntityInstance entityInstanceA2;

    private TrackedEntityInstance entityInstanceA3;

    private TrackedEntityInstance entityInstanceB1;

    private TrackedEntityInstance entityInstanceB2;

    private TrackedEntityAttribute entityInstanceAttribute;

    private int attributeId;

    private Program programA;

    private Program programB;

    private OrganisationUnit organisationUnit;

    private Date date = new Date();

    @Override
    public void setUpTest()
    {
        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        OrganisationUnit organisationUnitB = createOrganisationUnit( 'B' );
        organisationUnitService.addOrganisationUnit( organisationUnitB );

        TrackedEntityAttribute entityInstanceAttributeB = createTrackedEntityAttribute( 'B' );
        entityInstanceAttributeB.setUnique( true );
        attributeService.addTrackedEntityAttribute( entityInstanceAttributeB );

        entityInstanceAttribute = createTrackedEntityAttribute( 'A' );
        attributeId = attributeService.addTrackedEntityAttribute( entityInstanceAttribute );

        entityInstanceA1 = createTrackedEntityInstance( 'A', organisationUnit );
        entityInstanceA2 = createTrackedEntityInstance( 'A', organisationUnitB );
        entityInstanceA3 = createTrackedEntityInstance( 'A', organisationUnit, entityInstanceAttributeB );
        entityInstanceB1 = createTrackedEntityInstance( 'B', organisationUnit );
        entityInstanceB2 = createTrackedEntityInstance( 'B', organisationUnit );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnit );
    }

    @Test
    public void testAddGet()
    {
        int idA = entityInstanceStore.save( entityInstanceA1 );
        int idB = entityInstanceStore.save( entityInstanceB1 );

        assertNotNull( entityInstanceStore.get( idA ) );
        assertNotNull( entityInstanceStore.get( idB ) );
    }

    @Test
    public void testAddGetbyOu()
    {
        int idA = entityInstanceStore.save( entityInstanceA1 );
        int idB = entityInstanceStore.save( entityInstanceB1 );

        assertEquals( entityInstanceA1.getName(), entityInstanceStore.get( idA ).getName() );
        assertEquals( entityInstanceB1.getName(), entityInstanceStore.get( idB ).getName() );
    }

    @Test
    public void testDelete()
    {
        int idA = entityInstanceStore.save( entityInstanceA1 );
        int idB = entityInstanceStore.save( entityInstanceB1 );

        assertNotNull( entityInstanceStore.get( idA ) );
        assertNotNull( entityInstanceStore.get( idB ) );

        entityInstanceStore.delete( entityInstanceA1 );

        assertNull( entityInstanceStore.get( idA ) );
        assertNotNull( entityInstanceStore.get( idB ) );

        entityInstanceStore.delete( entityInstanceB1 );

        assertNull( entityInstanceStore.get( idA ) );
        assertNull( entityInstanceStore.get( idB ) );
    }

    @Test
    public void testGetAll()
    {
        entityInstanceStore.save( entityInstanceA1 );
        entityInstanceStore.save( entityInstanceB1 );

        assertTrue( equals( entityInstanceStore.getAll(), entityInstanceA1, entityInstanceB1 ) );
    }

    @Test
    public void testGetByOrgUnitProgram()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        entityInstanceStore.save( entityInstanceA1 );
        entityInstanceStore.save( entityInstanceB1 );
        entityInstanceStore.save( entityInstanceA2 );
        entityInstanceStore.save( entityInstanceB2 );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA1, programA, date, date, organisationUnit,
            null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB1, programA, date, date, organisationUnit,
            null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceA2, programA, date, date, organisationUnit,
            null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB2, programB, date, date, organisationUnit,
            null );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceStore.getByOrgUnitProgram( organisationUnit,
            programA, 0, 100 );

        assertEquals( 2, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA1 ) );
        assertTrue( entityInstances.contains( entityInstanceB1 ) );

        entityInstances = entityInstanceStore.getByOrgUnitProgram( organisationUnit, programB, 0, 100 );

        assertEquals( 1, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceB2 ) );
    }

    @Test
    public void testGetByProgram()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        entityInstanceStore.save( entityInstanceA1 );
        entityInstanceStore.save( entityInstanceB1 );
        entityInstanceStore.save( entityInstanceA2 );
        entityInstanceStore.save( entityInstanceB2 );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA1, programA, date, date, organisationUnit,
            null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceA2, programA, date, date, organisationUnit,
            null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB1, programA, date, date, organisationUnit,
            null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB2, programB, date, date, organisationUnit,
            null );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceStore.getByProgram( programA, 0, 100 );

        assertEquals( 3, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA1 ) );
        assertTrue( entityInstances.contains( entityInstanceA2 ) );
        assertTrue( entityInstances.contains( entityInstanceB1 ) );

        entityInstances = entityInstanceStore.getByOrgUnitProgram( organisationUnit, programB, 0, 100 );

        assertEquals( 1, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceB2 ) );
    }

    @Test
    public void testGetRepresentatives()
    {
        entityInstanceStore.save( entityInstanceB1 );

        entityInstanceA1.setRepresentative( entityInstanceB1 );
        entityInstanceA2.setRepresentative( entityInstanceB1 );
        entityInstanceStore.save( entityInstanceA1 );
        entityInstanceStore.save( entityInstanceA2 );

        assertEquals( 2, entityInstanceStore.getRepresentatives( entityInstanceB1 ).size() );
    }

    @Test
    public void testGetByPhoneNumber()
    {
        entityInstanceStore.save( entityInstanceA1 );
        entityInstanceStore.save( entityInstanceB1 );

        TrackedEntityAttribute attribute = createTrackedEntityAttribute( 'C' );
        attribute.setValueType( TrackedEntityAttribute.TYPE_PHONE_NUMBER );
        attributeService.addTrackedEntityAttribute( attribute );

        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA1,
            attribute );
        attributeValue.setValue( "123456789" );
        attributeValueService.addTrackedEntityAttributeValue( attributeValue );

        entityInstanceA1.addAttributeValue( attributeValue );
        entityInstanceStore.update( entityInstanceA1 );

        attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceB1, attribute );
        attributeValue.setValue( "123456789" );
        attributeValueService.addTrackedEntityAttributeValue( attributeValue );

        entityInstanceB1.addAttributeValue( attributeValue );
        entityInstanceStore.update( entityInstanceB1 );

        assertEquals( 2, entityInstanceStore.getByPhoneNumber( "123456789", null, null ).size() );
    }

    @Test
    public void testSearch()
    {
        int idA = programService.addProgram( programA );
        programService.addProgram( programB );

        entityInstanceStore.save( entityInstanceA1 );
        entityInstanceStore.save( entityInstanceA2 );
        entityInstanceStore.save( entityInstanceA3 );
        entityInstanceStore.save( entityInstanceB1 );
        entityInstanceStore.save( entityInstanceB2 );

        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA3,
            entityInstanceAttribute );
        attributeValueService.addTrackedEntityAttributeValue( attributeValue );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA3, programA, date, date, organisationUnit,
            null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB1, programA, date, date, organisationUnit,
            null );

        List<String> searchKeys = new ArrayList<String>();
        searchKeys.add( TrackedEntityInstance.PREFIX_TRACKED_ENTITY_ATTRIBUTE + TrackedEntityInstance.SEARCH_SAPERATE
            + attributeId + TrackedEntityInstance.SEARCH_SAPERATE + "a" + TrackedEntityInstance.SEARCH_SAPERATE
            + organisationUnit.getId() );
        searchKeys.add( TrackedEntityInstance.PREFIX_PROGRAM + TrackedEntityInstance.SEARCH_SAPERATE + idA );

        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();
        orgunits.add( organisationUnit );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceStore.search( searchKeys, orgunits, null, null,
            ProgramStageInstance.ACTIVE_STATUS, null, null );

        assertEquals( 1, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA3 ) );
    }

    @Test
    public void testQuery()
    {
        entityInstanceStore.save( entityInstanceA1 );
        entityInstanceStore.save( entityInstanceA2 );
        entityInstanceStore.save( entityInstanceA3 );
        entityInstanceStore.save( entityInstanceB1 );
        entityInstanceStore.save( entityInstanceB2 );

        TrackedEntityQueryParams params = new TrackedEntityQueryParams();

        List<TrackedEntityInstance> list = entityInstanceStore.query( params );

        assertEquals( 5, list.size() );
    }
}
