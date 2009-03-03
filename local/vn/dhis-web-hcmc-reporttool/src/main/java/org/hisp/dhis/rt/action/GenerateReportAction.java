package org.hisp.dhis.rt.action;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.rt.dataaccess.ReportDataAccess;
import org.hisp.dhis.rt.generators.ReportGenerator;
import org.hisp.dhis.rt.report.ChartElement;
import org.hisp.dhis.rt.report.Element;
import org.hisp.dhis.rt.report.OrgUnitSpesificElement;
import org.hisp.dhis.rt.report.ReportStore;
import org.hisp.dhis.rt.utils.NumberUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: GenerateReportAction.java 2871 2007-02-20 16:04:11Z andegje $
 */
public class GenerateReportAction
    extends AbstractAction
{
    // -----------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------

    private ReportDataAccess reportDataAccess;

    public void setReportDataAccess( ReportDataAccess reportDataAccess )
    {
        this.reportDataAccess = reportDataAccess;
    }

    // -----------------------------------------------------------------------
    // Parameters
    // -----------------------------------------------------------------------

    private boolean preview;

    private String fileName;

    private int reportType;

    public void setPreview( boolean preview )
    {
        this.preview = preview;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    public int getReportType()
    {
        return reportType;
    }

    public void setReportType( int reportType )
    {
        this.reportType = reportType;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( reportType == ReportStore.GENERIC )
        {
            return generateGenericReport();
        }
        else
        {
            return generateOrgUnitSpesificReport();
        }
    }

    private String generateGenericReport()
        throws Exception
    {
        organisationUnitId = getSelectedOrganisationUnitId();

        if ( report() && organisationUnitId() && startDate() && endDate() )
        {
            Date sDate = format.parseDate( startDate );
            Date eDate = format.parseDate( endDate );

            // -----------------------------------------------------------------------
            // Retrieving report elements
            // -----------------------------------------------------------------------

            SortedMap<String, String> reportElements = new TreeMap<String, String>();

            Collection<Element> reportCollection = reportStore.getAllReportElements( report );

            for ( Element reportElement : reportCollection )
            {
                String value = new String();

                if ( reportElement.getType().equals( ReportStore.DATAELEMENT ) )
                {
                    double v;

                    if ( this.isInASemiDataSet( reportElement.getElementId() ) )
                    {
                        v = reportDataAccess.getAggregatedDataValue( reportElement.getElementId(),
                            checkStartDateForSemiData( sDate, eDate ), eDate, String.valueOf( organisationUnitId ) );
                    }
                    else
                    {
                        v = reportDataAccess.getAggregatedDataValue( reportElement.getElementId(), sDate, eDate, String
                            .valueOf( organisationUnitId ) );
                    }
                    if ( v == -1 )
                        value = "";
                    else
                        value = NumberUtils.formatDataValue( v );
                }
                else if ( reportElement.getType().equals( ReportStore.INDICATOR ) )
                {
                    value = NumberUtils.formatIndicatorValue( reportDataAccess.getAggregatedIndicatorValue(
                        reportElement.getElementId(), sDate, eDate, String.valueOf( organisationUnitId ) ) );
                }

                reportElements.put( reportElement.getElementKey(), value );
            }

            // -----------------------------------------------------------------------
            // Retrieving chart elements
            // -----------------------------------------------------------------------

            List<ChartElement> chartElements = new ArrayList<ChartElement>();

            Collection<Element> chartCollection = reportStore.getAllChartElements( report );

            for ( Element chartElement : chartCollection )
            {
                double value = 0;

                if ( chartElement.getType().equals( ReportStore.DATAELEMENT ) )
                {
                    value = reportDataAccess.getAggregatedDataValue( chartElement.getElementId(), sDate, eDate, String
                        .valueOf( organisationUnitId ) );
                }
                else if ( chartElement.getType().equals( ReportStore.INDICATOR ) )
                {
                    value = reportDataAccess.getAggregatedIndicatorValue( chartElement.getElementId(), sDate, eDate,
                        String.valueOf( organisationUnitId ) );
                }

                ChartElement element = new ChartElement( chartElement.getElementKey(), value );

                chartElements.add( element );
            }

            // -----------------------------------------------------------------------
            // Adding report information
            // -----------------------------------------------------------------------

            String unitName = reportDataAccess.getOrganisationUnitName( organisationUnitId );
            String periodName = format.formatDate( sDate ) + " - " + format.formatDate( eDate );

            reportElements.put( "ReportName", report );
            reportElements.put( "OrganisationUnit", unitName );
            reportElements.put( "Period", periodName );

            reportElements.put( "FromDate", format.formatDate( sDate ) );
            reportElements.put( "ToDate", format.formatDate( eDate ) );

            DateFormat getYear = new SimpleDateFormat( "yyyy" );
            DateFormat getMonth = new SimpleDateFormat( "MM" );
            DateFormat getDay = new SimpleDateFormat( "dd" );

            String month_year;
            String th_month_year;

            if ( getMonth.format( sDate ).equals( getMonth.format( eDate ) )
                && getYear.format( sDate ).equals( getYear.format( eDate ) ) )
            {
                month_year = getMonth.format( sDate ) + "/" + getYear.format( sDate );
                th_month_year = "Th\u00E1ng: " + getMonth.format( sDate ) + "/" + getYear.format( sDate );
            }
            else
            {
                month_year = "";
                th_month_year = "";
            }
            reportElements.put( "Month_Year", month_year );
            reportElements.put( "Th_month_year", th_month_year );
            reportElements.put( "Nam_bao_cao", getYear.format( sDate ) );
            Date date = new Date();
            reportElements.put( "day", getDay.format( date ) );
            reportElements.put( "month", getMonth.format( date ) );
            reportElements.put( "year", getYear.format( date ) );

            // -----------------------------------------------------------------------
            // Generating report
            // -----------------------------------------------------------------------

            if ( preview )
            {
                String buffer;
                if ( report.equals( "hcm_quan_huyen" ) || report.equals( "hcm_benh_vien" ) )
                    buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 4 );
                else
                {
                    if ( report.equals( "hcm_nhi_thang" ) || report.equals( "hcm_nhi_quy" ) )
                        buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 2 );
                    else
                    {
                        if ( report.equals( "hcm_nhi_nam" ) )
                            buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 1 );
                        else
                            buffer = ReportGenerator.generateReportPreview( report, reportElements, chartElements );
                    }
                }

                setSessionVar( HTML_BUFFER, buffer );
            }
            else
            {
                fileName = report + "-" + unitName + "-" + periodName + ".pdf";

                if ( report.equals( "hcm_quan_huyen" ) || report.equals( "hcm_benh_vien" ) )
                    inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 4 );
                else
                {
                    if ( report.equals( "hcm_nhi_thang" ) || report.equals( "hcm_nhi_quy" ) )
                        inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 2 );
                    else
                    {
                        if ( report.equals( "hcm_nhi_nam" ) )
                            inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 1 );
                        else
                            inputStream = ReportGenerator.generateReportStream( report, reportElements, chartElements );
                    }
                }
            }

            return SUCCESS;
        }

        return ERROR;
    }

    private String generateOrgUnitSpesificReport()
        throws Exception
    {
        if ( report() && startDate() && endDate() )
        {
            Date sDate = format.parseDate( startDate );
            Date eDate = format.parseDate( endDate );

            // -----------------------------------------------------------------------
            // Retrieving report elements
            // -----------------------------------------------------------------------

            SortedMap<String, String> reportElements = new TreeMap<String, String>();

            Collection<Element> reportCollection = reportStore.getAllReportElements( report );

            for ( Element reportElement : reportCollection )
            {
                String value = new String();

                OrgUnitSpesificElement orgUnitSpesificElement = (OrgUnitSpesificElement) reportElement;

                if ( orgUnitSpesificElement.getType().equals( ReportStore.DATAELEMENT ) )
                {
                    value = NumberUtils.formatDataValue( reportDataAccess.getAggregatedDataValue(
                        orgUnitSpesificElement.getElementId(), sDate, eDate, String.valueOf( orgUnitSpesificElement
                            .getOrganisationUnitId() ) ) );
                }
                else if ( orgUnitSpesificElement.getType().equals( ReportStore.INDICATOR ) )
                {
                    value = NumberUtils.formatIndicatorValue( reportDataAccess.getAggregatedIndicatorValue(
                        orgUnitSpesificElement.getElementId(), sDate, eDate, String.valueOf( orgUnitSpesificElement
                            .getOrganisationUnitId() ) ) );
                }

                reportElements.put( orgUnitSpesificElement.getElementKey(), value );
            }

            // -----------------------------------------------------------------------
            // Retrieving chart elements
            // -----------------------------------------------------------------------

            List<ChartElement> chartElements = new ArrayList<ChartElement>();

            Collection<Element> chartCollection = reportStore.getAllChartElements( report );

            for ( Element chartElement : chartCollection )
            {
                double value = 0;

                OrgUnitSpesificElement orgUnitSpesificElement = (OrgUnitSpesificElement) chartElement;

                if ( orgUnitSpesificElement.getType().equals( ReportStore.DATAELEMENT ) )
                {
                    value = reportDataAccess.getAggregatedDataValue( orgUnitSpesificElement.getElementId(), sDate,
                        eDate, String.valueOf( orgUnitSpesificElement.getOrganisationUnitId() ) );
                }
                else if ( orgUnitSpesificElement.getType().equals( ReportStore.INDICATOR ) )
                {
                    value = reportDataAccess.getAggregatedIndicatorValue( orgUnitSpesificElement.getElementId(), sDate,
                        eDate, String.valueOf( orgUnitSpesificElement.getOrganisationUnitId() ) );
                }

                ChartElement element = new ChartElement( orgUnitSpesificElement.getElementKey(), value );

                chartElements.add( element );
            }

            // -----------------------------------------------------------------------
            // Adding report information
            // -----------------------------------------------------------------------

            String periodName = format.formatDate( sDate ) + " - " + format.formatDate( eDate );

            reportElements.put( "ReportName", report );
            reportElements.put( "OrganisationUnit", "" );
            reportElements.put( "Period", periodName );

            // -----------------------------------------------------------------------
            // Generating report
            // -----------------------------------------------------------------------

            if ( preview )
            {
                String buffer = ReportGenerator.generateReportPreview( report, reportElements, chartElements );

                setSessionVar( HTML_BUFFER, buffer );
            }
            else
            {
                fileName = report + "-" + periodName + ".pdf";

                inputStream = ReportGenerator.generateReportStream( report, reportElements, chartElements );
            }

            return SUCCESS;
        }

        return ERROR;
    }

    private boolean isInASemiDataSet( int elementId )
        throws Exception
    {
        Collection<DataSet> allSemiDataSets = reportDataAccess.getAllSemiDataSets();
        DataElement checkedDE = reportDataAccess.getDataElement( elementId );

        for ( DataSet each : allSemiDataSets )
        {
            Collection<DataElement> dataElements = each.getDataElements();
            for ( DataElement de : dataElements )
            {
                if ( de.equals( checkedDE ) )
                    return true;
            }
        }
        return false;
    }

    private Date checkStartDateForSemiData( Date startDate, Date endDate )
    {
        DateFormat yearFormat = new SimpleDateFormat( "yyyy" );
        DateFormat dayMonthFormat = new SimpleDateFormat( "dd-MM" );

        String startDay = dayMonthFormat.format( startDate );
        String endDay = dayMonthFormat.format( endDate );
        String startYear = yearFormat.format( startDate );
        String endYear = yearFormat.format( endDate );

        Date result;

        if ( startDay.equals( "01-01" ) && endDay.equals( "30-06" ) && startYear.equals( endYear ) )
            result = this.parseDate( "01-04-" + endYear );
        else
        {
            if ( startDay.equals( "01-01" ) && endDay.equals( "30-09" ) && startYear.equals( endYear ) )
                result = this.parseDate( "01-07-" + endYear );
            else
            {
                if ( startDay.equals( "01-01" ) && endDay.equals( "31-12" ) && startYear.equals( endYear ) )
                    result = this.parseDate( "01-10-" + endYear );
                else
                    result = startDate;
            }
        }
        return result;
    }

    private Date parseDate( String date )
    {
        DateFormat dayMonthYearFormat = new SimpleDateFormat( "dd-MM-yyyy" );
        Date result;
        try
        {
            result = dayMonthYearFormat.parse( date );
        }
        catch ( ParseException e )
        {
            return null;
        }
        return result;
    }
}
