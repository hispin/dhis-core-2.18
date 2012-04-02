package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2012, University of Oslo
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

import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.sqlview.SqlViewService;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
@Service
public class DefaultImportService
    implements ImportService
{
    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------
/*
    @Autowired
    private AttributeService attributeService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private ValidationRuleService validationRuleService;

    @Autowired
    private SqlViewService sqlViewService;

    @Autowired
    private ChartService chartService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportTableService reportTableService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private DataDictionaryService dataDictionaryService;
*/

    //-------------------------------------------------------------------------------------------------------
    // ImportService Implementation
    //-------------------------------------------------------------------------------------------------------

    @Override
    public ImportSummary importDxf2( DXF2 dxf2 )
    {
        return importDxf2WithImportOptions( dxf2, ImportOptions.getDefaultImportOptions() );
    }

    @Override
    public ImportSummary importDxf2WithImportOptions( DXF2 dxf2, ImportOptions importOptions )
    {
        return new ImportSummary();
    }
}
