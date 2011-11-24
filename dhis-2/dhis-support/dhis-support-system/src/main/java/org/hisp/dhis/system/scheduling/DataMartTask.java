package org.hisp.dhis.system.scheduling;

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

import static org.hisp.dhis.options.SystemSettingManager.DEFAULT_SCHEDULED_PERIOD_TYPES;
import static org.hisp.dhis.options.SystemSettingManager.KEY_SCHEDULED_PERIOD_TYPES;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * @author Lars Helge Overland
 */
public class DataMartTask
    implements Runnable
{
    private DataMartService dataMartService;

    private DataSetCompletenessService completenessService;
    
    private DataElementService dataElementService;
    
    private IndicatorService indicatorService;
    
    private OrganisationUnitService organisationUnitService;

    private DataSetService dataSetService;
    
    private SystemSettingManager systemSettingManager;
    
    public DataMartTask( DataMartService dataMartService, DataSetCompletenessService completenessService, 
        DataElementService dataElementService, IndicatorService indicatorService, 
        OrganisationUnitService organisationUnitService, DataSetService dataSetService, SystemSettingManager systemSettingManager )
    {
        this.dataMartService = dataMartService;
        this.completenessService = completenessService;
        this.dataElementService = dataElementService;
        this.indicatorService = indicatorService;
        this.organisationUnitService = organisationUnitService;
        this.dataSetService = dataSetService;
        this.systemSettingManager = systemSettingManager;
    }
    
    @Override
    @SuppressWarnings("unchecked")    
    public void run()
    {
        Collection<Integer> dataElementIds = ConversionUtils.getIdentifiers( DataElement.class, dataElementService.getAllDataElements() );
        Collection<Integer> indicatorIds = ConversionUtils.getIdentifiers( Indicator.class, indicatorService.getAllIndicators() );
        Collection<Integer> organisationUnitIds = ConversionUtils.getIdentifiers( OrganisationUnit.class, organisationUnitService.getAllOrganisationUnits() );
        Collection<Integer> dataSetIds = ConversionUtils.getIdentifiers( DataSet.class, dataSetService.getAllDataSets() );
        
        Set<String> periodTypes = (Set<String>) systemSettingManager.getSystemSetting( KEY_SCHEDULED_PERIOD_TYPES, DEFAULT_SCHEDULED_PERIOD_TYPES );
        
        RelativePeriods relatives = new RelativePeriods().getRelativePeriods( periodTypes );
        
        dataMartService.export( dataElementIds, indicatorIds, new HashSet<Integer>(), organisationUnitIds, relatives, true );
        completenessService.exportDataSetCompleteness( dataSetIds, relatives, organisationUnitIds ); 
    }
}
