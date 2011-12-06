package org.hisp.dhis.api.webdomain;

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

import org.codehaus.jackson.annotate.JsonProperty;
import org.hisp.dhis.attribute.Attributes;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.common.BaseLinkableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.CompleteDataSetRegistrations;
import org.hisp.dhis.dataset.DataSets;
import org.hisp.dhis.indicator.IndicatorGroupSets;
import org.hisp.dhis.indicator.IndicatorGroups;
import org.hisp.dhis.indicator.IndicatorTypes;
import org.hisp.dhis.indicator.Indicators;
import org.hisp.dhis.mapping.Maps;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSets;
import org.hisp.dhis.organisationunit.OrganisationUnitGroups;
import org.hisp.dhis.organisationunit.OrganisationUnits;
import org.hisp.dhis.user.Users;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * At some point this class will be extended to show all available options
 * for a current user for this resource. For now it is only used for index page.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "resources", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Resources extends BaseLinkableObject
{
    private List<Resource> resources = new ArrayList<Resource>();

    public Resources()
    {
        generateResources();
    }

    @XmlElement( name = "resource" )
    @JsonProperty( value = "resources" )
    public List<Resource> getResources()
    {
        return resources;
    }

    public void setResources( List<Resource> resources )
    {
        this.resources = resources;
    }

    //-----------------------------------------------
    // Helpers
    //-----------------------------------------------
    private void generateResources()
    {
        RequestMethod[] defaultRequestMethods = new RequestMethod[]{
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE
        };

        MediaType[] defaultMediaTypes = new MediaType[]{
            MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML,
            new MediaType( "application", "javascript" ), new MediaType( "application", "pdf" )
        };

        List<RequestMethod> requestMethods = Arrays.asList( defaultRequestMethods );
        List<MediaType> mediaTypes = Arrays.asList( defaultMediaTypes );

        resources.add( new Resource( "Attributes", Attributes.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Charts", Charts.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Maps", Maps.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "CompleteDataSetRegistrations", CompleteDataSetRegistrations.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Indicators", Indicators.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "IndicatorGroups", IndicatorGroups.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "IndicatorGroupSets", IndicatorGroupSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "IndicatorTypes", IndicatorTypes.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElements", DataElements.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElementGroups", DataElementGroups.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElementGroupSets", DataElementGroupSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElementCategories", DataElementCategories.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElementCategoryCombos", DataElementCategoryCombos.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElementCategoryOptions", DataElementCategoryOptions.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElementCategoryOptionCombos", DataElementCategoryOptionCombos.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "OrganisationUnits", OrganisationUnits.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "OrganisationUnitGroups", OrganisationUnitGroups.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "OrganisationUnitGroupSets", OrganisationUnitGroupSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataSets", DataSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Users", Users.class, requestMethods, mediaTypes ) );
    }
}
