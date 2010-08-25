/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.mobile.model;

/**
 *
 * @author abyotag_adm
 */
public class DataValue {

    private int programInstanceId;

    private int dataElementId;

    private String value;

    public DataValue(){}

    /**
     * @return the programInstanceId
     */
    public int getProgramInstanceId() {
        return programInstanceId;
    }

    /**
     * @param programInstanceId the programInstanceId to set
     */
    public void setProgramInstanceId(int programInstanceId) {
        this.programInstanceId = programInstanceId;
    }

    /**
     * @return the dataElementId
     */
    public int getDataElementId() {
        return dataElementId;
    }

    /**
     * @param dataElementId the dataElementId to set
     */
    public void setDataElementId(int dataElementId) {
        this.dataElementId = dataElementId;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
