package org.hisp.dhis.vn.chr.formreport.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.FormReport;
import org.hisp.dhis.vn.chr.FormReportService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class GetFormReportAction
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------
    private FormReportService formReportService;

    public void setFormReportService( FormReportService formReportService )
    {
        this.formReportService = formReportService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private FormReport formReport;

    public FormReport getFormReport()
    {
        return formReport;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
    {
        formReport = formReportService.getFormReport( id.intValue() );

        return SUCCESS;

    }
}
