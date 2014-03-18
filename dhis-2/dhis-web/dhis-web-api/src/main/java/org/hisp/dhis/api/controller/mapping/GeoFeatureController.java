package org.hisp.dhis.api.controller.mapping;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import static org.hisp.dhis.util.ContextUtils.clearIfNotModified;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.webdomain.GeoFeature;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.NameableObjectUtils;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.system.filter.OrganisationUnitWithValidCoordinatesFilter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping(value = GeoFeatureController.RESOURCE_PATH)
public class GeoFeatureController
{
    public static final String RESOURCE_PATH = "/geoFeatures";

    private static final Map<String, Integer> FEATURE_TYPE_MAP = new HashMap<String, Integer>() { {
        put( OrganisationUnit.FEATURETYPE_POINT, GeoFeature.TYPE_POINT );
        put( OrganisationUnit.FEATURETYPE_MULTIPOLYGON, GeoFeature.TYPE_POLYGON );
        put( OrganisationUnit.FEATURETYPE_POLYGON, GeoFeature.TYPE_POLYGON );
        put( null, 0 );
    } };
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    @RequestMapping( method = RequestMethod.GET, produces = "application/json" )
    public void getGeoFeatures( @RequestParam String ou, @RequestParam Map<String, String> parameters,
        HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        WebOptions options = new WebOptions( parameters );
        boolean includeGroupSets = "detailed".equals( options.getViewClass() );
        
        Set<String> set = new HashSet<String>();
        set.add( ou );
        
        DataQueryParams params = analyticsService.getFromUrl( set, null, AggregationType.SUM, null, false, false, false, false, false, false, null );
        
        DimensionalObject dim = params.getDimension( DimensionalObject.ORGUNIT_DIM_ID );
        
        List<OrganisationUnit> organisationUnits = NameableObjectUtils.asTypedList( dim.getItems() );

        FilterUtils.filter( organisationUnits, new OrganisationUnitWithValidCoordinatesFilter() );

        boolean modified = !clearIfNotModified( request, response, organisationUnits );

        if ( !modified )
        {
            return;
        }

        Collection<OrganisationUnitGroupSet> groupSets = includeGroupSets ? organisationUnitGroupService.getAllOrganisationUnitGroupSets() : null;
        
        List<GeoFeature> features = new ArrayList<GeoFeature>();
        
        for ( OrganisationUnit unit : organisationUnits )
        {
            GeoFeature feature = new GeoFeature();
            feature.setId( unit.getUid() );
            feature.setNa( unit.getDisplayName() );
            feature.setHcd( unit.hasChildrenWithCoordinates() );
            feature.setHcu( unit.hasCoordinatesUp() );
            feature.setLe( unit.getLevel() );
            feature.setPg( unit.getParentGraph() );
            feature.setPi( unit.getParent() != null ? unit.getParent().getUid() : null );
            feature.setPn( unit.getParent() != null ? unit.getParent().getDisplayName() : null );
            feature.setTy( FEATURE_TYPE_MAP.get( unit.getFeatureType() ) );
            feature.setCo( unit.getCoordinates() );
            
            if ( includeGroupSets )
            {
                for ( OrganisationUnitGroupSet groupSet : groupSets )
                {
                    OrganisationUnitGroup group = unit.getGroupInGroupSet( groupSet );
                    
                    if ( group != null )
                    {
                        feature.getDimensions().put( groupSet.getUid(), group.getUid() );
                    }
                }
            }
            
            features.add( feature );
        }
        
        Collections.sort( features, GeoFeatureTypeComparator.INSTANCE );
        
        JacksonUtils.toJson( response.getOutputStream(), features );
    }
    
    static class GeoFeatureTypeComparator
        implements Comparator<GeoFeature>
    {
        public static final GeoFeatureTypeComparator INSTANCE = new GeoFeatureTypeComparator();
        
        @Override
        public int compare( GeoFeature o1, GeoFeature o2 )
        {
            return Integer.valueOf( o1.getTy() ).compareTo( Integer.valueOf( o2.getTy() ) );
        }        
    }
}
