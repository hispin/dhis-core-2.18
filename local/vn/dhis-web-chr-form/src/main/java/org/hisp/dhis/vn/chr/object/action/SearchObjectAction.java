package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

public class SearchObjectAction
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

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

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private ElementService elementService;

    public void setElementService( ElementService elementService )
    {
        this.elementService = elementService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer formId;

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    private String keyword;

    public void setKeyword( String keyword )
    {
        this.keyword = keyword;
    }

    private Form form;

    public Form getForm()
    {
        return form;
    }

    private ArrayList<Object> data;

    public ArrayList<Object> getData()
    {
        return data;
    }

    private Collection<Element> formLinks;

    public Collection<Element> getFormLinks()
    {
        return formLinks;
    }

    // -----------------------------------------------------------------------------------------------
    // Actiton Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        form = formService.getForm( formId.intValue() );

        formLinks = elementService.getElementsByFormLink( form );

        int numberOfRecords = Integer.parseInt( (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_CHR_NUMBER_OF_RECORDS ) );

        data = formManager.searchObject( form, CodecUtils.unescape( keyword ), numberOfRecords );

        return SUCCESS;
    }
}
