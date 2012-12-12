package org.hisp.dhis.web.webapi.v1.controller;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.webapi.v1.domain.Facilities;
import org.hisp.dhis.web.webapi.v1.domain.Facility;
import org.hisp.dhis.web.webapi.v1.utils.MessageResponseUtils;
import org.hisp.dhis.web.webapi.v1.utils.ValidationUtils;
import org.hisp.dhis.web.webapi.v1.validation.group.Create;
import org.hisp.dhis.web.webapi.v1.validation.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller( value = "facility-controller-" + FredController.PREFIX )
@RequestMapping( FacilityController.RESOURCE_PATH )
@PreAuthorize( "hasRole('M_dhis-web-api-fred') or hasRole('ALL')" )
public class FacilityController
{
    public static final String RESOURCE_PATH = "/" + FredController.PREFIX + "/facilities";

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private Validator validator;

    @InitBinder
    protected void initBinder( WebDataBinder binder )
    {
        binder.registerCustomEditor( Date.class, new PropertyEditorSupport()
        {
            private SimpleDateFormat[] simpleDateFormats = new SimpleDateFormat[]{
                new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" ),
                new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" ),
                new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm" ),
                new SimpleDateFormat( "yyyy-MM-dd'T'HH" ),
                new SimpleDateFormat( "yyyy-MM-dd" ),
                new SimpleDateFormat( "yyyy-MM" ),
                new SimpleDateFormat( "yyyy" )
            };

            @Override
            public void setAsText( String value ) throws IllegalArgumentException
            {
                for ( SimpleDateFormat simpleDateFormat : simpleDateFormats )
                {
                    try
                    {
                        setValue( simpleDateFormat.parse( value ) );
                        return;
                    }
                    catch ( ParseException ignored )
                    {
                    }
                }

                setValue( null );
            }
        } );
    }

