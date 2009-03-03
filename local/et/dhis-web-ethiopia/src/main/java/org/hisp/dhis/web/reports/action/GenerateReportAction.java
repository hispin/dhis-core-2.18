package org.hisp.dhis.web.reports.action;

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

import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import com.opensymphony.xwork.Action;

/**
 * @author Kristian Nordal
 * @version $Id: GenerateReportAction.java 2878 2007-02-21 08:36:56Z andegje $
 */
public class GenerateReportAction
    implements Action
{
    String periodSelect;

    String reportSelect;

    String orgUnitId;

    String monthlyPeriodTypeId;

    // ----------------------------------------------------------------------
    // Dependencies
    // ----------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // ----------------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------------

    public String getPeriodSelect()
    {
        return periodSelect;
    }

    public void setPeriodSelect( String periodSelect )
    {
        this.periodSelect = periodSelect;
    }

    public String getReportSelect()
    {
        return reportSelect;
    }

    public void setReportSelect( String reportSelect )
    {
        this.reportSelect = reportSelect;
    }

    public String getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setMonthlyPeriodTypeId( String monthlyPeriodTypeId )
    {
        this.monthlyPeriodTypeId = monthlyPeriodTypeId;
    }

    public String getMonthlyPeriodTypeId()
    {
        return monthlyPeriodTypeId;
    }

    // ----------------------------------------------------------------------
    // Execution
    // ----------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        orgUnitId = Integer.toString( selectionManager.getSelectedOrganisationUnit().getId() );

        if ( reportSelect.equals( "mm" ) )
        {
            return "mm";
        }

        if ( reportSelect.equals( "a" ) )
        {
            return "a";
        }
        if ( reportSelect.equals( "b" ) )
        {
            return "b";
        }
        if ( reportSelect.equals( "c" ) )
        {
            return "c";

        }
        if ( reportSelect.equals( "d" ) )
        {
            return "d";
        }

        if ( reportSelect.equals( "e" ) )
        {
            return "e";
        }

        if ( reportSelect.equals( "f" ) )
        {
            return "f";
        }

        if ( reportSelect.equals( "g" ) )
        {
            return "g";
        }

        if ( reportSelect.equals( "h" ) )
        {
            return "h";
        }

        if ( reportSelect.equals( "i" ) )
        {
            return "i";
        }

        if ( reportSelect.equals( "j" ) )
        {
            return "j";

        }
        if ( reportSelect.equals( "k" ) )
        {
            return "k";

        }
        if ( reportSelect.equals( "l" ) )
        {
            return "l";

        }
        if ( reportSelect.equals( "m" ) )
        {
            return "m";

        }
        if ( reportSelect.equals( "n" ) )
        {
            return "n";

        }
        if ( reportSelect.equals( "o" ) )
        {
            return "o";

        }
        if ( reportSelect.equals( "p" ) )
        {
            return "p";

        }
        if ( reportSelect.equals( "q" ) )
        {
            return "q";

        }
        if ( reportSelect.equals( "r" ) )
        {
            return "r";

        }
        if ( reportSelect.equals( "s" ) )
        {
            return "s";

        }

        if ( reportSelect.equals( "t" ) )
        {
            return "t";

        }
        if ( reportSelect.equals( "u" ) )
        {
            return "u";

        }
        if ( reportSelect.equals( "v" ) )
        {
            return "v";

        }

        return ERROR;
    }//function
}
