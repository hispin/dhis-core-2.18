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
import java.util.List;

import org.hisp.dhis.api.mobile.TrackedEntityMobileSettingService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityMobileSetting;

import com.opensymphony.xwork2.Action;

public class UpdateMobileSettingAction
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

    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    private Integer groupingAttributeId;

    public Integer getGroupingAttributeId()
    {
        return groupingAttributeId;
    }

    public void setGroupingAttributeId( Integer groupingAttributeId )
    {
        this.groupingAttributeId = groupingAttributeId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        if ( selectedList.size() > 0 )
        {
            TrackedEntityMobileSetting setting;
            if ( patientMobileSettingService.getCurrentSetting().size() > 0 )
            {
                setting = patientMobileSettingService.getCurrentSetting().iterator().next();
                List<TrackedEntityAttribute> attributes = new ArrayList<TrackedEntityAttribute>();
                setting.setAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.updateTrackedEntityMobileSetting( setting );
            }
            else
            {
                setting = new TrackedEntityMobileSetting();
                List<TrackedEntityAttribute> attributes = new ArrayList<TrackedEntityAttribute>();
                setting.setAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.saveTrackedEntityMobileSetting( setting );
            }
        }
        else
        {
            TrackedEntityMobileSetting setting;
            if ( patientMobileSettingService.getCurrentSetting().size() > 0 )
            {
                setting = patientMobileSettingService.getCurrentSetting().iterator().next();
                List<TrackedEntityAttribute> attributes = new ArrayList<TrackedEntityAttribute>();
                setting.setAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.updateTrackedEntityMobileSetting( setting );
            }
            else
            {
                setting = new TrackedEntityMobileSetting();
                List<TrackedEntityAttribute> attributes = new ArrayList<TrackedEntityAttribute>();
                setting.setAttributes( attributes );
                fillValues( attributes );
                patientMobileSettingService.saveTrackedEntityMobileSetting( setting );
            }
        }

        Collection<TrackedEntityAttribute> allTrackedEntityAttributes = patientAttributeService.getAllTrackedEntityAttributes();

        for ( TrackedEntityAttribute patientAttribute : allTrackedEntityAttributes )
        {
//            patientAttribute.setGroupBy( false );
//            if ( patientAttribute.getId() == groupingAttributeId )
//            {
//                patientAttribute.setGroupBy( true );
//            }
            patientAttributeService.updateTrackedEntityAttribute( patientAttribute );
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void fillValues( List<TrackedEntityAttribute> attributes )
    {
        for ( String id : selectedList )
        {
            attributes.add( patientAttributeService.getTrackedEntityAttribute( Integer.parseInt( id ) ) );
        }
    }
}
