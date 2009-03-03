package org.hisp.dhis.options.sortorder;

/*
 * Copyright (c) 2004-2007, University of Oslo
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
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.comparator.DataElementAlternativeNameComparator;
import org.hisp.dhis.dataelement.comparator.DataElementCodeComparator;
import org.hisp.dhis.dataelement.comparator.DataElementNameComparator;
import org.hisp.dhis.dataelement.comparator.DataElementShortNameComparator;
import org.hisp.dhis.dataelement.comparator.DataElementSortOrderComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.comparator.DataSetCodeComparator;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.dataset.comparator.DataSetShortNameComparator;
import org.hisp.dhis.dataset.comparator.DataSetSortOrderComparator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.comparator.IndicatorAlternativeNameComparator;
import org.hisp.dhis.indicator.comparator.IndicatorCodeComparator;
import org.hisp.dhis.indicator.comparator.IndicatorNameComparator;
import org.hisp.dhis.indicator.comparator.IndicatorShortNameComparator;
import org.hisp.dhis.indicator.comparator.IndicatorSortOrderComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitCodeComparator;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitShortNameComparator;
import org.hisp.dhis.user.NoCurrentUserException;
import org.hisp.dhis.user.UserSettingService;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultSortOrderManager.java 6256 2008-11-10 17:10:30Z larshelg $
 */
public class DefaultSortOrderManager
    implements SortOrderManager
{
    private final static String SETTING_NAME_SORT_ORDER = "currentSortOrder";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // SortOrderManager implementation
    // -------------------------------------------------------------------------

    public void setCurrentSortOrder( String sortOrder )
    {
        try
        {
            userSettingService.saveUserSetting( SETTING_NAME_SORT_ORDER, sortOrder );
        }
        catch ( NoCurrentUserException e )
        {
        }
    }

    public String getCurrentSortOrder()
    {
        return (String) userSettingService.getUserSetting( SETTING_NAME_SORT_ORDER, SORT_ORDER_NAME );
    }

    public Comparator<DataElement> getCurrentDataElementSortOrderComparator()
    {
        String sortOrder = getCurrentSortOrder();

        if ( sortOrder != null )
        {
            if ( sortOrder.equals( SORT_ORDER_NAME ) )
            {
                return new DataElementNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_SHORTNAME ) )
            {
                return new DataElementShortNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_ALTERNATIVENAME ) )
            {
                return new DataElementAlternativeNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_CODE ) )
            {
                return new DataElementCodeComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_CUSTOM ) )
            {
                return new DataElementSortOrderComparator();
            }
        }

        return new DataElementNameComparator();
    }
    
    public Comparator<Indicator> getCurrentIndicatorSortOrderComparator()
    {
        String sortOrder = getCurrentSortOrder();

        if ( sortOrder != null )
        {
            if ( sortOrder.equals( SORT_ORDER_NAME ) )
            {
                return new IndicatorNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_SHORTNAME ) )
            {
                return new IndicatorShortNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_ALTERNATIVENAME ) )
            {
                return new IndicatorAlternativeNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_CODE ) )
            {
                return new IndicatorCodeComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_CUSTOM ) )
            {
                return new IndicatorSortOrderComparator();
            }
        }

        return new IndicatorNameComparator();
    }
    
    public Comparator<OrganisationUnit> getCurrentOrganisationUnitSortOrderComparator()
    {
        String sortOrder = getCurrentSortOrder();

        if ( sortOrder != null )
        {
            if ( sortOrder.equals( SORT_ORDER_NAME ) || sortOrder.equals( SORT_ORDER_ALTERNATIVENAME ) )
            {
                return new OrganisationUnitNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_SHORTNAME ) )
            {
                return new OrganisationUnitShortNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_CODE ) )
            {
                return new OrganisationUnitCodeComparator();
            }
            
            // Custom sort order not implemented
        }
        
        return new OrganisationUnitNameComparator();
    }
    
    public Comparator<DataSet> getCurrentDataSetSortOrderComparator()
    {
        String sortOrder = getCurrentSortOrder();
        
        if ( sortOrder != null )
        {
            if ( sortOrder.equals( SORT_ORDER_NAME ) || sortOrder.equals( SORT_ORDER_ALTERNATIVENAME ) )
            {
                return new DataSetNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_SHORTNAME ) )
            {
                return new DataSetShortNameComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_CODE ) )
            {
                return new DataSetCodeComparator();
            }
            if ( sortOrder.equals( SORT_ORDER_CUSTOM ) )
            {
                return new DataSetSortOrderComparator();
            }
        }
        
        return new DataSetNameComparator();
    }

    public List<String> getSortOrders()
    {
        List<String> list = new ArrayList<String>();

        list.add( SORT_ORDER_NAME );
        list.add( SORT_ORDER_SHORTNAME );
        list.add( SORT_ORDER_ALTERNATIVENAME );
        list.add( SORT_ORDER_CODE );
        list.add( SORT_ORDER_CUSTOM );

        return list;
    }
}
