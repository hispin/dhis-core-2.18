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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dxf2.datavalueset.DataValueSet;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.importexport.ImportException;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.util.ImportExportUtils;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.junit.Ignore;
import org.junit.Test;

@Ignore //TODO fix
public class DataValueSetServiceTest
    extends DhisTest
{

    private static final String DEFAULT_COMBO_UUID = "AAB2299E-ECD6-46CF-A61F-817D350C180D";

    private static final String ORGANISATION_UNIT_UUID = "9C1B1B5E-3D65-48F2-8D1D-D36C60DD7344";

    private static final String ORGANISATION_UNIT_NOT_IN_SET_UUID = "9C1B1B5E-3D65-48F2-8D1D-D36C60DD7345";

    private static final String DATA_SET_UUID = "16B2299E-ECD6-46CF-A61F-817D350C180D";

    private static final String DATA_ELEMENT_UUID = "56B2299E-ECD6-46CF-A61F-817D350C180D";

    private static final String DATA_ELEMENT_NOT_IN_SET_UUID = "96B2299E-ECD6-46CF-A61F-817D350C180D";

    private DataValueSetService service;

    private DataValueSet dataValueSet;

    private ImportService importService;

    private ClassLoader classLoader;

    private DataElementCategoryOptionCombo defaultCombo;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @SuppressWarnings( "serial" )
    @Override
    public void setUpTest()
        throws JAXBException, IOException, ImportException
    {
        importService = (ImportService) getBean( "org.hisp.dhis.importexport.ImportService" );
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        dataValueService = (DataValueService) getBean( DataValueService.ID );
        completeDataSetRegistrationService = (CompleteDataSetRegistrationService) getBean( CompleteDataSetRegistrationService.ID );
        
        service = (DataValueSetService) getBean( "org.hisp.dhis.importexport.dxf2.service.DataValueSetService" );

        classLoader = Thread.currentThread().getContextClassLoader();

        InputStream is = classLoader.getResourceAsStream( "dxf2/base.xml" );
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false,
            false );
        importService.importData( importParams, is );
        is.close();

        dataValueSet = new DataValueSet();
        dataValueSet.setDataSet( DATA_SET_UUID );
        dataValueSet.setPeriod( "2011W5" );
        dataValueSet.setOrgUnit( ORGANISATION_UNIT_UUID );

        final org.hisp.dhis.dxf2.datavalue.DataValue dv = new org.hisp.dhis.dxf2.datavalue.DataValue();
        dv.setDataElement( DATA_ELEMENT_UUID );
        dv.setValue( "11" );

        dataValueSet.setDataValues( new ArrayList<org.hisp.dhis.dxf2.datavalue.DataValue>()
        {
            {
                add( dv );
            }
        } );

        defaultCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        categoryService.updateDataElementCategoryOptionCombo( defaultCombo );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testJaxb()
        throws JAXBException, IOException
    {
        JAXBContext jc = JAXBContext.newInstance( DataValueSet.class,
            org.hisp.dhis.dxf2.datavalue.DataValue.class );
        Unmarshaller u = jc.createUnmarshaller();
        InputStream is = classLoader.getResourceAsStream( "dxf2/dataValueSet.xml" );

        DataValueSet dxfDataValueSet = (DataValueSet) u.unmarshal( is );
        is.close();

        assertEquals( dataValueSet.getDataSet(), dxfDataValueSet.getDataSet() );
        assertEquals( dataValueSet.getPeriod(), dxfDataValueSet.getPeriod() );
        assertEquals( dataValueSet.getOrgUnit(), dxfDataValueSet.getOrgUnit() );

        assertEquals( 1, dxfDataValueSet.getDataValues().size() );

        org.hisp.dhis.dxf2.datavalue.DataValue dv = dxfDataValueSet.getDataValues().get( 0 );
        org.hisp.dhis.dxf2.datavalue.DataValue dataValue = dataValueSet.getDataValues().get( 0 );

        assertEquals( dataValue.getDataElement(), dv.getDataElement() );

        assertNull( dv.getCategoryOptionCombo() );

    }

    @Test
    public void simpleMapping()
        throws Exception
    {
        long before = new Date().getTime();

        service.saveDataValueSet( dataValueSet );

        long after = new Date().getTime();

        Collection<DataValue> dataValues = dataValueService.getAllDataValues();
        assertEquals( 1, dataValues.size() );

        DataValue dataValue = dataValues.iterator().next();

        verifyDataValue( before, after, dataValue );

    }

    @Test
    @Ignore
    public void testValidvalue()
    {
        setValue( "" );

        try
        {
            service.saveDataValueSet( dataValueSet );
        }
        catch ( NumberFormatException e )
        {
            // Expected
        }

    }

    @Test
    @Ignore
    public void testDuplicatedDataValues()
    {

    }

    @Test
    @Ignore
    public void testExistingComboButNotInDataElement()
    {

    }

    @Test
    public void deleteDataValue()
    {
        service.saveDataValueSet( dataValueSet );

        Collection<DataValue> dataValues = dataValueService.getAllDataValues();
        assertEquals( 1, dataValues.size() );

        dataValues = dataValueService.getAllDataValues();
        assertEquals( 1, dataValues.size() );

        setValue( null );

        service.saveDataValueSet( dataValueSet );

        dataValues = dataValueService.getAllDataValues();
        assertEquals( 0, dataValues.size() );

    }

    @Test
    public void dataSetMissing()
    {
        dataValueSet.setDataSet( null );
        setValue( "999" );
        
        testSave( "999" );

    }

    @Test
    public void orgunitMissingOrNotInSet()
    {
        dataValueSet.setOrgUnit( "ladlalad" );
        try
        {
            service.saveDataValueSet( dataValueSet );
            fail( "Should miss org unit" );

        }
        catch ( IllegalArgumentException e )
        {
            // Expected
        }

        dataValueSet.setOrgUnit( ORGANISATION_UNIT_NOT_IN_SET_UUID );

        try
        {
            service.saveDataValueSet( dataValueSet );
            fail( "Should miss org unit association to data set" );

        }
        catch ( IllegalArgumentException e )
        {
            // Expected
        }
    }

    @Test
    public void illegalPeriod()
    {

        dataValueSet.setPeriod( "2011" );

        try
        {
            service.saveDataValueSet( dataValueSet );
            fail( "should not accept yearly period" );

        }
        catch ( IllegalArgumentException e )
        {
            // Expected
        }
    }

    @Test
    public void completeness()
    {

        service.saveDataValueSet( dataValueSet );

        Collection<CompleteDataSetRegistration> registrations = 
            completeDataSetRegistrationService.getAllCompleteDataSetRegistrations();

        assertTrue( registrations.isEmpty() );

        
        dataValueSet.setCompleteDate( "20110101" );
        service.saveDataValueSet( dataValueSet );

        registrations = 
            completeDataSetRegistrationService.getAllCompleteDataSetRegistrations();

        assertEquals( 1, registrations.size() );
        assertEquals( 2011 - 1900, registrations.iterator().next().getDate().getYear() );
        
        dataValueSet.setCompleteDate( null );

        try
        {
            service.saveDataValueSet( dataValueSet );
            fail( "Shouldn't allow saving to a completed set" );
        }
        catch ( IllegalArgumentException e )
        {
            // TODO: Expected
        }

        registrations = 
            completeDataSetRegistrationService.getAllCompleteDataSetRegistrations();

        assertEquals( 1, registrations.size() );
        assertEquals( 2011 - 1900, registrations.iterator().next().getDate().getYear() );

        dataValueSet.setCompleteDate( "201lala" );

        try
        {
            service.saveDataValueSet( dataValueSet );
        }
        catch ( IllegalArgumentException e )
        {
            // Expected
        }

        registrations = 
            completeDataSetRegistrationService.getAllCompleteDataSetRegistrations();

        assertEquals( 1, registrations.size() );
        assertEquals( 2011 - 1900, registrations.iterator().next().getDate().getYear() );

        dataValueSet.setCompleteDate( "20071010" );
        service.saveDataValueSet( dataValueSet );

        registrations = 
            completeDataSetRegistrationService.getAllCompleteDataSetRegistrations();

        assertEquals( 1, registrations.size() );
        assertEquals( 2007 - 1900, registrations.iterator().next().getDate().getYear() );

        dataValueSet.setCompleteDate( "" );
        service.saveDataValueSet( dataValueSet );

        registrations = 
            completeDataSetRegistrationService.getAllCompleteDataSetRegistrations();

        assertTrue( registrations.isEmpty() );
    }

    @Test
    public void elementExistsAndNotInSet()
    {
        org.hisp.dhis.dxf2.datavalue.DataValue dv = new org.hisp.dhis.dxf2.datavalue.DataValue();
        dv.setDataElement( "ladida" );
        dv.setValue( "11" );
        dataValueSet.getDataValues().add( dv );

        try
        {
            service.saveDataValueSet( dataValueSet );
            fail( "Should not accept non existing data element" );
        }
        catch ( IllegalArgumentException e )
        {
            // Expected
        }

        dv.setDataElement( DATA_ELEMENT_NOT_IN_SET_UUID );

        try
        {
            service.saveDataValueSet( dataValueSet );
            fail( "Should not accept data element not in set" );
        }
        catch ( IllegalArgumentException e )
        {
            // Expected
        }
    }

    @Test
    public void optionComboExistsAndInDataElement()
    {

        dataValueSet.getDataValues().get( 0 ).setCategoryOptionCombo( DEFAULT_COMBO_UUID );

        service.saveDataValueSet( dataValueSet );

        dataValueSet.getDataValues().get( 0 ).setCategoryOptionCombo( "AAB2299E-ECD6-46CF-A61F-817D350" );

        try
        {
            service.saveDataValueSet( dataValueSet );
            fail( "Shouldn't allow non existing option combo" );
        }
        catch ( IllegalArgumentException e )
        {
            // Expected
        }

    }

    @Test
    public void testUpdate()
    {
        testSave("11");

        // Update
        setValue( "101" );
        
        testSave( "101" );

    }

    private void testSave( String value )
    {
        Collection<DataValue> dataValues;
        DataValue dataValue;

        long before = new Date().getTime();

        service.saveDataValueSet( dataValueSet );

        long after = new Date().getTime();

        dataValues = dataValueService.getAllDataValues();
        assertEquals( 1, dataValues.size() );

        dataValue = dataValues.iterator().next();

        verifyDataValue( before, after, dataValue, value );
    }

    private void setValue( String value )
    {
        dataValueSet.getDataValues().get( 0 ).setValue( value );
    }

    private void verifyDataValue( long before, long after, DataValue dv )
    {
        verifyDataValue( before, after, dv, "11" );
    }

    private void verifyDataValue( long before, long after, DataValue dv, String value )
    {
        assertEquals( new WeeklyPeriodType().createPeriod( "2011W5" ), dv.getPeriod() );
        assertEquals( value, dv.getValue() );

        long time = dv.getTimestamp().getTime();
        assertTrue( time >= before );
        assertTrue( time <= after );

        assertEquals( defaultCombo, dv.getOptionCombo() );

    }

    @Override
    protected boolean emptyDatabaseAfterTest()
    {
        return true;
    }

}
