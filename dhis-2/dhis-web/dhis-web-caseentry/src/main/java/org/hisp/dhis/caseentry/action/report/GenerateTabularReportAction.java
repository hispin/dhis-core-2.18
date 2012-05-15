/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.caseentry.action.report;

import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_DATA_ELEMENT;
import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_IDENTIFIER_TYPE;
import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_META_DATA;
import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_PATIENT_ATTRIBUTE;
import static org.hisp.dhis.patientreport.PatientTabularReport.VALUE_TYPE_OPTION_SET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;

/**
 * @author Chau Thu Tran
 * 
 * @version $GenerateTabularReportAction.java Feb 29, 2012 10:15:05 AM$
 */
public class GenerateTabularReportAction
    extends ActionPagingSupport<ProgramStageInstance>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private PatientIdentifierTypeService identifierTypeService;

    public void setIdentifierTypeService( PatientIdentifierTypeService identifierTypeService )
    {
        this.identifierTypeService = identifierTypeService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer orgunitId;

    public void setOrgunitId( Integer orgunitId )
    {
        this.orgunitId = orgunitId;
    }

    private Integer programStageId;

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private List<String> values = new ArrayList<String>();

    public List<String> getValues()
    {
        return values;
    }

    private List<String> searchingValues = new ArrayList<String>();

    public void setSearchingValues( List<String> searchingValues )
    {
        this.searchingValues = searchingValues;
    }

    private boolean orderByOrgunitAsc;

    public void setOrderByOrgunitAsc( boolean orderByOrgunitAsc )
    {
        this.orderByOrgunitAsc = orderByOrgunitAsc;
    }

    private boolean orderByExecutionDateByAsc;

    public void setOrderByExecutionDateByAsc( boolean orderByExecutionDateByAsc )
    {
        this.orderByExecutionDateByAsc = orderByExecutionDateByAsc;
    }

    private Integer level;

    public void setLevel( Integer level )
    {
        this.level = level;
    }

    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private List<String> valueTypes = new ArrayList<String>();

    public List<String> getValueTypes()
    {
        return valueTypes;
    }

    private List<String> fixedAttributes = new ArrayList<String>();

    public void setFixedAttributes( List<String> fixedAttributes )
    {
        this.fixedAttributes = fixedAttributes;
    }

    private Map<Integer, List<String>> mapSuggestedValues = new HashMap<Integer, List<String>>();

    public Map<Integer, List<String>> getMapSuggestedValues()
    {
        return mapSuggestedValues;
    }

    private Map<Integer, String> searchingIdenKeys = new HashMap<Integer, String>();

    private Map<Integer, String> searchingAttrKeys = new HashMap<Integer, String>();

    private Map<Integer, String> searchingDEKeys = new HashMap<Integer, String>();

    private List<Boolean> hiddenCols = new ArrayList<Boolean>();

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get orgunitIds
        // ---------------------------------------------------------------------

        OrganisationUnit selectedOrgunit = organisationUnitService.getOrganisationUnit( orgunitId );

        Set<Integer> upperOrgunitIds = new HashSet<Integer>();

        Set<Integer> bottomOrgunitIds = new HashSet<Integer>();

        if ( facilityLB.equals( "selected" ) )
        {
            upperOrgunitIds.add( orgunitId );
        }
        else if ( facilityLB.equals( "childrenOnly" ) )
        {
            Set<OrganisationUnit> children = selectedOrgunit.getChildren();

            for ( OrganisationUnit child : children )
            {
                upperOrgunitIds.add( child.getId() );
            }
        }
        else
        {
            OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy();

            Set<Integer> childOrgUnitIdentifiers = hierarchy.getChildren( selectedOrgunit.getId() );
            upperOrgunitIds.add( orgunitId );
            upperOrgunitIds.addAll( childOrgUnitIdentifiers );
            
            // Get bottom orgunit
            int maxLevel = organisationUnitService.getMaxOfOrganisationUnitLevels();
            bottomOrgunitIds = getOrganisationUnitsAtLevel( selectedOrgunit, maxLevel - 1 );
            upperOrgunitIds.removeAll( bottomOrgunitIds );
        }

        // ---------------------------------------------------------------------
        // Get program-stage, start-date, end-date
        // ---------------------------------------------------------------------

        ProgramStage programStage = programStageService.getProgramStage( programStageId );

        Date startValue = format.parseDate( startDate );

        Date endValue = format.parseDate( endDate );

        // ---------------------------------------------------------------------
        // Get DE searching-keys
        // ---------------------------------------------------------------------

        getParams();

        // ---------------------------------------------------------------------
        // Generate tabular report
        // ---------------------------------------------------------------------

        if ( type == null )
        {
            Set<Integer> orgunitIds = new HashSet<Integer>();
            orgunitIds.addAll( upperOrgunitIds );
            orgunitIds.addAll( bottomOrgunitIds );

            int totalRecords = programStageInstanceService.countProgramStageInstances( programStage, searchingIdenKeys,
                searchingAttrKeys, searchingDEKeys, orgunitIds, startValue, endValue );

            total = getNumberOfPages( totalRecords );

            this.paging = createPaging( totalRecords );

            grid = programStageInstanceService.getTabularReport( programStage, hiddenCols, identifierTypes,
                fixedAttributes, patientAttributes, dataElements, searchingIdenKeys, searchingAttrKeys,
                searchingDEKeys, upperOrgunitIds, bottomOrgunitIds, level, startValue, endValue, orderByOrgunitAsc,
                orderByExecutionDateByAsc, paging.getStartPos(), paging.getPageSize(), format, i18n );

            return SUCCESS;
        }

        grid = programStageInstanceService.getTabularReport( programStage, hiddenCols, identifierTypes,
            fixedAttributes, patientAttributes, dataElements, searchingIdenKeys, searchingAttrKeys, searchingDEKeys,
            upperOrgunitIds, bottomOrgunitIds, level, startValue, endValue, orderByOrgunitAsc,
            orderByExecutionDateByAsc, format, i18n );

        return type;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    public int getNumberOfPages( int totalRecord )
    {
        int pageSize = this.getDefaultPageSize();
        return (totalRecord % pageSize == 0) ? (totalRecord / pageSize) : (totalRecord / pageSize + 1);
    }

    private void getParams()
    {
        // ---------------------------------------------------------------------
        // Get Patient-Identifier searching-keys
        // ---------------------------------------------------------------------
        int index = 0;
        for ( String searchingValue : searchingValues )
        {
            String[] infor = searchingValue.split( "_" );
            String objectType = infor[0];
            int objectId = Integer.parseInt( infor[1] );

            if ( objectType.equals( PREFIX_META_DATA ) )
            {
                hiddenCols.add( Boolean.parseBoolean( infor[2] ) );
            }
            else if ( objectType.equals( PREFIX_IDENTIFIER_TYPE ) )
            {
                PatientIdentifierType identifierType = identifierTypeService.getPatientIdentifierType( objectId );
                identifierTypes.add( identifierType );

                // Get value-type && suggested-values
                valueTypes.add( identifierType.getType() );
                hiddenCols.add( Boolean.parseBoolean( infor[2] ) );

                // Get searching-value
                if ( infor.length == 4 )
                {
                    searchingIdenKeys.put( objectId, infor[3].trim() );
                    values.add( infor[3].trim() );
                }
                else
                {
                    values.add( "" );
                }
                index++;
            }
            else if ( objectType.equals( PREFIX_PATIENT_ATTRIBUTE ) )
            {
                PatientAttribute attribute = patientAttributeService.getPatientAttribute( objectId );
                patientAttributes.add( attribute );

                // Get value-type && suggested-values
                valueTypes.add( attribute.getValueType() );
                mapSuggestedValues.put( index, getSuggestedAttrValues( attribute ) );
                hiddenCols.add( Boolean.parseBoolean( infor[2] ) );

                // Get searching-value
                if ( infor.length == 4 )
                {
                    searchingAttrKeys.put( objectId, infor[3].trim() );
                    String value = infor[3].trim();
                    if ( attribute.getValueType().equals( PatientAttribute.TYPE_BOOL ) )
                    {
                        value = (value.indexOf( i18n.getString( "yes" ) ) != -1) ? "true" : "false";
                    }
                    values.add( value );
                }
                else
                {
                    values.add( "" );
                }
                index++;
            }
            else if ( objectType.equals( PREFIX_DATA_ELEMENT ) )
            {
                DataElement dataElement = dataElementService.getDataElement( objectId );
                dataElements.add( dataElement );

                // Get value-type && suggested-values
                String valueType = (dataElement.getOptionSet() != null) ? VALUE_TYPE_OPTION_SET : dataElement.getType();
                valueTypes.add( valueType );
                mapSuggestedValues.put( index, getSuggestedDEValues( dataElement ) );
                hiddenCols.add( Boolean.parseBoolean( infor[2] ) );

                if ( infor.length == 4 )
                {
                    String value = infor[3].trim();
                    if ( dataElement.getType().equals( DataElement.VALUE_TYPE_BOOL ) )
                    {
                        int startIndx = value.indexOf( '\'' ) + 1;
                        int endIndx = value.lastIndexOf( '\'' );
                        String key = value.substring( startIndx, endIndx );
                                              
                        value = (key.equals(i18n.getString( "yes" ))) ? value.replace( key, "true" ) : value.replace( key, "false" );
                    }                   
                    searchingDEKeys.put( objectId, value );
                    values.add( value );
                }
                else
                {
                    values.add( "" );
                }
                index++;
            }

        }
    }

    private List<String> getSuggestedAttrValues( PatientAttribute patientAttribute )
    {
        List<String> values = new ArrayList<String>();
        String valueType = patientAttribute.getValueType();

        if ( valueType.equals( PatientAttribute.TYPE_BOOL ) )
        {
            values.add( i18n.getString( "yes" ) );
            values.add( i18n.getString( "no" ) );
        }
        else if ( valueType.equals( PatientAttribute.TYPE_COMBO ) )
        {
            for ( PatientAttributeOption attributeOption : patientAttribute.getAttributeOptions() )
            {
                values.add( attributeOption.getName() );
            }
        }

        return values;
    }

    private List<String> getSuggestedDEValues( DataElement dataElement )
    {
        List<String> values = new ArrayList<String>();
        String valueType = dataElement.getType();

        if ( valueType.equals( DataElement.VALUE_TYPE_BOOL ) )
        {
            values.add( i18n.getString( "yes" ) );
            values.add( i18n.getString( "no" ) );
        }
        else if ( dataElement.getOptionSet() != null )
        {
            values = dataElement.getOptionSet().getOptions();
        }

        return values;
    }

    private Set<Integer> getOrganisationUnitsAtLevel( OrganisationUnit orgunit, int level )
    {
        Set<Integer> result = new HashSet<Integer>();

        if ( level < 1 )
        {
            throw new IllegalArgumentException( "Level must be greater than zero" );
        }

        if ( level == 1 )
        {
            result.add( orgunit.getId() );
            return result;
        }

        for ( OrganisationUnit root : orgunit.getChildren() )
        {
            addOrganisationUnitChildrenAtLevel( root, 2, level, result );
        }

        return result;
    }

    private void addOrganisationUnitChildrenAtLevel( OrganisationUnit parent, int currentLevel, int targetLevel,
        Set<Integer> result )
    {
        if ( currentLevel == targetLevel )
        {
            Collection<OrganisationUnit> orgunits = parent.getChildren();
            for ( OrganisationUnit orgunit : orgunits )
            {
                result.add( orgunit.getId() );
            }
        }
        else
        {
            for ( OrganisationUnit child : parent.getChildren() )
            {
                addOrganisationUnitChildrenAtLevel( child, currentLevel + 1, targetLevel, result );
            }
        }
    }

}
