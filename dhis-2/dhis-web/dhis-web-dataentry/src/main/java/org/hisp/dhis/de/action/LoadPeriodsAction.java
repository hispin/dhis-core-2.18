package org.hisp.dhis.de.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.de.state.SelectedStateManager;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

public class LoadPeriodsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    private boolean periodValid;

    public boolean isPeriodValid()
    {
        return periodValid;
    }
    
    private Set<DataElement> significantZeros = new HashSet<DataElement>();

    public Set<DataElement> getSignificantZeros()
    {
        return significantZeros;
    }
    
    private Collection<Indicator> indicators = new HashSet<Indicator>();

    public Collection<Indicator> getIndicators()
    {
        return indicators;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        DataSet selectedDataSet = dataSetService.getDataSet( dataSetId );
        
        if ( selectedDataSet != null )
        {
            // -----------------------------------------------------------------
            // Check if previous data set has same period type as selected
            // -----------------------------------------------------------------

            DataSet previousDataSet = selectedStateManager.getSelectedDataSet();
            
            if ( previousDataSet != null && previousDataSet.getPeriodType().equals( selectedDataSet.getPeriodType() ) )
            {
                periodValid = true;
            }
            else
            {
                selectedStateManager.clearSelectedPeriod();
            }

            // -----------------------------------------------------------------
            // Load periods for period type of selected data set
            // -----------------------------------------------------------------

            selectedStateManager.setSelectedDataSet( selectedDataSet );
            
            periods = selectedStateManager.getPeriodList();
            
            for ( Period period : periods )
            {
                period.setName( format.formatPeriod( period ) );
            }

            // -----------------------------------------------------------------
            // Load data elemements for which zero values are insignificant
            // -----------------------------------------------------------------

            for ( DataElement dataElement : selectedDataSet.getDataElements() )
            {
                if ( dataElement.isZeroIsSignificant() )
                {
                    significantZeros.add( dataElement );
                }
            }

            // -----------------------------------------------------------------
            // Explode and add indicators from data set
            // -----------------------------------------------------------------

            for ( Indicator indicator : selectedDataSet.getIndicators() )
            {
                indicator.setExplodedNumerator( expressionService.explodeExpression( indicator.getNumerator() ) );
                indicator.setExplodedDenominator( expressionService.explodeExpression( indicator.getDenominator() ) );
                
                indicators.add( indicator );
            }
            
            // -----------------------------------------------------------------
            // Clear display mode when loading new data set
            // -----------------------------------------------------------------

            selectedStateManager.clearSelectedDisplayMode();
        }

        return SUCCESS;
    }
}
