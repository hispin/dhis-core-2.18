package org.hisp.dhis.de.action;

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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.de.state.SelectedStateManager;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.minmax.validation.MinMaxValuesGenerationService;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

/**
 * @author Margrethe Store
 * @version $Id: MinMaxGeneratingAction.java 5568 2008-08-21 13:47:11Z larshelg $
 * @version $Id: MinMaxGeneratingAction.java 2010-04-16 13:47:11Z Chau Thu Tran $
 */
public class MinMaxGeneratingAction implements Action {
    private static final Log log = LogFactory.getLog( MinMaxGeneratingAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private MinMaxValuesGenerationService minMaxValuesGenerationService;

    public void setMinMaxValuesGenerationService( MinMaxValuesGenerationService minMaxValuesGenerationService )
    {
        this.minMaxValuesGenerationService = minMaxValuesGenerationService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<MinMaxDataElement> minMaxDataElements;

    public List<MinMaxDataElement> getMinMaxDataElements()
    {
        return minMaxDataElements;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        log.info( "Starting min-max limits generation" );

        minMaxDataElements = new ArrayList<MinMaxDataElement>();

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        Double factor = (Double) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_FACTOR_OF_DEVIATION,
            2.0 );

        // Get min/max values for dataelements into dataset
        Collection<MinMaxDataElement> minMaxDataElements = (Collection<MinMaxDataElement>) minMaxValuesGenerationService
            .getMinMaxValues( organisationUnit, dataSet.getDataElements(), factor );

        // Save min / max value
        for ( MinMaxDataElement minMaxDataElement : minMaxDataElements )
        {
            MinMaxDataElement minMaxValue = minMaxDataElementService.getMinMaxDataElement( minMaxDataElement
                .getSource(), minMaxDataElement.getDataElement(), minMaxDataElement.getOptionCombo() );

            if ( minMaxValue != null )
            {
                minMaxValue.setMax( minMaxDataElement.getMax() );
                minMaxValue.setMin( minMaxDataElement.getMin() );
                minMaxDataElementService.updateMinMaxDataElement( minMaxValue );
            }
            else
            {
                minMaxDataElement.setGenerated( true );
                minMaxDataElementService.addMinMaxDataElement( minMaxDataElement );
            }
        }

        log.info( "Generated min-max limits" );

        return SUCCESS;
    }
    
}
