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
import java.sql.ResultSet;

/**
 * @author HISP
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MonthlyMMData
{

    private Connection con = null;

    private Database d = new Database();

    public void addMMData( String strOrgUnit, String dtmDataPeriod, String ICD, int recType, String sex,
        String dataCategoryMM, int dblValue, long lastUpdated, String lastUser, int ysnCheck )
    {
        try
        {
            con = d.mysqlConnect();

            String sql = "INSERT INTO tblMonthlyMMData(strOrgUnit,dtmDataPeriod,ICD,recType,sex,dataCategoryMM,dblValue,lastUpdated,lastUser,ysnCheck ) VALUES('"
                + strOrgUnit
                + "','"
                + dtmDataPeriod
                + "','"
                + ICD
                + "',"
                + recType
                + ",'"
                + sex
                + "','"
                + dataCategoryMM + "'," + dblValue + "," + lastUpdated + ",'" + lastUser + "'," + ysnCheck + ")";
            // sql="SELECT * from tblMonthlyMMData";

            d.executeSql( con, sql );

        }
        catch ( Exception e )
        {
            System.out.println( "Error add MMData :" + e.getMessage() );
        }
    }

    public void deleteMMData( String strOrgUnit, String dtmDataPeriod, String ICD, int recType )
    {
        try
        {
            con = d.mysqlConnect();
        }
        catch ( Exception e )
        {
            System.out.println( "Error delete MMData :" + e.getMessage() );
        }
        String sql = "DELETE FROM tblMonthlyMMData WHERE " + "strOrgUnit='" + strOrgUnit + "' AND dtmDataPeriod='"
            + dtmDataPeriod + "' and ICD='" + ICD + "' and recType= " + recType;

        d.executeSql( con, sql );
    }

    public void updateMMData( String strOrgUnit, String dtmDataPeriod, String ICD, int recType, String sex,
        String dataCategoryMM, int dblValue )
    {
        try
        {
            con = d.mysqlConnect();
        }
        catch ( Exception e )
        {
            System.out.println( "Error delete MMData :" + e.getMessage() );
        }
        String sql = "UPDATE  tblMonthlyMMData SET dblValue= " + dblValue + " WHERE " + "strOrgUnit='" + strOrgUnit
            + "' AND dtmDataPeriod='" + dtmDataPeriod + "' and ICD='" + ICD + "' and recType= " + recType
            + " and sex='" + sex + "' and dataCategoryMM='" + dataCategoryMM + "'";

        d.executeSql( con, sql );
    }

    public ResultSet selectMMData( String strOrgUnit, String dtmDataPeriod, String ICD, int recType, char sex )
    {
        ResultSet rs = null;

        try
        {
            con = d.mysqlConnect();
        }
        catch ( Exception e )
        {
            System.out.println( "Error select MMData :" + e.getMessage() );
        }
        String sql = "SELECT * FROM tblMonthlyMMData WHERE " + "strOrgUnit='" + strOrgUnit + "' AND dtmDataPeriod='"
            + dtmDataPeriod + "' and ICD='" + ICD + "' and recType= " + recType + " and sex='" + sex + "'";
        try
        {
            rs = d.getRecordset( con, sql );
        }
        catch ( Exception e )
        {
            System.out.println( "Error on creating select recordset: " + e.getMessage() );
        }
        return rs;
    }

    public String[][] getMMData( String OrgUnit, String Period, int recType )
    {
        ResultSet rec = null;
        ResultSet rs = null;
        String[][] v;
        int i, j;
        int numResults = 1;

        String sql = "select distinct icd from tblmonthlymmdata WHERE " + "strOrgUnit='" + OrgUnit
            + "' AND dtmDataPeriod='" + Period + "' and recType= " + recType;

        try
        {
            con = d.mysqlConnect();
        }
        catch ( Exception e )
        {
            System.out.println( "Error select MMData :" + e.getMessage() );
        }

        rs = d.getRecordset( con, sql );

        try
        {
            rs.last();
            numResults = rs.getRow();
        }
        catch ( Exception e )
        {
            System.out.println( "Error using rs: " + e.getMessage() );
        }

        v = new String[numResults][27];

        for ( i = 0; i < numResults; i++ )
        {
            for ( j = 0; j < 27; j++ )
            {
                v[i][j] = "0";
            }
        }

        int count = 0;
        try
        {
            rs.beforeFirst();
            while ( rs.next() )
            {

                String ICD = rs.getString( "ICD" );
                v[count][0] = ICD;

                sql = "SELECT * FROM tblMonthlyMMData WHERE " + "strOrgUnit='" + OrgUnit + "' AND dtmDataPeriod='"
                    + Period + "' and ICD='" + ICD + "' and recType= " + recType;

                rec = d.getRecordset( con, sql );
                rec.beforeFirst();
                while ( rec.next() )
                {

                    // Integer x= rec.getInt("dblValue");
                    // String value= x.toString();
                    String value = rec.getString( "dblValue" );
                    String dCategoryMM = rec.getString( "dataCategoryMM" );
                    String s = rec.getString( "sex" );

                    if ( s.trim().equalsIgnoreCase( "F" ) )
                    {
                        if ( dCategoryMM.trim().equalsIgnoreCase( "<1" ) )
                            v[count][1] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "1-4" ) )
                            v[count][2] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "5-14" ) )
                            v[count][3] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "15-44" ) )
                            v[count][4] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "45-64" ) )
                            v[count][5] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "65+" ) )
                            v[count][6] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Repeat" ) )
                            v[count][13] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "TLS" ) )
                            v[count][15] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Cured" ) )
                            v[count][17] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Improved" ) )
                            v[count][19] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Not Improvement" ) )
                            v[count][21] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Worse" ) )
                            v[count][23] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Dead" ) )
                            v[count][25] = value;
                    }
                    else
                    {
                        if ( dCategoryMM.trim().equalsIgnoreCase( "<1" ) )
                            v[count][7] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "1-4" ) )
                            v[count][8] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "5-14" ) )
                            v[count][9] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "15-44" ) )
                            v[count][10] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "45-64" ) )
                            v[count][11] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "65+" ) )
                            v[count][12] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Repeat" ) )
                            v[count][14] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "TLS" ) )
                            v[count][16] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Cured" ) )
                            v[count][18] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Improved" ) )
                            v[count][20] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Not Improvement" ) )
                            v[count][22] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Worse" ) )
                            v[count][24] = value;
                        else if ( dCategoryMM.trim().equalsIgnoreCase( "Dead" ) )
                            v[count][26] = value;

                    }
                }
                count++;
            }
        }
        catch ( Exception e )
        {
            System.out.println( "Error on accessing rs: " + e.getMessage() );
        }

        return v;
    }

    public String[][] getMMType()
    {
        ResultSet rs = null;
        String[][] v;
        int i, j;
        int numResults = 1;

        String sql = "select * from tlkMMType";

        try
        {
            con = d.mysqlConnect();
        }
        catch ( Exception e )
        {
            System.out.println( "Error select MMData :" + e.getMessage() );
        }

        rs = d.getRecordset( con, sql );

        try
        {
            rs.last();
            numResults = rs.getRow();
        }
        catch ( Exception e )
        {
            System.out.println( "Error using rs: " + e.getMessage() );
        }

        v = new String[numResults][2];

        for ( i = 0; i < numResults; i++ )
            for ( j = 0; j < 2; j++ )
                v[i][j] = "";

        int count = 0;
        try
        {
            rs.beforeFirst();
            while ( rs.next() )
            {
                v[count][0] = rs.getString( "recType" );
                v[count][1] = rs.getString( "Description" );
                count++;
            }
        }
        catch ( Exception e )
        {
            System.out.println( "Error on accessing rs: " + e.getMessage() );
        }
        return v;
    }

    public String[][] getICDData()
    {
        ResultSet rs = null;
        String[][] v;
        int i;
        int numResults = 1;

        String sql = "select  icd,description from tlkICD order by icd";

        try
        {
            con = d.mysqlConnect();
        }
        catch ( Exception e )
        {
            System.out.println( "Error select MMData :" + e.getMessage() );
        }

        rs = d.getRecordset( con, sql );

        try
        {
            rs.last();
            numResults = rs.getRow();
        }
        catch ( Exception e )
        {
            System.out.println( "Error using rs: " + e.getMessage() );
        }

        v = new String[numResults][2];

        for ( i = 0; i < numResults; i++ )
        {
            v[i][0] = "0";
            v[i][1] = "0";
        }

        int count = 0;
        try
        {
            rs.beforeFirst();
            while ( rs.next() )
            {
                v[count][0] = rs.getString( "ICD" );
                v[count][1] = rs.getString( "Description" );
                count++;
            }
        }
        catch ( Exception e )
        {
            System.out.println( "Error on accessing rs: " + e.getMessage() );
        }
        return v;
    }
    /*
     * public String [] getICDDataDes() { ResultSet rec = null; ResultSet rs =
     * null; String [] v ; int i; int numResults=1;
     * 
     * String sql="select description from tlkICD order by icd";
     * 
     * try{ con=d.mysqlConnect(); } catch(Exception e){
     * System.out.println("Error select MMData :" + e.getMessage()); }
     * 
     * rs = d.getRecordset(con,sql);
     * 
     * try{ rs.last(); numResults = rs.getRow(); }catch(Exception
     * e){System.out.println("Error using rs: "+ e.getMessage());}
     * 
     * 
     * 
     * 
     * v= new String[numResults];
     * 
     * for(i=0;i<numResults;i++) v[i]="0";
     * 
     * int count=0; try{ rs.beforeFirst(); while(rs.next()){
     * v[count]=rs.getString("description"); count++; } }catch(Exception e){
     * System.out.println("Error on accessing rs: " + e.getMessage()); } return
     * v; }
     * 
     * public static void main(String[] args){
     * 
     * String [][] v; MonthlyMMData mm = new MonthlyMMData(); v=
     * mm.getMMData("aa Abo liyu Clinic","2005-12-01 00:00:00",4); for(int i=0;i<v.length;i++){
     * for(int j=0;j<v[i].length;j++) System.out.print(v[i][j] + ",");
     * System.out.println(); }
     * 
     * 
     * //MonthlyMMData mm = new MonthlyMMData(); try{ //mm.addMMData(633,"aa Abo
     * liyu Clinic","2005-12-01 00:00:00","001",4,'F',"1-4",7,38785,"selam",0);
     * mm.deleteMMData("aa Abo liyu Clinic","2005-12-01 00:00:00","001",4);
     *  } catch(NullPointerException e) { System.out.println("Null pointer
     * Exception error: " + e.getMessage()); }
     *  }
     */

}
