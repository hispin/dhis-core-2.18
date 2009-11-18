package org.hisp.dhis.dbmanager.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySQLDataBaseManager
    implements DataBaseManagerInterface
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private LineListElement llElement;

    public boolean createTable( String tableName, List<String> columnNames, List<String> autoIncrement,
        List<String> dataTypes, List<Integer> sizeOfColumns )
    {
        boolean tableCreated = false;

        PreparedStatement preparedStatement = null;

        String columnDefinition = "create table " + tableName + " ( ";
        // System.out.println(columnDefinition);

        for ( int i = 0; i < columnNames.size(); i++ )
        {

            if ( dataTypes.get( i ).equalsIgnoreCase( "DATE" ) || dataTypes.get( i ).equalsIgnoreCase( "text" ) )
            {
                if ( i < (columnNames.size() - 1) )
                {
                    columnDefinition += columnNames.get( i ) + " " + dataTypes.get( i ) + ",";
                }
                else
                {
                    columnDefinition += columnNames.get( i ) + " " + dataTypes.get( i );
                }
            }
            else
            {
                if ( i < (columnNames.size() - 1) )
                {
                    columnDefinition += columnNames.get( i ) + " " + dataTypes.get( i ) + "(" + sizeOfColumns.get( i )
                        + ") " + autoIncrement.get( i ) + ",";
                }
                else
                {
                    columnDefinition += columnNames.get( i ) + " " + dataTypes.get( i ) + "(" + sizeOfColumns.get( i )
                        + ") " + autoIncrement.get( i );
                }
            }
        }

        columnDefinition += ");";
        // System.out.println(columnDefinition);
        try
        {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            preparedStatement = connection.prepareStatement( columnDefinition );

            preparedStatement.execute();
            tableCreated = true;
        }
        catch ( SQLException e )
        {
            tableCreated = false;
            e.printStackTrace();

        }

        return tableCreated;
    }

    public void dropTable( String tableName )
    {
        try
        {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
    
            String columnDefinition = "";
    
            PreparedStatement preparedStatement = null;
    
            columnDefinition += "drop table " + tableName + " ;";
        
            preparedStatement = connection.prepareStatement( columnDefinition );

            preparedStatement.execute();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
    }

    public boolean checkDataFromTable( String tableName, LineListElement lineListElement )
    {
        boolean doNotDelete = false;

        Statement statement = null;

        try
        {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            
            System.out.println( "inside checkDataFromTable " );
            statement = connection.createStatement();

            ResultSet rs = statement.executeQuery( "select " + lineListElement.getShortName() + " from " + tableName );

            if ( rs != null )
            {
                int i = 0;
                rs.first();
                while ( rs.next() )
                {

                    if ( lineListElement.getDataType().equalsIgnoreCase( "string" ) )
                    {
                        if ( rs.getString( lineListElement.getShortName() ) != null )
                        {
                            doNotDelete = true;
                            break;
                        }
                        System.out.println( "string" + rs.getString( lineListElement.getShortName() ) );

                    }
                    else if ( lineListElement.getDataType().equalsIgnoreCase( "date" ) )
                    {
                        if ( rs.getDate( lineListElement.getShortName() ) != null )
                        {
                            doNotDelete = true;
                            break;
                        }
                        System.out.println( "date" + rs.getDate( lineListElement.getShortName() ) );
                    }
                    else if ( lineListElement.getDataType().equalsIgnoreCase( "int" ) )
                    {

                        if ( rs.getInt( lineListElement.getShortName() ) != 0 )
                        {
                            doNotDelete = true;
                            break;
                        }
                        System.out.println( "int" + rs.getInt( lineListElement.getShortName() ) );
                    }
                    else
                    {
                    }
                }
            }
            statement.close();
            System.out.println( "dataIsThere " + doNotDelete );
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            doNotDelete = false;
            System.out.println( "dataIsThere " + doNotDelete );
        }

        return doNotDelete;
    }

    public boolean updateTable( String tableName, List<LineListElement> removeList, List<LineListElement> addList )
    {

        String columnDefinition = "";

        Statement statement = null;

        columnDefinition += "alter table " + tableName;

        int columnAffected = 0;
        boolean rowUpdated = false;

        if ( addList != null )
        {
            if ( !(addList.isEmpty()) )
            {
                int i = 1;
                Iterator addListItr = addList.iterator();
                int size = addList.size();
                while ( addListItr.hasNext() )
                {
                    LineListElement lineListElement = (LineListElement) addListItr.next();
                    if ( lineListElement.getDataType().equalsIgnoreCase( "string" ) )
                    {
                        columnDefinition += " add " + lineListElement.getShortName() + " VARCHAR (255)";
                    }
                    else if ( lineListElement.getDataType().equalsIgnoreCase( "bool" ) )
                    {
                        columnDefinition += " add " + lineListElement.getShortName() + "BIT (1)";

                    }
                    else if ( lineListElement.getDataType().equalsIgnoreCase( "date" ) )
                    {
                        columnDefinition += " add " + lineListElement.getShortName() + " DATE";
                    }
                    else if ( lineListElement.getDataType().equalsIgnoreCase( "int" ) )
                    {
                        columnDefinition += " add " + lineListElement.getShortName() + " int (11)";
                    }
                    else
                    {
                    }
                    if ( i < size )
                    {
                        columnDefinition += " ,";
                        i++;
                    }
                }
            }
        }

        System.out.println( removeList.size() );
        if ( removeList != null )
        {
            if ( !(removeList.isEmpty()) )
            {

                int j = 1;
                Iterator removeListItr = removeList.iterator();
                int size = removeList.size();

                while ( removeListItr.hasNext() )
                {
                    LineListElement element = (LineListElement) removeListItr.next();
                    columnDefinition += " drop " + element.getShortName();
                    System.out.println( " element = " + element.getShortName() );
                    if ( j < size )
                    {
                        columnDefinition += " ,";
                        j++;
                    }
                }

                System.out.println( " columnDefinition = " + columnDefinition );
            }
        }
        System.out.println( columnDefinition );

        try
        {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            
            statement = connection.createStatement();

            columnAffected = statement.executeUpdate( columnDefinition );

            System.out.println( columnAffected );
            rowUpdated = true;
            statement.close();

        }
        catch ( SQLException e )
        {

            rowUpdated = false;
            e.printStackTrace();
        }

        return rowUpdated;
    }

    public List<LineListDataValue> getFromLLTable( String tableName, Source source, Period period )
    {
        boolean valueReceieved = false;
        String columnDefinition = "";
        Statement statement = null;

        // creating map of element and its values
        Map<String, String> llElementValuesMap = new HashMap<String, String>();

        List<LineListDataValue> llDataValues = new ArrayList<LineListDataValue>();
        // LineListDataValue llDataValue = new LineListDataValue();

        columnDefinition += "select * from " + tableName + " where periodid = " + period.getId() + " and sourceid = "
            + source.getId() + " order by recordnumber";

        Collection<LineListElement> elementsCollection = new ArrayList<LineListElement>();

        elementsCollection = lineListService.getLineListGroupByShortName( tableName ).getLineListElements();

        LineListElement element;
        String name = "";

        try
        {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            
            statement = connection.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE );

            ResultSet result = statement.executeQuery( columnDefinition );
            // System.out.println(columnDefinition);

            if ( result != null )
            {
                result.beforeFirst();

                while ( result.next() )
                {
                    LineListDataValue llDataValue = new LineListDataValue();
                    llDataValue.setRecordNumber( result.getInt( "recordnumber" ) );
                    Iterator it1 = elementsCollection.iterator();
                    while ( it1.hasNext() )
                    {
                        element = (LineListElement) it1.next();
                        name = element.getShortName() + ":" + result.getInt( "recordnumber" );
                        if ( element.getDataType().equalsIgnoreCase( "string" ) )
                        {
                            llElementValuesMap.put( name, result.getString( element.getShortName() ) );
                        }
                        else if ( element.getDataType().equalsIgnoreCase( "date" ) )
                        {
                            llElementValuesMap.put( name, result.getDate( element.getShortName() ).toString() );
                        }
                        else if ( element.getDataType().equalsIgnoreCase( "int" ) )
                        {
                            llElementValuesMap.put( name, Integer.toString( result.getInt( element.getShortName() ) ) );
                        }
                        // System.out.println("Key = "+name+ "Value = "+
                        // llElementValuesMap.get(name));
                    }

                    llDataValue.setLineListValues( llElementValuesMap );

                    llDataValues.add( llDataValue );
                }

            }

        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }

        return llDataValues;
    }

    public boolean insertLLValueIntoDb( List<LineListDataValue> llDataValuesList, String tableName )
    {
        boolean valueInserted = false;

        String columnDefinition = "";

        PreparedStatement preparedStatement = null;

        // System.out.println(" llDataValuesList size = " +
        // llDataValuesList.size());
        for ( LineListDataValue llDataValue : llDataValuesList )
        {

            columnDefinition = "insert into " + tableName + " (periodid,sourceid,storedby,lastupdated,";
            // get periodid from llDataValue

            Period period = llDataValue.getPeriod();

            Source source = llDataValue.getSource();

            // System.out.println(period.getId() + " " + source.getId());

            Map<String, String> elementValues = llDataValue.getLineListValues();
            Set<String> elements = elementValues.keySet();

            int size = elements.size();
            int i = 1;
            java.util.Date today = llDataValue.getTimestamp();
            long t = today.getTime();
            java.sql.Date date = new java.sql.Date( t );
            String values = " values (" + period.getId() + "," + source.getId() + ",'" + llDataValue.getStoredBy()
                + "','" + date + "',";
            for ( String elementName : elements )
            {

                // String name = elementName.replaceAll(" ", "_");
                LineListElement lineListElement = lineListService.getLineListElementByShortName( elementName );
                if ( i == size )
                {
                    columnDefinition += elementName + ")";

                    if ( lineListElement.getDataType().equalsIgnoreCase( "int" ) )
                    {

                        values += Integer.parseInt( elementValues.get( elementName ) );
                    }
                    else
                    {
                        values += "'" + elementValues.get( elementName ) + "'";
                    }
                }
                else
                {
                    columnDefinition += elementName + ",";
                    if ( lineListElement.getDataType().equalsIgnoreCase( "int" ) )
                    {
                        values += Integer.parseInt( elementValues.get( elementName ) ) + ",";
                    }
                    else
                    {
                        values += "'" + elementValues.get( elementName ) + "'" + ",";
                    }
                    i++;
                }

            }
            columnDefinition += values + ")";
            System.out.println( "Column Definition = " + columnDefinition );

            try
            {
                Connection connection = jdbcTemplate.getDataSource().getConnection();
                
                preparedStatement = connection.prepareStatement( columnDefinition );

                valueInserted = preparedStatement.execute();

                columnDefinition = "";
            }
            catch ( SQLException e )
            {
                e.printStackTrace();
            }

        }

        return valueInserted;

    }

    public boolean removeLLRecord( int recordId, String tableName )
    {
        boolean valueDeleted = false;

        String columnDefinition = " delete from " + tableName + " where recordnumber = " + recordId;

        PreparedStatement preparedStatement = null;

        try
        {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            
            preparedStatement = connection.prepareStatement( columnDefinition );

            valueDeleted = preparedStatement.execute();

            columnDefinition = "";
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }

        return valueDeleted;
    }

    public boolean updateLLValue( List<LineListDataValue> llDataValuesList, String tableName )
    {
        boolean valueUpdated = false;

        String columnDefinition = "";

        PreparedStatement preparedStatement = null;

        // System.out.println(" llDataValuesList size = " +
        // llDataValuesList.size());
        for ( LineListDataValue llDataValue : llDataValuesList )
        {

            // UPDATE table_name SET column1=value, column2=value2,... WHERE
            // some_column=some_value
            columnDefinition = "update " + tableName + " set ";
            // get periodid from llDataValue

            Map<String, String> elementValues = llDataValue.getLineListValues();
            Set<String> elements = elementValues.keySet();

            int size = elements.size();
            int i = 1;
            java.util.Date today = llDataValue.getTimestamp();
            long t = today.getTime();
            java.sql.Date date = new java.sql.Date( t );
            String whereClause = " where recordnumber = " + llDataValue.getRecordNumber();
            for ( String elementName : elements )
            {

                LineListElement lineListElement = lineListService.getLineListElementByShortName( elementName );
                if ( i == size )
                {

                    if ( lineListElement.getDataType().equalsIgnoreCase( "int" ) )
                    {
                        try
                        {
                            columnDefinition += elementName + " = "
                                + Integer.parseInt( elementValues.get( elementName ) );
                        }
                        catch ( Exception e )
                        {
                            JOptionPane.showMessageDialog( null, elementName + " cannot be null" );
                        }
                    }
                    else
                    {
                        columnDefinition += elementName + " = '" + elementValues.get( elementName ) + "'";
                    }
                }
                else
                {
                    if ( lineListElement.getDataType().equalsIgnoreCase( "int" ) )
                    {
                        try
                        {
                            columnDefinition += elementName + " = "
                                + Integer.parseInt( elementValues.get( elementName ) ) + ",";
                        }
                        catch ( Exception e )
                        {
                            JOptionPane.showMessageDialog( null, elementName + " cannot be null " );
                        }
                    }
                    else
                    {
                        columnDefinition += elementName + " = '" + elementValues.get( elementName ) + "'" + ",";
                    }
                    i++;
                }

            }
            columnDefinition += whereClause;
            // System.out.println("Column Definition = " + columnDefinition);

            try
            {
                Connection connection = jdbcTemplate.getDataSource().getConnection();
                
                preparedStatement = connection.prepareStatement( columnDefinition );

                valueUpdated = preparedStatement.execute();

                columnDefinition = "";
            }
            catch ( SQLException e )
            {
                e.printStackTrace();
            }

        }

        return valueUpdated;
    }
}
