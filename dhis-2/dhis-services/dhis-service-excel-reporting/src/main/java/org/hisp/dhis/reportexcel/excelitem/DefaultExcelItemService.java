package org.hisp.dhis.reportexcel.excelitem;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

@Transactional
public class DefaultExcelItemService implements ExcelItemService {

	// -------------------------------------------------
	// Dependency
	// -------------------------------------------------

	private ExcelItemStore excelItemStore;

	public void setExcelItemStore(ExcelItemStore excelItemStore) {
		this.excelItemStore = excelItemStore;
	}

	// --------------------------------------
	// Excelitem group Services
	// --------------------------------------

	public void addExcelItemGroup(ExcelItemGroup excelItemGroup) {

		excelItemStore.addExcelItemGroup(excelItemGroup);
	}

	public void deleteExcelItemGroup(int id) {

		excelItemStore.deleteExcelItemGroup(id);
	}

	public Collection<ExcelItemGroup> getAllExcelItemGroup() {

		return excelItemStore.getAllExcelItemGroup();
	}

	public ExcelItemGroup getExcelItemGroup(int id) {

		return excelItemStore.getExcelItemGroup(id);
	}

	public void updateExcelItemGroup(ExcelItemGroup excelItemGroup) {

		excelItemStore.updateExcelItemGroup(excelItemGroup);
	}

	public Collection<ExcelItemGroup> getExcelItemGroupsByOrganisationUnit(
			OrganisationUnit organisationUnit) {
	
		return excelItemStore.getExcelItemGroupsByOrganisationUnit(organisationUnit);
	}

	// --------------------------------------
	// Excelitem Services
	// --------------------------------------

	public void addExcelItem(ExcelItem excelItem) {

		excelItemStore.addExcelItem(excelItem);
	}

	public void deleteExcelItem(int id) {

		excelItemStore.deleteExcelItem(id);
	}

	public Collection<ExcelItem> getAllExcelItem() {

		return excelItemStore.getAllExcelItem();
	}

	public void updateExcelItem(ExcelItem excelItem) {

		excelItemStore.updateExcelItem(excelItem);
	}

	public ExcelItem getExcelItem(int id) {

		return excelItemStore.getExcelItem(id);
	}

}
