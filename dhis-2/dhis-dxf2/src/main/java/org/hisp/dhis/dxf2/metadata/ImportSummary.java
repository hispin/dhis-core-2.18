package org.hisp.dhis.dxf2.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.dxf2.importsummary.ImportCount;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "importSummary", namespace = Dxf2Namespace.NAMESPACE )
public class ImportSummary
{
    private ImportCount importCount = new ImportCount();

    private List<ImportTypeSummary> importTypeSummaries = new ArrayList<ImportTypeSummary>();

    public ImportSummary()
    {

    }

    @JsonProperty
    @JacksonXmlProperty( namespace = Dxf2Namespace.NAMESPACE )
    public ImportCount getImportCount()
    {
        return importCount;
    }

    public void setImportCount( ImportCount importCount )
    {
        this.importCount = importCount;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "typeSummaries", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "typeSummary", namespace = Dxf2Namespace.NAMESPACE )
    public List<ImportTypeSummary> getImportTypeSummaries()
    {
        return importTypeSummaries;
    }

    public void setImportTypeSummaries( List<ImportTypeSummary> importTypeSummaries )
    {
        this.importTypeSummaries = importTypeSummaries;
    }

    //-------------------------------------------------------------------------
    // Helpers
    //-------------------------------------------------------------------------

    public void incrementImportCount( ImportCount importCount )
    {
        this.importCount.incrementImported( importCount.getImported() );
        this.importCount.incrementUpdated( importCount.getUpdated() );
        this.importCount.incrementIgnored( importCount.getIgnored() );
    }

    public void incrementImported( int n )
    {
        importCount.incrementImported( n );
    }

    public void incrementUpdated( int n )
    {
        importCount.incrementUpdated( n );
    }

    public void incrementIgnored( int n )
    {
        importCount.incrementIgnored( n );
    }
}
