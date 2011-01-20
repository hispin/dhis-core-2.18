package org.hisp.dhis.datamart.action;

import org.hisp.dhis.datamart.DataMartScheduler;

import com.opensymphony.xwork2.Action;

public class ExecuteExportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataMartScheduler dataMartScheduler;

    public void setDataMartScheduler( DataMartScheduler dataMartScheduler )
    {
        this.dataMartScheduler = dataMartScheduler;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        dataMartScheduler.executeDataMartExport();
        
        return SUCCESS;
    }
}
