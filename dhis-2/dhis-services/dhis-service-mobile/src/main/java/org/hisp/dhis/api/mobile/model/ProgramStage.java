package org.hisp.dhis.api.mobile.model;

/*
 * Copyright (c) 2010, University of Oslo
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ProgramStage
    extends Model
{

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        // FIXME: Children should serialize themselves
        dout.writeInt( this.getId() );
        dout.writeUTF( this.getName() );

        dout.writeInt( dataElements.size() );

        for ( int i = 0; i < dataElements.size(); i++ )
        {
            DataElement de = (DataElement) dataElements.get( i );
            dout.writeInt( de.getId() );
            dout.writeUTF( de.getName() );
            dout.writeUTF( de.getType() );
            dout.writeBoolean( de.isCompulsory() );

            List<Model> cateOptCombos = de.getCategoryOptionCombos().getModels();
            if ( cateOptCombos == null || cateOptCombos.size() <= 0 )
            {
                dout.writeInt( 0 );
            }
            else
            {
                dout.writeInt( cateOptCombos.size() );
                for ( Model each : cateOptCombos )
                {
                    dout.writeInt( each.getId() );
                    dout.writeUTF( each.getName() );
                }
            }

        }
    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        // FIXME: Get implementation from client
    }

}