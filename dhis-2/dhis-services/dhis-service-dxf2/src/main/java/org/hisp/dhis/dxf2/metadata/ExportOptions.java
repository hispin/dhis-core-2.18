package org.hisp.dhis.dxf2.metadata;

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

import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;

/**
 * @author Lars Helge Overland
 */
public class ExportOptions
{
    private IdentifiableProperty dataElementIdScheme;

    private IdentifiableProperty orgUnitIdScheme;
    
    private IdentifiableProperty categoryOptionComboIdScheme;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ExportOptions()
    {
    }
    
    public ExportOptions( IdentifiableProperty dataElementIdScheme, IdentifiableProperty orgUnitIdScheme, IdentifiableProperty categoryOptionComboIdScheme )
    {
        this.dataElementIdScheme = dataElementIdScheme;
        this.orgUnitIdScheme = orgUnitIdScheme;
        this.categoryOptionComboIdScheme = categoryOptionComboIdScheme;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    public String toString()
    {
        return "[Data element id scheme: " + dataElementIdScheme + 
            ", org unit id scheme: " + orgUnitIdScheme + 
            ", category option combo id scheme: " + categoryOptionComboIdScheme + "]";
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public IdentifiableProperty getDataElementIdSchemeFallback()
    {
        return dataElementIdScheme != null ? dataElementIdScheme : IdentifiableProperty.UID;
    }
    
    public IdentifiableProperty getOrgUnitIdSchemeFallback()
    {
        return orgUnitIdScheme != null ? orgUnitIdScheme : IdentifiableProperty.UID;
    }
    
    public IdentifiableProperty getCategoryOptionComboIdSchemeFallback()
    {
        return categoryOptionComboIdScheme != null ? categoryOptionComboIdScheme : IdentifiableProperty.UID;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public IdentifiableProperty getDataElementIdScheme()
    {
        return dataElementIdScheme;
    }

    public void setDataElementIdScheme( IdentifiableProperty dataElementIdScheme )
    {
        this.dataElementIdScheme = dataElementIdScheme;
    }

    public IdentifiableProperty getOrgUnitIdScheme()
    {
        return orgUnitIdScheme;
    }

    public void setOrgUnitIdScheme( IdentifiableProperty orgUnitIdScheme )
    {
        this.orgUnitIdScheme = orgUnitIdScheme;
    }

    public IdentifiableProperty getCategoryOptionComboIdScheme()
    {
        return categoryOptionComboIdScheme;
    }

    public void setCategoryOptionComboIdScheme( IdentifiableProperty categoryOptionComboIdScheme )
    {
        this.categoryOptionComboIdScheme = categoryOptionComboIdScheme;
    }
}
