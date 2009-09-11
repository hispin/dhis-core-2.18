package org.hisp.dhis.vn.chr.configuration.action;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class SetNumberOfRecordsAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private String numberOfRecords;

    public void setNumberOfRecords( String numberOfRecords )
    {
        this.numberOfRecords = numberOfRecords;
    }

    // --------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_CHR_NUMBER_OF_RECORDS, numberOfRecords );

        message = i18n.getString( "success" );

        return SUCCESS;
    }
}
