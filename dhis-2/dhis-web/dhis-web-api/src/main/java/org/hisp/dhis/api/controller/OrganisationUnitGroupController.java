package org.hisp.dhis.api.controller;

import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.WebLinkPopulatorListener;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroups;
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
@RequestMapping( value = "/organisationUnitGroups" )
public class OrganisationUnitGroupController
{
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @RequestMapping( method = RequestMethod.GET )
    public String getOrganisationUnits( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        OrganisationUnitGroups organisationUnitGroups = new OrganisationUnitGroups();
        organisationUnitGroups.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() ) );

        if ( params.hasLinks() )
        {
            WebLinkPopulatorListener listener = new WebLinkPopulatorListener( request );
            listener.beforeMarshal( organisationUnitGroups );
        }

        model.addAttribute( "model", organisationUnitGroups );

        return "organisationUnitGroups";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getOrganisationUnit( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulatorListener listener = new WebLinkPopulatorListener( request );
            listener.beforeMarshal( organisationUnitGroup );
        }

        model.addAttribute( "model", organisationUnitGroup );

        return "organisationUnitGroup";
    }
}
