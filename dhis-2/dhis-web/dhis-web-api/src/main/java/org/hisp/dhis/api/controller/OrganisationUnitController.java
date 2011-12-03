package org.hisp.dhis.api.controller;

import org.hisp.dhis.api.utils.WebLinkPopulatorListener;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.OrganisationUnits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping( method = RequestMethod.GET )
    public String getOrganisationUnits( Model model, HttpServletRequest request )
    {
        OrganisationUnits organisationUnits = new OrganisationUnits();
        organisationUnits.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );

        WebLinkPopulatorListener listener = new WebLinkPopulatorListener( request );
        listener.beforeMarshal( organisationUnits );

        model.addAttribute( "model", organisationUnits );

        return "organisationUnits";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getOrganisationUnit( @PathVariable( "uid" ) String uid, Model model, HttpServletRequest request )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( uid );

        WebLinkPopulatorListener listener = new WebLinkPopulatorListener( request );
        listener.beforeMarshal( organisationUnit );

        model.addAttribute( "model", organisationUnit );

        return "organisationUnit";
    }
}
