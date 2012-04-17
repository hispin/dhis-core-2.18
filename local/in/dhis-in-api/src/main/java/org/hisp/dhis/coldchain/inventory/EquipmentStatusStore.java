package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface EquipmentStatusStore
{
    String ID = EquipmentStatusStore.class.getName();
    
    int addEquipmentStatus( EquipmentStatus equipmentStatus );

    void updateEquipmentStatus( EquipmentStatus equipmentStatus );

    void deleteEquipmentStatus( EquipmentStatus equipmentStatus );

    Collection<EquipmentStatus> getAllEquipmentStatus();

}
