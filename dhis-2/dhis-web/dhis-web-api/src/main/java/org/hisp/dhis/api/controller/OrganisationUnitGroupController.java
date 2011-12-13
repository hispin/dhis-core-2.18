package org.hisp.dhis.api.controller;

import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.ObjectPersister;
import org.hisp.dhis.api.utils.WebLinkPopulator;
import org.hisp.dhis.api.view.Jaxb2Utils;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OrganisationUnitGroupController.RESOURCE_PATH )
public class OrganisationUnitGroupController
{
    public static final String RESOURCE_PATH = "/organisationUnitGroups";

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private ObjectPersister objectPersister;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getOrganisationUnits( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        OrganisationUnitGroups organisationUnitGroups = new OrganisationUnitGroups();
        organisationUnitGroups.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() ) );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( organisationUnitGroups );
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
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( organisationUnitGroup );
        }

        model.addAttribute( "model", organisationUnitGroup );

        return "organisationUnitGroup";
    }

    //-------------------------------------------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_WEBAPI_CREATE')" )
    public void postOrganisationUnitGroupXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        OrganisationUnitGroup organisationUnitGroup = Jaxb2Utils.unmarshal( OrganisationUnitGroup.class, input );

        postOrganisationUnitGroup( organisationUnitGroup, response );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_WEBAPI_CREATE')" )
    public void postOrganisationUnitGroupJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    public void postOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup, HttpServletResponse response )
    {
        if ( organisationUnitGroup == null )
        {
            response.setStatus( HttpServletResponse.SC_NOT_IMPLEMENTED );
        }
        else
        {
            try
            {
                organisationUnitGroup = objectPersister.persistOrganisationUnitGroup( organisationUnitGroup );

                if ( organisationUnitGroup.getUid() == null )
                {
                    response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                }
                else
                {
                    response.setStatus( HttpServletResponse.SC_CREATED );
                    response.setHeader( "Location", DataElementController.RESOURCE_PATH + "/" + organisationUnitGroup.getUid() );
                }
            } catch ( Exception e )
            {
                response.setStatus( HttpServletResponse.SC_CONFLICT );
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------
    // PUT
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putOrganisationUnitGroupXML( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putOrganisationUnitGroupJSON( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // DELETE
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteOrganisationUnitGroup( @PathVariable( "uid" ) String uid ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.DELETE.toString() );
    }
}
