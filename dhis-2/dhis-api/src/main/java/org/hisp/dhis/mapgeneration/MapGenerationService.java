package org.hisp.dhis.mapgeneration;

import java.awt.image.BufferedImage;

/**
 * The MapGenerationService interface generates map images from Map objects.
 * 
 * Map objects may be built by adding layers to them, and once passed to generateMapImage it will render 
 * an image representing the map according to the properties defined by Map and MapView.
 * 
 * TODO Extend with more configuration options, e.g. width
 * 
 * @author Kenneth Solbø Andersen <kennetsa@ifi.uio.no>
 * @author Olai Solheim <olais@ifi.uio.no>
 */
public interface MapGenerationService {
	
	public final String ID = MapGenerationService.class.getName();
	
	/**
	 * Generate an image that represents this map.
	 * @param map the map that will be rendered
	 * @return the rendered map image
	 */
	public BufferedImage generateMapImage(Map map);
}
