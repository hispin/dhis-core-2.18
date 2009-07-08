package org.hisp.dhis.den.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.den.api.LLDataValue;
import org.hisp.dhis.den.api.LLDataValueStore;
import org.hisp.dhis.den.util.DBConnection;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;

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
