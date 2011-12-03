package org.hisp.dhis.api.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.mapgeneration.Map;
import org.hisp.dhis.mapgeneration.MapGenerationService;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( value = "/maps" )
public class MapController
{
    @Autowired
    MapGenerationService mapGenerationService;

    @Autowired
    MappingService mappingService;

    @RequestMapping( value = "/{id}" )
    public void getMapImage(@PathVariable( "id" ) int id, HttpServletResponse response )
        throws IOException
    {
        MapView mapView = mappingService.getMapView( id );
        BufferedImage image = mapGenerationService.generateMapImage( new Map( mapView ) );

        ImageIO.write( image, "PNG", response.getOutputStream() );
    }

}
