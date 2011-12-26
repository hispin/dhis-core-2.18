package org.hisp.dhis.options.sortorder;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.hisp.dhis.user.UserSettingService;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultSortOrderManager.java 6256 2008-11-10 17:10:30Z larshelg $
 */
public class DefaultSortOrderManager
    implements SortOrderManager
{
    private final static String SETTING_NAME_SORT_ORDER = "currentSortOrder";

    private Map<String, Comparator<DataElement>> dataElementComparators;
    private Map<String, Comparator<Indicator>> indicatorComparators;
    private Map<String, Comparator<OrganisationUnit>> organisationUnitComparators;
    private Map<String, Comparator<DataSet>> dataSetComparators;
    
    public void init()
    {
        dataElementComparators = new HashMap<String, Comparator<DataElement>>();        
        dataElementComparators.put( SORT_ORDER_NAME, new DataElementNameComparator() );
        dataElementComparators.put( SORT_ORDER_SHORTNAME, new DataElementShortNameComparator() );
        dataElementComparators.put( SORT_ORDER_ALTERNATIVENAME, new DataElementAlternativeNameComparator() );
        dataElementComparators.put( SORT_ORDER_CODE, new DataElementCodeComparator() );
        dataElementComparators.put( SORT_ORDER_CUSTOM, new DataElementSortOrderComparator() );
        
        indicatorComparators = new HashMap<String, Comparator<Indicator>>();        
        indicatorComparators.put( SORT_ORDER_NAME, new IndicatorNameComparator() );
        indicatorComparators.put( SORT_ORDER_SHORTNAME, new IndicatorShortNameComparator() );
        indicatorComparators.put( SORT_ORDER_ALTERNATIVENAME, new IndicatorAlternativeNameComparator() );
        indicatorComparators.put( SORT_ORDER_CODE, new IndicatorCodeComparator() );
        indicatorComparators.put( SORT_ORDER_CUSTOM, new IndicatorSortOrderComparator() );
        
        organisationUnitComparators = new HashMap<String, Comparator<OrganisationUnit>>();        
        organisationUnitComparators.put( SORT_ORDER_NAME, new OrganisationUnitNameComparator() );
        organisationUnitComparators.put( SORT_ORDER_SHORTNAME, new OrganisationUnitShortNameComparator() );
        organisationUnitComparators.put( SORT_ORDER_ALTERNATIVENAME, new OrganisationUnitNameComparator() ); // SIC
        organisationUnitComparators.put( SORT_ORDER_CODE, new OrganisationUnitCodeComparator() );
        organisationUnitComparators.put( SORT_ORDER_CUSTOM, new OrganisationUnitNameComparator() ); // SIC
        
        dataSetComparators = new HashMap<String, Comparator<DataSet>>();        
        dataSetComparators.put( SORT_ORDER_NAME, new DataSetNameComparator() );
        dataSetComparators.put( SORT_ORDER_SHORTNAME, new DataSetShortNameComparator() );
        dataSetComparators.put( SORT_ORDER_ALTERNATIVENAME, new DataSetNameComparator() ); // SIC
        dataSetComparators.put( SORT_ORDER_CODE, new DataSetCodeComparator() );
        dataSetComparators.put( SORT_ORDER_CUSTOM, new DataSetSortOrderComparator() );
    }
    
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
        userSettingService.saveUserSetting( SETTING_NAME_SORT_ORDER, sortOrder );
    }

    public String getCurrentSortOrder()
    {
        return (String) userSettingService.getUserSetting( SETTING_NAME_SORT_ORDER, SORT_ORDER_NAME );
    }

    public Comparator<DataElement> getCurrentDataElementSortOrderComparator()
    {
        return dataElementComparators.get( getCurrentSortOrder() );
    }
    
    public Comparator<Indicator> getCurrentIndicatorSortOrderComparator()
    {
        return indicatorComparators.get( getCurrentSortOrder() );
    }
    
    public Comparator<OrganisationUnit> getCurrentOrganisationUnitSortOrderComparator()
    {
        return organisationUnitComparators.get( getCurrentSortOrder() );
    }
    
    public Comparator<DataSet> getCurrentDataSetSortOrderComparator()
    {
        return dataSetComparators.get( getCurrentSortOrder() );
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
