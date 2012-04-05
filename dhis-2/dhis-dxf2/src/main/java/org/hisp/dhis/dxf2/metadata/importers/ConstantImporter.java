package org.hisp.dhis.dxf2.metadata.importers;

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
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.springframework.stereotype.Component;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class ConstantImporter
    extends AbstractImporter<Constant>
{
    private static final Log LOG = LogFactory.getLog( ConstantImporter.class );

    @Override
    protected ImportConflict newObject( Constant constant, ImportOptions options )
    {
        LOG.info( "NEW OBJECT: " + constant );

        if ( !options.isDryRun() )
        {
            LOG.info( "Trying to save new object with UID: " + constant.getUid() );
            manager.save( constant );
            LOG.info( "Save successful." );
        }

        return null;
    }

    @Override
    protected ImportConflict updatedObject( Constant constant, Constant oldConstant, ImportOptions options )
    {
        oldConstant.setValue( constant.getValue() );

        if ( !options.isDryRun() )
        {
            LOG.info( "Trying to update object with UID: " + oldConstant.getUid() );
            manager.update( oldConstant );
            LOG.info( "Update successful." );
        }

        return null;
    }

    @Override
    protected String getObjectName()
    {
        return this.getClass().getName();
    }

    @Override
    public boolean canHandle( Class<?> clazz )
    {
        return Constant.class.equals( clazz );
    }
}
