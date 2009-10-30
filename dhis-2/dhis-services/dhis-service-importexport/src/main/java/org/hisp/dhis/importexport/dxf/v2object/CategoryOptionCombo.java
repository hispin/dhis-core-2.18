//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.10.30 at 03:44:18 PM GMT 
//


package org.hisp.dhis.importexport.dxf.v2object;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://dhis2.org/ns/schema/dxf2}identifiableObject">
 *       &lt;sequence>
 *         &lt;element ref="{http://dhis2.org/ns/schema/dxf2}categoryComboId"/>
 *         &lt;element ref="{http://dhis2.org/ns/schema/dxf2}categoryOptionId" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "categoryComboId",
    "categoryOptionId"
})
@XmlRootElement(name = "categoryOptionCombo")
public class CategoryOptionCombo
    extends IdentifiableObject
{

    @XmlElement(required = true)
    protected BigInteger categoryComboId;
    protected List<BigInteger> categoryOptionId;

    /**
     * Gets the value of the categoryComboId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCategoryComboId() {
        return categoryComboId;
    }

    /**
     * Sets the value of the categoryComboId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCategoryComboId(BigInteger value) {
        this.categoryComboId = value;
    }

    /**
     * Gets the value of the categoryOptionId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categoryOptionId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategoryOptionId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getCategoryOptionId() {
        if (categoryOptionId == null) {
            categoryOptionId = new ArrayList<BigInteger>();
        }
        return this.categoryOptionId;
    }

}
