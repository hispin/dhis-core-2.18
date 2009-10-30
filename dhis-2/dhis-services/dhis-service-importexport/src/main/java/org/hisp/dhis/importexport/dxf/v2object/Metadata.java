//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.10.30 at 03:44:18 PM GMT 
//


package org.hisp.dhis.importexport.dxf.v2object;

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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://dhis2.org/ns/schema/dxf2}MultiDimensionalElements" minOccurs="0"/>
 *         &lt;group ref="{http://dhis2.org/ns/schema/dxf2}DataElementDefinition" minOccurs="0"/>
 *         &lt;group ref="{http://dhis2.org/ns/schema/dxf2}IndicatorDefinition" minOccurs="0"/>
 *         &lt;group ref="{http://dhis2.org/ns/schema/dxf2}DataDictionaryDefinition" minOccurs="0"/>
 *         &lt;group ref="{http://dhis2.org/ns/schema/dxf2}DataSetDefinition" minOccurs="0"/>
 *         &lt;group ref="{http://dhis2.org/ns/schema/dxf2}OrgUnitDefinition" minOccurs="0"/>
 *         &lt;element ref="{http://dhis2.org/ns/schema/dxf2}periods" minOccurs="0"/>
 *         &lt;group ref="{http://dhis2.org/ns/schema/dxf2}ReportTableDefinition" minOccurs="0"/>
 *         &lt;element ref="{http://dhis2.org/ns/schema/dxf2}completeDataSetRegistrations" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "categories",
    "categoryCombos",
    "categoryOptionCombos",
    "dataElements",
    "dataElementGroupSets",
    "indicatorTypes",
    "indicators",
    "indicatorGroupSets",
    "dataDictionaries",
    "dataDictionaryDataElements",
    "dataDictionaryIndicators",
    "dataSets",
    "dataSetMembers",
    "organisationUnits",
    "organisationUnitRelationships",
    "organisationUnitGroups",
    "organisationUnitGroupMembers",
    "groupSets",
    "groupSetMembers",
    "organisationUnitLevels",
    "dataSetSourceAssociations",
    "periods",
    "reportTables",
    "reportTableDataElements",
    "reportTableCategoryOptionCombos",
    "reportTableIndicators",
    "reportTableDataSets",
    "reportTablePeriods",
    "reportTableOrganisationUnits",
    "completeDataSetRegistrations"
})
@XmlRootElement(name = "metadata")
public class Metadata {

    protected Categories categories;
    protected CategoryCombos categoryCombos;
    protected CategoryOptionCombos categoryOptionCombos;
    protected DataElements dataElements;
    protected DataElementGroupSets dataElementGroupSets;
    protected IndicatorTypes indicatorTypes;
    protected Indicators indicators;
    @XmlElement(name = "IndicatorGroupSets")
    protected IndicatorGroupSets indicatorGroupSets;
    protected DataDictionaries dataDictionaries;
    protected DataDictionaryDataElements dataDictionaryDataElements;
    protected DataDictionaryIndicators dataDictionaryIndicators;
    protected DataSets dataSets;
    protected DataSetMembers dataSetMembers;
    protected OrganisationUnits organisationUnits;
    protected OrganisationUnitRelationships organisationUnitRelationships;
    protected OrganisationUnitGroups organisationUnitGroups;
    protected OrganisationUnitGroupMembers organisationUnitGroupMembers;
    protected GroupSets groupSets;
    protected GroupSetMembers groupSetMembers;
    protected OrganisationUnitLevels organisationUnitLevels;
    protected DataSetSourceAssociations dataSetSourceAssociations;
    protected Periods periods;
    protected ReportTables reportTables;
    protected ReportTableDataElements reportTableDataElements;
    protected ReportTableCategoryOptionCombos reportTableCategoryOptionCombos;
    protected ReportTableIndicators reportTableIndicators;
    protected ReportTableDataSets reportTableDataSets;
    protected ReportTablePeriods reportTablePeriods;
    protected ReportTableOrganisationUnits reportTableOrganisationUnits;
    protected CompleteDataSetRegistrations completeDataSetRegistrations;

    /**
     * Gets the value of the categories property.
     * 
     * @return
     *     possible object is
     *     {@link Categories }
     *     
     */
    public Categories getCategories() {
        return categories;
    }

