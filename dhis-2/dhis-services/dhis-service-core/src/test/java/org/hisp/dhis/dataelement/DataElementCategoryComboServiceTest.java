package org.hisp.dhis.dataelement;

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

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementCategoryComboServiceTest
    extends DhisSpringTest
{
    private DataElementCategoryCombo categoryComboA;
    private DataElementCategoryCombo categoryComboB;
    private DataElementCategoryCombo categoryComboC;
    
    private DataElementCategory categoryA;
    private DataElementCategory categoryB;
    private DataElementCategory categoryC;
    
    private List<DataElementCategory> categories;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        categories = new ArrayList<DataElementCategory>();
        
        categoryA = new DataElementCategory( "CategoryA" );
        categoryB = new DataElementCategory( "CategoryB" );
        categoryC = new DataElementCategory( "CategoryC" );
        
        categoryService.addDataElementCategory( categoryA );
        categoryService.addDataElementCategory( categoryB );
        categoryService.addDataElementCategory( categoryC );
        
        categories.add( categoryA );
        categories.add( categoryB );
        categories.add( categoryC );        
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddGet()
    {        
        categoryComboA = new DataElementCategoryCombo( "CategoryComboA", categories );
        categoryComboB = new DataElementCategoryCombo( "CategoryComboB", categories );
        categoryComboC = new DataElementCategoryCombo( "CategoryComboC", categories );
        
        int idA = categoryService.addDataElementCategoryCombo( categoryComboA );
        int idB = categoryService.addDataElementCategoryCombo( categoryComboB );
        int idC = categoryService.addDataElementCategoryCombo( categoryComboC );
        
        assertEquals( categoryComboA, categoryService.getDataElementCategoryCombo( idA ) );
        assertEquals( categoryComboB, categoryService.getDataElementCategoryCombo( idB ) );
        assertEquals( categoryComboC, categoryService.getDataElementCategoryCombo( idC ) );
        
        assertEquals( categories, categoryService.getDataElementCategoryCombo( idA ).getCategories() );
        assertEquals( categories, categoryService.getDataElementCategoryCombo( idB ).getCategories() );
        assertEquals( categories, categoryService.getDataElementCategoryCombo( idC ).getCategories() );        
    }

    @Test
    public void testDelete()
    {
        categoryComboA = new DataElementCategoryCombo( "CategoryComboA", categories );
        categoryComboB = new DataElementCategoryCombo( "CategoryComboB", categories );
        categoryComboC = new DataElementCategoryCombo( "CategoryComboC", categories );
        
        int idA = categoryService.addDataElementCategoryCombo( categoryComboA );
        int idB = categoryService.addDataElementCategoryCombo( categoryComboB );
        int idC = categoryService.addDataElementCategoryCombo( categoryComboC );
        
        assertNotNull( categoryService.getDataElementCategoryCombo( idA ) );
        assertNotNull( categoryService.getDataElementCategoryCombo( idB ) );
        assertNotNull( categoryService.getDataElementCategoryCombo( idC ) );
        
        categoryService.deleteDataElementCategoryCombo( categoryComboA );

        assertNull( categoryService.getDataElementCategoryCombo( idA ) );
        assertNotNull( categoryService.getDataElementCategoryCombo( idB ) );
        assertNotNull( categoryService.getDataElementCategoryCombo( idC ) );
        
        categoryService.deleteDataElementCategoryCombo( categoryComboB );

        assertNull( categoryService.getDataElementCategoryCombo( idA ) );
        assertNull( categoryService.getDataElementCategoryCombo( idB ) );
        assertNotNull( categoryService.getDataElementCategoryCombo( idC ) );        
    }

    @Test
    public void testGetAll()
    {
        categoryComboA = new DataElementCategoryCombo( "CategoryComboA", categories );
        categoryComboB = new DataElementCategoryCombo( "CategoryComboB", categories );
        categoryComboC = new DataElementCategoryCombo( "CategoryComboC", categories );
        
        categoryService.addDataElementCategoryCombo( categoryComboA );
        categoryService.addDataElementCategoryCombo( categoryComboB );
        categoryService.addDataElementCategoryCombo( categoryComboC );
        
        Collection<DataElementCategoryCombo> categoryCombos = categoryService.getAllDataElementCategoryCombos();
        
        assertEquals( 4, categoryCombos.size() ); // Including default
        assertTrue( categoryCombos.contains( categoryComboA ) );
        assertTrue( categoryCombos.contains( categoryComboB ) );
        assertTrue( categoryCombos.contains( categoryComboC ) );        
    }
}
