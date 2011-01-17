package org.hisp.dhis.reportexcel.excelitem.action;

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

import java.util.Collection;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.excelitem.ExcelItem;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class CopyExcelItemToGroupAction extends ActionSupport {
	// -------------------------------------------
	// Dependency
	// -------------------------------------------

	private ExcelItemService excelItemService;

	private StatementManager statementManager;

	// -------------------------------------------
	// Input
	// -------------------------------------------

	private Integer sheetNo;
	
	private Collection<String> itemIds;

	private Integer excelItemGroupDestId;

	// -------------------------------------------
	// Getter & Setter
	// -------------------------------------------

	public void setStatementManager(StatementManager statementManager) {
		this.statementManager = statementManager;
	}

	public void setItemIds(Collection<String> itemIds) {
		this.itemIds = itemIds;
	}

	public Integer getSheetNo() {
		return sheetNo;
	}

	public void setExcelItemGroupDestId(Integer excelItemGroupDestId) {
		this.excelItemGroupDestId = excelItemGroupDestId;
	}

	public void setExcelItemService(ExcelItemService excelItemService) {
		this.excelItemService = excelItemService;
	}

	public void setSheetNo(Integer sheetNo) {
		this.sheetNo = sheetNo;
	}

	// -------------------------------------------
	// Action implementation
	// -------------------------------------------

	public String execute() throws Exception {
		
		statementManager.initialise();

		ExcelItemGroup dest = excelItemService.getExcelItemGroup(excelItemGroupDestId);
		
		for (String itemId : itemIds) {

			ExcelItem item = excelItemService
					.getExcelItem(Integer.parseInt(itemId));

			ExcelItem excelItem = new ExcelItem();

			excelItem.setName(item.getName());

			excelItem.setRow(item.getRow());

			excelItem.setColumn(item.getColumn());

			excelItem.setExpression(item.getExpression());

			excelItem.setSheetNo(sheetNo);

			excelItem.setExcelItemGroup(dest);
			
			excelItemService.addExcelItem(excelItem);
			
		}

		message = i18n.getString("success");
		
		statementManager.destroy();

		return SUCCESS;
	}

}
