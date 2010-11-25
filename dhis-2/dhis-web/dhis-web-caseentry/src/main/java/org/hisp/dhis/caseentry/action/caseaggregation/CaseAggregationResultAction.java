/*
 * Copyright (c) 2004-2010, University of Oslo
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

package org.hisp.dhis.caseentry.action.caseaggregation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitShortNameComparator;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class CaseAggregationResultAction
    implements Action
{

    Log log = LogFactory.getLog( getClass() );

    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
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

    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // ---------------------------------------------------------------
    // Input & Output Parameters
    // ---------------------------------------------------------------

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private String selectedDataSets;

    public void setSelectedDataSets( String selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    private String resultMessage;

    public String getResultMessage()
    {
        return resultMessage;
    }

    private DataSet selDataSet;

    private OrganisationUnit selOrgUnit;

    private List<OrganisationUnit> orgUnitList;

    private List<DataElement> dataElementList;

    private List<Period> periodList;

    private String storedBy;

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute()
        throws Exception
    {
        storedBy = currentUserService.getCurrentUsername() + "_CAE";

        resultMessage = "";

        // DataSet and DataElement
        selDataSet = dataSetService.getDataSet( Integer.parseInt( selectedDataSets ) );
        dataElementList = new ArrayList<DataElement>( selDataSet.getDataElements() );

        selOrgUnit = selectionTreeManager.getReloadedSelectedOrganisationUnit();

        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            orgUnitList = getChildOrgUnitTree( selOrgUnit );
        }
        else if ( facilityLB.equals( "immChildren" ) )
        {
            orgUnitList.add( selOrgUnit );
            List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( selOrgUnit.getChildren() );
            Collections.sort( organisationUnits, new OrganisationUnitShortNameComparator() );
            orgUnitList.addAll( organisationUnits );
        }
        else
        {
            orgUnitList.add( selOrgUnit );
        }

        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        PeriodType dataSetPeriodType = selDataSet.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( dataSetPeriodType,
            startPeriod.getStartDate(), endPeriod.getEndDate() ) );

        // Orgunit Iteration for Aggregation

        for ( OrganisationUnit orgUnit : orgUnitList )
        {

            for ( DataElement dElement : dataElementList )
            {
                List<DataElementCategoryOptionCombo> deCOCList = new ArrayList<DataElementCategoryOptionCombo>(
                    dElement.getCategoryCombo().getOptionCombos() );

                for ( DataElementCategoryOptionCombo optionCombo : deCOCList )
                {
                    CaseAggregationCondition condition = aggregationConditionService.getCaseAggregationCondition(
                        dElement, optionCombo );

                    if ( condition == null )
                        break;

                    for ( Period period : periodList )
                    {
                        double resultValue = aggregationConditionService.parseConditition( condition, orgUnit, period );
                        
                        if ( resultValue != 0 )
                        {
                            String tempStr = "" + orgUnit.getName() + "_" + dElement.getName() + "_"
                                + period.getStartDate() + "_";
                            DataValue dataValue = dataValueService
                                .getDataValue( orgUnit, dElement, period, optionCombo );
                            if ( dataValue == null )
                            {
                                dataValue = new DataValue( dElement, period, orgUnit, "" + resultValue, storedBy,
                                    new Date(), null, optionCombo );

                                dataValueService.addDataValue( dataValue );
                                tempStr += resultValue + "Added.";
                            }
                            else
                            {
                                dataValue.setValue( "" + resultValue );
                                dataValue.setTimestamp( new Date() );
                                dataValue.setStoredBy( storedBy );

                                dataValueService.updateDataValue( dataValue );
                                tempStr += resultValue + "Updated.";
                            }

                            System.out.println( tempStr );
                            resultMessage += "<br>" + tempStr;
                        }

                    }// PeriodList end

                }// OptionComboList end
            }// DataElementList end

        }// Orgunit for loop end

        return SUCCESS;
    }

    // Returns the OrgUnitTree for which Root is the orgUnit
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end

}
