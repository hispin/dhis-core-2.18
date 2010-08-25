package org.hisp.dhis.patient.api.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityPlanItem
{

    private Beneficiary beneficiary;

    private Task task;

    private Date dueDate;

    public Beneficiary getBeneficiary()
    {
        return beneficiary;
    }

    public void setBeneficiary( Beneficiary beneficiary )
    {
        this.beneficiary = beneficiary;
    }

    public Task getTask()
    {
        return task;
    }

    public void setTask( Task task )
    {
        this.task = task;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( Date dueDate )
    {
        this.dueDate = dueDate;
    }

}
