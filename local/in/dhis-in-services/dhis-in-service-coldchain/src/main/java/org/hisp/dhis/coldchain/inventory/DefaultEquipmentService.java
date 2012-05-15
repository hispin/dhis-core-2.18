package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;
@Transactional
public class DefaultEquipmentService implements EquipmentService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentStore equipmentStore;
    
    public void setEquipmentStore( EquipmentStore equipmentStore )
    {
        this.equipmentStore = equipmentStore;
    }

    // -------------------------------------------------------------------------
    // EquipmentDetails
    // -------------------------------------------------------------------------
    
    @Override
    public void addEquipment( Equipment equipment )
    {
        equipmentStore.addEquipment( equipment );
    }
    @Override
    public void deleteEquipment( Equipment equipment )
    {
        equipmentStore.deleteEquipment( equipment );
    }
    @Override
    public Collection<Equipment> getAllEquipments()
    {
        return equipmentStore.getAllEquipments();
    }
    @Override
    public void updateEquipment( Equipment equipment )
    {
        equipmentStore.updateEquipment( equipment );
    }
    
    public Collection<Equipment> getEquipments( EquipmentInstance equipmentInstance)
    {
        return equipmentStore.getEquipments( equipmentInstance );
    }

    public Equipment getEquipment( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute )
    {
        return equipmentStore.getEquipment( equipmentInstance, inventoryTypeAttribute );
    }
    
}
