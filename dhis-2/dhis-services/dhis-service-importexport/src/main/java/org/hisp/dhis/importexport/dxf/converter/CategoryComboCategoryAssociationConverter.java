package org.hisp.dhis.importexport.dxf.converter;

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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.converter.AbstractGroupMemberConverter;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CategoryComboCategoryAssociationConverter
    extends AbstractGroupMemberConverter implements XMLConverter
{
    public static final String COLLECTION_NAME = "categoryComboCategoryAssociations";
    public static final String ELEMENT_NAME = "categoryComboCategoryAssociation";
    
    private static final String FIELD_CATEGORY_COMBO = "categoryCombo";
    private static final String FIELD_CATEGORY = "category";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private DataElementCategoryComboService categoryComboService;
    
    private DataElementCategoryService categoryService;
    
    private Map<Object, Integer> categoryComboMapping;
    
    private Map<Object, Integer> categoryMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public CategoryComboCategoryAssociationConverter( DataElementCategoryComboService categoryComboService,
        DataElementCategoryService categoryService )
    {
        this.categoryComboService = categoryComboService;
        this.categoryService = categoryService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryComboMapping the categoryComboMapping to use.
     * @param categoryMapping the categoryMapping to use.
     */
    public CategoryComboCategoryAssociationConverter( BatchHandler<GroupMemberAssociation> batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> categoryComboMapping,
        Map<Object, Integer> categoryMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.categoryComboMapping = categoryComboMapping;
        this.categoryMapping = categoryMapping;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElementCategoryCombo> categoryCombos = categoryComboService.getDataElementCategoryCombos( params.getCategoryCombos() );
        
        Collection<DataElementCategory> categories = categoryService.getDataElementCategories( params.getCategories() );
        
        if ( categoryCombos != null && categoryCombos.size() > 0 && categories != null && categories.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataElementCategoryCombo categoryCombo : categoryCombos )
            {
                if ( categoryCombo.getCategories() != null )
                {
                    for ( DataElementCategory category : categoryCombo.getCategories() )
                    {
                        if ( categories.contains( category ) )
                        {
                            writer.openElement( ELEMENT_NAME );
                            
                            writer.writeElement( FIELD_CATEGORY_COMBO, String.valueOf( categoryCombo.getId() ) );
                            writer.writeElement( FIELD_CATEGORY, String.valueOf( category.getId() ) );
                            
                            writer.closeElement();
                        }
                    }
                }
            }
            
            writer.closeElement();
        }
    }

    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final GroupMemberAssociation association = new GroupMemberAssociation( AssociationType.SET );
            
            association.setGroupId( categoryComboMapping.get( Integer.parseInt( values.get( FIELD_CATEGORY_COMBO ) ) ) );            
            association.setMemberId( categoryMapping.get( Integer.parseInt( values.get( FIELD_CATEGORY ) ) ) );
            
            read( association, GroupMemberType.CATEGORYCOMBO_CATEGORY, params );
        }
    }
}
