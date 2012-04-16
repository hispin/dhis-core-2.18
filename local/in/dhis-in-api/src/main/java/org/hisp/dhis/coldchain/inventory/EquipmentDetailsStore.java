package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface EquipmentDetailsStore
{
    String ID = EquipmentDetailsStore.class.getName();
    
    int addEquipmentDetails( EquipmentDetails equipmentDetails );

    void updateEquipmentDetails( EquipmentDetails equipmentDetails );

    void deleteEquipmentDetails( EquipmentDetails equipmentDetails );

    Collection<EquipmentDetails> getAllEquipmentDetails();

}
