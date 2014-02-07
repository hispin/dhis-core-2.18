package org.hisp.dhis.dxf2.events.person;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractPersonService
    implements PersonService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

    @Autowired
    private TrackedEntityAttributeValueService attributeValueService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private IdentifiableObjectManager manager;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public Persons getPersons()
    {
        List<TrackedEntityInstance> entityInstances = new ArrayList<TrackedEntityInstance>(
            entityInstanceService.getAllTrackedEntityInstances() );
        return getPersons( entityInstances );
    }

    @Override
    public Persons getPersons( OrganisationUnit organisationUnit )
    {
        List<TrackedEntityInstance> entityInstances = new ArrayList<TrackedEntityInstance>(
            entityInstanceService.getTrackedEntityInstances( organisationUnit, null, null ) );
        return getPersons( entityInstances );
    }

    @Override
    public Persons getPersons( Program program )
    {
        List<TrackedEntityInstance> entityInstances = new ArrayList<TrackedEntityInstance>(
            entityInstanceService.getTrackedEntityInstances( program ) );
        return getPersons( entityInstances );
    }

    @Override
    public Persons getPersons( OrganisationUnit organisationUnit, Program program )
    {
        List<TrackedEntityInstance> entityInstances = new ArrayList<TrackedEntityInstance>(
            entityInstanceService.getTrackedEntityInstances( organisationUnit, program ) );
        return getPersons( entityInstances );
    }

    @Override
    public Persons getPersons( Collection<TrackedEntityInstance> entityInstances )
    {
        Persons persons = new Persons();

        for ( TrackedEntityInstance entityInstance : entityInstances )
        {
            persons.getPersons().add( getPerson( entityInstance ) );
        }

        return persons;
    }

    @Override
    public Person getPerson( String uid )
    {
        return getPerson( entityInstanceService.getTrackedEntityInstance( uid ) );
    }

    @Override
    public Person getPerson( TrackedEntityInstance entityInstance )
    {
        if ( entityInstance == null )
        {
            return null;
        }

        Person person = new Person();
        person.setPerson( entityInstance.getUid() );
        person.setOrgUnit( entityInstance.getOrganisationUnit().getUid() );

        Collection<Relationship> relationships = relationshipService
            .getRelationshipsForTrackedEntityInstance( entityInstance );

        for ( Relationship entityRelationship : relationships )
        {
            org.hisp.dhis.dxf2.events.person.Relationship relationship = new org.hisp.dhis.dxf2.events.person.Relationship();
            relationship.setDisplayName( entityRelationship.getRelationshipType().getDisplayName() );
            relationship.setPerson( entityRelationship.getEntityInstanceA().getUid() );
            relationship.setType( entityRelationship.getRelationshipType().getUid() );

            person.getRelationships().add( relationship );
        }

        Collection<TrackedEntityAttributeValue> attributeValues = attributeValueService
            .getTrackedEntityAttributeValues( entityInstance );

        for ( TrackedEntityAttributeValue attributeValue : attributeValues )
        {
            Attribute attribute = new Attribute();

            attribute.setDisplayName( attributeValue.getAttribute().getDisplayName() );
            attribute.setAttribute( attributeValue.getAttribute().getUid() );
            attribute.setType( attributeValue.getAttribute().getValueType() );
            attribute.setCode( attributeValue.getAttribute().getCode() );
            attribute.setValue( attributeValue.getValue() );

            person.getAttributes().add( attribute );
        }

        return person;
    }

    public TrackedEntityInstance getTrackedEntityInstance( Person person )
    {
        Assert.hasText( person.getOrgUnit() );

        TrackedEntityInstance entityInstance = new TrackedEntityInstance();

        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, person.getOrgUnit() );
        Assert.notNull( organisationUnit );

        entityInstance.setOrganisationUnit( organisationUnit );

        return entityInstance;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary savePerson( Person person )
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.setDataValueCount( null );

        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        importConflicts.addAll( checkAttributes( person ) );

        importSummary.setConflicts( importConflicts );

        if ( !importConflicts.isEmpty() )
        {
            importSummary.setStatus( ImportStatus.ERROR );
            importSummary.getImportCount().incrementIgnored();
            return importSummary;
        }

        TrackedEntityInstance entityInstance = getTrackedEntityInstance( person );
        entityInstanceService.saveTrackedEntityInstance( entityInstance );

        updateAttributeValues( person, entityInstance );
        entityInstanceService.updateTrackedEntityInstance( entityInstance );

        importSummary.setStatus( ImportStatus.SUCCESS );
        importSummary.setReference( entityInstance.getUid() );
        importSummary.getImportCount().incrementImported();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary updatePerson( Person person )
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.setDataValueCount( null );

        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        importConflicts.addAll( checkRelationships( person ) );
        importConflicts.addAll( checkAttributes( person ) );

        TrackedEntityInstance entityInstance = manager.get( TrackedEntityInstance.class, person.getPerson() );

        if ( entityInstance == null )
        {
            importConflicts.add( new ImportConflict( "Person", "person " + person.getPerson()
                + " does not point to valid person" ) );
        }

        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, person.getOrgUnit() );

        if ( organisationUnit == null )
        {
            importConflicts.add( new ImportConflict( "OrganisationUnit", "orgUnit " + person.getOrgUnit()
                + " does not point to valid organisation unit" ) );
        }

        importSummary.setConflicts( importConflicts );

        if ( !importConflicts.isEmpty() )
        {
            importSummary.setStatus( ImportStatus.ERROR );
            importSummary.getImportCount().incrementIgnored();
            return importSummary;
        }

        removeRelationships( entityInstance );
        removeAttributeValues( entityInstance );
        entityInstanceService.updateTrackedEntityInstance( entityInstance );

        updateRelationships( person, entityInstance );
        updateAttributeValues( person, entityInstance );
        entityInstanceService.updateTrackedEntityInstance( entityInstance );

        importSummary.setStatus( ImportStatus.SUCCESS );
        importSummary.setReference( entityInstance.getUid() );
        importSummary.getImportCount().incrementImported();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deletePerson( Person person )
    {
        TrackedEntityInstance entityInstance = entityInstanceService.getTrackedEntityInstance( person.getPerson() );

        if ( entityInstance != null )
        {
            entityInstanceService.deleteTrackedEntityInstance( entityInstance );
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private List<ImportConflict> checkAttributes( Person person )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        Collection<TrackedEntityAttribute> entityAttributes = manager.getAll( TrackedEntityAttribute.class );
        Set<String> cache = new HashSet<String>();

        for ( Attribute attribute : person.getAttributes() )
        {
            if ( attribute.getValue() != null )
            {
                cache.add( attribute.getAttribute() );
            }
        }

        for ( TrackedEntityAttribute entityAttribute : entityAttributes )
        {
            if ( entityAttribute.isMandatory() )
            {
                if ( !cache.contains( entityAttribute.getUid() ) )
                {
                    importConflicts.add( new ImportConflict( "Attribute.type", "Missing required attribute type "
                        + entityAttribute.getUid() ) );
                }
            }
        }

        for ( Attribute attribute : person.getAttributes() )
        {
            TrackedEntityAttribute entityAttribute = manager.get( TrackedEntityAttribute.class,
                attribute.getAttribute() );

            if ( entityAttribute == null )
            {
                importConflicts
                    .add( new ImportConflict( "Attribute.type", "Invalid type " + attribute.getAttribute() ) );
            }
        }

        return importConflicts;
    }

    private List<ImportConflict> checkRelationships( Person person )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        for ( org.hisp.dhis.dxf2.events.person.Relationship relationship : person.getRelationships() )
        {
            RelationshipType relationshipType = manager.get( RelationshipType.class, relationship.getType() );

            if ( relationshipType == null )
            {
                importConflicts
                    .add( new ImportConflict( "Relationship.type", "Invalid type " + relationship.getType() ) );
            }

            TrackedEntityInstance entityInstance = manager.get( TrackedEntityInstance.class, relationship.getPerson() );

            if ( entityInstance == null )
            {
                importConflicts.add( new ImportConflict( "Relationship.person", "Invalid person "
                    + relationship.getPerson() ) );
            }
        }

        return importConflicts;
    }

    private void updateAttributeValues( Person person, TrackedEntityInstance entityInstance )
    {
        for ( Attribute attribute : person.getAttributes() )
        {
            TrackedEntityAttribute entityAttribute = manager.get( TrackedEntityAttribute.class,
                attribute.getAttribute() );

            if ( entityAttribute != null )
            {
                TrackedEntityAttributeValue attributeValue = new TrackedEntityAttributeValue();
                attributeValue.setEntityInstance( entityInstance );
                attributeValue.setValue( attribute.getValue() );
                attributeValue.setAttribute( entityAttribute );

                attributeValueService.saveTrackedEntityAttributeValue( attributeValue );
            }
        }
    }

    private void updateRelationships( Person person, TrackedEntityInstance entityInstance )
    {
        for ( org.hisp.dhis.dxf2.events.person.Relationship relationship : person.getRelationships() )
        {
            TrackedEntityInstance entityInstanceB = manager.get( TrackedEntityInstance.class, relationship.getPerson() );
            RelationshipType relationshipType = manager.get( RelationshipType.class, relationship.getType() );

            Relationship entityRelationship = new Relationship();
            entityRelationship.setEntityInstanceA( entityInstance );
            entityRelationship.setEntityInstanceB( entityInstanceB );
            entityRelationship.setRelationshipType( relationshipType );

            relationshipService.saveRelationship( entityRelationship );
        }
    }

    private void removeRelationships( TrackedEntityInstance entityInstance )
    {
        Collection<Relationship> relationships = relationshipService
            .getRelationshipsForTrackedEntityInstance( entityInstance );

        for ( Relationship relationship : relationships )
        {
            relationshipService.deleteRelationship( relationship );
        }
    }

    private void removeAttributeValues( TrackedEntityInstance entityInstance )
    {
        attributeValueService.deleteTrackedEntityAttributeValue( entityInstance );
        entityInstanceService.updateTrackedEntityInstance( entityInstance );
    }
}
