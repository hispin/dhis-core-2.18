package org.hisp.dhis.rt.report;

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

import java.util.Collection;

/**
 * @author Lars Helge Overland
 * @version $Id: ReportStore.java 2871 2007-02-20 16:04:11Z andegje $
 */
public interface ReportStore
{
    String ID = ReportStore.class.getName();
    
    // -----------------------------------------------------------------------
    // Report Type
    // -----------------------------------------------------------------------
    
    final static int GENERIC = 0;
    
    final static int ORGUNIT_SPESIFIC = 1;
    
    // -----------------------------------------------------------------------
    // Report Element Type
    // -----------------------------------------------------------------------
    
    final static String DATAELEMENT = "DataElement";
    
    final static String INDICATOR = "Indicator";   

    // -----------------------------------------------------------------------
    // Report
    // -----------------------------------------------------------------------
    
    void addReport( String name, int reportType )
        throws ReportStoreException;
    
    boolean deleteReport( String name );
    
    int getReportType( String reportName )
        throws ReportStoreException;
        
    Collection<String> getAllReports();

    Collection<String> getAllDesigns();
    
    Collection<String> getDesignsByType( int reportType )
    throws ReportStoreException;
    
    // -----------------------------------------------------------------------
    // Report Element
    // -----------------------------------------------------------------------
    
    void addReportElement( String reportName, String type, int elementId )
        throws ReportStoreException;
    
    void addReportElement( String reportName, String type, int elementId, int organisationUnitId )
        throws ReportStoreException;
    
    void removeReportElement( String reportName, String id )
        throws ReportStoreException;
    
    Collection<Element> getAllReportElements( String reportName )
        throws ReportStoreException;
    
    void moveUpReportElement( String reportName, String id )
        throws ReportStoreException;
    
    void moveDownReportElement( String reportName, String id )
        throws ReportStoreException;

    // -----------------------------------------------------------------------
    // Chart Element
    // -----------------------------------------------------------------------

    void addChartElement( String reportName, String type, int elementId )
        throws ReportStoreException;
    
    void addChartElement( String reportName, String type, int elementId, int organisationUnitId )
        throws ReportStoreException;
    
    void removeChartElement( String reportName, String id )
        throws ReportStoreException;
    
    Collection<Element> getAllChartElements( String reportName )
        throws ReportStoreException;
    
    // -----------------------------------------------------------------------
    // Design Template
    // -----------------------------------------------------------------------
        
    void setDesignTemplate( String reportName, int number )
        throws ReportStoreException;
    
    int getDesignTemplate( String reportName )
        throws ReportStoreException;

    // -----------------------------------------------------------------------
    // Chart Template
    // -----------------------------------------------------------------------
    
    void setChartTemplate( String reportName, int number )
        throws ReportStoreException;
    
    int getChartTemplate( String reportName )
        throws ReportStoreException;
}
