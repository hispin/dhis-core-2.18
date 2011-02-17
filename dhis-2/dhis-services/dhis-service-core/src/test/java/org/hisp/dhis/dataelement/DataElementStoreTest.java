package org.hisp.dhis.dataelement;

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.system.util.UUIdUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DataElementStoreTest.java 5742 2008-09-26 11:37:35Z larshelg $
 */
public class DataElementStoreTest
    extends DhisSpringTest
{
    private DataElementStore dataElementStore;
    
    private DataSetService dataSetService;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataElementStore = (DataElementStore) getBean( DataElementStore.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        dataSetService = (DataSetService) getBean( DataSetService.ID );
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    private DataElement setDataElementFields( DataElement dataElement, char uniqueCharacter )
    {
        dataElement.setUuid( UUIdUtils.getUUId() );
        dataElement.setName( "DataElement" + uniqueCharacter );
        dataElement.setAlternativeName( "DataElementAlternative" + uniqueCharacter );
        dataElement.setShortName( "DataElementShort" + uniqueCharacter );
        dataElement.setCode( "DataElementCode" + uniqueCharacter );
        dataElement.setDescription( "DataElementDescription" + uniqueCharacter );
        dataElement.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement.setType( DataElement.VALUE_TYPE_INT );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        
        return dataElement;
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataElement()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'A' );

        int idA = dataElementStore.addDataElement( dataElementA );
        int idB = dataElementStore.addDataElement( dataElementB );
        int idC = dataElementStore.addDataElement( dataElementC );

        try
        {
            // Should give unique constraint violation
            dataElementStore.addDataElement( dataElementD );
            fail();
        }
        catch ( Exception e )
        {
            // Expected
        }

        dataElementA = dataElementStore.getDataElement( idA );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementStore.getDataElement( idB );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        dataElementC = dataElementStore.getDataElement( idC );
        assertNotNull( dataElementC );
        assertEquals( idC, dataElementC.getId() );
        assertEquals( "DataElementC", dataElementC.getName() );
    }

    @Test
    public void testUpdateDataElement()
    {
        DataElement dataElementA = createDataElement( 'A' );
        int idA = dataElementStore.addDataElement( dataElementA );
        dataElementA = dataElementStore.getDataElement( idA );
        assertEquals( DataElement.VALUE_TYPE_INT, dataElementA.getType() );

        dataElementA.setType( DataElement.VALUE_TYPE_BOOL );
        dataElementStore.updateDataElement( dataElementA );
        dataElementA = dataElementStore.getDataElement( idA );
        assertNotNull( dataElementA.getType() );
        assertEquals( DataElement.VALUE_TYPE_BOOL, dataElementA.getType() );
    }

    @Test
    public void testDeleteAndGetDataElement()
        throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );

        int idA = dataElementStore.addDataElement( dataElementA );
        int idB = dataElementStore.addDataElement( dataElementB );
        int idC = dataElementStore.addDataElement( dataElementC );
        int idD = dataElementStore.addDataElement( dataElementD );

        assertNotNull( dataElementStore.getDataElement( idA ) );
        assertNotNull( dataElementStore.getDataElement( idB ) );
        assertNotNull( dataElementStore.getDataElement( idC ) );
        assertNotNull( dataElementStore.getDataElement( idD ) );

        dataElementA = dataElementStore.getDataElement( idA );
        dataElementB = dataElementStore.getDataElement( idB );
        dataElementC = dataElementStore.getDataElement( idC );
        dataElementD = dataElementStore.getDataElement( idD );

        dataElementStore.deleteDataElement( dataElementA );
        assertNull( dataElementStore.getDataElement( idA ) );
        assertNotNull( dataElementStore.getDataElement( idB ) );
        assertNotNull( dataElementStore.getDataElement( idC ) );
        assertNotNull( dataElementStore.getDataElement( idD ) );

        dataElementStore.deleteDataElement( dataElementB );
        assertNull( dataElementStore.getDataElement( idA ) );
        assertNull( dataElementStore.getDataElement( idB ) );
        assertNotNull( dataElementStore.getDataElement( idC ) );
        assertNotNull( dataElementStore.getDataElement( idD ) );

        dataElementStore.deleteDataElement( dataElementC );
        assertNull( dataElementStore.getDataElement( idA ) );
        assertNull( dataElementStore.getDataElement( idB ) );
        assertNull( dataElementStore.getDataElement( idC ) );
        assertNotNull( dataElementStore.getDataElement( idD ) );

        dataElementStore.deleteDataElement( dataElementD );
        assertNull( dataElementStore.getDataElement( idA ) );
        assertNull( dataElementStore.getDataElement( idB ) );
        assertNull( dataElementStore.getDataElement( idC ) );
        assertNull( dataElementStore.getDataElement( idD ) );
    }

    @Test
    public void testGetDataElementByUUID()
    {
        String uuid = UUIdUtils.getUUId();
        
        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setUuid( uuid );
        
        dataElementStore.addDataElement( dataElementA );
        
        dataElementA = dataElementStore.getDataElement( uuid );
        
        assertNotNull( dataElementA );
        assertEquals( dataElementA.getUuid(), uuid );
    }

    @Test
    public void testGetDataElementByName()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        int idA = dataElementStore.addDataElement( dataElementA );
        int idB = dataElementStore.addDataElement( dataElementB );

        dataElementA = dataElementStore.getDataElementByName( "DataElementA" );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementStore.getDataElementByName( "DataElementB" );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        DataElement dataElementC = dataElementStore.getDataElementByName( "DataElementC" );
        assertNull( dataElementC );
    }

    @Test
    public void testGetDataElementByAlternativeName()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        int idA = dataElementStore.addDataElement( dataElementA );
        int idB = dataElementStore.addDataElement( dataElementB );

        dataElementA = dataElementStore.getDataElementByAlternativeName( "DataElementAlternativeA" );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementStore.getDataElementByAlternativeName( "DataElementAlternativeB" );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        DataElement dataElementC = dataElementStore.getDataElementByAlternativeName( "DataElementAlternativeC" );
        assertNull( dataElementC );
    }

    @Test
    public void testGetDataElementByShortName()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        int idA = dataElementStore.addDataElement( dataElementA );
        int idB = dataElementStore.addDataElement( dataElementB );

        dataElementA = dataElementStore.getDataElementByShortName( "DataElementShortA" );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementStore.getDataElementByShortName( "DataElementShortB" );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        DataElement dataElementC = dataElementStore.getDataElementByShortName( "DataElementShortC" );
        assertNull( dataElementC );
    }
    
    @Test
    public void testGetAllDataElements()
    {
        assertEquals( 0, dataElementStore.getAllDataElements().size() );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );

        dataElementStore.addDataElement( dataElementA );
        dataElementStore.addDataElement( dataElementB );
        dataElementStore.addDataElement( dataElementC );
        dataElementStore.addDataElement( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementC );
        dataElementsRef.add( dataElementD );

        Collection<DataElement> dataElements = dataElementStore.getAllDataElements();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetAggregateableDataElements()
    {
        assertEquals( 0, dataElementStore.getAggregateableDataElements().size() );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );
        
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        dataElementB.setType( DataElement.VALUE_TYPE_BOOL );
        dataElementC.setType( DataElement.VALUE_TYPE_STRING );
        dataElementD.setType( DataElement.VALUE_TYPE_INT );

        dataElementStore.addDataElement( dataElementA );
        dataElementStore.addDataElement( dataElementB );
        dataElementStore.addDataElement( dataElementC );
        dataElementStore.addDataElement( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementD );

        Collection<DataElement> dataElements = dataElementStore.getAggregateableDataElements();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetAllActiveDataElements()
    {
        assertEquals( 0, dataElementStore.getAllActiveDataElements().size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setActive( true );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setActive( true );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setActive( true );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setActive( false );

        dataElementStore.addDataElement( dataElementA );
        dataElementStore.addDataElement( dataElementB );
        dataElementStore.addDataElement( dataElementC );
        dataElementStore.addDataElement( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementC );

        assertEquals( dataElementsRef.size() + 1, dataElementStore.getAllDataElements().size() );

        Collection<DataElement> dataElements = dataElementStore.getAllActiveDataElements();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetDataElementsByAggregationOperator()
    {
        assertEquals( 0, dataElementStore.getDataElementsByAggregationOperator(
            DataElement.AGGREGATION_OPERATOR_AVERAGE ).size() );
        assertEquals( 0, dataElementStore.getDataElementsByAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM )
            .size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );

        dataElementStore.addDataElement( dataElementA );
        dataElementStore.addDataElement( dataElementB );
        dataElementStore.addDataElement( dataElementC );
        dataElementStore.addDataElement( dataElementD );

        assertEquals( 1, dataElementStore.getDataElementsByAggregationOperator(
            DataElement.AGGREGATION_OPERATOR_AVERAGE ).size() );
        assertEquals( 3, dataElementStore.getDataElementsByAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM )
            .size() );
    }
    
    @Test
    public void testGetDataElementsByDomainType()
    {
        assertEquals( 0, dataElementStore.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_AGGREGATE ).size() );
        assertEquals( 0, dataElementStore.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_PATIENT ).size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );

        dataElementStore.addDataElement( dataElementA );
        dataElementStore.addDataElement( dataElementB );
        dataElementStore.addDataElement( dataElementC );
        dataElementStore.addDataElement( dataElementD );

        assertEquals( 1, dataElementStore.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_AGGREGATE ).size() );
        assertEquals( 3, dataElementStore.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_PATIENT ).size() );
    }

    @Test
    public void testGetDataElementsByType()
    {
        assertEquals( 0, dataElementStore.getDataElementsByType( DataElement.VALUE_TYPE_INT ).size() );
        assertEquals( 0, dataElementStore.getDataElementsByType( DataElement.VALUE_TYPE_BOOL ).size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setType( DataElement.VALUE_TYPE_BOOL );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setType( DataElement.VALUE_TYPE_BOOL );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setType( DataElement.VALUE_TYPE_BOOL );

        dataElementStore.addDataElement( dataElementA );
        dataElementStore.addDataElement( dataElementB );
        dataElementStore.addDataElement( dataElementC );
        dataElementStore.addDataElement( dataElementD );

        assertEquals( 1, dataElementStore.getDataElementsByType( DataElement.VALUE_TYPE_INT ).size() );
        assertEquals( 3, dataElementStore.getDataElementsByType( DataElement.VALUE_TYPE_BOOL ).size() );
    }

    @Test
    public void testGetDataElementAggregationLevels()
    {
        List<Integer> aggregationLevels = Arrays.asList( 3, 5 );
        
        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setAggregationLevels( aggregationLevels );
        
        int idA = dataElementStore.addDataElement( dataElementA );
        
        assertNotNull( dataElementStore.getDataElement( idA ).getAggregationLevels() );
        assertEquals( 2, dataElementStore.getDataElement( idA ).getAggregationLevels().size() );
        assertEquals( aggregationLevels, dataElementStore.getDataElement( idA ).getAggregationLevels() );
    }
        
    @Test
    public void testGetDataElementsZeroIsSignificant()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );

        dataElementA.setZeroIsSignificant( true );
        dataElementB.setZeroIsSignificant( true );
        
        dataElementStore.addDataElement( dataElementA );
        dataElementStore.addDataElement( dataElementB );
        dataElementStore.addDataElement( dataElementC );
        dataElementStore.addDataElement( dataElementD );
        
        Collection<DataElement> dataElements = dataElementStore.getDataElementsByZeroIsSignificant( true );
        
        assertTrue( equals( dataElements, dataElementA, dataElementB ) );
    }
    
    @Test
    public void testGetDataElements()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );
        DataElement dataElementE = createDataElement( 'E' );
        DataElement dataElementF = createDataElement( 'F' );
        
        dataElementStore.addDataElement( dataElementA );
        dataElementStore.addDataElement( dataElementB );
        dataElementStore.addDataElement( dataElementC );
        dataElementStore.addDataElement( dataElementD );
        dataElementStore.addDataElement( dataElementE );
        dataElementStore.addDataElement( dataElementF );
        
        DataSet dataSetA = createDataSet( 'A', new MonthlyPeriodType() );
        DataSet dataSetB = createDataSet( 'B', new MonthlyPeriodType() );
        
        dataSetA.getDataElements().add( dataElementA );
        dataSetA.getDataElements().add( dataElementC );
        dataSetA.getDataElements().add( dataElementF );
        dataSetB.getDataElements().add( dataElementD );
        dataSetB.getDataElements().add( dataElementF );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        
        Collection<DataSet> dataSets = new HashSet<DataSet>();
        dataSets.add( dataSetA );
        dataSets.add( dataSetB );
        
        Collection<DataElement> dataElements = dataElementStore.getDataElementsByDataSets( dataSets );
        
        assertNotNull( dataElements );
        assertEquals( 4, dataElements.size() );
        assertTrue( dataElements.contains( dataElementA ) );
        assertTrue( dataElements.contains( dataElementC ) );
        assertTrue( dataElements.contains( dataElementD ) );
        assertTrue( dataElements.contains( dataElementF ) );
    }

    // -------------------------------------------------------------------------
    // CalculatedDataElements
    // -------------------------------------------------------------------------

    @Ignore //TODO
    @Test
    public void testCalculatedDataElements()
    {
        DataElement deA = createDataElement('A');
        DataElement deB = createDataElement('B');
        DataElement deC = createDataElement('C');
        DataElement deD = createDataElement('D');
        DataElement deE = createDataElement('E');

        int deIdA = dataElementStore.addDataElement(deA);
        int deIdB = dataElementStore.addDataElement(deB);
        int deIdC = dataElementStore.addDataElement(deC);
        int deIdD = dataElementStore.addDataElement(deD);
        dataElementStore.addDataElement(deE);
        
        CalculatedDataElement cdeX = (CalculatedDataElement) setDataElementFields( new CalculatedDataElement (), 'X' );
        CalculatedDataElement cdeY = (CalculatedDataElement) setDataElementFields( new CalculatedDataElement (), 'Y' );

        Set<DataElement> dataElementsX = new HashSet<DataElement> ();
        dataElementsX.add(deA);
        dataElementsX.add(deB);
        Expression expressionX = new Expression ( "["+deIdA+"] * 2 + ["+deIdB+"] * 3", "foo", dataElementsX );
        cdeX.setExpression(expressionX);
        cdeX.setSaved(true);
        dataElementStore.addDataElement(cdeX);
        
        Set<DataElement> dataElementsY = new HashSet<DataElement> ();
        dataElementsY.add(deC);
        dataElementsY.add(deD);
        Expression expressionY = new Expression ( "["+deIdC+"] * 2 + ["+deIdD+"] * 3", "foo", dataElementsY );
        cdeY.setExpression(expressionY);
        cdeY.setSaved(true);
        dataElementStore.addDataElement(cdeY);
        
        Collection<CalculatedDataElement> cdes = dataElementStore.getAllCalculatedDataElements();
        assertEquals( 2, cdes.size() );
        
        //CalculatedDataElement cde;
        CalculatedDataElement cde = dataElementStore.getCalculatedDataElementByDataElement( deA );
        assertNotNull(cde);
        assertEquals("DataElementX", cde.getName() );
        
        cde = dataElementStore.getCalculatedDataElementByDataElement( deE );
        assertNull(cde);
        
        Set<DataElement> dataElements = new HashSet<DataElement> ();
        dataElements.add(deA);
        cdes = dataElementStore.getCalculatedDataElementsByDataElements( dataElements );
        assertEquals( 1, cdes.size() );
        assertEquals("DataElementX", cdes.iterator().next().getName());
        
        dataElements.add(deC);
        cdes = dataElementStore.getCalculatedDataElementsByDataElements( dataElements );
        assertEquals( 2, cdes.size() );

        Iterator<CalculatedDataElement> iterator = cdes.iterator();
        assertEquals( iterator.next().getName(), "DataElementX" );
        assertEquals( iterator.next().getName(), "DataElementY" );
        
        //Make sure the results are unique
        dataElements.add(deB);
        cdes = dataElementStore.getCalculatedDataElementsByDataElements( dataElements );
        assertEquals( 2, cdes.size() );

        iterator = cdes.iterator();
        assertEquals( iterator.next().getName(), "DataElementX" );
        assertEquals( iterator.next().getName(), "DataElementY" );

        //Check that no other data elements are returned
        dataElements.add(deE);
        cdes = dataElementStore.getCalculatedDataElementsByDataElements( dataElements );
        assertEquals( 2, cdes.size() );
    }
}
