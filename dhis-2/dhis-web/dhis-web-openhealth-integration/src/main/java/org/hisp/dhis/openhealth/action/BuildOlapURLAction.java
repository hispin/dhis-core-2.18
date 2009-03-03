package org.hisp.dhis.openhealth.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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
import org.hisp.dhis.olap.OlapURLService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;

import static org.hisp.dhis.util.ContextUtils.getBaseUrl;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class BuildOlapURLAction
    implements Action
{
    private static final Log log = LogFactory.getLog( BuildOlapURLAction.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OlapURLService olapURLService;

    public void setOlapURLService( OlapURLService olapURLService )
    {
        this.olapURLService = olapURLService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String application;

    public void setApplication( String application )
    {
        this.application = application;
    }

    private String cube;

    public void setCube( String cube )
    {
        this.cube = cube;
    }

    private String indicator;

    public void setIndicator( String indicator )
    {
        this.indicator = indicator;
    }

    private String period;

    public void setPeriod( String period )
    {
        this.period = period;
    }
    
    private String level;
    
    public void setLevel( String level )
    {
        this.level = level;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

     private String redirect;

     public String getRedirect()
     {
         return redirect;
     }
     
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        String[] periodElements = period.split( " " );
        String month = periodElements[0].substring( 0, 3 );
        String year = periodElements[1];

        String organisationUnit = organisationUnitService.getRootOrganisationUnits().iterator().next().getGeoCode();

        String baseUrl = getBaseUrl( ServletActionContext.getRequest() );
        
        String queryString = olapURLService.getMapURL( application, cube, indicator, organisationUnit, year, month, level );
        
        String url = baseUrl + queryString;
        
        log.info( "URL: " + url );
        
        redirect = url;
        
        return SUCCESS;
    }
}