    /**
     * Sets the value of the categories property.
     * 
     * @param value
     *     allowed object is
     *     {@link Categories }
     *     
     */
    public void setCategories(Categories value) {
        this.categories = value;
    }

    /**
     * Gets the value of the categoryCombos property.
     * 
     * @return
     *     possible object is
     *     {@link CategoryCombos }
     *     
     */
    public CategoryCombos getCategoryCombos() {
        return categoryCombos;
    }

    /**
     * Sets the value of the categoryCombos property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryCombos }
     *     
     */
    public void setCategoryCombos(CategoryCombos value) {
        this.categoryCombos = value;
    }

    /**
     * Gets the value of the categoryOptionCombos property.
     * 
     * @return
     *     possible object is
     *     {@link CategoryOptionCombos }
     *     
     */
    public CategoryOptionCombos getCategoryOptionCombos() {
        return categoryOptionCombos;
    }

    /**
     * Sets the value of the categoryOptionCombos property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryOptionCombos }
     *     
     */
    public void setCategoryOptionCombos(CategoryOptionCombos value) {
        this.categoryOptionCombos = value;
    }

    /**
     * Gets the value of the dataElements property.
     * 
     * @return
     *     possible object is
     *     {@link DataElements }
     *     
     */
    public DataElements getDataElements() {
        return dataElements;
    }

    /**
     * Sets the value of the dataElements property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataElements }
     *     
     */
    public void setDataElements(DataElements value) {
        this.dataElements = value;
    }

    /**
     * Gets the value of the dataElementGroupSets property.
     * 
     * @return
     *     possible object is
     *     {@link DataElementGroupSets }
     *     
     */
    public DataElementGroupSets getDataElementGroupSets() {
        return dataElementGroupSets;
    }

    /**
     * Sets the value of the dataElementGroupSets property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataElementGroupSets }
     *     
     */
    public void setDataElementGroupSets(DataElementGroupSets value) {
        this.dataElementGroupSets = value;
    }

    /**
     * Gets the value of the indicatorTypes property.
     * 
     * @return
     *     possible object is
     *     {@link IndicatorTypes }
     *     
     */
    public IndicatorTypes getIndicatorTypes() {
        return indicatorTypes;
    }

    /**
     * Sets the value of the indicatorTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link IndicatorTypes }
     *     
     */
    public void setIndicatorTypes(IndicatorTypes value) {
        this.indicatorTypes = value;
    }

    /**
     * Gets the value of the indicators property.
     * 
     * @return
     *     possible object is
     *     {@link Indicators }
     *     
     */
    public Indicators getIndicators() {
        return indicators;
    }

    /**
     * Sets the value of the indicators property.
     * 
     * @param value
     *     allowed object is
     *     {@link Indicators }
     *     
     */
    public void setIndicators(Indicators value) {
        this.indicators = value;
    }

    /**
     * Gets the value of the indicatorGroupSets property.
     * 
     * @return
     *     possible object is
     *     {@link IndicatorGroupSets }
     *     
     */
    public IndicatorGroupSets getIndicatorGroupSets() {
        return indicatorGroupSets;
    }

    /**
     * Sets the value of the indicatorGroupSets property.
     * 
     * @param value
     *     allowed object is
     *     {@link IndicatorGroupSets }
     *     
     */
    public void setIndicatorGroupSets(IndicatorGroupSets value) {
        this.indicatorGroupSets = value;
    }

    /**
     * Gets the value of the dataDictionaries property.
     * 
     * @return
     *     possible object is
     *     {@link DataDictionaries }
     *     
     */
    public DataDictionaries getDataDictionaries() {
        return dataDictionaries;
    }

    /**
     * Sets the value of the dataDictionaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataDictionaries }
     *     
     */
    public void setDataDictionaries(DataDictionaries value) {
        this.dataDictionaries = value;
    }

    /**
     * Gets the value of the dataDictionaryDataElements property.
     * 
     * @return
     *     possible object is
     *     {@link DataDictionaryDataElements }
     *     
     */
    public DataDictionaryDataElements getDataDictionaryDataElements() {
        return dataDictionaryDataElements;
    }

    /**
     * Sets the value of the dataDictionaryDataElements property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataDictionaryDataElements }
     *     
     */
    public void setDataDictionaryDataElements(DataDictionaryDataElements value) {
        this.dataDictionaryDataElements = value;
    }

