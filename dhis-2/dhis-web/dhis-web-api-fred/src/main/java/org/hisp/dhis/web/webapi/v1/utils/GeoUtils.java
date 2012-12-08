package org.hisp.dhis.web.webapi.v1.utils;

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

import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
final public class GeoUtils
{
    public static enum CoordinateOrder
    {
        COORDINATE_LATLNG,
        COORDINATE_LNGLAT
    }

    public static class Coordinates
    {
        public Double lat = 0.0d;

        public Double lng = 0.0d;

        @Override
        public String toString()
        {
            return "Coordinates{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
        }
    }

    // helper for most common case, our internal lnglat to latlng
    public static Coordinates parseCoordinates( String coordinatesString )
    {
        return parseCoordinates( coordinatesString, CoordinateOrder.COORDINATE_LNGLAT, CoordinateOrder.COORDINATE_LATLNG );
    }

    public static Coordinates parseCoordinates( String coordinatesString, CoordinateOrder from, CoordinateOrder to )
    {
        Coordinates coordinates = new Coordinates();

        try
        {
            List list = new ObjectMapper().readValue( coordinatesString, List.class );

            if ( from == CoordinateOrder.COORDINATE_LATLNG )
            {
                if ( to == CoordinateOrder.COORDINATE_LATLNG )
                {
                    coordinates.lat = convertToDouble( list.get( 0 ) );
                    coordinates.lng = convertToDouble( list.get( 1 ) );
                }
                else if ( to == CoordinateOrder.COORDINATE_LNGLAT )
                {
                    coordinates.lat = convertToDouble( list.get( 0 ) );
                    coordinates.lng = convertToDouble( list.get( 1 ) );
                }
            }
            else if ( from == CoordinateOrder.COORDINATE_LNGLAT )
            {
                if ( to == CoordinateOrder.COORDINATE_LATLNG )
                {
                    coordinates.lat = convertToDouble( list.get( 1 ) );
                    coordinates.lng = convertToDouble( list.get( 0 ) );
                }
                else if ( to == CoordinateOrder.COORDINATE_LNGLAT )
                {
                    coordinates.lat = convertToDouble( list.get( 1 ) );
                    coordinates.lng = convertToDouble( list.get( 0 ) );
                }
            }
        }
        catch ( Exception ignored )
        {
        }

        return coordinates;
    }

    private static Double convertToDouble( Object object )
    {
        Double d = 0.0d;

        if ( Double.class.isInstance( object ) )
        {
            d = (Double) object;
        }
        else if ( Integer.class.isInstance( object ) )
        {
            Integer lng = (Integer) object;
            d = Double.valueOf( lng );
        }

        return d;
    }
}
