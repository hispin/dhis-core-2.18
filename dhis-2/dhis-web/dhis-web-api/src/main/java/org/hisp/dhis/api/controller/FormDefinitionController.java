package org.hisp.dhis.api.controller;

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


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dxf2.metadata.ExportService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *  Exports a filtered view of the metadata sufficient for defining dataset reports
 *  Caveats: 
 *      - there is still much too much data.  
 *      - duplicated code
 *      - It would be better to render this as more form oriented view
 *      - It would be good to select individual forms based on dataset identifier
 * 
 *  @author Bob Jolliffe <bobjolliffe@gmail.com>
 */
@Controller
@RequestMapping( value = FormDefinitionController.RESOURCE_PATH )
public class FormDefinitionController
{
    public static final String RESOURCE_PATH = "/forms";

    private static final Log log = LogFactory.getLog( FormDefinitionController.class );

    @Autowired
    private ExportService exportService;

    @Autowired
    private ContextUtils contextUtils;

    @RequestMapping( value = FormDefinitionController.RESOURCE_PATH )
    public String export( @RequestParam Map<String, String> parameters, Model model )
    {
        // Ignore web options to avoid security exploit
        // WebOptions options = new WebOptions( parameters );
        WebOptions options = setOptions();
        
        MetaData metaData = exportService.getMetaData( options );

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", "export" );

        return "export";
    }

    @RequestMapping( value = FormDefinitionController.RESOURCE_PATH + ".xml", produces = "*/*" )
    public void exportXml( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        WebOptions options = setOptions();
        
        MetaData metaData = exportService.getMetaData( options );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, ContextUtils.CacheStrategy.NO_CACHE, "metaData.xml", true );

        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, ExportView.class );
    }
    
    /**
     * select only the metadata required to describe form definitions
     * 
     * @return the filtered options 
     */
    protected WebOptions setOptions()
    {      
        WebOptions options = new WebOptions(new HashMap<String,String>());
        options.setAssumeTrue( false);
        options.addOption( "categoryOptionCombos", "true" );
        options.addOption( "dataElements","true" );
        options.addOption( "dataSets", "true" );   
        return options;
    }
}
                                                