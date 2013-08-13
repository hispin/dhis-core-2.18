package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.EquipmentStatus;
import org.hisp.dhis.coldchain.inventory.EquipmentStatusService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentStatusHistoryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentInstanceService equipmentInstanceService;

    public void setEquipmentInstanceService( EquipmentInstanceService equipmentInstanceService )
    {
        this.equipmentInstanceService = equipmentInstanceService;
    }

    private EquipmentStatusService equipmentStatusService;
    
    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer equipmentInstanceId;
    
    public void setEquipmentInstanceId( Integer equipmentInstanceId )
    {
        this.equipmentInstanceId = equipmentInstanceId;
    }
    
    private List<EquipmentStatus> equipmentStatusHistory;
    
    public List<EquipmentStatus> getEquipmentStatusHistory()
    {
        return equipmentStatusHistory;
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
        //System.out.println("inside ShowEquipmentStatusHistoryFormAction ");
        equipmentInstance = equipmentInstanceService.getEquipmentInstance( equipmentInstanceId );
         
        //equipmentStatusHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipmentInstance ) );
        
        equipmentStatusHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistoryDescOrder( equipmentInstance ) );
        
        
        return SUCCESS;
    }
}
