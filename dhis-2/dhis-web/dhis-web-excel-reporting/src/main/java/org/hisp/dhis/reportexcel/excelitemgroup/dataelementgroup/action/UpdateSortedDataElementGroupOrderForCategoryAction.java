/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.reportexcel.excelitemgroup.dataelementgroup.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class UpdateSortedDataElementGroupOrderForCategoryAction extends 
		ActionSupport {
	// -------------------------------------------
	// Dependency
	// -------------------------------------------

	private ExcelItemService excelItemService;

	// -------------------------------------------
	// Input & Output
	// -------------------------------------------

	private Integer excelItemGroupId;

	private List<String> dataElementGroupOrderId = new ArrayList<String>();

	// -------------------------------------------
	// Getters & Setters
	// -------------------------------------------

	public void setExcelItemService(ExcelItemService excelItemService) {
		this.excelItemService = excelItemService;
	}

	public void setExcelItemGroupId(Integer excelItemGroupId) {
		this.excelItemGroupId = excelItemGroupId;
	}

	public void setDataElementGroupOrderId(List<String> dataElementGroupOrderId) {
		this.dataElementGroupOrderId = dataElementGroupOrderId;
	}

	// -------------------------------------------
	// Action implementation
	// -------------------------------------------

	public String execute() throws Exception {

		ExcelItemGroup excelItemGroup = excelItemService
				.getExcelItemGroup(excelItemGroupId);

		List<DataElementGroupOrder> dataElementGroupOrders = new ArrayList<DataElementGroupOrder>();

		for (String id : this.dataElementGroupOrderId) {

			DataElementGroupOrder daElementGroupOrder = excelItemService
					.getDataElementGroupOrder(Integer.parseInt(id));

			dataElementGroupOrders.add(daElementGroupOrder);

		}

		excelItemGroup.setDataElementOrders(dataElementGroupOrders);

		excelItemService.updateExcelItemGroup(excelItemGroup);

		message = i18n.getString("success");
		
		return SUCCESS;
	}

}
