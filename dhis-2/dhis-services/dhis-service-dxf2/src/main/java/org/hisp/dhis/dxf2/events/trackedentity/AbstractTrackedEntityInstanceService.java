package org.hisp.dhis.dxf2.events.trackedentity;

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

import com.google.common.collect.Lists;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.common.QueryOperator;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.trackedentity.TrackedEntity;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams;
import org.hisp.dhis.trackedentity.TrackedEntityService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private org.hisp.dhis.trackedentity.TrackedEntityInstanceService teiService;

    @Autowired
    private TrackedEntityAttributeValueService attributeValueService;

    @Autowired
    private TrackedEntityAttributeService trackedEntityAttributeService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private TrackedEntityService trackedEntityService;

    @Autowired
    private TrackedEntityAttributeValueService trackedEntityAttributeValueService;

    @Autowired
    private org.hisp.dhis.trackedentity.TrackedEntityInstanceService entityInstanceService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private UserService userService;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public TrackedEntityInstance getTrackedEntityInstance( String uid )
    {
        return getTrackedEntityInstance( teiService.getTrackedEntityInstance( uid ) );
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
        trackedEntityInstance.setTrackedEntity( entityInstance.getTrackedEntity().getUid() );

        Collection<Relationship> relationships = relationshipService
            .getRelationshipsForTrackedEntityInstance( entityInstance );

        for ( Relationship entityRelationship : relationships )
        {
            org.hisp.dhis.dxf2.events.trackedentity.Relationship relationship = new org.hisp.dhis.dxf2.events.trackedentity.Relationship();
            relationship.setDisplayName( entityRelationship.getRelationshipType().getDisplayName() );

            if ( entityInstance.getUid().equals( entityRelationship.getEntityInstanceA().getUid() ) )
            {
                relationship.setTrackedEntityInstance( entityRelationship.getEntityInstanceB().getUid() );
            }
            else
            {
                relationship.setTrackedEntityInstance( entityRelationship.getEntityInstanceA().getUid() );
            }

            relationship.setRelationship( entityRelationship.getRelationshipType().getUid() );

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

        teiService.addTrackedEntityInstance( entityInstance );

        updateRelationships( trackedEntityInstance, entityInstance );
        updateAttributeValues( trackedEntityInstance, entityInstance );
        teiService.updateTrackedEntityInstance( entityInstance );

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
        teiService.updateTrackedEntityInstance( entityInstance );

        updateRelationships( trackedEntityInstance, entityInstance );
        updateAttributeValues( trackedEntityInstance, entityInstance );
        teiService.updateTrackedEntityInstance( entityInstance );

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
        org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = teiService.getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() );

        if ( entityInstance != null )
        {
            teiService.deleteTrackedEntityInstance( entityInstance );
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

        for ( Attribute attribute : trackedEntityInstance.getAttributes() )
        {
            TrackedEntityAttribute entityAttribute = manager.get( TrackedEntityAttribute.class,
                attribute.getAttribute() );

            if ( entityAttribute == null )
            {
                importConflicts.add( new ImportConflict( "Attribute.attribute", "Invalid attribute "
                    + attribute.getAttribute() ) );
                continue;
            }

            if ( entityAttribute.isUnique() )
            {
                OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, trackedEntityInstance.getOrgUnit() );
                org.hisp.dhis.trackedentity.TrackedEntityInstance tei = teiService.getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() );

                importConflicts.addAll(
                    checkScope( tei, entityAttribute, attribute.getValue(), organisationUnit )
                );
            }

            importConflicts.addAll( validateAttributeType( attribute ) );
        }

        return importConflicts;
    }

    private List<ImportConflict> checkScope( org.hisp.dhis.trackedentity.TrackedEntityInstance tei, TrackedEntityAttribute attribute, String value, OrganisationUnit organisationUnit )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();

        QueryItem queryItem = new QueryItem( attribute, QueryOperator.EQ, value, false );
        params.addAttribute( queryItem );

        if ( attribute.getOrgunitScope() )
        {
            params.getOrganisationUnits().add( organisationUnit );
        }
        else
        {
            params.setOrganisationUnitMode( OrganisationUnitSelectionMode.ALL );
        }

        Grid instances = teiService.getTrackedEntityInstances( params );

        if ( instances.getHeight() == 0 || (instances.getHeight() == 1 && instances.getRow( 0 ).contains( tei.getUid() )) )
        {
            return importConflicts;
        }

        importConflicts.add( new ImportConflict( "Attribute.value", "Non-unique attribute value '" + value + "' for attribute " + attribute.getUid() ) );

        return importConflicts;
    }

    private List<ImportConflict> checkRelationships( TrackedEntityInstance trackedEntityInstance )
    {
        List<ImportConflict> importConflicts = new ArrayList<ImportConflict>();

        for ( org.hisp.dhis.dxf2.events.trackedentity.Relationship relationship : trackedEntityInstance.getRelationships() )
        {
            RelationshipType relationshipType = manager.get( RelationshipType.class, relationship.getRelationship() );

            if ( relationshipType == null )
            {
                importConflicts.add( new ImportConflict( "Relationship.type", "Invalid type "
                    + relationship.getRelationship() ) );
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

                attributeValueService.addTrackedEntityAttributeValue( attributeValue );
            }
        }
    }

    private void updateRelationships( TrackedEntityInstance trackedEntityInstance, org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance )
    {
        for ( org.hisp.dhis.dxf2.events.trackedentity.Relationship relationship : trackedEntityInstance.getRelationships() )
        {
            org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstanceB = manager.get( org.hisp.dhis.trackedentity.TrackedEntityInstance.class, relationship.getTrackedEntityInstance() );
            RelationshipType relationshipType = manager.get( RelationshipType.class, relationship.getRelationship() );

            Relationship entityRelationship = new Relationship();
            entityRelationship.setEntityInstanceA( entityInstance );
            entityRelationship.setEntityInstanceB( entityInstanceB );
            entityRelationship.setRelationshipType( relationshipType );

            relationshipService.addRelationship( entityRelationship );
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

        teiService.updateTrackedEntityInstance( entityInstance );
    }


    private List<ImportConflict> validateAttributeType( Attribute attribute )
    {
        List<ImportConflict> importConflicts = Lists.newArrayList();
        TrackedEntityAttribute teAttribute = trackedEntityAttributeService.getTrackedEntityAttribute( attribute.getAttribute() );

        if ( teAttribute == null )
        {
            importConflicts.add( new ImportConflict( "Attribute.attribute", "Does not point to a valid attribute." ) );
            return importConflicts;
        }

        if ( attribute.getValue().length() > 255 )
        {
            importConflicts.add( new ImportConflict( "Attribute.value", "Value length is greater than 256 chars." ) );
        }

        if ( TrackedEntityAttribute.TYPE_NUMBER.equals( teAttribute.getValueType() ) && !MathUtils.isNumeric( attribute.getValue() ) )
        {
            importConflicts.add( new ImportConflict( "Attribute.value", "Value is not numeric." ) );
        }
        else if ( TrackedEntityAttribute.TYPE_BOOL.equals( teAttribute.getValueType() ) && !MathUtils.isBool( attribute.getValue() ) )
        {
            importConflicts.add( new ImportConflict( "Attribute.value", "Value is not boolean." ) );
        }
        else if ( TrackedEntityAttribute.TYPE_DATE.equals( teAttribute.getValueType() ) && !DateUtils.dateIsValid( attribute.getValue() ) )
        {
            importConflicts.add( new ImportConflict( "Attribute.value", "Value is not date." ) );
        }
        else if ( TrackedEntityAttribute.TYPE_TRUE_ONLY.equals( teAttribute.getValueType() ) && "true".equals( attribute.getValue() ) )
        {
            importConflicts.add( new ImportConflict( "Attribute.value", "Value is not true (true-only value type)." ) );
        }
        else if ( TrackedEntityAttribute.TYPE_USERS.equals( teAttribute.getValueType() ) )
        {
            if ( userService.getUserCredentialsByUsername( attribute.getValue() ) == null )
            {
                importConflicts.add( new ImportConflict( "Attribute.value", "Value is not pointing to a valid username." ) );
            }
        }
        else if ( TrackedEntityAttribute.TYPE_OPTION_SET.equals( teAttribute.getValueType() ) && !teAttribute.getOptionSet().getOptions().contains( attribute.getValue() ) )
        {
            importConflicts.add( new ImportConflict( "Attribute.value", "Value is not pointing to a valid option." ) );
        }

        return importConflicts;
    }
}
