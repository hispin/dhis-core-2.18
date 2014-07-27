package org.hisp.dhis.dxf2.csv;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import static org.hisp.dhis.system.util.DateUtils.getMediumDate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementDomain;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.expression.Operator;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.validation.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;

import com.csvreader.CsvReader;

/**
 * @author Lars Helge Overland
 */
public class DefaultCsvImportService
    implements CsvImportService
{
    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private ExpressionService expressionService;

    // -------------------------------------------------------------------------
    // CsvImportService implementation
    // -------------------------------------------------------------------------

    public MetaData fromCsv( InputStream input, Class<?> clazz )
        throws IOException
    {
        CsvReader reader = new CsvReader( input, Charset.forName( "UTF-8" ) );
        reader.readRecord(); // Ignore first row

        MetaData metaData = new MetaData();

        if ( DataElement.class.equals( clazz ) )
        {
            metaData.setDataElements( dataElementsFromCsv( reader ) );
        }
        else if ( DataElementGroup.class.equals( clazz ) )
        {
            metaData.setDataElementGroups( dataElementGroupsFromCsv( reader ) );
        }
        else if ( DataElementCategoryOption.class.equals( clazz ) )
        {
            metaData.setCategoryOptions( categoryOptionsFromCsv( reader ) );
        }
        else if ( CategoryOptionGroup.class.equals( clazz ) )
        {
            metaData.setCategoryOptionGroups( categoryOptionGroupsFromCsv( reader ) );
        }
        else if ( OrganisationUnit.class.equals( clazz ) )
        {
            metaData.setOrganisationUnits( organisationUnitsFromCsv( reader ) );
        }
        else if ( OrganisationUnitGroup.class.equals( clazz ) )
        {
            metaData.setOrganisationUnitGroups( organisationUnitGroupsFromCsv( reader ) );
        }
        else if ( ValidationRule.class.equals( clazz ) )
        {
            metaData.setValidationRules( validationRulesFromCsv( reader ) );
        }
        else if ( OptionSet.class.equals( clazz ) )
        {
            metaData.setOptionSets( getOptionSetsFromCsv( reader ) );
        }

        return metaData;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private List<DataElementCategoryOption> categoryOptionsFromCsv( CsvReader reader )
        throws IOException
    {
        List<DataElementCategoryOption> list = new ArrayList<DataElementCategoryOption>();

        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                DataElementCategoryOption object = new DataElementCategoryOption();
                setIdentifiableObject( object, values );
                list.add( object );
            }
        }

        return list;
    }

    private List<CategoryOptionGroup> categoryOptionGroupsFromCsv( CsvReader reader )
        throws IOException
    {
        List<CategoryOptionGroup> list = new ArrayList<CategoryOptionGroup>();

        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                CategoryOptionGroup object = new CategoryOptionGroup();
                setIdentifiableObject( object, values );
                list.add( object );
            }
        }

        return list;
    }

    private List<DataElement> dataElementsFromCsv( CsvReader reader )
        throws IOException
    {
        DataElementCategoryCombo categoryCombo = categoryService.getDefaultDataElementCategoryCombo();
        
        List<DataElement> list = new ArrayList<DataElement>();

        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                DataElement object = new DataElement();
                setIdentifiableObject( object, values );
                object.setShortName( getSafe( values, 3, object.getName(), 50 ) );
                object.setDescription( getSafe( values, 4, null, null ) );
                object.setFormName( getSafe( values, 5, null, 230 ) );
                
                String domainType = getSafe( values, 6, DataElementDomain.AGGREGATE.getValue(), 16 );
                object.setDomainType( DataElementDomain.fromValue( domainType ) );

                object.setType( getSafe( values, 7, DataElement.VALUE_TYPE_INT, 16 ) );
                object.setNumberType( getSafe( values, 8, DataElement.VALUE_TYPE_NUMBER, 16 ) );
                object.setTextType( getSafe( values, 9, null, 16 ) );
                object.setAggregationOperator( getSafe( values, 10, DataElement.AGGREGATION_OPERATOR_SUM, 16 ) );                
                String categoryComboUid = getSafe( values, 11, null, 11 );
                object.setUrl( getSafe( values, 12, null, 255 ) );
                object.setZeroIsSignificant( Boolean.valueOf( getSafe( values, 13, "false", null ) ) );

                if ( categoryComboUid != null )
                {
                    DataElementCategoryCombo cc = new DataElementCategoryCombo();
                    cc.setUid( categoryComboUid );
                    object.setCategoryCombo( cc );
                }
                else
                {
                    object.setCategoryCombo( categoryCombo );
                }
                
                list.add( object );
            }
        }

        return list;
    }

    private List<DataElementGroup> dataElementGroupsFromCsv( CsvReader reader )
        throws IOException
    {
        List<DataElementGroup> list = new ArrayList<DataElementGroup>();

        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                DataElementGroup object = new DataElementGroup();
                setIdentifiableObject( object, values );
                list.add( object );
            }
        }

        return list;
    }
    
    private List<ValidationRule> validationRulesFromCsv( CsvReader reader )
        throws IOException
    {
        List<ValidationRule> list = new ArrayList<ValidationRule>();

        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {            
                Expression leftSide = new Expression();
                Expression rightSide = new Expression();
                
                ValidationRule object = new ValidationRule();
                setIdentifiableObject( object, values );
                object.setDescription( getSafe( values, 3, null, 255 ) );
                object.setInstruction( getSafe( values, 4, null, 255 ) );
                object.setImportance( getSafe( values, 5, ValidationRule.IMPORTANCE_MEDIUM, 255 ) );
                object.setRuleType( getSafe( values, 6, ValidationRule.RULE_TYPE_VALIDATION, 255 ) );
                object.setOperator( Operator.safeValueOf( getSafe( values, 7, Operator.equal_to.toString(), 255 ) ) );
                object.setPeriodType( PeriodType.getByNameIgnoreCase( getSafe( values, 8, MonthlyPeriodType.NAME, 255 ) ) );
                
                leftSide.setExpression( getSafe( values, 9, null, 255 ) );
                leftSide.setDescription( getSafe( values, 10, null, 255 ) );
                leftSide.setNullIfBlank( Boolean.valueOf( getSafe( values, 11, Boolean.FALSE.toString(), 5 ) ) );
                leftSide.setDataElementsInExpression( expressionService.getDataElementsInExpression( leftSide.getExpression() ) );
                leftSide.setOptionCombosInExpression( expressionService.getOptionCombosInExpression( leftSide.getExpression() ) );
                leftSide.setNullIfBlank( true );
                
                rightSide.setExpression( getSafe( values, 12, null, 255 ) );
                rightSide.setDescription( getSafe( values, 13, null, 255 ) );
                rightSide.setNullIfBlank( Boolean.valueOf( getSafe( values, 14, Boolean.FALSE.toString(), 5 ) ) );
                rightSide.setDataElementsInExpression( expressionService.getDataElementsInExpression( rightSide.getExpression() ) );
                rightSide.setOptionCombosInExpression( expressionService.getOptionCombosInExpression( rightSide.getExpression() ) );
                rightSide.setNullIfBlank( true );
                
                object.setLeftSide( leftSide );
                object.setRightSide( rightSide );
                
                list.add( object );
            }
        }
        
        return list;
    }
    
    private List<OrganisationUnit> organisationUnitsFromCsv( CsvReader reader )
        throws IOException
    {
        List<OrganisationUnit> list = new ArrayList<OrganisationUnit>();

        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                OrganisationUnit object = new OrganisationUnit();
                setIdentifiableObject( object, values );
                String parentUid = getSafe( values, 3, null, 11 );
                object.setShortName( getSafe( values, 4, object.getName(), 50 ) );
                object.setDescription( getSafe( values, 5, null, null ) );
                object.setUuid( getSafe( values, 6, null, 36 ) );
                object.setOpeningDate( getMediumDate( getSafe( values, 7, "1970-01-01", null ) ) );
                object.setClosedDate( getMediumDate( getSafe( values, 8, "1970-01-01", null ) ) );
                object.setActive( true );
                object.setComment( getSafe( values, 9, null, null ) );
                object.setFeatureType( getSafe( values, 10, null, 50 ) );
                object.setCoordinates( getSafe( values, 11, null, null ) );
                object.setUrl( getSafe( values, 12, null, 255 ) );
                object.setContactPerson( getSafe( values, 13, null, 255 ) );
                object.setAddress( getSafe( values, 14, null, 255 ) );
                object.setEmail( getSafe( values, 15, null, 150 ) );
                object.setPhoneNumber( getSafe( values, 16, null, 150 ) );
                                
                if ( parentUid != null )
                {
                    OrganisationUnit parent = new OrganisationUnit();
                    parent.setUid( parentUid );
                    object.setParent( parent );
                }

                list.add( object );
            }
        }

        return list;
    }

    private List<OrganisationUnitGroup> organisationUnitGroupsFromCsv( CsvReader reader )
        throws IOException
    {
        List<OrganisationUnitGroup> list = new ArrayList<OrganisationUnitGroup>();

        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                OrganisationUnitGroup object = new OrganisationUnitGroup();
                setIdentifiableObject( object, values );
                list.add( object );
            }
        }

        return list;
    }

    private List<OptionSet> getOptionSetsFromCsv( CsvReader reader )
        throws IOException
    {
        ListMap<OptionSet, String> listMap = new ListMap<OptionSet, String>();

        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                OptionSet object = new OptionSet();
                setIdentifiableObject( object, values );
                String option = getSafe( values, 3, null, 2000000 );

                listMap.putValue( object, option );
            }
        }

        List<OptionSet> optionSets = new ArrayList<OptionSet>();

        for ( OptionSet optionSet : listMap.keySet() )
        {
            List<String> options = new ArrayList<String>( listMap.get( optionSet ) );
            optionSet.setOptions( options );
            optionSets.add( optionSet );
        }

        return optionSets;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Sets the name, uid and code properties on the given object.
     * 
     * @param object the object to set identifiable properties.
     * @param values the array of property values.
     */
    private static void setIdentifiableObject( BaseIdentifiableObject object, String[] values )
    {
        object.setName( getSafe( values, 0, null, 230 ) );
        object.setUid( getSafe( values, 1, CodeGenerator.generateCode(), 11 ) );
        object.setCode( getSafe( values, 2, null, 50 ) );
    }

    /**
     * Returns a string from the given array avoiding exceptions.
     * 
     * @param values the string array.
     * @param index the array index of the string to get.
     * @param defaultValue the default value in case index is out of bounds.
     * @param maxChars the max number of characters to return for the string.
     */
    private static String getSafe( String[] values, int index, String defaultValue, Integer maxChars )
    {
        String string = null;

        if ( values == null || index < 0 || index >= values.length )
        {
            string = defaultValue;
        }
        else
        {
            string = values[index];
        }

        string = StringUtils.defaultIfBlank( string, defaultValue );

        if ( string != null )
        {
            return maxChars != null ? StringUtils.substring( string, 0, maxChars ) : string;
        }

        return null;
    }
}
