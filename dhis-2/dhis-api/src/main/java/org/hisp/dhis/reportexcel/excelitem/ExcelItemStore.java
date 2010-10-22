package org.hisp.dhis.reportexcel.excelitem;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;

public interface ExcelItemStore
{

    String ID = ExcelItemStore.class.getName();

    // ----------------------------------------------------------------------------
    // Excelitem group services
    // ----------------------------------------------------------------------------

    public int addExcelItemGroup( ExcelItemGroup excelItemGroup );

    public void updateExcelItemGroup( ExcelItemGroup excelItemGroup );

    public void deleteExcelItemGroup( int id );

    public Collection<ExcelItemGroup> getAllExcelItemGroup();

    public ExcelItemGroup getExcelItemGroup( int id );
    
    public ExcelItemGroup getExcelItemGroup( String name );

    public Collection<ExcelItemGroup> getExcelItemGroups( OrganisationUnit organisationUnit );

    // ----------------------------------------------------------------------------
    // Excelitem services
    // ----------------------------------------------------------------------------

    public int addExcelItem( ExcelItem excelItem );

    public void updateExcelItem( ExcelItem excelItem );

    public void deleteExcelItem( int id );

    public Collection<ExcelItem> getAllExcelItem();

    public ExcelItem getExcelItem( int id );
    
    public ExcelItem getExcelItem( String name );

    // --------------------------------------
    // DataElement Order
    // --------------------------------------

    public DataElementGroupOrder getDataElementGroupOrder( Integer id );

    public void updateDataElementGroupOrder( DataElementGroupOrder dataElementGroupOrder );

    public void deleteDataElementGroupOrder( Integer id );
}
