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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.importexport.ImportException;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.dxf2.model.DataSet;
import org.hisp.dhis.importexport.util.ImportExportUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Stub test class...
 */
public class DataSetMappingTest
    extends DhisTest
{

    private static final String DATA_SET_UUID = "16B2299E-ECD6-46CF-A61F-817D350C180D";

    private ImportService importService;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws JAXBException, IOException, ImportException
    {
        importService = (ImportService) getBean( "org.hisp.dhis.importexport.ImportService" );
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        dataValueService = (DataValueService) getBean( DataValueService.ID );

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream is = classLoader.getResourceAsStream( "dxf2/base.xml" );
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false,
            false );
        importService.importData( importParams, is );
        is.close();

    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test @Ignore
    public void testJaxb()
        throws JAXBException, IOException
    {
        JAXBContext jc = JAXBContext.newInstance( DataSet.class );
        Marshaller u = jc.createMarshaller();

        StringWriter writer = new StringWriter();
        org.hisp.dhis.dataset.DataSet dataSet = dataSetService.getDataSet( DATA_SET_UUID );
        DataSet dxfDataSet = DataSetMapper.convert( dataSet );
        u.marshal( dxfDataSet, writer );

        System.out.println( writer.toString() );
    }

    @Override
    protected boolean emptyDatabaseAfterTest()
    {
        return true;
    }
}
