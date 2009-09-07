package org.hisp.dhis.dbmanager;

import java.util.List;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;


public interface DataBaseManagerInterface
{
    public boolean createTable( String tableName, List<String> columnNames, List<String> autoIncrement, List<String> dataTypes,
        List<Integer> sizeOfColumns );
    
    public void dropTable( String tableName);

    public boolean checkDataFromTable( String tableName, LineListElement lineListElement);

    public boolean updateTable( String tableName, List<LineListElement> removeList, List<LineListElement> addList );

    public boolean insertLLValueIntoDb( List<LineListDataValue> llDataValuesList, String tableName );

    //public List<Map<String, String>> getFromLLTable( String tableName, int source, int  period );
    public List<LineListDataValue> getFromLLTable( String tableName, Source source, Period  period );

    public boolean removeLLRecord( int recordId , String tableName );

    public boolean updateLLValue( List<LineListDataValue> llDataValuesList, String tableName );
}
