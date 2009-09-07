package org.hisp.dhis.linelisting.linelistdataelementmapping;

import java.io.Serializable;

import org.hisp.dhis.dataelement.Operand;

public class LineListOperand
    implements Serializable, Comparable<LineListOperand>
{
    public static final String DOT_SEPARATOR = ".";
    public static final String COLON_SEPARATOR = ":";

    private String id;

    private int lineListGroupId;

    private int lineListElementId;

    private int lineListOptionId;

    private String operandName;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LineListOperand()
    {
    }
    
    public LineListOperand( int lineListGroupId, int lineListElementId, int lineListOptionId )
    {
        this.id = lineListGroupId + COLON_SEPARATOR + lineListElementId + DOT_SEPARATOR + lineListOptionId;
        this.lineListGroupId = lineListGroupId;
        this.lineListElementId = lineListElementId;
        this.lineListOptionId = lineListOptionId;
    }

    public LineListOperand( int lineListGroupId, int lineListElementId, int lineListOptionId, String operandName )
    {
        this.id = lineListGroupId + COLON_SEPARATOR + lineListElementId + DOT_SEPARATOR + lineListOptionId;
        this.lineListGroupId = lineListGroupId;
        this.lineListElementId = lineListElementId;
        this.lineListOptionId = lineListOptionId;
        this.operandName = operandName;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        
        int result = 1;
        
        result = prime * result + lineListGroupId;
        result = prime * result + lineListElementId;
        result = prime * result + lineListOptionId;
        
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final LineListOperand other = (LineListOperand) object;
        
        return lineListGroupId == other.lineListGroupId && lineListElementId == other.lineListElementId && lineListOptionId == other.lineListOptionId;
    }

    @Override
    public String toString()
    {
        return "[LineListGroupId: " + lineListGroupId + ", LineListElementId: " + lineListElementId + ", LineListOptionId: " + lineListOptionId + "]";
    }
    
    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------


    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public int getLineListGroupId()
    {
        return lineListGroupId;
    }

    public void setLineListGroupId( int lineListGroupId )
    {
        this.lineListGroupId = lineListGroupId;
    }

    public int getLineListElementId()
    {
        return lineListElementId;
    }

    public void setLineListElementId( int lineListElementId )
    {
        this.lineListElementId = lineListElementId;
    }

    public int getLineListOptionId()
    {
        return lineListOptionId;
    }

    public void setLineListOptionId( int lineListOptionId )
    {
        this.lineListOptionId = lineListOptionId;
    }

    public String getOperandName()
    {
        return operandName;
    }

    public void setOperandName( String operandName )
    {
        this.operandName = operandName;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // compareTo
    // -------------------------------------------------------------------------


    public int compareTo( LineListOperand other )
    {
        if ( this.getLineListGroupId() != other.getLineListGroupId() )
        {
            return this.getLineListGroupId() - other.getLineListGroupId();
        }
        
        if ( this.getLineListElementId() != other.getLineListElementId() )
        {
            return this.getLineListElementId() - other.getLineListElementId();
        }
        
        return this.getLineListOptionId() - other.getLineListOptionId();
    }

}
