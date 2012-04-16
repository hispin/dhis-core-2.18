package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultEquipmentInstanceService implements EquipmentInstanceService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentInstanceStore equipmentInstanceStore;

    public void setEquipmentInstanceStore( EquipmentInstanceStore equipmentInstanceStore )
    {
        this.equipmentInstanceStore = equipmentInstanceStore;
    }

    // -------------------------------------------------------------------------
    // EquipmentInstance
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        return equipmentInstanceStore.addEquipmentInstance( equipmentInstance );
    }
    @Transactional
    @Override
    public void deleteEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        equipmentInstanceStore.deleteEquipmentInstance( equipmentInstance );
    }
    @Transactional
    @Override
    public Collection<EquipmentInstance> getAllEquipmentInstance()
    {
        return equipmentInstanceStore.getAllEquipmentInstance();
    }
    @Transactional
    @Override
    public void updateEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        equipmentInstanceStore.updateEquipmentInstance( equipmentInstance );
    }
    
    
}
