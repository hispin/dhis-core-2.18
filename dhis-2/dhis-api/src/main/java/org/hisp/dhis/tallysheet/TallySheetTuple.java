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

import org.hisp.dhis.dataelement.DataElement;

/**
 * @author Haavard Tegelsrud, Oddmund Stroemme, Joergen Froeysadal, Ruben Wangberg
 * @version $Id$
 */
public class TallySheetTuple
{
    private int numberOfElements;

    private int numberOfRows;

    private int rowWidth;

    private DataElement dataElement;

    private boolean checked = false;

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * @return DataElement the data element
     */
    public DataElement getDataElement()
    {
        return dataElement;
    }

    /**
     * Check if the user wants to display this tuple on the tally sheet.
     * 
     * @return boolean display tuple or not
     */
    public boolean isChecked()
    {
        return checked;
    }
    
    /**
     * @param calculatedNumberOfElements
     * @param dataElement
     * @param rowWidth
     */
    public void setTallySheetTuple( int numberOfElements, DataElement dataElement, int rowWidth )
    {
        this.numberOfElements = numberOfElements;
        this.dataElement = dataElement;
        this.numberOfRows = (int) Math.ceil( numberOfElements * 1.0 / rowWidth );
        this.rowWidth = rowWidth;
    }

    /**
     * Set this tuple to be displayed or not on the tally sheet.
     * 
     * @param checked display tuple or not
     */
    public void setChecked( boolean checked )
    {
        this.checked = checked;
    }

    /**
     * Returns the number of tally rows for this tuple.
     * 
     * @return number of rows
     */
    public int getNumberOfRows()
    {
        return numberOfRows;
    }

    /**
     * Set the number of tally rows for this tuple.
     * 
     * @param rows the number of rows
     */
    public void setNumberOfRows( int rows )
    {
        this.numberOfRows = rows;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------
    
    /**
     * Recalculate the number of tally rows needed for this tuple. The user may
     * have altered the factor.
     * 
     * @param factor the factor to recalculate by
     */
    public void recalculateRows( double factor )
    {
        numberOfRows = (int) Math.ceil( numberOfElements * factor / rowWidth );
    }
}
