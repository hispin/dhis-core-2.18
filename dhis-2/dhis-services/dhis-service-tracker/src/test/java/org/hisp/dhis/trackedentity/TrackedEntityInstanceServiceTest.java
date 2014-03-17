/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.trackedentity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.hisp.dhis.validation.ValidationCriteriaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ TrackedEntityInstanceServiceTest.java Nov 5, 2013 10:35:31 AM $
 */
public class TrackedEntityInstanceServiceTest
    extends DhisSpringTest
{
    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

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

    @Autowired
    private RelationshipTypeService relationshipTypeService;

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

        entityInstanceAttribute = createTrackedEntityAttribute( 'A' );
        attributeId = attributeService.addTrackedEntityAttribute( entityInstanceAttribute );

        entityInstanceA1 = createTrackedEntityInstance( 'A', organisationUnit );
        entityInstanceA2 = createTrackedEntityInstance( 'A', organisationUnitB );
        entityInstanceA3 = createTrackedEntityInstance( 'A', organisationUnit, entityInstanceAttribute );
        entityInstanceB1 = createTrackedEntityInstance( 'B', organisationUnit );
        entityInstanceB2 = createTrackedEntityInstance( 'B', organisationUnit, entityInstanceAttribute );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnit );
    }

    @Test
    public void testSaveTrackedEntityInstance()
    {
        int idA = entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        int idB = entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );

        assertNotNull( entityInstanceService.getTrackedEntityInstance( idA ) );
        assertNotNull( entityInstanceService.getTrackedEntityInstance( idB ) );
    }

    @Test
    public void testDeleteTrackedEntityInstance()
    {
        int idA = entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        int idB = entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );

        assertNotNull( entityInstanceService.getTrackedEntityInstance( idA ) );
        assertNotNull( entityInstanceService.getTrackedEntityInstance( idB ) );

        entityInstanceService.deleteTrackedEntityInstance( entityInstanceA1 );

        assertNull( entityInstanceService.getTrackedEntityInstance( idA ) );
        assertNotNull( entityInstanceService.getTrackedEntityInstance( idB ) );

        entityInstanceService.deleteTrackedEntityInstance( entityInstanceB1 );

        assertNull( entityInstanceService.getTrackedEntityInstance( idA ) );
        assertNull( entityInstanceService.getTrackedEntityInstance( idB ) );
    }

    @Test
    public void testUpdateTrackedEntityInstance()
    {
        int idA = entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );

        assertNotNull( entityInstanceService.getTrackedEntityInstance( idA ) );

        entityInstanceA1.setName( "B" );
        entityInstanceService.updateTrackedEntityInstance( entityInstanceA1 );

        assertEquals( "B", entityInstanceService.getTrackedEntityInstance( idA ).getName() );
    }

    @Test
    public void testGetTrackedEntityInstanceById()
    {
        int idA = entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        int idB = entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );

        assertEquals( entityInstanceA1, entityInstanceService.getTrackedEntityInstance( idA ) );
        assertEquals( entityInstanceB1, entityInstanceService.getTrackedEntityInstance( idB ) );
    }

    @Test
    public void testGetTrackedEntityInstanceByUid()
    {
        entityInstanceA1.setUid( "A1" );
        entityInstanceB1.setUid( "B1" );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );

        assertEquals( entityInstanceA1, entityInstanceService.getTrackedEntityInstance( "A1" ) );
        assertEquals( entityInstanceB1, entityInstanceService.getTrackedEntityInstance( "B1" ) );
    }

    @Test
    public void testGetAllTrackedEntityInstances()
    {
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );

        assertTrue( equals( entityInstanceService.getAllTrackedEntityInstances(), entityInstanceA1, entityInstanceB1 ) );
    }

    @Test
    public void testGetTrackedEntityInstancesByOu()
    {
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceService.getTrackedEntityInstances( organisationUnit, null, null );
        assertEquals( 2, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA1 ) );
        assertTrue( entityInstances.contains( entityInstanceA3 ) );
    }

    @Test
    public void testGetTrackedEntityInstancesByProgram()
    {
        programService.addProgram( programA );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceA3, programA, date, date, organisationUnit, null );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceService.getTrackedEntityInstances( programA );
        assertEquals( 2, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA1 ) );
        assertTrue( entityInstances.contains( entityInstanceA3 ) );
    }

    @Test
    public void testGetTrackedEntityInstancesbyOuProgram()
    {
        programService.addProgram( programA );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceA2, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceA3, programA, date, date, organisationUnit, null );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceService.getTrackedEntityInstances( organisationUnit, programA );
        assertEquals( 2, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA1 ) );
        assertTrue( entityInstances.contains( entityInstanceA3 ) );
    }

    @Test
    public void testGetTrackedEntityInstancesByAttribute()
    {
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB2 );

        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA3,
            entityInstanceAttribute );
        Set<TrackedEntityAttributeValue> entityInstanceAttributeValues = new HashSet<TrackedEntityAttributeValue>();
        entityInstanceAttributeValues.add( attributeValue );

        entityInstanceService.createTrackedEntityInstance( entityInstanceA3, null, null, entityInstanceAttributeValues );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceService.getTrackedEntityInstance( attributeId, "AttributeA" );

        assertEquals( 1, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA3 ) );

        TrackedEntityInstance entityInstance = entityInstances.iterator().next();
        assertEquals( 1, entityInstance.getAttributeValues().size() );
        assertTrue( entityInstance.getAttributeValues().contains( attributeValue ) );
    }

    @Test
    public void testGetTrackedEntityInstancesByProgramOu()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB2 );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceA2, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB2, programB, date, date, organisationUnit, null );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceService.getTrackedEntityInstances( organisationUnit, programA, 0,
            100 );

        assertEquals( 2, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA1 ) );
        assertTrue( entityInstances.contains( entityInstanceB1 ) );

        entityInstances = entityInstanceService.getTrackedEntityInstances( organisationUnit, programB, 0, 100 );

        assertEquals( 1, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceB2 ) );
    }

    @Test
    public void testGetRepresentatives()
    {
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );

        entityInstanceA1.setRepresentative( entityInstanceB1 );
        entityInstanceA2.setRepresentative( entityInstanceB1 );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );

        assertEquals( 2, entityInstanceService.getRepresentatives( entityInstanceB1 ).size() );
    }

    @Test
    public void testCreateTrackedEntityInstanceAndRelative()
    {
        int idB = entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );

        RelationshipType relationshipType = createRelationshipType( 'A' );
        int relationshipTypeId = relationshipTypeService.addRelationshipType( relationshipType );

        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA1,
            entityInstanceAttribute );
        Set<TrackedEntityAttributeValue> entityInstanceAttributeValues = new HashSet<TrackedEntityAttributeValue>();
        entityInstanceAttributeValues.add( attributeValue );

        int idA = entityInstanceService.createTrackedEntityInstance( entityInstanceA1, idB, relationshipTypeId, entityInstanceAttributeValues );
        assertNotNull( entityInstanceService.getTrackedEntityInstance( idA ) );
    }

    @Test
    public void testUpdateTrackedEntityInstanceAndRelative()
    {
        int idB = entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );

        RelationshipType relationshipType = createRelationshipType( 'A' );
        int relationshipTypeId = relationshipTypeService.addRelationshipType( relationshipType );

        entityInstanceA3.setName( "B" );
        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA3,
            entityInstanceAttribute );
        Set<TrackedEntityAttributeValue> entityInstanceAttributeValues = new HashSet<TrackedEntityAttributeValue>();
        entityInstanceAttributeValues.add( attributeValue );
        int idA = entityInstanceService.createTrackedEntityInstance( entityInstanceA3, idB, relationshipTypeId, entityInstanceAttributeValues );
        assertNotNull( entityInstanceService.getTrackedEntityInstance( idA ) );

        attributeValue.setValue( "AttributeB" );
        List<TrackedEntityAttributeValue> attributeValues = new ArrayList<TrackedEntityAttributeValue>();
        attributeValues.add( attributeValue );

        entityInstanceService.updateTrackedEntityInstance( entityInstanceA3, idB, relationshipTypeId, attributeValues,
            new ArrayList<TrackedEntityAttributeValue>(), new ArrayList<TrackedEntityAttributeValue>() );
        assertEquals( "B", entityInstanceService.getTrackedEntityInstance( idA ).getName() );
    }

    @Test
    public void testCountGetTrackedEntityInstancesByOrgUnit()
    {
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );

        assertEquals( 2, entityInstanceService.countGetTrackedEntityInstancesByOrgUnit( organisationUnit ) );
    }

    @Test
    public void testCountGetTrackedEntityInstancesByOrgUnitProgram()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB2 );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceA2, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB2, programB, date, date, organisationUnit, null );

        assertEquals( 2, entityInstanceService.countGetTrackedEntityInstancesByOrgUnitProgram( organisationUnit, programA ) );
        assertEquals( 1, entityInstanceService.countGetTrackedEntityInstancesByOrgUnitProgram( organisationUnit, programB ) );
    }

    @Test
    public void testSearchTrackedEntityInstances()
    {
        int idA = programService.addProgram( programA );
        programService.addProgram( programB );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB2 );

        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA3,
            entityInstanceAttribute );
        attributeValueService.saveTrackedEntityAttributeValue( attributeValue );
        entityInstanceA3.getAttributeValues().add( attributeValue );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA3, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB1, programA, date, date, organisationUnit, null );

        List<String> searchKeys = new ArrayList<String>();
        searchKeys.add( TrackedEntityInstance.PREFIX_TRACKED_ENTITY_ATTRIBUTE + TrackedEntityInstance.SEARCH_SAPERATE
            + attributeId + TrackedEntityInstance.SEARCH_SAPERATE + "a" );
        searchKeys.add( TrackedEntityInstance.PREFIX_PROGRAM + TrackedEntityInstance.SEARCH_SAPERATE + idA );

        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();
        orgunits.add( organisationUnit );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceService.searchTrackedEntityInstances( searchKeys, orgunits, null,
            null, ProgramStageInstance.ACTIVE_STATUS, null, null );

        assertEquals( 1, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA3 ) );
    }

    @Test
    public void testCountSearchEntityInstances()
    {
        int idA = programService.addProgram( programA );
        programService.addProgram( programB );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB2 );

        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA3,
            entityInstanceAttribute );
        attributeValueService.saveTrackedEntityAttributeValue( attributeValue );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA3, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB1, programA, date, date, organisationUnit, null );

        List<String> searchKeys = new ArrayList<String>();
        searchKeys.add( TrackedEntityInstance.PREFIX_TRACKED_ENTITY_ATTRIBUTE + TrackedEntityInstance.SEARCH_SAPERATE
            + attributeId + TrackedEntityInstance.SEARCH_SAPERATE + "a" );
        searchKeys.add( TrackedEntityInstance.PREFIX_PROGRAM + TrackedEntityInstance.SEARCH_SAPERATE + idA );

        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();
        orgunits.add( organisationUnit );

        assertEquals( 1,
            entityInstanceService.countSearchTrackedEntityInstances( searchKeys, orgunits, null, ProgramStageInstance.ACTIVE_STATUS ) );
    }

    @Test
    public void testGetTrackedEntityInstancePhoneNumbers()
    {
        int idA = programService.addProgram( programA );
        programService.addProgram( programB );

        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceB2 );

        TrackedEntityAttribute phoneAttribute = createTrackedEntityAttribute( 'B' );
        phoneAttribute.setValueType( TrackedEntityAttribute.TYPE_PHONE_NUMBER );
        attributeService.addTrackedEntityAttribute( phoneAttribute );

        Set<TrackedEntityAttributeValue> attributeValues = new HashSet<TrackedEntityAttributeValue>();
        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA3, phoneAttribute );
        attributeValue.setValue( "123456789" );
        attributeValueService.saveTrackedEntityAttributeValue( attributeValue );

        attributeValues.add( attributeValue );
        entityInstanceA3.addAttributeValue( attributeValue );
        entityInstanceService.updateTrackedEntityInstance( entityInstanceA3 );

        programInstanceService.enrollTrackedEntityInstance( entityInstanceA3, programA, date, date, organisationUnit, null );
        programInstanceService.enrollTrackedEntityInstance( entityInstanceB1, programA, date, date, organisationUnit, null );

        List<String> searchKeys = new ArrayList<String>();
        searchKeys.add( TrackedEntityInstance.PREFIX_PROGRAM + TrackedEntityInstance.SEARCH_SAPERATE + idA );

        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();
        orgunits.add( organisationUnit );

        Collection<String> phoneNumbers = entityInstanceService.getTrackedEntityInstancePhoneNumbers( searchKeys, orgunits, null,
            ProgramStageInstance.ACTIVE_STATUS, null, null );

        assertEquals( 1, phoneNumbers.size() );
    }

    @Test
    public void testGetTrackedEntityInstancesByPhone()
    {
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA1 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA2 );
        entityInstanceService.saveTrackedEntityInstance( entityInstanceA3 );

        TrackedEntityAttribute entityInstanceAttribute = createTrackedEntityAttribute( 'B' );
        entityInstanceAttribute.setValueType( TrackedEntityAttribute.TYPE_PHONE_NUMBER );
        attributeService.addTrackedEntityAttribute( entityInstanceAttribute );

        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA1,
            entityInstanceAttribute );
        attributeValue.setValue( "123456789" );
        attributeValueService.saveTrackedEntityAttributeValue( attributeValue );

        entityInstanceA1.addAttributeValue( attributeValue );
        entityInstanceService.updateTrackedEntityInstance( entityInstanceA1 );

        attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA2, entityInstanceAttribute );
        attributeValue.setValue( "123456789" );
        attributeValueService.saveTrackedEntityAttributeValue( attributeValue );
        entityInstanceA2.addAttributeValue( attributeValue );
        entityInstanceService.updateTrackedEntityInstance( entityInstanceA2 );

        Collection<TrackedEntityInstance> entityInstances = entityInstanceService.getTrackedEntityInstancesByPhone(
            "123456789", null, null );
        assertEquals( 2, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA1 ) );
        assertTrue( entityInstances.contains( entityInstanceA2 ) );
    }

}
