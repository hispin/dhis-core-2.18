package org.hisp.dhis.importexport.action.exp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
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

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    private DataElementCategoryOptionService categoryOptionService;

    public void setCategoryOptionService( DataElementCategoryOptionService categoryOptionService )
    {
        this.categoryOptionService = categoryOptionService;
    }

    private DataElementCategoryComboService categoryComboService;

    public void setCategoryComboService( DataElementCategoryComboService categoryComboService )
    {
        this.categoryComboService = categoryComboService;
    }

    private DataElementCategoryOptionComboService categoryOptionComboService;

    public void setCategoryOptionComboService( DataElementCategoryOptionComboService categoryOptionComboService )
    {
        this.categoryOptionComboService = categoryOptionComboService;
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

        params.setCategories( categoryService.getAllDataElementCategories() );
        params.setCategoryCombos( categoryComboService.getAllDataElementCategoryCombos() );
        params.setCategoryOptions( categoryOptionService.getAllDataElementCategoryOptions() );
        params.setCategoryOptionCombos( categoryOptionComboService.getAllDataElementCategoryOptionCombos() );
        
        Set<DataElement> dataElements = new HashSet<DataElement>();
        
        if ( selectedIndicators.size() > 0 )
        {
            params.setIndicatorTypes( indicatorService.getAllIndicatorTypes() );
        }
        
        for ( String id : selectedDataElements )
        {
            dataElements.add( dataElementService.getDataElement( Integer.parseInt( id ) ) );
        }
        
        for ( String id : selectedIndicators )
        {
            Indicator indicator = indicatorService.getIndicator( Integer.parseInt( id ) );

            dataElements.addAll( expressionService.getDataElementsInExpression( indicator.getNumerator() ) );
            dataElements.addAll( expressionService.getDataElementsInExpression( indicator.getDenominator() ) );
                        
            params.getIndicators().add( indicator );
        }

        for ( DataElement element : dataElements )
        {
            if ( element instanceof CalculatedDataElement )
            {
                params.getCalculatedDataElements().add( (CalculatedDataElement)element );
            }
            else
            {
                params.getDataElements().add( element );
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
