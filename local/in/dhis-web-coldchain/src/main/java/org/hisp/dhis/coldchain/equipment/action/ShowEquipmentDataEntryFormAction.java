package org.hisp.dhis.coldchain.equipment.action;

import org.hisp.dhis.coldchain.inventory.EquipmentDataValueService;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentDataEntryFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private EquipmentDataValueService equipmentDataValueService;
    
    public void setEquipmentDataValueService( EquipmentDataValueService equipmentDataValueService )
    {
        this.equipmentDataValueService = equipmentDataValueService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer equipmentInstanceId;

    public void setEquipmentInstanceId( Integer equipmentInstanceId )
    {
        this.equipmentInstanceId = equipmentInstanceId;
    }

    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer periodId;
    
    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }




    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {

        return SUCCESS;
    }

}
