package org.hisp.dhis.mobile.model;

public class Task
{

    private int progStageInstId;

    private int progStageId;

    private boolean complete;

    public int getProgStageInstId()
    {
        return progStageInstId;
    }

    public void setProgStageInstId( int progStageInstId )
    {
        this.progStageInstId = progStageInstId;
    }

    public int getProgStageId()
    {
        return progStageId;
    }

    public void setProgStageId( int progStageId )
    {
        this.progStageId = progStageId;
    }

    public boolean isComplete()
    {
        return complete;
    }

    public void setComplete( boolean complete )
    {
        this.complete = complete;
    }

}
