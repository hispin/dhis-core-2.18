package org.hisp.dhis.reporting.dataset.action;

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.order.manager.DataElementOrderManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reporting.dataset.generators.DesignGenerator;
import org.hisp.dhis.reporting.dataset.generators.ReportGenerator;
import org.hisp.dhis.reporting.dataset.generators.TabularDesignGenerator;
import org.hisp.dhis.reporting.dataset.generators.TabularReportGenerator;
import org.hisp.dhis.reporting.dataset.report.Element;
import org.hisp.dhis.reporting.dataset.report.ReportStore;
import org.hisp.dhis.reporting.dataset.state.SelectedStateManager;
import org.hisp.dhis.reporting.dataset.utils.NumberUtils;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class GenerateDefaultDataSetReportAction
    extends AbstractAction
{
    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private Collection<DataElementCategory> orderedCategories = new ArrayList<DataElementCategory>();

    private Map<Integer, Integer> catColSize = new HashMap<Integer, Integer>();

    private boolean preview;

    public void setPreview( boolean preview )
    {
        this.preview = preview;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private int reportType;    

    public int getReportType()
    {
        return reportType;
    }

    public void setReportType( int reportType )
    {
        this.reportType = reportType;
    }
    
    private String selectedUnitOnly;

    public String getSelectedUnitOnly()
    {
        return selectedUnitOnly;
    }   
    
    public void setSelectedUnitOnly( String selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }
    
    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    public void setInputStream( InputStream inputStream )
    {
        this.inputStream = inputStream;
    }   

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DesignGenerator designGenerator;

    public void setDesignGenerator( DesignGenerator designGenerator )
    {
        this.designGenerator = designGenerator;
    }

    private TabularDesignGenerator tabularDesignGenerator;

    public void setTabularDesignGenerator( TabularDesignGenerator tabularDesignGenerator )
    {
        this.tabularDesignGenerator = tabularDesignGenerator;
    }

    private ReportGenerator reportGenerator;

    public void setReportGenerator( ReportGenerator reportGenerator )
    {
        this.reportGenerator = reportGenerator;
    }

    private TabularReportGenerator tabularReportGenerator;

    public void setTabularReportGenerator( TabularReportGenerator tabularReportGenerator )
    {
        this.tabularReportGenerator = tabularReportGenerator;
    }    

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementOrderManager dataElementOrderManager;

    public void setDataElementOrderManager( DataElementOrderManager dataElementOrderManager )
    {
        this.dataElementOrderManager = dataElementOrderManager;
    }

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService( DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }    

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }  
    
    private DataMartStore dataMartStore;
    
    public void setDataMartStore( DataMartStore dataMartStore )
    {
    	this.dataMartStore = dataMartStore;
    }
    
    private DataValueService dataValueService;
    
    public void setDataValueService( DataValueService dataValueService)
    {
    	this.dataValueService = dataValueService;
    }    

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {        
        DataSet dataSet = selectedStateManager.getSelectedDataSet();    
        
        OrganisationUnit orgUnit = selectedStateManager.getSelectedOrganisationUnit();       

        Period period = selectedStateManager.getSelectedPeriod();       

        if ( orgUnit != null && dataSet != null && period != null )
        {
            int designTemplate = 2;

            int chartTemplate = 0;

            int numberOfColumns = 1;

            boolean isTabular = false;
            
            String dataSetReportName = dataSet.getName();

            Collection<DataElement> dataElements = dataElementOrderManager.getOrderedDataElements( dataSet );

            //CollectionUtils.filter( dataElements, new AggregateableDataElementPredicate() );           
            
            // -----------------------------------------------------------------
            // Adding new report based on the selected data set
            // -----------------------------------------------------------------                       

            if ( !reportStore.isXMLReportExists( dataSetReportName ) )
            {             	            	            	
                reportStore.addReport( dataSetReportName, ReportStore.GENERIC );
            }

            List<String> textFieldNames = new ArrayList<String>();

            List<String> staticTextNames = new ArrayList<String>();

            SortedMap<String, String> reportElements = new TreeMap<String, String>();

            Collection<String> collectedDataElements = new ArrayList<String>();
            Collection<String> collectedOptionCombos = new ArrayList<String>();

            SortedMap<String, Collection<String>> complexReportElements = new TreeMap<String, Collection<String>>();

            Collection<String> tableHeaders = new ArrayList<String>();
            Collection<String> tableColumns = new ArrayList<String>();

            int rowCount = 1;

            if ( dataElements.iterator().next().getCategoryCombo().getOptionCombos().size() > 1 )
            {
                isTabular = true;

                DataElementCategoryCombo catCombo = dataElements.iterator().next().getCategoryCombo();

                for ( DataElementCategory category : catCombo.getCategories() )
                {
                    for ( DataElementCategoryOption option : category.getCategoryOptions() )
                    {
                        tableHeaders.add( option.getName() );
                        reportElements.put( option.getName(), option.getName() );
                    }
                }

                for ( DataElement dataElement : dataElements )
                {
                    int colCount = 0;

                    Collection<DataElementCategoryOptionCombo> optionCombos = dataElementCategoryOptionComboService
                        .sortOptionCombos( catCombo );

                    collectedDataElements.add( dataElement.getName() );

                    numberOfColumns = optionCombos.size();

                    isTabular = true;

                    for ( DataElementCategoryOptionCombo optionComb : optionCombos )
                    {
                        collectedOptionCombos.add( Integer.toString( optionComb.getId() ) );
                    }

                    for ( DataElementCategoryOptionCombo optionCombo : optionCombos )
                    {
                        String value;
                        DataValue dataValue;

                        if ( dataElement.getType().equals( DataElement.TYPE_INT ) )
                        {
                            double aggregatedValue;
                        	
                            if ( selectedUnitOnly != null )
                            {                            		
                                dataValue = dataValueService.getDataValue(orgUnit, dataElement, period, optionCombo);
                                
                                value = ( dataValue != null ) ? dataValue.getValue() : "";                              		
                            }
                            else
                            {                            		
                                aggregatedValue = dataMartStore.getAggregatedValue( dataElement, optionCombo, period, orgUnit );		
                        		
                                value = ( aggregatedValue != DataMartStore.NO_VALUES_REGISTERED ) ? NumberUtils.formatDataValue( aggregatedValue ) : "";                    		                    		
                            }         
                        }
                        else
                        {                        	
                        	if ( selectedUnitOnly != null )
                        	{                        		
                        	    dataValue = dataValueService.getDataValue(orgUnit, dataElement, period, optionCombo);                        		
                        	    value = ( dataValue != null ) ? dataValue.getValue() : "";
                        	}
                        	else
                        	{
                        		value = " ";
                        	}                    	
                            
                        }

                        Collection<String> cellValues = new ArrayList<String>();

                        if ( complexReportElements.containsKey( "col" + colCount ) )
                        {
                            cellValues = complexReportElements.get( "col" + colCount );
                        }

                        cellValues.add( value );
                        complexReportElements.put( "col" + colCount, cellValues );
                        colCount++;
                    }

                    rowCount++;
                }

                complexReportElements.put( "tabularColumn", collectedOptionCombos );
                complexReportElements.put( "tabularRow", collectedDataElements );
            }
            else
            {            	
            	
                for ( DataElement element : dataElements )
                {                	
                    reportStore.addReportElement( dataSetReportName, ReportStore.DATAELEMENT, element.getId() );                    
                }

                Collection<Element> reportCollection = reportStore.getAllReportElements( dataSetReportName );                

                for ( Element element : reportCollection )
                {
                    textFieldNames.add( element.getElementKey() );

                    staticTextNames.add( element.getElementName() );
                }

                if ( !reportStore.isJRXMLReportExists( dataSetReportName ) )
                {
                    designGenerator.generateDesign( dataSetReportName, textFieldNames, staticTextNames,
                        designTemplate, chartTemplate );
                }

                for ( Element reportElement : reportCollection )
                {
                    String value;
                    DataValue dataValue;

                    if ( dataElementService.getDataElement( reportElement.getElementId() ).getType().equals(
                        DataElement.TYPE_INT ) )
                    {
                    	double aggregatedValue;
                    	
                    	if( selectedUnitOnly != null )
                    	{                        		
                    	    dataValue = dataValueService.getDataValue(orgUnit, 
                    	    dataElementService.getDataElement( reportElement.getElementId()), period,
                    	        dataElements.iterator().next().getCategoryCombo().getOptionCombos().iterator().next());
                    		
                    	    value = ( dataValue != null ) ? dataValue.getValue() : "";
                    	}
                    	else
                    	{                           		
                    	    aggregatedValue = dataMartStore.getAggregatedValue(
                    	    dataElementService.getDataElement(reportElement.getElementId()), 
                    	    dataElements.iterator().next().getCategoryCombo().getOptionCombos().iterator().next(), period, orgUnit);                   		
                    		
                    	    value = ( aggregatedValue != DataMartStore.NO_VALUES_REGISTERED ) ? NumberUtils.formatDataValue( aggregatedValue ) : "";
                    	}                       	                    
                    }

                    else                    	
                    {                   	
                    	if ( selectedUnitOnly != null )
                    	{                        		
                    		dataValue = dataValueService.getDataValue(orgUnit, 
                            	    dataElementService.getDataElement( reportElement.getElementId()), period,
                            	        dataElements.iterator().next().getCategoryCombo().getOptionCombos().iterator().next());
                            		
                            	    value = ( dataValue != null ) ? dataValue.getValue() : "";
                    	}
                    	else
                    	{
                    		value = " ";
                    	}                
                    }

                    reportElements.put( reportElement.getElementKey(), value );
                }
            }

            // -----------------------------------------------------------------
            // Adding report information
            // -----------------------------------------------------------------         

            reportElements.put( "ReportName", dataSetReportName );
            reportElements.put( "OrganisationUnit", orgUnit.getName() );
            reportElements.put( "Period", format.formatPeriod( period ) );

            // -----------------------------------------------------------------
            // Generating report
            // -----------------------------------------------------------------

            if ( preview )
            {
                String buffer;

                if ( isTabular )
                {
                    if ( !reportStore.isXMLReportExists( dataSetReportName ) )
                    {
                        reportStore.deleteReport( dataSetReportName );

                        reportStore.addReport( dataSetReportName, ReportStore.GENERIC );
                    }

                    if ( !reportStore.isJRXMLReportExists( dataSetReportName ) )
                    {
                        for ( int i = 0; i < numberOfColumns; i++ )
                        {
                            tableColumns.add( "col" + i );
                        }

                        Map<Integer, Collection<DataElementCategoryOption>> orderedOptionsMap = getHeadingLayout( dataSet );

                        tabularDesignGenerator.generateDesign( dataSetReportName, tableHeaders, tableColumns,
                            orderedCategories, numberOfColumns, orderedOptionsMap );

                    }

                    buffer = tabularReportGenerator.generateReportPreview( dataSetReportName, reportElements,
                        complexReportElements );
                }
                else
                {
                    buffer = reportGenerator.generateReportPreview( dataSetReportName, reportElements, null );
                }

                setSessionVar( HTML_BUFFER, buffer );
                setSessionVar( REPORT_TYPE, ReportStore.ORGUNIT_SPECIFIC );
                setSessionVar( REPORT, report );
                setSessionVar( START_DATE, startDate );
                setSessionVar( END_DATE, endDate );
            }
            else
            {

                fileName = report + "-" + format.formatPeriod( period ) + ".pdf";

                if ( isTabular )
                {
                    if ( !reportStore.isXMLReportExists( dataSetReportName ) )
                    {
                        reportStore.deleteReport( dataSetReportName );

                        reportStore.addReport( dataSetReportName, ReportStore.GENERIC );
                    }

                    if ( !reportStore.isJRXMLReportExists( dataSetReportName ) )
                    {
                        for ( int i = 0; i < numberOfColumns; i++ )
                        {
                            tableColumns.add( "col" + i );
                        }

                        Map<Integer, Collection<DataElementCategoryOption>> orderedOptionsMap = getHeadingLayout( dataSet );

                        tabularDesignGenerator.generateDesign( dataSetReportName, tableHeaders, tableColumns,
                            orderedCategories, numberOfColumns, orderedOptionsMap );
                    }

                    inputStream = tabularReportGenerator.generateReportStream( dataSetReportName, reportElements,
                        complexReportElements );
                }
                else
                {
                    inputStream = reportGenerator.generateReportStream( dataSetReportName, reportElements, null );
                }
            }

            return SUCCESS;
        }

        return ERROR;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    Map<Integer, Collection<DataElementCategoryOption>> getHeadingLayout( DataSet dataSet )
    {
        Map<Integer, Collection<DataElementCategoryOption>> orderedOptionsMap = new HashMap<Integer, Collection<DataElementCategoryOption>>();

        Collection<DataElement> dataElements = dataSet.getDataElements();

        DataElement sampleDataElement = dataElements.iterator().next();

        DataElementCategoryCombo catCombo = sampleDataElement.getCategoryCombo();

        orderedCategories = catCombo.getCategories();

        // ---------------------------------------------------------------------
        // Calculating the number of times each category is supposed to be
        // repeated in the dataentry form
        // ---------------------------------------------------------------------

        Map<Integer, Integer> catRepeat = new HashMap<Integer, Integer>();

        int catColSpan = catCombo.getOptionCombos().size();

        for ( DataElementCategory cat : orderedCategories )
        {

            catColSpan = catColSpan / cat.getCategoryOptions().size();

            catColSize.put( cat.getId(), catColSpan );

            catRepeat.put( cat.getId(), catCombo.getOptionCombos().size() / (catColSpan * cat.getCategoryOptions().size()) );

        }

        // ---------------------------------------------------------------------
        // Get the order of options
        // ---------------------------------------------------------------------

        for ( DataElementCategory dec : orderedCategories )
        {
            Collection<DataElementCategoryOption> options = dec.getCategoryOptions();

            Collection<DataElementCategoryOption> allOptions = new ArrayList<DataElementCategoryOption>();

            for ( int i = 1; i <= catRepeat.get( dec.getId() ); i++ )
            {
                allOptions.addAll( options );
            }

            orderedOptionsMap.put( dec.getId(), allOptions );
        }

        return orderedOptionsMap;
    }
}
