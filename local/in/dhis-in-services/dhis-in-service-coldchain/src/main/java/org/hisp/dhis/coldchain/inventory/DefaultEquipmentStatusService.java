package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultEquipmentStatusService implements EquipmentStatusService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentStatusStore equipmentStatusStore;

    public void setEquipmentStatusStore( EquipmentStatusStore equipmentStatusStore )
    {
        this.equipmentStatusStore = equipmentStatusStore;
    }
    
    // -------------------------------------------------------------------------
    // EquipmentWorkingStatus
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        return equipmentStatusStore.addEquipmentStatus( equipmentStatus );
    }
    @Transactional
    @Override
    public void deleteEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        equipmentStatusStore.deleteEquipmentStatus( equipmentStatus );
    }
    @Transactional
    @Override
    public Collection<EquipmentStatus> getAllEquipmentStatus()
    {
        return equipmentStatusStore.getAllEquipmentStatus();
    }
    @Transactional
    @Override
    public void updateEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        equipmentStatusStore.updateEquipmentStatus( equipmentStatus );
    }
    
}
