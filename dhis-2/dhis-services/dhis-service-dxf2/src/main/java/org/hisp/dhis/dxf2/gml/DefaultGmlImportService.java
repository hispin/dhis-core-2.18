package org.hisp.dhis.dxf2.gml;

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

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.scheduling.TaskId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * @author Halvdan Hoem Grelland
 */
public class DefaultGmlImportService
    implements GmlImportService
{
    private static final String GML_TO_DXF_TRANSFORM = "gml/gml2dxf2.xsl";
    private static final ClassLoader CL = new DefaultResourceLoader().getClassLoader();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private ImportService importService;

    @Autowired
    private RenderService renderService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // GmlImportService implementation
    // -------------------------------------------------------------------------

    @Override
    public MetaData fromGml( InputStream inputStream )
        throws IOException, TransformerException
    {
        InputStream dxfStream = transformGml( inputStream );

        MetaData metaData = renderService.fromXml( dxfStream, MetaData.class );

        dxfStream.close();

        Map<String, OrganisationUnit> namedMap = Maps.uniqueIndex( metaData.getOrganisationUnits(),
            new Function<OrganisationUnit, String>()
            {
                public String apply( OrganisationUnit organisationUnit )
                {
                    return organisationUnit.getName();
                }
            }
        );

        // Fetch persisted OrganisationUnits and merge imported GML properties
        Collection<OrganisationUnit> persistedOrgUnits = organisationUnitService.getOrganisationUnitsByNames( namedMap.keySet() );

        for( OrganisationUnit persisted : persistedOrgUnits )
        {
            OrganisationUnit unit = namedMap.get( persisted.getName() );

            if( unit == null || unit.getCoordinates() == null || unit.getFeatureType() == null )
            {
                continue;
            }

            String coordinates = unit.getCoordinates(),
                   featureType = unit.getFeatureType();

            unit.mergeWith( persisted );

            unit.setCoordinates( coordinates );
            unit.setFeatureType( featureType );

            if( persisted.getParent() != null )
            {
                OrganisationUnit parent = new OrganisationUnit();
                parent.setUid( persisted.getParent().getUid() );
                unit.setParent( parent );
            }
        }

        return metaData;
    }

    @Transactional
    @Override
    public void importGml( InputStream inputStream, String userUid, ImportOptions importOptions, TaskId taskId )
        throws IOException, TransformerException
    {
        importService.importMetaData( userUid, fromGml( inputStream ), importOptions, taskId );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private InputStream transformGml( InputStream input )
        throws IOException, TransformerException
    {
        StreamSource gml = new StreamSource( input );
        StreamSource xsl = new StreamSource( CL.getResourceAsStream( GML_TO_DXF_TRANSFORM ) );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        TransformerFactory.newInstance().newTransformer( xsl ).transform( gml, new StreamResult( output ) );

        xsl.getInputStream().close();
        gml.getInputStream().close();

        return new ByteArrayInputStream( output.toByteArray() );
    }

}
