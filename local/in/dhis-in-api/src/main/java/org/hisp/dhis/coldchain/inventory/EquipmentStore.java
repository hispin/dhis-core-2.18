package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface EquipmentStore extends GenericNameableObjectStore<Equipment>
{
    String ID = EquipmentStore.class.getName();
    
    /*
    void addEquipmentDetails( Equipment equipmentDetails );
    
    int addEquipmentDetails( Equipment equipment );

    void updateEquipmentDetails( Equipment equipmentDetails );

    void deleteEquipmentDetails( Equipment equipmentDetails );
    */
    Collection<Equipment> getAllEquipments();

    Collection<Equipment> getEquipments( EquipmentInstance equipmentInstance);
    
    Equipment getEquipment( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute );
    
}
