package org.hisp.dhis.patient.api.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Task
{
    private int id;
    
    private boolean completed;
    
    private int programStageId;

    private String programStageName;

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

    @XmlAttribute
    public String getProgramStageName()
    {
        return programStageName;
    }

    public void setProgramStageName( String programStageName )
    {
        this.programStageName = programStageName;
    }
}
