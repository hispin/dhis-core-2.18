package org.hisp.dhis.reportexcel.importing;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;

public class ExcelItemValueByOrganisationUnit
{

    private OrganisationUnit organisationUnit;

    private Collection<ExcelItemValue> excelItemValues;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public ExcelItemValueByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        super();

        this.organisationUnit = organisationUnit;

        excelItemValues = new ArrayList<ExcelItemValue>();
    }

    // ----------------------------------------------------------------------
    // Getters and setters
    // ----------------------------------------------------------------------

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public Collection<ExcelItemValue> getExcelItemValues()
    {
        return excelItemValues;
    }

    public void setExcelItemValues( Collection<ExcelItemValue> excelItemValues )
    {
        this.excelItemValues = excelItemValues;
    }

}
