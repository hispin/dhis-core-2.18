package org.hisp.dhis.validation;

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

import static org.hisp.dhis.expression.Expression.SEPARATOR;
import static org.hisp.dhis.expression.Operator.equal_to;
import static org.hisp.dhis.expression.Operator.greater_than;
import static org.hisp.dhis.expression.Operator.less_than;
import static org.hisp.dhis.expression.Operator.less_than_or_equal_to;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.system.util.MathUtils;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ValidationRuleServiceTest
    extends DhisTest
{
    private DataElement dataElementA;

    private DataElement dataElementB;

    private DataElement dataElementC;

    private DataElement dataElementD;

    private DataElement dataElementE;

    private Set<DataElement> dataElementsA = new HashSet<DataElement>();

    private Set<DataElement> dataElementsB = new HashSet<DataElement>();

    private Set<DataElement> dataElementsC = new HashSet<DataElement>();

    private Set<DataElement> dataElementsD = new HashSet<DataElement>();

    private Set<DataElementCategoryOptionCombo> optionCombos;

    private DataElementCategoryCombo categoryCombo;

    private DataElementCategoryOptionCombo categoryOptionCombo;

    private Expression expressionA;

    private Expression expressionB;

    private Expression expressionC;

    private Expression expressionD;

    private Expression expressionE;

    private Expression expressionF;

    private Expression expressionG;

    private DataSet dataSetWeekly;

    private DataSet dataSetMonthly;

    private DataSet dataSetYearly;

    private Period periodA;

    private Period periodB;

    private Period periodC;

    private Period periodD;

    private Period periodE;

    private Period periodF;

    private Period periodG;

    private Period periodH;

    private Period periodI;

    private Period periodX;

    private Period periodY;

    private OrganisationUnit sourceA;

    private OrganisationUnit sourceB;

    private OrganisationUnit sourceC;

    private OrganisationUnit sourceD;

    private OrganisationUnit sourceE;

    private OrganisationUnit sourceF;

    private OrganisationUnit sourceG;

    private Set<OrganisationUnit> sourcesA = new HashSet<OrganisationUnit>();

    private ValidationRule validationRuleA;

    private ValidationRule validationRuleB;

    private ValidationRule validationRuleC;

    private ValidationRule validationRuleD;

    private ValidationRule monitoringRuleE;

    private ValidationRule monitoringRuleF;

    private ValidationRule monitoringRuleG;

    private ValidationRule monitoringRuleH;

    private ValidationRule monitoringRuleI;

    private ValidationRule monitoringRuleJ;

    private ValidationRule monitoringRuleK;

    private ValidationRuleGroup group;

    private PeriodType periodTypeWeekly;
    
    private PeriodType periodTypeMonthly;
    
    private PeriodType periodTypeYearly;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        validationRuleService = (ValidationRuleService) getBean( ValidationRuleService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        expressionService = (ExpressionService) getBean( ExpressionService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        periodTypeWeekly = new WeeklyPeriodType();
        periodTypeMonthly = new MonthlyPeriodType();
        periodTypeYearly = new YearlyPeriodType();

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        dataElementC = createDataElement( 'C' );
        dataElementD = createDataElement( 'D' );
        dataElementE = createDataElement( 'E' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );
        dataElementService.addDataElement( dataElementE );

        dataElementsA.add( dataElementA );
        dataElementsA.add( dataElementB );
        dataElementsB.add( dataElementC );
        dataElementsB.add( dataElementD );
        dataElementsC.add( dataElementB );
        dataElementsD.add( dataElementB );
        dataElementsD.add( dataElementE );

        categoryCombo = categoryService
            .getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        categoryOptionCombo = categoryCombo.getOptionCombos().iterator().next();

        String suffix = SEPARATOR + categoryOptionCombo.getUid();

        optionCombos = new HashSet<DataElementCategoryOptionCombo>();
        optionCombos.add( categoryOptionCombo );

        expressionA = new Expression( "#{" + dataElementA.getUid() + suffix + "} + #{" + dataElementB.getUid() + suffix + "}",
            "descriptionA", dataElementsA, optionCombos );
        expressionB = new Expression( "#{" + dataElementC.getUid() + suffix + "} - #{" + dataElementD.getUid() + suffix + "}",
            "descriptionB", dataElementsB , optionCombos);
        expressionC = new Expression( "#{" + dataElementB.getUid() + suffix + "} * 2", "descriptionC", dataElementsC, optionCombos );
        expressionD = new Expression( "#{" + dataElementB.getUid() + suffix + "}", "descriptionD", dataElementsC, optionCombos );
        expressionE = new Expression( "#{" + dataElementB.getUid() + suffix + "} * 1.25", "descriptionE", dataElementsC, optionCombos );
        expressionF = new Expression( "#{" + dataElementB.getUid() + suffix + "} / #{" + dataElementE.getUid() + suffix + "}",
        		"descriptionF", dataElementsD, optionCombos );
        expressionG = new Expression( "#{" + dataElementB.getUid() + suffix + "} * 1.25 / #{" + dataElementE.getUid() + suffix + "}",
        		"descriptionG", dataElementsD, optionCombos );

        expressionService.addExpression( expressionA );
        expressionService.addExpression( expressionB );
        expressionService.addExpression( expressionC );
        expressionService.addExpression( expressionD );
        expressionService.addExpression( expressionE );
        expressionService.addExpression( expressionF );
        expressionService.addExpression( expressionG );

        periodA = createPeriod( periodTypeMonthly, getDate( 2000, 3, 1 ), getDate( 2000, 3, 31 ) );
        periodB = createPeriod( periodTypeMonthly, getDate( 2000, 4, 1 ), getDate( 2000, 4, 30 ) );
        periodC = createPeriod( periodTypeMonthly, getDate( 2000, 5, 1 ), getDate( 2000, 5, 31 ) );
        periodD = createPeriod( periodTypeMonthly, getDate( 2000, 6, 1 ), getDate( 2000, 6, 31 ) );
        periodE = createPeriod( periodTypeMonthly, getDate( 2001, 2, 1 ), getDate( 2001, 2, 28 ) );
        periodF = createPeriod( periodTypeMonthly, getDate( 2001, 3, 1 ), getDate( 2001, 3, 31 ) );
        periodG = createPeriod( periodTypeMonthly, getDate( 2001, 4, 1 ), getDate( 2001, 4, 30 ) );
        periodH = createPeriod( periodTypeMonthly, getDate( 2001, 5, 1 ), getDate( 2001, 5, 31 ) );
        periodI = createPeriod( periodTypeWeekly, getDate( 2000, 4, 1 ), getDate( 2000, 4, 30 ) );
        periodX = createPeriod( periodTypeYearly, getDate( 2000, 1, 1 ), getDate( 2000, 12, 31 ) );
        periodY = createPeriod( periodTypeYearly, getDate( 2001, 1, 1 ), getDate( 2001, 12, 31 ) );

        dataSetWeekly = createDataSet( 'W', periodTypeWeekly );
        dataSetMonthly = createDataSet( 'M', periodTypeMonthly );
        dataSetYearly = createDataSet( 'Y', periodTypeYearly );

        sourceA = createOrganisationUnit( 'A' );
        sourceB = createOrganisationUnit( 'B' );
        sourceC = createOrganisationUnit( 'C', sourceB );
        sourceD = createOrganisationUnit( 'D', sourceB );
        sourceE = createOrganisationUnit( 'E', sourceD );
        sourceF = createOrganisationUnit( 'F', sourceD );
        sourceG = createOrganisationUnit( 'G' );

        sourceA.getDataSets().add( dataSetMonthly );
        sourceB.getDataSets().add( dataSetMonthly );
        sourceC.getDataSets().add( dataSetWeekly );
        sourceC.getDataSets().add( dataSetMonthly );
        sourceC.getDataSets().add( dataSetYearly );
        sourceD.getDataSets().add( dataSetMonthly );
        sourceD.getDataSets().add( dataSetYearly );
        sourceE.getDataSets().add( dataSetMonthly );
        sourceE.getDataSets().add( dataSetYearly );
        sourceF.getDataSets().add( dataSetMonthly );
        sourceF.getDataSets().add( dataSetYearly );

        organisationUnitService.addOrganisationUnit( sourceA );
        organisationUnitService.addOrganisationUnit( sourceB );
        organisationUnitService.addOrganisationUnit( sourceC );
        organisationUnitService.addOrganisationUnit( sourceD );
        organisationUnitService.addOrganisationUnit( sourceE );
        organisationUnitService.addOrganisationUnit( sourceF );
        
        sourcesA.add( sourceA );
        sourcesA.add( sourceB );

        dataSetMonthly.getDataElements().add( dataElementA );
        dataSetMonthly.getDataElements().add( dataElementB );
        dataSetMonthly.getDataElements().add( dataElementC );
        dataSetMonthly.getDataElements().add( dataElementD );

        dataSetMonthly.getSources().add( sourceA );
        dataSetMonthly.getSources().add( sourceB );
        dataSetMonthly.getSources().add( sourceC );
        dataSetMonthly.getSources().add( sourceD );
        dataSetMonthly.getSources().add( sourceE );
        dataSetMonthly.getSources().add( sourceF );
        dataSetWeekly.getSources().add( sourceB );
        dataSetWeekly.getSources().add( sourceC );
        dataSetWeekly.getSources().add( sourceD );
        dataSetWeekly.getSources().add( sourceE );
        dataSetWeekly.getSources().add( sourceF );
        dataSetWeekly.getSources().add( sourceG );
        dataSetYearly.getSources().add( sourceB );
        dataSetYearly.getSources().add( sourceC );
        dataSetYearly.getSources().add( sourceD );
        dataSetYearly.getSources().add( sourceE );
        dataSetYearly.getSources().add( sourceF );

        dataElementA.getDataSets().add( dataSetMonthly );
        dataElementB.getDataSets().add( dataSetMonthly );
        dataElementC.getDataSets().add( dataSetMonthly );
        dataElementD.getDataSets().add( dataSetMonthly );

        dataSetService.addDataSet( dataSetMonthly );

        dataElementService.updateDataElement( dataElementA );
        dataElementService.updateDataElement( dataElementB );
        dataElementService.updateDataElement( dataElementC );
        dataElementService.updateDataElement( dataElementD );

        validationRuleA = createValidationRule( 'A', equal_to, expressionA, expressionB, periodTypeMonthly );
        validationRuleB = createValidationRule( 'B', greater_than, expressionB, expressionC, periodTypeMonthly );
        validationRuleC = createValidationRule( 'C', less_than_or_equal_to, expressionB, expressionA, periodTypeMonthly );
        validationRuleD = createValidationRule( 'D', less_than, expressionA, expressionC, periodTypeMonthly );
        
        // Compare dataElementB with 1.25 times itself for one previous sequential period.
        monitoringRuleE = createMonitoringRule( 'E', less_than, expressionD, expressionE, periodTypeMonthly, 1, 1, 0, 0, 0 );

        // Compare dataElementB with 1.25 times itself for one previous annual period.
        monitoringRuleF = createMonitoringRule( 'F', less_than, expressionD, expressionE, periodTypeMonthly, 1, 0, 1, 0, 0 );
        
        // Compare dataElementB with 1.25 times itself for two previous and two annual sequential periods.
        monitoringRuleG = createMonitoringRule( 'G', less_than, expressionD, expressionE, periodTypeMonthly, 1, 2, 2, 0, 0 );
        
        // Compare dataElementB with 1.25 times itself for two previous and two annual sequential periods, discarding 2 high outliers.
        monitoringRuleH = createMonitoringRule( 'H', less_than, expressionD, expressionE, periodTypeMonthly, 1, 2, 2, 2, 0 );
        
        // Compare dataElementB with 1.25 times itself for two previous and two annual sequential periods, discarding 2 low outliers.
        monitoringRuleI = createMonitoringRule( 'I', less_than, expressionD, expressionE, periodTypeMonthly, 1, 2, 2, 2, 0 );
        
        // Compare dataElementB with 1.25 times itself for two previous and two annual sequential periods, discarding 2 high & 2 low outliers.
        monitoringRuleJ = createMonitoringRule( 'J', less_than, expressionD, expressionE, periodTypeMonthly, 1, 2, 2, 2, 2 );
        
        // Compare dataElements B/E with 1.25 * B/E for two previous and two annual sequential periods, no outlier discarding
        monitoringRuleK = createMonitoringRule( 'K', less_than, expressionF, expressionG, periodTypeMonthly, 1, 2, 2, 0, 0 );

        group = createValidationRuleGroup( 'A' );
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Business logic tests
    // -------------------------------------------------------------------------

    @Test
    public void testValidateDateDateSources()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceB, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceB, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceB, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceB, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceB, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceB, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceB, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceB, "4", categoryOptionCombo ) );

        validationRuleService.saveValidationRule( validationRuleA ); // Invalid
        validationRuleService.saveValidationRule( validationRuleB ); // Invalid
        validationRuleService.saveValidationRule( validationRuleC ); // Valid
        validationRuleService.saveValidationRule( validationRuleD ); // Valid

        // Note: in this and subsequent tests we insert the validation results collection into a new HashSet. This
        // insures that if they are the same as the reference results, they will appear in the same order.
        
        Collection<ValidationResult> results = new HashSet<ValidationResult>( validationRuleService.validate( getDate( 2000, 2, 1 ),
        		getDate( 2000, 6, 1 ), sourcesA ) );

        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodA, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodA, sourceB, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceB, validationRuleA, 3.0, -1.0 ) );

        reference.add( new ValidationResult( periodA, sourceA, validationRuleB, -1.0, 4.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleB, -1.0, 4.0 ) );
        reference.add( new ValidationResult( periodA, sourceB, validationRuleB, -1.0, 4.0 ) );
        reference.add( new ValidationResult( periodB, sourceB, validationRuleB, -1.0, 4.0 ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 8, results.size() );
        assertEquals( reference, results );
    }

    @Test
    public void testValidateDateDateSourcesGroup()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceB, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceB, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceB, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceB, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceB, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceB, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceB, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceB, "4", categoryOptionCombo ) );

        validationRuleService.saveValidationRule( validationRuleA ); // Invalid
        validationRuleService.saveValidationRule( validationRuleB ); // Invalid
        validationRuleService.saveValidationRule( validationRuleC ); // Valid
        validationRuleService.saveValidationRule( validationRuleD ); // Valid

        group.getMembers().add( validationRuleA );
        group.getMembers().add( validationRuleC );

        validationRuleService.addValidationRuleGroup( group );

        Collection<ValidationResult> results = new HashSet<ValidationResult>( validationRuleService.validate( getDate( 2000, 2, 1 ),
        		getDate( 2000, 6, 1 ), sourcesA, group ) );

        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodA, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodA, sourceB, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceB, validationRuleA, 3.0, -1.0 ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 4, results.size() );
        assertEquals( reference, results );
    }

    @Test
    public void testValidateDateDateSource()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceA, "4", categoryOptionCombo ) );

        validationRuleService.saveValidationRule( validationRuleA );
        validationRuleService.saveValidationRule( validationRuleB );
        validationRuleService.saveValidationRule( validationRuleC );
        validationRuleService.saveValidationRule( validationRuleD );

        Collection<ValidationResult> results = new HashSet<ValidationResult>( validationRuleService.validate( getDate( 2000, 2, 1 ),
        		getDate( 2000, 6, 1 ), sourceA ) );

        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodA, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodA, sourceA, validationRuleB, -1.0, 4.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleB, -1.0, 4.0 ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 4, results.size() );
        assertEquals( reference, results );
    }

    @Test
    public void testValidateDataSetPeriodSource()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceA, "4", categoryOptionCombo ) );

        validationRuleService.saveValidationRule( validationRuleA );
        validationRuleService.saveValidationRule( validationRuleB );
        validationRuleService.saveValidationRule( validationRuleC );
        validationRuleService.saveValidationRule( validationRuleD );

        Collection<ValidationResult> results = new HashSet<ValidationResult>( validationRuleService.validate( dataSetMonthly, periodA, sourceA ) );

        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodA, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodA, sourceA, validationRuleB, -1.0, 4.0 ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 2, results.size() );
        assertEquals( reference, results );
    }

    @Test
    public void testValidateMonitoringSequential()
    {
    	
    }

    @Test
    public void testValidateMonitoringAnnual()
    {
    	
    }

    @Test
    public void testValidateMonitoringSequentialAndAnnual()
    {
    	
    }

    @Test
    public void testValidateMonitoringTwoSequentialAndAnnual()
    {
    	
    }

    @Test
    public void testValidateMonitoringHighOutliers()
    {
    	
    }

    @Test
    public void testValidateMonitoringLowOutliers()
    {
    	
    }

    @Test
    public void testValidateMonitoringHighAndLowOutliers()
    {
    	
    }

    @Test
    public void testValidateMonitoringWithBaseline()
    {
    	
    }

    // -------------------------------------------------------------------------
    // CURD functionality tests
    // -------------------------------------------------------------------------

    //@Test
    public void testSaveValidationRule()
    {
        int id = validationRuleService.saveValidationRule( validationRuleA );

        validationRuleA = validationRuleService.getValidationRule( id );

        assertEquals( "ValidationRuleA", validationRuleA.getName() );
        assertEquals( "DescriptionA", validationRuleA.getDescription() );
        assertEquals( ValidationRule.TYPE_ABSOLUTE, validationRuleA.getType() );
        assertEquals( equal_to, validationRuleA.getOperator() );
        assertNotNull( validationRuleA.getLeftSide().getExpression() );
        assertNotNull( validationRuleA.getRightSide().getExpression() );
        assertEquals( periodTypeMonthly, validationRuleA.getPeriodType() );
    }

    @Test
    public void testUpdateValidationRule()
    {
        int id = validationRuleService.saveValidationRule( validationRuleA );
        validationRuleA = validationRuleService.getValidationRuleByName( "ValidationRuleA" );

        assertEquals( "ValidationRuleA", validationRuleA.getName() );
        assertEquals( "DescriptionA", validationRuleA.getDescription() );
        assertEquals( ValidationRule.TYPE_ABSOLUTE, validationRuleA.getType() );
        assertEquals( equal_to, validationRuleA.getOperator() );

        validationRuleA.setId( id );
        validationRuleA.setName( "ValidationRuleB" );
        validationRuleA.setDescription( "DescriptionB" );
        validationRuleA.setType( ValidationRule.TYPE_STATISTICAL );
        validationRuleA.setOperator( greater_than );

        validationRuleService.updateValidationRule( validationRuleA );
        validationRuleA = validationRuleService.getValidationRule( id );

        assertEquals( "ValidationRuleB", validationRuleA.getName() );
        assertEquals( "DescriptionB", validationRuleA.getDescription() );
        assertEquals( ValidationRule.TYPE_STATISTICAL, validationRuleA.getType() );
        assertEquals( greater_than, validationRuleA.getOperator() );
    }

    @Test
    public void testDeleteValidationRule()
    {
        int idA = validationRuleService.saveValidationRule( validationRuleA );
        int idB = validationRuleService.saveValidationRule( validationRuleB );

        assertNotNull( validationRuleService.getValidationRule( idA ) );
        assertNotNull( validationRuleService.getValidationRule( idB ) );

        validationRuleA.clearExpressions();

        validationRuleService.deleteValidationRule( validationRuleA );

        assertNull( validationRuleService.getValidationRule( idA ) );
        assertNotNull( validationRuleService.getValidationRule( idB ) );

        validationRuleB.clearExpressions();

        validationRuleService.deleteValidationRule( validationRuleB );

        assertNull( validationRuleService.getValidationRule( idA ) );
        assertNull( validationRuleService.getValidationRule( idB ) );
    }

    //@Test
    public void testGetAllValidationRules()
    {
        validationRuleService.saveValidationRule( validationRuleA );
        validationRuleService.saveValidationRule( validationRuleB );

        Collection<ValidationRule> rules = validationRuleService.getAllValidationRules();

        assertTrue( rules.size() == 2 );
        assertTrue( rules.contains( validationRuleA ) );
        assertTrue( rules.contains( validationRuleB ) );
    }

    @Test
    public void testGetValidationRuleByName()
    {
        int id = validationRuleService.saveValidationRule( validationRuleA );
        validationRuleService.saveValidationRule( validationRuleB );

        ValidationRule rule = validationRuleService.getValidationRuleByName( "ValidationRuleA" );

        assertEquals( id, rule.getId() );
        assertEquals( "ValidationRuleA", rule.getName() );
    }

    // -------------------------------------------------------------------------
    // ValidationRuleGroup
    // -------------------------------------------------------------------------

    @Test
    public void testAddValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleService.addValidationRuleGroup( groupA );
        int idB = validationRuleService.addValidationRuleGroup( groupB );

        assertEquals( groupA, validationRuleService.getValidationRuleGroup( idA ) );
        assertEquals( groupB, validationRuleService.getValidationRuleGroup( idB ) );
    }

    @Test
    public void testUpdateValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleService.addValidationRuleGroup( groupA );
        int idB = validationRuleService.addValidationRuleGroup( groupB );

        assertEquals( groupA, validationRuleService.getValidationRuleGroup( idA ) );
        assertEquals( groupB, validationRuleService.getValidationRuleGroup( idB ) );

        ruleA.setName( "UpdatedValidationRuleA" );
        ruleB.setName( "UpdatedValidationRuleB" );

        validationRuleService.updateValidationRuleGroup( groupA );
        validationRuleService.updateValidationRuleGroup( groupB );

        assertEquals( groupA, validationRuleService.getValidationRuleGroup( idA ) );
        assertEquals( groupB, validationRuleService.getValidationRuleGroup( idB ) );
    }

    @Test
    public void testDeleteValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleService.addValidationRuleGroup( groupA );
        int idB = validationRuleService.addValidationRuleGroup( groupB );

        assertNotNull( validationRuleService.getValidationRuleGroup( idA ) );
        assertNotNull( validationRuleService.getValidationRuleGroup( idB ) );

        validationRuleService.deleteValidationRuleGroup( groupA );

        assertNull( validationRuleService.getValidationRuleGroup( idA ) );
        assertNotNull( validationRuleService.getValidationRuleGroup( idB ) );

        validationRuleService.deleteValidationRuleGroup( groupB );

        assertNull( validationRuleService.getValidationRuleGroup( idA ) );
        assertNull( validationRuleService.getValidationRuleGroup( idB ) );
    }

    @Test
    public void testGetAllValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        validationRuleService.addValidationRuleGroup( groupA );
        validationRuleService.addValidationRuleGroup( groupB );

        Collection<ValidationRuleGroup> groups = validationRuleService.getAllValidationRuleGroups();

        assertEquals( 2, groups.size() );
        assertTrue( groups.contains( groupA ) );
        assertTrue( groups.contains( groupB ) );
    }

    @Test
    public void testGetValidationRuleGroupByName()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        validationRuleService.addValidationRuleGroup( groupA );
        validationRuleService.addValidationRuleGroup( groupB );

        ValidationRuleGroup groupByName = validationRuleService.getValidationRuleGroupByName( groupA.getName() );

        assertEquals( groupA, groupByName );
    }
}
