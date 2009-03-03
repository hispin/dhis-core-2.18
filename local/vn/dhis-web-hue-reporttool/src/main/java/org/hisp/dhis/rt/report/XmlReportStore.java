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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.hisp.dhis.rt.dataaccess.ReportDataAccess;
import org.hisp.dhis.rt.dataaccess.ReportDataAccessException;
import org.hisp.dhis.rt.utils.FileUtils;
import org.hisp.dhis.rt.utils.JRXmlFilter;
import org.hisp.dhis.rt.utils.XmlFilter;
import com.thoughtworks.xstream.XStream;

/**
 * @author Lars Helge Overland
 * @version $Id: XmlReportStore.java 2871 2007-02-20 16:04:11Z andegje $
 */
public class XmlReportStore
    implements ReportStore
{
    // -----------------------------------------------------------------------
    // Options
    // -----------------------------------------------------------------------
    
    private static final String CHARSET = "UTF-8";
    
    private static final String NAME = "name";
    
    private static final String SHORT_NAME = "shortname";
    
    private String reportDisplayProperty;    

    public void setReportDisplayProperty( String reportDisplayProperty )
    {
        this.reportDisplayProperty = reportDisplayProperty;
    }
    
    // -----------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------
    
    private ReportDataAccess reportDataAccess;

    public void setReportDataAccess( ReportDataAccess reportDataAccess )
    {
        this.reportDataAccess = reportDataAccess;
    }
        
    // -----------------------------------------------------------------------
    // Report
    // -----------------------------------------------------------------------
    
    public void addReport( String name, int reportType )
        throws ReportStoreException
    {
        try
        {
            String filePath = FileUtils.getXmlFileName( name );
            
            File file = new File( filePath );
            
            file.createNewFile();
            
            Report report = new Report( reportType );
            
            XStream xStream = new XStream();
            
            Writer out = new OutputStreamWriter( new FileOutputStream( file ), CHARSET );
                        
            xStream.toXML( report, out );
            
            out.close();            
        }
        catch ( IOException ex )
        {
            throw new ReportStoreException( "Failed to add report", ex );
        }
    }
    
    private void updateReport( String name, Report report )
        throws ReportStoreException
    {
        try
        {
            String filePath = FileUtils.getXmlFileName( name );
            
            File file = new File( filePath );
            
            file.createNewFile();
            
            XStream xStream = new XStream();
            
            Writer out = new OutputStreamWriter( new FileOutputStream( file ), CHARSET );
                        
            xStream.toXML( report, out ); 
            
            out.close();
        }
        catch ( IOException ex )
        {
            throw new ReportStoreException( "Failed to add report", ex );
        }
    }
    
    private Report getReport( String name )
        throws ReportStoreException
    {
        try
        {
            XStream xStream = new XStream();
            
            String filePath = FileUtils.getXmlFileName( name );
            
            File file = new File( filePath );
            
            Reader in = new InputStreamReader( new FileInputStream( file ), CHARSET );
                
            Report report = (Report) xStream.fromXML( in );
            
            in.close();
            
            return report;
        }
        catch ( FileNotFoundException ex )
        {
            throw new ReportStoreException( "File not found, failed to get report", ex );
        }
        catch ( IOException ex )
        {
            throw new ReportStoreException( "Failed to close inputstream", ex );
        }
    }
    
    public int getReportType( String reportName )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        return report.getReportType();
    }
    
    public boolean deleteReport( String name )
    {
        String xmlFilePath = FileUtils.getXmlFileName( name );
        String jrXmlFilePath = FileUtils.getJRXmlFileName( name );
        
        File xmlFile = new File( xmlFilePath );
        File jrXmlFile = new File( jrXmlFilePath );
        
        jrXmlFile.delete();
        
        return xmlFile.delete();
    }
    
    public Collection<String> getAllReports()
    {
        String dirPath = FileUtils.getOutputPath().get( "xml" ).toString();
        
        File dir = new File( dirPath );
        
        File[] files = dir.listFiles( new XmlFilter() );
        
        List<String> list = new ArrayList<String>();
        
        for ( int i = 0; i < files.length; i++ )
        {
            list.add( FileUtils.getBaseName( files[ i ] ) );            
        }
        
        return list;
    }
    
    public Collection<String> getAllDesigns()
    {
        String dirPath = FileUtils.getOutputPath().get( "jrxml" ).toString();
        
        File dir = new File( dirPath );
        
        File[] files = dir.listFiles( new JRXmlFilter() );
        
        List<String> list = new ArrayList<String>();
        
        for ( int i = 0; i < files.length; i++ )
        {
            list.add( FileUtils.getBaseName( files[ i ] ) );            
        }
        
        return list;
    }

    public Collection<String> getDesignsByType( int reportType )
        throws ReportStoreException
    {
        Collection<String> reportNames = getAllReports();
        
        List<String> reportNamesByType = new ArrayList<String>();
        
        for ( String reportName : reportNames )
        {
            Report report = getReport( reportName );
            
            if ( report.getReportType() == reportType )
            {
                reportNamesByType.add( reportName );
            }
        }
        
        Collection<String> designNames = getAllDesigns();
        
        List<String> designNamesByType = new ArrayList<String>();
        
        for ( String designName : designNames )
        {
            if ( reportNamesByType.contains( designName ) )
            {
                designNamesByType.add( designName );
            }
        }
        
        return designNamesByType;
    }
    
    // -----------------------------------------------------------------------
    // ReportElement
    // -----------------------------------------------------------------------
    
    public void addReportElement( String reportName, String type, int elementId )
        throws ReportStoreException
    {
        try
        {
            Report report = getReport( reportName );
            
            List<Element> reportElements = report.getReportElements();
            
            String elementName = new String();
            
            if ( type.equals( ReportStore.DATAELEMENT ) )
            {
                if ( reportDisplayProperty.equals( NAME ) )
                {
                    elementName = reportDataAccess.getDataElementName( elementId );
                }
                else if ( reportDisplayProperty.equals( SHORT_NAME ) )
                {
                    elementName = reportDataAccess.getDataElementShortName( elementId );
                }
            }
            else if ( type.equals( ReportStore.INDICATOR ) )
            {
                if ( reportDisplayProperty.equals( NAME ) )
                {
                    elementName = reportDataAccess.getIndicatorName( elementId );
                }
                else if ( reportDisplayProperty.equals( SHORT_NAME ) )
                {
                    elementName = reportDataAccess.getIndicatorShortName( elementId );
                }
            }
            
            Element element = new Element( type, elementId, elementName );
            
            reportElements.add( element );
            
            updateReport( reportName, report );
        }
        catch ( ReportDataAccessException ex )
        {
            throw new ReportStoreException( "Failed to retrieve data", ex );
        }
    }
    
    public void addReportElement( String reportName, String type, int elementId, int organisationUnitId )
        throws ReportStoreException
    {
        try
        {
            Report report = getReport( reportName );
            
            List<Element> reportElements = report.getReportElements();
            
            String elementName = new String();
            
            if ( type.equals( ReportStore.DATAELEMENT ) )
            {
                if ( reportDisplayProperty.equals( NAME ) )
                {
                    elementName = reportDataAccess.getDataElementName( elementId );
                }
                else if ( reportDisplayProperty.equals( SHORT_NAME ) )
                {
                    elementName = reportDataAccess.getDataElementShortName( elementId );
                }
            }
            else if ( type.equals( ReportStore.INDICATOR ) )
            {
                if ( reportDisplayProperty.equals( NAME ) )
                {
                    elementName = reportDataAccess.getIndicatorName( elementId );
                }
                else if ( reportDisplayProperty.equals( SHORT_NAME ) )
                {
                    elementName = reportDataAccess.getIndicatorShortName( elementId );
                }
            }
            
            String organisationUnitName = reportDataAccess.getOrganisationUnitShortName( organisationUnitId );
            
            OrgUnitSpesificElement element = new OrgUnitSpesificElement( type, elementId, elementName, 
                organisationUnitId, organisationUnitName );
            
            reportElements.add( element );
            
            updateReport( reportName, report );
        }
        catch ( ReportDataAccessException ex )
        {
            throw new ReportStoreException( "Failed to retrieve data", ex );
        }
    }
    
    public void removeReportElement( String reportName, String id )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        List<Element> reportElements = report.getReportElements();
        
        Element element = getReportElement( reportName, id );
        
        reportElements.remove( element );
        
        updateReport( reportName, report );
    }
    
    private Element getReportElement( String reportName, String id )
        throws ReportStoreException
    {
        Collection reportElements = getAllReportElements( reportName );
        
        Iterator iterator = reportElements.iterator();
        
        while ( iterator.hasNext() )
        {
            Element element = (Element) iterator.next();
            
            if ( element.getId().equals( id ) )
            {
                return element;
            }
        }
        
        return null;
    }
    
    public void moveUpReportElement( String reportName, String id )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        List<Element> reportElements = report.getReportElements();
        
        Element element = getReportElement( reportName, id );
        
        int index = reportElements.indexOf( element );
        
        if ( index > 0 )
        {
            Element tempElement = reportElements.get( index - 1 );
            
            reportElements.set( index - 1, element );
            
            reportElements.set( index, tempElement );
        }
        
        updateReport( reportName, report );
    }
    
    public void moveDownReportElement( String reportName, String id )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        List<Element> reportElements = report.getReportElements();
        
        Element element = getReportElement( reportName, id );
        
        int index = reportElements.indexOf( element );
        
        if ( index < reportElements.size() - 1 )
        {
            Element tempElement = reportElements.get( index + 1 );
            
            reportElements.set( index + 1, element );
            
            reportElements.set( index, tempElement );
        }
        
        updateReport( reportName, report );
    }
    
    public Collection<Element> getAllReportElements( String reportName )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        return report.getReportElements();
    }

    // -----------------------------------------------------------------------
    // ChartElement
    // -----------------------------------------------------------------------
    
    public void addChartElement( String reportName, String type, int elementId )
        throws ReportStoreException
    {
        try
        {
            Report report = getReport( reportName );
            
            List<Element> chartElements = report.getChartElements();
            
            String elementName = new String();
            
            if ( type.equals( ReportStore.DATAELEMENT ) )
            {
                if ( reportDisplayProperty.equals( NAME ) )
                {
                    elementName = reportDataAccess.getDataElementName( elementId );
                }
                else if ( reportDisplayProperty.equals( SHORT_NAME ) )
                {
                    elementName = reportDataAccess.getDataElementShortName( elementId );
                }
            }
            else if ( type.equals( ReportStore.INDICATOR ) )
            {
                if ( reportDisplayProperty.equals( NAME ) )
                {
                    elementName = reportDataAccess.getIndicatorName( elementId );
                }
                else if ( reportDisplayProperty.equals( SHORT_NAME ) )
                {
                    elementName = reportDataAccess.getIndicatorShortName( elementId );
                }
            }
            
            Element element = new Element( type, elementId, elementName );
                        
            chartElements.add( element );
            
            updateReport( reportName, report );
        }
        catch ( ReportDataAccessException ex )
        {
            throw new ReportStoreException( "Failed to retrieve data", ex );
        }
    }
    
    public void addChartElement( String reportName, String type, int elementId, int organisationUnitId )
        throws ReportStoreException
    {
        try
        {
            Report report = getReport( reportName );
            
            List<Element> chartElements = report.getChartElements();
            
            String elementName = new String();
            
            if ( type.equals( ReportStore.DATAELEMENT ) )
            {
                if ( reportDisplayProperty.equals( NAME ) )
                {
                    elementName = reportDataAccess.getDataElementName( elementId );
                }
                else if ( reportDisplayProperty.equals( SHORT_NAME ) )
                {
                    elementName = reportDataAccess.getDataElementShortName( elementId );
                }
            }
            else if ( type.equals( ReportStore.INDICATOR ) )
            {
                if ( reportDisplayProperty.equals( NAME ) )
                {
                    elementName = reportDataAccess.getIndicatorName( elementId );
                }
                else if ( reportDisplayProperty.equals( SHORT_NAME ) )
                {
                    elementName = reportDataAccess.getIndicatorShortName( elementId );
                }
            }
            
            String organisationUnitName = reportDataAccess.getOrganisationUnitShortName( organisationUnitId );
            
            OrgUnitSpesificElement element = new OrgUnitSpesificElement( type, elementId, elementName,
                organisationUnitId, organisationUnitName );
                        
            chartElements.add( element );
            
            updateReport( reportName, report );
        }
        catch ( ReportDataAccessException ex )
        {
            throw new ReportStoreException( "Failed to retrieve data", ex );
        }
    }
    
    public void removeChartElement( String reportName, String id )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        List chartElements = report.getChartElements();
        
        Iterator iterator = chartElements.iterator();
        
        while ( iterator.hasNext() )
        {
            Element element = (Element) iterator.next();
            
            if ( element.getId().equals( id ) )
            {
                iterator.remove();
            }
        }
        
        updateReport( reportName, report );
    }
    
    public Collection<Element> getAllChartElements( String reportName )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        return report.getChartElements();
    }
        
    // -----------------------------------------------------------------------
    // Design Template
    // -----------------------------------------------------------------------
    
    public void setDesignTemplate( String reportName, int number )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        report.setDesignTemplate( number );
        
        updateReport( reportName, report );
    }
    
    public int getDesignTemplate( String reportName )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        return report.getDesignTemplate();
    }

    // -----------------------------------------------------------------------
    // Chart Template
    // -----------------------------------------------------------------------
    
    public void setChartTemplate( String reportName, int number )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        report.setChartTemplate( number );
        
        updateReport( reportName, report );
    }
    
    public int getChartTemplate( String reportName )
        throws ReportStoreException
    {
        Report report = getReport( reportName );
        
        return report.getChartTemplate();
    }

}


