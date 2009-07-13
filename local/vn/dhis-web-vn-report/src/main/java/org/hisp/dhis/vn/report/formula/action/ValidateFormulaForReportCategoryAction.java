// HIEU DONE //

package org.hisp.dhis.vn.report.formula.action;

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

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.vn.report.ReportItem;

import com.opensymphony.xwork.Action;


public class ValidateFormulaForReportCategoryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String formula;

    public void setFormula( String formula )
    {
        this.formula = formula;
    }

    private String mode;

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( formula == null || formula.trim().length() == 0 )
        {
            message = i18n.getString( "specify_formula" );

            return ERROR;
        }

        if ( mode.equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) ) {
        	
        	if ( !formula.equalsIgnoreCase(ReportItem.TYPE.DATAELEMENT) ) {
        		
        		message += i18n.getString( "expression_must_be_dataelement_string" );
        		return ERROR;
        	}
        }
        if ( mode.equalsIgnoreCase( ReportItem.TYPE.SERIAL ) ) {
        	
        	if ( !formula.equalsIgnoreCase(ReportItem.TYPE.SERIAL) ) {
        		
        		message += i18n.getString( "expression_must_be_serial_string" );
        		return ERROR;
        	}
        }
        if ( mode.equalsIgnoreCase( ReportItem.TYPE.ELEMENT_OPTIONCOMBO ) ) {
        	
        	try {
        		if ( Integer.parseInt(formula) <= 0 ) {
            		
            		message = "expression_must_be_greater_than_zero";
            		return ERROR;
            	}	
			} catch (NumberFormatException nfe) {
				System.err.println("Formula : ["+ formula +"] is not a number " + nfe);
				return ERROR;
			}
        }

        message = "valid";

        return SUCCESS;
    }
}
