package org.hisp.dhis.vn.chr.formreport.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormReport;
import org.hisp.dhis.vn.chr.FormReportService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;
import org.hisp.dhis.vn.chr.comparator.FormReportNameComparator;

public class ListFormReports
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------
    private FormReportService formReportService;

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------
    private Integer id; // element's id

    private Form form;

    private List<FormReport> formReports;

    // -----------------------------------------------------------------------------------------------
    // Getters && Setters
    // -----------------------------------------------------------------------------------------------
    public void setId( Integer id )
    {
        this.id = id;
    }

    public Form getForm()
    {
        return form;
    }

    public List<FormReport> getFormReports()
    {
        return formReports;
    }

    public void setFormReportService( FormReportService formReportService )
    {
        this.formReportService = formReportService;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        try
        {

            formReports = new ArrayList<FormReport>( formReportService.getAllFormReports() );

            Collections.sort( formReports, new FormReportNameComparator() );

            message = i18n.getString( "success" );

        }
        catch ( Exception ex )
        {

            message = i18n.getString( "error" ) + "\n" + ex.getMessage();

        }

        return SUCCESS;
    }

}
