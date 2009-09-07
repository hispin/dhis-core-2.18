package org.hisp.dhis.den.action;

import org.hisp.dhis.den.api.LLDataValueStore;

import com.opensymphony.xwork2.Action;

public class RemoveLLRecordAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LLDataValueStore lldataValueStore;
    
    public void setLldataValueStore( LLDataValueStore lldataValueStore )
    {
        this.lldataValueStore = lldataValueStore;
    } 
    
    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------
 
    private Integer recordId;
    
    public void setRecordId( Integer recordId )
    {
        this.recordId = recordId;
    }
        
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        lldataValueStore.removeLLRecord( recordId );

        return SUCCESS;
    }

}
