package org.hisp.dhis.databrowser;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.io.OutputStream;

import org.hisp.dhis.i18n.I18n;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public interface DataBrowserXLSService
{
    final String ID = DataBrowserXLSService.class.getName();

    /**
     * Returns an InputStream representing the tally sheet The InputStream will
     * give the opportunity to either print or save the tally sheet.
     * 
     * @param dataBrowserTitleName the title name of data browser
     * @param dataBrowserFromDate the start date
     * @param dataBrowserToDate the end date
     * @param dataBrowserPeriodType the period type
     * @param pageLayout the layout of page
     * @param fileName the output file name
     * @param fontSize the font size
     * @param dataBrowserTable the given instance of DataBrowserTable
     * @param out the output stream
     * @param i18n the internationalization
     * @param format the formatter for Date
     * @return void
     */
    void writeDataBrowserResult( String dataBrowserTitleName, String dataBrowserFromDate, String dataBrowserToDate,
        String dataBrowserPeriodType, int fontSize, DataBrowserTable dataBrowserTable, OutputStream out, I18n i18n );
}