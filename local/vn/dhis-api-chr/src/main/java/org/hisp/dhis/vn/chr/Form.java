package org.hisp.dhis.vn.chr;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;


public class Form implements java.io.Serializable {

	/**
	 * The database internal identifier for this Form.
	 */
	private int id;
	
	/**
	 * The name of this Form. Required and unique.
	 */
	private String name;
	
	/**
	 * The label of this Form. Required and unique.
	 */
	private String label;
	
	/**
	 * The number of rows of this Form
	 */
	private int noRow;
	
	/**
	 * The number of columns of this Form
	 */
	private int noColumn;
	
	/**
	 * The number of columns of this FormLink
	 */
	private int noColumnLink;
	
	/**
	 * The icon of this Form
	 */
	private String icon;
	
	/**
	 * The visible Form
	 */
	private String visible;
	
	/**
	 * The Attached form
	 */
	private String attached;
	
	/**
	 * Sort Element of the Form by desc 
	 */
	private String desc1;
	
	/**
	 * Element Group of the Form x
	 */
	private Collection<Element> elements;
	
	/**
	 * Sort Egroup List of the Form
	 */
	private Collection<Egroup> egroups;

	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public Form() {
	}

	public Form(String name, String label, int noRow, int noColumn,
			int noColumnLink, String visible, String attached, String desc1) {
		this.name = name;
		this.label = label;
		this.noRow = noRow;
		this.noColumn = noColumn;
		this.noColumnLink = noColumnLink;
		this.visible = visible;
		this.attached = attached;
		this.desc1 = desc1;
	}

	public Form(String name, String label, int noRow, int noColumn,
			int noColumnLink, String icon, String visible, String attached,
			String desc1, Collection<Element> elements, Collection<Egroup> egroups) {
		this.name = name;
		this.label = label;
		this.noRow = noRow;
		this.noColumn = noColumn;
		this.noColumnLink = noColumnLink;
		this.icon = icon;
		this.visible = visible;
		this.attached = attached;
		this.desc1 = desc1;
		this.elements = elements;
		this.egroups = egroups;
	}
	// -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof Form) )
        {
            return false;
        }

        final Form other = (Form) o;

        return name.equals( other.getName() );
    }
    
    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

	// -------------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------------

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getNoRow() {
		return noRow;
	}

	public void setNoRow(int noRow) {
		this.noRow = noRow;
	}

	public int getNoColumn() {
		return noColumn;
	}

	public void setNoColumn(int noColumn) {
		this.noColumn = noColumn;
	}

	public int getNoColumnLink() {
		return noColumnLink;
	}

	public void setNoColumnLink(int noColumnLink) {
		this.noColumnLink = noColumnLink;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getAttached() {
		return attached;
	}

	public void setAttached(String attached) {
		this.attached = attached;
	}

	public String getDesc1() {
		return desc1;
	}

	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}

	public Collection<Element> getElements() {
		return elements;
	}

	public void setElements(Collection<Element> elements) {
		this.elements = elements;
	}

	public Collection<Egroup> getEgroups() {
		return egroups;
	}

	public void setEgroups(Collection<Egroup> egroups) {
		this.egroups = egroups;
	}
	
}
