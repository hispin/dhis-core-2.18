/**
 * @author Dang Duy Hieu
 */

package org.hisp.dhis.vn.report;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;

public class ReportExcelCategory
    extends ReportExcel
    implements ReportExcelInterface
{

    /**
     * Constructors
     */
    public ReportExcelCategory()
    {
        super();
    }

    public ReportExcelCategory( String name, String excelTemplateFile, int periodRow, int periodColumn,
        int organisationRow, int organisationColumn )
    {
        super( name, excelTemplateFile, periodRow, periodColumn, organisationRow, organisationColumn );
    }

    /**
     * @dataElements: List of DataElement
     */
    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    /**
     * @categoryCombo: Object of DataElementCategoryCombo
     */
    private DataElementCategoryCombo categoryCombo;

    public DataElementCategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }

    public void setCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
    }

    /**
     * @ getReportType: gets Report Type for ReportExcelCategory
     */
    public String getReportType()
    {
        return ReportExcel.TYPE.CATEGORY;
    }
}
