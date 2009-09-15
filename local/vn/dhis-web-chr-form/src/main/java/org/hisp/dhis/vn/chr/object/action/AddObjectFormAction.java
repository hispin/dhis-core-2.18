package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

import com.opensymphony.xwork2.Action;

public class AddObjectFormAction
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormService formService;

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    private FormManager formManager;

    public void setFormManager( FormManager formManager )
    {
        this.formManager = formManager;
    }

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

    private int formId;

    public void setFormId( int formId )
    {
        this.formId = formId;
    }

    private Collection<Egroup> egroups;

    public Collection<Egroup> getEgroups()
    {
        return egroups;
    }

    private Form form;

    public Form getForm()
    {
        return form;
    }

    private String objectId;

    public void setObjectId( String objectId )
    {
        this.objectId = objectId;
    }

    private ArrayList<String> parentObject;

    public ArrayList<String> getParentObject()
    {
        return parentObject;
    }

    // -----------------------------------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        form = formService.getForm( formId );

        egroups = form.getEgroups();

        if ( objectId != null )
        {

            Iterator<Egroup> iter = egroups.iterator();
            if ( iter.hasNext() )
            {
                for ( Element element : iter.next().getElements() )
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
        }

        return SUCCESS;
    }
}
