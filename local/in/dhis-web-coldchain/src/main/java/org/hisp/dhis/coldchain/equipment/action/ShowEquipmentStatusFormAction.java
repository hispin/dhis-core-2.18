package org.hisp.dhis.coldchain.equipment.action;

import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentStatusFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private EquipmentInstanceService equipmentInstanceService;

    public void setEquipmentInstanceService( EquipmentInstanceService equipmentInstanceService )
    {
        this.equipmentInstanceService = equipmentInstanceService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private Integer equipmentInstanceId;
    
    public void setEquipmentInstanceId( Integer equipmentInstanceId )
    {
        this.equipmentInstanceId = equipmentInstanceId;
    }

    public Integer getEquipmentInstanceId()
    {
        return equipmentInstanceId;
    }
    
    private EquipmentInstance equipmentInstance;
    
    public EquipmentInstance getEquipmentInstance()
    {
        return equipmentInstance;
    }

    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        if ( equipmentInstanceId != null )
        {
            equipmentInstance = equipmentInstanceService.getEquipmentInstance( equipmentInstanceId );
        }
        
        //equipmentInstance.getOrganisationUnit().getName();
        //equipmentInstance.getCatalog().getName();
        
        return SUCCESS;
    }
}
