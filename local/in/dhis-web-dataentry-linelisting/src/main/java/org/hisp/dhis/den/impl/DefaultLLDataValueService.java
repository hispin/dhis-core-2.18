package org.hisp.dhis.den.impl;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.den.api.LLDataValue;
import org.hisp.dhis.den.api.LLDataValueService;
import org.hisp.dhis.den.api.LLDataValueStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;

public class DefaultLLDataValueService implements LLDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LLDataValueStore dataValueStore;

    public void setDataValueStore( LLDataValueStore dataValueStore )
    {
        this.dataValueStore = dataValueStore;
    }
    
    
    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------


    public void addDataValue( LLDataValue dataValue )
    {
        if ( !(dataValue.getValue() == null && dataValue.getComment() == null) )
        {
            dataValueStore.addDataValue( dataValue );
        }
    }

    public void updateDataValue( LLDataValue dataValue )
    {
        if ( dataValue.getValue() == null && dataValue.getComment() == null )
        {
            dataValueStore.deleteDataValue( dataValue );
        }
        else
        {
            dataValueStore.updateDataValue( dataValue );
        }
    }

    public void deleteDataValue( LLDataValue dataValue )
    {
        dataValueStore.deleteDataValue( dataValue );
    }

    public int deleteDataValuesBySource( Source source )
    {
        return dataValueStore.deleteDataValuesBySource( source );
    }
    
    public int deleteDataValuesByDataElement( DataElement dataElement )
    {
        return dataValueStore.deleteDataValuesByDataElement( dataElement );
    }

    public LLDataValue getDataValue(  Source source, DataElement dataElement, Period period, DataElementCategoryOptionCombo optionCombo, int recordNo)
    {
        return dataValueStore.getDataValue(  source, dataElement, period, optionCombo, recordNo);
    }

    public Collection<LLDataValue> getDataValues( Source source, DataElement dataElement, Period period )
    {
        return dataValueStore.getDataValues( source, dataElement, period );
    }
    
    public Collection<LLDataValue> getDataValues( Source source, DataElement dataElement, Period period, DataElementCategoryOptionCombo optionCombo )
    {
        return dataValueStore.getDataValues( source, dataElement, period, optionCombo );
    }

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    public Collection<LLDataValue> getAllDataValues()
    {
        return dataValueStore.getAllDataValues();
    }
    
    public Collection<LLDataValue> getDataValues( Source source, Period period )
    {
        return dataValueStore.getDataValues( source, period );
    }

    public Collection<LLDataValue> getDataValues( Source source, DataElement dataElement )
    {
        return dataValueStore.getDataValues( source, dataElement );
    }

    public Collection<LLDataValue> getDataValues( Collection<Source> sources, DataElement dataElement )
    {
        return dataValueStore.getDataValues( sources, dataElement );
    }

    public Collection<LLDataValue> getDataValues( Source source, Period period, Collection<DataElement> dataElements )
    {
        return dataValueStore.getDataValues( source, period, dataElements );
    }
    
    public Collection<LLDataValue> getDataValues( Source source, Period period, Collection<DataElement> dataElements, Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        return dataValueStore.getDataValues( source, period, dataElements, optionCombos );
    }
    
    public Collection<LLDataValue> getDataValues( DataElement dataElement, Collection<Period> periods, Collection<? extends Source> sources )
    {
        return dataValueStore.getDataValues( dataElement, periods, sources );
    }
    
    public Collection<LLDataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo optionCombo, 
        Collection<Period> periods, Collection<? extends Source> sources )
    {
        return dataValueStore.getDataValues( dataElement, optionCombo, periods, sources );
    }
    
    public Collection<LLDataValue> getDataValues( Collection<DataElement> dataElements, Collection<Period> periods, 
        Collection<? extends Source> sources, int firstResult, int maxResults )
    {
        return dataValueStore.getDataValues( dataElements, periods, sources, firstResult, maxResults );
    }
    
    public Collection<LLDataValue> getDataValues( Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        return dataValueStore.getDataValues( optionCombos );    
        
    }
    
    public Collection<LLDataValue> getDataValues( DataElement dataElement )
    {
        return dataValueStore.getDataValues( dataElement );     
        
    }   

    public int getMaxRecordNo()
    {
        return dataValueStore.getMaxRecordNo();
    }
    
    public Map<String,String> processLineListBirths(OrganisationUnit organisationUnit, Period period)
    {
    	return dataValueStore.processLineListBirths(organisationUnit, period);
    }

    public Map<String,String> processLineListDeaths(OrganisationUnit organisationUnit, Period periodL)
    {
    	return dataValueStore.processLineListDeaths(organisationUnit, periodL);
    }
    
    public Map<String,String> processLineListMaternalDeaths(OrganisationUnit organisationUnit, Period periodL)
    {
    	return dataValueStore.processLineListMaternalDeaths(organisationUnit, periodL);
    }
}
