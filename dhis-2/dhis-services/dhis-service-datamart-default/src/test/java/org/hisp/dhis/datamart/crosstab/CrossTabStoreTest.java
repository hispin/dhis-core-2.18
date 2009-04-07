package org.hisp.dhis.datamart.crosstab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datamart.crosstab.jdbc.CrossTabStore;

public class CrossTabStoreTest
    extends DhisConvenienceTest
{
    private CrossTabStore crossTabStore;
    
    private List<Operand> operands;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        crossTabStore = (CrossTabStore) getBean( CrossTabStore.ID );
        
        operands = new ArrayList<Operand>();
        operands.add( new Operand( 1, 1 ) );
        operands.add( new Operand( 1, 2 ) );
        operands.add( new Operand( 2, 1 ) );
        operands.add( new Operand( 2, 2 ) );        
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    public void testCreateGetCrossTabTable()
    {
        crossTabStore.createCrossTabTable( operands );
        
        Map<Integer, String> columnNames = crossTabStore.getCrossTabTableColumns();
        
        assertEquals( 6, columnNames.size() );
        assertEquals( "periodid", columnNames.get( 1 ) );
        assertEquals( "sourceid", columnNames.get( 2 ) );
        assertEquals( "de1_1", columnNames.get( 3 ) );
        assertEquals( "de1_2", columnNames.get( 4 ) );
        assertEquals( "de2_1", columnNames.get( 5 ) );
        assertEquals( "de2_2", columnNames.get( 6 ) );        
    }
    
    public void testDropCrossTabTable()
    {
        crossTabStore.createCrossTabTable( operands );
        
        crossTabStore.dropCrossTabTable();
    }
}
