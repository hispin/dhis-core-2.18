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

 /*   
    @Override
    public void addEquipmentDetails( Equipment equipmentDetails )
    {
        equipmentStore.addEquipmentDetails( equipmentDetails );
    }
    @Override
    public void deleteEquipmentDetails( Equipment equipmentDetails )
    {
        equipmentStore.deleteEquipmentDetails( equipmentDetails );
    }
    @Override
    public Collection<Equipment> getAllEquipmentDetails()
    {
        return equipmentStore.getAllEquipmentDetails();
    }
    @Override
    public void updateEquipmentDetails( Equipment equipmentDetails )
    {
        equipmentStore.updateEquipmentDetails( equipmentDetails );
    }
    
    public Collection<Equipment> getEquipmentDetails( EquipmentInstance equipmentInstance)
    {
        return equipmentStore.getEquipmentDetails( equipmentInstance );
    }

    public Equipment getEquipmentDetails( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute )
    {
        return equipmentStore.getEquipmentDetails( equipmentInstance, inventoryTypeAttribute );
    }
    */
    @Override
    public void addEquipment( Equipment equipment )
    {
         equipmentStore.save( equipment );
    }
    @Override
    public void deleteEquipment( Equipment equipment )
    {
        equipmentStore.delete( equipment );
    }

    @Override
    public void updateEquipment( Equipment equipment )
    {
        equipmentStore.update( equipment );
    }
    
    @Override
    public Collection<Equipment> getAllEquipments()
    {
        return equipmentStore.getAllEquipments();
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
