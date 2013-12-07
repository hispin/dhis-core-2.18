package org.hisp.dhis.pbf.api;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

public interface TariffDataValueStore
{
    String ID = TariffDataValueStore.class.getName();
    
    void addTariffDataValue( TariffDataValue tariffDataValue );
    
    void updateTariffDataValue( TariffDataValue tariffDataValue );
    
    void deleteTariffDataValue( TariffDataValue tariffDataValue );
        
    TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, OrganisationUnitGroup organisationUnitGroup, Date startDate, Date endDate );
    
    TariffDataValue getTariffDataValue( int organisationUnitId, int dataElementId, int categoryOptionComboId, int organisationUnitGroupId, Date startDate, Date endDate );
    
    Collection<TariffDataValue> getAllTariffDataValues();
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, OrganisationUnitGroup organisationUnitGroup );
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement );
}
