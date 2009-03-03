package org.hisp.dhis.importexport.dhis14;

import java.io.InputStream;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.importexport.ImportInternalProcess;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportType;

public class Dhis14XmlImportServiceTest
    extends DhisSpringTest
{
    private ImportInternalProcess importService;

    private InputStream inputStream;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    public void setUpTest()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        inputStream = classLoader.getResourceAsStream( "dhis14A.zip" );
        
        importService = (ImportInternalProcess) getBean( "internal-process-DHIS14XMLImportService" );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    public void testImport()
    {
        ImportParams params = new ImportParams();

        params.setType( ImportType.IMPORT );
        params.setDataValues( true );
        params.setExtendedMode( false );
        params.setSkipCheckMatching( false );
        
        importService.importData( params, inputStream );
    }
}
