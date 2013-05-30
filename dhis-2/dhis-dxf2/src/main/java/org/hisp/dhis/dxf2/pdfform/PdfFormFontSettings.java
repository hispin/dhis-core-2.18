package org.hisp.dhis.dxf2.pdfform;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import com.lowagie.text.Font;
import java.awt.*;

public class PdfFormFontSettings
{
    public final static int FONTTYPE_BODY = 0;
    public final static int FONTTYPE_TITLE = 1;
    public final static int FONTTYPE_DESCRIPTION = 2;
    public final static int FONTTYPE_SECTIONHEADER = 3;
    public final static int FONTTYPE_FOOTER = 4;

    private final static float FONTSIZE_BODY = 10;
    private final static float FONTSIZE_TITLE = 16;
    private final static float FONTSIZE_DESCRIPTION = 11;
    private final static float FONTSIZE_SECTIONHEADER = 14;
    private final static float FONTSIZE_FOOTER = 8;

    private final static String FONTFAMILY = "HELVETICA";

    private Font fontBody;
    private Font fontTitle;
    private Font fontDescription;
    private Font fontSectionHeader;
    private Font fontFooter;

    public PdfFormFontSettings()
    {
        fontBody = createFont( FONTTYPE_BODY );
        fontTitle = createFont( FONTTYPE_TITLE );
        fontDescription = createFont( FONTTYPE_DESCRIPTION );
        fontSectionHeader = createFont( FONTTYPE_SECTIONHEADER );
        fontFooter = createFont( FONTTYPE_FOOTER );
    }

    //TODO use map instead of fixed properties
    
    public void setFont( int fontType, Font font )
    {
        switch ( fontType )
        {
            case FONTTYPE_BODY:
                fontBody = font;
                break;
            case FONTTYPE_TITLE:
                fontTitle = font;
                break;
            case FONTTYPE_DESCRIPTION:
                fontDescription = font;
                break;
            case FONTTYPE_SECTIONHEADER:
                fontSectionHeader = font;
                break;
            case FONTTYPE_FOOTER:
                fontFooter = font;
                break;
        }
    }

    public Font getFont( int fontType )
    {
        Font font = null;

        switch ( fontType )
        {
            case FONTTYPE_BODY:
                font = fontBody;
                break;
            case FONTTYPE_TITLE:
                font = fontTitle;
                break;
            case FONTTYPE_DESCRIPTION:
                font = fontDescription;
                break;
            case FONTTYPE_SECTIONHEADER:
                font = fontSectionHeader;
                break;
            case FONTTYPE_FOOTER:
                font = fontFooter;
                break;
        }

        return font;
    }

    private Font createFont( int fontType )
    {
        Font font = new Font();
        font.setFamily( FONTFAMILY );

        switch ( fontType )
        {
            case FONTTYPE_BODY:
                font.setSize( FONTSIZE_BODY );
                break;
            case FONTTYPE_TITLE:
                font.setSize( FONTSIZE_TITLE );
                font.setStyle( java.awt.Font.BOLD );
                font.setColor( new Color( 0, 0, 128 ) ); // Navy Color
                break;
            case FONTTYPE_DESCRIPTION:
                font.setSize( FONTSIZE_DESCRIPTION );
                font.setColor( Color.DARK_GRAY );
                break;
            case FONTTYPE_SECTIONHEADER:
                font.setSize( FONTSIZE_SECTIONHEADER );
                font.setStyle( java.awt.Font.BOLD );
                font.setColor( new Color( 70, 130, 180 ) ); // Steel Blue Color
                break;
            case FONTTYPE_FOOTER:
                font.setSize( FONTSIZE_FOOTER );
                break;
            default:
                font.setSize( FONTSIZE_BODY );
                break;
        }

        return font;
    }
}