    /**
     * Gets the value of the dataDictionaryIndicators property.
     * 
     * @return
     *     possible object is
     *     {@link DataDictionaryIndicators }
     *     
     */
    public DataDictionaryIndicators getDataDictionaryIndicators() {
        return dataDictionaryIndicators;
    }

    /**
     * Sets the value of the dataDictionaryIndicators property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataDictionaryIndicators }
     *     
     */
    public void setDataDictionaryIndicators(DataDictionaryIndicators value) {
        this.dataDictionaryIndicators = value;
    }

    /**
     * Gets the value of the dataSets property.
     * 
     * @return
     *     possible object is
     *     {@link DataSets }
     *     
     */
    public DataSets getDataSets() {
        return dataSets;
    }

    /**
     * Sets the value of the dataSets property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSets }
     *     
     */
    public void setDataSets(DataSets value) {
        this.dataSets = value;
    }

    /**
     * Gets the value of the dataSetMembers property.
     * 
     * @return
     *     possible object is
     *     {@link DataSetMembers }
     *     
     */
    public DataSetMembers getDataSetMembers() {
        return dataSetMembers;
    }

    /**
     * Sets the value of the dataSetMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSetMembers }
     *     
     */
    public void setDataSetMembers(DataSetMembers value) {
        this.dataSetMembers = value;
    }

    /**
     * Gets the value of the organisationUnits property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationUnits }
     *     
     */
    public OrganisationUnits getOrganisationUnits() {
        return organisationUnits;
    }

    /**
     * Sets the value of the organisationUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationUnits }
     *     
     */
    public void setOrganisationUnits(OrganisationUnits value) {
        this.organisationUnits = value;
    }

    /**
     * Gets the value of the organisationUnitRelationships property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationUnitRelationships }
     *     
     */
    public OrganisationUnitRelationships getOrganisationUnitRelationships() {
        return organisationUnitRelationships;
    }

    /**
     * Sets the value of the organisationUnitRelationships property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationUnitRelationships }
     *     
     */
    public void setOrganisationUnitRelationships(OrganisationUnitRelationships value) {
        this.organisationUnitRelationships = value;
    }

    /**
     * Gets the value of the organisationUnitGroups property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationUnitGroups }
     *     
     */
    public OrganisationUnitGroups getOrganisationUnitGroups() {
        return organisationUnitGroups;
    }

    /**
     * Sets the value of the organisationUnitGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationUnitGroups }
     *     
     */
    public void setOrganisationUnitGroups(OrganisationUnitGroups value) {
        this.organisationUnitGroups = value;
    }

    /**
     * Gets the value of the organisationUnitGroupMembers property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationUnitGroupMembers }
     *     
     */
    public OrganisationUnitGroupMembers getOrganisationUnitGroupMembers() {
        return organisationUnitGroupMembers;
    }

    /**
     * Sets the value of the organisationUnitGroupMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationUnitGroupMembers }
     *     
     */
    public void setOrganisationUnitGroupMembers(OrganisationUnitGroupMembers value) {
        this.organisationUnitGroupMembers = value;
    }

    /**
     * Gets the value of the groupSets property.
     * 
     * @return
     *     possible object is
     *     {@link GroupSets }
     *     
     */
    public GroupSets getGroupSets() {
        return groupSets;
    }

    /**
     * Sets the value of the groupSets property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupSets }
     *     
     */
    public void setGroupSets(GroupSets value) {
        this.groupSets = value;
    }

    /**
     * Gets the value of the groupSetMembers property.
     * 
     * @return
     *     possible object is
     *     {@link GroupSetMembers }
     *     
     */
    public GroupSetMembers getGroupSetMembers() {
        return groupSetMembers;
    }

    /**
     * Sets the value of the groupSetMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupSetMembers }
     *     
     */
    public void setGroupSetMembers(GroupSetMembers value) {
        this.groupSetMembers = value;
    }

    /**
     * Gets the value of the organisationUnitLevels property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationUnitLevels }
     *     
     */
    public OrganisationUnitLevels getOrganisationUnitLevels() {
        return organisationUnitLevels;
    }

    /**
     * Sets the value of the organisationUnitLevels property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationUnitLevels }
     *     
     */
    public void setOrganisationUnitLevels(OrganisationUnitLevels value) {
        this.organisationUnitLevels = value;
    }

