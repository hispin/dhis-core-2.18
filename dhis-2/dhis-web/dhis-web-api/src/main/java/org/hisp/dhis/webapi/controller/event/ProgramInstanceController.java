package org.hisp.dhis.webapi.controller.event;

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

import com.google.common.collect.Lists;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.fieldfilter.FieldFilterService;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@RestController
@RequestMapping( value = "/programInstances" )
public class ProgramInstanceController
{
    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    protected ContextService contextService;

    @Autowired
    protected FieldFilterService fieldFilterService;

    @RequestMapping( method = RequestMethod.GET )
    public RootNode getProgramInstances(
        @RequestParam( value = "program" ) String programId,
        @RequestParam( value = "orgUnit" ) List<String> orgUnits,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Map<String, String> parameters, HttpServletResponse response
    )
    {
        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );

        if ( fields.isEmpty() )
        {
            fields.add( ":identifiable" );
        }

        WebOptions options = new WebOptions( parameters );
        WebMetaData metaData = new WebMetaData();

        Program program = manager.get( Program.class, programId );

        if ( program == null )
        {
            ContextUtils.conflictResponse( response, "program parameter is required." );
        }

        if ( orgUnits.isEmpty() )
        {
            ContextUtils.conflictResponse( response, "At least one orgUnit is required." );
        }

        List<OrganisationUnit> organisationUnits = new ArrayList<>( manager.getByUid( OrganisationUnit.class, orgUnits ) );
        List<Integer> identifiers = IdentifiableObjectUtils.getIdentifiers( organisationUnits );

        List<ProgramInstance> programInstances;

        if ( options.hasPaging() )
        {
            int count = programInstanceService.countProgramInstances( program, identifiers, startDate, endDate );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            programInstances = new ArrayList<>( programInstanceService.getProgramInstances( program, identifiers,
                startDate, endDate, pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            programInstances = new ArrayList<>( programInstanceService.getProgramInstances( program, identifiers,
                startDate, endDate, 0, Integer.MAX_VALUE ) );
        }

        RootNode rootNode = new RootNode( "metadata" );
        rootNode.setDefaultNamespace( DxfNamespaces.DXF_2_0 );
        rootNode.setNamespace( DxfNamespaces.DXF_2_0 );

        if ( options.hasPaging() )
        {
            ComplexNode pagerNode = rootNode.addChild( new ComplexNode( "pager" ) );
            pagerNode.addChild( new SimpleNode( "page", metaData.getPager().getPage() ) );
            pagerNode.addChild( new SimpleNode( "pageCount", metaData.getPager().getPageCount() ) );
            pagerNode.addChild( new SimpleNode( "total", metaData.getPager().getTotal() ) );
            pagerNode.addChild( new SimpleNode( "nextPage", metaData.getPager().getNextPage() ) );
            pagerNode.addChild( new SimpleNode( "prevPage", metaData.getPager().getPrevPage() ) );
        }

        rootNode.addChild( fieldFilterService.filter( ProgramInstance.class, programInstances, fields ) );

        return rootNode;
    }
}
