package org.hisp.dhis.importexport.datavalueset;

/*
 * Copyright (c) 2011, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class DataValueSetService
{

    private OrganisationUnitService organisationUnitService;

    private DataSetService dataSetService;

    private DataElementCategoryService categoryService;

    private DataElementService dataElementService;

    private DataValueService dataValueService;

    private CompleteDataSetRegistrationService registrationService;

    /**
     * Save a dataValueSet if all of the following is valid
     * <p>
     * First checks that:
     * <ul>
     * <li>dataSet exists
     * <li>orgUnit exists
     * <li>orgunit reports dataSet
     * <li>period is a valid period
     * <li>the dataValueSet is not registered as complete or that if it is a
     * complete date is present
     * <li>a present complete date is valid
     * </ul>
     * For all dataValues it checks that:
     * <ul>
     * <li>dataElement exists and is in dataSet
     * <li>optionCombo exists (defaults to 'default' if not specified) and is in
     * dataElement
     * </ul>
     * What isn't checked yet:
     * <ul>
     * <li>That there isn't duplicated value entries (will throw Constraint
     * exception)
     * <li>That the value is valid!
     * </ul>
     * Concerns:
     * <ul>
     * <li>deletion through sending "empty string" value dependant on semantics
     * of add/update in data value store
     * <li>completed semantics, can't uncomplete but can complete and
     * "recomplete"
     * <li>what is 'comment' really?
     * 
     * @param dxf
     * @throws IllegalArgumentException if
     */
    @Transactional
    public void saveDataValueSet( Dxf dxf )
        throws IllegalArgumentException
    {
        handleComplete( dxf.getDataValueSets() );
        for ( org.hisp.dhis.importexport.datavalueset.DataValue dxfValue : dxf.getDataValues() )
        {
            saveDataValue( dxfValue );
        }
    }

    private void handleComplete( List<DataValueSet> completeNotDataSets )
    {
        if ( completeNotDataSets == null )
        {
            return;
        }

        for ( DataValueSet nds : completeNotDataSets )
        {
            CompleteDataSetRegistration complete = null;

            DataSet dataSet = getDataSet( nds.getDataSetUuid() );

            OrganisationUnit unit = getOrgUnit( nds.getOrganisationUnitUuid() );

            if ( !dataSet.getSources().contains( unit ) )
            {
                throw new IllegalArgumentException( "Org unit with UUID " + unit.getUuid()
                    + " does not report data set with UUID " + dataSet.getUuid() );
            }

            Period period = getPeriod( nds.getPeriodIsoDate(), dataSet.getPeriodType() );

            CompleteDataSetRegistration alreadyComplete = registrationService.getCompleteDataSetRegistration( dataSet,
                period, unit );
            String completeDateString = nds.getCompleteDate();

            if ( alreadyComplete != null && completeDateString == null )
            {
                throw new IllegalArgumentException(
                    "DataValueSet is complete, include a new complete date if you want to recomplete" );
            }

            if ( completeDateString != null )
            {
                complete = getComplete( dataSet, unit, period, completeDateString, complete );
            }

            if ( alreadyComplete != null )
            {
                registrationService.deleteCompleteDataSetRegistration( alreadyComplete );
            }
            if ( complete != null )
            {
                registrationService.saveCompleteDataSetRegistration( complete );
            }

        }
    }

    private void saveDataValue( org.hisp.dhis.importexport.datavalueset.DataValue dxfValue )
    {
        Date timestamp = new Date();

        OrganisationUnit unit = getOrgUnit( dxfValue.getOrganisationUnitUuid() );

        DataElement dataElement = getDataElement( dxfValue.getDataElementUuid() );

        DataSet dataSet = null;
        if ( dxfValue.getDataSetUuid() != null )
        {
            dataSet = getDataSet( dxfValue.getDataSetUuid() );

            if ( !dataSet.getDataElements().contains( dataElement ) )
            {
                throw new IllegalArgumentException( "Data element '" + dataElement.getUuid() + "' isn't in data set "
                    + dataSet.getUuid() );
            }

        }
        else
        {
            dataSet = resolveDataSet( dataElement, unit );
        }

        if ( !dataSet.getSources().contains( unit ) )
        {
            throw new IllegalArgumentException( "Org unit with UUID " + unit.getUuid()
                + " does not report data set with UUID " + dataSet.getUuid() );
        }

        Period period = getPeriod( dxfValue.getPeriodIsoDate(), dataSet.getPeriodType() );

        DataElementCategoryOptionCombo combo = getOptionCombo( dxfValue.getCategoryOptionComboUuid(), dataElement );

        String value = dxfValue.getValue();

        // dataElement.isValidValue(value);

        DataValue dv = dataValueService.getDataValue( unit, dataElement, period, combo );

        if ( dv == null )
        {
            dv = new DataValue( dataElement, period, unit, value, dxfValue.getStoredBy(), timestamp, null, combo );
            dataValueService.addDataValue( dv );
        }
        else
        {
            dv.setValue( value );
            dv.setTimestamp( timestamp );
            dv.setStoredBy( dxfValue.getStoredBy() );
            dataValueService.updateDataValue( dv );
        }
    }

    private DataSet resolveDataSet( DataElement dataElement, OrganisationUnit unit )
    {

        Set<DataSet> dataSets = dataElement.getDataSets();

        if ( dataSets == null || dataSets.isEmpty() )
        {
            throw new IllegalArgumentException( "data element '" + dataElement.getName() + "' with UUID '"
                + dataElement.getUuid() + "' isn't assigned to any data set" );
        }
        else if ( dataSets.size() == 1 )
        {
            return dataSets.iterator().next();
        }
        else
        {
            for ( DataSet dataSet : dataSets )
            {
                if ( dataSet.getOrganisationUnits().contains( unit ) )
                {
                    return dataSet;
                }
            }
        }
        throw new IllegalArgumentException( "data element '" + dataElement.getName() + "' with UUID '"
            + dataElement.getUuid() + "' isn't assigned to any data set that in turn is assigned to org unit '"
            + unit.getName() + "', uuid '" + unit.getUuid() + "'" );
    }

    private CompleteDataSetRegistration getComplete( DataSet dataSet, OrganisationUnit unit, Period period,
        String completeDateString, CompleteDataSetRegistration complete )
    {
        SimpleDateFormat format = new SimpleDateFormat( DailyPeriodType.ISO_FORMAT );
        try
        {
            Date completeDate = format.parse( completeDateString );
            complete = new CompleteDataSetRegistration( dataSet, period, unit, completeDate );
        }
        catch ( ParseException e )
        {
            throw new IllegalArgumentException( "Complete date not in valid format: " + DailyPeriodType.ISO_FORMAT );
        }
        return complete;
    }

    private Period getPeriod( String periodIsoDate, PeriodType periodType )
    {
        Period period;

        try
        {
            period = periodType.createPeriod( periodIsoDate );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Period " + periodIsoDate + " is not valid period of type "
                + periodType.getName() );
        }
        return period;
    }

    private OrganisationUnit getOrgUnit( String uuid )
    {
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( uuid );

        if ( unit == null )
        {
            throw new IllegalArgumentException( "Org unit with UUID " + uuid + " does not exist" );
        }
        return unit;
    }

    private DataSet getDataSet( String uuid )
    {
        DataSet dataSet = dataSetService.getDataSet( uuid );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "Data set with UUID " + uuid + " does not exist" );
        }
        return dataSet;
    }

    private DataElement getDataElement( String uuid )
    {
        DataElement dataElement = dataElementService.getDataElement( uuid );

        if ( dataElement == null )
        {
            throw new IllegalArgumentException( "Data element with UUID " + uuid + " does not exist" );
        }

        return dataElement;
    }

    private DataElementCategoryOptionCombo getOptionCombo( String uuid, DataElement dataElement )
    {
        DataElementCategoryOptionCombo combo;

        if ( uuid == null )
        {
            combo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }
        else
        {
            combo = categoryService.getDataElementCategoryOptionCombo( uuid );
        }

        if ( combo == null )
        {
            throw new IllegalArgumentException( "DataElementCategoryOptionCombo with UUID '" + uuid
                + "' does not exist" );
        }

        if ( !dataElement.getCategoryCombo().getOptionCombos().contains( combo ) )
        {
            throw new IllegalArgumentException( "DataElementCategoryOptionCombo with UUID '" + combo.getUuid()
                + "' isn't in DataElement '" + dataElement.getUuid() + "'" );
        }
        return combo;
    }

    // @Transactional
    // private void save( CompleteDataSetRegistration alreadyComplete,
    // CompleteDataSetRegistration complete,
    // List<DataValue> newDataValues, List<DataValue> updatedDataValues )
    // {
    // if ( alreadyComplete != null )
    // {
    // registrationService.deleteCompleteDataSetRegistration( alreadyComplete );
    // }
    //
    // for ( DataValue dataValue : newDataValues )
    // {
    // dataValueService.addDataValue( dataValue );
    // }
    //
    // for ( DataValue dataValue : updatedDataValues )
    // {
    // dataValueService.updateDataValue( dataValue );
    // }
    //
    // if ( complete != null )
    // {
    // registrationService.saveCompleteDataSetRegistration( complete );
    // }
    // }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    @Required
    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

}
