package org.hisp.dhis.api.controller.event;

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

import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.event.person.Gender;
import org.hisp.dhis.dxf2.event.person.Person;
import org.hisp.dhis.dxf2.event.person.PersonService;
import org.hisp.dhis.dxf2.event.person.Persons;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = PersonController.RESOURCE_PATH )
public class PersonController
{
    public static final String RESOURCE_PATH = "/persons";

    @Autowired
    private PersonService personService;

    @Autowired
    private IdentifiableObjectManager manager;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.GET )
    public String getPersons(
        @RequestParam( value = "orgUnit", required = false ) String orgUnitUid,
        @RequestParam( required = false ) Gender gender,
        @RequestParam( value = "program", required = false ) String programUid,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        Persons persons;

        // it will be required in the future to have at least orgUnitUid, but for now, we allow no parameters
        if ( gender == null && orgUnitUid == null && programUid == null )
        {
            persons = personService.getPersons();
        }
        else if ( orgUnitUid != null && programUid != null && gender != null )
        {
            OrganisationUnit organisationUnit = getOrganisationUnit( orgUnitUid );
            Program program = getProgram( programUid );
            persons = personService.getPersons( organisationUnit, program, gender );
        }
        else if ( orgUnitUid != null && gender != null )
        {
            OrganisationUnit organisationUnit = getOrganisationUnit( orgUnitUid );
            persons = personService.getPersons( organisationUnit, gender );
        }
        else if ( orgUnitUid != null && programUid != null )
        {
            OrganisationUnit organisationUnit = getOrganisationUnit( orgUnitUid );
            Program program = getProgram( programUid );

            persons = personService.getPersons( organisationUnit, program );
        }
        else if ( programUid != null && gender != null )
        {
            Program program = getProgram( programUid );
            persons = personService.getPersons( program, gender );
        }
        else if ( orgUnitUid != null )
        {
            OrganisationUnit organisationUnit = getOrganisationUnit( orgUnitUid );
            persons = personService.getPersons( organisationUnit );
        }
        else if ( programUid != null )
        {
            Program program = getProgram( programUid );
            persons = personService.getPersons( program );
        }
        else
        {
            persons = new Persons();
        }

        model.addAttribute( "model", persons );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return "persons";
    }

    private Program getProgram( String programUid )
    {
        Program program = manager.get( Program.class, programUid );

        if ( program == null )
        {
            throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, "program is not valid uid." );
        }

        return program;
    }

    private OrganisationUnit getOrganisationUnit( String orgUnitUid )
    {
        OrganisationUnit organisationUnit = manager.get( OrganisationUnit.class, orgUnitUid );

        if ( organisationUnit == null )
        {
            throw new HttpClientErrorException( HttpStatus.BAD_REQUEST, "orgUnit is not a valid uid." );
        }

        return organisationUnit;
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public String getPerson( @PathVariable String id, @RequestParam Map<String, String> parameters, Model model )
    {
        WebOptions options = new WebOptions( parameters );
        Person person = personService.getPerson( id );

        model.addAttribute( "model", person );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return "person";
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE )
    public void postPersonXml( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        Persons persons = personService.savePersonXml( request.getInputStream() );

        if ( persons.getPersons().size() > 1 )
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            JacksonUtils.toXml( response.getOutputStream(), persons );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            response.setHeader( "Location", getResourcePath( request, persons.getPersons().get( 0 ) ) );
            JacksonUtils.toXml( response.getOutputStream(), persons.getPersons().get( 0 ) );
        }
    }

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public void postPersonJson( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        Persons persons = personService.savePersonJson( request.getInputStream() );

        if ( persons.getPersons().size() > 1 )
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            JacksonUtils.toJson( response.getOutputStream(), persons );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            response.setHeader( "Location", getResourcePath( request, persons.getPersons().get( 0 ) ) );
            JacksonUtils.toJson( response.getOutputStream(), persons.getPersons().get( 0 ) );
        }
    }

    public String getResourcePath( HttpServletRequest request, Person person )
    {
        return ContextUtils.getContextPath( request ) + "/api/" + "persons" + "/" + person.getPerson();
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void updatePersonXml( @PathVariable String id, HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        Person person = personService.updatePersonXml( id, request.getInputStream() );
        JacksonUtils.toXml( response.getOutputStream(), person );
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void updatePersonJson( @PathVariable String id, HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        Person person = personService.updatePersonJson( id, request.getInputStream() );
        JacksonUtils.toJson( response.getOutputStream(), person );
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deletePerson( @PathVariable String id )
    {
        Person person = personService.getPerson( id );
        personService.deletePerson( person );
    }
}
