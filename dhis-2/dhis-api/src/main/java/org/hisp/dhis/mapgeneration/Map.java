package org.hisp.dhis.mapgeneration;

/*
 * Copyright (c) 2004-2010, University of Oslo
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
import java.util.List;

import org.hisp.dhis.mapping.MapView;

/**
 * The Map class represents a single map that may contain several layers.
 * 
 * @author Kjetil Andresen <kjetand@ifi.uio.no>
 */
public class Map
{
    private List<MapView> mapViews;

    /**
     * Construct an initially empty map.
     */
    public Map()
    {
        mapViews = new ArrayList<MapView>();
    }

    /**
     * Construct a map with a single initial layer.
     * 
     * @param mapView the initial layer
     */
    public Map( MapView mapView )
    {
        mapViews = new ArrayList<MapView>();
        mapViews.add( mapView );
    }

    /**
     * Construct a map with a given list of predefined layers.
     * 
     * @param mapViews the list of layers
     */
    public Map( List<MapView> mapViews )
    {
        this.mapViews = mapViews;
    }

    /**
     * Add a layer to this map.
     * 
     * @param mapView the layer
     */
    public void addMapView( MapView mapView )
    {
        mapViews.add( mapView );
    }

    /**
     * Add a list of layers to this map.
     * 
     * @param mapViews the list of layers
     */
    public void addMapViews( List<MapView> mapViews )
    {
        this.mapViews.addAll( mapViews );
    }

    /**
     * Gets all the layers currently associated with this map.
     * 
     * @return the list of layers
     */
    public List<MapView> getMapViews()
    {
        return mapViews;
    }
}
