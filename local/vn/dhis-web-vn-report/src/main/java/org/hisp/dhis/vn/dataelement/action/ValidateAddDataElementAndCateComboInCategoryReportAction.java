package org.hisp.dhis.vn.dataelement.action;

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

/**
 * @author DANG DUY HIEU
 */

import org.hisp.dhis.vn.report.action.ActionSupport;


public class ValidateAddDataElementAndCateComboInCategoryReportAction 
	extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer dataelement_no;
	private Integer catecomboid;

    public void setDataelement_no( Integer dataelement_no )
    {
        this.dataelement_no = dataelement_no;
    }

    public void setCatecomboid( Integer catecomboid )
    {
        this.catecomboid = catecomboid;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
    	System.out.println("dataelement_no - catecomboid : " + dataelement_no + " - " + catecomboid);
        if ( dataelement_no <= 0 || dataelement_no == null ) {
        	
            message = i18n.getString( "add_element_invalid" );

            return ERROR;
        }
        if ( catecomboid <= 0 || catecomboid == null ) {
        	
            message = i18n.getString( "add_categorycombo_invalid" );

            return ERROR;
        }
        message = i18n.getString( "valid" );
        
        System.err.println(message);
        
        return SUCCESS;
    }
}
