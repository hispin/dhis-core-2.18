package org.hisp.dhis.dataset.action;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class IndicatorListFilteredByGroup
    implements Action
{
    private String indicatorGroupId;

    private String selectedIndicators[];

    private List<Indicator> indicators;

    private Integer dataSetId;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private Comparator<Indicator> indicatorComparator;

    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public void setIndicatorGroupId( String indicatorGroupId )
    {
        this.indicatorGroupId = indicatorGroupId;
    }

    public void setSelectedIndicators( String[] selectedIndicators )
    {
        this.selectedIndicators = selectedIndicators;
    }

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public String getIndicatorGroupId()
    {
        return indicatorGroupId;
    }

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( indicatorGroupId == null || indicatorGroupId.equals( "ALL" ) )
        {
            indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        }
        else
        {
            IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( Integer.parseInt( indicatorGroupId ) );

            indicators = new ArrayList<Indicator>( indicatorGroup.getMembers() );
        }

        if ( selectedIndicators != null && selectedIndicators.length > 0 )
        {
            Iterator<Indicator> iter = indicators.iterator();

            while ( iter.hasNext() )
            {
                Indicator indicator = iter.next();

                for ( int i = 0; i < selectedIndicators.length; i++ )
                {
                    if ( indicator.getId() == Integer.parseInt( selectedIndicators[i] ) )
                    {
                        iter.remove();
                    }
                }
            }
        }

        if ( dataSetId != null )
        {
            DataSet dataSet = dataSetService.getDataSet( dataSetId );

            indicators.removeAll( dataSet.getIndicators() );
        }

        Collections.sort( indicators, indicatorComparator );

        displayPropertyHandler.handle( indicators );

        return SUCCESS;
    }
}
