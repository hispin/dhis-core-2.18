package org.hisp.dhis.mobile.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.api.mobile.TrackedEntityMobileSettingService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityMobileSetting;

import com.opensymphony.xwork2.Action;

public class ShowMobileSettingFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityAttributeService patientAttributeService;

    public TrackedEntityAttributeService getTrackedEntityAttributeService()
    {
        return patientAttributeService;
    }

    public void setTrackedEntityAttributeService( TrackedEntityAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private TrackedEntityMobileSettingService patientMobileSettingService;

    public TrackedEntityMobileSettingService getTrackedEntityMobileSettingService()
    {
        return patientMobileSettingService;
    }

    public void setTrackedEntityMobileSettingService( TrackedEntityMobileSettingService patientMobileSettingService )
    {
        this.patientMobileSettingService = patientMobileSettingService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private List<TrackedEntityAttribute> patientAtts;

    public List<TrackedEntityAttribute> getPatientAtts()
    {
        return patientAtts;
    }

    public void setPatientAtts( List<TrackedEntityAttribute> patientAtts )
    {
        this.patientAtts = patientAtts;
    }

    private Collection<TrackedEntityAttribute> attributes;

    public Collection<TrackedEntityAttribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( Collection<TrackedEntityAttribute> attributes )
    {
        this.attributes = attributes;
    }
    
    private List<TrackedEntityAttribute> allAttributes;
    
    public List<TrackedEntityAttribute> getAllAttributes()
    {
        return allAttributes;
    }

    public void setAllAttributes( List<TrackedEntityAttribute> allAttributes )
    {
        this.allAttributes = allAttributes;
    }

    private TrackedEntityMobileSetting setting;

    public TrackedEntityMobileSetting getSetting()
    {
        return setting;
    }

    public void setSetting( TrackedEntityMobileSetting setting )
    {
        this.setting = setting;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        attributes = patientAttributeService.getAllTrackedEntityAttributes();
        allAttributes = new ArrayList<TrackedEntityAttribute>(attributes);

        Collection<TrackedEntityMobileSetting> paSettings = new HashSet<TrackedEntityMobileSetting>( patientMobileSettingService
            .getCurrentSetting() );

        if ( !paSettings.isEmpty() )
        {
            Iterator<TrackedEntityMobileSetting> settingsIt = paSettings.iterator();

            if ( settingsIt.hasNext() )
            {
                setting = settingsIt.next();

                patientAtts = setting.getAttributes();

                for ( TrackedEntityAttribute attribute : patientAtts )
                {
                    if ( attributes.contains( attribute ) )
                    {
                        attributes.remove( attribute );
                    }
                }
            }
            else
            {
                patientAtts = new ArrayList<TrackedEntityAttribute>();
            }
        }

        return SUCCESS;
    }

}
