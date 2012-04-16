package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultEquipmentDetailsService implements EquipmentDetailsService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentDetailsStore equipmentDetailsStore;

    public void setEquipmentDetailsStore( EquipmentDetailsStore equipmentDetailsStore )
    {
        this.equipmentDetailsStore = equipmentDetailsStore;
    }

    // -------------------------------------------------------------------------
    // EquipmentDetails
    // -------------------------------------------------------------------------

    @Transactional
    @Override
    public int addEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        return equipmentDetailsStore.addEquipmentDetails( equipmentDetails );
    }
    @Transactional
    @Override
    public void deleteEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        equipmentDetailsStore.deleteEquipmentDetails( equipmentDetails );
    }
    @Transactional
    @Override
    public Collection<EquipmentDetails> getAllEquipmentDetails()
    {
        return equipmentDetailsStore.getAllEquipmentDetails();
    }
    @Transactional
    @Override
    public void updateEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        equipmentDetailsStore.updateEquipmentDetails( equipmentDetails );
    }
    
}
