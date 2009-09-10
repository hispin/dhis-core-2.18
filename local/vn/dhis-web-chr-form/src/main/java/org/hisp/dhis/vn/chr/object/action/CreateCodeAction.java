package org.hisp.dhis.vn.chr.object.action;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

import com.opensymphony.xwork2.Action;

public class CreateCodeAction
    implements Action
{

    private FormManager formManager;

    public void setFormManager( FormManager formManager )
    {
        this.formManager = formManager;
    }

    private FormService formService;

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    private Integer formId;

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    private String code;

    public String getCode()
    {
        return code;
    }
 

    public String execute()
        throws Exception
    {
        // create code : MCH<yyMM>
        Date date = new Date();

        Format formatter = new SimpleDateFormat( "yyMM" );

        code = "MCH" + formatter.format( date );

        Form form = formService.getForm( formId.intValue() );

        // create code : xxxx
        code += formManager.createCode( form );

        return SUCCESS;
    }
}
