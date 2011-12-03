package org.hisp.dhis.mapgeneration;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

/**
 * An interval is a collection of map objects that have been distributed into this interval.
 * 
 * It contains all the map objects that have values that lie in the range of values this interval covers.
 * 
 * @author Olai Solheim <olais@ifi.uio.no>
 */
public class Interval {
    
    /**
     * The color value associated with this interval.
     */
    private Color color;
    
    /**
     * The low and high boundaries of values this interval covers.
     */
    private double valueLow, valueHigh;
    
    /**
     * The map object members that fall into this interval category.
     */
    private List<InternalMapObject> members;
    
    public Interval(double valueLow, double valueHigh) {
        this.valueLow = valueLow;
        this.valueHigh = valueHigh;
        
        this.members = new LinkedList<InternalMapObject>();
    }
    
    /**
     * Gets the low value of this interval.
     * @return the low value
     */
    public double getValueLow() {
        return this.valueLow;
    }
    
    /**
     * Sets the low value of this interval.
     * @param valueLow the low value
     */
    public void setValueLow(double valueLow) {
        this.valueLow = valueLow;
    }
    
    /**
     * Gets the high value of this interval.
     * @return the high value
     */
    public double getValueHigh() {
        return this.valueHigh;
    }
    
    /**
     * Sets the high value of this interval.
     * @param valueHigh the high value
     */
    public void setValueHigh(double valueHigh) {
        this.valueHigh = valueHigh;
    }
    
    /**
     * Gets the color this interval has on the map.
     * @return the color
     */
    public Color getColor() {
        return this.color;
    }
    
    /**
     * Sets the color this interval has on the map.
     * @param color the color
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Adds a map object to this interval category.
     * @param member the member to add
     */
    public void addMember(InternalMapObject member) {     
        this.members.add(member);
    }
    
    /**
     * Returns a list of the members that have fallen into this interval category, or null if none.
     * @return the list of members
     */
    public List<InternalMapObject> getMembers() {
        return this.members;
    }
}
