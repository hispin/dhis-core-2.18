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
 * @version $Id$
 */
public class GenerateReportAction
    implements Action
{
    String periodSelect;

    String reportSelect;

    String orgUnitId;

    String monthlyPeriodTypeId;

    String startingPeriod;

    String endingPeriod;

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

    public void setStartingPeriod( String startingPeriod )
    {
        this.startingPeriod = startingPeriod;
    }

    public String getStartingPeriod()
    {
        return startingPeriod;
    }

    public void setEndingPeriod( String endingPeriod )
    {
        this.endingPeriod = endingPeriod;
    }

    public String getEndingPeriod()
    {
        return endingPeriod;
    }

    // ----------------------------------------------------------------------
    // Execution
    // ----------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        orgUnitId = Integer.toString( selectionManager.getSelectedOrganisationUnit().getId() );

        if ( reportSelect.equals( "form6" ) )
        {
            return "form6";
        }
        if ( reportSelect.equals( "form7" ) )
        {
            return "form7";
        }
        if ( reportSelect.equals( "form7b" ) )
        {
            return "form7b";
        }
        if ( reportSelect.equals( "mf4" ) )
        {
            return "mf4";
        }
        if ( reportSelect.equals( "mf5" ) )
        {
            return "mf5";
        }
        if ( reportSelect.equals( "mf6" ) )
        {
            return "mf6";
        }
        if ( reportSelect.equals( "rntcp" ) )
        {
            return "rntcp";
        }
        if ( reportSelect.equals( "nlepp" ) )
        {
            return "nlepp";
        }
        if ( reportSelect.equals( "stockpos" ) )
        {
            return "stockpos";
        }
        if ( reportSelect.equals( "noncomm" ) )
        {
            return "noncomm";
        }            
        
        if ( reportSelect.equals( "commiec" ) )
        {
            return "commiec";
        }
        if ( reportSelect.equals( "nmep" ) )
        {
            return "nmep";
        }
        if ( reportSelect.equals( "jphn" ) )
        {
            return "jphn";
        }
        if ( reportSelect.equals( "opip" ) )
        {
            return "opip";
        }
        if ( reportSelect.equals( "nvbdcp" ) )
        {
            return "nvbdcp";
        }
        if ( reportSelect.equals( "Prntcp" ) )
        {
            return "Prntcp";
        }
        if ( reportSelect.equals( "Pnoncommunicable" ) )
        {
            return "Pnoncommunicable";
        }
        if ( reportSelect.equals( "Pcommiec" ) )
        {
            return "Pcommiec";
        }
        if ( reportSelect.equals( "communicable" ) )
        {
            return "communicable";
        }
        if ( reportSelect.equals( "form1" ) )
        {
            return "form1";
        }
        if ( reportSelect.equals( "form2" ) )
        {
            return "form2";
        }
        if ( reportSelect.equals( "form3" ) )
        {
            return "form3";
        }
        if ( reportSelect.equals( "form4" ) )
        {
            return "form4";
        }
        if ( reportSelect.equals( "form5" ) )
        {
            return "form5";
        }
        if ( reportSelect.equals( "form8" ) )
        {
            return "form8";
        }
        if ( reportSelect.equals( "form9" ) )
        {
            return "form9";
        }


        return ERROR;
    }
}