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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.importexport.ImportException;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.datavalueset.DataValueSet;
import org.hisp.dhis.importexport.datavalueset.DataValueSetMapper;
import org.hisp.dhis.importexport.util.ImportExportUtils;
import org.junit.Test;

/**
 * Messy test class checking that jaxb produces the expected java @link{DataValueSet data value set} structure, that
 * the set is converted into a correct list of {@link DataValues data values} and also checks that it is stored.. 
 */
public class DataValueSetMapperTest
    extends DhisTest
{

    private static final String ORGANISATION_UNIT_UUID = "9C1B1B5E-3D65-48F2-8D1D-D36C60DD7344";

    private static final String DATA_SET_UUID = "16B2299E-ECD6-46CF-A61F-817D350C180D";

    private static final String DATA_ELEMENT_UUID = "56B2299E-ECD6-46CF-A61F-817D350C180D";

    private static final String DATA_ELEMENT_NOT_IN_SET_UUID = "96B2299E-ECD6-46CF-A61F-817D350C180D";
    
    private DataValueSetMapper mapper;

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
        
        mapper = (DataValueSetMapper) getBean( "org.hisp.dhis.importexport.datavalueset.DataValueSetMapper" );

        classLoader = Thread.currentThread().getContextClassLoader();

        InputStream is = classLoader.getResourceAsStream( "datavalueset/base.xml" );
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false,
            false );
        importService.importData( importParams, is );
        is.close();

        dataValueSet = new DataValueSet();
        dataValueSet.setDataSetUuid( DATA_SET_UUID );
        dataValueSet.setPeriodIsoDate( "2011W5" );
        dataValueSet.setOrganisationUnitUuid( ORGANISATION_UNIT_UUID );
        dataValueSet.setStoredBy( "misterindia" );

        final org.hisp.dhis.importexport.datavalueset.DataValue dv = new org.hisp.dhis.importexport.datavalueset.DataValue();
        dv.setDataElementUuid( DATA_ELEMENT_UUID );
        dv.setValue( "11" );

        dataValueSet.setDataValues( new ArrayList<org.hisp.dhis.importexport.datavalueset.DataValue>() {{ add( dv ); }} );

        defaultCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testJaxb()
        throws JAXBException, IOException
    {
        JAXBContext jc = JAXBContext.newInstance( DataValueSet.class, org.hisp.dhis.importexport.datavalueset.DataValue.class );
        Unmarshaller u = jc.createUnmarshaller();
        InputStream is = classLoader.getResourceAsStream( "datavalueset/dataValueSet.xml" );

        DataValueSet dxfDataValueSet = (DataValueSet) u.unmarshal( is );
        is.close();

        assertEquals( dataValueSet.getDataSetUuid(), dxfDataValueSet.getDataSetUuid() );
        assertEquals( dataValueSet.getPeriodIsoDate(), dxfDataValueSet.getPeriodIsoDate() );
        assertEquals( dataValueSet.getOrganisationUnitUuid(), dxfDataValueSet.getOrganisationUnitUuid() );
        assertEquals( dataValueSet.getStoredBy(), dxfDataValueSet.getStoredBy() );

        assertEquals( 1, dxfDataValueSet.getDataValues().size() );

        org.hisp.dhis.importexport.datavalueset.DataValue dv = dxfDataValueSet.getDataValues().get( 0 );

        assertEquals( dataValueSet.getDataValues().get( 0 ).getDataElementUuid(), dv.getDataElementUuid() );

        assertNull( dv.getCategoryOptionComboUuid() );
    }

    @Test
    public void simpleMapping()
        throws Exception
    {
        long before = new Date().getTime();

        List<DataValue> dataValues = mapper.getDataValues( dataValueSet );

        long after = new Date().getTime();

        assertEquals( 1, dataValues.size() );

        DataValue dv = dataValues.get( 0 );

        verifyDataValue( before, after, dv );

        dataValueService.addDataValue( dv );
        Collection<DataValue> persistedDataValues = dataValueService.getAllDataValues();
        assertEquals(1, persistedDataValues.size());
        
        DataValue persisted = persistedDataValues.iterator().next();
        assertEquals( dv, persisted );
        verifyDataValue( before, after, persisted );

    }

    @Test
    public void missingThingsFromInput() {

        dataValueSet.setDataSetUuid( null );
        try {
            mapper.getDataValues( dataValueSet );
            fail("Should miss data set");
        
        } catch (IllegalArgumentException e) {
            // Expected
        }

        dataValueSet.setDataSetUuid( DATA_SET_UUID );
        dataValueSet.setOrganisationUnitUuid( "ladlalad" );
        try {
            mapper.getDataValues( dataValueSet );
            fail("Should miss org unit");
        
        } catch (IllegalArgumentException e) {
            // Expected
        }

        dataValueSet.setOrganisationUnitUuid( ORGANISATION_UNIT_UUID );
        
        final org.hisp.dhis.importexport.datavalueset.DataValue dv = new org.hisp.dhis.importexport.datavalueset.DataValue();
        dv.setDataElementUuid( DATA_ELEMENT_NOT_IN_SET_UUID );
        dv.setValue( "11" );
        dataValueSet.getDataValues().add( dv );
        
        try {
            mapper.getDataValues( dataValueSet );
            fail("Should not accept extra data value");
        
        } catch (IllegalArgumentException e) {
            // Expected
            System.out.println(e.getMessage());
        }
        
    }

    private void verifyDataValue( long before, long after, DataValue dv )
    {
        assertEquals( DATA_ELEMENT_UUID, dv.getDataElement().getUuid() );
        assertEquals( ORGANISATION_UNIT_UUID, dv.getSource().getUuid() );
        assertEquals( "misterindia", dv.getStoredBy() );
        assertEquals( "11", dv.getValue() );

        long time = dv.getTimestamp().getTime();
        assertTrue( time >= before );
        assertTrue( time <= after );

        assertEquals( defaultCombo, dv.getOptionCombo() );
    }

}
