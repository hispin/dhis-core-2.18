package org.hisp.dhis.api.controller;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.OrganisationUnits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping( method = RequestMethod.GET )
    public String getOrganisationUnits( Model model )
    {
        OrganisationUnits organisationUnits = new OrganisationUnits();
        organisationUnits.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );

        model.addAttribute( "model", organisationUnits );

        return "organisationUnits";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getOrganisationUnit( @PathVariable( "uid" ) String uid, Model model )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( uid );

        model.addAttribute( "model", organisationUnit );

        return "organisationUnit";
    }
}
