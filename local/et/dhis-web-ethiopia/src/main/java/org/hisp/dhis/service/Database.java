package org.hisp.dhis.service;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author HISP
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Database
{
    public Connection mysqlConnect()
        throws Exception
    {
        Connection con = null;
        String userName = "root";
        String password = "";
        String urlForConnection = "jdbc:mysql://localhost/dhis2";

        try
        {
            con = getConnection( urlForConnection, userName, password );
        }
        catch ( Exception e )
        {
            System.out.println( "Error mysql connect: " + e.getMessage() );

            throw e;
        }
        return con;
    }

    public Connection getConnection( String url, String usr, String pwd )
        throws Exception
    {

        Connection con = null;
        try
        {
            Class.forName( "com.mysql.jdbc.Driver" );
            con = DriverManager.getConnection( url, usr, pwd );
        }
        catch ( Exception e )
        {
            System.out.println( "Error getconnection: " + e.getMessage() );

            throw e;
        }
        return con;
    }

    public ResultSet getRecordset( Connection con, String sql )
    {

        ResultSet rs = null;
        Statement str;

        try
        {
            str = con.createStatement();
            rs = str.executeQuery( sql );
        }
        catch ( Exception e )
        {
            System.out.println( "Error getRecordset: " + e.getMessage() );
        }
        return rs;
    }

    public void executeSql( Connection con, String sql )
    {
        Statement stmSql;

        try
        {
            stmSql = con.createStatement();
            stmSql.execute( sql );
        }
        catch ( SQLException e )
        {
            System.out.println( "Error excuting sql statment: " + e.getMessage() );
        }
    }

}
