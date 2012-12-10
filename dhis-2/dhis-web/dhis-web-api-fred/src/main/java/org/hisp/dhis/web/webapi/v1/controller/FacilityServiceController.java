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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.webapi.v1.domain.Facility;
import org.hisp.dhis.web.webapi.v1.utils.ValidationUtils;
import org.hisp.dhis.web.webapi.v1.validation.group.Create;
import org.hisp.dhis.web.webapi.v1.validation.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.io.IOException;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller(value = "facility-service-controller-" + FredController.PREFIX)
@RequestMapping(FacilityServiceController.RESOURCE_PATH)
public class FacilityServiceController
{
    public static final String RESOURCE_PATH = "/" + FredController.PREFIX + "/facility-service";

    @Autowired
    @Qualifier("org.hisp.dhis.organisationunit.OrganisationUnitService")
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private Validator validator;

    //--------------------------------------------------------------------------
    // EXTRA WEB METHODS
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{id}/activate", method = RequestMethod.POST)
    public ResponseEntity<Void> activateFacility( @PathVariable String id )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( id );

        if ( organisationUnit != null )
        {
            organisationUnit.setActive( true );
            organisationUnitService.updateOrganisationUnit( organisationUnit );

            return new ResponseEntity<Void>( HttpStatus.OK );
        }

        return new ResponseEntity<Void>( HttpStatus.NOT_FOUND );
    }

    @RequestMapping(value = "/{id}/deactivate", method = RequestMethod.POST)
    public ResponseEntity<Void> deactivateFacility( @PathVariable String id )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( id );

        if ( organisationUnit != null )
        {
            organisationUnit.setActive( false );
            organisationUnitService.updateOrganisationUnit( organisationUnit );

            return new ResponseEntity<Void>( HttpStatus.OK );
        }

        return new ResponseEntity<Void>( HttpStatus.NOT_FOUND );
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validateFacilityForCreate( @RequestBody Facility facility ) throws IOException
    {
        Set<ConstraintViolation<Facility>> constraintViolations = validator.validate( facility, Default.class, Create.class );

        String json = ValidationUtils.constraintViolationsToJson( constraintViolations );

        if ( constraintViolations.isEmpty() )
        {
            return new ResponseEntity<String>( json, HttpStatus.OK );
        }
        else
        {
            return new ResponseEntity<String>( json, HttpStatus.UNPROCESSABLE_ENTITY );
        }
    }

    @RequestMapping(value = "/validate", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validateFacilityForUpdate( @RequestBody Facility facility ) throws IOException
    {
        Set<ConstraintViolation<Facility>> constraintViolations = validator.validate( facility, Default.class, Update.class );

        String json = ValidationUtils.constraintViolationsToJson( constraintViolations );

        if ( constraintViolations.isEmpty() )
        {
            return new ResponseEntity<String>( json, HttpStatus.OK );
        }
        else
        {
            return new ResponseEntity<String>( json, HttpStatus.UNPROCESSABLE_ENTITY );
        }
    }
}
