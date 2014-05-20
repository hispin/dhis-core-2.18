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

import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.CREATED_ID;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.LAST_UPDATED_ID;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.META_DATA_NAMES_KEY;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.ORG_UNIT_ID;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.PAGER_META_KEY;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.TRACKED_ENTITY_ID;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.TRACKED_ENTITY_INSTANCE_ID;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.DimensionalObjectUtils;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.common.QueryOperator;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStatus;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.hisp.dhis.validation.ValidationCriteria;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 */
@Transactional
public class DefaultTrackedEntityInstanceService
    implements TrackedEntityInstanceService
{
    private static final Log log = LogFactory.getLog( DefaultTrackedEntityInstanceService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityInstanceStore trackedEntityInstanceStore;

    public void setTrackedEntityInstanceStore( TrackedEntityInstanceStore trackedEntityInstanceStore )
    {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
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

    private TrackedEntityService trackedEntityService;

    public void setTrackedEntityService( TrackedEntityService trackedEntityService )
    {
        this.trackedEntityService = trackedEntityService;
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

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    // TODO lower index on attribute value?

    @Override
    public Grid getTrackedEntityInstances( TrackedEntityInstanceQueryParams params )
    {
        validate( params );

        // ---------------------------------------------------------------------
        // Verify params
        // ---------------------------------------------------------------------

        for ( OrganisationUnit organisationUnit : params.getOrganisationUnits() )
        {
            if ( !organisationUnit.hasLevel() )
            {
                organisationUnit
                    .setLevel( organisationUnitService.getLevelOfOrganisationUnit( organisationUnit.getId() ) );
            }
        }

        // ---------------------------------------------------------------------
        // If params of type query and no attributes or filters defined, use
        // attributes from program if exists, if not, use display-in-list
        // attributes.
        // ---------------------------------------------------------------------

        if ( params.isOrQuery() || !params.hasAttributes() )
        {
            if ( params.hasProgram() )
            {
                params.addAttributesIfNotExist( QueryItem.getQueryItems( params.getProgram()
                    .getTrackedEntityAttributes() ) );
            }
            else
            {
                Collection<TrackedEntityAttribute> filters = attributeService.getAllTrackedEntityAttributes();
                Collection<TrackedEntityAttribute> attributes = attributeService
                    .getTrackedEntityAttributesDisplayInList();
                filters.removeAll( attributes );

                params.addAttributesIfNotExist( QueryItem.getQueryItems( attributes ) );
                params.addFiltersIfNotExist( QueryItem.getQueryItems( filters ) );
            }
        }

        // ---------------------------------------------------------------------
        // Conform params
        // ---------------------------------------------------------------------

        params.conform();

        // ---------------------------------------------------------------------
        // Grid headers
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid();

        grid.addHeader( new GridHeader( TRACKED_ENTITY_INSTANCE_ID, "Instance" ) );
        grid.addHeader( new GridHeader( CREATED_ID, "Created" ) );
        grid.addHeader( new GridHeader( LAST_UPDATED_ID, "Last updated" ) );
        grid.addHeader( new GridHeader( ORG_UNIT_ID, "Org unit" ) );
        grid.addHeader( new GridHeader( TRACKED_ENTITY_ID, "Tracked entity" ) );

        for ( QueryItem item : params.getAttributes() )
        {
            grid.addHeader( new GridHeader( item.getItem().getUid(), item.getItem().getName() ) );
        }

        List<Map<String, String>> entities = trackedEntityInstanceStore.getTrackedEntityInstances( params );

        // ---------------------------------------------------------------------
        // Grid rows
        // ---------------------------------------------------------------------

        Set<String> tes = new HashSet<String>();

        for ( Map<String, String> entity : entities )
        {
            grid.addRow();
            grid.addValue( entity.get( TRACKED_ENTITY_INSTANCE_ID ) );
            grid.addValue( entity.get( CREATED_ID ) );
            grid.addValue( entity.get( LAST_UPDATED_ID ) );
            grid.addValue( entity.get( ORG_UNIT_ID ) );
            grid.addValue( entity.get( TRACKED_ENTITY_ID ) );

            tes.add( entity.get( TRACKED_ENTITY_ID ) );

            for ( QueryItem item : params.getAttributes() )
            {
                grid.addValue( entity.get( item.getItemId() ) );
            }
        }

        Map<Object, Object> metaData = new HashMap<Object, Object>();

        if ( params.isPaging() )
        {
            int count = trackedEntityInstanceStore.getTrackedEntityInstanceCount( params );

            Pager pager = new Pager( params.getPageWithDefault(), count, params.getPageSizeWithDefault() );
            metaData.put( PAGER_META_KEY, pager );
        }

        if ( !params.isSkipMeta() )
        {
            Map<String, String> names = new HashMap<String, String>();

            for ( String te : tes )
            {
                TrackedEntity entity = trackedEntityService.getTrackedEntity( te );
                names.put( te, entity != null ? entity.getDisplayName() : null );
            }

            metaData.put( META_DATA_NAMES_KEY, names );
        }

        grid.setMetaData( metaData );

        return grid;
    }

    @Override
    public void validate( TrackedEntityInstanceQueryParams params )
        throws IllegalQueryException
    {
        String violation = null;

        if ( params == null )
        {
            throw new IllegalQueryException( "Params cannot be null" );
        }

        if ( !params.hasOrganisationUnits() && !params.isOrganisationUnitMode( OrganisationUnitSelectionMode.ALL ) )
        {
            violation = "At least one organisation unit must be specified";
        }

        if ( params.hasProgram() && params.hasTrackedEntity() )
        {
            violation = "Program and tracked entity cannot be specified simultaneously";
        }

        if ( params.hasProgramStatus() && !params.hasProgram() )
        {
            violation = "Program must be defined when program status is defined";
        }

        if ( params.hasFollowUp() && !params.hasProgram() )
        {
            violation = "Program must be defined when follow up status is defined";
        }

        if ( params.hasProgramStartDate() && !params.hasProgram() )
        {
            violation = "Program must be defined when program start date is specified";
        }

        if ( params.hasProgramEndDate() && !params.hasProgram() )
        {
            violation = "Program must be defined when program end date is specified";
        }

        if ( params.hasEventStatus() && (!params.hasEventStartDate() || !params.hasEventEndDate()) )
        {
            violation = "Event start and end date must be specified when event status is specified";
        }

        if ( params.isOrQuery() && params.hasFilters() )
        {
            violation = "Query cannot be specified together with filters";
        }

        if ( !params.getDuplicateAttributes().isEmpty() )
        {
            violation = "Attributes cannot be specified more than once: " + params.getDuplicateAttributes();
        }

        if ( !params.getDuplicateFilters().isEmpty() )
        {
            violation = "Filters cannot be specified more than once: " + params.getDuplicateFilters();
        }

        if ( violation != null )
        {
            log.warn( "Validation failed: " + violation );

            throw new IllegalQueryException( violation );
        }
    }

    @Override
    public TrackedEntityInstanceQueryParams getFromUrl( String query, Set<String> attribute, Set<String> filter,
        Set<String> ou, OrganisationUnitSelectionMode ouMode, String program, ProgramStatus programStatus,
        Boolean followUp, Date programStartDate, Date programEndDate, String trackedEntity, EventStatus eventStatus,
        Date eventStartDate, Date eventEndDate, boolean skipMeta, Integer page, Integer pageSize )
    {
        TrackedEntityInstanceQueryParams params = new TrackedEntityInstanceQueryParams();

        if ( attribute != null )
        {
            for ( String attr : attribute )
            {
                QueryItem it = getQueryItem( attr );

                params.getAttributes().add( it );
            }
        }

        if ( filter != null )
        {
            for ( String filt : filter )
            {
                QueryItem it = getQueryItem( filt );

                params.getFilters().add( it );
            }
        }

        if ( ou != null )
        {
            for ( String orgUnit : ou )
            {
                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnit );

                if ( organisationUnit == null )
                {
                    throw new IllegalQueryException( "Organisation unit does not exist: " + orgUnit );
                }

                params.getOrganisationUnits().add( organisationUnit );
            }
        }

        Program pr = program != null ? programService.getProgram( program ) : null;

        if ( program != null && pr == null )
        {
            throw new IllegalQueryException( "Program does not exist: " + program );
        }

        TrackedEntity te = trackedEntity != null ? trackedEntityService.getTrackedEntity( trackedEntity ) : null;

        if ( trackedEntity != null && te == null )
        {
            throw new IllegalQueryException( "Tracked entity does not exist: " + program );
        }

        params.setQuery( query );
        params.setProgram( pr );
        params.setProgramStatus( programStatus );
        params.setFollowUp( followUp );
        params.setProgramStartDate( programStartDate );
        params.setProgramEndDate( programEndDate );
        params.setTrackedEntity( te );
        params.setOrganisationUnitMode( ouMode );
        params.setEventStatus( eventStatus );
        params.setEventStartDate( eventStartDate );
        params.setEventEndDate( eventEndDate );
        params.setSkipMeta( skipMeta );
        params.setPage( page );
        params.setPageSize( pageSize );

        return params;
    }

    private QueryItem getQueryItem( String item )
    {
        if ( !item.contains( DimensionalObjectUtils.DIMENSION_NAME_SEP ) )
        {
            return getItem( item, null, null );
        }
        else
        // Filter
        {
            String[] split = item.split( DimensionalObjectUtils.DIMENSION_NAME_SEP );

            if ( split == null || split.length != 3 )
            {
                throw new IllegalQueryException( "Item filter has invalid format: " + item );
            }

            return getItem( split[0], split[1], split[2] );
        }
    }

    private QueryItem getItem( String item, String operator, String filter )
    {
        TrackedEntityAttribute at = attributeService.getTrackedEntityAttribute( item );

        if ( at == null )
        {
            throw new IllegalQueryException( "Attribute does not exist: " + item );
        }

        if ( operator != null && filter != null )
        {
            QueryOperator op = QueryOperator.fromString( operator );
            
            return new QueryItem( at, op, filter, at.isNumericType() );
        }
        else
        {
            return new QueryItem( at, at.isNumericType() );
        }
    }

    @Override
    public int addTrackedEntityInstance( TrackedEntityInstance instance )
    {
        return trackedEntityInstanceStore.save( instance );
    }

    @Override
    public int createTrackedEntityInstance( TrackedEntityInstance instance, String representativeId,
        Integer relationshipTypeId, Set<TrackedEntityAttributeValue> attributeValues )
    {
        int id = addTrackedEntityInstance( instance );

        for ( TrackedEntityAttributeValue pav : attributeValues )
        {
            attributeValueService.addTrackedEntityAttributeValue( pav );
            instance.getAttributeValues().add( pav );
        }

        // ---------------------------------------------------------------------
        // If under age, save representative information
        // ---------------------------------------------------------------------

        if ( representativeId != null )
        {
            TrackedEntityInstance representative = trackedEntityInstanceStore.getByUid( representativeId );
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
                        relationshipService.addRelationship( rel );
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
        trackedEntityInstanceStore.update( instance );
    }

    @Override
    public void deleteTrackedEntityInstance( TrackedEntityInstance instance )
    {
        trackedEntityInstanceStore.delete( instance );
    }

    @Override
    public TrackedEntityInstance getTrackedEntityInstance( int id )
    {
        return trackedEntityInstanceStore.get( id );
    }

    @Override
    public TrackedEntityInstance getTrackedEntityInstance( String uid )
    {
        return trackedEntityInstanceStore.getByUid( uid );
    }

    @Override
    public Collection<TrackedEntityInstance> getTrackedEntityInstances( OrganisationUnit organisationUnit, Integer min,
        Integer max )
    {
        return trackedEntityInstanceStore.getByOrgUnit( organisationUnit, min, max );
    }

    @Override
    public void updateTrackedEntityInstance( TrackedEntityInstance instance, String representativeId,
        Integer relationshipTypeId, List<TrackedEntityAttributeValue> valuesForSave,
        List<TrackedEntityAttributeValue> valuesForUpdate, Collection<TrackedEntityAttributeValue> valuesForDelete )
    {
        trackedEntityInstanceStore.update( instance );

        for ( TrackedEntityAttributeValue av : valuesForSave )
        {
            attributeValueService.addTrackedEntityAttributeValue( av );
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
            TrackedEntityInstance representative = trackedEntityInstanceStore.getByUid( representativeId );

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
                        relationshipService.addRelationship( rel );
                    }
                }
            }
        }
    }

    private boolean shouldSaveRepresentativeInformation( TrackedEntityInstance instance, String representativeId )
    {
        if ( representativeId == null || representativeId.isEmpty() )
        {
            return false;
        }

        return instance.getRepresentative() == null || !(instance.getRepresentative().getUid() == representativeId);
    }
    
    @Override
    public Collection<TrackedEntityInstance> getRepresentatives( TrackedEntityInstance instance )
    {
        return trackedEntityInstanceStore.getRepresentatives( instance );
    }

    @Override
    public String validateTrackedEntityInstance( TrackedEntityInstance instance, Program program, I18nFormat format )
    {
        return trackedEntityInstanceStore.validate( instance, program, format );
    }

    @Override
    public ValidationCriteria validateEnrollment( TrackedEntityInstance instance, Program program, I18nFormat format )
    {
        return trackedEntityInstanceStore.validateEnrollment( instance, program, format );
    }
}
