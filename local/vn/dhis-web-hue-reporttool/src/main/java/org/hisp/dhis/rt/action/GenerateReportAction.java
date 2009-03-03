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
 * * Neither the name of the <Organisation> nor the names of its contributors may
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.rt.dataaccess.ReportDataAccess;
import org.hisp.dhis.rt.generators.ReportGenerator;
import org.hisp.dhis.rt.report.ChartElement;
import org.hisp.dhis.rt.report.Element;
import org.hisp.dhis.rt.report.OrgUnitSpesificElement;
import org.hisp.dhis.rt.report.ReportStore;
import org.hisp.dhis.rt.utils.NumberUtils;

/**
 * @author Lars Helge Overland
 * @author Nguyen Dang Quang
 * @version $Id: GenerateReportAction.java 2869 2007-02-20 14:26:09Z andegje $
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
            Set<OrganisationUnit> children = reportDataAccess.getOrganisationUnitChildren( organisationUnitId );

            Set<String> reportChildren = new TreeSet<String>();
            for ( OrganisationUnit child : children )
            {
                reportChildren.add( child.getName() );
            }
            int numberOfChild = reportChildren.size();

            Date sDate = format.parseDate( startDate );
            Date eDate = format.parseDate( endDate );

            // -----------------------------------------------------------------------
            // Retrieving report elements
            // -----------------------------------------------------------------------

            SortedMap<String, String> reportElements = new TreeMap<String, String>();

            Collection<Element> reportCollection = reportStore.getAllReportElements( report );

            if ( report.toLowerCase().trim().startsWith( "hue_bieu_" ) )
            {
                // Get value for parent level
                for ( Element reportElement : reportCollection )
                {
                    String value = new String();

                    if ( reportElement.getType().equals( ReportStore.DATAELEMENT ) )
                    {
                        double v = reportDataAccess.getAggregatedDataValue( reportElement.getElementId(), sDate, eDate,
                            String.valueOf( organisationUnitId ) );
                        if ( v == -1 )
                            value = " ";
                        else
                            value = NumberUtils.formatDataValue( v );
                    }
                    else if ( reportElement.getType().equals( ReportStore.INDICATOR ) )
                    {
                        double v = reportDataAccess.getAggregatedIndicatorValue( reportElement.getElementId(), sDate,
                            eDate, String.valueOf( organisationUnitId ) );
                        if ( v == -1 )
                            value = " ";
                        else
                            value = NumberUtils.formatDataValue( v );
                    }

                    reportElements.put( reportElement.getElementKey(), value );
                }
                // Get value for child level

                Integer elementNumber;
                Integer childNumber = new Integer( 0 );

                for ( String childName : reportChildren )
                {
                    elementNumber = new Integer( 0 );
                    for ( Element reportElement : reportCollection )
                    {
                        String value = new String();

                        if ( reportElement.getType().equals( ReportStore.DATAELEMENT ) )
                        {
                            double v = reportDataAccess.getAggregatedDataValue( reportElement.getElementId(), sDate,
                                eDate, String.valueOf( reportDataAccess.getOrganisationUnitByName( childName ) ) );
                            if ( v == -1 )
                                value = " ";
                            else
                                value = NumberUtils.formatDataValue( v );
                        }
                        else if ( reportElement.getType().equals( ReportStore.INDICATOR ) )
                        {
                            double v = reportDataAccess
                                .getAggregatedIndicatorValue( reportElement.getElementId(), sDate, eDate, String
                                    .valueOf( reportDataAccess.getOrganisationUnitByName( childName ) ) );
                            if ( v == -1 )
                                value = " ";
                            else
                                value = NumberUtils.formatDataValue( v );
                        }

                        reportElements.put( reportElement.getElementKeyForChild( "Child_" + childNumber ), value );

                        elementNumber += 1;
                    }
                    reportElements.put( "Child_" + childNumber, reportDataAccess
                        .getOrganisationUnitName( reportDataAccess.getOrganisationUnitByName( childName ) ) );
                    reportElements.put( "number_" + childNumber, String.valueOf( childNumber + 1 ) );
                    childNumber += 1;
                }
            }
            else
            {
                for ( Element reportElement : reportCollection )
                {
                    String value = new String();

                    if ( reportElement.getType().equals( ReportStore.DATAELEMENT ) )
                    {
                        value = NumberUtils.formatDataValue( reportDataAccess.getAggregatedDataValue( reportElement
                            .getElementId(), sDate, eDate, String.valueOf( organisationUnitId ) ) );
                    }
                    else if ( reportElement.getType().equals( ReportStore.INDICATOR ) )
                    {
                        value = NumberUtils.formatIndicatorValue( reportDataAccess.getAggregatedIndicatorValue(
                            reportElement.getElementId(), sDate, eDate, String.valueOf( organisationUnitId ) ) );
                    }

                    reportElements.put( reportElement.getElementKey(), value );
                }
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
            reportElements.put( "ReportName", report );
            reportElements.put( "OrganisationUnit", unitName );
            reportElements.put( "FromDate", format.formatDate( sDate ) );
            reportElements.put( "ToDate", format.formatDate( eDate ) );

            DateFormat getYear = new SimpleDateFormat( "yyyy" );
            DateFormat getMonth = new SimpleDateFormat( "MM" );
            DateFormat getDay = new SimpleDateFormat( "dd" );

            String sMonth = getMonth.format( sDate );
            String eMonth = getMonth.format( eDate );

            String periodName;
            if ( sMonth.equals( "01" ) && eMonth.equals( "03" ) )
                periodName = "Qu\u00fd 1" + "/" + getYear.format( sDate );
            else
            {
                if ( sMonth.equals( "04" ) && eMonth.equals( "06" ) )
                    periodName = "Qu\u00fd 2" + "/" + getYear.format( sDate );
                else
                {
                    if ( sMonth.equals( "07" ) && eMonth.equals( "09" ) )
                        periodName = "Qu\u00fd 3" + "/" + getYear.format( sDate );
                    else
                    {
                        if ( sMonth.equals( "10" ) && eMonth.equals( "12" ) )
                            periodName = "Qu\u00fd 4" + "/" + getYear.format( sDate );
                        else
                            periodName = format.formatDate( sDate ) + " - " + format.formatDate( eDate );
                    }
                }
            }

            reportElements.put( "Period", periodName );

            Date today = new Date();
            reportElements.put( "day", getDay.format( today ) );
            reportElements.put( "month", getMonth.format( today ) );
            reportElements.put( "year", getYear.format( today ) );

            // -----------------------------------------------------------------------
            // Generating report
            // -----------------------------------------------------------------------

            if ( preview )
            {
                String buffer;

                if ( report.toLowerCase().trim().startsWith( "hue_bieu_CoverSignature" ) )
                {

                    buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 2 );

                }
                else if ( report.toLowerCase().trim().startsWith( "hue_bieu_13" ) )
                {
                    if ( numberOfChild <= 9 )
                        buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 2 );
                    else
                    {
                        if ( numberOfChild > 9 && numberOfChild <= 19 )
                            buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 4 );
                        else
                        {
                            if ( numberOfChild > 19 && numberOfChild <= 29 )
                                buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 6 );
                            else
                                buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 8 );
                        }
                    }
                }
                else if ( report.toLowerCase().trim().startsWith( "hue_bieu_14" ) )
                {
                    if ( numberOfChild <= 9 )
                        buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 3 );
                    else
                    {
                        if ( numberOfChild > 9 && numberOfChild <= 19 )
                            buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 6 );
                        else
                        {
                            if ( numberOfChild > 19 && numberOfChild <= 29 )
                                buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 9 );
                            else
                                buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 12 );
                        }
                    }
                }
                else if ( report.toLowerCase().trim().startsWith( "hue_bieu_" ) )
                {
                    if ( numberOfChild <= 9 )
                        buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 1 );
                    else
                    {
                        if ( numberOfChild > 9 && numberOfChild <= 19 )
                            buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 2 );
                        else
                        {
                            if ( numberOfChild > 19 && numberOfChild <= 29 )
                                buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 3 );
                            else
                                buffer = ReportGenerator.generateHCMCReportPreview( report, reportElements, 4 );
                        }
                    }
                }
                else
                    buffer = ReportGenerator.generateReportPreview( report, reportElements, chartElements );

                setSessionVar( HTML_BUFFER, buffer );
            }
            else
            {
                fileName = report + "-" + unitName + "-" + periodName + ".pdf";

                if ( report.toLowerCase().trim().startsWith( "hue_bieu_0" ) )
                {
                    inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 2 );

                }
                else if ( report.toLowerCase().trim().startsWith( "hue_bieu_13" ) )
                {
                    if ( numberOfChild <= 9 )
                        inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 2 );
                    else
                    {
                        if ( numberOfChild > 9 && numberOfChild <= 19 )
                            inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 4 );
                        else
                        {
                            if ( numberOfChild > 19 && numberOfChild <= 29 )
                                inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 6 );
                            else
                                inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 8 );
                        }
                    }
                }
                else if ( report.toLowerCase().trim().startsWith( "hue_bieu_14" ) )
                {
                    if ( numberOfChild <= 9 )
                        inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 3 );
                    else
                    {
                        if ( numberOfChild > 9 && numberOfChild <= 19 )
                            inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 6 );
                        else
                        {
                            if ( numberOfChild > 19 && numberOfChild <= 29 )
                                inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 9 );
                            else
                                inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 12 );
                        }
                    }
                }
                else if ( report.toLowerCase().trim().startsWith( "hue_bieu_" ) )
                {
                    if ( numberOfChild <= 9 )
                        inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 1 );
                    else
                    {
                        if ( numberOfChild > 9 && numberOfChild <= 19 )
                            inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 2 );
                        else
                        {
                            if ( numberOfChild > 19 && numberOfChild <= 29 )
                                inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 3 );
                            else
                                inputStream = ReportGenerator.generateHCMCReportPdfFile( report, reportElements, 4 );
                        }
                    }
                }
                else
                {
                    inputStream = ReportGenerator.generateReportStream( report, reportElements, chartElements );
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
}
