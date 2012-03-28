package org.hisp.dhis.api.webdomain;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.attribute.Attributes;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.common.BaseCollection;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.constant.Constants;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSets;
import org.hisp.dhis.document.Documents;
import org.hisp.dhis.dxf2.datavalueset.DataValueSets;
import org.hisp.dhis.indicator.IndicatorGroupSets;
import org.hisp.dhis.indicator.IndicatorGroups;
import org.hisp.dhis.indicator.IndicatorTypes;
import org.hisp.dhis.indicator.Indicators;
import org.hisp.dhis.mapping.MapLayers;
import org.hisp.dhis.mapping.MapLegendSets;
import org.hisp.dhis.mapping.MapLegends;
import org.hisp.dhis.mapping.Maps;
import org.hisp.dhis.message.MessageConversations;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSets;
import org.hisp.dhis.organisationunit.OrganisationUnitGroups;
import org.hisp.dhis.organisationunit.OrganisationUnits;
import org.hisp.dhis.report.Reports;
import org.hisp.dhis.reporttable.ReportTables;
import org.hisp.dhis.sqlview.SqlViews;
import org.hisp.dhis.user.UserAuthorityGroups;
import org.hisp.dhis.user.UserGroups;
import org.hisp.dhis.user.Users;
import org.hisp.dhis.validation.ValidationRuleGroups;
import org.hisp.dhis.validation.ValidationRules;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * At some point this class will be extended to show all available options
 * for a current user for this resource. For now it is only used for index page.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "dxf2", namespace = Dxf2Namespace.NAMESPACE )
public class Resources
    extends BaseCollection
{
    private List<Resource> resources = new ArrayList<Resource>();

    public Resources()
    {
        generateResources();
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "resources", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "resource", namespace = Dxf2Namespace.NAMESPACE )
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
        List<String> requestMethods = new ArrayList<String>();
        requestMethods.add( RequestMethod.GET.toString() );

        List<String> mediaTypes = new ArrayList<String>();
        mediaTypes.add( MediaType.TEXT_HTML.toString() );
        mediaTypes.add( MediaType.APPLICATION_JSON.toString() );
        mediaTypes.add( MediaType.APPLICATION_XML.toString() );
        mediaTypes.add( new MediaType( "application", "javascript" ).toString() );

        resources.add( new Resource( "AttributeTypes", Attributes.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Categories", DataElementCategories.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "CategoryCombos", DataElementCategoryCombos.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "CategoryOptions", DataElementCategoryOptions.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "CategoryOptionCombos", DataElementCategoryOptionCombos.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Charts", Charts.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Constants", Constants.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElements", DataElements.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElementGroups", DataElementGroups.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataElementGroupSets", DataElementGroupSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataSets", DataSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "DataValueSets", DataValueSets.class, requestMethods, mediaTypes.subList( 0, 0 ) ) );
        resources.add( new Resource( "Documents", Documents.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Indicators", Indicators.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "IndicatorGroups", IndicatorGroups.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "IndicatorGroupSets", IndicatorGroupSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "IndicatorTypes", IndicatorTypes.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Maps", Maps.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "MapLegends", MapLegends.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "MapLegendSets", MapLegendSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "MapLayers", MapLayers.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "MessageConversations", MessageConversations.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "OrganisationUnits", OrganisationUnits.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "OrganisationUnitGroups", OrganisationUnitGroups.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "OrganisationUnitGroupSets", OrganisationUnitGroupSets.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Reports", Reports.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "ReportTables", ReportTables.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "SqlViews", SqlViews.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "Users", Users.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "UserGroups", UserGroups.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "UserAuthorityGroups", UserAuthorityGroups.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "ValidationRules", ValidationRules.class, requestMethods, mediaTypes ) );
        resources.add( new Resource( "ValidationRuleGroups", ValidationRuleGroups.class, requestMethods, mediaTypes ) );
    }
}
