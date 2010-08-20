/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.cbhis.model;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author abyotag_adm
 */
public class AbstractModel {

    public static final String SEPARATOR = ",";
    
    private int id;

    private String name;

    public AbstractModel(){}

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public static AbstractModel recordToAbstractModel( byte[] rec)
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(rec);
        DataInputStream din = new DataInputStream(bin);

        AbstractModel model = new AbstractModel();

        try{
            model.setId( din.readInt() );
            model.setName( din.readUTF());
        }catch(IOException ioe){}

        return model;
    }

}
