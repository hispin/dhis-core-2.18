package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface EquipmentDetailsService
{
    String ID = EquipmentDetailsService.class.getName();
    
    int addEquipmentDetails( EquipmentDetails equipmentDetails );

    void updateEquipmentDetails( EquipmentDetails equipmentDetails );

    void deleteEquipmentDetails( EquipmentDetails equipmentDetails );

    Collection<EquipmentDetails> getAllEquipmentDetails();

}
