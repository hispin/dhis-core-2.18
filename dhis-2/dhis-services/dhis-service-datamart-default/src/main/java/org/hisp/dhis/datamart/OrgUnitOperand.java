package org.hisp.dhis.datamart;

public class OrgUnitOperand
{
    private int orgUnitId;
    
    private int orgUnitGroupId;
    
    private double value;
    
    public OrgUnitOperand()
    {
    }
    
    public OrgUnitOperand( int orgUnitId, int orgUnitGroupId, double value )
    {
        this.orgUnitId = orgUnitId;
        this.orgUnitGroupId = orgUnitGroupId;
        this.value = value;
    }

    public int getOrgUnitId()
    {
        return orgUnitId;
    }

    public int getOrgUnitGroupId()
    {
        return orgUnitGroupId;
    }

    public double getValue()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + orgUnitId;
        result = prime * result + orgUnitGroupId;
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        
        if ( obj == null )
        {
            return false;
        }
        
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        
        final OrgUnitOperand other = (OrgUnitOperand) obj;
        
        return orgUnitId == other.orgUnitId && orgUnitGroupId == other.orgUnitGroupId;
    }    
}
