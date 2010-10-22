package org.hisp.dhis.editor;

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
import net.fckeditor.FCKeditor;

import org.apache.struts2.ServletActionContext;

/**
 * @author Tran Thanh Tri
 * @version $Id: FCKEditorManager
 */
public class FCKEditorManager
    implements EditorManager
{
    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

    private String basePath;

    public void setBasePath( String basePath )
    {
        this.basePath = basePath;
    }

    private String skin;

    public void setSkin( String skin )
    {
        this.skin = skin;
    }

    public String create( String name, String width, String height )
    {
        FCKeditor editor = new FCKeditor( ServletActionContext.getRequest(), name );
        editor.setConfig( "SkinPath", "skins/" + skin + "/" );
        editor.setBasePath( basePath );
        editor.setWidth( width );
        editor.setHeight( height );
        editor.setValue( value );
        return editor.createHtml();

    }

}
