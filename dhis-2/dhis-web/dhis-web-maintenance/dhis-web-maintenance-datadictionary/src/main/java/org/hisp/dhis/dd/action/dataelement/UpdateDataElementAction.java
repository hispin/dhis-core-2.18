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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.system.util.ConversionUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 */
public class UpdateDataElementAction
    implements Action
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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String alternativeName;

    public void setAlternativeName( String alternativeName )
    {
        this.alternativeName = alternativeName;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private Boolean active;

    public void setActive( Boolean active )
    {
        this.active = active;
    }

    private String domainType;

    public void setDomainType( String domainType )
    {
        this.domainType = domainType;
    }
    
    private String numberType;

    public void setNumberType( String numberType )
    {
        this.numberType = numberType;
    }
    
    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }
    
    private String aggregationOperator;

    public void setAggregationOperator( String aggregationOperator )
    {
        this.aggregationOperator = aggregationOperator;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private Collection<String> aggregationLevels;

    public void setAggregationLevels( Collection<String> aggregationLevels )
    {
        this.aggregationLevels = aggregationLevels;
    }

    private Integer selectedCategoryComboId;

    public void setSelectedCategoryComboId( Integer selectedCategoryComboId )
    {
        this.selectedCategoryComboId = selectedCategoryComboId;
    }

    private Boolean zeroIsSignificant;

    public void setZeroIsSignificant( Boolean zeroIsSignificant )
    {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        if ( alternativeName != null && alternativeName.trim().length() == 0 )
        {
            alternativeName = null;
        }

        if ( code != null && code.trim().length() == 0 )
        {
            code = null;
        }

        if ( description != null && description.trim().length() == 0 )
        {
            description = null;
        }

        // ---------------------------------------------------------------------
        // Update data element
        // ---------------------------------------------------------------------

        DataElement dataElement = dataElementService.getDataElement( id );

        DataElementCategoryCombo categoryCombo = dataElementCategoryService
            .getDataElementCategoryCombo( selectedCategoryComboId );

        dataElement.setName( name );
        dataElement.setAlternativeName( alternativeName );
        dataElement.setShortName( shortName );
        dataElement.setCode( code );
        dataElement.setDescription( description );
        dataElement.setActive( active );
        dataElement.setDomainType( domainType );
        dataElement.setType( valueType );
        dataElement.setNumberType( numberType );
        dataElement.setAggregationOperator( aggregationOperator );
        dataElement.setUrl( url );
        dataElement.setCategoryCombo( categoryCombo );
        dataElement.setAggregationLevels( new ArrayList<Integer>( ConversionUtils.getIntegerCollection( aggregationLevels ) ) );
        dataElement.setZeroIsSignificant( zeroIsSignificant );
        
        Set<DataSet> dataSets = dataElement.getDataSets();
        for ( DataSet dataSet : dataSets )
        {
            if ( dataSet.getMobile() != null && dataSet.getMobile() )
            {
                dataSet.setVersion( dataSet.getVersion() + 1 );
                dataSetService.updateDataSet( dataSet );
            }
        }

        dataElementService.updateDataElement( dataElement );

        return SUCCESS;
    }
}
