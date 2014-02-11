package org.hisp.dhis.importexport.dhis14.file.rowhandler;

import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.importer.DataSetImporter;
import org.hisp.dhis.period.DailyPeriodType;

import com.ibatis.sqlmap.client.event.RowHandler;

public class DataSetDailyCaptureRowHandler
    extends DataSetImporter
    implements RowHandler
{
    private ImportParams params;

    private Map<String, Integer> periodTypeMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataSetDailyCaptureRowHandler( BatchHandler<DataSet> batchHandler, ImportObjectService importObjectService,
        DataSetService dataSetService, Map<String, Integer> periodTypeMapping, ImportParams params,
        ImportAnalyser importAnalyser )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataSetService = dataSetService;
        this.periodTypeMapping = periodTypeMapping;
        this.params = params;
        this.importAnalyser = importAnalyser;
    }

    // -------------------------------------------------------------------------
    // RowHandler implementation
    // -------------------------------------------------------------------------

    public void handleRow( Object object )
    {
        final DataSet dataSet = (DataSet) object;

        Integer dailyPeriodTypeId = periodTypeMapping.get( DailyPeriodType.NAME );

        dataSet.getPeriodType().setId( dailyPeriodTypeId );

        importObject( dataSet, params );
    }
}
