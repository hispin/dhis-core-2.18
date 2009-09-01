package org.hisp.dhis.vn.chr.configuration.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

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

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public String getNumberOfRecords()
    {
        return numberOfRecords;
    }

    public void setNumberOfRecords( String numberOfRecords )
    {
        this.numberOfRecords = numberOfRecords;
    }

    // --------------------------------------------------------------------
    // Implements
    // --------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_CHR_NUMBER_OF_RECORDS, numberOfRecords );

        message = i18n.getString( "success" );

        return SUCCESS;
    }
}
