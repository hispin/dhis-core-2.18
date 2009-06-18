package org.hisp.dhis.gis.ext;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hisp.dhis.gis.Legend;
import org.hisp.dhis.gis.LegendSet;
import org.hisp.dhis.gis.action.export.FeatureStructure;
import org.hisp.dhis.indicator.Indicator;

public class BagSession
{
    private Indicator indicator;

    private String startDate;

    private String endDate;

    private List<org.hisp.dhis.gis.ext.Feature> features;

    private LegendSet legendSet;

    public Indicator getIndicator()
    {
        return indicator;
    }

    public void setIndicator( Indicator indicator )
    {
        this.indicator = indicator;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public List<org.hisp.dhis.gis.ext.Feature> getFeatures()
    {
        return features;
    }

    public void setFeatures( List<org.hisp.dhis.gis.ext.Feature> features )
    {
        this.features = features;
    }

    public LegendSet getLegendSet()
    {
        return legendSet;
    }

    public void setLegendSet( LegendSet legendSet )
    {
        this.legendSet = legendSet;
    }

    public BagSession( Indicator indicator, String startDate, String endDate, List<Feature> features,
        LegendSet legendSet )
    {
        super();
        this.indicator = indicator;
        this.startDate = startDate;
        this.endDate = endDate;
        this.features = features;
        this.legendSet = legendSet;
    }

    public BagSession()
    {
        // TODO Auto-generated constructor stub
    }

    public Title getTitle()
    {
        return new Title( this.getIndicator().getName(), this.getStartDate(), this.getEndDate() );
    }

    public List<Legend> getLegends()
    {
        for ( Legend legend : this.getLegendSet().getLegends() )
        {

            legend.setColor( "#" + legend.getColor() );

        }
        return this.getLegendSet().getLegends();
    }

    public List<FeatureStructure> getFeatureStructure()
    {
        List<FeatureStructure> featureStructure = new ArrayList<FeatureStructure>();
        for ( Feature feature : this.getFeatures() )
        {
            if ( feature.getFeature() != null )
            {
                featureStructure.add( new FeatureStructure( feature.getFeature().getFeatureCode(), feature.getColor(),
                    feature.getAggregatedDataValue(), feature.getFeature().getOrganisationUnit().getId() ) );
            }
        }
        return featureStructure;
    }

    public Map<String, String> getFeature()
    {
        Map<String, String> result = new HashMap<String, String>();

        for ( Feature feature : this.features )
        {

            if ( feature.getFeature() != null )
            {
                String key = feature.getFeature().getFeatureCode();

                String color = feature.getColor();

                double value = feature.getAggregatedDataValue();

                result.put( key, color + "-" + value );
            }

        }

        return result;

    }
    
    public Double getIndicatorValue(String geoCode){
        
        for ( Feature feature : this.features )
        {

            if ( feature.getFeature() != null )
            {
                String key = feature.getFeature().getFeatureCode();
                
                if(key.equalsIgnoreCase( geoCode )){
                    return feature.getAggregatedDataValue(); 
                }        
               
            }

        }
        
        return null;
    }

}
