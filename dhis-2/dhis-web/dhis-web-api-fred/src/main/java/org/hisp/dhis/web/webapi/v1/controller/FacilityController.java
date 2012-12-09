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
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.webapi.v1.domain.Facilities;
import org.hisp.dhis.web.webapi.v1.domain.Facility;
import org.hisp.dhis.web.webapi.v1.utils.ValidationUtils;
import org.hisp.dhis.web.webapi.v1.validation.group.UpdateSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller(value = "facility-controller-" + FredController.PREFIX)
@RequestMapping(FacilityController.RESOURCE_PATH)
public class FacilityController
{
    public static final String RESOURCE_PATH = "/" + FredController.PREFIX + "/facilities";

    @Autowired
    @Qualifier("org.hisp.dhis.organisationunit.OrganisationUnitService")
    private OrganisationUnitService organisationUnitService;

    @Autowired
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Autowired
    private Validator validator;

    //--------------------------------------------------------------------------
    // GET HTML
    //--------------------------------------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String readFacilities( Model model )
    {
        Facilities facilities = new Facilities();

        List<OrganisationUnit> allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
        Collections.sort( allOrganisationUnits, IdentifiableObjectNameComparator.INSTANCE );

        for ( OrganisationUnit organisationUnit : allOrganisationUnits )
        {
            Facility facility = conversionService.convert( organisationUnit, Facility.class );

            facilities.getFacilities().add( facility );
        }

        model.addAttribute( "esc", StringEscapeUtils.class );
        model.addAttribute( "entity", facilities );
        model.addAttribute( "baseUrl", linkTo( FredController.class ).toString() );
        model.addAttribute( "pageName", "facilities" );
        model.addAttribute( "page", FredController.PREFIX + "/facilities.vm" );

        return FredController.PREFIX + "/layout";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String readFacility( Model model, @PathVariable String id )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( id );

        Facility facility = conversionService.convert( organisationUnit, Facility.class );

        model.addAttribute( "esc", StringEscapeUtils.class );
        model.addAttribute( "entity", facility );
        model.addAttribute( "baseUrl", linkTo( FredController.class ).toString() );
        model.addAttribute( "pageName", "facility" );
        model.addAttribute( "page", FredController.PREFIX + "/facility.vm" );

        return FredController.PREFIX + "/layout";
    }

    //--------------------------------------------------------------------------
    // POST JSON
    //--------------------------------------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> createFacility( @RequestBody Facility facility ) throws IOException
    {
        OrganisationUnit organisationUnit = conversionService.convert( facility, OrganisationUnit.class );

        return new ResponseEntity<String>( "ok", HttpStatus.OK );

        /*
        Set<ConstraintViolation<Facility>> constraintViolations = validator.validate( facility, CreateSequence.class );

        String json = ValidationUtils.constraintViolationsToJson( constraintViolations );

        if ( constraintViolations.isEmpty() )
        {
            return new ResponseEntity<String>( json, HttpStatus.OK );
        }
        else
        {
            return new ResponseEntity<String>( json, HttpStatus.UNPROCESSABLE_ENTITY );
        }
        */
    }

    //--------------------------------------------------------------------------
    // PUT JSON
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateFacility( @PathVariable String id, @RequestBody Facility facility ) throws IOException
    {
        facility.setId( id );
        OrganisationUnit organisationUnit = conversionService.convert( facility, OrganisationUnit.class );

        Set<ConstraintViolation<Facility>> constraintViolations = validator.validate( facility, UpdateSequence.class );

        String json = ValidationUtils.constraintViolationsToJson( constraintViolations );

        if ( constraintViolations.isEmpty() )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( facility.getId() );

            ou.setName( organisationUnit.getName() );
            ou.setShortName( organisationUnit.getShortName() );

            ou.setFeatureType( organisationUnit.getFeatureType() );
            ou.setCoordinates( organisationUnit.getCoordinates() );
            ou.setDataSets( organisationUnit.getDataSets() );
            ou.setParent( organisationUnit.getParent() );

            ou.setActive( organisationUnit.isActive() );

            organisationUnitService.updateOrganisationUnit( ou );

            return new ResponseEntity<String>( json, HttpStatus.OK );
        }
        else
        {
            return new ResponseEntity<String>( json, HttpStatus.UNPROCESSABLE_ENTITY );
        }
    }

    //--------------------------------------------------------------------------
    // DELETE JSON
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
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

    @ExceptionHandler({ DeleteNotAllowedException.class, HierarchyViolationException.class })
    public ResponseEntity<String> exceptionHandler( Exception ex )
    {
        return new ResponseEntity<String>( ex.getMessage(), HttpStatus.FORBIDDEN );
    }
}
