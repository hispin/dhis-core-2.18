package org.hisp.dhis.api.utils;

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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Marshaller;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.Attributes;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseLinkableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategories;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryCombos;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombos;
import org.hisp.dhis.dataelement.DataElementCategoryOptions;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementGroupSets;
import org.hisp.dhis.dataelement.DataElementGroups;
import org.hisp.dhis.dataelement.DataElements;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrations;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSets;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorGroupSets;
import org.hisp.dhis.indicator.IndicatorGroups;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.indicator.IndicatorTypes;
import org.hisp.dhis.indicator.Indicators;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.Maps;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSets;
import org.hisp.dhis.organisationunit.OrganisationUnitGroups;
import org.hisp.dhis.organisationunit.OrganisationUnits;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.Users;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class WebLinkPopulatorListener
    extends Marshaller.Listener
{
    private HttpServletRequest request;

    private static Map<Class, String> resourcePaths = new HashMap<Class, String>();

    private String rootPath = null;

    static
    {
        resourcePaths.put( Attributes.class, "attributes" );
        resourcePaths.put( Attribute.class, "attributes" );

        resourcePaths.put( Charts.class, "charts" );
        resourcePaths.put( Chart.class, "charts" );

        resourcePaths.put( Maps.class, "maps" );
        resourcePaths.put( MapView.class, "maps" );

        resourcePaths.put( CompleteDataSetRegistrations.class, "completeDataSetRegistrations" );
        resourcePaths.put( CompleteDataSetRegistration.class, "completeDataSetRegistrations" );

        resourcePaths.put( Indicators.class, "indicators" );
        resourcePaths.put( Indicator.class, "indicators" );
        resourcePaths.put( IndicatorGroups.class, "indicatorGroups" );
        resourcePaths.put( IndicatorGroup.class, "indicatorGroups" );
        resourcePaths.put( IndicatorGroupSets.class, "indicatorGroupSets" );
        resourcePaths.put( IndicatorGroupSet.class, "indicatorGroupSets" );
        resourcePaths.put( IndicatorTypes.class, "indicatorTypes" );
        resourcePaths.put( IndicatorType.class, "indicatorTypes" );

        resourcePaths.put( DataElements.class, "dataElements" );
        resourcePaths.put( DataElement.class, "dataElements" );
        resourcePaths.put( DataElementGroups.class, "dataElementGroups" );
        resourcePaths.put( DataElementGroup.class, "dataElementGroups" );
        resourcePaths.put( DataElementGroupSets.class, "dataElementGroupSets" );
        resourcePaths.put( DataElementGroupSet.class, "dataElementGroupSets" );

        resourcePaths.put( DataElementCategories.class, "dataElementCategories" );
        resourcePaths.put( DataElementCategory.class, "dataElementCategories" );
        resourcePaths.put( DataElementCategoryCombos.class, "dataElementCategoryCombos" );
        resourcePaths.put( DataElementCategoryCombo.class, "dataElementCategoryCombos" );
        resourcePaths.put( DataElementCategoryOptions.class, "dataElementCategoryOptions" );
        resourcePaths.put( DataElementCategoryOption.class, "dataElementCategoryOptions" );
        resourcePaths.put( DataElementCategoryOptionCombos.class, "dataElementCategoryOptionCombos" );
        resourcePaths.put( DataElementCategoryOptionCombo.class, "dataElementCategoryOptionCombos" );

        resourcePaths.put( OrganisationUnits.class, "organisationUnits" );
        resourcePaths.put( OrganisationUnit.class, "organisationUnits" );
        resourcePaths.put( OrganisationUnitGroups.class, "organisationUnitGroups" );
        resourcePaths.put( OrganisationUnitGroup.class, "organisationUnitGroups" );
        resourcePaths.put( OrganisationUnitGroupSets.class, "organisationUnitGroupSets" );
        resourcePaths.put( OrganisationUnitGroupSet.class, "organisationUnitGroupSets" );

        resourcePaths.put( DataSets.class, "dataSets" );
        resourcePaths.put( DataSet.class, "dataSets" );

        resourcePaths.put( Users.class, "users" );
        resourcePaths.put( User.class, "users" );
    }

    public WebLinkPopulatorListener( HttpServletRequest request )
    {
        this.request = request;
    }

    @Override
    public void beforeMarshal( Object source )
    {
        if ( source instanceof BaseIdentifiableObject )
        {
            BaseIdentifiableObject entity = (BaseIdentifiableObject) source;
            entity.setLink( getPathWithUid( entity ) );
        }
        else if ( source instanceof BaseLinkableObject )
        {
            BaseLinkableObject linkable = (BaseLinkableObject) source;
            linkable.setLink( getBasePath( linkable.getClass() ) );
        }

    }

    private String getPathWithUid( BaseIdentifiableObject baseIdentifiableObject )
    {
        return getBasePath( baseIdentifiableObject.getClass() ) + "/" + baseIdentifiableObject.getUid();
    }

    private String getBasePath( Class<?> clazz )
    {
        if ( rootPath == null )
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append( request.getScheme() );

            buffer.append( "://" + request.getServerName() );

            if ( request.getServerPort() != 80 && request.getServerPort() != 443 )
            {
                buffer.append( ":" + request.getServerPort() );
            }

            buffer.append( request.getContextPath() );
            buffer.append( request.getServletPath() );

            rootPath = buffer.toString();
        }

        String resourcePath = resourcePaths.get( clazz );

        // in some cases, the class is a dynamic subclass (usually subclassed
        // with javaassist), so
        // we need to fetch the superClass instead.
        if ( resourcePath == null )
        {
            resourcePath = resourcePaths.get( clazz.getSuperclass() );
        }

        return rootPath + "/" + resourcePath;
    }

}
