package org.hisp.dhis.tallysheet;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Haavard Tegelsrud, Oddmund Stroemme, Joergen Froeysadal, Ruben Wangberg
 * @version $Id$
 */
public class TallySheet
{
    public static final String KEY_TALLY_SHEET = "tallySheet";
    
    private String tallySheetName;

    private List<TallySheetTuple> tallySheetTuples;

    private boolean a3Format;

    private boolean displayFacilityName;

    private OrganisationUnit organisationUnit;

    private int rowWidth;

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * Returns the name of the tally sheet 
     * 
     * @return the name of the tally sheet 
     */
    public String getTallySheetName()
    {
        return tallySheetName;
    }
    
    /**
     * Returns a list of TallySheetTuples. The TallySheetTuple object contains 
     * the necessary information to build a row in the final tally sheet PDF. 
     * (DataElement name and number of cell rows). 
     * 
     * @return a List of TallySheetTuples
     */
    public List<TallySheetTuple> getTallySheetTuples()
    {
        return tallySheetTuples;
    }

    /**
     * Returns a boolean that tells the tally sheet format.
     * 
     * @return a boolean that is true if tally sheet is A3, false if tally sheet is A4. 
     */
    public boolean isA3Format()
    {
        return a3Format;
    }

    /**
     * Returns a boolean that tells if the facility name is to be shown on the final 
     * tally sheet PDF.
     * 
     * @return a boolean that is true if the facility name is to be shown on PDF, false if not.
     */
    public boolean isDisplayFacilityName()
    {
        return displayFacilityName;
    }

    /**
     * Sets the format of the tally sheet.   
     * 
     * @param a3Format true if format is A3, false if format is A4 
     */
    public void setA3Format( boolean a3Format )
    {
        this.a3Format = a3Format;
        
        if ( a3Format )
        {
            rowWidth = 100;
        }
        else
        {
            rowWidth = 50;
        }
    }

    /**
     * Sets the displayFacilityName variable, that decides wether the facility name will be written
     * to the PDF or not.
     * 
     * @param displayFacilityName true if facility name is to be shown, false if not.
     */
    public void setDisplayFacilityName( boolean displayFacilityName )
    {
        this.displayFacilityName = displayFacilityName;
    }

    /**
     * Sets the organisation unit associated with the tally sheet.
     * 
     * @param organisationUnit the OrganisationUnit
     */
    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    /**
     * Sets the name of the tally sheet
     * 
     * @param tallySheetName the tally sheet name
     */
    public void setTallySheetName( String tallySheetName )
    {
        this.tallySheetName = tallySheetName;
    }

    /**
     * Sets the List of TallySheetTuples that the tally sheet contains
     * 
     * @param tallySheetTuples the List of TallySheetTuples associated with the tally sheet.
     */
    public void setTallySheetTuples( List<TallySheetTuple> tallySheetTuples )
    {
        this.tallySheetTuples = tallySheetTuples;
    }

    /**
     * Returns the name of the organisation unit associated with the tally sheet.
     * 
     * @return the name of the organisation unit
     */
    public String getFacilityName()
    {
        return organisationUnit.getName();
    }

    /**
     * Returns the number of zeros the tally sheet can fit on each row. Dependent on whether
     * the format is A4 or A3.
     * 
     * @return the number of zeros on each row
     */
    public int getRowWidth()
    {
        return rowWidth;
    }
}
