package org.hisp.dhis.commons.action;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.system.util.IdentifiableObjectUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: GetIndicatorsAction.java 3305 2007-05-14 18:55:52Z larshelg $
 */
public class GetIndicatorsAction
    extends ActionPagingSupport<Indicator>
{
    private final static int ALL = 0;

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

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<Indicator> indicatorComparator;

    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<Integer> removeDataSets = new ArrayList<Integer>();

    public void setRemoveDataSets( String removeDataSets )
    {
        if ( removeDataSets.length() > 0 )
        {
            List<String> stringList = Arrays.asList( removeDataSets.split( "," ) );

            for ( String s : stringList )
            {
                this.removeDataSets.add( Integer.parseInt( s ) );
            }
        }
    }

    private List<Integer> removeIndicators = new ArrayList<Integer>();

    public void setRemoveIndicators( String removeIndicators )
    {
        if ( removeIndicators.length() > 0 )
        {
            List<String> stringList = Arrays.asList( removeIndicators.split( "," ) );

            for ( String s : stringList )
            {
                this.removeIndicators.add( Integer.parseInt( s ) );
            }
        }
    }

    private List<Indicator> indicators;

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( id != null && id != ALL )
        {
            IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( id );

            if ( indicatorGroup != null )
            {
                indicators = new ArrayList<Indicator>( indicatorGroup.getMembers() );
            }
        }
        else if ( dataSetId != null )
        {
            DataSet dataset = dataSetService.getDataSet( dataSetId );

            if ( dataset != null )
            {
                indicators = new ArrayList<Indicator>( dataset.getIndicators() );
            }
        }
        else
        {
            indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        }

        if ( indicators == null )
        {
            indicators = new ArrayList<Indicator>();
        }

        if ( removeDataSets.size() > 0 )
        {
            for ( Integer id : removeDataSets )
            {
                DataSet dataSet = dataSetService.getDataSet( id );
                indicators.removeAll( dataSet.getDataElements() );
            }
        }

        if ( removeIndicators.size() > 0 )
        {
            for ( Integer id : removeIndicators )
            {
                Indicator indicator = indicatorService.getIndicator( id );
                indicators.remove( indicator );
            }
        }

        if ( key != null )
        {
            indicators = IdentifiableObjectUtils.filterNameByKey( indicators, key, true );
        }

        Collections.sort( indicators, indicatorComparator );

        if ( usePaging )
        {
            this.paging = createPaging( indicators.size() );

            indicators = indicators.subList( paging.getStartPos(), paging.getEndPos() );
        }

        displayPropertyHandler.handle( indicators );

        return SUCCESS;
    }
}
