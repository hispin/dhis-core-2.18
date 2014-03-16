package org.hisp.dhis.trackedentity;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.hisp.dhis.validation.ValidationCriteria;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultTrackedEntityInstanceService
    implements TrackedEntityInstanceService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityInstanceStore entityInstanceStore;

    public void setEntityInstanceStore( TrackedEntityInstanceStore entityInstanceStore )
    {
        this.entityInstanceStore = entityInstanceStore;
    }

    private TrackedEntityAttributeValueService attributeValueService;

    public void setAttributeValueService( TrackedEntityAttributeValueService attributeValueService )
    {
        this.attributeValueService = attributeValueService;
    }

    private TrackedEntityAttributeService attributeService;

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private RelationshipService relationshipService;

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    private RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public int saveTrackedEntityInstance( TrackedEntityInstance instance )
    {
        return entityInstanceStore.save( instance );
    }

    @Override
    public int createTrackedEntityInstance( TrackedEntityInstance instance, Integer representativeId,
        Integer relationshipTypeId, Set<TrackedEntityAttributeValue> attributeValues )
    {
        int id = saveTrackedEntityInstance( instance );

        for ( TrackedEntityAttributeValue pav : attributeValues )
        {
            attributeValueService.saveTrackedEntityAttributeValue( pav );
            instance.getAttributeValues().add( pav );
        }

        // ---------------------------------------------------------------------
        // If under age, save representative information
        // ---------------------------------------------------------------------

        if ( representativeId != null )
        {
            TrackedEntityInstance representative = entityInstanceStore.get( representativeId );
            if ( representative != null )
            {
                instance.setRepresentative( representative );

                Relationship rel = new Relationship();
                rel.setEntityInstanceA( representative );
                rel.setEntityInstanceB( instance );

                if ( relationshipTypeId != null )
                {
                    RelationshipType relType = relationshipTypeService.getRelationshipType( relationshipTypeId );
                    if ( relType != null )
                    {
                        rel.setRelationshipType( relType );
                        relationshipService.saveRelationship( rel );
                    }
                }
            }
        }

        updateTrackedEntityInstance( instance ); // Save instance to update
                                                 // associations

        return id;
    }

    @Override
    public void updateTrackedEntityInstance( TrackedEntityInstance instance )
    {
        entityInstanceStore.update( instance );
    }

    @Override
    public void deleteTrackedEntityInstance( TrackedEntityInstance instance )
    {
        entityInstanceStore.delete( instance );
    }

    @Override
    public TrackedEntityInstance getTrackedEntityInstance( int id )
    {
        return entityInstanceStore.get( id );
    }

    @Override
    public TrackedEntityInstance getTrackedEntityInstance( String uid )
    {
        return entityInstanceStore.getByUid( uid );
    }

    @Override
    public Collection<TrackedEntityInstance> getAllTrackedEntityInstances()
    {
        return entityInstanceStore.getAll();
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstancesForMobile( String searchText, int orgUnitId )
    {
        Set<TrackedEntityInstance> entityInstances = new HashSet<TrackedEntityInstance>();
        entityInstances.addAll( getTrackedEntityInstancesByPhone( searchText, 0, Integer.MAX_VALUE ) );

        // if an org-unit has been selected, filter out every instance that has
        // a
        // different org-unit
        if ( orgUnitId != 0 )
        {
            Set<TrackedEntityInstance> toRemoveList = new HashSet<TrackedEntityInstance>();

            for ( TrackedEntityInstance instance : entityInstances )
            {
                if ( instance.getOrganisationUnit().getId() != orgUnitId )
                {
                    toRemoveList.add( instance );
                }
            }

            entityInstances.removeAll( toRemoveList );
        }

        return entityInstances;
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstances( OrganisationUnit organisationUnit, Integer min,
        Integer max )
    {
        return entityInstanceStore.getByOrgUnit( organisationUnit, min, max );
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstances( Program program )
    {
        return entityInstanceStore.getByProgram( program, 0, Integer.MAX_VALUE );
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstances( OrganisationUnit organisationUnit,
        Program program )
    {
        return entityInstanceStore.getByOrgUnitProgram( organisationUnit, program, 0, Integer.MAX_VALUE );
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstance( Integer attributeId, String value )
    {
        TrackedEntityAttribute attribute = attributeService.getTrackedEntityAttribute( attributeId );
        if ( attribute != null )
        {
            return attributeValueService.getTrackedEntityInstance( attribute, value );
        }

        return null;
    }

    @Override
    public Collection<TrackedEntityInstance> sortTrackedEntityInstancesByAttribute(
        Collection<TrackedEntityInstance> entityInstances, TrackedEntityAttribute attribute )
    {
        Collection<TrackedEntityInstance> sortedTrackedEntityInstances = new ArrayList<TrackedEntityInstance>();

        // ---------------------------------------------------------------------
        // Better to fetch all attribute values at once than fetching the
        // required attribute value of each instance using loop
        // ---------------------------------------------------------------------

        Collection<TrackedEntityAttributeValue> attributeValues = attributeValueService
            .getTrackedEntityAttributeValues( entityInstances );

        if ( attributeValues != null )
        {
            for ( TrackedEntityAttributeValue attributeValue : attributeValues )
            {
                if ( attribute == attributeValue.getAttribute() )
                {
                    sortedTrackedEntityInstances.add( attributeValue.getEntityInstance() );
                    entityInstances.remove( attributeValue.getEntityInstance() );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Make sure all entityInstances are in the sorted list - because all
        // entityInstances might not have the sorting attribute/value
        // ---------------------------------------------------------------------

        sortedTrackedEntityInstances.addAll( entityInstances );

        return sortedTrackedEntityInstances;
    }

    @Override
    public int countGetTrackedEntityInstancesByOrgUnit( OrganisationUnit organisationUnit )
    {
        return entityInstanceStore.countListTrackedEntityInstanceByOrgunit( organisationUnit );
    }

    @Override
    public void updateTrackedEntityInstance( TrackedEntityInstance instance, Integer representativeId,
        Integer relationshipTypeId, List<TrackedEntityAttributeValue> valuesForSave,
        List<TrackedEntityAttributeValue> valuesForUpdate, Collection<TrackedEntityAttributeValue> valuesForDelete )
    {
        entityInstanceStore.update( instance );

        for ( TrackedEntityAttributeValue av : valuesForSave )
        {
            attributeValueService.saveTrackedEntityAttributeValue( av );
        }

        for ( TrackedEntityAttributeValue av : valuesForUpdate )
        {
            attributeValueService.updateTrackedEntityAttributeValue( av );
        }

        for ( TrackedEntityAttributeValue av : valuesForDelete )
        {
            attributeValueService.deleteTrackedEntityAttributeValue( av );
        }

        if ( shouldSaveRepresentativeInformation( instance, representativeId ) )
        {
            TrackedEntityInstance representative = entityInstanceStore.get( representativeId );

            if ( representative != null )
            {
                instance.setRepresentative( representative );

                Relationship rel = new Relationship();
                rel.setEntityInstanceA( representative );
                rel.setEntityInstanceB( instance );

                if ( relationshipTypeId != null )
                {
                    RelationshipType relType = relationshipTypeService.getRelationshipType( relationshipTypeId );
                    if ( relType != null )
                    {
                        rel.setRelationshipType( relType );
                        relationshipService.saveRelationship( rel );
                    }
                }
            }
        }
    }

    private boolean shouldSaveRepresentativeInformation( TrackedEntityInstance instance, Integer representativeId )
    {
        if ( representativeId == null )
        {
            return false;
        }

        return instance.getRepresentative() == null || !(instance.getRepresentative().getId() == representativeId);
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstances( OrganisationUnit organisationUnit,
        Program program, Integer min, Integer max )
    {
        return entityInstanceStore.getByOrgUnitProgram( organisationUnit, program, min, max );
    }

    @Override
    public int countGetTrackedEntityInstancesByOrgUnitProgram( OrganisationUnit organisationUnit, Program program )
    {
        return entityInstanceStore.countGetTrackedEntityInstancesByOrgUnitProgram( organisationUnit, program );
    }

    @Override
    public Object getObjectValue( String property, String value, I18nFormat format )
    {
        try
        {
            Type type = TrackedEntityInstance.class.getMethod( "get" + StringUtils.capitalize( property ) )
                .getReturnType();

            if ( type == Integer.class || type == Integer.TYPE )
            {
                return Integer.valueOf( value );
            }
            else if ( type.equals( Boolean.class ) || type == Boolean.TYPE )
            {
                return Boolean.valueOf( value );
            }
            else if ( type.equals( Date.class ) )
            {
                return format.parseDate( value.trim() );
            }
            else if ( type.equals( Character.class ) || type == Character.TYPE )
            {
                return Character.valueOf( value.charAt( 0 ) );
            }

            return value;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<TrackedEntityInstance> getRepresentatives( TrackedEntityInstance instance )
    {
        return entityInstanceStore.getRepresentatives( instance );
    }

    @Override
    public Collection<TrackedEntityInstance> searchTrackedEntityInstances( List<String> searchKeys,
        Collection<OrganisationUnit> orgunits, Boolean followup, Collection<TrackedEntityAttribute> attributes,
        Integer statusEnrollment, Integer min, Integer max )
    {
        return entityInstanceStore.search( searchKeys, orgunits, followup, attributes, statusEnrollment, min, max );
    }

    @Override
    public int countSearchTrackedEntityInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Integer statusEnrollment )
    {
        return entityInstanceStore.countSearch( searchKeys, orgunits, followup, statusEnrollment );
    }

    @Override
    public Collection<String> getTrackedEntityInstancePhoneNumbers( List<String> searchKeys,
        Collection<OrganisationUnit> orgunits, Boolean followup, Integer statusEnrollment, Integer min, Integer max )
    {
        Collection<TrackedEntityInstance> entityInstances = entityInstanceStore.search( searchKeys, orgunits, followup,
            null, statusEnrollment, min, max );
        Set<String> phoneNumbers = new HashSet<String>();

        for ( TrackedEntityInstance instance : entityInstances )
        {
            Collection<TrackedEntityAttributeValue> attributeValues = instance.getAttributeValues();
            if ( attributeValues != null )
            {
                for ( TrackedEntityAttributeValue attributeValue : attributeValues )
                {
                    if ( attributeValue.getAttribute().getValueType().equals( TrackedEntityAttribute.TYPE_PHONE_NUMBER ) )
                    {
                        phoneNumbers.add( attributeValue.getValue() );
                    }
                }
            }
        }

        return phoneNumbers;
    }

    @Override
    public List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max )
    {
        return entityInstanceStore.getProgramStageInstances( searchKeys, orgunits, followup, null, statusEnrollment,
            min, max );
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstancesByPhone( String phoneNumber, Integer min,
        Integer max )
    {
        return entityInstanceStore.getByPhoneNumber( phoneNumber, min, max );
    }

    @Override
    public Grid getScheduledEventsReport( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max, I18n i18n )
    {
        String startDate = "";
        String endDate = "";
        for ( String searchKey : searchKeys )
        {
            String[] keys = searchKey.split( "_" );
            if ( keys[0].equals( TrackedEntityInstance.PREFIX_PROGRAM_EVENT_BY_STATUS ) )
            {
                startDate = keys[2];
                endDate = keys[3];
            }
        }

        Grid grid = new ListGrid();
        grid.setTitle( i18n.getString( "activity_plan" ) );
        if ( !startDate.isEmpty() && !endDate.isEmpty() )
        {
            grid.setSubtitle( i18n.getString( "from" ) + " " + startDate + " " + i18n.getString( "to" ) + " " + endDate );
        }

        grid.addHeader( new GridHeader( "entityInstanceid", true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "first_name" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "middle_name" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "last_name" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "gender" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "phone_number" ), false, true ) );

        Collection<TrackedEntityAttribute> attributes = attributeService
            .getTrackedEntityAttributesByDisplayOnVisitSchedule( true );
        for ( TrackedEntityAttribute attribute : attributes )
        {
            grid.addHeader( new GridHeader( attribute.getDisplayName(), false, true ) );
        }

        grid.addHeader( new GridHeader( "programstageinstanceid", true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "program_stage" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "due_date" ), false, true ) );

        return entityInstanceStore.getTrackedEntityInstanceEventReport( grid, searchKeys, orgunits, followup,
            attributes, statusEnrollment, min, max );
    }

    @Override
    public Grid getTrackingEventsReport( Program program, List<String> searchKeys,
        Collection<OrganisationUnit> orgunits, Boolean followup, Integer statusEnrollment, I18n i18n )
    {
        String startDate = "";
        String endDate = "";
        for ( String searchKey : searchKeys )
        {
            String[] keys = searchKey.split( "_" );
            if ( keys[0].equals( TrackedEntityInstance.PREFIX_PROGRAM_EVENT_BY_STATUS ) )
            {
                startDate = keys[2];
                endDate = keys[3];
            }
        }

        Grid grid = new ListGrid();
        grid.setTitle( i18n.getString( "program_tracking" ) );
        if ( !startDate.isEmpty() && !endDate.isEmpty() )
        {
            grid.setSubtitle( i18n.getString( "from" ) + " " + startDate + " " + i18n.getString( "to" ) + " " + endDate );
        }

        grid.addHeader( new GridHeader( "entityInstanceid", true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "first_name" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "middle_name" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "last_name" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "gender" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "phone_number" ), false, true ) );

        Collection<TrackedEntityAttribute> attributes = program.getEntityAttributes();

        for ( TrackedEntityAttribute attribute : attributes )
        {
            grid.addHeader( new GridHeader( attribute.getDisplayName(), false, true ) );
        }

        grid.addHeader( new GridHeader( "programstageinstanceid", true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "program_stage" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "due_date" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "risk" ), false, true ) );

        return entityInstanceStore.getTrackedEntityInstanceEventReport( grid, searchKeys, orgunits, followup,
            attributes, statusEnrollment, null, null );
    }

    @Override
    public int validateTrackedEntityInstance( TrackedEntityInstance instance, Program program, I18nFormat format )
    {
        return entityInstanceStore.validate( instance, program, format );
    }

    @Override
    public ValidationCriteria validateEnrollment( TrackedEntityInstance instance, Program program, I18nFormat format )
    {
        return entityInstanceStore.validateEnrollment( instance, program, format );
    }

    @Override
    public Collection<TrackedEntityInstance> searchTrackedEntityInstancesForMobile( String searchText,

    int orgUnitId, int attributeId )
    {

        Set<TrackedEntityInstance> entityInstances = new HashSet<TrackedEntityInstance>();

        entityInstances.addAll( getTrackedEntityInstancesByAttributeValue( searchText,

        attributeId, 0, Integer.MAX_VALUE ) );

        if ( orgUnitId != 0 )
        {

            Set<TrackedEntityInstance> toRemoveList = new HashSet<TrackedEntityInstance>();

            for ( TrackedEntityInstance instance : entityInstances )
            {

                if ( instance.getOrganisationUnit().getId() != orgUnitId )
                {
                    toRemoveList.add( instance );
                }
            }
            entityInstances.removeAll( toRemoveList );
        }
        return entityInstances;
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstancesByAttributeValue( String searchText,
        int attributeId, Integer min, Integer max )
    {
        return entityInstanceStore.getByAttributeValue( searchText, attributeId, min, max );
    }
}
