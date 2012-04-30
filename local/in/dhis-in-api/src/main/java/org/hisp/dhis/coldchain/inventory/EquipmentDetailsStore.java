package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface EquipmentDetailsStore
{
    String ID = EquipmentDetailsStore.class.getName();
    
    void addEquipmentDetails( EquipmentDetails equipmentDetails );

    void updateEquipmentDetails( EquipmentDetails equipmentDetails );

    void deleteEquipmentDetails( EquipmentDetails equipmentDetails );

    Collection<EquipmentDetails> getAllEquipmentDetails();

    Collection<EquipmentDetails> getEquipmentDetails( EquipmentInstance equipmentInstance);
    
    EquipmentDetails getEquipmentDetails( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute );
    
}
