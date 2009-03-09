package org.hisp.dhis.olap;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public interface OlapURLService
{
    String ID = OlapURLService.class.getName();
    
    /**
     * Returns a mapping URL.
     * 
     * @param application the application name.
     * @param cube the cube name.
     * @param indicator the indicator name.
     * @param organisationUnit the organisation unit name.
     * @param year the year.
     * @param month the month.
     * @param level the organisation unit level.
     * @return a mapping URL.
     */
    String getMapURL( String application, String cube, String indicator, 
        String organisationUnit, String year, String month, String level );
    
    int saveOlapURL( OlapURL olapURL );
    
    void updateOlapURL( OlapURL olapURL );
    
    OlapURL getOlapURL( int id );
    
    Collection<OlapURL> getOlapURLs( Collection<Integer> identifiers );
    
    void deleteOlapURL( OlapURL olapURL );
    
    Collection<OlapURL> getAllOlapURLs();
    
    OlapURL getOlapURLByName( String name );
}
