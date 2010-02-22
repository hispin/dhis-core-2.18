package org.hisp.dhis.patient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.period.PeriodTypePopulator;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;

public class PatientAttributePopulator extends AbstractStartupRoutine
{
	private static final Log LOG = LogFactory.getLog(PeriodTypePopulator.class);

	public static final String ATTRIBUTE_CHILD_CONTACT_NAME = "Child contact name";

	public static final String ATTRIBUTE_CHILD_RELATIONSHIP_TYPE = "Child contact type";

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private PatientAttributeService attributeService;
	
	public void setAttributeService(PatientAttributeService attributeService) {
		this.attributeService = attributeService;
	}

	// -------------------------------------------------------------------------
	// Execute
	// -------------------------------------------------------------------------

	@Override
	public void execute() throws Exception {

		// -----------------------------------------------------------------------------
		// Create Child Contact Name attribute
		// -----------------------------------------------------------------------------
		PatientAttribute attrChildContactName = attributeService.getPatientAttributeByName(ATTRIBUTE_CHILD_CONTACT_NAME);
		if (attrChildContactName == null ) 
		{
			attrChildContactName = new PatientAttribute();
			attrChildContactName.setName(ATTRIBUTE_CHILD_CONTACT_NAME);
			attrChildContactName.setDescription(ATTRIBUTE_CHILD_CONTACT_NAME);
			attrChildContactName.setValueType(PatientAttribute.TYPE_STRING);
			attrChildContactName.setMandatory(true);
			attributeService.savePatientAttribute(attrChildContactName);
		}

		// -----------------------------------------------------------------------------
		// Create Child Contact RelationShip Type attribute
		// -----------------------------------------------------------------------------
		PatientAttribute arrChildRelationShipType = attributeService.getPatientAttributeByName(ATTRIBUTE_CHILD_RELATIONSHIP_TYPE);
		if (arrChildRelationShipType == null )
		{
			arrChildRelationShipType = new PatientAttribute();
			arrChildRelationShipType.setName(ATTRIBUTE_CHILD_RELATIONSHIP_TYPE);
			arrChildRelationShipType.setDescription(ATTRIBUTE_CHILD_RELATIONSHIP_TYPE);
			arrChildRelationShipType.setValueType(PatientAttribute.TYPE_STRING);
			arrChildRelationShipType.setMandatory(true);
			attributeService.savePatientAttribute(arrChildRelationShipType);
		}
	}

	

}
