package org.hisp.dhis.rt.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hisp.dhis.rt.generators.DesignGenerator;
import org.hisp.dhis.rt.generators.GeneratorException;
import org.hisp.dhis.rt.report.Element;
import org.hisp.dhis.rt.report.ReportStoreException;

/**
 * @author Lars Helge Overland
 * @version $Id: GenerateDesignAction.java 2871 2007-02-20 16:04:11Z andegje $
 */
public class GenerateDesignAction
    extends AbstractAction
{
    // -----------------------------------------------------------------------
    // Execute
    // -----------------------------------------------------------------------
    
    public String execute()
        throws ReportStoreException, GeneratorException
    {
        if ( report() )
        {
            Collection<Element> reportCollection = reportStore.getAllReportElements( report );
            
            List<String> textFieldNames = new ArrayList<String>();
            
            List<String> staticTextNames = new ArrayList<String>();
            
            for ( Element element : reportCollection )
            {
                textFieldNames.add( element.getElementKey() );
                
                staticTextNames.add( element.getElementName() );
            }
            
            DesignGenerator.generateDesign( report, textFieldNames, staticTextNames, designTemplate, chartTemplate );
            
            message = i18n.getString( "design_generated" );
        }
            
        return SUCCESS;
    }

}







