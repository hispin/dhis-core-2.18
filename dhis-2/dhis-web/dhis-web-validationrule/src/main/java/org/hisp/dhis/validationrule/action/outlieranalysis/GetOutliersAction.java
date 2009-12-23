package org.hisp.dhis.validationrule.action.outlieranalysis;

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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataanalysis.DataAnalysisService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.ConversionUtils;

import com.opensymphony.xwork2.Action;

/**
 * Finds outliers in given data elements for given sources in a given period and
 * displays a list of them.
 * 
 * @author Jon Moen Drange, Peter Flem, Dag Haavi Finstad
 * @version $Id: GetOutliersAction.java 1005 2009-06-04 13:29:44Z jonmd $
 */
public class GetOutliersAction
    implements Action
{
    private static final String TYPE_MINMAX = "minmax";
    private static final String TYPE_STDDEV = "stddev";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataAnalysisService minMaxOutlierAnalysisService;

    public void setMinMaxOutlierAnalysisService( DataAnalysisService minMaxOutlierAnalysisService )
    {
        this.minMaxOutlierAnalysisService = minMaxOutlierAnalysisService;
    }

    private DataAnalysisService stdDevOutlierAnalysisService;

    public void setStdDevOutlierAnalysisService( DataAnalysisService stdDevOutlierAnalysisService )
    {
        this.stdDevOutlierAnalysisService = stdDevOutlierAnalysisService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String toDateString;

    public void setToDate( String toDate )
    {
        this.toDateString = toDate.trim();
    }

    private String fromDateString;

    public void setFromDate( String fromDate )
    {
        this.fromDateString = fromDate.trim();
    }

    private String dataSetId;

    public void setDataset( String dataSet )
    {
        this.dataSetId = dataSet;
    }

    private List<String> dataElementsById;

    public void setDataElementsById( List<String> dataElementsById )
    {
        this.dataElementsById = dataElementsById;
    }

    private String outlierType;

    public void setOutlierType( String outlierType )
    {
        this.outlierType = outlierType;
    }

    private Double standardDeviation;

    public void setStandardDeviation( Double standardDeviation )
    {
        this.standardDeviation = standardDeviation;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private Collection<DeflatedDataValue> dataValues;

    public Collection<DeflatedDataValue> getDataValues()
    {
        return dataValues;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Collection<DataElement> dataElements = dataElementService.getDataElements( ConversionUtils.getIntegerCollection( dataElementsById ) );

        OrganisationUnit organisationUnit = selectionTreeManager.getSelectedOrganisationUnit();
        
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        
        Date fromDate = null;
        Date toDate = null;

        if ( fromDateString == null || fromDateString.trim().length() == 0 )
        {
            Date epoch = new Date( 0 );
            fromDate = epoch;
        }
        else
        {
            fromDate = format.parseDate( fromDateString );
        }

        if ( toDateString == null || toDateString.trim().length() == 0 )
        {
            toDate = new Date();
        }
        else
        {
            toDate = format.parseDate( toDateString );
        }

        Collection<Period> periods = periodService.getPeriodsBetweenDates( dataSet.getPeriodType(), fromDate, toDate );

        if ( outlierType.equals( TYPE_MINMAX ) )
        {
            dataValues = minMaxOutlierAnalysisService.analyse( organisationUnit, dataElements, periods, null );
        }
        else if ( outlierType.equals( TYPE_STDDEV ) )
        {
            dataValues = stdDevOutlierAnalysisService.analyse( organisationUnit, dataElements, periods, standardDeviation );
        }
        else
        {
            return ERROR;
        }

        return SUCCESS;
    }
}
