package org.hisp.dhis.dxf2.events.trackedentity;

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

import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.trackedentity.TrackedEntity;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractTrackedEntityInstanceService
    implements TrackedEntityInstanceService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private org.hisp.dhis.trackedentity.TrackedEntityInstanceService trackedEntityInstanceService;

    @Autowired
    private TrackedEntityAttributeValueService attributeValueService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private TrackedEntityService trackedEntityService;

    @Autowired
    private IdentifiableObjectManager manager;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public TrackedEntityInstance getTrackedEntityInstance( String uid )
    {
        return getTrackedEntityInstance( trackedEntityInstanceService.getTrackedEntityInstance( uid ) );
    }

    @Override
    public TrackedEntityInstance getTrackedEntityInstance( org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance )
    {
        if ( entityInstance == null )
        {
            return null;
        }

        TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setTrackedEntityInstance( entityInstance.getUid() );
        trackedEntityInstance.setOrgUnit( entityInstance.getOrganisationUnit().getUid() );

        Collection<Relationship> relationships = relationshipService
            .getRelationshipsForTrackedEntityInstance( entityInstance );

        for ( Relationship entityRelationship : relationships )
        {
            org.hisp.dhis.dxf2.events.trackedentity.Relationship relationship = new org.hisp.dhis.dxf2.events.trackedentity.Relationship();
            relationship.setDisplayName( entityRelationship.getRelationshipType().getDisplayName() );
            relationship.setTrackedEntityInstance( entityRelationship.getEntityInstanceA().getUid() );
            relationship.setType( entityRelationship.getRelationshipType().getUid() );

            trackedEntityInstance.getRelationships().add( relationship );
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

            trackedEntityInstance.getAttributes().add( attribute );
        }

        return trackedEntityInstance;
    }

    public org.hisp.dhis.trackedentity.TrackedEntityInstance getTrackedEntityInstance( TrackedEntityInstance trackedEntityInstance )
    {
        Assert.hasText( trackedEntityInstance.getOrgUnit() );

        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = new org.hisp.dhis.trackedentity.TrackedEntityInstance();

        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, trackedEntityInstance.getOrgUnit() );
        Assert.notNull( organisationUnit );
        entityInstance.setOrganisationUnit( organisationUnit );

        TrackedEntity trackedEntity = trackedEntityService.getTrackedEntity( trackedEntityInstance.getTrackedEntity() );
        entityInstance.setTrackedEntity( trackedEntity );

        return entityInstance;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary addTrackedEntityInstance( TrackedEntityInstance trackedEntityInstance )
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.setDataValueCount( null );

        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        importConflicts.addAll( checkTrackedEntity( trackedEntityInstance ) );
        importConflicts.addAll( checkAttributes( trackedEntityInstance ) );

        importSummary.setConflicts( importConflicts );

        if ( !importConflicts.isEmpty() )
        {
            importSummary.setStatus( ImportStatus.ERROR );
            importSummary.getImportCount().incrementIgnored();
            return importSummary;
        }

        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = getTrackedEntityInstance( trackedEntityInstance );
        trackedEntityInstanceService.addTrackedEntityInstance( entityInstance );

        updateAttributeValues( trackedEntityInstance, entityInstance );
        trackedEntityInstanceService.updateTrackedEntityInstance( entityInstance );

        importSummary.setStatus( ImportStatus.SUCCESS );
        importSummary.setReference( entityInstance.getUid() );
        importSummary.getImportCount().incrementImported();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary updateTrackedEntityInstance( TrackedEntityInstance trackedEntityInstance )
    {
        ImportSummary importSummary = new ImportSummary();
        importSummary.setDataValueCount( null );

        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        importConflicts.addAll( checkRelationships( trackedEntityInstance ) );
        importConflicts.addAll( checkAttributes( trackedEntityInstance ) );

        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = manager.get( org.hisp.dhis.trackedentity.TrackedEntityInstance.class, trackedEntityInstance.getTrackedEntityInstance() );

        if ( entityInstance == null )
        {
            importConflicts.add( new ImportConflict( "TrackedEntityInstance", "trackedEntityInstance " + trackedEntityInstance.getTrackedEntityInstance()
                + " does not point to valid trackedEntityInstance" ) );
        }

        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, trackedEntityInstance.getOrgUnit() );

        if ( organisationUnit == null )
        {
            importConflicts.add( new ImportConflict( "OrganisationUnit", "orgUnit " + trackedEntityInstance.getOrgUnit()
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
        trackedEntityInstanceService.updateTrackedEntityInstance( entityInstance );

        updateRelationships( trackedEntityInstance, entityInstance );
        updateAttributeValues( trackedEntityInstance, entityInstance );
        trackedEntityInstanceService.updateTrackedEntityInstance( entityInstance );

        importSummary.setStatus( ImportStatus.SUCCESS );
        importSummary.setReference( entityInstance.getUid() );
        importSummary.getImportCount().incrementUpdated();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deleteTrackedEntityInstance( TrackedEntityInstance trackedEntityInstance )
    {
        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = trackedEntityInstanceService.getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() );

        if ( entityInstance != null )
        {
            trackedEntityInstanceService.deleteTrackedEntityInstance( entityInstance );
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private List<ImportConflict> checkTrackedEntity( TrackedEntityInstance trackedEntityInstance )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        if ( trackedEntityInstance.getTrackedEntity() == null )
        {
            importConflicts.add( new ImportConflict( "TrackedEntityInstance.trackedEntity", "Missing required property trackedEntity" ) );
            return importConflicts;
        }

        TrackedEntity trackedEntity = trackedEntityService.getTrackedEntity( trackedEntityInstance.getTrackedEntity() );

        if ( trackedEntity == null )
        {
            importConflicts.add( new ImportConflict( "TrackedEntityInstance.trackedEntity", "Invalid trackedEntity" +
                trackedEntityInstance.getTrackedEntity() ) );
        }

        return importConflicts;
    }

    private List<ImportConflict> checkAttributes( TrackedEntityInstance trackedEntityInstance )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();
        Collection<TrackedEntityAttribute> entityAttributes = manager.getAll( TrackedEntityAttribute.class );
        Set<String> cache = new HashSet<String>();

        for ( Attribute attribute : trackedEntityInstance.getAttributes() )
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

        for ( Attribute attribute : trackedEntityInstance.getAttributes() )
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

    private List<ImportConflict> checkRelationships( TrackedEntityInstance trackedEntityInstance )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        for ( org.hisp.dhis.dxf2.events.trackedentity.Relationship relationship : trackedEntityInstance.getRelationships() )
        {
            RelationshipType relationshipType = manager.get( RelationshipType.class, relationship.getType() );

            if ( relationshipType == null )
            {
                importConflicts
                    .add( new ImportConflict( "Relationship.type", "Invalid type " + relationship.getType() ) );
            }

            org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = manager.get( org.hisp.dhis.trackedentity.TrackedEntityInstance.class, relationship.getTrackedEntityInstance() );

            if ( entityInstance == null )
            {
                importConflicts.add( new ImportConflict( "Relationship.trackedEntityInstance", "Invalid trackedEntityInstance "
                    + relationship.getTrackedEntityInstance() ) );
            }
        }

        return importConflicts;
    }

    private void updateAttributeValues( TrackedEntityInstance trackedEntityInstance, org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance )
    {
        for ( Attribute attribute : trackedEntityInstance.getAttributes() )
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

    private void updateRelationships( TrackedEntityInstance trackedEntityInstance, org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance )
    {
        for ( org.hisp.dhis.dxf2.events.trackedentity.Relationship relationship : trackedEntityInstance.getRelationships() )
        {
            org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstanceB = manager.get( org.hisp.dhis.trackedentity.TrackedEntityInstance.class, relationship.getTrackedEntityInstance() );
            RelationshipType relationshipType = manager.get( RelationshipType.class, relationship.getType() );

            Relationship entityRelationship = new Relationship();
            entityRelationship.setEntityInstanceA( entityInstance );
            entityRelationship.setEntityInstanceB( entityInstanceB );
            entityRelationship.setRelationshipType( relationshipType );

            relationshipService.saveRelationship( entityRelationship );
        }
    }

    private void removeRelationships( org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance )
    {
        Collection<Relationship> relationships = relationshipService
            .getRelationshipsForTrackedEntityInstance( entityInstance );

        for ( Relationship relationship : relationships )
        {
            relationshipService.deleteRelationship( relationship );
        }
    }

    private void removeAttributeValues( org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance )
    {
        for ( TrackedEntityAttributeValue trackedEntityAttributeValue : entityInstance.getAttributeValues() )
        {
            attributeValueService.deleteTrackedEntityAttributeValue( trackedEntityAttributeValue );
        }

        trackedEntityInstanceService.updateTrackedEntityInstance( entityInstance );
    }
}
