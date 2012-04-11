package org.hisp.dhis.importexport.action.util;

import java.io.InputStream;

import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.importexport.ImportStrategy;

public class ImportDataValueTask
    implements Runnable
{    
    private DataValueSetService dataValueSetService;
    private InputStream in;
    private boolean dryRun;
    private ImportStrategy strategy;
    
    public ImportDataValueTask( DataValueSetService dataValueSetService, InputStream in, boolean dryRun, ImportStrategy strategy )
    {
        this.dataValueSetService = dataValueSetService;
        this.in = in;
        this.dryRun = dryRun;
        this.strategy = strategy;
    }
    
    @Override
    public void run()
    {
        dataValueSetService.saveDataValueSet( in, IdentifiableProperty.UID, IdentifiableProperty.UID, dryRun, strategy );
    }
}
