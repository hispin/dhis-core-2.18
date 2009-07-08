package org.hisp.dhis.den.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.hisp.dhis.den.api.LLDataValue;
import org.hisp.dhis.den.util.DBConnection;

public class MYSQLLLDataValueStore 
{

    private DBConnection dbConnection;
    
    public void setDbConnection( DBConnection dbConnection )
    {
        this.dbConnection = dbConnection;
    }

    public void addDataValue( LLDataValue dataValue ) throws Exception
    {
    	//Connection con = (new DBConnection()).openConnection();        
        
        Connection con = dbConnection.openConnection();
    	String query = "INSERT INTO lldatavalue VALUES ()";
    	
    	PreparedStatement pst = con.prepareStatement(query);
    	
    }
}
