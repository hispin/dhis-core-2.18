package org.hisp.dhis.pbf.payment.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class GetPaymentAdjustmentDetailsAction implements Action {

	private final static String PAYMENT_ADJUSTMENT_LEVEL_ORG_GROUP = "PAYMENT_ADJUSTMENT_LEVEL_ORG_GROUP";
	private final static String PAYMENT_ADJUSTMENT_AMOUNT_DE = "PAYMENT_ADJUSTMENT_AMOUNT_DE";

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private DataSetService dataSetService;

	public void setDataSetService(DataSetService dataSetService) {
		this.dataSetService = dataSetService;
	}

	private OrganisationUnitGroupService organisationUnitGroupService;
	
	public void setOrganisationUnitGroupService(
			OrganisationUnitGroupService organisationUnitGroupService) {
		this.organisationUnitGroupService = organisationUnitGroupService;
	}

	private LookupService lookupService;

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	private ConstantService constantService;

	public void setConstantService(ConstantService constantService) {
		this.constantService = constantService;
	}

	// -------------------------------------------------------------------------
	// Input / Output
	// -------------------------------------------------------------------------

	private List<DataSet> dataSets = new ArrayList<DataSet>();

	public List<DataSet> getDataSets() {
		return dataSets;
	}

	private List<String> orgUnitList = new ArrayList<String>();
	
	public List<String> getOrgUnitList() {
		return orgUnitList;
	}

	private String amountDEId;
	
	public String getAmountDEId() {
		return amountDEId;
	}
	// -------------------------------------------------------------------------
	// Action implementation
	// -------------------------------------------------------------------------

	

	public String execute() throws Exception {
		Constant orgUnitGrp = constantService
				.getConstantByName(PAYMENT_ADJUSTMENT_LEVEL_ORG_GROUP);
		
		Constant paymentAmount = constantService
		.getConstantByName(PAYMENT_ADJUSTMENT_AMOUNT_DE);
		
		amountDEId = paymentAmount.getValue()+"";

		List<OrganisationUnit> organisationUnitList = new ArrayList<OrganisationUnit>(
				organisationUnitGroupService.getOrganisationUnitGroup(
						(int) orgUnitGrp.getValue()).getMembers());
		
		for(OrganisationUnit org : organisationUnitList)
		{
			orgUnitList.add( "\"" + org.getUid() + "\"" );
		}

		List<Lookup> lookups = new ArrayList<Lookup>(lookupService
				.getAllLookupsByType(Lookup.DS_PBF_TYPE));

		for (Lookup lookup : lookups) 
		{
			Integer dataSetId = Integer.parseInt(lookup.getValue());

			DataSet dataSet = dataSetService.getDataSet(dataSetId);

			dataSets.add(dataSet);
		}
		return SUCCESS;
	}
}