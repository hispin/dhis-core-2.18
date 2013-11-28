package org.hisp.dhis.caseaggregation;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
import java.util.List;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.Program;

/**
 * @author Chau Thu Tran
 * 
 * @version CaseAggregationCondititionService.java Nov 17, 2010 10:56:29 AM
 */
public interface CaseAggregationConditionService
{
    int addCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition );

    void updateCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition );

    void deleteCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition );

    CaseAggregationCondition getCaseAggregationCondition( int id );

    CaseAggregationCondition getCaseAggregationCondition( String name );

    Collection<CaseAggregationCondition> getAllCaseAggregationCondition();

    Collection<CaseAggregationCondition> getCaseAggregationCondition( DataElement dataElement );

    CaseAggregationCondition getCaseAggregationCondition( DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo );

    Collection<CaseAggregationCondition> getCaseAggregationCondition( Collection<DataElement> dataElements );

    Collection<DataElement> getDataElementsInCondition( String aggregationExpression );

    Collection<Program> getProgramsInCondition( String aggregationExpression );

    Collection<PatientAttribute> getPatientAttributesInCondition( String aggregationExpression );

    String getConditionDescription( String condition );

    /**
     * Aggregate data values from query builder formulas defined based on
     * datasets which have data elements defined in the formulas
     * 
     * @param caseAggregateSchedule
     * @param taskStrategy Specify how to get period list based on period type
     *        of each dataset. There are four options, include last month, last
     *        3 month, last 6 month and last 12 month
     */
    void aggregate( List<CaseAggregateSchedule> caseAggregateSchedules, String taskStrategy );

    /**
     * Return a data value table aggregated of a query builder formula
     * 
     * @param caseAggregationCondition The query builder expression
     * @param orgunitIds The ids of organisation unit where to aggregate data
     *        value
     * @param period The date range for aggregate data value
     * @param format
     * @param i18n
     */
    Grid getAggregateValue( CaseAggregationCondition caseAggregationCondition, Collection<Integer> orgunitIds,
        Period period, I18nFormat format, I18n i18n );

    void insertAggregateValue( CaseAggregationCondition caseAggregationCondition, Collection<Integer> orgunitIds,
        Period period );

    Grid getAggregateValueDetails( CaseAggregationCondition aggregationCondition, OrganisationUnit orgunit,
        Period period, I18nFormat format, I18n i18n );

    String parseExpressionToSql( boolean isInsert, String caseExpression, String operator, Integer aggregateDeId,
        String aggregateDeName, Integer optionComboId, String optionComboName, Integer deSumId,
        Collection<Integer> orgunitIds, Period period );

    String parseExpressionDetailsToSql( String caseExpression, String operator, Integer orgunitId, Period period );

    List<Integer> executeSQL( String sql );
}
