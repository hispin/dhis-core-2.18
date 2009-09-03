package org.hisp.dhis.vn.chr.formreport.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormReport;
import org.hisp.dhis.vn.chr.FormReportService;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class UpdateFormReport
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependency
    // -----------------------------------------------------------------------------------------------

    private FormService formService;

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

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

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String chosenOperand;

    public void setChosenOperand( String chosenOperand )
    {
        this.chosenOperand = chosenOperand;
    }

    private String formula;

    public void setFormula( String formula )
    {
        this.formula = formula;
    }

    private Integer mainForm;

    public void setMainForm( Integer mainForm )
    {
        this.mainForm = mainForm;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        // create a new formReport
        FormReport formReport = formReportService.getFormReport( id.intValue() );

        // set name
        formReport.setName( CodecUtils.unescape( name ) );

        // set formula for the element
        formula = formula.toLowerCase();
        formReport.setFormula( formula );

        // get all forms
        Collection<Form> forms = formService.getAllForms();
        // forms used in the formula
        List<Form> formulaForms = new ArrayList<Form>();
        for ( Form form : forms )
        {
            String formName = form.getName().toLowerCase() + ".";
            if ( formula.contains( formName ) )
            {
                formulaForms.add( form );
            }
        }
        // set forms used in the formula
        Form main = formService.getForm( mainForm.intValue() );
        formulaForms.add( main );
        formReport.setForms( formulaForms );
        // set mainForm used to identify statistics-form
        formReport.setMainForm( main );

        // set operand of dataelement
        formReport.setOperand( chosenOperand );

        // update the form into database
        formReportService.updateFormReport( formReport );

        message = i18n.getString( "success" );

        return SUCCESS;
    }
}
