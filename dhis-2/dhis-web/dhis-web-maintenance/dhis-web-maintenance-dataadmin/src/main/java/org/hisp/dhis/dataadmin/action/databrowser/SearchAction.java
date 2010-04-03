package org.hisp.dhis.dataadmin.action.databrowser;

/*
 * Copyright (c) 2004-${year}, University of Oslo
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.databrowser.DataBrowserService;
import org.hisp.dhis.databrowser.DataBrowserTable;
import org.hisp.dhis.databrowser.MetaValue;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.util.SessionUtils;

import com.opensymphony.xwork2.Action;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author espenjac, joakibj, briane, eivinhb
 * @version $Id$
 */
public class SearchAction
    implements Action
{
    private static final String KEY_PERIODTYPEID = "periodTypeId";

    private static final String KEY_DATABROWSERTITLENAME = "dataBrowserTitleName";

    private static final String KEY_DATABROWSERFROMDATE = "dataBrowserFromDate";

    private static final String KEY_DATABROWSERTODATE = "dataBrowserToDate";

    private static final String KEY_DATABROWSERPERIODTYPE = "dataBrowserPeriodType";

    private static final String KEY_DATABROWSERTABLE = "dataBrowserTableResults";

    private static final String HYPHEN = " - ";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataBrowserService dataBrowserService;

    public void setDataBrowserService( DataBrowserService dataBrowserService )
    {
        this.dataBrowserService = dataBrowserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Input / output
    // -------------------------------------------------------------------------

    private OrganisationUnit selectedUnit;

    public long getQueryTime()
    {
        return dataBrowserTable.getQueryTime();
    }

    public int getQueryCount()
    {
        return dataBrowserTable.getQueryCount();
    }

    public List<MetaValue> getAllColumns()
    {
        return dataBrowserTable.getColumns();
    }

    private DataBrowserTable dataBrowserTable;

    public DataBrowserTable getDataBrowserTable()
    {
        return dataBrowserTable;
    }

    public Collection<DataElementGroup> getDataElementGroups()
    {
        return dataElementService.getAllDataElementGroups();
    }

    public List<List<Integer>> getAllCounts()
    {
        return dataBrowserTable.getCounts();
    }

    public Iterator<MetaValue> getRowNamesIterator()
    {
        return dataBrowserTable.getRows().iterator();
    }

    private String selectedUnitChanger;

    public void setSelectedUnitChanger( String selectedUnitChanger )
    {
        this.selectedUnitChanger = selectedUnitChanger;
    }

    private String searchOption;

    public String getSearchOption()
    {
        return searchOption;
    }

    public void setSearchOption( String searchOption )
    {
        this.searchOption = searchOption;
    }

    private String toDate;

    public String getToDate()
    {
        return toDate;
    }

    public void setToDate( String toDate )
    {
        this.toDate = toDate;
    }

    private String fromDate;

    public String getFromDate()
    {
        return fromDate;
    }

    public void setFromDate( String fromDate )
    {
        this.fromDate = fromDate;
    }

    private String fromToDate;

    public String getFromToDate()
    {
        return fromToDate;
    }

    private String periodTypeId;

    public String getPeriodTypeId()
    {
        return periodTypeId;
    }

    public void setPeriodTypeId( String periodTypeId )
    {
        SessionUtils.setSessionVar( KEY_PERIODTYPEID, periodTypeId );
    }

    private String parent;

    public String getParent()
    {
        return parent;
    }

    public void setParent( String parent )
    {
        this.parent = parent;
    }

    private String orgunitid;

    public String getOrgunitid()
    {
        return orgunitid;
    }

    public void setOrgunitid( String orgunitid )
    {
        this.orgunitid = orgunitid;
    }

    private String tmpParent;

    public String getTmpParent()
    {
        return tmpParent;
    }

    private long requestTime;

    public long getRequestTime()
    {
        return requestTime;
    }

    private String dataElementName;

    public String getDataElementName()
    {
        return dataElementName;
    }

    public String getParentName()
    {
        if ( searchOption.equals( "OrganisationUnit" ) )
        {
            return selectedUnit.getName();
        }

        if ( parent == null )
        {
            return "";
        }

        if ( searchOption.equals( "DataSet" ) )
        {
            return dataSetService.getDataSet( Integer.parseInt( parent ) ).getName();
        }

        if ( searchOption.equals( "OrganisationUnitGroup" ) )
        {
            return organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( parent ) ).getName();
        }

        if ( searchOption.equals( "DataElementGroup" ) )
        {
            return dataElementService.getDataElementGroup( Integer.parseInt( parent ) ).getName();
        }

        return "";
    }

    public String getCurrentParentsParent()
    {
        String name = "";
        try
        {
            name = selectedUnit.getParent().getName();
        }
        catch ( Exception e )
        {
            name = "";
        }
        return name;
    }

    public List<OrganisationUnit> getCurrentChildren()
    {
        Set<OrganisationUnit> tmp = selectedUnit.getChildren();
        List<OrganisationUnit> list = new ArrayList<OrganisationUnit>();

        for ( OrganisationUnit o : tmp )
        {
            if ( o.getChildren().size() > 0 )
            {
                list.add( o );
            }
        }
        Collections.sort( list, new OrganisationUnitNameComparator() );

        return list;
    }

    private List<MetaValue> allColumnsConverted;

    public List<MetaValue> getAllColumnsConverted()
    {
        return allColumnsConverted;
    }

    public List<OrganisationUnit> getBreadCrumbOrgUnit()
    {
        List<OrganisationUnit> myList = new ArrayList<OrganisationUnit>();

        boolean loop = true;
        OrganisationUnit currentOrgUnit = selectedUnit;
        while ( loop )
        {
            myList.add( currentOrgUnit );
            if ( currentOrgUnit.getParent() == null )
            {
                loop = false;
            }
            else
            {
                currentOrgUnit = currentOrgUnit.getParent();
            }
        }
        Collections.reverse( myList );

        return myList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        long before = System.currentTimeMillis();

        periodTypeId = (String) SessionUtils.getSessionVar( KEY_PERIODTYPEID );

        // If set, change the current selected org unit
        if ( selectedUnitChanger != null )
        {
            selectionManager.setSelectedOrganisationUnit( this.organisationUnitService.getOrganisationUnit( Integer
                .parseInt( selectedUnitChanger.trim() ) ) );
        }

        // Checks if the selected unit is a leaf node. If it is, we must add
        // parent as the same parameter value
        if ( parent == null && selectionManager.getSelectedOrganisationUnit() != null
            && selectionManager.getSelectedOrganisationUnit().getChildren().size() == 0 )
        {
            parent = selectionManager.getSelectedOrganisationUnit().getId() + "";
        }

        // Check if the second selected date is later than the first selected
        // date
        if ( fromDate.length() == 0 && toDate.length() == 0 )
        {
            if ( checkDates( fromDate, toDate ) )
            {
                return ERROR;
            }
        }

        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeId );

        if ( searchOption.equals( "DataSet" ) )
        {
            if ( parent != null )
            {
                // Show DataElement for a given DataSet
                Integer parentInt = Integer.parseInt( parent );

                dataBrowserTable = dataBrowserService.getCountDataElementsForDataSetInPeriod( parentInt, fromDate,
                    toDate, periodType );

            }
            else
            {
                // Get all DataSets
                dataBrowserTable = dataBrowserService.getDataSetsInPeriod( fromDate, toDate, periodType );
            }
        }
        else if ( searchOption.equals( "OrganisationUnitGroup" ) )
        {
            if ( parent != null )
            {
                // Show DataElementGroups
                Integer parentInt = Integer.parseInt( parent );
                dataBrowserTable = dataBrowserService.getCountDataElementGroupsForOrgUnitGroupInPeriod( parentInt,
                    fromDate, toDate, periodType );
            }
            else
            {
                dataBrowserTable = dataBrowserService.getOrgUnitGroupsInPeriod( fromDate, toDate, periodType );
            }
        }
        else if ( searchOption.equals( "DataElementGroup" ) )
        {
            // Get all DataElementGroup objects
            if ( parent != null )
            {
                // Show DataElement
                Integer parentInt = Integer.parseInt( parent );

                dataBrowserTable = dataBrowserService.getCountDataElementsForDataElementGroupInPeriod( parentInt,
                    fromDate, toDate, periodType );
            }
            else
            {

                dataBrowserTable = dataBrowserService.getDataElementGroupsInPeriod( fromDate, toDate, periodType );
            }
        }
        else if ( searchOption.equals( "OrganisationUnit" ) )
        {
            selectedUnit = selectionManager.getSelectedOrganisationUnit();
            if ( parent != null )
            {
                Integer parentInt = Integer.parseInt( parent );
                // Show DataElement values only for specified organization unit
                dataBrowserTable = dataBrowserService.getCountDataElementsForOrgUnitInPeriod( parentInt, fromDate,
                    toDate, periodType );
            }
            else if ( selectedUnit != null )
            {
                dataBrowserTable = dataBrowserService.getOrgUnitsInPeriod( selectedUnit.getId(), fromDate, toDate,
                    periodType );

            }
            else
            {
                return ERROR;
            }
        }
        else
        {
            return ERROR;
        }

        // Set DataBrowserTable variable for PDF export
        SessionUtils.setSessionVar( KEY_DATABROWSERTABLE, dataBrowserTable );

        requestTime = System.currentTimeMillis() - before;

        // Convert column date names
        convertColumnNames( dataBrowserTable );

        // Set DataBrowserTable variable for PDF export
        setExportPDFVariables();

        // Get format standard for periods which appropriate with from date, to date and period type
        fromToDate = getFromToDateFormat( periodType, fromDate, toDate );

        if ( fromToDate == null )
        {
            fromToDate = "";
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * This is a helper method for checking if the fromDate is later than the
     * toDate. This is necessary in case a user sends the dates with HTTP GET.
     * 
     * @param fromDate
     * @param toDate
     * @return boolean
     */
    private boolean checkDates( String fromDate, String toDate )
    {
        String formatString = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat( formatString );

        Date date1 = new Date();
        Date date2 = new Date();

        try
        {
            date1 = sdf.parse( fromDate );
            date2 = sdf.parse( toDate );
        }
        catch ( ParseException e )
        {
            return false; // The user hasn't specified any dates
        }

        if ( !date1.before( date2 ) )
        {
            return true; // Return true if date2 is earlier than date1
        }
        else
        {
            return false;
        }
    }

    /**
     * This is a helper method for setting session variables for PDF export
     */
    private void setExportPDFVariables()
    {
        SessionUtils.setSessionVar( KEY_DATABROWSERTITLENAME, searchOption + " - " + getParentName() );
        SessionUtils.setSessionVar( KEY_DATABROWSERFROMDATE, fromDate );
        SessionUtils.setSessionVar( KEY_DATABROWSERTODATE, toDate );
        SessionUtils.setSessionVar( KEY_DATABROWSERPERIODTYPE, periodTypeId );
        SessionUtils.setSessionVar( KEY_DATABROWSERTABLE, dataBrowserTable );
    }

    /**
     * This is a helper method for populating a list of converted column names
     * 
     * @param DataBrowserTable
     */
    private void convertColumnNames( DataBrowserTable dataBrowserTable )
    {
        allColumnsConverted = dataBrowserTable.getColumns();

        for ( MetaValue col : allColumnsConverted )
        {
            col.setName( DateUtils.convertDate( col.getName() ) );
        }
    }

    /**
     * This is a helper method for checking if the fromDate is later than the
     * toDate. This is necessary in case a user sends the dates with HTTP GET.
     * 
     * @param fromDate
     * @param toDate
     * @return List of Periods
     */
    private List<Period> getPeriodsList( PeriodType periodType, String fromDate, String toDate )
    {
        String formatString = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat( formatString );

        Date date1 = new Date();
        Date date2 = new Date();

        try
        {
            date1 = sdf.parse( fromDate );
            date2 = sdf.parse( toDate );

            List<Period> periods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, date1,
                date2 ) );

            if ( periods.isEmpty() )
            {
                CalendarPeriodType calendarPeriodType = (CalendarPeriodType) periodType;

                periods.add( calendarPeriodType.createPeriod( date1 ) );
                periods.add( calendarPeriodType.createPeriod( date2 ) );
            }

            return periods;
        }
        catch ( ParseException e )
        {
            return null; // The user hasn't specified any dates
        }
    }

    @SuppressWarnings( "unchecked" )
    private String getFromToDateFormat( PeriodType periodType, String fromDate, String toDate )
    {
        String stringFormatDate = "";
        List<Period> periods = new ArrayList( this.getPeriodsList( periodType, fromDate, toDate ) );

        for ( Period period : periods )
        {
            String sTemp = format.formatPeriod( period );
            
            if ( !stringFormatDate.contains( sTemp ) )
            {
                stringFormatDate += HYPHEN + sTemp;
            }
        }

        return stringFormatDate;
    }
}
