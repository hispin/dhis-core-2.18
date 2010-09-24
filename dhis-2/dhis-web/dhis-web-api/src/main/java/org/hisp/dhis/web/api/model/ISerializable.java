/**
 * 
 */
package org.hisp.dhis.web.api.model;

import java.io.IOException;

/**
 * @author abyotag_adm
 *
 */
public interface ISerializable {
    byte[] serialize() throws IOException;
    void deSerialize(byte[] data)throws IOException;
}