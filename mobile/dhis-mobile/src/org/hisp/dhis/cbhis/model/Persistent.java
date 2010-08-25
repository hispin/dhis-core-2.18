package org.hisp.dhis.cbhis.model;

import java.io.*;


/**
 * 
 * @author Daniel Kayiwa
 *
 */
public interface Persistent {
    void write(DataOutputStream dos) throws IOException;
    void read(DataInputStream dis) throws IOException,InstantiationException,IllegalAccessException;
}
