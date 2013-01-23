package org.hisp.dhis.adhoc;

import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomFormWriter
    implements Command
{
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Override
    public void execute()
    {
    }
}
