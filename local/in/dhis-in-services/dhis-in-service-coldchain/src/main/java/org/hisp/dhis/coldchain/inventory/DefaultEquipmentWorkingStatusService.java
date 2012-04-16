package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultEquipmentWorkingStatusService implements EquipmentWorkingStatusService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentWorkingStatusStore equipmentWorkingStatusStore;

    public void setEquipmentWorkingStatusStore( EquipmentWorkingStatusStore equipmentWorkingStatusStore )
    {
        this.equipmentWorkingStatusStore = equipmentWorkingStatusStore;
    }

    // -------------------------------------------------------------------------
    // EquipmentWorkingStatus
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus )
    {
        return equipmentWorkingStatusStore.addEquipmentWorkingStatus( equipmentWorkingStatus );
    }
    @Transactional
    @Override
    public void deleteEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus )
    {
        equipmentWorkingStatusStore.deleteEquipmentWorkingStatus( equipmentWorkingStatus );
    }
    @Transactional
    @Override
    public Collection<EquipmentWorkingStatus> getAllEquipmentWorkingStatus()
    {
        return equipmentWorkingStatusStore.getAllEquipmentWorkingStatus();
    }
    @Transactional
    @Override
    public void updateEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus )
    {
        equipmentWorkingStatusStore.updateEquipmentWorkingStatus( equipmentWorkingStatus );
    }
    
}
