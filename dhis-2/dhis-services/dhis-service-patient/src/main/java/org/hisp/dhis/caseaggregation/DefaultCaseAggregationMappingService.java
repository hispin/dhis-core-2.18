package org.hisp.dhis.caseaggregation;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultCaseAggregationMappingService
    implements CaseAggregationMappingService
{
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final String ALIAS_DATAVALUE = "pdv";
    
    private static final String ALIAS_PROGRAMSTAGEINSTANCE = "psi";

    private static final String ALIAS_PROGRAMINSTANCE = "pi";

    private static final String ALIAS_PATIENT_ATTRIBUTE_VALUE = "pav";
    
    private static final String ALIAS_PATIENT = "pa";
    
    private static final String MAP_CONSTANT_QUERYFROM = "queryFrom";
    
    private static final String MAP_CONSTANT_QUERYWHERE = "queryWhere";
    
    private static final String MAP_CONSTANT_ALIAS_PROGRAMSTAGEINSTANCE = "aliasProgramStageInstance";
    
    private static final String MAP_CONSTANT_ALIAS_DATAVALUE = "aliasDataValue";
    
    private static final String MAP_CONSTANT_ALIAS_PROGRAMINSTANCE = "aliasProgramInstance";
    
    private static final String MAP_CONSTANT_ALIAS_PATIENT_ATTRIBUTE_VALUE = "aliasPatientAttributeValue";
    
    private static final String MAP_CONSTANT_ALIAS_PATIENT = "aliasPatient";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CaseAggregationMappingStore caseAggregationMappingStore;

    public void setCaseAggregationMappingStore( CaseAggregationMappingStore caseAggregationMappingStore )
    {
        this.caseAggregationMappingStore = caseAggregationMappingStore;
    }
    
    // -------------------------------------------------------------------------
    // CaseAggregationMapping
    // -------------------------------------------------------------------------

    @Override
    public void addCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        caseAggregationMappingStore.addCaseAggregationMapping( caseAggregationMapping );
    }

    @Override
    public void deleteCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        caseAggregationMappingStore.deleteCaseAggregationMapping( caseAggregationMapping );
    }

    @Override
    public void updateCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        caseAggregationMappingStore.updateCaseAggregationMapping( caseAggregationMapping );
    }

    @Override
    public CaseAggregationMapping getCaseAggregationMappingByOptionCombo( DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo )
    {
        return caseAggregationMappingStore.getCaseAggregationMappingByOptionCombo( dataElement, optionCombo );
    }

    public int getCaseAggregateValue( OrganisationUnit orgUnit, Period period,
        CaseAggregationMapping caseAggregationMapping )
    {
        
        CaseAggregationQuery query = scan( caseAggregationMapping.getExpression() );
        
        String hqlQuery = buildQuery( orgUnit, period , query);
       
        return  caseAggregationMappingStore.executeMappingQuery( hqlQuery );

    }
    
    
    public List<PatientDataValue> getCaseAggregatePatientDataValue( OrganisationUnit orgUnit, Period period,
        CaseAggregationMapping caseAggregationMapping )
    {
        
        CaseAggregationQuery query = scan( caseAggregationMapping.getExpression() );
        
        String hqlQuery = buildQueryForListPatientDataValue( orgUnit, period , query);
       
        return  caseAggregationMappingStore.executeMappingQueryForListPatientDataValue( hqlQuery );

    }
    
    /**
     * Scan and parse the Aggeration Mapping Query 
     * @param input
     * @return CaseAggregationQuery, this object contains the list Conditions of the mapping query and the main function of the query  (SUM OR COUNT)
     */

    public CaseAggregationQuery scan( String input )
    {
        input = StringUtils.deleteWhitespace( input );
        CaseAggregationQuery query = new CaseAggregationQuery();
        
        String[] arr = input.split( CaseAggregationCondition.FUNCTION_IDENTIFIER );
        query.setFunction( arr[0] );
        char[] arrChar = arr[1].toCharArray();
        String tmp = "";
        int i = 0;
        CaseAggregationCondition c = null;
        while ( i < arrChar.length )
        {
            tmp += arrChar[i];

            if ( CaseAggregationCondition.SINGLE_CONDITION.equals( tmp ) || CaseAggregationCondition.CONDITION.equals( tmp ) )
            {
                c = new CaseAggregationCondition();
                c.setType( CaseAggregationCondition.SINGLE_CONDITION.equals( tmp ) ? CaseAggregationCondition.SINGLE_CONDITION : CaseAggregationCondition.CONDITION );
                tmp = "";
            }
            if ( CaseAggregationCondition.OPEN_CONDITION == arrChar[i] )
            {
                i++;
                tmp = "";
                while ( CaseAggregationCondition.CLOSE_CONDITION != arrChar[i] && i < arrChar.length )
                {
                    tmp += arrChar[i];
                    i++;
                }
                if ( CaseAggregationCondition.SINGLE_CONDITION.equals( c.getType() ) )
                {
                    c.setLeftExpression( tmp );
                }
                else if ( CaseAggregationCondition.CONDITION.equals( c.getType() ) )
                {
                    c = parseCondition( c, tmp );
                }
                tmp = "";
                query.addCondition( c );
            }
            if ( "AND".equals( tmp ) || "OR".equals( tmp ) )
            {
                query.getConditions().get( query.getConditions().size() - 1 ).setNext( tmp );
                
                tmp = "";
            }
            i++;
        }

        return query;
    }

    private CaseAggregationCondition parseCondition( CaseAggregationCondition c, String input )
    {
        input = StringUtils.deleteWhitespace( input );
        String tmp = "";
        int i = 0;
        char[] arr = input.toCharArray();
        while ( i < arr.length )
        {
            if ( CaseAggregationCondition.OPEN_EXPRESSION == arr[i] )
            {
                i++;
                while ( CaseAggregationCondition.CLOSE_EXPRESSION != arr[i] && i < arr.length )
                {
                    tmp += arr[i];
                    i++;
                }
                if ( c.getLeftExpression() == null )
                {
                    c.setLeftExpression( tmp );
                }
                else
                {
                    c.setRightExpression(  tmp ); 
                }
                tmp = "";

            }
            else
            {
                c.setOperator( c.getOperator()  +  arr[i] );
            }
            i++;
        }
        return c;
    }

    private List<String> parseExpression( String input )
    {
        if ( input == null )
            return null;
        
        List<String> list = new ArrayList<String>();

        input = StringUtils.deleteWhitespace( input );
        String tmp = "";
        int i = 0;
        char[] arr = input.toCharArray();
        while ( i < arr.length )
        {
            if ( CaseAggregationCondition.OPEN_ARGUMENT == arr[i] )
            {
                i++;
                while ( CaseAggregationCondition.CLOSE_ARGUMENT != arr[i] && i < arr.length )
                {
                    tmp += arr[i];
                    i++;
                }
                list.add( tmp );
                tmp = "";

            }
            else if ( isOperator( arr[i] ) )
            {
                if ( list.size() == 0 )
                {
                    System.out.println("Error parseExpression: "+input+"-- at char: "+arr[i]);
                }
                list.add( CharUtils.toString( arr[i] ) );
            }
            else
            {
                // error unidentified character ....
                System.out.println("Error unidentified character.ParseExpression: "+input+"-- at char: "+arr[i]);
            }
            i++;
        }

        return list;
    }

    
    private String buildQueryForListPatientDataValue( OrganisationUnit organisationUnit, Period period,
        CaseAggregationQuery input )
    {
        String queryFrom = "Select "+ ALIAS_DATAVALUE +" from ";
//        String queryFrom = "Select pdv from ";
        String queryWhere = " where ";
        System.out.println("------------buildQuery");
        System.out.println("ORGUNITL : "+organisationUnit);
        

        System.out.println( "listConditions: " + input.getConditions() );
        Map<String, String> map =   new HashMap<String, String>();
        map.put( MAP_CONSTANT_QUERYWHERE , queryWhere );
        map.put( MAP_CONSTANT_QUERYFROM , queryFrom );
        CaseAggregationCondition c = null;
        for ( int i=0; i< input.getConditions().size(); i++ )
        {
            c = input.getConditions().get( i );
            System.out.println("queryWhere: "+queryWhere);
            System.out.println("queryFrom: "+queryFrom);
            map = buildQueryForExpression( c, map, i );
            if( c.getNext() != null )
            {
                map.put( MAP_CONSTANT_QUERYWHERE , map.get( MAP_CONSTANT_QUERYWHERE ) + " " + c.getNext() + " " ) ;
            }
        }
        
        queryFrom =  map.get( MAP_CONSTANT_QUERYFROM );
        queryWhere = map.get( MAP_CONSTANT_QUERYWHERE );
        
        
        if( queryFrom.charAt(  queryFrom.length() - 1 ) == ',' )
        {
            queryFrom = StringUtils.left( queryFrom, queryFrom.length() - 1 );
        }
        
        queryWhere = queryWhere.replace( "$ENDDATE$",   "" + period.getEndDate()  );
        queryWhere = queryWhere.replace( "$STARTDATE$", "" + period.getStartDate() );
        queryWhere = queryWhere.replace( "$ORGUNITID$", "" + organisationUnit.getId() );
        
        System.out.println("queryyyyyyyyyyyy:\n "+queryFrom + queryWhere );
        System.out.println("=================================================");
        return queryFrom + queryWhere;
    }

    
    private String buildQuery( OrganisationUnit organisationUnit, Period period,
        CaseAggregationQuery input )
    {
        
        String function = "";
        if(  input.getFunction().equals( "COUNT" ) )
        {
            function = "COUNT(*)";
        }
        else if( input.getFunction().equals( "SUM" ))
        {
            function = "SUM(" + ALIAS_DATAVALUE + ".value)";
        }
        else 
        {
            // TODO: throw error : Invalid function
            return null;
        }
        
        String queryFrom = "Select " +  function + " from ";
        
        String queryWhere = "  ";

        Map<String, String> map =   new HashMap<String, String>();
        map.put( MAP_CONSTANT_QUERYWHERE , queryWhere );
        map.put( MAP_CONSTANT_QUERYFROM , queryFrom );
        CaseAggregationCondition c = null;
        for ( int i=0; i< input.getConditions().size(); i++ )
        {
            c = input.getConditions().get( i );
            System.out.println("queryWhere: "+queryWhere);
            System.out.println("queryFrom: "+queryFrom);
            map = buildQueryForExpression( c, map, i );
            if( c.getNext() != null )
            {
                String condition = map.get( MAP_CONSTANT_QUERYWHERE )  + c.getNext();
                map.put( MAP_CONSTANT_QUERYWHERE , condition ) ;
            }
        }
        queryFrom =  map.get( MAP_CONSTANT_QUERYFROM );
        queryWhere = " where "+ map.get( MAP_CONSTANT_QUERYWHERE );
        
        
        if( queryFrom.charAt(  queryFrom.length() - 1 ) == ',' )
        {
            queryFrom = StringUtils.left( queryFrom, queryFrom.length() - 1 );
            
        }
        
        queryWhere = queryWhere.replace( "$ENDDATE$",   "" + period.getEndDate()  );
        queryWhere = queryWhere.replace( "$STARTDATE$", "" + period.getStartDate() );
        queryWhere = queryWhere.replace( "$ORGUNITID$", "" + organisationUnit.getId() );      
 
        System.out.println("queryyyyyyyyyyyy 123:\n "+queryFrom + queryWhere );
        System.out.println("=================================================");

        return queryFrom + queryWhere;
    }
    
    
    private  Map<String, String> buildQueryForExpression( CaseAggregationCondition condition, Map<String, String> map,  int index)
    {
        if( map == null )
        {
            map = new HashMap<String, String>();
        }
        
        String[] arrExp = null;
        String[] arrIds = null;
        
        int programStageId = 0;
        int dataElementId = 0;
        int optionComboId = 0;
        int caseAttributeId = 0;
        String casePropertyName = null;
        
        String tmpFromQuery = "";
        String tmpWhereQuery = "";
                
        String curAliasDataValue = null;
        String curAliasProgramStageInstance = null;
        String curAliasPatientAttributeValue = null;
        String curAliasProgramInstance = null;
        String curAliasPatient = null;
        
        boolean singleExpression = false;
        
        List<String> listArg = parseExpression( condition.getLeftExpression());
        System.out.println("listArg: "+listArg);
        
        if ( listArg.size() > 1 )
        {
            // there are more than 1 arg in this expression -> using sub query
            tmpFromQuery = " ( Select $value$ from ";
        }
        else
        {
            singleExpression = true;
            tmpFromQuery = map.get( MAP_CONSTANT_QUERYFROM );
            tmpWhereQuery = map.get( MAP_CONSTANT_QUERYWHERE );
//            if( CaseAggregationCondition.CONDITION.equals( condition.getType())){
                tmpWhereQuery += " ( ";
//            }
        }
        String arg = null;
        for ( int i=0; i < listArg.size(); i++ )
        {
            arg = listArg.get(i);
            System.out.println("=========arg: "+arg);
            
            if ( isOperator( arg.charAt( 0 ) ) ) 
            {
                // if this is operator ( +, - , * , / ) then just append to the where clause
                // current not support .... 
                System.out.println("BBBBBBBBBBBBBBBBBB"+arg);
                map.put(MAP_CONSTANT_QUERYWHERE, map.get( MAP_CONSTANT_QUERYWHERE ) + arg );
            }
            else
            {
                // if this is an expression, then replace all needed parameters
                
                arrExp = StringUtils.split( arg, CaseAggregationCondition.ARGUMENT_IDENTIFIER );
                System.out.println("arrExp[0]: "+ arrExp[0]);
                if ( arrExp.length != 2 )
                {
                    //error
                    
                }else if ( CaseAggregationCondition.ARGUMENT_DATALEMENT.equals( arrExp[0] ) )
                {
                    arrIds = StringUtils.split( arrExp[1], CaseAggregationCondition.ARGUMENT_SPLITTER );

                    if ( arrIds.length != 3 )
                    {
                        // throw error
                        System.out.println( "Error: arrIds.length = " + arrIds.length );
                    }
                    
                    // get all parameters
                    programStageId = NumberUtils.toInt( arrIds[0], 0 );
                    dataElementId = NumberUtils.toInt( arrIds[1], 0 );
                    optionComboId = NumberUtils.toInt( arrIds[2], 0 );
                    
                    System.out.println("singleExpression: "+singleExpression);
                    
                    // build the where clause with all parameters
                    
                    tmpWhereQuery +=  "$ALIAS_PROGRAMSTAGEINSTANCE$.programStage.id = " + programStageId + " and " 
                                  + "$ALIAS_PROGRAMSTAGEINSTANCE$.id = $ALIAS_PATIENTDATAVALUE$.programStageInstance.id" + " and "
                                  + "$ALIAS_PATIENTDATAVALUE$.dataElement.id = " + dataElementId + " and "
                                  + "$ALIAS_PATIENTDATAVALUE$.optionCombo.id = " + optionComboId + " ";
                     
                    if( condition.getRightExpression() != null && condition.getOperator() != null )
                    {
                        // build query for the right expression of current condition.
                        
                        List<String> listRightArg = parseExpression( condition.getRightExpression());
                        
                        if( listRightArg.size() != 1 )
                        {
                            // current not supported ... [DE.1.2.3] > [DE.1.2.3] + 100 ???
                            continue;
                        }else 
                        {
                            arrExp = StringUtils.split( listRightArg.get( 0 ), CaseAggregationCondition.ARGUMENT_IDENTIFIER );
                            if( arrExp.length != 2 )
                            {
                                // error format . Correct  : [VL:123]
                                continue;
                            }
                            if( "VL".equals( arrExp[0] ) )
                            {
                                tmpWhereQuery += " and $ALIAS_PATIENTDATAVALUE$.value " + condition.getOperator() + " " + arrExp[1];
                            }else
                            {
                                // current not supported ... [DE:1.2.3] > [DE.2.3.1] ???
                                continue;
                            }
                        }
                        
                    }
                    if ( !singleExpression )
                    {
                        tmpWhereQuery += ")";
                    }
                }
                else if ( CaseAggregationCondition.ARGUMENT_CASE_ATTRIBUTE.equals( arrExp[0] ) )
                {
                    
                    caseAttributeId = NumberUtils.toInt( arrExp[1], 0 );
                    tmpWhereQuery += "$ALIAS_PATIENTATTRIBUTEVALUE$.patientAttribute.id = " + caseAttributeId + " and "
                                  + " $ALIAS_PATIENTATTRIBUTEVALUE$.patient.id = " + " $ALIAS_PROGRAMINSTANCE$.patient.id  and "
                                  + " $ALIAS_PROGRAMINSTANCE$.id = $ALIAS_PROGRAMSTAGEINSTANCE$.programInstance.id  " ; 

                    if( condition.getRightExpression() != null && condition.getOperator() != null )
                    {
                        // build query for the right expression of current condition.
                        
                        List<String> listRightArg = parseExpression( condition.getRightExpression());
                        
                        if( listRightArg.size() != 1 )
                        {
                            // current not supported ... [DE.1.2.3] > [DE.1.2.3] + 100 ???
                            continue;
                        }else 
                        {
                            arrExp = StringUtils.split( listRightArg.get( 0 ), CaseAggregationCondition.ARGUMENT_IDENTIFIER );
                            if( arrExp.length != 2 )
                            {
                                // error format . Correct  : [VL:123]
                                continue;
                            }
                            if( "VL".equals( arrExp[0] ) )
                            {
                                tmpWhereQuery += " and $ALIAS_PATIENTATTRIBUTEVALUE$.value " + condition.getOperator() + " " + arrExp[1];
                            }else
                            {
                                // current not supported ... [DE:1.2.3] > [DE.2.3.1] ???
                                continue;
                            }
                        }
                        
                    }
                  
                    if ( !singleExpression )
                    {
                        tmpWhereQuery += ")";
                    }
                }
                else if ( CaseAggregationCondition.ARGUMENT_CASE_PROPERTIES.equals( arrExp[0] ) )
                {
                    
                    casePropertyName = arrExp[1];
                    tmpWhereQuery   += " $ALIAS_PATIENT$.id = $ALIAS_PROGRAMINSTANCE$.patient.id and "
                                    + " $ALIAS_PROGRAMINSTANCE$.id = $ALIAS_PROGRAMSTAGEINSTANCE$.programInstance.id  "  ;
          
                    if( condition.getRightExpression() != null && condition.getOperator() != null )
                    {
                        // build query for the right expression of current condition.
                        
                        List<String> listRightArg = parseExpression( condition.getRightExpression());
                  
                        if( listRightArg.size() != 1 )
                        {
                            // current not supported ... [DE.1.2.3] > [DE.1.2.3] + 100 ???
                            return null;
                        }else 
                        {
                            arrExp = StringUtils.split( listRightArg.get( 0 ), CaseAggregationCondition.ARGUMENT_IDENTIFIER );
                            if( arrExp.length != 2 )
                            {
                                // error format . Correct  : [VL:123]
                                return null;
                            }
                            if( "VL".equals( arrExp[0] ) )
                            {
                                tmpWhereQuery += " and $ALIAS_PATIENT$." + casePropertyName + " " + condition.getOperator() + " " + arrExp[1];
                            }else
                            {
                                // current not supported ... [DE:1.2.3] > [DE.2.3.1] ???
                                return null; 
                            }
                        }
                        
                    }
                    if ( !singleExpression )
                    {
                        tmpWhereQuery += ")";
                    }
                }
            }         
         
            //-----------------------------------------------------------------------------------------
            // Replace alias
            //-----------------------------------------------------------------------------------------
            if ( singleExpression )
            {
                // add patientdatavalue  and programstageinstance alias to FROM clause, only once.
                
                if( map.get( MAP_CONSTANT_ALIAS_PROGRAMSTAGEINSTANCE ) == null )
                {
                    curAliasProgramStageInstance = " " + ALIAS_PROGRAMSTAGEINSTANCE; 
                    map.put( MAP_CONSTANT_ALIAS_PROGRAMSTAGEINSTANCE,  curAliasProgramStageInstance );
                    tmpFromQuery += ProgramStageInstance.class.getSimpleName() + " as " + curAliasProgramStageInstance + ",";
                    
                }else
                {
                    curAliasProgramStageInstance = map.get( MAP_CONSTANT_ALIAS_PROGRAMSTAGEINSTANCE );
                }
                
                //-----------------------------------------------------------------------
                
                if( programStageId != 0 && dataElementId != 0 && optionComboId != 0 )
                {
                    if( map.get( MAP_CONSTANT_ALIAS_DATAVALUE ) == null )
                    {
                        curAliasDataValue =  " " + ALIAS_DATAVALUE;
                        map.put( MAP_CONSTANT_ALIAS_DATAVALUE, curAliasDataValue ) ;
                        tmpFromQuery += PatientDataValue.class.getSimpleName() + " as " + curAliasDataValue + ",";
                        
                    }else 
                    {
                        curAliasDataValue = map.get( MAP_CONSTANT_ALIAS_DATAVALUE );
                    }
                }
                if( caseAttributeId != 0 )
                {
                    if( map.get( MAP_CONSTANT_ALIAS_PATIENT_ATTRIBUTE_VALUE ) == null )
                    {
                        curAliasPatientAttributeValue =  " " + ALIAS_PATIENT_ATTRIBUTE_VALUE;
                        map.put( MAP_CONSTANT_ALIAS_PATIENT_ATTRIBUTE_VALUE, curAliasPatientAttributeValue ) ;
                        tmpFromQuery += PatientAttributeValue.class.getSimpleName() + " as " + curAliasPatientAttributeValue + ",";
                        
                    }else 
                    {
                        curAliasPatientAttributeValue = map.get( MAP_CONSTANT_ALIAS_PATIENT_ATTRIBUTE_VALUE );
                    }
                    if( map.get( MAP_CONSTANT_ALIAS_PROGRAMINSTANCE ) == null )
                    {
                        curAliasProgramInstance =  " " + ALIAS_PROGRAMINSTANCE;
                        map.put( MAP_CONSTANT_ALIAS_PROGRAMINSTANCE, curAliasProgramInstance ) ;
                        tmpFromQuery += ProgramInstance.class.getSimpleName() + " as " + curAliasProgramInstance + ",";
                        
                    }else 
                    {
                        curAliasProgramInstance = map.get( MAP_CONSTANT_ALIAS_PROGRAMINSTANCE );
                    }
                }
                if( casePropertyName != null )
                {
                    if( map.get( MAP_CONSTANT_ALIAS_PATIENT ) == null )
                    {
                        curAliasPatient =  " " + ALIAS_PATIENT;
                        map.put( MAP_CONSTANT_ALIAS_PATIENT, curAliasPatient ) ;
                        tmpFromQuery += Patient.class.getSimpleName() + " as " + curAliasPatient + ",";
                        
                    }else 
                    {
                        curAliasPatientAttributeValue = map.get( MAP_CONSTANT_ALIAS_PATIENT );
                    }
                    if( map.get( MAP_CONSTANT_ALIAS_PROGRAMINSTANCE ) == null )
                    {
                        curAliasProgramInstance =  " " + ALIAS_PROGRAMINSTANCE;
                        map.put( MAP_CONSTANT_ALIAS_PROGRAMINSTANCE, curAliasProgramInstance ) ;
                        tmpFromQuery += ProgramInstance.class.getSimpleName() + " as " + curAliasProgramInstance + ",";
                        
                    }else 
                    {
                        curAliasProgramInstance = map.get( MAP_CONSTANT_ALIAS_PROGRAMINSTANCE );
                    }
                }
            }else if( !singleExpression )
            {
                // using subquery here , need to add some unique number to the alias ...
                if( programStageId != 0 && dataElementId != 0 && optionComboId != 0 )
                {
                    curAliasDataValue = " " + ALIAS_DATAVALUE + "_"+ index;
                    curAliasProgramStageInstance = " " + ALIAS_PROGRAMSTAGEINSTANCE + "_" + index;
                    
                    tmpFromQuery += PatientDataValue.class.getSimpleName() + " as " + curAliasDataValue + ",";
                    tmpFromQuery += ProgramStageInstance.class.getSimpleName() + " as " + curAliasProgramStageInstance + ",";
                }
                if( caseAttributeId != 0 )
                {
                    curAliasPatientAttributeValue = " " + ALIAS_PATIENT_ATTRIBUTE_VALUE + "_"+ index;
                    curAliasProgramStageInstance = " " + ALIAS_PROGRAMSTAGEINSTANCE + "_" + index;
                    curAliasProgramInstance = " " + ALIAS_PROGRAMINSTANCE + "_" + index;
                    
                    tmpFromQuery += ProgramStageInstance.class.getSimpleName() + " as " + curAliasProgramStageInstance + ",";
                    tmpFromQuery += PatientAttributeValue.class.getSimpleName() + " as " + curAliasPatientAttributeValue + ",";
                    tmpFromQuery += ProgramInstance.class.getSimpleName() + " as " + curAliasProgramInstance + ",";
                }
                if( casePropertyName != null )
                {
                    curAliasPatient = " " + ALIAS_PATIENT + "_" + index;
                    curAliasProgramInstance = " " + ALIAS_PROGRAMINSTANCE + "_" + index;
                    curAliasProgramStageInstance = " " + ALIAS_PROGRAMSTAGEINSTANCE + "_" + index;
                    
                    tmpFromQuery += Patient.class.getSimpleName() + " as " + curAliasPatient + ", ";
                    tmpFromQuery += ProgramStageInstance.class.getSimpleName() + " as " + curAliasProgramStageInstance + ",";
                    tmpFromQuery += ProgramInstance.class.getSimpleName() + " as " + curAliasProgramInstance + ",";
                    
                }
            }
            //   Add org Unit and period condition 
            if( !tmpWhereQuery.contains( "$STARTDATE$" )  )
            {    
                tmpWhereQuery += " and $ALIAS_PATIENTDATAVALUE$.organisationUnit.id =  $ORGUNITID$   and "
                              + "$ALIAS_PROGRAMSTAGEINSTANCE$.executionDate between  '$STARTDATE$' and ' $ENDDATE$ ' ";
            }
            if( curAliasProgramStageInstance != null    ) tmpWhereQuery = tmpWhereQuery.replace( "$ALIAS_PROGRAMSTAGEINSTANCE$", curAliasProgramStageInstance );
            if( curAliasDataValue != null               ) tmpWhereQuery = tmpWhereQuery.replace( "$ALIAS_PATIENTDATAVALUE$", curAliasDataValue );
            if( curAliasPatient != null                 ) tmpWhereQuery = tmpWhereQuery.replace( "$ALIAS_PATIENT$", curAliasPatient );
            if( curAliasProgramInstance != null         ) tmpWhereQuery = tmpWhereQuery.replace( "$ALIAS_PROGRAMINSTANCE$", curAliasProgramInstance );
            if( curAliasPatientAttributeValue != null   ) tmpWhereQuery = tmpWhereQuery.replace( "$ALIAS_PATIENTATTRIBUTEVALUE$", curAliasPatientAttributeValue );
            
//            if( CaseAggregationCondition.CONDITION.equals( condition.getType())){
                tmpWhereQuery += " ) ";
//            }
        }
        
        map.put( MAP_CONSTANT_QUERYWHERE , tmpWhereQuery );
        map.put( MAP_CONSTANT_QUERYFROM , tmpFromQuery );
        
        return map;
    }


    private boolean isOperator( char input )
    {
        return '+' == input || '-' == input || '*' == input || '/' == input;
    }
    
    public static void main( String[] args )
    {

        //String string = "SUM@ COND{ ([DE:8.57.1]) < ([VL:2500]) } AND COND{ ([CP:gender]) = ([VL:'M']) }   ";
        
        //String string = "COUNT@ COND{ ( [DE:1.13.1] ) < ( [VL:2500] ) } AND COND{ ( [DE:1.16.1] ) = ( [VL:2500] ) } ";
        // String string = "COUNT@ COND{ ( [CP:GENDER] ) < ( [VL:23] ) } ";
        //String string = "COUNT@ COND{ ( [CP:GENDER] ) = ( [VL:'F'] ) } AND COND{ ( [DE:1.13.1] ) < ( [VL:2500] ) }";
        String string = "COUNT@ SCOND{ ( [DE:8.75.1] ) } AND COND{ ( [CP:gender] ) = ( [VL:'F'] ) }";
        
        DefaultCaseAggregationMappingService test = new DefaultCaseAggregationMappingService();
        CaseAggregationQuery query = test.scan( string );
        System.out.println( test.buildQuery(  null, null, query ) );
        
        
    }

}
