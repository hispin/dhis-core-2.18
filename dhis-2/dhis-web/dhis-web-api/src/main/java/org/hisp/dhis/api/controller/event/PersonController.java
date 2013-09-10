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
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.event.Gender;
import org.hisp.dhis.dxf2.event.Person;
import org.hisp.dhis.dxf2.event.PersonService;
import org.hisp.dhis.dxf2.event.Persons;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = PersonController.RESOURCE_PATH)
public class PersonController
{
    public static final String RESOURCE_PATH = "/persons";

    @Autowired
    private PersonService personService;

    @Autowired
    private IdentifiableObjectManager manager;

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
    public String getPerson( @PathVariable String id, @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request )
    {
        WebOptions options = new WebOptions( parameters );
        Person person = personService.getPerson( id );

        model.addAttribute( "model", person );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return "person";
    }
}
