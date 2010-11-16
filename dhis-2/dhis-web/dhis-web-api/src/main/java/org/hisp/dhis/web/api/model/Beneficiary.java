package org.hisp.dhis.web.api.model;

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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

public class Beneficiary
    implements MobileSerializable
{
    private int id;

    private String firstName;

    private String middleName;

    private String lastName;

    private Set<String> patientAttValues;

    private PatientAttribute groupAttribute;

    public PatientAttribute getGroupAttribute()
    {
        return groupAttribute;
    }

    public void setGroupAttribute( PatientAttribute groupAttribute )
    {
        this.groupAttribute = groupAttribute;
    }

    public Set<String> getPatientAttValues()
    {
        return patientAttValues;
    }

    public void setPatientAttValues( Set<String> patientAttValues )
    {
        this.patientAttValues = patientAttValues;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    @Override
    public void serialize( DataOutputStream out )
        throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        dout.writeInt( this.getId() );
        dout.writeUTF( this.getFirstName() );
        dout.writeUTF( this.getMiddleName() );
        dout.writeUTF( this.getLastName() );
        //Write attribute which is used as group factor of beneficiary.
        /* False: no group factor
         * True: with group factor
         * */
        if ( this.getGroupAttribute() != null )
        {
            dout.writeBoolean( true );
            this.getGroupAttribute().serialize( dout );
        }
        else
        {
            dout.writeBoolean( false );
        }

        Set<String> atts = this.getPatientAttValues();
        dout.writeInt( atts.size() );
        for ( String att : atts )
        {
            dout.writeUTF( att );
        }

        bout.flush();
        bout.writeTo( out );
    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        // FIXME: Get implementation from client

    }


}
