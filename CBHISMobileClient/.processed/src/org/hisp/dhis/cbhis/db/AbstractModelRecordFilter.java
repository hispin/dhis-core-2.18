/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.cbhis.db;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import javax.microedition.rms.RecordFilter;
import org.hisp.dhis.cbhis.model.AbstractModel;

/**
 *
 * @author abyotag_adm
 */
public class AbstractModelRecordFilter implements RecordFilter
{
    private AbstractModel model;
    private ByteArrayInputStream inputstream = null;
    private DataInputStream datainputstream = null;

    public AbstractModelRecordFilter(AbstractModel model)
    {
        this.model = model;
    }
    public boolean matches(byte[] suspect)
    {
        boolean matches = false;
        
        if( model == null)
        {
            return matches;
        }
        
        AbstractModel abstractModel = AbstractModel.recordToAbstractModel(suspect);
        matches = abstractModel.getId() == model.getId() && abstractModel.getName().equalsIgnoreCase(model.getName() );
               
        return matches;
    }

    public void close()
    {
        try
        {
            if (inputstream != null)
            {
                inputstream.close();
            }
            if (datainputstream != null)
            {
                datainputstream.close();
            }
        }
        catch ( Exception error){}
    }
}
