/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.cbhis.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordStoreException;
import org.hisp.dhis.cbhis.db.ModelRecordStore;

/**
 *
 * @author abyotag_adm
 */
public class ProgramStageForm extends AbstractModel {

    private Vector dataElements;

    public ProgramStageForm(){}    

    /**
     * @return the dataElements
     */
    public Vector getDataElements() {
        return dataElements;
    }

    /**
     * @param dataElements the dataElements to set
     */
    public void setDataElements(Vector dataElements) {
        this.dataElements = dataElements;
    }

    public static ProgramStageForm recordToProgramStageForm( byte[] rec)
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(rec);
        DataInputStream din = new DataInputStream(bin);

        ProgramStageForm programStageForm = new ProgramStageForm();
        try{
            programStageForm.setId( din.readInt() );
            programStageForm.setName( din.readUTF() );

            Vector deIds = generateIds(din.readUTF());
            Vector des = new Vector();

            byte[] deRec = null;
            DataElement de;

            //need to think a way to load vector of des at once ....
            ModelRecordStore recordStore = new ModelRecordStore(ModelRecordStore.DATAELEMENT_DB);

            for(int i=0; i<deIds.size();i++)
            {
                try{

                   deRec = recordStore.getRecord( Integer.parseInt(deIds.elementAt(i).toString()));
                   if( deRec != null )
                   {
                       de = DataElement.recordToDataElement(deRec);
                       des.addElement(de);
                   }
                }catch(RecordStoreException rse){}
            }

            programStageForm.setDataElements( des );

        }catch(IOException ioe){}

        return programStageForm;
    }

    public static byte[] programStageFormToRecord(ProgramStageForm programStageForm) {

        ByteArrayOutputStream deOs = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(deOs);
        try {
            dout.writeInt(programStageForm.getId());
            dout.writeUTF(programStageForm.getName());

            Vector des = programStageForm.getDataElements();
            String deIds="";

            for(int i=0; i<des.size(); i++)
            {
                DataElement de = (DataElement)des.elementAt(i);

                deIds += de.getId() + SEPARATOR;

            }
            dout.writeUTF(deIds);
            dout.flush();
        }
        catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return deOs.toByteArray();
    }

    private static Vector generateIds(String commaDelimitedIds)
    {
        Vector ids = new Vector();

        // Parse nodes into vector
        int index = commaDelimitedIds.indexOf(SEPARATOR);
        while(index>=0) {
            ids.addElement( commaDelimitedIds.substring(0, index) );
            commaDelimitedIds = commaDelimitedIds.substring(index+SEPARATOR.length());
            index = commaDelimitedIds.indexOf(SEPARATOR);
        }

        return ids;
    }
}
