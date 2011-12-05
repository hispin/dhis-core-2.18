package org.hisp.dhis.api.view;

import java.awt.image.BufferedImage;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.mapgeneration.MapGenerationService;
import org.hisp.dhis.mapping.MapView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;

public class MapGenerationView
    extends AbstractView
{

    @Autowired
    MapGenerationService mapGenerationService;
    
    public MapGenerationView()
    {
        super();
        setContentType( "image/png" );
    }

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request,
        HttpServletResponse response )
        throws Exception
    {
        MapView mapView = (MapView) model.get( "model" );
        BufferedImage image = mapGenerationService.generateMapImage( new org.hisp.dhis.mapgeneration.Map( mapView ) );

        response.setContentType( MediaType.IMAGE_PNG.toString() );
        ImageIO.write( image, "PNG", response.getOutputStream() );
       

    }

}
