package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.importexport.dxf2.model.DataValueSet;
import org.hisp.dhis.importexport.dxf2.service.DataValueSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.velocity.VelocityManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

@Controller
@RequestMapping( value = DataValueSetController.RESOURCE_PATH )
public class DataValueSetController
{
    public static final String RESOURCE_PATH = "/dataValueSets";

    private static final Log log = LogFactory.getLog( DataValueSetController.class );

    @Autowired
    private DataValueSetService dataValueSetService;

    @Autowired
    private UserService userService;

    @RequestMapping( method = RequestMethod.GET )
    public void getDataValueSet( Writer writer ) throws Exception
    {
        VelocityManager velocityManager = new VelocityManager();
        String str = velocityManager.render( "/templates/html/dataValueSet" );
        writer.write( str );
    }

    @RequestMapping( method = RequestMethod.POST )
    public void storeDataValueSet( @RequestBody DataValueSet dataValueSet, @RequestParam( required = false ) String phoneNumber )
    {
        if ( phoneNumber != null && !phoneNumber.trim().isEmpty() )
        {
            String unitId = findOrgUnit( phoneNumber );
            dataValueSet.setOrganisationUnitIdentifier( unitId );
        }

        dataValueSetService.saveDataValueSet( dataValueSet );

        if ( log.isDebugEnabled() )
        {
            String message = "Saved data value set for " + dataValueSet.getDataSetIdentifier() + ", "
                + dataValueSet.getOrganisationUnitIdentifier() + ", " + dataValueSet.getPeriodIsoDate();

            log.debug( message );
        }
    }

    @ExceptionHandler
    public void mapException( IllegalArgumentException exception, HttpServletResponse response )
        throws IOException
    {
        response.setStatus( HttpServletResponse.SC_CONFLICT );
        response.setContentType( "text/plain" );
        response.getWriter().write( "Problem with input: " + exception.getMessage() );
    }

    /**
     * Find orgunit corresponding to the registered phone number.
     *
     * @param phoneNumber The phone number to look up
     * @return the organisation unit uid
     * @throws IllegalArgumentException if
     *                                  <ul>
     *                                  <li>No user has phone number
     *                                  <li>More than one user has phone number
     *                                  <li>User not associated with org unit
     *                                  <li>User associated with multiple org units
     *                                  </ul>
     */
    private String findOrgUnit( String phoneNumber )
        throws IllegalArgumentException
    {
        Collection<User> users = userService.getUsersByPhoneNumber( phoneNumber );

        if ( users == null || users.isEmpty() )
        {
            throw new IllegalArgumentException( "Phone number '" + phoneNumber + "' not associated with any user" );
        }
        else if ( users.size() > 1 )
        {
            throw new IllegalArgumentException( "Phone number '" + phoneNumber + "' associated with multiple users" );
        }

        User user = users.iterator().next();

        Collection<OrganisationUnit> organisationUnits = user.getOrganisationUnits();

        if ( organisationUnits == null || organisationUnits.isEmpty() )
        {
            throw new IllegalArgumentException( "User '" + user.getName()
                + "' not associated with any organisation unit" );
        }
        else if ( organisationUnits.size() > 1 )
        {
            throw new IllegalArgumentException( "User '" + user.getName()
                + "' associated with multiple organisation units" );
        }

        return organisationUnits.iterator().next().getUid();

    }
}
