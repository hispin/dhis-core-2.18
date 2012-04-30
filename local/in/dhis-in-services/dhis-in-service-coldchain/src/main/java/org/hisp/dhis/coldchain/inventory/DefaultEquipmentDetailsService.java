package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;
@Transactional
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

    
    @Override
    public void addEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        equipmentDetailsStore.addEquipmentDetails( equipmentDetails );
    }
    @Override
    public void deleteEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        equipmentDetailsStore.deleteEquipmentDetails( equipmentDetails );
    }
    @Override
    public Collection<EquipmentDetails> getAllEquipmentDetails()
    {
        return equipmentDetailsStore.getAllEquipmentDetails();
    }
    @Override
    public void updateEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        equipmentDetailsStore.updateEquipmentDetails( equipmentDetails );
    }
    
    public Collection<EquipmentDetails> getEquipmentDetails( EquipmentInstance equipmentInstance)
    {
        return equipmentDetailsStore.getEquipmentDetails( equipmentInstance );
    }

    public EquipmentDetails getEquipmentDetails( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute )
    {
        return equipmentDetailsStore.getEquipmentDetails( equipmentInstance, inventoryTypeAttribute );
    }

}
