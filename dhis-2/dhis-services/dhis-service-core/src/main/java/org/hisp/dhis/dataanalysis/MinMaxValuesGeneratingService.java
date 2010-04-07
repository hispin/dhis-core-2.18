package org.hisp.dhis.dataanalysis;

import static org.hisp.dhis.system.util.MathUtils.isEqual;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class MinMaxValuesGeneratingService
    implements DataAnalysisService
{ // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataAnalysisStore dataAnalysisStore;

    public void setDataAnalysisStore( DataAnalysisStore dataAnalysisStore )
    {
        this.dataAnalysisStore = dataAnalysisStore;
    }

    // -------------------------------------------------------------------------
    // MinMaxValuesGeneratingService implementation
    // -------------------------------------------------------------------------

    public final Collection<MinMaxDataElement> analyse( OrganisationUnit organisationUnit,
        Collection<DataElement> dataElements, Collection<Period> periods, Double stdDevFactor )
    {
        Collection<MinMaxDataElement> minMaxDataElements = new ArrayList<MinMaxDataElement>();

        for ( DataElement dataElement : dataElements )
        {
            if ( dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
            {
                Collection<DataElementCategoryOptionCombo> categoryOptionCombos = dataElement.getCategoryCombo()
                    .getOptionCombos();

                for ( DataElementCategoryOptionCombo categoryOptionCombo : categoryOptionCombos )
                {
                    Double stdDev = dataAnalysisStore.getStandardDeviation( dataElement, categoryOptionCombo,
                        organisationUnit );
                    
                    if ( !isEqual( stdDev, 0.0 ) ) // No values found or no
                    {
                        Double avg = dataAnalysisStore.getAverage( dataElement, categoryOptionCombo, organisationUnit );

                        double deviation = stdDev * stdDevFactor;
                        Double lowerBound = avg - deviation;
                        Double upperBound = avg + deviation;

                        MinMaxDataElement minMaxDataElement = new MinMaxDataElement();
                        minMaxDataElement.setDataElement( dataElement );
                        minMaxDataElement.setOptionCombo( categoryOptionCombo );
                        minMaxDataElement.setSource(  organisationUnit );
                        minMaxDataElement.setMin( lowerBound.intValue() );
                        minMaxDataElement.setMax( upperBound.intValue() );

                        minMaxDataElements.add( minMaxDataElement );
                    }
                }
            }
        }

        return minMaxDataElements;
    }

}
