package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface EquipmentWorkingStatusService
{
    String ID = EquipmentWorkingStatusService.class.getName();
    
    int addEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus );

    void updateEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus );

    void deleteEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus );

    Collection<EquipmentWorkingStatus> getAllEquipmentWorkingStatus();

}
