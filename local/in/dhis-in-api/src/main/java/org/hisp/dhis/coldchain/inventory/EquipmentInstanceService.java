package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface EquipmentInstanceService
{
    String ID = EquipmentInstanceService.class.getName();
    
    int addEquipmentInstance( EquipmentInstance equipmentInstance );

    void updateEquipmentInstance( EquipmentInstance equipmentInstance );

    void deleteEquipmentInstance( EquipmentInstance equipmentInstance );

    Collection<EquipmentInstance> getAllEquipmentInstance();

}
