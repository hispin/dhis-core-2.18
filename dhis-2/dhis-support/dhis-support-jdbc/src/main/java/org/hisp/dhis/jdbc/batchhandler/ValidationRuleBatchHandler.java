package org.hisp.dhis.jdbc.batchhandler;

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.validation.ValidationRule;

public class ValidationRuleBatchHandler
extends AbstractBatchHandler<ValidationRule>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public ValidationRuleBatchHandler( JdbcConfiguration config )
    {
        super( config, false, false );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "dataelement" );
    }

    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "dataelementid" );
    }

    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "validationruleid" );
    }

    @Override
    protected void setIdentifierValues( ValidationRule validationRule )
    {        
        statementBuilder.setIdentifierValue( validationRule.getId() );
    }

    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "name" );
        statementBuilder.setUniqueColumn( "code" );
    }

    protected void setUniqueValues( ValidationRule validationRule )
    {
        statementBuilder.setUniqueValue( validationRule.getName() );
        statementBuilder.setUniqueValue( validationRule.getCode() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "uid" );
        statementBuilder.setColumn( "name" );
        statementBuilder.setColumn( "code" );
        statementBuilder.setColumn( "description" );
        statementBuilder.setColumn( "operator" );
        statementBuilder.setColumn( "type" );
    }
    
    protected void setValues( ValidationRule validationRule )
    {
        statementBuilder.setValue( validationRule.getUid() );
        statementBuilder.setValue( validationRule.getName() );
        statementBuilder.setValue( validationRule.getCode() );
        statementBuilder.setValue( validationRule.getDescription() );
        statementBuilder.setValue( validationRule.getOperator() );
        statementBuilder.setValue( validationRule.getType() );
    }

}