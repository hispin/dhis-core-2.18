package org.hisp.dhis.settings.action.system;

import org.hisp.dhis.dataapproval.DataApprovalLevel;
import org.hisp.dhis.dataapproval.DataApprovalLevelService;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.i18n.I18n;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ValidateApprovalLevelAction
    implements Action
{
    @Autowired
    private DataApprovalLevelService dataApprovalLevelService;
    
    @Autowired
    private DataElementCategoryService categoryService;

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private int organisationUnitLevel;

    public void setOrganisationUnitLevel( int organisationUnitLevel )
    {
        this.organisationUnitLevel = organisationUnitLevel;
    }

    private int categoryOptionGroupSet;

    public void setCategoryOptionGroupSet( int categoryOptionGroupSet )
    {
        this.categoryOptionGroupSet = categoryOptionGroupSet;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        CategoryOptionGroupSet catOptGroupSet = categoryService.getCategoryOptionGroupSet( categoryOptionGroupSet );
        
        DataApprovalLevel level = new DataApprovalLevel( organisationUnitLevel, catOptGroupSet );
        
        boolean exists = dataApprovalLevelService.dataApprovalLevelExists( level );

        if ( exists )
        {
            message = i18n.getString( "approval_level_is_already_defined" );

            return ERROR;
        }

        message = "ok";
        
        return SUCCESS;
    }
}
