package org.hisp.dhis.patient;

import java.sql.ResultSet;
import java.sql.Statement;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TableAlteror
{
    private static final Log log = LogFactory.getLog( TableAlteror.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    public void execute()
        throws Exception
    {
        updatePatientOrgunitAssociation();
        
        updateDOBType();
        
        executeSql("UPDATE patientidentifiertype SET type='" + PatientIdentifierType.VALUE_TYPE_TEXT +"' WHERE type IS NULL");
        
        executeSql("UPDATE program SET minDaysAllowedInputData=0 WHERE minDaysAllowedInputData IS NULL");
        
        executeSql("UPDATE program SET maxDaysAllowedInputData=0 WHERE maxDaysAllowedInputData IS NULL");
    }

    private void updatePatientOrgunitAssociation(){
        
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet isUpdated = statement
                .executeQuery( "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'patientidentifier' AND COLUMN_NAME = 'organisationunitid'" );

            if ( isUpdated.next() )
            {

                ResultSet resultSet = statement
                    .executeQuery( "SELECT patientid, organisationunitid FROM patientidentifier" );
                while ( resultSet.next() )
                {
                    executeSql( "UPDATE patient SET organisationunitid=" + resultSet.getInt( 2 ) + " WHERE patientid="
                        + resultSet.getInt( 1 ) );
                }
                
                executeSql( "ALTER TABLE patientidentifier DROP COLUMN organisationunitid" );
            }
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    private void updateDOBType(){
        StatementHolder holder = statementManager.getHolder();

        try
        {
            executeSql( "UPDATE patient SET dobType='A' WHERE birthdateestimated=true");
            
            executeSql("ALTER TABLE patient drop column birthdateestimated");
            
            executeSql("DELETE FROM validationcriteria where property='birthdateestimated'");
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    private int executeSql( String sql )
    {
        try
        {
            return statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( ex );

            return -1;
        }
    }
}
