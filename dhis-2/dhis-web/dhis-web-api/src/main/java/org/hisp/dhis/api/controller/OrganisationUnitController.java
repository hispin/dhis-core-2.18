package org.hisp.dhis.api.controller;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.OrganisationUnits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/organisationUnits" )
public class OrganisationUnitController
{
    @Autowired
    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitController()
    {

    }

    @RequestMapping( method = RequestMethod.GET )
    public OrganisationUnits getOrganisationUnits()
    {
        OrganisationUnits organisationUnits = new OrganisationUnits();
        organisationUnits.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );

        return organisationUnits;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public OrganisationUnit getOrganisationUnit( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( uid );

        return organisationUnit;
    }
}
