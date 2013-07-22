package org.hisp.dhis.api.view;

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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.system.util.PredicateUtils;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class PdfView extends AbstractPdfView
{
    private Font boldFont = FontFactory.getFont( FontFactory.HELVETICA_BOLD );

    @Override
    protected void buildPdfDocument( Map<String, Object> model, Document document, PdfWriter writer,
        HttpServletRequest request, HttpServletResponse response ) throws DocumentException
    {
        Object object = model.get( "model" );

        if ( WebMetaData.class.isAssignableFrom( object.getClass() ) )
        {
            WebMetaData webMetaData = (WebMetaData) object;
            buildList( webMetaData, document );
        }
        else
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;
            buildSingleObject( identifiableObject, document );
        }
    }

    private void buildList( WebMetaData webMetaData, Document document ) throws DocumentException
    {
        Collection<Field> fields = ReflectionUtils.collectFields( WebMetaData.class, PredicateUtils.idObjectCollections );

        for ( Field field : fields )
        {
            Collection<IdentifiableObject> col = ReflectionUtils.invokeGetterMethod( field.getName(), webMetaData );

            if ( col.isEmpty() )
            {
                continue;
            }

            PdfPTable table = new PdfPTable( 2 );
            table.setSpacingAfter( 10 );

            renderIdentifiableObjectRowHeader( table );

            for ( IdentifiableObject identifiableObject : col )
            {
                renderIdentifiableObjectRow( identifiableObject, table );
            }

            document.add( table );
        }
    }

    private void buildSingleObject( IdentifiableObject identifiableObject, Document document ) throws DocumentException
    {
        PdfPTable table = new PdfPTable( 2 );
        table.setSpacingAfter( 10 );
        renderIdentifiableObject( identifiableObject, table );
        document.add( table );
    }

    private void renderIdentifiableObject( IdentifiableObject identifiableObject, PdfPTable table )
    {
        table.addCell( new Phrase( "Name", boldFont ) );
        table.addCell( identifiableObject.getDisplayName() );

        table.addCell( new Phrase( "Uid", boldFont ) );
        table.addCell( identifiableObject.getUid() );

        table.addCell( new Phrase( "Code", boldFont ) );
        table.addCell( identifiableObject.getCode() );

        table.addCell( new Phrase( "Created", boldFont ) );
        table.addCell( identifiableObject.getCreated().toString() );

        table.addCell( new Phrase( "LastUpdated", boldFont ) );
        table.addCell( identifiableObject.getLastUpdated().toString() );
    }

    private void renderIdentifiableObjectRowHeader( PdfPTable table ) throws DocumentException
    {
        table.setWidths( new int[]{ 3, 1 } );

        PdfPCell nameCell = new PdfPCell( new Phrase( "Name", boldFont ) );
        nameCell.setHorizontalAlignment( Element.ALIGN_LEFT );
        table.addCell( nameCell );

        PdfPCell uidCell = new PdfPCell( new Phrase( "UID", boldFont ) );
        uidCell.setHorizontalAlignment( Element.ALIGN_LEFT );
        table.addCell( uidCell );
    }

    private void renderIdentifiableObjectRow( IdentifiableObject identifiableObject, PdfPTable table )
    {
        PdfPCell nameCell = new PdfPCell( new Phrase( identifiableObject.getDisplayName() ) );
        nameCell.setHorizontalAlignment( Element.ALIGN_LEFT );
        table.addCell( nameCell );

        PdfPCell uidCell = new PdfPCell( new Phrase( identifiableObject.getUid() ) );
        uidCell.setHorizontalAlignment( Element.ALIGN_RIGHT );
        table.addCell( uidCell );
    }
}
