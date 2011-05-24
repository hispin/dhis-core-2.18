package org.hisp.dhis.reportexcel.importing.action;

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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.reportexcel.importing.ImportItemValue;
import org.hisp.dhis.reportexcel.importing.ImportItemValueByOrganisationUnit;
import org.hisp.dhis.reportexcel.importitem.ExcelItem;
import org.hisp.dhis.reportexcel.importitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class ViewDataOrganizationGroupAction
    implements Action
{

    // --------------------------------------------------------------------
    // Dependency
    // --------------------------------------------------------------------

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    // --------------------------------------------------------------------
    // Inputs && Outputs
    // --------------------------------------------------------------------

    private File upload;

    private List<ImportItemValueByOrganisationUnit> importItemValueByOrgUnits;

    private ArrayList<ExcelItem> importItems;

    private ExcelItemGroup importReport;

    // --------------------------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------------------------

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    public void setImportReport( ExcelItemGroup importReport )
    {
        this.importReport = importReport;
    }

    public void setImportItems( ArrayList<ExcelItem> importItems )
    {
        this.importItems = importItems;
    }

    public List<ImportItemValueByOrganisationUnit> getImportItemValueByOrgUnits()
    {
        return importItemValueByOrgUnits;
    }

    // --------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------

    public String execute()
    {
        try
        {
            OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

            FileInputStream inputStream = new FileInputStream( upload );

            HSSFWorkbook wb = new HSSFWorkbook( inputStream );

            importItemValueByOrgUnits = new ArrayList<ImportItemValueByOrganisationUnit>();

            if ( organisationUnit != null )
            {
                for ( OrganisationUnitGroup organisationUnitGroup : importReport.getOrganisationUnitGroups() )
                {
                    List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( getOrganisationUnits(
                        organisationUnitGroup, organisationUnit ) );

                    Collections.sort( organisationUnits, new OrganisationUnitNameComparator() );

                    int row = 0;

                    for ( OrganisationUnit o : organisationUnits )
                    {
                        ImportItemValueByOrganisationUnit importItemValueByOrgUnit = new ImportItemValueByOrganisationUnit(
                            o );
                        ArrayList<ImportItemValue> importItemValues = new ArrayList<ImportItemValue>();

                        for ( ExcelItem importItem : importItems )
                        {
                            HSSFSheet sheet = wb.getSheetAt( importItem.getSheetNo() - 1 );

                            String value = ExcelUtils.readValueImportingByPOI( importItem.getRow() + row, importItem
                                .getColumn(), sheet );

                            if ( value.length() > 0 )
                            {
                                ImportItemValue importItemValue = new ImportItemValue( importItem, value );
                                importItemValues.add( importItemValue );
                            }

                        }// end for (ImportItem ...

                        row++;

                        importItemValueByOrgUnit.setImportItemValues( importItemValues );

                        importItemValueByOrgUnits.add( importItemValueByOrgUnit );

                    }// end for (OrganisationUnit ...

                } // end for ( OrganisationUnitGroup ...

            }// end if (organisationUnit ...
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return SUCCESS;
    }

    private Collection<OrganisationUnit> getOrganisationUnits( OrganisationUnitGroup group, OrganisationUnit parentUnit )
    {
        List<OrganisationUnit> childrenOrganisationUnits = new ArrayList<OrganisationUnit>( parentUnit.getChildren() );

        Collection<OrganisationUnit> organisationUnits = group.getMembers();

        organisationUnits.retainAll( childrenOrganisationUnits );

        return organisationUnits;
    }

}
