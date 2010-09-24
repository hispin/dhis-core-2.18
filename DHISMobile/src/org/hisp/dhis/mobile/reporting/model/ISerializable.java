/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.mobile.reporting.model;

import java.io.IOException;

/**
 * 
 * @author abyotag_adm
 */
public interface ISerializable {
	byte[] serialize() throws IOException;

	void deSerialize(byte[] data) throws IOException;
}