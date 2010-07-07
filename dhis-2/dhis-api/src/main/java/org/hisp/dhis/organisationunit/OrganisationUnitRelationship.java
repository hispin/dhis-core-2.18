package org.hisp.dhis.organisationunit;

public class OrganisationUnitRelationship
{
    private int parentId;
    
    private int childId;
    
    public OrganisationUnitRelationship()
    {
    }
    
    public OrganisationUnitRelationship( int parentId, int childId )
    {
        this.parentId = parentId;
        this.childId = childId;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId( int parentId )
    {
        this.parentId = parentId;
    }

    public int getChildId()
    {
        return childId;
    }

    public void setChildId( int childId )
    {
        this.childId = childId;
    }
}
