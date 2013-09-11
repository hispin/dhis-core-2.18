package org.hisp.dhis.api.mobile.model.LWUITmodel;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.api.mobile.model.DataStreamSerializable;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.PatientIdentifier;

/**
 * @author Nguyen Kim Lai
 */
public class Patient
    implements DataStreamSerializable
{
    private String clientVersion;

    private int id;

    private String firstName;

    private String middleName;

    private String lastName;

    private int age;

    private List<PatientAttribute> patientAttValues;

    private List<PatientIdentifier> identifiers;

    private String gender;

    private String birthDate;

    private Date registrationDate;

    private Character dobType;

    private List<Program> programs;
    
    //private List<Integer> programsID;
    
    //private Map<Integer, String> patientDataValues;

    private List<Program> enrollmentPrograms;

    private List<Relationship> relationships;

    private List<Relationship> enrollmentRelationships;

    private String phoneNumber;

    private String organisationUnitName;
    
    private List<Program> completedPrograms;

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public List<PatientIdentifier> getIdentifiers()
    {
        return identifiers;
    }

    public void setIdentifiers( List<PatientIdentifier> identifiers )
    {
        this.identifiers = identifiers;
    }

    public List<Program> getPrograms()
    {
        return programs;
    }

    public void setPrograms( List<Program> programs )
    {
        this.programs = programs;
    }

    /*public List<Integer> getProgramsID()
    {
        return programsID;
    }

    public void setProgramsID( List<Integer> programsID )
    {
        this.programsID = programsID;
    }
    
    public Map<Integer, String> getPatientDataValues()
    {
        return patientDataValues;
    }

    public void setPatientDataValues( Map<Integer, String> patientDataValues )
    {
        this.patientDataValues = patientDataValues;
    }*/

    public List<Relationship> getRelationships()
    {
        return relationships;
    }

    public void setRelationships( List<Relationship> relationships )
    {
        this.relationships = relationships;
    }

    public List<Program> getEnrollmentPrograms()
    {
        return enrollmentPrograms;
    }

    public void setEnrollmentPrograms( List<Program> enrollmentPrograms )
    {
        this.enrollmentPrograms = enrollmentPrograms;
    }

    public String getFullName()
    {
        boolean space = false;
        String name = "";

        if ( firstName != null && firstName.length() != 0 )
        {
            name = firstName;
            space = true;
        }
        if ( middleName != null && middleName.length() != 0 )
        {
            if ( space )
                name += " ";
            name += middleName;
            space = true;
        }
        if ( lastName != null && lastName.length() != 0 )
        {
            if ( space )
                name += " ";
            name += lastName;
        }
        return name;
    }

    public int getAge()
    {
        return age;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate( Date registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    public Character getDobType()
    {
        return dobType;
    }

    public void setDobType( Character dobType )
    {
        this.dobType = dobType;
    }

    public void setAge( int age )
    {
        this.age = age;
    }

    public List<PatientAttribute> getPatientAttValues()
    {
        return patientAttValues;
    }

    public void setPatientAttValues( List<PatientAttribute> patientAttValues )
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

    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public List<Relationship> getEnrollmentRelationships()
    {
        return enrollmentRelationships;
    }

    public void setEnrollmentRelationships( List<Relationship> enrollmentRelationships )
    {
        this.enrollmentRelationships = enrollmentRelationships;
    }

    public String getOrganisationUnitName()
    {
        return organisationUnitName;
    }

    public void setOrganisationUnitName( String organisationUnitName )
    {
        this.organisationUnitName = organisationUnitName;
    }

    public List<Program> getCompletedPrograms()
    {
        return completedPrograms;
    }

    public void setCompletedPrograms( List<Program> completedPrograms )
    {
        this.completedPrograms = completedPrograms;
    }
    
    // -------------------------------------------------------------------------
    // Override Methods
    // -------------------------------------------------------------------------

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
        
        if ( organisationUnitName != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( organisationUnitName );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( gender != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( gender );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( dobType != null )
        {
            dout.writeBoolean( true );
            dout.writeChar( dobType );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( birthDate != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( birthDate );
        }
        else
        {
            dout.writeBoolean( false );
        }
        // doesn't transfer blood group to client
        dout.writeBoolean( false );

        if ( registrationDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( registrationDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( phoneNumber != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( phoneNumber );
        }
        else
        {
            dout.writeBoolean( false );
        }

        // Write Patient Attribute
        if ( patientAttValues != null )
        {
            dout.writeInt( patientAttValues.size() );
            for ( PatientAttribute patientAtt : patientAttValues )
            {
                patientAtt.serialize( dout );
            }
        }
        else
        {
            dout.writeInt( 0 );
        }
        
        // Write PatientIdentifier
        if ( identifiers != null )
        {
            dout.writeInt( identifiers.size() );
            for ( PatientIdentifier each : identifiers )
            {
                each.serialize( dout );
            }
        }
        else
        {
            dout.writeInt( 0 );
        }

        // Write Programs
        dout.writeInt( programs.size() );
        for ( Program each : programs )
        {
            each.serialize( dout );
        }
        
        /*dout.writeInt( programsID.size() );
        for ( Integer each : programsID )
        {
            dout.writeInt( each );
        }

        // Write Patient Data Value
        dout.writeInt( patientDataValues.keySet().size() );
        for ( Integer key : patientDataValues.keySet() )
        {
            dout.writeInt( key );
            dout.writeUTF( patientDataValues.get( key ) );
        }*/
        
        // Write Relationships
        dout.writeInt( relationships.size() );
        for ( Relationship each : relationships )
        {
            each.serialize( dout );
        }

        // Write Enrolled Programs

        dout.writeInt( enrollmentPrograms.size() );
        for ( Program each : enrollmentPrograms )
        {
            each.serialize( dout );
        }

        // Write Enrolled Relationships

        dout.writeInt( enrollmentRelationships.size() );
        for ( Relationship each : enrollmentRelationships )
        {
            each.serialize( dout );
        }
        
        // Write completed Programs
        dout.writeInt( completedPrograms.size() );
        for ( Program each : completedPrograms )
        {
            each.serialize( dout );
        }

        bout.flush();
        bout.writeTo( out );
    }

    @Override
    public void deSerialize( DataInputStream din )
        throws IOException, EOFException
    {
        this.setId( din.readInt() );
        this.setFirstName( din.readUTF() );
        this.setMiddleName( din.readUTF() );
        this.setLastName( din.readUTF() );
        
        // Org Name
        if ( din.readBoolean() )
        {
            this.setOrganisationUnitName( din.readUTF() );
        }
        else
        {
            this.setOrganisationUnitName( null );
        }
        
        // Gender
        if ( din.readBoolean() )
        {
            this.setGender( din.readUTF() );
        }
        else
        {
            this.setGender( null );
        }
        
        // DOB Type
        if ( din.readBoolean() )
        {
            char dobTypeDeserialized = din.readChar();
            this.setDobType( new Character( dobTypeDeserialized ) );
        }
        else
        {
            this.setDobType( null );
        }
        
        // DOB
        if ( din.readBoolean() )
        {
            this.setBirthDate( din.readUTF() );
        }
        else
        {
            this.setBirthDate( null );
        }

        // doesn't transfer blood group to client
        din.readBoolean();
        
        // Registration Date
        if ( din.readBoolean() )
        {
            this.setRegistrationDate( new Date( din.readLong() ) );
        }
        else
        {
            this.setRegistrationDate( null );
        }

        // Phone Number
        if ( din.readBoolean() )
        {
            this.setPhoneNumber( din.readUTF() );
        }
        else
        {
            this.setPhoneNumber( null );
        }
        
        // Patient Attribute & Identifiers
        int attsNumb = din.readInt();
        if( attsNumb > 0 )
        {
            this.patientAttValues = new ArrayList<PatientAttribute>();
            for ( int j = 0; j < attsNumb; j++ )
            {
                PatientAttribute pa = new PatientAttribute();
                pa.deSerialize( din );
                this.patientAttValues.add( pa );
            }
        }
        else
        {
            this.patientAttValues = null;
        }

        int numbIdentifiers = din.readInt();
        this.identifiers = new ArrayList<PatientIdentifier>();
        if ( numbIdentifiers > 0 )
        {
            for ( int i = 0; i < numbIdentifiers; i++ )
            {
                PatientIdentifier identifier = new PatientIdentifier();
                identifier.deSerialize( din );
                this.identifiers.add( identifier );
    
            }
        }
        
        // Program & Relationship
        int numbPrograms = din.readInt();
        if ( numbPrograms > 0 )
        {
            this.programs = new ArrayList<Program>();
            for ( int i = 0; i < numbPrograms; i++ )
            {
                Program program = new Program();
                program.deSerialize( din );
                this.programs.add( program );
            }
        }
        else
        {
            this.programs = null;
        }
        
        int numbRelationships = din.readInt();
        if ( numbRelationships > 0 )
        {
            this.relationships = new ArrayList<Relationship>();
            for ( int i = 0; i < numbRelationships; i++ )
            {
                Relationship relationship = new Relationship();
                relationship.deSerialize( din );
                this.relationships.add( relationship );
            }
        }
        else
        {
            this.relationships = null;
        }
        
        int numbEnrollmentPrograms = din.readInt();
        if ( numbEnrollmentPrograms > 0 )
        {
            this.enrollmentPrograms = new ArrayList<Program>();
            for ( int i = 0; i < numbEnrollmentPrograms; i++ )
            {
                Program program = new Program();
                program.deSerialize( din );
                this.enrollmentPrograms.add( program );
            }
        }
        else
        {
            this.enrollmentPrograms = null;
        }
        
        int numbEnrollmentRelationships = din.readInt();
        if ( numbEnrollmentRelationships > 0 )
        {
            this.enrollmentRelationships = new ArrayList<Relationship>();
            for ( int i = 0; i < numbEnrollmentRelationships; i++ )
            {
                Relationship relationship = new Relationship();
                relationship.deSerialize( din );
                this.enrollmentRelationships.add( relationship );
            }
        }
        else
        {
            this.enrollmentRelationships = null;
        }
        
        int numbCompletedPrograms = din.readInt();
        if ( numbCompletedPrograms > 0 )
        {
            this.completedPrograms = new ArrayList<Program>();
            for ( int i = 0; i < numbCompletedPrograms; i++ )
            {
                Program program = new Program();
                program.deSerialize( din );
                this.completedPrograms.add( program );
            }
        }
        else
        {
            this.completedPrograms = null;
        }
    }

    @Override
    public boolean equals( Object otherObject )
    {
        if ( this == otherObject )
        {
            return true;
        }

        if ( otherObject == null )
        {
            return false;
        }

        if ( getClass() != otherObject.getClass() )
        {
            return false;
        }

        final Patient otherPatient = (Patient) otherObject;

        if ( birthDate == null )
        {
            if ( otherPatient.birthDate != null )
            {
                return false;
            }
        }
        else if ( !birthDate.equals( otherPatient.birthDate ) )
        {
            return false;
        }

        if ( firstName == null )
        {
            if ( otherPatient.firstName != null )
            {
                return false;
            }
        }
        else if ( !firstName.equals( otherPatient.firstName ) )
        {
            return false;
        }

        if ( gender == null )
        {
            if ( otherPatient.gender != null )
                return false;
        }
        else if ( !gender.equals( otherPatient.gender ) )
        {
            return false;
        }

        if ( lastName == null )
        {
            if ( otherPatient.lastName != null )
            {
                return false;
            }
        }
        else if ( !lastName.equals( otherPatient.lastName ) )
        {
            return false;
        }

        if ( middleName == null )
        {
            if ( otherPatient.middleName != null )
            {
                return false;
            }
        }
        else if ( !middleName.equals( otherPatient.middleName ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());

        return result;
    }

    @Override
    public void serializeVersion2_8( DataOutputStream out )
        throws IOException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void serializeVersion2_9( DataOutputStream dout )
        throws IOException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void serializeVersion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
    }

}
