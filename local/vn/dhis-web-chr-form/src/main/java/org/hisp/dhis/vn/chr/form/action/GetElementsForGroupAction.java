package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;
import java.util.Iterator;

import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

import com.opensymphony.xwork2.Action;

public class GetElementsForGroupAction
    implements Action
{

    // ------------------------------------------------------------------------------------------
    // Input & Output
    // ------------------------------------------------------------------------------------------

    private Form form;
    
    public Form getForm()
    {
        return form;
    }

    public void setForm( Form form )
    {
        this.form = form;
    }

    Collection<Egroup> egroups;

    public Collection<Egroup> getEgroups()
    {
        return egroups;
    }
    
    Collection<Element> availableElements;

    public Collection<Element> getAvailableElements()
    {
        return availableElements;
    }

    // ------------------------------------------------------------------------------------------
    // Action Implementation
    // ------------------------------------------------------------------------------------------
    public String execute()
        throws Exception
    {

        this.egroups = form.getEgroups();

        this.availableElements = form.getElements();

        if ( availableElements != null )
        {
            Iterator<Element> iter = availableElements.iterator();
            while ( iter.hasNext() )
            {
                if ( iter.next().getEgroup() != null )
                    iter.remove();
            }
        }

        return SUCCESS;

    }

}
