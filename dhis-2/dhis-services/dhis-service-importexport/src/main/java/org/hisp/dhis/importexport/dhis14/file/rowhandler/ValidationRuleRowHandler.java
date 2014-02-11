package org.hisp.dhis.importexport.dhis14.file.rowhandler;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.importer.ValidationRuleImporter;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;

import com.ibatis.sqlmap.client.event.RowHandler;

public class ValidationRuleRowHandler
    extends ValidationRuleImporter
    implements RowHandler
{
    private ImportParams params;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public ValidationRuleRowHandler( BatchHandler<ValidationRule> batchHandler,
        ImportObjectService importObjectService, ValidationRuleService validationRuleService, ImportParams params )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.validationRuleService = validationRuleService;
        this.params = params;
    }

    // -------------------------------------------------------------------------
    // RowHandler implementation
    // -------------------------------------------------------------------------

    public void handleRow( Object object )
    {
        final ValidationRule validationRule = (ValidationRule) object;

        if ( validationRule.getCode() != null && validationRule.getCode().trim().length() == 0 )
        {
            validationRule.setCode( null );
        }

        // validationRule.setType( ValidationRule.TYPE_ABSOLUTE );

        importObject( validationRule, params );
    }
}