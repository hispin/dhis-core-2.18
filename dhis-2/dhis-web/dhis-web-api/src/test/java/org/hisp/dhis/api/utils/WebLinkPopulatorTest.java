package org.hisp.dhis.api.utils;

import static org.hisp.dhis.api.utils.WebLinkPopulator.getPath;
import static org.junit.Assert.assertEquals;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.junit.Test;

public class WebLinkPopulatorTest
{

    @Test
    public void testClassToPathMapping()
    {
        assertEquals( "charts", getPath( Charts.class ) );
        assertEquals( "charts", getPath( Chart.class ) );
        assertEquals( "dataElementCategoryOptionCombos", getPath( DataElementCategoryOptionCombo.class ) );
    }

}
