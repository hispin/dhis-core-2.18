package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

import com.opensymphony.xwork2.Action;

public class ListRelativeObjectAction
    implements Action
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

    private ElementService elementService;

    public void setElementService( ElementService elementService )
    {
        this.elementService = elementService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer formId;

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    private String objectId;

    public void setObjectId( String objectId )
    {
        this.objectId = objectId;
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

    private String column;

    public void setColumn( String column )
    {
        this.column = column;
    }

    private ArrayList<String> parentObject;

    public ArrayList<String> getParentObject()
    {
        return parentObject;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        form = formService.getForm( formId.intValue() );

        formLinks = elementService.getElementsByFormLink( form );

        int numberOfRecords = Integer.parseInt( (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_CHR_NUMBER_OF_RECORDS ) );

        data = formManager.listRelativeObject( form, column, objectId, numberOfRecords );

        if ( objectId != null )
        {

            for ( Element element : form.getElements() )
            {

                if ( element.getFormLink() != null )
                {

                    Form fparent = element.getFormLink();

                    ArrayList<String> data = formManager.getObject( fparent, Integer.parseInt( objectId ) );

                    parentObject = new ArrayList<String>();

                    int k = 0;

                    for ( Egroup egroup : fparent.getEgroups() )
                    {
                        for ( Element e : egroup.getElements() )
                        {

                            if ( data.get( k ) != null )
                                parentObject.add( e.getLabel() + " : " + data.get( k ) );
                            k++;
                            if ( k == fparent.getNoColumnLink() )
                                break;
                        }// end for element

                        if ( k == fparent.getNoColumnLink() )
                            break;
                    }// end for egroup
                }
            }
        }

        return SUCCESS;
    }
}
