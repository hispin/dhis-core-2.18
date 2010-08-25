package org.hisp.dhis.web.api.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Task
{
    private int id;
    
    private boolean completed;
    
    private int programStageId;

    @XmlAttribute
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @XmlAttribute
    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    @XmlAttribute
    public int getProgramStageId()
    {
        return programStageId;
    }

    public void setProgramStageId( int programStageId )
    {
        this.programStageId = programStageId;
    }

}
