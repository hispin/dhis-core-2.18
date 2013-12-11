package org.hisp.dhis.pbf.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;

import com.opensymphony.xwork2.Action;

public class DeleteTariffDataAction implements Action {
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private TariffDataValueService tariffDataValueService;

	public void setTariffDataValueService(TariffDataValueService tariffDataValueService) 
	{
		this.tariffDataValueService = tariffDataValueService;
	}
	
	// -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
	
	private String organisationUnitId;
	
	private String dataElementId;
	
	private String categoryOptionComboId;
	
	private String organisationUnitGroupId;
	
	private String startDate;
	
	private String endDate;
	
	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute() throws Exception {
		
		Date sDate;
		Date eDate;
		int orgUnitId = Integer.parseInt(organisationUnitId);
		
		int deId = Integer.parseInt(dataElementId);
		
		int categoryId = Integer.parseInt(categoryOptionComboId);
		int orgUnitGrpId = Integer.parseInt(organisationUnitGroupId);
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		sDate = dateFormat.parse(startDate);
		eDate = dateFormat.parse(endDate);
		
		TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue
						(orgUnitId, deId, categoryId, orgUnitGrpId, sDate, eDate);
				
				
		tariffDataValueService.deleteTariffDataValue(tariffDataValue);
		
		return SUCCESS;
	}
}