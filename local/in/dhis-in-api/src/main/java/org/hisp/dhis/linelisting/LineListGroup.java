package org.hisp.dhis.linelisting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

public class LineListGroup
{
    /**
     * The unique identifier for this LineListing Group / Program
     */
    private int id;

    /**
     * Name of Group / LineList Program. Required and unique.
     */
    private String name;
    
    /**
     * Shortname of LineList Group. Required and unique. Used as Name of the LineListing
     * Data Value Table for Individual Programs
     */
    private String shortName;
    
    /**
     * Description of the LineListing Group.
     */
    private String description;

    /**
     * The PeriodType indicating the frequency that this LineList should be used
     * for Data Entry
     */
    private PeriodType periodType;

    /**
     * All Elements associated with this Line List Group which comprise the 
     * columns in the LineList data entry.
     */
    private Collection<LineListElement> lineListElements = new HashSet<LineListElement>();
    
    /**
     * All Sources that register data with this LineList Group.
     */
    private Set<Source> sources = new HashSet<Source>();
    
    /**
    * Property indicating whether the LineList is locked for data entry.
    */
    private transient Boolean locked = false;
    
    /**
     * All locked periods within the LineList Group.
     */    
    private Set<Period> lineListLockedPeriods = new HashSet<Period>();

    /**
     * All locked Sources within the LineList Group.
     */    
    private Set<Source> lockedSources = new HashSet<Source>();
    
    /**
     * Indicating position in the custom sort order.
     */
    private Integer sortOrder;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public LineListGroup()
    {
    }

    public LineListGroup( String name, PeriodType periodType )
    {
        this.name = name;
        this.periodType = periodType;
    }
    
    public LineListGroup( String name, String shortName, PeriodType periodType )
    {
        this.name = name;
        this.shortName = shortName;
        this.periodType = periodType;
    }
    
    public LineListGroup( String name, String shortName, String description, PeriodType periodType )
    {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.periodType = periodType;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
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

        if ( !(o instanceof LineListGroup) )
        {
            return false;
        }

        final LineListGroup other = (LineListGroup) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String code )
    {
        this.description = code;
    }
    
    public String getAlternativeName()
    {
        return getShortName();
    }
    
    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    public Collection<LineListElement> getLineListElements()
    {
        return lineListElements;
    }

    public void setLineListElements( Collection<LineListElement> lineListElements )
    {
        this.lineListElements = lineListElements;
    }

    public Set<Source> getSources()
    {
        return sources;
    }

    public void setSources( Set<Source> sources )
    {
        this.sources = sources;
    }

    public Set<Period> getLineListLockedPeriods()
    {
        return lineListLockedPeriods;
    }

    public void setLineListLockedPeriods( Set<Period> lockedPeriods )
    {
        this.lineListLockedPeriods = lockedPeriods;
    }

    public Set<Source> getLockedSources()
    {
        return lockedSources;
    }

    public void setLockedSources( Set<Source> lockedSources )
    {
        this.lockedSources = lockedSources;
    }

    public Boolean getLocked()
    {
        return locked;
    }

    public void setLocked( Boolean locked )
    {
        this.locked = locked;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

}
