package org.hisp.dhis.dd.action.dataelement;

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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Hans S. Toemmerholt
 * @version $Id: GetDataElementAction.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class ShowUpdateDataElementFormAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private DataElement dataElement;

    public DataElement getDataElement()
    {
        return dataElement;
    }

    private CalculatedDataElement calculatedDataElement;

    public CalculatedDataElement getCalculatedDataElement()
    {
        return calculatedDataElement;
    }

    private Collection<DataElementGroup> dataElementGroups;

    public Collection<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    private Map<String, Double> factorMap;

    public Map<String, Double> getFactorMap()
    {
        return factorMap;
    }

    private Collection<DataElementOperand> operands = new ArrayList<DataElementOperand>();

    public Collection<DataElementOperand> getOperands()
    {
        return operands;
    }

    private final static int ALL = 0;

    public int getALL()
    {
        return ALL;
    }

    private List<DataElementCategoryCombo> dataElementCategoryCombos;

    public List<DataElementCategoryCombo> getDataElementCategoryCombos()
    {
        return dataElementCategoryCombos;
    }

    private List<OrganisationUnitLevel> organisationUnitLevels;

    public List<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        return organisationUnitLevels;
    }

    private List<OrganisationUnitLevel> aggregationLevels = new ArrayList<OrganisationUnitLevel>();

    public List<OrganisationUnitLevel> getAggregationLevels()
    {
        return aggregationLevels;
    }

    private DataElementCategoryCombo defaultCategoryCombo;

    public DataElementCategoryCombo getDefaultCategoryCombo()
    {
        return defaultCategoryCombo;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        defaultCategoryCombo = dataElementCategoryService
            .getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        dataElementCategoryCombos = new ArrayList<DataElementCategoryCombo>( dataElementCategoryService
            .getAllDataElementCategoryCombos() );

        dataElement = dataElementService.getDataElement( id );

        organisationUnitLevels = organisationUnitService.getOrganisationUnitLevels();

        List<OrganisationUnitLevel> filledOrganisationUnitLevels = organisationUnitService
            .getFilledOrganisationUnitLevels();

        for ( Integer level : dataElement.getAggregationLevels() )
        {
            aggregationLevels.add( getOrganisationUnitLevel( level, filledOrganisationUnitLevels ) );
        }

        organisationUnitLevels.removeAll( aggregationLevels );

        if ( dataElement != null && dataElement instanceof CalculatedDataElement )
        {
            calculatedDataElement = (CalculatedDataElement) dataElement;
            dataElementGroups = dataElementService.getAllDataElementGroups();

            Collection<String> operandIds = new ArrayList<String>();

            operandIds = dataElementService.getOperandIds( calculatedDataElement );
            factorMap = dataElementService.getOperandFactors( calculatedDataElement );

            for ( String operandId : operandIds )
            {
                String dataElementIdString = operandId.substring( 0, operandId.indexOf( SEPARATOR ) );
                String optionComboIdString = operandId.substring( operandId.indexOf( SEPARATOR ) + 1, operandId
                    .length() );

                DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementIdString ) );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( Integer.parseInt( optionComboIdString ) );

                DataElementOperand operand = new DataElementOperand( dataElement.getId(), optionCombo.getId(),
                    dataElement.getName() + optionCombo.getName() );

                operands.add( operand );
            }
        }

        return SUCCESS;
    }

    private OrganisationUnitLevel getOrganisationUnitLevel( Integer level,
        List<OrganisationUnitLevel> organisationUnitLevels )
    {
        for ( OrganisationUnitLevel organisationUnitLevel : organisationUnitLevels )
        {
            if ( level.equals( organisationUnitLevel.getLevel() ) )
                return organisationUnitLevel;
        }

        return null;
    }
}