    /**
     * Gets the value of the dataSetSourceAssociations property.
     * 
     * @return
     *     possible object is
     *     {@link DataSetSourceAssociations }
     *     
     */
    public DataSetSourceAssociations getDataSetSourceAssociations() {
        return dataSetSourceAssociations;
    }

    /**
     * Sets the value of the dataSetSourceAssociations property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSetSourceAssociations }
     *     
     */
    public void setDataSetSourceAssociations(DataSetSourceAssociations value) {
        this.dataSetSourceAssociations = value;
    }

    /**
     * Gets the value of the periods property.
     * 
     * @return
     *     possible object is
     *     {@link Periods }
     *     
     */
    public Periods getPeriods() {
        return periods;
    }

    /**
     * Sets the value of the periods property.
     * 
     * @param value
     *     allowed object is
     *     {@link Periods }
     *     
     */
    public void setPeriods(Periods value) {
        this.periods = value;
    }

    /**
     * Gets the value of the reportTables property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTables }
     *     
     */
    public ReportTables getReportTables() {
        return reportTables;
    }

    /**
     * Sets the value of the reportTables property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTables }
     *     
     */
    public void setReportTables(ReportTables value) {
        this.reportTables = value;
    }

    /**
     * Gets the value of the reportTableDataElements property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTableDataElements }
     *     
     */
    public ReportTableDataElements getReportTableDataElements() {
        return reportTableDataElements;
    }

    /**
     * Sets the value of the reportTableDataElements property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTableDataElements }
     *     
     */
    public void setReportTableDataElements(ReportTableDataElements value) {
        this.reportTableDataElements = value;
    }

    /**
     * Gets the value of the reportTableCategoryOptionCombos property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTableCategoryOptionCombos }
     *     
     */
    public ReportTableCategoryOptionCombos getReportTableCategoryOptionCombos() {
        return reportTableCategoryOptionCombos;
    }

    /**
     * Sets the value of the reportTableCategoryOptionCombos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTableCategoryOptionCombos }
     *     
     */
    public void setReportTableCategoryOptionCombos(ReportTableCategoryOptionCombos value) {
        this.reportTableCategoryOptionCombos = value;
    }

    /**
     * Gets the value of the reportTableIndicators property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTableIndicators }
     *     
     */
    public ReportTableIndicators getReportTableIndicators() {
        return reportTableIndicators;
    }

    /**
     * Sets the value of the reportTableIndicators property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTableIndicators }
     *     
     */
    public void setReportTableIndicators(ReportTableIndicators value) {
        this.reportTableIndicators = value;
    }

    /**
     * Gets the value of the reportTableDataSets property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTableDataSets }
     *     
     */
    public ReportTableDataSets getReportTableDataSets() {
        return reportTableDataSets;
    }

    /**
     * Sets the value of the reportTableDataSets property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTableDataSets }
     *     
     */
    public void setReportTableDataSets(ReportTableDataSets value) {
        this.reportTableDataSets = value;
    }

    /**
     * Gets the value of the reportTablePeriods property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTablePeriods }
     *     
     */
    public ReportTablePeriods getReportTablePeriods() {
        return reportTablePeriods;
    }

    /**
     * Sets the value of the reportTablePeriods property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTablePeriods }
     *     
     */
    public void setReportTablePeriods(ReportTablePeriods value) {
        this.reportTablePeriods = value;
    }

    /**
     * Gets the value of the reportTableOrganisationUnits property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTableOrganisationUnits }
     *     
     */
    public ReportTableOrganisationUnits getReportTableOrganisationUnits() {
        return reportTableOrganisationUnits;
    }

    /**
     * Sets the value of the reportTableOrganisationUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTableOrganisationUnits }
     *     
     */
    public void setReportTableOrganisationUnits(ReportTableOrganisationUnits value) {
        this.reportTableOrganisationUnits = value;
    }

    /**
     * Gets the value of the completeDataSetRegistrations property.
     * 
     * @return
     *     possible object is
     *     {@link CompleteDataSetRegistrations }
     *     
     */
    public CompleteDataSetRegistrations getCompleteDataSetRegistrations() {
        return completeDataSetRegistrations;
    }

    /**
     * Sets the value of the completeDataSetRegistrations property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompleteDataSetRegistrations }
     *     
     */
    public void setCompleteDataSetRegistrations(CompleteDataSetRegistrations value) {
        this.completeDataSetRegistrations = value;
    }

}
