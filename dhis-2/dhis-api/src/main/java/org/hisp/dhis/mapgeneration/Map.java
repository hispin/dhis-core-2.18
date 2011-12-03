package org.hisp.dhis.mapgeneration;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.mapping.MapView;

/**
 * The Map class represents a single map that may contain several layers.
 * 
 * @author Kjetil Andresen <kjetand@ifi.uio.no>
 */
public class Map {

	private List<MapView> mapViews;
	
	/**
	 * Construct an initially empty map.
	 */
	public Map() {
		mapViews = new ArrayList<MapView>();
	}
	
	/**
	 * Construct a map with a single initial layer. 
	 * @param mapView the initial layer
	 */
	public Map(MapView mapView) {
		mapViews = new ArrayList<MapView>();
		mapViews.add(mapView);
	}
	
	/**
	 * Construct a map with a given list of predefined layers.
	 * @param mapViews the list of layers
	 */
	public Map(List<MapView> mapViews) {
		this.mapViews = mapViews;
	}
	
	/**
	 * Add a layer to this map.
	 * @param mapView the layer
	 */
	public void addMapView(MapView mapView) {
		mapViews.add(mapView);
	}
	
	/**
	 * Add a list of layers to this map.
	 * @param mapViews the list of layers
	 */
	public void addMapViews(List<MapView> mapViews) {
		this.mapViews.addAll(mapViews);
	}
	
	/**
	 * Gets all the layers currently associated with this map.
	 * @return the list of layers
	 */
	public List<MapView> getMapViews() {
		return mapViews;
	}
}
