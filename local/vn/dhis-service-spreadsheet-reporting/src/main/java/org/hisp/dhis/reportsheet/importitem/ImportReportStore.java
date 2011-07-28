package org.hisp.dhis.reportsheet.importitem;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportsheet.DataElementGroupOrder;

public interface ImportReportStore
{
    String ID = ImportReportStore.class.getName();

    // -------------------------------------------------------------------------
    // Import Report services
    // -------------------------------------------------------------------------

    public int addImportReport( ImportReport importReport );

    public void updateImportReport( ImportReport importReport );

    public void deleteImportReport( int id );

    public Collection<ImportReport> getAllImportReport();

    public ImportReport getImportReport( int id );

    public ImportReport getImportReport( String name );

    public Collection<ImportReport> getImportReports( OrganisationUnit organisationUnit );

    public Collection<ImportReport> getImportReportsByType( String type );

    // -------------------------------------------------------------------------
    // Import Item services
    // -------------------------------------------------------------------------

    public int addImportItem( ImportItem importItem );

    public void updateImportItem( ImportItem importItem );

    public void deleteImportItem( int id );

    public Collection<ImportItem> getAllImportItem();

    public ImportItem getImportItem( int id );

    public ImportItem getImportItem( String name );

    public Collection<Integer> getSheets();

    // -------------------------------------------------------------------------
    // DataElement Order
    // -------------------------------------------------------------------------

    public DataElementGroupOrder getDataElementGroupOrder( Integer id );

    public void updateDataElementGroupOrder( DataElementGroupOrder dataElementGroupOrder );

    public void deleteDataElementGroupOrder( Integer id );
}
