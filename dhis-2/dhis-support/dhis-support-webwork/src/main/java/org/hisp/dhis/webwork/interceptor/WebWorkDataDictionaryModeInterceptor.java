package org.hisp.dhis.webwork.interceptor;

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

import java.util.HashMap;
import java.util.Map;

import ognl.NoSuchPropertyException;
import ognl.Ognl;

import org.hisp.dhis.options.datadictionary.DataDictionaryModeManager;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class WebWorkDataDictionaryModeInterceptor
    implements Interceptor
{
    private static final String KEY_DATA_DICTIONARY_MODE = "dataDictionaryMode";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataDictionaryModeManager dataDictionaryModeManager;

    public void setDataDictionaryModeManager( DataDictionaryModeManager dataDictionaryModeManager )
    {
        this.dataDictionaryModeManager = dataDictionaryModeManager;
    }
    
    // -------------------------------------------------------------------------
    // AroundInterceptor implementation
    // -------------------------------------------------------------------------

    public void destroy()
    {
        // TODO Auto-generated method stub
        
    }

    public void init()
    {
        // TODO Auto-generated method stub
        
    }

    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        Action action = (Action) invocation.getAction();
        
        String currentMode = dataDictionaryModeManager.getCurrentDataDictionaryMode();
        
        // ---------------------------------------------------------------------
        // Make the objects available for web templates
        // ---------------------------------------------------------------------
        
        Map<String, Object> templateMap = new HashMap<String, Object>( 1 );
        
        templateMap.put( KEY_DATA_DICTIONARY_MODE, currentMode );
        
        invocation.getStack().push( templateMap );
        
        // ---------------------------------------------------------------------
        // Set the objects in the action class if the properties exist
        // ---------------------------------------------------------------------

        Map<?, ?> contextMap = invocation.getInvocationContext().getContextMap();
        
        try
        {
            Ognl.setValue( KEY_DATA_DICTIONARY_MODE, contextMap, action, currentMode );
        }
        catch ( NoSuchPropertyException e )
        {
        }
        
        return invocation.invoke();
    }   
}
