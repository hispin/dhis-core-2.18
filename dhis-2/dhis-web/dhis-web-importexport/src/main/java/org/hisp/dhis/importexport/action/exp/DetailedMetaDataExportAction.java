package org.hisp.dhis.importexport.action.exp;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.ConversionUtils.getIntegerCollection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ExportService;
import org.hisp.dhis.importexport.ImportDataValueService;
import org.hisp.dhis.importexport.ImportExportServiceManager;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;

import com.opensymphony.xwork.Action;

public class DetailedMetaDataExportAction
    implements Action
{
    private static final String FILENAME = "Export_dataelements_indicators.zip";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
        
    private ImportExportServiceManager serviceManager;

    public void setServiceManager( ImportExportServiceManager serviceManager )
    {
        this.serviceManager = serviceManager;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private ImportDataValueService importDataValueService;

    public void setImportDataValueService( ImportDataValueService importDataValueService )
    {
        this.importDataValueService = importDataValueService;
    }

    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String exportFormat;

    public void setExportFormat( String exportFormat )
    {
        this.exportFormat = exportFormat;
    }

    private Collection<String> selectedDataElements = new ArrayList<String>();

    public void setSelectedDataElements( Collection<String> selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }

    private Collection<String> selectedIndicators = new ArrayList<String>();

    public void setSelectedIndicators( Collection<String> selectedIndicators )
    {
        this.selectedIndicators = selectedIndicators;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        importDataValueService.deleteImportDataValues();        
        importObjectService.deleteImportObjects();
        
        ExportParams params = new ExportParams();

        params.setCategories( null );
        params.setCategoryCombos( null );
        params.setCategoryOptions( null );
        params.setCategoryOptionCombos( null );
        
        Set<Integer> dataElements = new HashSet<Integer>();
        
        if ( selectedIndicators.size() > 0 )
        {
            params.setIndicatorTypes( null );
        }
        
        dataElements.addAll( getIntegerCollection( selectedDataElements ) );
        
        params.setIndicators( getIntegerCollection( selectedIndicators ) );
        
        for ( String id : selectedIndicators )
        {
            Indicator indicator = indicatorService.getIndicator( Integer.parseInt( id ) );

            dataElements.addAll( getIdentifiers( DataElement.class, expressionService.getDataElementsInExpression( indicator.getNumerator() ) ) );
            dataElements.addAll( getIdentifiers( DataElement.class, expressionService.getDataElementsInExpression( indicator.getDenominator() ) ) );
        }

        for ( Integer id : dataElements )
        {
            final DataElement element = dataElementService.getDataElement( id );
            
            if ( element instanceof CalculatedDataElement )
            {
                params.getCalculatedDataElements().add( element.getId() );
            }
            else
            {
                params.getDataElements().add( element.getId() );
            }
        }
        
        params.setIncludeDataValues( false );
        
        params.setI18n( i18n );
        params.setFormat( format );

        ExportService exportService = serviceManager.getExportService( exportFormat );
        
        inputStream = exportService.exportData( params );
        
        fileName = FILENAME;
        
        return SUCCESS;
    }
}
