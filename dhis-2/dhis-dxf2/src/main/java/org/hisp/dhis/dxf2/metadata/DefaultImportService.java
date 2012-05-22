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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
public class DefaultImportService
    implements ImportService
{
    private static final Log log = LogFactory.getLog( DefaultImportService.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired( required = false )
    private Set<Importer> importerClasses = new HashSet<Importer>();

    @Autowired
    private ObjectBridge objectBridge;

    @Autowired
    private HibernateCacheManager cacheManager;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SessionFactory sessionFactory;

    //-------------------------------------------------------------------------------------------------------
    // ImportService Implementation
    //-------------------------------------------------------------------------------------------------------

    @Override
    public ImportSummary importMetaData( MetaData metaData )
    {
        return importMetaData( metaData, ImportOptions.getDefaultImportOptions() );
    }

    @Override
    public ImportSummary importMetaData( MetaData metaData, ImportOptions importOptions )
    {
        ImportSummary importSummary = new ImportSummary();
        objectBridge.init();

        if ( importOptions.isDryRun() )
        {
            objectBridge.setWriteEnabled( false );
        }

        Date startDate = new Date();

        log.info( "User '" + currentUserService.getCurrentUsername() + "' started import at " + startDate );

        doImport( metaData.getSqlViews(), importOptions, importSummary );
        doImport( metaData.getConcepts(), importOptions, importSummary );
        doImport( metaData.getConstants(), importOptions, importSummary );
        doImport( metaData.getDocuments(), importOptions, importSummary );
        doImport( metaData.getOptionSets(), importOptions, importSummary );
        doImport( metaData.getAttributeTypes(), importOptions, importSummary );

        doImport( metaData.getOrganisationUnits(), importOptions, importSummary );
        doImport( metaData.getOrganisationUnitLevels(), importOptions, importSummary );
        doImport( metaData.getOrganisationUnitGroups(), importOptions, importSummary );
        doImport( metaData.getOrganisationUnitGroupSets(), importOptions, importSummary );

        // doImport( metaData.getUsers(), importOptions, importSummary );
        // doImport( metaData.getUserGroups(), importOptions, importSummary );
        // doImport( metaData.getUserAuthorityGroups(), importOptions, importSummary );

        doImport( metaData.getCategoryOptions(), importOptions, importSummary );
        doImport( metaData.getCategories(), importOptions, importSummary );
        doImport( metaData.getCategoryCombos(), importOptions, importSummary );
        doImport( metaData.getCategoryOptionCombos(), importOptions, importSummary );

        doImport( metaData.getDataElements(), importOptions, importSummary );
        doImport( metaData.getDataElementGroups(), importOptions, importSummary );
        doImport( metaData.getDataElementGroupSets(), importOptions, importSummary );

        doImport( metaData.getIndicatorTypes(), importOptions, importSummary );
        doImport( metaData.getIndicators(), importOptions, importSummary );
        doImport( metaData.getIndicatorGroups(), importOptions, importSummary );
        doImport( metaData.getIndicatorGroupSets(), importOptions, importSummary );

        doImport( metaData.getValidationRules(), importOptions, importSummary );
        doImport( metaData.getValidationRuleGroups(), importOptions, importSummary );

        // doImport( metaData.getMessageConversations(), importOptions, importSummary );

        doImport( metaData.getDataDictionaries(), importOptions, importSummary );
        doImport( metaData.getDataSets(), importOptions, importSummary );
        doImport( metaData.getSections(), importOptions, importSummary );

        doImport( metaData.getReportTables(), importOptions, importSummary );
        doImport( metaData.getReports(), importOptions, importSummary );
        doImport( metaData.getCharts(), importOptions, importSummary );

        doImport( metaData.getMaps(), importOptions, importSummary );
        doImport( metaData.getMapLegends(), importOptions, importSummary );
        doImport( metaData.getMapLegendSets(), importOptions, importSummary );
        doImport( metaData.getMapLayers(), importOptions, importSummary );

        if ( importOptions.isDryRun() )
        {
            sessionFactory.getCurrentSession().clear();
        }

        cacheManager.clearCache();
        objectBridge.destroy();

        Date endDate = new Date();
        log.info( "Finished import at " + endDate );

        return importSummary;
    }

    //-------------------------------------------------------------------------------------------------------
    // Helpers
    //-------------------------------------------------------------------------------------------------------

    private <T> Importer<T> findImporterClass( List<?> clazzes )
    {
        if ( !clazzes.isEmpty() )
        {
            return findImporterClass( clazzes.get( 0 ).getClass() );
        }

        return null;
    }

    private <T> Importer<T> findImporterClass( Class<?> clazz )
    {
        for ( Importer<T> i : importerClasses )
        {
            if ( i.canHandle( clazz ) )
            {
                return i;
            }
        }

        return null;
    }

    private <T> void doImport( List<T> objects, ImportOptions importOptions, ImportSummary importSummary )
    {
        if ( !objects.isEmpty() )
        {
            Importer<T> importer = findImporterClass( objects );

            if ( importer != null )
            {
                List<ImportConflict> importConflicts = importer.importObjects( objects, importOptions );

                importSummary.getConflicts().addAll( importConflicts );
                // importSummary.getCounts().add( count ); //FIXME
            }
            else
            {
                log.info( "Importer for object of type " + objects.get( 0 ).getClass().getSimpleName() + " not found." );
            }
        }
    }
}
