package org.hisp.dhis.api.controller;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.importexport.dxf2.model.DataValueSet;
import org.hisp.dhis.importexport.dxf2.service.DataValueSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping( value = "/dataValueSets" )
public class DataValueSetController
{

    private static final Log log = LogFactory.getLog( DataValueSetController.class );

    @Autowired
    private DataValueSetService dataValueSetService;

    @Autowired
    private UserService userService;

    @RequestMapping( method = RequestMethod.POST )
    public void storeDataValueSet( @RequestBody
    DataValueSet dataValueSet, @RequestParam( required = false )
    String phoneNumber )
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
     *         <ul>
     *         <li>No user has phone number
     *         <li>More than one user has phone number
     *         <li>User not associated with org unit
     *         <li>User associated with multiple org units
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
