/*
 * Copyright (c) 2004-2008, University of Oslo
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
package org.hisp.dhis.integration.rims.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.hisp.dhis.integration.rims.api.RIMSTable;
import org.hisp.dhis.integration.rims.api.tables.RIMS_Aefi_Dtl;
import org.hisp.dhis.integration.rims.api.tables.RIMS_DIO_Position_Mst;
import org.hisp.dhis.integration.rims.api.tables.RIMS_Immun_Sessions_Dtl;
import org.hisp.dhis.integration.rims.api.tables.RIMS_Immun_Vaccine_Dtl;
import org.hisp.dhis.integration.rims.api.tables.RIMS_Opn_Stock_Vaccine_Dtl;
import org.hisp.dhis.integration.rims.api.tables.RIMS_Phc_Target_Dtl;
import org.hisp.dhis.integration.rims.api.tables.RIMS_Surveillance_Dtl;
import org.hisp.dhis.integration.rims.api.tables.RIMS_Vaccine_Supply_Dtl;
import org.hisp.dhis.integration.util.DataBaseConnection;

/**
 * Eventually this class will read configuration from an XML file.
 * 
 * @author Leif Arne Storset
 * @version $Id$
 */
public class Configuration
{
   private HashMap<String, RIMSTable> tables = new HashMap<String, RIMSTable>();
   private List<String> monthlyFields = new ArrayList<String>();
   private List<String> yearlyFields = new ArrayList<String>();
private String connectionName;
   
    public Configuration( String connectionName )
    {
        setConnectionName( connectionName );
        init();
    }
    
    public Configuration()
    {
        init();
    }
    
    private void init()
    {
        // ---------------------------------------------------------------------
        // RIMS Tables
        // ---------------------------------------------------------------------
        RIMSTable[] phcTableArray = new RIMSTable[] {
            new RIMS_Immun_Vaccine_Dtl(), new RIMS_Immun_Sessions_Dtl(),
            new RIMS_Vaccine_Supply_Dtl(), new RIMS_Aefi_Dtl(),
            new RIMS_Surveillance_Dtl(), new RIMS_Phc_Target_Dtl(),
            new RIMS_Opn_Stock_Vaccine_Dtl(), new RIMS_DIO_Position_Mst() };
        for ( RIMSTable table : phcTableArray )
        {
            tables.put( table.getTableName(), table );
            table.setConfiguration( this );
        }
        
        // ---------------------------------------------------------------------
        // Fields
        // ---------------------------------------------------------------------
        // immun_session_dtl
        monthlyFields.add( "ses_planned" );
        monthlyFields.add( "ses_held" );
        monthlyFields.add( "ses_vac_rcvd" );
        monthlyFields.add( "ses_voln_eng" );
        monthlyFields.add( "anim_hired" );
        monthlyFields.add( "undser_area" );
        monthlyFields.add( "urban_slums" );
        monthlyFields.add( "ses_held_aanganwadi" );
        monthlyFields.add( "fully_immu_infants" );

        // immun_vaccine_dtl
        monthlyFields.add( "preg_women" );
        monthlyFields.add( "under1year_total" );
        monthlyFields.add( "under1year_male" );
        monthlyFields.add( "under1year_female" );
        monthlyFields.add( "over1year_total" );
        monthlyFields.add( "over1year_male" );
        monthlyFields.add( "over1year_female" );

        // vaccine_supply_dtl
        monthlyFields.add( "opn_balance" );
        monthlyFields.add( "vaccine_rcvd" );
        monthlyFields.add( "vaccine_consd" );
        monthlyFields.add( "vaccine_unused" );
        monthlyFields.add( "vaccine_bal" );
        monthlyFields.add( "vaccine_dispd" );
        monthlyFields.add( "vaccine_consd" );

        // aefi_dtl
        monthlyFields.add( "during_the_mnth" );
        
        // surveillance_dtl
        monthlyFields.add( "cases" );
        monthlyFields.add( "deaths" );
        
        // dio_position_mst
        monthlyFields.add( "dio_position" );
        monthlyFields.add( "blockphc_level" );
        monthlyFields.add( "phc_level" );
        monthlyFields.add( "sub_center" );
        monthlyFields.add( "session_sites" );
        monthlyFields.add( "budget_obal" );
        monthlyFields.add( "budget_received" );
        monthlyFields.add( "budget_released" );
        monthlyFields.add( "budget_disbursed" );
        monthlyFields.add( "budget_cbal" );
        monthlyFields.add( "utilized_received" );
        monthlyFields.add( "utilized_released" );
        monthlyFields.add( "utilizedtillpre_month" );
        monthlyFields.add( "utilizedduring_month" );
        monthlyFields.add( "cuml_exp" );
        monthlyFields.add( "ret_state" );
        monthlyFields.add( "balance" );
        
        // phc_target_dtl
        yearlyFields.add( "cur_target" );
        yearlyFields.add( "lst_target" );
        yearlyFields.add( "snd_target" );
        
        // opn_stock_vaccine_dtl
        yearlyFields.add( "opening_bal" );
    }

    public RIMSTable getTable( String name )
    {
        RIMSTable table = tables.get( name );
        if ( table == null ) 
        {
            throw new RuntimeException( "Table "+ name +" not found");
        }
        return table;
    }

    public Collection<RIMSTable> getTables()
    {
        return tables.values();
    }

    public Connection getConnection() throws SQLException
    {
        if ( connectionName == null )
        {
            throw new IllegalStateException( "Connection name not set" );
        }
        return (new DataBaseConnection())
            .openConnection( DataBaseConnection.RIMS, connectionName );
    }
    
    public void setConnectionName( String name )
    {
        connectionName = name;
    }
    
    public String getConnectionName()
    {
        return connectionName;
    }

    public String getStoredBy()
    {
        return "dhis-web-integration";
    }

    public List<String> getMonthlyFields()
    {
        return monthlyFields;
    }

    public List<String> getYearlyFields()
    {
        return yearlyFields;
    }
}
