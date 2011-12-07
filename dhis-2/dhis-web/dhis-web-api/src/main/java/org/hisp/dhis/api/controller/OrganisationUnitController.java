package org.hisp.dhis.api.controller;

import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.WebLinkPopulator;
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
    public String getOrganisationUnits( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        OrganisationUnits organisationUnits = new OrganisationUnits();

        if ( params.hasNoPaging() )
        {
            organisationUnits.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );
        }
        else
        {
            organisationUnits.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );
        }

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( organisationUnits );
        }

        model.addAttribute( "model", organisationUnits );

        return "organisationUnits";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getOrganisationUnit( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( organisationUnit );
        }

        model.addAttribute( "model", organisationUnit );

        return "organisationUnit";
    }
}
