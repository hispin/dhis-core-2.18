package org.hisp.dhis.vn.chr.formreport.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collections;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class GetDataSets extends ActionSupport {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private DataSetService dataSetService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private ArrayList<DataSet> dataSets;

	// -----------------------------------------------------------------------------------------------
	// Getters && Setters
	// -----------------------------------------------------------------------------------------------

	public ArrayList<DataSet> getDataSets() {
		return dataSets;
	}

	public void setDataSetService(DataSetService dataSetService) {
		this.dataSetService = dataSetService;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		try {

			dataSets = new ArrayList<DataSet>(dataSetService.getAllDataSets());

			Collections.sort(dataSets, new DataSetNameComparator());

			message = i18n.getString("success");

		} catch (Exception ex) {

			ex.printStackTrace();
			message = i18n.getString("error");

		}
		return SUCCESS;
	}

}
