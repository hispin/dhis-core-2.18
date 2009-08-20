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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.databrowser.DataBrowserService;
import org.hisp.dhis.databrowser.DataBrowserTable;
import org.hisp.dhis.databrowser.MetaValue;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.util.SessionUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author espenjac, joakibj
 * @version $Id$
 */
public class SearchAction
    implements Action
{
    private static final String KEY_PERIODTYPEID = "periodTypeId";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataBrowserService dataBrowserService;

    public void setDataBrowserService( DataBrowserService dataBrowserService )
    {
        this.dataBrowserService = dataBrowserService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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

    private DataBrowserTable dataBrowserTable;

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

    public DataBrowserTable getDataBrowserTable()
    {
        return dataBrowserTable;
    }

    public List<List<Integer>> getAllCounts()
    {
        return dataBrowserTable.getCounts();
    }

    public Iterator<MetaValue> getRowNamesIterator()
    {
        return dataBrowserTable.getRows().iterator();
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

    public String getParentName()
    {
        if ( searchOption.equals( "OrganisationUnit" ) )
        {
            return selectedUnit.getName();
        }

        if ( tmpParent == null )
        {
            return "";
        }

        if ( searchOption.equals( "DataSet" ) )
        {
            return dataSetService.getDataSet( Integer.parseInt( tmpParent ) ).getName();
        }

        return "";
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        long before = System.currentTimeMillis();
        
        periodTypeId = (String) SessionUtils.getSessionVar( KEY_PERIODTYPEID );

        // Check if the second selected date is later than the first selected date
        
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
            // Get all dataset objects
            
            if ( fromDate.length() == 0 && toDate.length() == 0 )
            {
                dataBrowserTable = dataBrowserService.getAllCountDataSetsByPeriodType( periodType );
            }
            else
            {
                dataBrowserTable = dataBrowserService.getCountDataSetsInPeriod( fromDate, toDate, periodType );
            }

            if ( parent != null )
            {
                // Show dataelement

                Integer parentInt = Integer.parseInt( parent );

                if ( fromDate.length() == 0 && toDate.length() == 0 )
                {
                    dataBrowserTable = dataBrowserService.getAllCountDataElementsByPeriodType( parentInt, periodType );
                }
                else
                {
                    dataBrowserTable = dataBrowserService.getCountDataElementsInPeriod( parentInt, fromDate, toDate,
                        periodType );
                }

                tmpParent = parent;
            }

        }
        else if ( searchOption.equals( "OrganisationUnit" ) )
        {
            selectedUnit = selectionManager.getSelectedOrganisationUnit();

            if ( selectedUnit != null )
            {

                if ( fromDate.length() == 0 && toDate.length() == 0 )
                {
                    dataBrowserTable = dataBrowserService.getAllCountOrgUnitsByPeriodType( selectedUnit.getId(), periodType );
                }
                else
                {
                    dataBrowserTable = dataBrowserService.getCountOrgUnitsInPeriod( selectedUnit.getId(), fromDate, toDate, periodType );
                }

                requestTime = System.currentTimeMillis() - before;
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
        
        requestTime = System.currentTimeMillis() - before;
        
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
     * @return
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
}