    //--------------------------------------------------------------------------
    // GET HTML
    //--------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.GET )
    public String readFacilities( Model model, @RequestParam( required = false ) Boolean active,
        @RequestParam( value = "updatedSince", required = false ) Date lastUpdated )
    {
        Facilities facilities = new Facilities();
        List<OrganisationUnit> allOrganisationUnits;

        if ( active == null && lastUpdated == null )
        {
            allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
        }
        else if ( active == null )
        {
            allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnitsByLastUpdated( lastUpdated ) );
        }
        else if ( lastUpdated == null )
        {
            allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnitsByStatus( active ) );
        }
        else
        {
            allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnitsByStatusLastUpdated( active, lastUpdated ) );
        }

        Collections.sort( allOrganisationUnits, IdentifiableObjectNameComparator.INSTANCE );

        for ( OrganisationUnit organisationUnit : allOrganisationUnits )
        {
            Facility facility = conversionService.convert( organisationUnit, Facility.class );

            facilities.getFacilities().add( facility );
        }

        setAccessRights( model );

        model.addAttribute( "esc", StringEscapeUtils.class );
        model.addAttribute( "entity", facilities );
        model.addAttribute( "baseUrl", linkTo( FredController.class ).toString() );
        model.addAttribute( "pageName", "facilities" );
        model.addAttribute( "page", FredController.PREFIX + "/facilities.vm" );

        return FredController.PREFIX + "/layout";
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public String readFacility( Model model, @PathVariable String id )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( id );

        Facility facility = conversionService.convert( organisationUnit, Facility.class );

        setAccessRights( model );

        model.addAttribute( "esc", StringEscapeUtils.class );
        model.addAttribute( "entity", facility );

        List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Collections.sort( dataSets, IdentifiableObjectNameComparator.INSTANCE );
        model.addAttribute( "dataSets", dataSets );

        model.addAttribute( "baseUrl", linkTo( FredController.class ).toString() );
        model.addAttribute( "pageName", "facility" );
        model.addAttribute( "page", FredController.PREFIX + "/facility.vm" );

        return FredController.PREFIX + "/layout";
    }

    private void setAccessRights( Model model )
    {
        Set<String> authorities = currentUserService.getCurrentUser().getUserCredentials().getAllAuthorities();

        model.addAttribute( "canCreate", authorities.contains( "F_FRED_CREATE" ) || currentUserService.currentUserIsSuper() );
        model.addAttribute( "canRead", authorities.contains( "M-dhis-web-api-fred" ) || currentUserService.currentUserIsSuper() );
        model.addAttribute( "canUpdate", authorities.contains( "F_FRED_UPDATE" ) || currentUserService.currentUserIsSuper() );
        model.addAttribute( "canDelete", authorities.contains( "F_FRED_DELETE" ) || currentUserService.currentUserIsSuper() );
    }

    //--------------------------------------------------------------------------
    // POST JSON
    //--------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.POST )
    @PreAuthorize( "hasRole('F_FRED_CREATE') or hasRole('ALL')" )
    public ResponseEntity<String> createFacility( @RequestBody Facility facility ) throws IOException
    {
        Set<ConstraintViolation<Facility>> constraintViolations = validator.validate( facility, Default.class, Create.class );

        String json = ValidationUtils.constraintViolationsToJson( constraintViolations );

        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        if ( constraintViolations.isEmpty() )
        {
            OrganisationUnit organisationUnit = conversionService.convert( facility, OrganisationUnit.class );

            if ( organisationUnitService.getOrganisationUnit( organisationUnit.getUid() ) != null )
            {
                return new ResponseEntity<String>( MessageResponseUtils.jsonMessage( "An object with that ID already exists." ), headers, HttpStatus.CONFLICT );
            }
            else if ( organisationUnitService.getOrganisationUnitByName( organisationUnit.getName() ) != null )
            {
                return new ResponseEntity<String>( MessageResponseUtils.jsonMessage( "An object with that name already exists." ), headers, HttpStatus.CONFLICT );
            }
            else if ( organisationUnit.getCode() != null && organisationUnitService.getOrganisationUnitByCode( organisationUnit.getCode() ) != null )
            {
                return new ResponseEntity<String>( MessageResponseUtils.jsonMessage( "An object with that code already exists." ), headers, HttpStatus.CONFLICT );
            }

            organisationUnitService.addOrganisationUnit( organisationUnit );

            for ( DataSet dataSet : organisationUnit.getDataSets() )
            {
                dataSet.addOrganisationUnit( organisationUnit );
                dataSetService.updateDataSet( dataSet );
            }

            headers.setLocation( linkTo( FacilityController.class ).slash( organisationUnit.getUid() ).toUri() );

            return new ResponseEntity<String>( json, headers, HttpStatus.CREATED );
        }
        else
        {
            return new ResponseEntity<String>( json, headers, HttpStatus.UNPROCESSABLE_ENTITY );
        }
    }

    //--------------------------------------------------------------------------
    // PUT JSON
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize( "hasRole('F_FRED_UPDATE') or hasRole('ALL')" )
    public ResponseEntity<String> updateFacility( @PathVariable String id, @RequestBody Facility facility ) throws IOException
    {
        facility.setId( id );
        OrganisationUnit organisationUnit = conversionService.convert( facility, OrganisationUnit.class );

        Set<ConstraintViolation<Facility>> constraintViolations = validator.validate( facility, Default.class, Update.class );

        String json = ValidationUtils.constraintViolationsToJson( constraintViolations );

        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        if ( constraintViolations.isEmpty() )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( facility.getId() );

            if ( ou == null )
            {
                return new ResponseEntity<String>( MessageResponseUtils.jsonMessage( "No object with that identifier exists." ),
                    headers, HttpStatus.NOT_FOUND );
            }
            else if ( !ou.getName().equals( organisationUnit.getName() ) )
            {
                OrganisationUnit ouByName = organisationUnitService.getOrganisationUnitByName( organisationUnit.getName() );

                if ( ouByName != null && !ou.getUid().equals( ouByName.getUid() ) )
                {
                    return new ResponseEntity<String>( MessageResponseUtils.jsonMessage( "Another object with the same name already exists." ),
                        headers, HttpStatus.CONFLICT );
                }
            }
            else if ( organisationUnit.getCode() != null )
            {
                OrganisationUnit ouByCode = organisationUnitService.getOrganisationUnitByCode( organisationUnit.getCode() );

                if ( ouByCode != null && !ou.getUid().equals( ouByCode.getUid() ) )
                {
                    return new ResponseEntity<String>( MessageResponseUtils.jsonMessage( "Another object with the same code already exists." ),
                        headers, HttpStatus.CONFLICT );
                }
            }

            ou.setName( organisationUnit.getName() );
            ou.setShortName( organisationUnit.getShortName() );
            ou.setCode( organisationUnit.getCode() );
            ou.setFeatureType( organisationUnit.getFeatureType() );
            ou.setCoordinates( organisationUnit.getCoordinates() );
            ou.setParent( organisationUnit.getParent() );
            ou.setActive( organisationUnit.isActive() );

            ou.removeAllDataSets();
            organisationUnitService.updateOrganisationUnit( ou );

            for ( DataSet dataSet : organisationUnit.getDataSets() )
            {
                dataSet.addOrganisationUnit( ou );
                dataSetService.updateDataSet( dataSet );
            }

            return new ResponseEntity<String>( json, headers, HttpStatus.OK );
        }
        else
        {
            return new ResponseEntity<String>( json, headers, HttpStatus.UNPROCESSABLE_ENTITY );
        }
    }

    //--------------------------------------------------------------------------
    // DELETE JSON
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('F_FRED_DELETE') or hasRole('ALL')" )
    public ResponseEntity<Void> deleteFacility( @PathVariable String id ) throws HierarchyViolationException
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( id );

        if ( organisationUnit != null )
        {
            organisationUnitService.deleteOrganisationUnit( organisationUnit );

            return new ResponseEntity<Void>( HttpStatus.OK );
        }

        return new ResponseEntity<Void>( HttpStatus.NOT_FOUND );
    }

    //--------------------------------------------------------------------------
    // EXCEPTION HANDLERS
    //--------------------------------------------------------------------------

    @ExceptionHandler( { DeleteNotAllowedException.class, HierarchyViolationException.class } )
    public ResponseEntity<String> exceptionHandler( Exception ex )
    {
        return new ResponseEntity<String>( ex.getMessage(), HttpStatus.FORBIDDEN );
    }
}
