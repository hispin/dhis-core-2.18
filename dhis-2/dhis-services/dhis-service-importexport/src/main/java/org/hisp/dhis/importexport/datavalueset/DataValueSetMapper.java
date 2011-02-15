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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;

public class DataValueSetMapper
{

    private OrganisationUnitService organisationUnitService;

    private DataSetService dataSetService;

    private DataElementCategoryService categoryService;

    private DataElementService dataElementService;

    public List<DataValue> getDataValues( DataValueSet dataValueSet )
    {
        Date timestamp = new Date();

        DataSet dataSet = dataSetService.getDataSet( dataValueSet.getDataSetUuid() );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "Data set with UUID " + dataValueSet.getDataSetUuid()
                + " does not exist" );
        }

        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( dataValueSet.getOrganisationUnitUuid() );

        if ( unit == null )
        {
            throw new IllegalArgumentException( "Org unit with UUID " + dataValueSet.getOrganisationUnitUuid()
                + " does not exist" );
        }

        if ( !dataSet.getSources().contains( unit ) )
        {
            throw new IllegalArgumentException( "Org unit with UUID " + dataValueSet.getOrganisationUnitUuid()
                + " does not report data set with UUID " + dataSet.getUuid() );
        }

        Period period;

        try
        {
            period = dataSet.getPeriodType().createPeriod( dataValueSet.getPeriodIsoDate() );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Period " + dataValueSet.getPeriodIsoDate()
                + " is not valid period of type " + dataSet.getPeriodType().getName() );
        }

        List<org.hisp.dhis.importexport.datavalueset.DataValue> dxfDataValues = dataValueSet.getDataValues();
        List<DataValue> dataValues = new ArrayList<DataValue>( dxfDataValues.size() );

        for ( org.hisp.dhis.importexport.datavalueset.DataValue dxfValue : dxfDataValues )
        {
            DataValue dataValue = getDataValue( dxfValue, dataSet );
            dataValue.setSource( unit );
            dataValue.setTimestamp( timestamp );
            dataValue.setStoredBy( dataValueSet.getStoredBy() );
            dataValue.setPeriod( period );

            dataValues.add( dataValue );

        }

        return dataValues;
    }

    public DataValue getDataValue( org.hisp.dhis.importexport.datavalueset.DataValue dxfValue, DataSet dataSet )
    {
        DataElement dataElement = dataElementService.getDataElement( dxfValue.getDataElementUuid() );

        if ( dataElement == null )
        {
            throw new IllegalArgumentException( "Data value with UUID " + dxfValue.getDataElementUuid()
                + " does not exist" );
        }

        if ( !dataSet.getDataElements().contains( dataElement ) )
        {
            throw new IllegalArgumentException( "Data element " + dataElement.getUuid() + " isn't in data set "
                + dataSet.getUuid() );
        }

        DataValue dv = new DataValue();

        dv.setDataElement( dataElement );

        dv.setValue( dxfValue.getValue() );

        DataElementCategoryOptionCombo combo;

        String comboId = dxfValue.getCategoryOptionComboUuid();

        if ( comboId != null )
        {
            combo = categoryService.getDataElementCategoryOptionCombo( Integer.parseInt( comboId ) );

            if ( combo == null )
            {
                throw new IllegalArgumentException( "DataElementCategoryOptionCombo with UUID " + comboId
                    + " does not exist" );
            }
        }
        else
        {
            combo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }

        dv.setOptionCombo( combo );

        return dv;
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

}
