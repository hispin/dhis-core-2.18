package org.hisp.dhis.pbf.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.pbf.api.TariffDataValueStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultTariffDataValueService implements TariffDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TariffDataValueStore tariffDataValueStore;

    public void setTariffDataValueStore( TariffDataValueStore tariffDataValueStore )
    {
        this.tariffDataValueStore = tariffDataValueStore;
    }

    // -------------------------------------------------------------------------
    // TariffDataValue
    // -------------------------------------------------------------------------

    @Override
    public void addTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.addTariffDataValue( tariffDataValue );;
    }

    @Override
    public void updateTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.updateTariffDataValue( tariffDataValue );
    }

    @Override
    public void deleteTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.deleteTariffDataValue( tariffDataValue );
    }

    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnitGroup organisationUnitGroup, Date startDate,
        Date endDate )
    {
        return tariffDataValueStore.getTariffDataValue( organisationUnit, dataElement, optionCombo, organisationUnitGroup, startDate, endDate );
    }

    @Override
    public TariffDataValue getTariffDataValue( int organisationUnitId, int dataElementId, int categoryOptionComboId,
        int organisationUnitGroupId, Date startDate, Date endDate )
    {
        return tariffDataValueStore.getTariffDataValue( organisationUnitId, dataElementId, categoryOptionComboId, organisationUnitGroupId, startDate, endDate );
    }

    @Override
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        return tariffDataValueStore.getAllTariffDataValues();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, OrganisationUnitGroup organisationUnitGroup )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, organisationUnitGroup );
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataElement );
    }

}
