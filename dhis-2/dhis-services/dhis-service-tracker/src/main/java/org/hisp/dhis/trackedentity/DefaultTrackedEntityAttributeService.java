package org.hisp.dhis.trackedentity;

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

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultTrackedEntityAttributeService
    implements TrackedEntityAttributeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityAttributeStore attributeStore;

    public void setAttributeStore( TrackedEntityAttributeStore attributeStore )
    {
        this.attributeStore = attributeStore;
    }

    private GenericNameableObjectStore<TrackedEntityAttributeOption> attributeOptionStore;

    public void setAttributeOptionStore( GenericNameableObjectStore<TrackedEntityAttributeOption> attributeOptionStore )
    {
        this.attributeOptionStore = attributeOptionStore;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public void deleteTrackedEntityAttribute( TrackedEntityAttribute attribute )
    {
        attributeStore.delete( attribute );
    }

    public Collection<TrackedEntityAttribute> getAllTrackedEntityAttributes()
    {
        return attributeStore.getAll();
    }

    public TrackedEntityAttribute getTrackedEntityAttribute( int id )
    {
        return attributeStore.get( id );
    }

    public int saveTrackedEntityAttribute( TrackedEntityAttribute attribute )
    {
        return attributeStore.save( attribute );
    }

    public void updateTrackedEntityAttribute( TrackedEntityAttribute attribute )
    {
        attributeStore.update( attribute );
    }

    public Collection<TrackedEntityAttribute> getTrackedEntityAttributesByValueType( String valueType )
    {
        return attributeStore.getByValueType( valueType );
    }

    public TrackedEntityAttribute getTrackedEntityAttributeByName( String name )
    {
        return attributeStore.getByName( name );
    }

    public TrackedEntityAttribute getTrackedEntityAttributeByGroupBy()
    {
        return attributeStore.getByGroupBy();
    }

    public Collection<TrackedEntityAttribute> getOptionalAttributesWithoutGroup()
    {
        return attributeStore.getOptionalAttributesWithoutGroup();
    }

    public Collection<TrackedEntityAttribute> getTrackedEntityAttributesByMandatory( boolean mandatory )
    {
        return attributeStore.getByMandatory( mandatory );
    }

    public Collection<TrackedEntityAttribute> getTrackedEntityAttributesWithoutGroup()
    {
        return attributeStore.getWithoutGroup();
    }

    public TrackedEntityAttribute getTrackedEntityAttribute( String uid )
    {
        return attributeStore.getByUid( uid );
    }

    public Collection<TrackedEntityAttribute> getTrackedEntityAttributesByDisplayOnVisitSchedule(
        boolean displayOnVisitSchedule )
    {
        return attributeStore.getByDisplayOnVisitSchedule( displayOnVisitSchedule );
    }

    public Collection<TrackedEntityAttribute> getTrackedEntityAttributesWithoutProgram()
    {
        Collection<TrackedEntityAttribute> result = attributeStore.getAll();

        Collection<Program> programs = programService.getAllPrograms();
        for ( Program program : programs )
        {
            result.removeAll( program.getAttributes() );
        }

        return result;
    }

    public Collection<TrackedEntityAttribute> getTrackedEntityAttributesDisplayedInList( boolean displayInListNoProgram )
    {
        return attributeStore.getDisplayedInList( displayInListNoProgram );
    }

    public Collection<TrackedEntityAttribute> getTrackedEntityAttributesBetweenByName( String name, int min, int max )
    {
        return attributeStore.getAllLikeNameOrderedName( name, min, max );
    }

    public int getTrackedEntityAttributeCount()
    {
        return attributeStore.getCount();
    }

    public Collection<TrackedEntityAttribute> getTrackedEntityAttributesBetween( int min, int max )
    {
        return attributeStore.getAllOrderedName( min, max );
    }

    public int getTrackedEntityAttributeCountByName( String name )
    {
        return attributeStore.getCountEqNameNoAcl( name );
    }

    // -------------------------------------------------------------------------
    // TrackedEntityAttributeOption
    // -------------------------------------------------------------------------

    public int addTrackedEntityAttributeOption( TrackedEntityAttributeOption option )
    {
        return attributeOptionStore.save( option );
    }

    public void deleteTrackedEntityAttributeOption( TrackedEntityAttributeOption option )
    {
        attributeOptionStore.delete( option );
    }

    public void updateTrackedEntityAttributeOption( TrackedEntityAttributeOption option )
    {
        attributeOptionStore.update( option );
    }

    public TrackedEntityAttributeOption getTrackedEntityAttributeOption( TrackedEntityAttribute attribute, String name )
    {
        return attributeStore.get( attribute, name );
    }

    public TrackedEntityAttributeOption getTrackedEntityAttributeOption( int id )
    {
        return attributeOptionStore.get( id );
    }

    public Collection<TrackedEntityAttributeOption> getTrackedEntityAttributeOption( TrackedEntityAttribute attribute )
    {
        return attributeStore.get( attribute );
    }
}
