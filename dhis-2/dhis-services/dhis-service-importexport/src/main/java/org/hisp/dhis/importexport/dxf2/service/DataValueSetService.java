package org.hisp.dhis.importexport.dxf2.service;

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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.hisp.dhis.importexport.dxf2.model.DataValueSet;
import org.hisp.dhis.importexport.dxf2.model.DataValueSet.IdentificationStrategy;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class DataValueSetService
{
    private static final Log log = LogFactory.getLog( DataValueSetService.class );

    private OrganisationUnitService organisationUnitService;

    private DataSetService dataSetService;

    private DataElementCategoryService categoryService;

    private DataElementService dataElementService;

    private DataValueService dataValueService;

    private CompleteDataSetRegistrationService registrationService;

    private CurrentUserService currentUserService;

    /**
     * Save a dataValueSet.
     * <p>
     * Handles the content in the following way
     * <ul>
     * <li>if data set is not specified, will resolve it through data elements
     * if not ambiguous.
     * <li>optionCombo defaults to 'default' if not specified
     * <li>storedBy defaults to currently logged in user's name
     * <li>if value is empty not present -> delete value
     * </ul>
     * <ul>
     * Validates the following:
     * <p>
     * First checks that:
     * <ul>
     * <li>dataSet exists (tries to resolve it through the data elements if not
     * specified)
     * <li>orgUnit exists
     * <li>orgunit reports dataSet
     * <li>period is a valid period
     * <li>the dataValueSet is not registered as complete or that if it is a
     * complete date is present
     * <li>if complete date is empty string - delete completion
     * <li>if complete date present checks validity
     * </ul>
     * For all dataValues check that:
     * <ul>
     * <li>dataElement exists and is in dataSet
     * <li>optionCombo exists and is in dataElement
     * </ul>
     * What isn't checked yet:
     * <ul>
     * <li>The value is valid!
     * <li>There isn't duplicated value entries (will throw Constraint
     * exception)
     * <li>If multiple data sets are possible, evaluate if they are incompatible
     * (complete, locking and possibly period)
     * </ul>
     * Concerns:
     * <ul>
     * <li>deletion through sending "empty string" value dependent on semantics
     * of add/update in data value store
     * <li>completed semantics: can't uncomplete but can complete and
     * "recomplete"
     * <li>what is 'comment' good for really?
     *
     * @param dataValueSet
     * @throws IllegalArgumentException if there are any inconsistencies
     */
    @Transactional
    public void saveDataValueSet( DataValueSet dataValueSet )
        throws IllegalArgumentException
    {

        Date timestamp = new Date();

        IdentificationStrategy idStrategy = dataValueSet.getIdScheme();
        if ( idStrategy != DataValueSet.DEFAULT_STRATEGY )
        {
            throw new IllegalArgumentException( "Only UID id strategy supported currently." );
        }

        DataSet dataSet = getDataSet( dataValueSet );

        OrganisationUnit unit = getOrgUnit( dataValueSet.getOrganisationUnitIdentifier() );

        if ( !dataSet.getSources().contains( unit ) )
        {
            throw new IllegalArgumentException( "Org unit with UID " + unit.getUid()
                + " does not report data set with UID " + dataSet.getUid() );
        }

        Period period = getPeriod( dataValueSet.getPeriodIsoDate(), dataSet.getPeriodType() );

        handleComplete( dataValueSet, dataSet, unit, period );

        for ( org.hisp.dhis.importexport.dxf2.model.DataValue dxfValue : dataValueSet.getDataValues() )
        {
            saveDataValue( timestamp, dataSet, unit, period, dxfValue );
        }

        log( dataValueSet, unit, dataSet );
    }

    private void log( DataValueSet dataValueSet, OrganisationUnit unit, DataSet dataSet )
    {
        String message = "Saved data value set for " + dataSet.getName() + ", " + unit.getName() + ", "
            + dataValueSet.getPeriodIsoDate() + " - Data values received: ";

        for ( org.hisp.dhis.importexport.dxf2.model.DataValue value : dataValueSet.getDataValues() )
        {
            message += value.getDataElementIdentifier() + " = " + value.getValue() + ", ";
        }

        log.info( message.substring( 0, message.length() - 3 ) );
    }

    private DataSet getDataSet( DataValueSet dataValueSet )
    {
        DataSet dataSet = null;

        String uid = dataValueSet.getDataSetIdentifier();
        if ( uid != null )
        {
            dataSet = dataSetService.getDataSet( uid );

            if ( dataSet == null )
            {
                throw new IllegalArgumentException( "Data set with UID " + uid + " does not exist" );
            }
        }
        else
        {
            dataSet = resolveDataSet( dataValueSet );
        }
        return dataSet;
    }

    private DataSet resolveDataSet( DataValueSet dataValueSet )
    {
        if ( dataValueSet.getDataValues() == null )
        {
            throw new IllegalArgumentException(
                "Data value set doesn't specify data set and does not contain data values." );
        }

        Set<DataSet> potential = null;

        for ( org.hisp.dhis.importexport.dxf2.model.DataValue value : dataValueSet.getDataValues() )
        {
            DataElement dataElement = getDataElement( value.getDataElementIdentifier() );
            Set<DataSet> dataSets = dataElement.getDataSets();

            if ( dataSets == null || dataSets.isEmpty() )
            {
                throw new IllegalArgumentException( "Data element '" + dataElement.getUid() + "' isn't in a data set." );
            }
            else if ( dataSets.size() == 1 )
            {
                return dataSets.iterator().next();
            }
            else
            {
                if ( potential == null )
                {
                    potential = new HashSet<DataSet>( dataSets );
                }
                else
                {
                    for ( DataSet set : dataSets )
                    {
                        if ( !potential.contains( set ) )
                        {
                            potential.remove( set );
                        }
                    }
                    if ( potential.size() == 1 )
                    {
                        return potential.iterator().next();
                    }
                }
            }
        }

        // TODO: Check if potential data sets are compatible

        String message = "Ambiguous which of these data set the data values belong to: ";
        for ( DataSet p : potential )
        {
            message += p.getUid() + ", ";
        }
        message.substring( 0, message.length() - 2 );
        throw new IllegalArgumentException( message );
    }

    private void saveDataValue( Date timestamp, DataSet dataSet, OrganisationUnit unit, Period period,
        org.hisp.dhis.importexport.dxf2.model.DataValue dxfValue )
    {
        DataElement dataElement = getDataElement( dxfValue.getDataElementIdentifier() );

        if ( !dataSet.getDataElements().contains( dataElement ) )
        {
            throw new IllegalArgumentException( "Data element '" + dataElement.getUid() + "' isn't in data set "
                + dataSet.getUid() );
        }

        DataElementCategoryOptionCombo combo = getOptionCombo( dxfValue.getCategoryOptionComboIdentifier(), dataElement );

        DataValue dv = dataValueService.getDataValue( unit, dataElement, period, combo );

        String value = dxfValue.getValue();

        // dataElement.isValidValue(value);

        String storedBy = currentUserService.getCurrentUsername();

        if ( dv == null )
        {
            dv = new DataValue( dataElement, period, unit, value, storedBy, timestamp, null, combo );
            dataValueService.addDataValue( dv );
        }
        else
        {
            dv.setValue( value );
            dv.setTimestamp( timestamp );
            dv.setStoredBy( storedBy );
            dataValueService.updateDataValue( dv );
        }
    }

    private void handleComplete( DataValueSet dataValueSet, DataSet dataSet, OrganisationUnit unit, Period period )
    {
        CompleteDataSetRegistration alreadyComplete = registrationService.getCompleteDataSetRegistration( dataSet,
            period, unit );
        String completeDateString = dataValueSet.getCompleteDate();

        if ( alreadyComplete != null && completeDateString == null )
        {
            throw new IllegalArgumentException(
                "DataValueSet is complete, include a new complete date if you want to recomplete" );
        }

        if ( alreadyComplete != null )
        {
            registrationService.deleteCompleteDataSetRegistration( alreadyComplete );
        }

        CompleteDataSetRegistration complete = null;

        if ( completeDateString != null && !completeDateString.trim().isEmpty() )
        {
            complete = getComplete( dataSet, unit, period, completeDateString, complete );
        }
        if ( complete != null )
        {
            registrationService.saveCompleteDataSetRegistration( complete );
        }
    }

    private CompleteDataSetRegistration getComplete( DataSet dataSet, OrganisationUnit unit, Period period,
        String completeDateString, CompleteDataSetRegistration complete )
    {
        SimpleDateFormat format = new SimpleDateFormat( DailyPeriodType.ISO_FORMAT );
        try
        {
            Date completeDate = format.parse( completeDateString );
            complete = new CompleteDataSetRegistration( dataSet, period, unit, completeDate,
                currentUserService.getCurrentUsername() );
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

    private OrganisationUnit getOrgUnit( String uid )
    {
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( uid );

        if ( unit == null )
        {
            throw new IllegalArgumentException( "Org unit with UID " + uid + " does not exist" );
        }
        return unit;
    }

    private DataElement getDataElement( String uid )
    {
        DataElement dataElement = dataElementService.getDataElement( uid );

        if ( dataElement == null )
        {
            throw new IllegalArgumentException( "Data element with UID " + uid + " does not exist" );
        }

        return dataElement;
    }

    private DataElementCategoryOptionCombo getOptionCombo( String uid, DataElement dataElement )
    {
        DataElementCategoryOptionCombo combo;

        if ( uid == null )
        {
            combo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }
        else
        {
            combo = categoryService.getDataElementCategoryOptionCombo( uid );
        }

        if ( combo == null )
        {
            throw new IllegalArgumentException( "DataElementCategoryOptionCombo with UID '" + uid
                + "' does not exist" );
        }

        if ( !dataElement.getCategoryCombo().getOptionCombos().contains( combo ) )
        {
            throw new IllegalArgumentException( "DataElementCategoryOptionCombo with UID '" + combo.getUid()
                + "' isn't in DataElement '" + dataElement.getUid() + "'" );
        }
        return combo;
    }

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

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

}
