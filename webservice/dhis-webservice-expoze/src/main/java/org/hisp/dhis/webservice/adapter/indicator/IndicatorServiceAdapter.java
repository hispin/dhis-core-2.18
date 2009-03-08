package org.hisp.dhis.webservice.adapter.indicator;

import java.util.Collection;

import org.hisp.dhis.indicator.Indicator;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public interface IndicatorServiceAdapter
{
    Collection<Indicator> getIndicatorsByIndicatorGroup( int indicatorGroupId );
}